package com.roop.utils.listeners;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: roop
 * Date: 31.03.2014
 * Time: 15:41
 * Copyright: roop
 */
public class EventHelper {

	private final List<IEventListener> listeners = new ArrayList<IEventListener>();

	public void fireEvent(Object source, int type, Object... data){
		this.fireEvent(new EventInfo(source, type, data));
	}
	public void fireEvent(EventInfo m){
		synchronized (listeners) {
			for (IEventListener l : listeners) {
				l.onEvent(m);
			}
		}
	}

	public boolean add(IEventListener e){
		synchronized (listeners) {
			return listeners.add(e);
		}
	}
	public boolean remove(IEventListener e){
		synchronized (listeners) {
			return listeners.remove(e);
		}
	}
}
