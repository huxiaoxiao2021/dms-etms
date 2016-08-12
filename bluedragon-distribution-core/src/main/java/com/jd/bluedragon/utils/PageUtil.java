/**
 * PageUtil.java
 * com.jd.bluedragon.web
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 Feb 13, 2012 		libin
 *
 * Copyright (c) 2012, TNT All Rights Reserved.
 */

package com.jd.bluedragon.utils;

/**
 * ClassName:PageUtil Function: 分页工具类
 */
public class PageUtil {

	private int curPage = 1;// 当前页
	private int nextPage;// 下一页
	private int prePage;// 前一页
	private int totalRow;// 总条数
	private int pageSize = 10;// 每页条数
	private int totalPage;// 总页数
	private int start;// 开始条数
	private int end;//结束

	public PageUtil() {
	}

	public PageUtil(int curPage) {
		this.curPage = curPage;
	}

	public PageUtil(int curPage, int pageSize) {
		this.curPage = curPage;
		this.pageSize = pageSize;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getPrePage() {
		return prePage;
	}

	public void setPrePage(int prePage) {
		this.prePage = prePage;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	
	
	public int getEnd() {
        return end;
    }

    public void init() {
		totalPage = (totalRow + pageSize - 1) / pageSize;
		if (totalRow == 0)
			curPage = 1;
//		else if (curPage > totalPage)
//			curPage = totalPage;

		if (curPage < 1) {
			curPage = 1;
		}
		nextPage = (curPage < totalPage ? curPage + 1 : totalPage);
		prePage = (curPage - 1 > 1 ? curPage - 1 : 1);
		start = (curPage - 1) * pageSize;
		end=start+pageSize;
		if (totalRow == 0) {
			curPage = 1;
			totalPage = 1;
		}
	}

}
