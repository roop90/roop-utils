package com.roop.utils.plugin;

/**
 * Created with IntelliJ IDEA.
 * Project: Utilities
 * User: roop
 * Date: 26.03.2016
 * Time: 01:49
 * Copyright: Ralf Wiedemann
 */
public class PluginEntry {

	private final IPlugin p;
	private final IPluginMeta m;

	public IPlugin getPlugin() {
		return p;
	}

	public IPluginMeta getMeta() {
		return m;
	}

	public PluginEntry(IPlugin p, PluginMeta m) {
		this.p = p;
		this.m = m;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj instanceof PluginEntry) {
			PluginEntry o = (PluginEntry) obj;

			boolean b = o.p == p;
			return b || m.getClassName().equals(o.m.getClassName());
		}

		return false;
	}

	@Override
	public String toString() {
		return m.toString();
	}
}
