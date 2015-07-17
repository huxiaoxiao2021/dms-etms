package com.jd.bluedragon.core.exception;


public class OrderCallTimeoutException extends Exception {
	
	private static final long serialVersionUID = -3911798096688804915L;
	
	public OrderCallTimeoutException(String msg) {
		super(msg);
	}
	
	public OrderCallTimeoutException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
