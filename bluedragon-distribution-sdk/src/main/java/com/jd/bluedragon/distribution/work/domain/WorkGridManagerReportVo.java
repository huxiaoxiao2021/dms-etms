package com.jd.bluedragon.distribution.work.domain;

import java.io.Serializable;
import java.util.Date;

public class WorkGridManagerReportVo  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 业务主键
	 */
	private String bizId;

	/**
	 * 任务类型：1-例会（标题+内容） 2-例会记录（仅拍照） 3-工作区任务（多页签）
	 */
	private Integer taskType;

	/**
	 * 需要扫描岗位码标志,0-不扫描,1-扫描
	 */
	private Integer needScanGrid;

	/**
	 * 任务编码
	 */
	private String taskCode;

	/**
	 * 任务名称
	 */
	private String taskName;

	/**
	 * 任务说明
	 */
	private String taskDescription;

	/**
	 * 任务批次号
	 */
	private String taskBatchCode;

	/**
	 * 任务网格key：work_station_grid业务主键
	 */
	private String taskRefGridKey;

	/**
	 * 排序值：决定执行顺序
	 */
	private Integer orderNum;

	/**
	 * 省区编码
	 */
	private String provinceAgencyCode;

	/**
	 * 省区名称
	 */
	private String provinceAgencyName;

	/**
	 * 枢纽编码
	 */
	private String areaHubCode;

	/**
	 * 枢纽名称
	 */
	private String areaHubName;

	/**
	 * 站点编号
	 */
	private Integer siteCode;

	/**
	 * 站点名称
	 */
	private String siteName;

	/**
	 * 作业区编码
	 */
	private String areaCode;

	/**
	 * 作业区名称
	 */
	private String areaName;

	/**
	 * 网格名称
	 */
	private String gridName;

	/**
	 * 任务日期:任务下发日期
	 */
	private Date taskDate;

	/**
	 * 预计完成时间,超过这个时间即为超时
	 */
	private Date preFinishTime;

	/**
	 * 处理人erp
	 */
	private String handlerErp;

	/**
	 * 处理人名称
	 */
	private String handlerUserName;

	/**
	 * 处理人-岗位编码
	 */
	private String handlerUserPositionCode;

	/**
	 * 处理人-岗位名称
	 */
	private String handlerUserPositionName;

	/**
	 * 处理人-上岗码编码
	 */
	private String handlerPositionCode;

	/**
	 * 异常状态:0：待分配 1：待处理 2：处理中 3：已完成  4:超时删除
	 */
	private Integer status;
	/**
	 * 状态:0：待分配 1：待处理 2：处理中 3：已完成  4:超时删除
	 */
	private String statusName;	

	/**
	 * 开始处理时间
	 */
	private Date processBeginTime;

	/**
	 * 处理完成时间
	 */
	private Date processEndTime;

	/**
	 * 创建人ERP
	 */
	private String createUser;

	/**
	 * 创建人姓名
	 */
	private String createUserName;

	/**
	 * 修改人ERP
	 */
	private String updateUser;

	/**
	 * 更新人姓名
	 */
	private String updateUserName;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 数据库时间
	 */
	private Date ts;

	/**
	 * @See com.jd.bluedragon.distribution.jy.work.enums.WorkCheckResultEnum
	 * 是否符合,0-未选择,1-符合 2-不符合
	 */
	private Integer isMatch;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public Integer getTaskType() {
		return taskType;
	}
	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}
	public Integer getNeedScanGrid() {
		return needScanGrid;
	}
	public void setNeedScanGrid(Integer needScanGrid) {
		this.needScanGrid = needScanGrid;
	}
	public String getTaskCode() {
		return taskCode;
	}
	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskDescription() {
		return taskDescription;
	}
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
	public String getTaskBatchCode() {
		return taskBatchCode;
	}
	public void setTaskBatchCode(String taskBatchCode) {
		this.taskBatchCode = taskBatchCode;
	}
	public String getTaskRefGridKey() {
		return taskRefGridKey;
	}
	public void setTaskRefGridKey(String taskRefGridKey) {
		this.taskRefGridKey = taskRefGridKey;
	}
	public Integer getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	public String getProvinceAgencyCode() {
		return provinceAgencyCode;
	}
	public void setProvinceAgencyCode(String provinceAgencyCode) {
		this.provinceAgencyCode = provinceAgencyCode;
	}
	public String getProvinceAgencyName() {
		return provinceAgencyName;
	}
	public void setProvinceAgencyName(String provinceAgencyName) {
		this.provinceAgencyName = provinceAgencyName;
	}
	public String getAreaHubCode() {
		return areaHubCode;
	}
	public void setAreaHubCode(String areaHubCode) {
		this.areaHubCode = areaHubCode;
	}
	public String getAreaHubName() {
		return areaHubName;
	}
	public void setAreaHubName(String areaHubName) {
		this.areaHubName = areaHubName;
	}
	public Integer getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
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
	public Date getPreFinishTime() {
		return preFinishTime;
	}
	public void setPreFinishTime(Date preFinishTime) {
		this.preFinishTime = preFinishTime;
	}
	public String getHandlerErp() {
		return handlerErp;
	}
	public void setHandlerErp(String handlerErp) {
		this.handlerErp = handlerErp;
	}
	public String getHandlerUserName() {
		return handlerUserName;
	}
	public void setHandlerUserName(String handlerUserName) {
		this.handlerUserName = handlerUserName;
	}
	public String getHandlerUserPositionCode() {
		return handlerUserPositionCode;
	}
	public void setHandlerUserPositionCode(String handlerUserPositionCode) {
		this.handlerUserPositionCode = handlerUserPositionCode;
	}
	public String getHandlerUserPositionName() {
		return handlerUserPositionName;
	}
	public void setHandlerUserPositionName(String handlerUserPositionName) {
		this.handlerUserPositionName = handlerUserPositionName;
	}
	public String getHandlerPositionCode() {
		return handlerPositionCode;
	}
	public void setHandlerPositionCode(String handlerPositionCode) {
		this.handlerPositionCode = handlerPositionCode;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public Date getProcessBeginTime() {
		return processBeginTime;
	}
	public void setProcessBeginTime(Date processBeginTime) {
		this.processBeginTime = processBeginTime;
	}
	public Date getProcessEndTime() {
		return processEndTime;
	}
	public void setProcessEndTime(Date processEndTime) {
		this.processEndTime = processEndTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public String getUpdateUserName() {
		return updateUserName;
	}
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Date getTs() {
		return ts;
	}
	public void setTs(Date ts) {
		this.ts = ts;
	}

	public Integer getIsMatch() {
		return isMatch;
	}

	public void setIsMatch(Integer isMatch) {
		this.isMatch = isMatch;
	}
}
