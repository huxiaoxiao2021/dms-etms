package com.jd.bluedragon.distribution.jy.dto.work;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: JyBizTaskWorkGridManager
 * @Description: 巡检任务表-实体类
 * @author wuyoude
 * @date 2023年06月14日 17:33:11
 *
 */
public class JyBizTaskWorkGridManagerQuery implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 业务主键
	 */
	private String bizId;

	/**
	 * 任务配置编码
	 */
	private String taskConfigCode;

	/**
	 * 任务批次号
	 */
	private String taskBatchCode;

	/**
	 * 省区编码
	 */
	private String provinceAgencyCode;

	/**
	 * 枢纽编码
	 */
	private String areaHubCode;

	/**
	 * 站点编号
	 */
	private Integer siteCode;

	/**
	 * 作业区编码
	 */
	private String areaCode;
	
	/**
	 * 作业区编码
	 */
	private List<String> areaCodeList;	

	/**
	 * 网格名称
	 */
	private String gridName;

	/**
	 * 任务日期:任务下发日期
	 */
	private Date taskDate;

	/**
	 * 处理人erp
	 */
	private String handlerErp;

	/**
	 * 处理人-岗位编码
	 */
	private String handlerUserPositionCode;

	/**
	 * 异常状态:0：待分配 1：待处理 2：处理中 3：已完成  4:超时删除
	 */
	private Integer status;
	
	/**
	 * 数据条数
	 */
	private int limit = 10;

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public String getTaskConfigCode() {
		return taskConfigCode;
	}

	public void setTaskConfigCode(String taskConfigCode) {
		this.taskConfigCode = taskConfigCode;
	}

	public String getTaskBatchCode() {
		return taskBatchCode;
	}

	public void setTaskBatchCode(String taskBatchCode) {
		this.taskBatchCode = taskBatchCode;
	}

	public String getProvinceAgencyCode() {
		return provinceAgencyCode;
	}

	public void setProvinceAgencyCode(String provinceAgencyCode) {
		this.provinceAgencyCode = provinceAgencyCode;
	}

	public String getAreaHubCode() {
		return areaHubCode;
	}

	public void setAreaHubCode(String areaHubCode) {
		this.areaHubCode = areaHubCode;
	}

	public Integer getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public List<String> getAreaCodeList() {
		return areaCodeList;
	}

	public void setAreaCodeList(List<String> areaCodeList) {
		this.areaCodeList = areaCodeList;
	}

	public String getGridName() {
		return gridName;
	}

	public void setGridName(String gridName) {
		this.gridName = gridName;
	}

	public Date getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}

	public String getHandlerErp() {
		return handlerErp;
	}

	public void setHandlerErp(String handlerErp) {
		this.handlerErp = handlerErp;
	}

	public String getHandlerUserPositionCode() {
		return handlerUserPositionCode;
	}

	public void setHandlerUserPositionCode(String handlerUserPositionCode) {
		this.handlerUserPositionCode = handlerUserPositionCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
