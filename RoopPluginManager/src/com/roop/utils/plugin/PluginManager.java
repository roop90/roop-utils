package com.roop.utils.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class PluginManager implements IPluginManager {
//	private static final PluginManager instance = new PluginManager();

	private final ExecutorService exec;

	//	public final EventHelper listeners = new EventHelper();
//	private final ConcurrentHashMap<PluginMeta, IPlugin> plugins = new ConcurrentHashMap<>();
	private final List<PluginEntry> plugins = new ArrayList<>();
	//private final ConcurrentHashMap<AbstractPluginMeta, LinkedList<Thread>> threads = new ConcurrentHashMap<AbstractPluginMeta, LinkedList<Thread>>();
	//private final ExecutorService exec = Executors.newCachedThreadPool();
//	private IExceptionHandler handler = new NullExceptionHandler();
//	private IGUIHandler gHandler = new NullGUIHandler();
	private File directory = new File(System.getProperty("user.dir"));
	private boolean running = false;

	private final IPluginManagerHandler handler;

	//	public void setExceptionHandler( IExceptionHandler eh){handler = eh; }
//	public void setGUIHandler( IGUIHandler gh){gHandler = gh; }
	public void setPluginDir(String dir){ this.setPluginDir(new File(dir)); }
	public void setPluginDir(File dir){
		if(dir == null || !dir.isDirectory()) {
			handler.managerExceptionCaught(new IOException("This is not a Directory!"));
			return;
		}
		this.directory = dir;
	}

	public PluginEntry[] getPlugins(){
//		Collection<IPlugin> values = plugins.values();
//		return values.toArray(new IPlugin[values.size()]);
		return plugins.toArray(new PluginEntry[plugins.size()]);
	}

	public PluginEntry getPluginByClassName(String className){

		for (PluginEntry e : plugins) {
			if(e.getMeta().getClassName().equals(className))
				return e;
		}

//		Set<Entry<PluginMeta, IPlugin>> set = plugins.entrySet();
//		for(Entry<PluginMeta, IPlugin> e : set){
//			if(e.getKey().getClassName().equals(className))
//				return e.getValue();
//		}

		return null;
	}

	/**
	 * Creates ExecutorService which will use threads with daemon flag.
	 */
	public PluginManager(IPluginManagerHandler handler) {
		this.handler = handler;
		exec = Executors.newCachedThreadPool(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				return t;
			}
		});
	}

	public synchronized void start(){
		if(!running){
			running = true;
			this.load();
		}
	}

	public synchronized void reload(){
		if(running){
			this.unload();
			this.load();
		}
	}

	public synchronized void load(){
		if(running){
			this.readPluginDir();
		}
	}

	public synchronized void unload(){
		if(running){
			for (PluginEntry e : plugins) {
				this.unregisterPlugin(e);
			}

//			for(IPlugin p : plugins.values()){
//				this.unregisterPlugin(p);
//			}
		}
	}

	public synchronized void stop(){
		if(running){
			this.unload();
			running = false;
		}

		//		try {
		//			this.shutdown();
		//		} catch (InterruptedException e) {
		//			handler.managerExceptionCaught(e);
		//		}

	}

	/**
	 * Reads all jar files in given directory and tries to read metadata.
	 * Piles without metadata wll be ignored.
	 */
	private void readPluginDir(){

		for(File f : directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				return arg0.getName().endsWith(".jar") && arg0.isFile() && arg0.canRead();
			}}))
		{
			try{
				PluginMeta meta = PluginMeta.fromFile(f);

				this.addPlugin(meta);
			}
			catch (Throwable e){
				handler.managerExceptionCaught(e);
			}
		}
	}

	/**
	 * 1. Checks metadata for errors and forbidden configs.
	 * 2. Creates a new Thread for Plugin related code.
	 * 3. Tries to create a Plugin and a PluginEntry.
	 * 4. Registers the newly created Plugin and informs Listeners.
	 * 5. Plugin onStart() method is called.
	 * @param meta
	 */
	private void addPlugin(final PluginMeta meta){
		//check if metadata is valid
		if(meta == null || !meta.isValid())
			return;

		//load plugin (uses classloader to load plugin class defined in metadata)
		try {
			meta.loadPlugin();
		} catch (ClassNotFoundException e) {
			handler.managerExceptionCaught(e);
			return;
		}

		//ignore classes with same package as our plugin classes
		if(meta.getPluginClass().getPackage().getName().
				equalsIgnoreCase(this.getClass().getPackage().getName()))
			return;

		exec.execute(new Runnable() {
			@Override
			public void run() {
				try{
					IPlugin p = meta.getPluginClass().asSubclass(IPlugin.class).newInstance();
					PluginEntry pe = new PluginEntry(p, meta);

					try {

						if (plugins.contains(pe))
							throw new Exception("Plugin already exists");

						PluginManager.this.registerPlugin(pe);
						handler.onPluginAdded(pe);
						pe.getPlugin().onStart();
//							PluginManager.this.fireOnPluginAdded(pe);
					} catch (Throwable e) {
						handler.pluginExceptionCaught(e, pe);
					}
				} catch (Throwable e) {
					handler.managerExceptionCaught(e);
				}
			}
		});

	}

	private void registerPlugin(PluginEntry p) throws Throwable {
		p.getPlugin().onRegister(p.getMeta(), handler.setupCustomObjects());
	}

	public void unregisterPlugin(PluginEntry pe){

//		if(p.hasGUI()){
//			synchronized (gHandler) {
//				gHandler.onGUIUnregister(p);
//			}
//		}

		try {
			pe.getPlugin().onStop();
			pe.getPlugin().onUnregister();
		} catch (InterruptedException e) {
			handler.pluginExceptionCaught(e, pe);
			handler.managerExceptionCaught(e);
		} catch (Throwable t) {
			handler.pluginExceptionCaught(t, pe);
		}
		this.removePlugin(pe);
		handler.onPluginRemoved(pe);
//		this.fireOnPluginRemoved(pe);
	}

	private void removePlugin(PluginEntry pe){
		try{
			((PluginMeta)pe.getMeta()).close();
			plugins.remove(pe);
		} catch (IOException e) {
			handler.managerExceptionCaught(e);
		}
	}

	@Deprecated
	private boolean validatePM(IPluginMeta pm){
		return pm != null && pm.isValid() && pm instanceof PluginMeta;
	}

