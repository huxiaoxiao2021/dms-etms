package com.jd.bluedragon.distribution.print.request;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;

/**
 * 
 * @ClassName: WaybillPrintRequest
 * @Description: 包裹标签打印请求实体
 * @author: wuyoude
 * @date: 2018年1月23日 下午10:36:18
 *
 */
public class PackagePrintRequest extends WaybillPrintRequest{
	private static final long serialVersionUID = 1L;
	/**
	 * 包裹号打印开始序号
	 */
	private Integer packageStartIndex = 0;
	/**
	 * 包裹号打印结束序号
	 */
	private Integer packageEndIndex = 0;
	/**
	 * @return the packageStartIndex
	 */
	public Integer getPackageStartIndex() {
		return packageStartIndex;
	}
	/**
	 * @param packageStartIndex the packageStartIndex to set
	 */
	public void setPackageStartIndex(Integer packageStartIndex) {
		this.packageStartIndex = packageStartIndex;
	}
	/**
	 * @return the packageEndIndex
	 */
	public Integer getPackageEndIndex() {
		return packageEndIndex;
	}
	/**
	 * @param packageEndIndex the packageEndIndex to set
	 */
	public void setPackageEndIndex(Integer packageEndIndex) {
		this.packageEndIndex = packageEndIndex;
	}
	
}
