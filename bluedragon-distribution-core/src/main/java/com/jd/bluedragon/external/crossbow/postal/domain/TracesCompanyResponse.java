package com.jd.bluedragon.external.crossbow.postal.domain;

import java.util.List;

/**
 * <p>
 *     运单轨迹信息传输接口返回对象
 *
 * @author wuyoude
 **/
public class TracesCompanyResponse extends EmsBaseResponse{
	private static final long serialVersionUID = 1L;
	/**
	 * 返回的执行结果
	 */
	private List<TracesCompanyResponseItem> responseItems;

	public List<TracesCompanyResponseItem> getResponseItems() {
		return responseItems;
	}

	public void setResponseItems(List<TracesCompanyResponseItem> responseItems) {
		this.responseItems = responseItems;
	}
	
}
