package com.jd.bluedragon.distribution.jy.dto.pickinggood;

/**
 * @Author zhengchengfa
 * @Date 2023/12/7 22:10
 * @Description
 */
public class PickingGoodTaskDetailInitDto {
    private static final long serialVersionUID = 1L;

    private String bizId;
    /**
     * 提货场地id
     */
    private Long pickingSiteId;
    /**
     * 提货机场编码/提货车站编码
     */
    private String pickingNodeCode;

    private String sealCarCode;

    private String batchCode;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getPickingSiteId() {
        return pickingSiteId;
    }

    public void setPickingSiteId(Long pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
    }

    public String getPickingNodeCode() {
        return pickingNodeCode;
    }

    public void setPickingNodeCode(String pickingNodeCode) {
        this.pickingNodeCode = pickingNodeCode;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }
}