//	//plugin interaction
//	@Override
//	public synchronized void showPluginGUI(AbstractPlugin p) {
//		if(this.validatePM(p.getMeta()))
//			gHandler.onPluginRequest(p);
//	}
//
//	@Override
//	public synchronized void sendToPlugin(AbstractPluginMeta sender, String receiverClassName, Object data) {
//		if(this.validatePM(sender) && !sender.getClassName().equals(receiverClassName)){
//			AbstractPlugin p = this.getPluginByClassName(receiverClassName);
//			if(p != null && this.validatePM(p.getMeta())){
//				p.onMessageReceived(sender.getClassName(), data);
//			}
//		}
//	}
//
//	// works with listeners
//	@Override
//	public synchronized void sendToMainApp(AbstractPluginMeta sender, Object data) {
//		if(this.validatePM(sender)){
//			listeners.fireEvent(this, 0, sender.getClassName(), data);
////			for(PluginMessageListener l : holder.getListeners(PluginMessageListener.class)){
////				l.onMessageReceived(sender.getClassName(), data);
////			}
//		}
//	}
//	// works with listeners end
//
//	@Override
//	public synchronized void pluginExceptionCaught(AbstractPluginMeta sender, Throwable e) {
//		if(this.validatePM(sender)){
//			handler.pluginExceptionCaught(e, sender.getClassName());
//		}
//	}
//	//plugin interaction end

//	//listeners
//	private synchronized void fireOnPluginAdded(PluginEntry e){
//		handler.onPluginAdded(e);
////		listeners.fireEvent(this, 1, p);
////		for (PluginChangedListener l : holder.getListeners(PluginChangedListener.class)) {
////			l.onPluginAdded(p);
////		}
//	}
//
//	private synchronized void fireOnPluginUpdated(PluginEntry e){
//		handler.onPluginUpdated(e);
////		listeners.fireEvent(this, 2, p);
////		for (PluginChangedListener l : holder.getListeners(PluginChangedListener.class)) {
////			l.onPluginUpdated(p);
////		}
//	}
//
//	private synchronized void fireOnPluginRemoved(PluginEntry e){
//		handler.onPluginRemoved(e);
////		listeners.fireEvent(this, 3, p);
////		for (PluginChangedListener l : holder.getListeners(PluginChangedListener.class)) {
////			l.onPluginRemoved(p);
////		}
//	}
//	//listeners end
}
