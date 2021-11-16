package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;
import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2021/10/24 10:24
 */
public class SpotCheckPagerResult<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
	private int total;
	private List<E> rows;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<E> getRows() {
		return rows;
	}

	public void setRows(List<E> rows) {
		this.rows = rows;
	}
}
