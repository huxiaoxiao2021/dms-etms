package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class CollectDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    private String waybillCode;

    private Integer nextSiteCode;

    private Integer collectNodeSiteCode;

    private String operatorErp;

    private String BizId;

    private String sealBatchCode;


    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getNextSiteCode() {
        return nextSiteCode;
    }

    public void setNextSiteCode(Integer nextSiteCode) {
        this.nextSiteCode = nextSiteCode;
    }

    public Integer getCollectNodeSiteCode() {
        return collectNodeSiteCode;
    }

    public void setCollectNodeSiteCode(Integer collectNodeSiteCode) {
        this.collectNodeSiteCode = collectNodeSiteCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getBizId() {
        return BizId;
    }

    public void setBizId(String bizId) {
        BizId = bizId;
    }

    public String getSealBatchCode() {
        return sealBatchCode;
    }

    public void setSealBatchCode(String sealBatchCode) {
        this.sealBatchCode = sealBatchCode;
    }
}
