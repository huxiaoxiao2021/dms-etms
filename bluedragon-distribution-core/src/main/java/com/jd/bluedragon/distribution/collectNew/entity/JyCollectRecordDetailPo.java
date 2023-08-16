package com.jd.bluedragon.distribution.collectNew.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JyCollectRecordDetailPo {
    private Long id;

    private String collectionCode;

    private Long siteId;

    private String scanCode;

    private String scanCodeType;

    private String aggCode;

    private String aggCodeType;

    private String collectedMark;

    private Date createTime;

    private Boolean yn;

    private Date ts;

    private Date operateTime;

}