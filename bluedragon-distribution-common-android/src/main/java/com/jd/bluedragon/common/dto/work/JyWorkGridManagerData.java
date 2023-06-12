package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * @ClassName: JyWorkGridManagerPageData
 * @Description: 任务管理-实体
 * @author wuyoude
 * @date 2023年05月30日 11:01:53
 *
 */
public class JyWorkGridManagerData implements Serializable {

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
	 * 站点编号
	 */
	private Integer siteCode;

	/**
	 * 站点名称
	 */
	private String siteName;
	
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
	 * @See com.jd.bluedragon.distribution.jy.work.enums.WorkTaskTypeEnum
	 * 任务类型：1-例会（标题+内容） 2-例会记录（仅拍照） 3-工作区任务（多页签） 
	 */
	private Integer taskType;

	/**
	 * 需要扫描岗位码标志,0-不扫描,1-扫描
	 */
	private Integer needScanGrid;
	/**
	 * 任务-网格key
	 */
	private String taskRefGridKey;

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
	 * @See com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum
	 * 异常状态:0：待分配 1：未完成 2：处理中 3：已完成  4:超时未完成
	 */
	private Integer status;	
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 开始处理时间
	 */
	private Date processBeginTime;

	/**
	 * 处理完成时间
	 */
	private Date processEndTime;
	
	private List<JyWorkGridManagerCaseData> caseList;

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

	public String getTaskRefGridKey() {
		return taskRefGridKey;
	}

	public void setTaskRefGridKey(String taskRefGridKey) {
		this.taskRefGridKey = taskRefGridKey;
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

	public List<JyWorkGridManagerCaseData> getCaseList() {
		return caseList;
	}

	public void setCaseList(List<JyWorkGridManagerCaseData> caseList) {
		this.caseList = caseList;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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
	
}
