package com.jd.bluedragon.distribution.collection.entity;

import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
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
    private String scanCodeType;

    /**
     * 待集齐单号的各种聚合维度下的聚合编码，
     * 根据businessType的不同，取不同的aggCodes
     */
    private Map<CollectionAggCodeTypeEnum, String> collectionAggCodeMaps;

}
