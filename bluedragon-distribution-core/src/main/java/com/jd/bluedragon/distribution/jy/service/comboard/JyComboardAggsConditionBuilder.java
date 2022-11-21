package com.jd.bluedragon.distribution.jy.service.comboard;

import java.util.List;

public class JyComboardAggsConditionBuilder {

    private static final String DEFAULT_CONDITON_VALUE = "-1";

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

    public JyComboardAggsConditionBuilder operateSiteId(String operateSiteId) {
        this.operateSiteId = operateSiteId;
        return this;
    }

    public JyComboardAggsConditionBuilder receiveSiteId(String receiveSiteId) {
        this.receiveSiteId = receiveSiteId;
        return this;
    }

    public JyComboardAggsConditionBuilder bizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public JyComboardAggsConditionBuilder boardCode(String boardCode) {
        this.boardCode = boardCode;
        return this;
    }

    public JyComboardAggsConditionBuilder productType(String productType) {
        this.productType = productType;
        return this;
    }

    public JyComboardAggsConditionBuilder scanType(String scanType) {
        this.scanType = scanType;
        return this;
    }

    public JyComboardAggsConditionBuilder productTypes(List<String> productTypes) {
        this.productTypes = productTypes;
        return this;
    }

    public JyComboardAggsConditionBuilder scanTypes(List<String> scanTypes) {
        this.scanTypes = scanTypes;
        return this;
    }

    public JyComboardAggsCondition build() {
        return new JyComboardAggsCondition(operateSiteId == null? DEFAULT_CONDITON_VALUE : operateSiteId,
                receiveSiteId == null? DEFAULT_CONDITON_VALUE : receiveSiteId,
                bizId == null? DEFAULT_CONDITON_VALUE : bizId,
                boardCode == null? DEFAULT_CONDITON_VALUE : boardCode,
                productType == null? DEFAULT_CONDITON_VALUE : productType,
                scanType == null? DEFAULT_CONDITON_VALUE : scanType,
                productTypes == null? null : productTypes,
                scanTypes == null? null : scanTypes);
    }

}
