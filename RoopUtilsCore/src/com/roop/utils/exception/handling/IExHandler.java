package com.roop.utils.exception.handling;

/**
 * Created with IntelliJ IDEA.
 * User: roop
 * Date: 26.11.13
 * Time: 02:37
 * Copyright: roop
 */
public interface IExHandler {
	public void exceptionCaught(Throwable e, Object source);
}