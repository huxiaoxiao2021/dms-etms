package com.jd.bluedragon.distribution.collectNew.entity;

import lombok.Data;

import java.util.Date;


@Data
public class JyCollectRecordPo {
    private Long id;

    private String collectionCode;

    private Long siteId;

    private String aggCode;

    private String aggCodeType;

    private Integer shouldCollectNum;
    /**
     * 已扫数量（维度根据不同业务定义，当前维度是包裹维度）
     */
    private Integer realCollectNum;

    private Integer isCollected;
    /**
     * 单据类型：
     * com.jd.bluedragon.distribution.jy.constants.WaybillCustomTypeEnum
     */
    private String customType;

    private String aggMark;

    private Date createTime;

    private Date updateTime;

    private Integer yn;

    private Date ts;

}