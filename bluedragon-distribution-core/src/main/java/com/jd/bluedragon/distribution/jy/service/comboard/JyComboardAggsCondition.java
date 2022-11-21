package com.jd.bluedragon.distribution.jy.service.comboard;

import lombok.Data;

import java.util.List;

@Data
public class JyComboardAggsCondition {

    /**
     * 始发
     */
    private String operateSiteId;
    /**
     * 目的
     */
    private String receiveSiteId;
    /**
     * 业务id
     */
    private String bizId;
    /**
     * 板号
     */
    private String boardCode;
    /**
     * 产品类型
     */
    private String productType;
    /**
     * 扫描类型
     */
    private String scanType;

    private List<String> productTypes;

    private List<String> scanTypes;

    public JyComboardAggsCondition(String operateSiteId, String receiveSiteId, String bizId, String boardCode, String productType, String scanType, List<String> productTypes, List<String> scanTypes) {
        this.operateSiteId = operateSiteId;
        this.receiveSiteId = receiveSiteId;
        this.bizId = bizId;
        this.boardCode = boardCode;
        this.productType = productType;
        this.scanType = scanType;
        this.productTypes = productTypes;
        this.scanTypes = scanTypes;
    }

    public String redisKey(){
        return "comboardAggs:" + operateSiteId + ":" + receiveSiteId + ":" + bizId + ":" + boardCode + ":" + productType + ":" + scanType;
    }

}
