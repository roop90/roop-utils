package com.roop.utils.listeners;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: roop
 * Date: 31.03.2014
 * Time: 15:38
 * Copyright: roop
 */
public interface IEventListener extends EventListener {
	public void onEvent(EventInfo m);
}
