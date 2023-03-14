package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 任务列表
 */
public class ExpTaskDto implements Serializable {
    //业务ID
    private String bizId;

    // 提报入口
    private Integer source;

    // 任务状态
    private Integer status;

    //提交条码
    private String barCode;

    //停留时间 hh:mm
    private String stayTime;

    //楼层
    private Integer floor;

    //网格编号
    private String gridCode;

    //网格号
    private String gridNo;

    //工作区名称
    private String areaName;

    //提报人姓名
    private String reporterName;

    //标签列表
    private List<TagDto> tags;

    // 产生时间
    private String createTime;

    // 图片地址 逗号分割
    private String imageUrls;

    //是否保存过
    private boolean saved;

    /**
     * 是否超时：1- 超时，0-未超时
     */
    private Integer timeOut;

    /**
     * 异常类型0：三无 1：报废 2：破损
     */
    private Integer type;

    /**
     * 审核人erp
     */
    private String checkerErp;

    /**
     * 审批时间
     */
    private Date checkTime;

    /**
     * 任务子状态 0:待录入 1：待匹配 2：暂存 3: 处理完成 4：待打印 、5：审批中、6：审批驳回、7：客服介入
     */
    private Integer processingStatus;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getStayTime() {
        return stayTime;
    }

    public void setStayTime(String stayTime) {
        this.stayTime = stayTime;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

    public boolean getSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public boolean isSaved() {
        return saved;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCheckerErp() {
        return checkerErp;
    }

    public void setCheckerErp(String checkerErp) {
        this.checkerErp = checkerErp;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public Integer getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(Integer processingStatus) {
        this.processingStatus = processingStatus;
    }
}
