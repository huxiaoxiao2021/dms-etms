package com.jd.bluedragon.distribution.jy.service.comboard;

import java.util.List;

public class JyComboardAggsConditionBuilder {

    private static final String DEFAULT_CONDITON_VALUE = "-1";
    private static final Integer DEFAULT_CONDITON_INTEGER_VALUE = -1;

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
    private String scanType;

    private List<String> productTypes;

    private List<String> scanTypes;

    private List<Integer> receiveSiteIds;

    private List<String> boardCodes;

    public JyComboardAggsConditionBuilder operateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
        return this;
    }

    public JyComboardAggsConditionBuilder receiveSiteId(Integer receiveSiteId) {
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

    public JyComboardAggsConditionBuilder receiveSiteIds(List<Integer> receiveSiteIds) {
        this.receiveSiteIds = receiveSiteIds;
        return this;
    }

    public JyComboardAggsConditionBuilder boardCodes(List<String> boardCodes) {
        this.boardCodes = boardCodes;
        return this;
    }

    public JyComboardAggsCondition build() {
        return new JyComboardAggsCondition(operateSiteId==null?DEFAULT_CONDITON_INTEGER_VALUE:operateSiteId,
                receiveSiteId==null?DEFAULT_CONDITON_INTEGER_VALUE:receiveSiteId,
                bizId==null?DEFAULT_CONDITON_VALUE:bizId,
                boardCode==null?DEFAULT_CONDITON_VALUE:boardCode,
                productType==null?DEFAULT_CONDITON_VALUE:productType,
                scanType==null?DEFAULT_CONDITON_VALUE:scanType,
                productTypes, scanTypes, receiveSiteIds, boardCodes);
    }

}
