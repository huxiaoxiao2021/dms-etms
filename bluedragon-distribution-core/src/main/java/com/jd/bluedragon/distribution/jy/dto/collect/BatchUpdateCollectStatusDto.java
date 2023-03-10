package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class BatchUpdateCollectStatusDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    /**
     * 封车编码
     */
    private String bizId;
    /**
     * 操作时间
     */
    private Long operateTime;
    /**
     * 分批类型
     */
    private Integer batchType;

    /**
     * 操作面单号
     */
    private String scanCode;
    /**
     * 操作场地编码
     */
    private Integer scanSiteCode;




    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getBatchType() {
        return batchType;
    }

    public void setBatchType(Integer batchType) {
        this.batchType = batchType;
    }

    public String getScanCode() {
        return scanCode;
    }

    public void setScanCode(String scanCode) {
        this.scanCode = scanCode;
    }

    public Integer getScanSiteCode() {
        return scanSiteCode;
    }

    public void setScanSiteCode(Integer scanSiteCode) {
        this.scanSiteCode = scanSiteCode;
    }
}

