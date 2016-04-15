package com.roop.utils.plugin;

public interface IPlugin {

	/**
	 * Called when Plugin registers.
	 * Execution in seperate thread.
	 * @param meta contains information about the Plugin
	 */
	public void onRegister(IPluginMeta meta, ICustomObject... objects) throws Throwable;

	/**
	 * Called when Plugin successfully registers.
	 * Executed in same thread as onRegister().
	 */
	public void onStart() throws Throwable;

	/**
	 * Called when Plugin throws an exception.
	 * @param t
	 */
	public void onException(Throwable t);

	/**
	 * Called when System is stopped.
	 * Executed in seperate thread.
	 */
	public void onStop() throws Throwable;

	/**
	 * Called when Plugin is stopped.
	 * Executed in same thread as onStop()
	 */
	public void onUnregister() throws Throwable;

	//prefilled stuff
//	IPluginMeta getMeta();
	
	//userfilled stuff
//	public boolean hasGUI();
//	public JComponent getGUI();
//	public void start() throws Throwable;
//	public void stop();

	//communication
//	public void sendToMainApp(Object data);
//	public void sendToPlugin(String receiverClassName, Object data);
//	public void onMessageReceived(String senderClassName, Object data);
}
