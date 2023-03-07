package com.jd.bluedragon.distribution.jy.dto.collect;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //
 * @date
 **/
public class CollectReportReqDto extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    private String bizId;
    private String waybillCode;
    /**
     * 集齐类型
     * CollectTypeEnum
     */
    private Integer collectType;

    /**
     * 是否自建任务标识： true
     */
    private Boolean manualCreateTaskFlag;
    /**
     * 页容量
     */
    private int pageSize;
    /**
     * 页码
     */
    private int pageNo;



    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public Boolean getManualCreateTaskFlag() {
        return manualCreateTaskFlag;
    }

    public void setManualCreateTaskFlag(Boolean manualCreateTaskFlag) {
        this.manualCreateTaskFlag = manualCreateTaskFlag;
    }
}
