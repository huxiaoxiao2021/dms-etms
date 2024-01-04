package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: JyWorkGridManagerQueryRequest
 * @Description: 任务管理-查询请求
 * @author wuyoude
 * @date 2023年05月30日 11:01:53
 *
 */
public class JyWorkGridManagerQueryRequest implements Serializable {

	private static final long serialVersionUID = 1L;

    /**
     * 操作人erp
     */
    private String operateUserCode;
    /**
     * 岗位码
     */
    private String positionCode;
    /**
     * 岗位码-对应的网格key
     */
    private String taskRefGridKey;
	/**
	 * 异常状态:0：待分配 1：未完成 2：处理中 3：已完成  4:超时未完成 7:转派
	 */
	private Integer status;
	/**
	 * 开始处理时间
	 */
	private Date processBeginTime;
	/**
	 * 分页参数-开始值
	 */
	private int offset = 0;
	/**
	 * 分页参数-数据条数
	 */
	private int limit = 10;

	private Integer pageNumber = 1;

	private Integer pageSize = 10;
	/**
	 * 转派任务id
	 */
	private String bizId;


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

	public String getOperateUserCode() {
		return operateUserCode;
	}

	public void setOperateUserCode(String operateUserCode) {
		this.operateUserCode = operateUserCode;
	}

	public String getPositionCode() {
		return positionCode;
	}

	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}

	public String getTaskRefGridKey() {
		return taskRefGridKey;
	}

	public void setTaskRefGridKey(String taskRefGridKey) {
		this.taskRefGridKey = taskRefGridKey;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getProcessBeginTime() {
		return processBeginTime;
	}

	public void setProcessBeginTime(Date processBeginTime) {
		this.processBeginTime = processBeginTime;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
}
