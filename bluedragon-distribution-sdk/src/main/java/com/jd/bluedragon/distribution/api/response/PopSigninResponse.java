package com.jd.bluedragon.distribution.api.response;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

public class PopSigninResponse extends JdResponse{
	
	private static final long serialVersionUID = 2161137022509802975L;
	public List data;
	public int totalNo;
	public int pageSize;
	public int pageNo;
	public int totalSize;
	public List getData() {
		return data;
	}
	public int getTotalNo() {
		return totalNo;
	}
	public void setTotalNo(int totalNo) {
		this.totalNo = totalNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public void setData(List data) {
		this.data = data;
	}
	
	

}
