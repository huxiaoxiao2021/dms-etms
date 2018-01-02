package com.jd.ql.dms.common.web.mvc.api;

import java.util.List;

/**
 * 
 * @ClassName: PagerResult
 * @Description: 分页数据结果
 * @author: wuyoude
 * @date: 2017年12月19日 下午6:47:14
 * 
 * @param <E>
 */
public class PagerResult<E> {
	/**
	 * 数据总数
	 */
	private int total;
	/**
	 * 当前页的数据
	 */
	private List<E> rows;
	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	/**
	 * @return the rows
	 */
	public List<E> getRows() {
		return rows;
	}
	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<E> rows) {
		this.rows = rows;
	}
}
