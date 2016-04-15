package com.roop.utils.plugin;

/**
 * Created with IntelliJ IDEA.
 * Project: Utilities
 * User: roop
 * Date: 26.03.2016
 * Time: 21:38
 * Copyright: Ralf Wiedemann
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
