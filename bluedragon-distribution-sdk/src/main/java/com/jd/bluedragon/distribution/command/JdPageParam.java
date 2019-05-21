package com.jd.bluedragon.distribution.command;

import java.io.Serializable;

public class JdPageParam implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 当前页码，默认为1
	 */
	public static final int DEFAULT_CURRENT_PAGE = 1;
	/**
	 * 默认页的大小
	 */
	public static final int DEFAULT_PAGE_SIZE = 10;
	
	public JdPageParam() {
		super();
	}
	public JdPageParam(int pageSize) {
		super();
		this.setPageSize(pageSize);
	}
	public JdPageParam(int currentPage, int pageSize) {
		super();
		this.setCurrentPage(currentPage);
		this.setPageSize(pageSize);
	}
	/**
	 * 当前页码，默认为1
	 */
	private int currentPage = 1;
	/**
	 * 每页数据条数，默认为10条
	 */
	private int pageSize = DEFAULT_PAGE_SIZE;
	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}
	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		if(currentPage > 0){
			this.currentPage = currentPage;
		}
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		if(pageSize > 0){
			this.pageSize = pageSize;
		}
	}
}
