package com.roop.utils.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: roop
 * Date: 31.03.2014
 * Time: 15:31
 * Copyright: roop
 */
public class EventInfo {

	private final Object source;
	public final int type;
	private final List<Object> data = new ArrayList<Object>();

	public void add(Object e){
		data.add(e);
	}

	public <T> T get(int i){
		return (T)data.get(i);
	}
	public <T> T get(Class<T> c, int i){
		return c.cast(data.get(i));
	}
	public <T> T get(T e, int i){
		return (T)(data.get(i));
	}

	public <T> T source(){
		return (T)source;
	}
	public <T> T source(Class<T> c){
		return c.cast(source);
	}

	public EventInfo(Object source, int type){
		this(source, type, new Object[0]);
	}

	public EventInfo(Object source, int type, Object... data){
		this.source = source;
		this.type = type;
		Collections.addAll(this.data, data);
	}
}
