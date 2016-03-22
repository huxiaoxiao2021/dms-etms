package com.jd.bluedragon.core.dbs.util;

import java.io.Serializable;
import java.util.List;

public class DbsList<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T firstObject;
	private List<T> allObject;

	public T getFirstObject() {
		if (allObject != null && allObject.size() > 0)
			firstObject = allObject.get(0);
		return firstObject;
	}

	public List<T> getAllObject() {
		return allObject;
	}

	public void setAllObject(List<T> allObject) {
		this.allObject = allObject;
	}

}
