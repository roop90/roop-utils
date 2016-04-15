package com.roop.utils.plugin;

/**
 * Created with IntelliJ IDEA.
 * Project: Utilities
 * User: roop
 * Date: 26.03.2016
 * Time: 21:31
 * Copyright: Ralf Wiedemann
 */
public interface ICustomObject {
	public String getClassName();
	public <T> T getObject();
}
