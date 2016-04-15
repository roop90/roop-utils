package com.roop.utils.plugin;

/**
 * Created with IntelliJ IDEA.
 * Project: Utilities
 * User: roop
 * Date: 26.03.2016
 * Time: 19:52
 * Copyright: Ralf Wiedemann
 */

/**
 * Those Methods need to be implemented by the Main-Application Developer.
 */
public interface IPluginManagerHandler {
	public void managerExceptionCaught(Throwable e);
	public void pluginExceptionCaught(Throwable e, PluginEntry pe);
	public void onPluginAdded(PluginEntry e);
	public void onPluginUpdated(PluginEntry e);
	public void onPluginRemoved(PluginEntry e);
	public ICustomObject[] setupCustomObjects();
}
