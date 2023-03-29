package com.jd.bluedragon.distribution.jy.service.comboard;

import lombok.Data;

import java.util.List;

@Data
public class JyComboardAggsCondition {

    /**
     * 始发
     */
    private Integer operateSiteId;
    /**
     * 目的
     */
    private Integer receiveSiteId;
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
    private Integer scanType;

    private List<String> productTypes;

    private List<Integer> scanTypes;

    private List<Integer> receiveSiteIds;

    private List<String> boardCodes;


    public JyComboardAggsCondition(Integer operateSiteId, Integer receiveSiteId, String bizId, String boardCode, String productType, Integer scanType, List<String> productTypes, List<Integer> scanTypes, List<Integer> receiveSiteIds, List<String> boardCodes) {
        this.operateSiteId = operateSiteId;
        this.receiveSiteId = receiveSiteId;
        this.bizId = bizId;
        this.boardCode = boardCode;
        this.productType = productType;
        this.scanType = scanType;
        this.productTypes = productTypes;
        this.scanTypes = scanTypes;
        this.receiveSiteIds = receiveSiteIds;
        this.boardCodes = boardCodes;
    }

    public String redisKey(){
        return "comboardAggs:" + operateSiteId + ":" + receiveSiteId + ":" + bizId + ":" + boardCode + ":" + productType + ":" + scanType;
    }

}
