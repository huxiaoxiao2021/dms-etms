package com.jd.bluedragon.common.dto.group;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: GroupMemberQueryRequest
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
	 * 任务id
	 */
	private String taskId;
	/**
	 * 状态
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
	/**
	 * 组员类型
	 */
	private Integer memberType;
	/**
	 * 签到日期-开始
	 */
	private Date signInTimeStart;
	/**
	 * 签到日期-结束
	 */
	private Date signInTimeEnd;	

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
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

	public Integer getMemberType() {
		return memberType;
	}

	public void setMemberType(Integer memberType) {
		this.memberType = memberType;
	}

	public Date getSignInTimeStart() {
		return signInTimeStart;
	}

	public void setSignInTimeStart(Date signInTimeStart) {
		this.signInTimeStart = signInTimeStart;
	}

	public Date getSignInTimeEnd() {
		return signInTimeEnd;
	}

	public void setSignInTimeEnd(Date signInTimeEnd) {
		this.signInTimeEnd = signInTimeEnd;
	}
}
