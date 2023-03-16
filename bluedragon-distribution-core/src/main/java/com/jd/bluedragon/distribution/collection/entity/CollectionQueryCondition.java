package com.jd.bluedragon.distribution.collection.entity;

import lombok.Data;

import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionQueryCondition
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/16 18:34
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class CollectionQueryCondition {

    private List<String> collectionCodes;

    private String aggCode;

    private String aggCodeType;

    private String scanCode;

    private String scanCodeType;

    private String aggMark;

    private String collectedMark;

    private String collectedStatus;
}
