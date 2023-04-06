package com.jd.bluedragon.distribution.collection.entity;

import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionCollectedMarkCounter
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/12 16:43
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class CollectionCollectedMarkCounter {

    /**
     * 待集齐集合ID
     */
    private String collectionCode;

    /**
     * 待集齐数据所属业务类型
     */
    private CollectionBusinessTypeEnum businessType;

    /**
     * 聚合统计号
     */
    private String aggCode;

    /**
     * 聚合统计类型
     */
    private String aggCodeType;

    /**
     * 集齐标识
     */
    private String collectedMark;

    /**
     * 聚合统计标识
     */
    private String aggMark;

    /**
     * 集齐状态
     */
    private Integer collectedStatus;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 数据时间，可以判断数据是否发生更新
     */
    private Timestamp ts;
}
