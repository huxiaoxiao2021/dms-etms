package com.jd.bluedragon.distribution.collection.entity;

import lombok.Data;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionScanCodeCounter
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/10 16:24
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class CollectionScanCodeCounter {

    private String collectionCode;

    private String aggCode;

    private String aggCodeType;

    private String scanCode;

    private String scanCodeType;

    private String collectedStatus;

    private String collectedMark;
}
