package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UnloadVehicleTaskDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = -4856090554484728089L;
    /**
     * 业务主键
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
     * 线路类型编码
     */
    private Integer lineType;
    /**
     * 线路类型名称
     */
    private String lineTypeName;
    private String tagsSign;
    /**
     * 属性标签
     */
    private List<LabelOptionDto> labelOptionList;
    /**
     * 始发场地ID（上游场地）
     */
    private Long startSiteId;
    private String startSiteName;
    /**
     * 目的场地ID（下游场地）
     */
    private Long endSiteId;
    private String endSiteName;
    /**
     * 进度时间
     */
    private String processTime;

    /**
     * 卸车进度
     */
    private Integer processPercent;
    /**
     * 组板数量
     */
    private Integer comBoardCount;
    /**
     * 拦截数量
     */
    private Integer interceptCount;
    /**
     * 多扫数量
     */
    private Integer extraScanCount;


    /**
     * 应扫包裹/运单数量(总数)
     */
    private Integer shouldScanCount;

    /**
     * 待扫
     */
    private Integer waitScanCount;

    /**
     * 已扫
     */
    private Integer haveScanCount;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    private Date actualArriveTime;
    private Date unloadStartTime;
    private Date unloadFinishTime;

    /**
     * JyBizTaskUnloadStatusEnum
     */
    private Integer vehicleStatus;
    /**
     * 类型：1人工 2流水线'
     */
    private Integer unloadType;
    /**
     * 月台号
     */
    private String railwayPfNo;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    @Override
    public String getVehicleNumber() {
        return vehicleNumber;
    }

    @Override
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
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

    public String getTagsSign() {
        return tagsSign;
    }

    public void setTagsSign(String tagsSign) {
        this.tagsSign = tagsSign;
    }

    public List<LabelOptionDto> getLabelOptionList() {
        return labelOptionList;
    }

    public void setLabelOptionList(List<LabelOptionDto> labelOptionList) {
        this.labelOptionList = labelOptionList;
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

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public Integer getProcessPercent() {
        return processPercent;
    }

    public void setProcessPercent(Integer processPercent) {
        this.processPercent = processPercent;
    }

    public Integer getComBoardCount() {
        return comBoardCount;
    }

    public void setComBoardCount(Integer comBoardCount) {
        this.comBoardCount = comBoardCount;
    }

    public Integer getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Integer interceptCount) {
        this.interceptCount = interceptCount;
    }

    public Integer getExtraScanCount() {
        return extraScanCount;
    }

    public void setExtraScanCount(Integer extraScanCount) {
        this.extraScanCount = extraScanCount;
    }

    public Integer getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Integer shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public Integer getWaitScanCount() {
        return waitScanCount;
    }

    public void setWaitScanCount(Integer waitScanCount) {
        this.waitScanCount = waitScanCount;
    }

    public Integer getHaveScanCount() {
        return haveScanCount;
    }

    public void setHaveScanCount(Integer haveScanCount) {
        this.haveScanCount = haveScanCount;
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

    public Date getActualArriveTime() {
        return actualArriveTime;
    }

    public void setActualArriveTime(Date actualArriveTime) {
        this.actualArriveTime = actualArriveTime;
    }

    public Date getUnloadStartTime() {
        return unloadStartTime;
    }

    public void setUnloadStartTime(Date unloadStartTime) {
        this.unloadStartTime = unloadStartTime;
    }

    public Date getUnloadFinishTime() {
        return unloadFinishTime;
    }

    public void setUnloadFinishTime(Date unloadFinishTime) {
        this.unloadFinishTime = unloadFinishTime;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Integer getUnloadType() {
        return unloadType;
    }

    public void setUnloadType(Integer unloadType) {
        this.unloadType = unloadType;
    }

    public String getRailwayPfNo() {
        return railwayPfNo;
    }

    public void setRailwayPfNo(String railwayPfNo) {
        this.railwayPfNo = railwayPfNo;
    }
}
