package com.jd.bluedragon.distribution.collection.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionRecordPo
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/2/28 21:12
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
@Builder
@NoArgsConstructor
public class CollectionRecordDetailPo {

    /**
     * 数据库ID
     */
    private Long id;

    /**
     * 待集齐集合ID
     */
    private String collectionCode;

    /**
     * 待集齐集合单号
     * 例如：包裹号
     */
    private String scanCode;

    /**
     * 待集齐单号类型
     * 例如：包裹号类型
     * @see com.jd.bluedragon.distribution.collection.enums.CollectionScanCodeTypeEnum
     */
    private String scanCodeType;

    /**
     * 待集齐集合中待集齐单号的聚合统计号
     * 例如：运单号
     */
    private String aggCode;

    /**
     * 待集齐集合中，待集齐单号的聚合统计号类型
     * 例如：运单号类型
     */
    private String aggCodeType;

    /**
     * 待集齐集合中，待集齐单号的集齐状态
     * 0 未集(未扫)
     * 1 已集(已扫)
     * 2 多集(多扫)
     * @see com.jd.bluedragon.distribution.collection.enums.CollectionStatusEnum
     */
    private Integer collectedStatus;

    /**
     * 待集齐集合中，该待集齐单号在集齐时的标志
     * 用于标记 分类 或者排序等
     */
    private String collectedMark;

    /**
     * 待集齐创建时间
     */
    private Date createTime;

    /**
     * 集齐时间或者多扫时间
     */
    private Date collectedTime;

    /**
     * 数据是否有效标志
     */
    private Integer yn;

    /**
     * 数据库时间
     */
    private Timestamp ts;


}
