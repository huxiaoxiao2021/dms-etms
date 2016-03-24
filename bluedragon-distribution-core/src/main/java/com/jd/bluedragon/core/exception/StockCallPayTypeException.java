package com.jd.bluedragon.core.exception;


public class StockCallPayTypeException extends Exception {

	private static final long serialVersionUID = 4101421562525996642L;

	public StockCallPayTypeException(String msg) {
		super(msg);
	}
	
	public StockCallPayTypeException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
