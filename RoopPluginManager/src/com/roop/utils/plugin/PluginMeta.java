package com.roop.utils.plugin;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

final class PluginMeta implements IPluginMeta {
	private static final String VERSION_STRING = "version=";
	private static final String CLASS_STRING = "main=";
	private static final String NAME_STRING = "name=";

	private URLClassLoader ucl;
	private Class<?> clazz = null;

	private String name = null;
	private String className = null;
	private String version = null;

//	public Class<?> getPluginClass() throws ClassNotFoundException{
//		return ucl.loadClass(className);
//	}

	public Class<?> getPluginClass() {
		return clazz;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public final boolean isValid() {
		return this.getClassName() != null && this.getName() != null && this.getVersion() != null;
	}

	private PluginMeta(){}

	public void loadPlugin() throws ClassNotFoundException{
		clazz = ucl.loadClass(className);
	}

	public void close() throws IOException {
		ucl.close();
	}

	@Override
	public String toString() {
		return "[" + className + "][" + version + "][" + name + "]";
	}

	private static void fillPluginMeta(PluginMeta meta) throws IOException{
		//fill pluginmeta
		InputStream in = meta.ucl.getResourceAsStream("plugin");

		if(in == null)
			throw new IOException("There is no Information included");

		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		while((line = br.readLine()) != null){
			if(line.startsWith(CLASS_STRING)){
				meta.className = line.replaceFirst(CLASS_STRING, "");
			}
			else if(line.startsWith(VERSION_STRING)){
				meta.version = line.replaceFirst(VERSION_STRING, "");
			}
			else if(line.startsWith(NAME_STRING)){
				meta.name = line.replaceFirst(NAME_STRING, "");
			}
		}

	}

	public static PluginMeta fromFile(File f) throws Exception{
		PluginMeta out = new PluginMeta();

		out.ucl = URLClassLoader.newInstance(new URL[]{f.toURI().toURL()});

		//URL jarURL = new URL("jar", "","file:" + f.getAbsolutePath()+"!/");
		//URL metaURL = new URL("jar", "","file:" + f.getAbsolutePath()+"!/plugin");

		PluginMeta.fillPluginMeta(out);

//			if(out.isValid()){
//				ClassPathHack.addURL(jarURL);
//			}

		return out;
	}
}
