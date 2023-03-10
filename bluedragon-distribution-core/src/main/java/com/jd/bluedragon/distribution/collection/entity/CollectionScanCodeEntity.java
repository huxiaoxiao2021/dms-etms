package com.jd.bluedragon.distribution.collection.entity;

import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionScanCodeTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionScanCodeEntity
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/1 15:42
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class CollectionScanCodeEntity {

    /**
     * 待集齐单号
     */
    private String scanCode;

    /**
     * 待集齐单号类型
     */
    private CollectionScanCodeTypeEnum scanCodeType;

    /**
     * 待集齐单号的类型，用于标记待集齐单号
     * 比如末端集齐场景中，如果collectedMark有值说明是已到或者是已扫的，无值说明是未到的，是否是本车集齐的也可以用collectedMark来实现，比如存卸车任务号
     */
    private String collectedMark;

    /**
     * 待集齐单号的各种聚合维度下的聚合编码，
     * 根据businessType的不同，取不同的aggCodes
     */
    private Map<CollectionAggCodeTypeEnum, String> collectionAggCodeMaps;

}
