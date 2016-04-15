package com.roop.utils.concurrency;

/**
 * Created with IntelliJ IDEA.
 * User: roop
 * Date: 07.09.2014
 * Time: 18:48
 * Copyright: roop
 */
public interface ICommand {
	public void execute() throws Throwable;
}
