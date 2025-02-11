package com.jd.bluedragon.distribution.jy.task;


import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 发车任务明细表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JyBizTaskSendVehicleDetailEntity implements Serializable {

	private static final long serialVersionUID = 4089383783438643445L;

    public static final String NO_TASK_BIZ_PREFIX = "NTSD%s";

    public JyBizTaskSendVehicleDetailEntity() {}

    public JyBizTaskSendVehicleDetailEntity(Long startSiteId, Long endSiteId) {
        this.startSiteId = startSiteId;
        this.endSiteId = endSiteId;
    }

    public JyBizTaskSendVehicleDetailEntity(Long startSiteId, Long endSiteId, String sendVehicleBizId) {
        this.startSiteId = startSiteId;
        this.endSiteId = endSiteId;
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public JyBizTaskSendVehicleDetailEntity(Long startSiteId, String sendVehicleBizId) {
        this.startSiteId = startSiteId;
        this.sendVehicleBizId = sendVehicleBizId;
    }

	/**
	 * 业务主键 == 派车明细单号
	 */
	private String bizId;

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * send_vehicle业务主键
	 */
	private String sendVehicleBizId;
	/**
	 * 派车明细单号
	 */
	private String transWorkItemCode;
    /**
     * 任务状态；0-待发货，1-发货中，2-待封车，3-已封车，4-已作废
     * <see>{@link com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum}</see>
     */
	private Integer vehicleStatus;

	/**
	 * 前一个状态
	 */
	private Integer preVehicleStatus;
	/**
	 * 始发场地ID
	 */
	private Long startSiteId;
	/**
	 * 始发场地名称
	 */
	private String startSiteName;
	/**
	 * 目的场地ID
	 */
	private Long endSiteId;
	/**
	 * 目的场地名称
	 */
	private String endSiteName;
	/**
	 * 预计发车时间
	 */
	private Date planDepartTime;
	/**
	 * 到来时间 （对应当前始发场地）
	 */
	private Date comeTime;
	/**
	 * 即将到来时间（对应当前始发场地相距一定范围内）
	 */
	private Date nearComeTime;
	/**
	 * 封车时间
	 */
	private Date sealCarTime;
	/**
	 * 创建人ERP
	 */
	private String createUserErp;
	/**
	 * 创建人姓名
	 */
	private String createUserName;
	/**
	 * 更新人ERP
	 */
	private String updateUserErp;
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
	 * 最晚计划发车时间 范围查找-开始时间
	 */
	private transient Date lastPlanDepartTimeBegin;

	/**
	 * 最晚计划发车时间 范围查找-结束时间
	 */
	private transient Date lastPlanDepartTimeEnd;

	/**
	 * 待发货状态-最晚计划发车时间 范围查找-开始时间
	 */
	private transient Date toSendLastPlanDepartTimeBegin;

	/**
	 * 待发货状态-最晚计划发车时间 范围查找-结束时间
	 */
	private transient Date toSendLastPlanDepartTimeEnd;

	/**
	 * 发货中状态-最晚计划发车时间 范围查找-开始时间
	 */
	private transient Date sendingLastPlanDepartTimeBegin;

	/**
	 * 发货中状态-最晚计划发车时间 范围查找-结束时间
	 */
	private transient Date sendingLastPlanDepartTimeEnd;

	/**
	 * 待封车状态-最晚计划发车时间 范围查找-开始时间
	 */
	private transient Date toSealLastPlanDepartTimeBegin;

	/**
	 * 待封车状态-最晚计划发车时间 范围查找-结束时间
	 */
	private transient Date toSealLastPlanDepartTimeEnd;

	private Integer excepLabel;
	private transient Date createTimeBegin;
	private transient List<String> transWorkCodeList;
	private transient List<String> sendVehicleBizIdList;
	/**
	 * 迁入迁出标识
	 * 1 source（出） 2 target （入）
	 */
	private transient Integer transferFlag;

	public Integer getTransferFlag() {
		return transferFlag;
	}

	public void setTransferFlag(Integer transferFlag) {
		this.transferFlag = transferFlag;
	}

	private Integer lineType;
	private String lineTypeName;
	private String taskSimpleCode;

    /**
     * 是否只卸不装，需要操作无任务解封签
     */
	private Integer onlyLoadNoUnload;


	/**
	 * 线路类型集合
	 */
	private List<Integer> lineTypeList;

	private List<String> bizIdList;

	private List<Integer> statusList;

	/**
	 * 任务分类
	 * JySendTaskTypeEnum
	 */
	private Integer taskType;

	public String getTaskSimpleCode() {
		return taskSimpleCode;
	}

	public void setTaskSimpleCode(String taskSimpleCode) {
		this.taskSimpleCode = taskSimpleCode;
	}

	public Integer getLineType() {
		return lineType;
	}

	public void setLineType(Integer lineType) {
		this.lineType = lineType;
	}

	public String getLineTypeName() {
		return lineTypeName;
	}

	public void setLineTypeName(String lineTypeName) {
		this.lineTypeName = lineTypeName;
	}

	public List<String> getTransWorkCodeList() {
		return transWorkCodeList;
	}

	public void setTransWorkCodeList(List<String> transWorkCodeList) {
		this.transWorkCodeList = transWorkCodeList;
	}

	public List<String> getSendVehicleBizIdList() {
		return sendVehicleBizIdList;
	}

	public void setSendVehicleBizIdList(List<String> sendVehicleBizIdList) {
		this.sendVehicleBizIdList = sendVehicleBizIdList;
	}

	public Integer getPreVehicleStatus() {
		return preVehicleStatus;
	}

	public void setPreVehicleStatus(Integer preVehicleStatus) {
		this.preVehicleStatus = preVehicleStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSendVehicleBizId() {
		return sendVehicleBizId;
	}

	public void setSendVehicleBizId(String sendVehicleBizId) {
		this.sendVehicleBizId = sendVehicleBizId;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public String getTransWorkItemCode() {
		return transWorkItemCode;
	}

	public void setTransWorkItemCode(String transWorkItemCode) {
		this.transWorkItemCode = transWorkItemCode;
	}

	public Integer getVehicleStatus() {
		return vehicleStatus;
	}

	public void setVehicleStatus(Integer vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}

	public Long getStartSiteId() {
		return startSiteId;
	}

	public void setStartSiteId(Long startSiteId) {
		this.startSiteId = startSiteId;
	}

	public String getStartSiteName() {
		return startSiteName;
	}

	public void setStartSiteName(String startSiteName) {
		this.startSiteName = startSiteName;
	}

	public Long getEndSiteId() {
		return endSiteId;
	}

	public void setEndSiteId(Long endSiteId) {
		this.endSiteId = endSiteId;
	}

	public String getEndSiteName() {
		return endSiteName;
	}

	public void setEndSiteName(String endSiteName) {
		this.endSiteName = endSiteName;
	}

	public Date getPlanDepartTime() {
		return planDepartTime;
	}

	public void setPlanDepartTime(Date planDepartTime) {
		this.planDepartTime = planDepartTime;
	}

	public Date getComeTime() {
		return comeTime;
	}

	public void setComeTime(Date comeTime) {
		this.comeTime = comeTime;
	}

	public Date getNearComeTime() {
		return nearComeTime;
	}

	public void setNearComeTime(Date nearComeTime) {
		this.nearComeTime = nearComeTime;
	}

	public Date getSealCarTime() {
		return sealCarTime;
	}

	public void setSealCarTime(Date sealCarTime) {
		this.sealCarTime = sealCarTime;
	}

	public String getCreateUserErp() {
		return createUserErp;
	}

	public void setCreateUserErp(String createUserErp) {
		this.createUserErp = createUserErp;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getUpdateUserErp() {
		return updateUserErp;
	}

	public void setUpdateUserErp(String updateUserErp) {
		this.updateUserErp = updateUserErp;
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

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Date getTs() {
		return ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}

	public Date getLastPlanDepartTimeBegin() {
		return lastPlanDepartTimeBegin;
	}

	public void setLastPlanDepartTimeBegin(Date lastPlanDepartTimeBegin) {
		this.lastPlanDepartTimeBegin = lastPlanDepartTimeBegin;
	}

	public Date getLastPlanDepartTimeEnd() {
		return lastPlanDepartTimeEnd;
	}

	public void setLastPlanDepartTimeEnd(Date lastPlanDepartTimeEnd) {
		this.lastPlanDepartTimeEnd = lastPlanDepartTimeEnd;
	}

	public Integer getExcepLabel() {
		return excepLabel;
	}

	public void setExcepLabel(Integer excepLabel) {
		this.excepLabel = excepLabel;
	}

	public Date getCreateTimeBegin() {
		return createTimeBegin;
	}

	public void setCreateTimeBegin(Date createTimeBegin) {
		this.createTimeBegin = createTimeBegin;
	}

	public static class DetailComparatorByTime implements Comparator<JyBizTaskSendVehicleDetailEntity> {
		@Override
		public int compare(JyBizTaskSendVehicleDetailEntity o1, JyBizTaskSendVehicleDetailEntity o2) {
			if (o1.getPlanDepartTime()!=null && !"".equals(o1.getPlanDepartTime())
					&& o2.getPlanDepartTime()!=null && !"".equals(o2.getPlanDepartTime())){
				return o2.getPlanDepartTime().compareTo(o1.getPlanDepartTime());
			}
			return o2.getId().compareTo(o1.getId());
		}
	}

    public Integer getOnlyLoadNoUnload() {
        return onlyLoadNoUnload;
    }

    public void setOnlyLoadNoUnload(Integer onlyLoadNoUnload) {
        this.onlyLoadNoUnload = onlyLoadNoUnload;
    }

    public List<Integer> getLineTypeList() {
		return lineTypeList;
	}

	public void setLineTypeList(List<Integer> lineTypeList) {
		this.lineTypeList = lineTypeList;
	}

	public List<String> getBizIdList() {
		return bizIdList;
	}

	public void setBizIdList(List<String> bizIdList) {
		this.bizIdList = bizIdList;
	}

	public List<Integer> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Integer> statusList) {
		this.statusList = statusList;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Date getToSendLastPlanDepartTimeBegin() {
		return toSendLastPlanDepartTimeBegin;
	}

	public void setToSendLastPlanDepartTimeBegin(Date toSendLastPlanDepartTimeBegin) {
		this.toSendLastPlanDepartTimeBegin = toSendLastPlanDepartTimeBegin;
	}

	public Date getToSendLastPlanDepartTimeEnd() {
		return toSendLastPlanDepartTimeEnd;
	}

	public void setToSendLastPlanDepartTimeEnd(Date toSendLastPlanDepartTimeEnd) {
		this.toSendLastPlanDepartTimeEnd = toSendLastPlanDepartTimeEnd;
	}

	public Date getSendingLastPlanDepartTimeBegin() {
		return sendingLastPlanDepartTimeBegin;
	}

	public void setSendingLastPlanDepartTimeBegin(Date sendingLastPlanDepartTimeBegin) {
		this.sendingLastPlanDepartTimeBegin = sendingLastPlanDepartTimeBegin;
	}

	public Date getSendingLastPlanDepartTimeEnd() {
		return sendingLastPlanDepartTimeEnd;
	}

	public void setSendingLastPlanDepartTimeEnd(Date sendingLastPlanDepartTimeEnd) {
		this.sendingLastPlanDepartTimeEnd = sendingLastPlanDepartTimeEnd;
	}

	public Date getToSealLastPlanDepartTimeBegin() {
		return toSealLastPlanDepartTimeBegin;
	}

	public void setToSealLastPlanDepartTimeBegin(Date toSealLastPlanDepartTimeBegin) {
		this.toSealLastPlanDepartTimeBegin = toSealLastPlanDepartTimeBegin;
	}

	public Date getToSealLastPlanDepartTimeEnd() {
		return toSealLastPlanDepartTimeEnd;
	}

	public void setToSealLastPlanDepartTimeEnd(Date toSealLastPlanDepartTimeEnd) {
		this.toSealLastPlanDepartTimeEnd = toSealLastPlanDepartTimeEnd;
	}
}
