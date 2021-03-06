package com.roop.utils.plugin;

/**
 * Created with IntelliJ IDEA.
 * Project: Utilities
 * User: roop
 * Date: 26.03.2016
 * Time: 21:38
 * Copyright: Ralf Wiedemann
 */

/**
 * CustomObjects make it possible to present any Object and its class to a plugin.
 * The Plugin Developer decides whether to use it or not.
 */
public class CustomObject implements ICustomObject {
	private final String classname;
	private final Object object;

	@Override
	public String getClassName() {
		return classname;
	}

	@Override
	public <T> T getObject() {
		return (T)object;
	}

	public CustomObject(Object o) {
		this.classname = o.getClass().getName();
		this.object = o;
	}
}
