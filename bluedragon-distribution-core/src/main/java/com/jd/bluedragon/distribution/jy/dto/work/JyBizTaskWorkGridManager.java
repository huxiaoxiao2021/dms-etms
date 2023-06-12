package com.jd.bluedragon.distribution.jy.dto.work;

import java.util.Date;
import java.io.Serializable;

/**
 * @ClassName: JyBizTaskWorkGridManager
 * @Description: 巡检任务表-实体类
 * @author wuyoude
 * @date 2023年06月12日 17:54:00
 *
 */
public class JyBizTaskWorkGridManager implements Serializable {

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
	 * 预计完成时间,超过这个时间即为超时
	 */
	private Date preFinishTime;

	/**
	 * 处理人erp
	 */
	private String handlerErp;

	/**
	 * 处理人-上岗码编码
	 */
	private String handlerPositionCode;

	/**
	 * 异常状态:0：待分配 1：待处理 2：处理中 3：已完成  4:超时删除
	 */
	private Integer status;

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
	 * 是否删除：1-有效，0-删除
	 */
	private Integer yn;

	/**
	 * 数据库时间
	 */
	private Date ts;

	/**
	 * 异常状态:0：未超时 1：已超时
	 */
	private Integer timeOut;

	/**
	 *
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 *
	 * @return id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 *
	 * @param bizId
	 */
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	/**
	 *
	 * @return bizId
	 */
	public String getBizId() {
		return this.bizId;
	}

	/**
	 *
	 * @param taskType
	 */
	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	/**
	 *
	 * @return taskType
	 */
	public Integer getTaskType() {
		return this.taskType;
	}

	/**
	 *
	 * @param taskCode
	 */
	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	/**
	 *
	 * @return taskCode
	 */
	public String getTaskCode() {
		return this.taskCode;
	}

	/**
	 *
	 * @param taskName
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 *
	 * @return taskName
	 */
	public String getTaskName() {
		return this.taskName;
	}

	/**
	 *
	 * @param taskDescription
	 */
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	/**
	 *
	 * @return taskDescription
	 */
	public String getTaskDescription() {
		return this.taskDescription;
	}

	/**
	 *
	 * @param taskBatchCode
	 */
	public void setTaskBatchCode(String taskBatchCode) {
		this.taskBatchCode = taskBatchCode;
	}

	/**
	 *
	 * @return taskBatchCode
	 */
	public String getTaskBatchCode() {
		return this.taskBatchCode;
	}

	/**
	 *
	 * @param taskRefGridKey
	 */
	public void setTaskRefGridKey(String taskRefGridKey) {
		this.taskRefGridKey = taskRefGridKey;
	}

	/**
	 *
	 * @return taskRefGridKey
	 */
	public String getTaskRefGridKey() {
		return this.taskRefGridKey;
	}

	/**
	 *
	 * @param orderNum
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 *
	 * @return orderNum
	 */
	public Integer getOrderNum() {
		return this.orderNum;
	}

	/**
	 *
	 * @param siteCode
	 */
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 *
	 * @return siteCode
	 */
	public Integer getSiteCode() {
		return this.siteCode;
	}

	/**
	 *
	 * @param siteName
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 *
	 * @return siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 *
	 * @param areaCode
	 */
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	/**
	 *
	 * @return areaCode
	 */
	public String getAreaCode() {
		return this.areaCode;
	}

	/**
	 *
	 * @param areaName
	 */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	/**
	 *
	 * @return areaName
	 */
	public String getAreaName() {
		return this.areaName;
	}

	/**
	 *
	 * @param gridName
	 */
	public void setGridName(String gridName) {
		this.gridName = gridName;
	}

	/**
	 *
	 * @return gridName
	 */
	public String getGridName() {
		return this.gridName;
	}

	/**
	 *
	 * @param preFinishTime
	 */
	public void setPreFinishTime(Date preFinishTime) {
		this.preFinishTime = preFinishTime;
	}

	/**
	 *
	 * @return preFinishTime
	 */
	public Date getPreFinishTime() {
		return this.preFinishTime;
	}

	/**
	 *
	 * @param handlerErp
	 */
	public void setHandlerErp(String handlerErp) {
		this.handlerErp = handlerErp;
	}

	/**
	 *
	 * @return handlerErp
	 */
	public String getHandlerErp() {
		return this.handlerErp;
	}

	/**
	 *
	 * @param handlerPositionCode
	 */
	public void setHandlerPositionCode(String handlerPositionCode) {
		this.handlerPositionCode = handlerPositionCode;
	}

	/**
	 *
	 * @return handlerPositionCode
	 */
	public String getHandlerPositionCode() {
		return this.handlerPositionCode;
	}

	/**
	 *
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 *
	 * @return status
	 */
	public Integer getStatus() {
		return this.status;
	}

	/**
	 *
	 * @param processBeginTime
	 */
	public void setProcessBeginTime(Date processBeginTime) {
		this.processBeginTime = processBeginTime;
	}

	/**
	 *
	 * @return processBeginTime
	 */
	public Date getProcessBeginTime() {
		return this.processBeginTime;
	}

	/**
	 *
	 * @param processEndTime
	 */
	public void setProcessEndTime(Date processEndTime) {
		this.processEndTime = processEndTime;
	}

	/**
	 *
	 * @return processEndTime
	 */
	public Date getProcessEndTime() {
		return this.processEndTime;
	}

	/**
	 *
	 * @param createUser
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 *
	 * @return createUser
	 */
	public String getCreateUser() {
		return this.createUser;
	}

	/**
	 *
	 * @param createUserName
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 *
	 * @return createUserName
	 */
	public String getCreateUserName() {
		return this.createUserName;
	}

	/**
	 *
	 * @param updateUser
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 *
	 * @return updateUser
	 */
	public String getUpdateUser() {
		return this.updateUser;
	}

	/**
	 *
	 * @param updateUserName
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	/**
	 *
	 * @return updateUserName
	 */
	public String getUpdateUserName() {
		return this.updateUserName;
	}

	/**
	 *
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 *
	 * @return createTime
	 */
	public Date getCreateTime() {
		return this.createTime;
	}

	/**
	 *
	 * @param updateTime
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 *
	 * @return updateTime
	 */
	public Date getUpdateTime() {
		return this.updateTime;
	}

	/**
	 *
	 * @param yn
	 */
	public void setYn(Integer yn) {
		this.yn = yn;
	}

	/**
	 *
	 * @return yn
	 */
	public Integer getYn() {
		return this.yn;
	}

	/**
	 *
	 * @param ts
	 */
	public void setTs(Date ts) {
		this.ts = ts;
	}

	/**
	 *
	 * @return ts
	 */
	public Date getTs() {
		return this.ts;
	}

	/**
	 *
	 * @param timeOut
	 */
	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}

	/**
	 *
	 * @return timeOut
	 */
	public Integer getTimeOut() {
		return this.timeOut;
	}


}
