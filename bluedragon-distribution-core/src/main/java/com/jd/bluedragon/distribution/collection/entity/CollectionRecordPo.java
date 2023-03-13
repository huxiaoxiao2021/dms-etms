package com.jd.bluedragon.distribution.collection.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionRecordPo
 * @Description: collection_record数据库对象，待集齐集合的统计表
 * @Author： wuzuxiang
 * @CreateDate 2023/2/28 21:12
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionRecordPo {

    /**
     * 数据库ID
     */
    private Long id;

    /**
     * 待集齐集合ID
     */
    private String collectionCode;

    /**
     * 待集齐集合的统计聚合号
     * 例如：运单号
     */
    private String aggCode;

    /**
     * 待集齐集合的聚合统计类型
     * 例如：运单号类型
     * @see com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum
     */
    private String aggCodeType;

    /**
     * 在该待集齐集合下，该聚合下的总数
     * 例如：运单下共有多少个包裹数
     */
    private Integer sum;

    /**
     *  是否集齐
     *  0 未集齐
     *  1 集齐
     */
    private Integer isCollected;

    /**
     * 是否存在多集（多扫）
     * 0 不存在多集（多扫）
     * 1 存在多集（多扫）
     */
    private Integer isExtraCollected;

    /**
     * 聚合统计号aggCode的特殊标识
     * 主要用于分类和排序
     */
    private String aggMark;

    /**
     * 数据创建时间
     */
    private Date createTime;

    /**
     * 数据更新时间
     */
    private Date updateTime;

    /**
     * 数据是否有效标志
     */
    private Integer yn;

    /**
     * 数据库时间
     */
    private Timestamp ts;
}
