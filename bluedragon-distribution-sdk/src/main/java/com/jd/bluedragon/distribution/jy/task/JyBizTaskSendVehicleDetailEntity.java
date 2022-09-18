package com.jd.bluedragon.distribution.jy.task;


import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

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

	private transient Date lastPlanDepartTimeBegin;

	/**
	 * 最晚计划发车时间 范围查找-结束时间
	 */
	private transient Date lastPlanDepartTimeEnd;

	private Integer excepLabel;
	private transient Date createTimeBegin;

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
			return o2.getCreateTime().compareTo(o1.getCreateTime());
		}
	}
}
