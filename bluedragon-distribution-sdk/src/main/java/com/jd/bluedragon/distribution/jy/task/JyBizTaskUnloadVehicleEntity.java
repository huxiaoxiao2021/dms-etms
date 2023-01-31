package com.jd.bluedragon.distribution.jy.task;


import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 卸车业务任务表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 15:18:58
 */
public class JyBizTaskUnloadVehicleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public JyBizTaskUnloadVehicleEntity() {}

    public JyBizTaskUnloadVehicleEntity(String bizId) {
        this.bizId = bizId;
    }

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 业务主键 = 封车编码
     */
    private String bizId;
    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * 车牌号
     */
    private String vehicleNumber;
    /**
     * 派车明细编码
     */
    private String transWorkItemCode;
    /**
     * 模糊查询车牌号
     */
    private String fuzzyVehicleNumber;
    /**
     * 任务状态；0-等待初始，1-在途，2-待解，3-待卸，4-卸车，5-卸车完成，6-取消
     */
    private Integer vehicleStatus;
    /**
     * 是否无任务卸车；1：是：0：否
     */
    private Integer manualCreatedFlag;
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
     * 排序时间
     */
    private Date sortTime;

    /**
     * 排名（积分模式）
     */
    private Integer ranking;
    /**
     * 预计到达时间
     */
    private Date predictionArriveTime;
    /**
     * 实际到达时间
     */
    private Date actualArriveTime;
    /**
     * 解封车时间
     */
    private Date desealCarTime;
    /**
     * 卸车完成时间
     */
    private Date unloadFinishTime;
    /**
     * 少扫件数
     */
    private Long lessCount;
    /**
     * 多扫件数
     */
    private Long moreCount;
    /**
     * 组板数量
     */
    private Integer comboardCount;
    /**
     * 拦截数量
     */
    private Integer interceptCount;
    /**
     * 是否异常；0-否 1-是
     */
    private Integer abnormalFlag;

    /**
     * 包裹总数
     */
    private Long totalCount;

    /**
     * 卸车进度
     */
    private BigDecimal unloadProgress;
    /**
     * 线路类型
     */
    private Integer lineType;
    /**
     * 线路类型名称
     */
    private String lineTypeName;
    /**
     * 任务标签 每一位当做一个标签
     */
    private String tagsSign;
    /**
     * 组号
     */
    private String refGroupCode;
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
    private String railwayPfNo;
    /**
     * 卸车开始时间
     */
    private Date unloadStartTime;
    /**
     * 卸车类型 1人工 2流水线
     */
    private Integer unloadType;
    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;
    /**
     * 数据库时间
     */
    private Date ts;
    /**
     * 状态列表
     */
    private List<Integer> statusCodeList;
    /**
     * 任务类别：1 分拣任务 2转运任务
     */
    private Integer taskType;

    /**
     * 时间字段过滤查询：解封车时间 状态等于待卸状态时使用update_time代替，因为解封车时间使用的是flink加工的字段防止有延迟
     */
    private Date actualArriveStartTime;

    public Long setId(Long id) {
        return this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String setBizId(String bizId) {
        return this.bizId = bizId;
    }

    public String getBizId() {
        return this.bizId;
    }

    public String setSealCarCode(String sealCarCode) {
        return this.sealCarCode = sealCarCode;
    }

    public String getSealCarCode() {
        return this.sealCarCode;
    }

    public String setVehicleNumber(String vehicleNumber) {
        return this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleNumber() {
        return this.vehicleNumber;
    }

    public String setTransWorkItemCode(String transWorkItemCode) {
        return this.transWorkItemCode = transWorkItemCode;
    }

    public String getTransWorkItemCode() {
        return this.transWorkItemCode;
    }

    public String setFuzzyVehicleNumber(String fuzzyVehicleNumber) {
        return this.fuzzyVehicleNumber = fuzzyVehicleNumber;
    }

    public String getFuzzyVehicleNumber() {
        return this.fuzzyVehicleNumber;
    }

    public Integer setVehicleStatus(Integer vehicleStatus) {
        return this.vehicleStatus = vehicleStatus;
    }

    public Integer getVehicleStatus() {
        return this.vehicleStatus;
    }

    public Integer setManualCreatedFlag(Integer manualCreatedFlag) {
        return this.manualCreatedFlag = manualCreatedFlag;
    }

    public Integer getManualCreatedFlag() {
        return this.manualCreatedFlag;
    }

    public Long setStartSiteId(Long startSiteId) {
        return this.startSiteId = startSiteId;
    }

    public Long getStartSiteId() {
        return this.startSiteId;
    }

    public String setStartSiteName(String startSiteName) {
        return this.startSiteName = startSiteName;
    }

    public String getStartSiteName() {
        return this.startSiteName;
    }

    public Long setEndSiteId(Long endSiteId) {
        return this.endSiteId = endSiteId;
    }

    public Long getEndSiteId() {
        return this.endSiteId;
    }

    public String setEndSiteName(String endSiteName) {
        return this.endSiteName = endSiteName;
    }

    public String getEndSiteName() {
        return this.endSiteName;
    }

    public Date setSortTime(Date sortTime) {
        return this.sortTime = sortTime;
    }

    public Date getSortTime() {
        return this.sortTime;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Date setPredictionArriveTime(Date predictionArriveTime) {
        return this.predictionArriveTime = predictionArriveTime;
    }

    public Date getPredictionArriveTime() {
        return this.predictionArriveTime;
    }

    public Date setActualArriveTime(Date actualArriveTime) {
        return this.actualArriveTime = actualArriveTime;
    }

    public Date getActualArriveTime() {
        return this.actualArriveTime;
    }

    public Date setDesealCarTime(Date desealCarTime) {
        return this.desealCarTime = desealCarTime;
    }

    public Date getDesealCarTime() {
        return this.desealCarTime;
    }

    public Date setUnloadFinishTime(Date unloadFinishTime) {
        return this.unloadFinishTime = unloadFinishTime;
    }

    public Date getUnloadFinishTime() {
        return this.unloadFinishTime;
    }

    public Long setLessCount(Long lessCount) {
        return this.lessCount = lessCount;
    }

    public Long getLessCount() {
        return this.lessCount;
    }

    public Long setMoreCount(Long moreCount) {
        return this.moreCount = moreCount;
    }

    public Long getMoreCount() {
        return this.moreCount;
    }

    public Integer setAbnormalFlag(Integer abnormalFlag) {
        return this.abnormalFlag = abnormalFlag;
    }

    public Integer getAbnormalFlag() {
        return this.abnormalFlag;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal setUnloadProgress(BigDecimal unloadProgress) {
        return this.unloadProgress = unloadProgress;
    }

    public BigDecimal getUnloadProgress() {
        return this.unloadProgress;
    }

    public Integer setLineType(Integer lineType) {
        return this.lineType = lineType;
    }

    public Integer getLineType() {
        return this.lineType;
    }

    public String setLineTypeName(String lineTypeName) {
        return this.lineTypeName = lineTypeName;
    }

    public String getLineTypeName() {
        return this.lineTypeName;
    }

    public String setTagsSign(String tagsSign) {
        return this.tagsSign = tagsSign;
    }

    public String getTagsSign() {
        return this.tagsSign;
    }

    public String getRefGroupCode() {
        return refGroupCode;
    }

    public void setRefGroupCode(String refGroupCode) {
        this.refGroupCode = refGroupCode;
    }

    public String setCreateUserErp(String createUserErp) {
        return this.createUserErp = createUserErp;
    }

    public String getCreateUserErp() {
        return this.createUserErp;
    }

    public String setCreateUserName(String createUserName) {
        return this.createUserName = createUserName;
    }

    public String getCreateUserName() {
        return this.createUserName;
    }

    public String setUpdateUserErp(String updateUserErp) {
        return this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserErp() {
        return this.updateUserErp;
    }

    public String setUpdateUserName(String updateUserName) {
        return this.updateUserName = updateUserName;
    }

    public String getUpdateUserName() {
        return this.updateUserName;
    }

    public Date setCreateTime(Date createTime) {
        return this.createTime = createTime;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public Date setUpdateTime(Date updateTime) {
        return this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public Integer setYn(Integer yn) {
        return this.yn = yn;
    }

    public Integer getYn() {
        return this.yn;
    }

    public Date setTs(Date ts) {
        return this.ts = ts;
    }

    public Date getTs() {
        return this.ts;
    }

    public boolean unloadWithoutTask() {
        return this.manualCreatedFlag != null && this.manualCreatedFlag == 1;
    }

    public boolean unloadAbnormal() {
        return this.abnormalFlag != null && this.abnormalFlag == 1;
    }

	public List<Integer> getStatusCodeList() {
		return statusCodeList;
	}

	public void setStatusCodeList(List<Integer> statusCodeList) {
		this.statusCodeList = statusCodeList;
	}

    public Integer getComboardCount() {
        return comboardCount;
    }

    public void setComboardCount(Integer comboardCount) {
        this.comboardCount = comboardCount;
    }

    public Integer getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Integer interceptCount) {
        this.interceptCount = interceptCount;
    }

    public Date getUnloadStartTime() {
        return unloadStartTime;
    }

    public void setUnloadStartTime(Date unloadStartTime) {
        this.unloadStartTime = unloadStartTime;
    }

    public Integer getUnloadType() {
        return unloadType;
    }

    public void setUnloadType(Integer unloadType) {
        this.unloadType = unloadType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }


    public String getRailwayPfNo() {
        return railwayPfNo;
    }

    public void setRailwayPfNo(String railwayPfNo) {
        this.railwayPfNo = railwayPfNo;
    }


    public Date getActualArriveStartTime() {
        return actualArriveStartTime;
    }

    public void setActualArriveStartTime(Date actualArriveStartTime) {
        this.actualArriveStartTime = actualArriveStartTime;
    }
}
