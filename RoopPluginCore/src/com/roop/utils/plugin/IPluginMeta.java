package com.roop.utils.plugin;
 
public interface IPluginMeta {
	public abstract boolean isValid();
	public abstract String getName();
	public abstract String getVersion();
	public abstract String getClassName();
}