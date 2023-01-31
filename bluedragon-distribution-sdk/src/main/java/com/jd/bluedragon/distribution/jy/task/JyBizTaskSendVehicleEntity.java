package com.jd.bluedragon.distribution.jy.task;


import java.io.Serializable;
import java.util.Date;

/**
 * 发车业务任务表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JyBizTaskSendVehicleEntity implements Serializable {

	private static final long serialVersionUID = -2352536857784355996L;

    public static final String BIZ_PREFIX = "SST%s";
	public static final String BIZ_PREFIX_NOTASK = "NSST%s";

    public JyBizTaskSendVehicleEntity() {}

    public JyBizTaskSendVehicleEntity(String transWorkCode, Long startSiteId) {
        this.transWorkCode = transWorkCode;
        this.startSiteId = startSiteId;
    }
    /**
     * 派车单始发场地（相同派车单不同始发场地会散列成多条）
     */
    private Long startSiteId;
    /**
     * 派车单最晚预计发车时间（取相同始发地派车明细最晚预计发车时间）
     */
    private Date lastPlanDepartTime;

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 业务主键
	 */
	private String bizId;
	/**
	 * 自建任务的流水号
	 */
	private String bizNo;
	private String taskName;
	/**
	 * 运输派车单编码
	 */
	private String transWorkCode;
    /**
     * 派车单最晚封车时间（取相同始发地派车明细最晚封车时间）
     */
    private Date lastSealCarTime;

	/**
	 * 到来时间 取明细中最早数据（对应当前始发场地）
	 */
	private Date comeTime;
	/**
	 * 即将到来时间 取明细中最早数据（对应当前始发场地相距一定范围内）
	 */
	private Date nearComeTime;

	/**
	 * 车牌号
	 */
	private String vehicleNumber;
	/**
	 * 任务状态；0-待发货，1-发货中，2-待封车，3-已封车
     * <see>{@link com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum}</see>
	 */
	private Integer vehicleStatus;
	/**
	 * 前一个状态
	 */
	private Integer preVehicleStatus;
	/**
	 * 是否无任务发车；0-否 1-是
	 */
	private Integer manualCreatedFlag;
	/**
	 * 是否异常；0-否 1-是
	 */
	private Integer abnormalFlag;
	/**
	 * 无任务是否绑定；0-否 1-是
	 */
	private Integer bindFlag;
	/**
	 * 运输方式
	 */
	private Integer transWay;
	/**
	 * 运输方式名称
	 */
	private String transWayName;
	/**
	 * 车型
	 */
	private Integer vehicleType;
	/**
	 * 车型名称
	 */
	private String vehicleTypeName;
	/**
	 * 线路类型
	 */
	private Integer lineType;
	/**
	 * 线路类型名称
	 */
	private String lineTypeName;

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

	private transient Date lastPlanDepartTimeBegin;

	/**
	 * 最晚计划发车时间 范围查找-结束时间
	 */
	private transient Date lastPlanDepartTimeEnd;

	private transient Date createTimeBegin;

	public Integer getPreVehicleStatus() {
		return preVehicleStatus;
	}

	public void setPreVehicleStatus(Integer preVehicleStatus) {
		this.preVehicleStatus = preVehicleStatus;
	}

	public String getBizNo() {
		return bizNo;
	}

	public void setBizNo(String bizNo) {
		this.bizNo = bizNo;
	}

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

	public String getTransWorkCode() {
		return transWorkCode;
	}

	public void setTransWorkCode(String transWorkCode) {
		this.transWorkCode = transWorkCode;
	}

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public Integer getVehicleStatus() {
		return vehicleStatus;
	}

	public void setVehicleStatus(Integer vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}

	public Integer getManualCreatedFlag() {
		return manualCreatedFlag;
	}

	public void setManualCreatedFlag(Integer manualCreatedFlag) {
		this.manualCreatedFlag = manualCreatedFlag;
	}

	public Integer getAbnormalFlag() {
		return abnormalFlag;
	}

	public void setAbnormalFlag(Integer abnormalFlag) {
		this.abnormalFlag = abnormalFlag;
	}

	public Integer getBindFlag() {
		return bindFlag;
	}

	public void setBindFlag(Integer bindFlag) {
		this.bindFlag = bindFlag;
	}

	public Integer getTransWay() {
		return transWay;
	}

	public void setTransWay(Integer transWay) {
		this.transWay = transWay;
	}

	public String getTransWayName() {
		return transWayName;
	}

	public void setTransWayName(String transWayName) {
		this.transWayName = transWayName;
	}

	public Integer getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(Integer vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getVehicleTypeName() {
		return vehicleTypeName;
	}

	public void setVehicleTypeName(String vehicleTypeName) {
		this.vehicleTypeName = vehicleTypeName;
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

    public Date getLastPlanDepartTime() {
        return lastPlanDepartTime;
    }

    public void setLastPlanDepartTime(Date lastPlanDepartTime) {
        this.lastPlanDepartTime = lastPlanDepartTime;
    }

    public Date getLastSealCarTime() {
        return lastSealCarTime;
    }

    public void setLastSealCarTime(Date lastSealCarTime) {
        this.lastSealCarTime = lastSealCarTime;
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

    public boolean manualCreatedTask() {
        return this.manualCreatedFlag != null && this.manualCreatedFlag == 1;
    }

    public boolean noTaskBindVehicle() {
        return this.bindFlag != null && this.bindFlag == 1;
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

	public boolean hasBeenBindedOrDeleted(){
		if (this.bindFlag!=null && this.bindFlag==1){
			return true;
		}
		if (this.yn!=null && this.yn==0){
			return true;
		}
		return false;
	}


	public Date getCreateTimeBegin() {
		return createTimeBegin;
	}

	public void setCreateTimeBegin(Date createTimeBegin) {
		this.createTimeBegin = createTimeBegin;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
