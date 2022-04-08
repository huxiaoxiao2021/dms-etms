package com.jd.bluedragon.common.dto.group;

import java.io.Serializable;

/**
 * @ClassName: UserSignQueryRequest
 * @Description: 小组成员查询请求
 * @author wuyoude
 * @date 2022年03月30日 11:01:53
 *
 */
public class GroupMemberQueryRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 小组编码
	 */
	private String groupCode;
	/**
	 * 小组编码
	 */
	private Integer status;
	/**
	 * 分页参数-开始值
	 */
	private int offset = 0;
	/**
	 * 分页参数-数据条数
	 */
	private int limit = 10;
	
	private Integer pageNumber;
	
	private Integer pageSize;

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
