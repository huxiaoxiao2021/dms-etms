package com.jd.bluedragon.distribution.jy.dto.collect;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class UnloadScanCollectDealDto extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    private String bizId;
    private String scanCode;
    /**
     * 扫描面单类型
     * ScanCodeTypeEnum
     */
    private Integer scanCodeType;

    /**
     * 集齐类型
     * CollectTypeEnum
     */
    private Integer collectType;
    /**
     * 是否自建任务标识： true
     */
    private Boolean manualCreateTaskFlag;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getScanCode() {
        return scanCode;
    }

    public void setScanCode(String scanCode) {
        this.scanCode = scanCode;
    }

    public Integer getScanCodeType() {
        return scanCodeType;
    }

    public void setScanCodeType(Integer scanCodeType) {
        this.scanCodeType = scanCodeType;
    }

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public Boolean getManualCreateTaskFlag() {
        return manualCreateTaskFlag;
    }

    public void setManualCreateTaskFlag(Boolean manualCreateTaskFlag) {
        this.manualCreateTaskFlag = manualCreateTaskFlag;
    }
}
