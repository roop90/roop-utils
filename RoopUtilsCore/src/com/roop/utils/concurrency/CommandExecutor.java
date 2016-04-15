package com.roop.utils.concurrency;

import com.roop.utils.Constants;
import com.roop.utils.exception.handling.IExHandler;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: roop
 * Date: 07.09.2014
 * Time: 18:49
 * Copyright: roop
 */
public class CommandExecutor {

	private final AtomicBoolean running = new AtomicBoolean(true);
	private final AtomicBoolean pause = new AtomicBoolean(true);
	private final PriorityBlockingQueue<CommandWrapper> queue = new PriorityBlockingQueue<CommandWrapper>();
	private final Queue<CommandWrapper> reuseable = new ConcurrentLinkedQueue<CommandWrapper>();
	private final ExecutorService exec;

	private final IExHandler exh;

	public int size() {
		return queue.size();
	}

	public CommandExecutor(){
		this(1);
	}
	public CommandExecutor(int aThreads){
		this(aThreads, Constants.EXH);
	}
	public CommandExecutor(int aThreads, boolean start){
		this(aThreads, Constants.EXH, start);
	}
	public CommandExecutor(int aThreads, IExHandler exh){
		this(aThreads, exh, true);
	}
	public CommandExecutor(int aThreads, IExHandler exh, boolean start){
		if(aThreads < 1 || exh == null){
			throw new IllegalArgumentException("You need at least one Thread.");
		}

		this.exh = exh;
		exec = Executors.newFixedThreadPool(aThreads, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				return t;
			}
		});

		for(int i = 0; i<aThreads; i++){
			exec.execute(new Worker());
		}

		if(start)
			this.resume();
	}

	public void pause(){
		this.pause.set(true);
	}

	public void resume(){
		this.pause.set(false);
	}

	public void add(ICommand c){
		this.add(c, 0);
	}
	public void add(ICommand c, int prio){
		CommandWrapper wrapper;

		if(reuseable.isEmpty()){
			wrapper = new CommandWrapper();
		} else {
			wrapper = reuseable.poll();
		}

		wrapper.set(c, prio);
		queue.put(wrapper);
	}

	public void shutdown(){
		this.running.set(false);
		exec.shutdownNow();
	}
	public void shutdown(int seconds){
		this.running.set(false);
		exec.shutdown();
		try {
			exec.awaitTermination(seconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}
		exec.shutdownNow();
	}

	private final class Worker implements Runnable{
		@Override
		public void run() {
			while (running.get()){

				CommandWrapper cw = null;
				try {
					cw = queue.take();
					if(pause.get()){
						queue.put(cw);
						cw = null;
					} else {
						cw.command.execute();
					}
				} catch (InterruptedException e) {
				} catch (Throwable t){
					exh.exceptionCaught(t, CommandExecutor.this);
				} finally {
					if(cw != null){
						reuseable.add(cw);
					}
				}

				while(pause.get() && running.get()){
					Thread.yield();
				}
			}
		}
	}

	private final static class CommandWrapper implements Comparable<CommandWrapper> {
		public volatile ICommand command;
		public volatile int prio;

		public void set(ICommand c, int prio){
			this.command = c;
			this.prio = prio;
		}

		@Override
		public int compareTo(CommandWrapper o) {
			return o.prio-this.prio;
		}
	}
}
