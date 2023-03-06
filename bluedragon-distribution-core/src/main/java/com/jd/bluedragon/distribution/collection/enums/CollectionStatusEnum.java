package com.jd.bluedragon.distribution.collection.enums;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.enums
 * @ClassName: CollectionStatusEnum
 * @Description: 拣运能力域-待集齐集合的状态枚举
 * @Author： wuzuxiang
 * @CreateDate 2023/2/27 15:29
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum CollectionStatusEnum {

    /**
     * 未集
     */
    none_collected(0),

    /**
     * 集齐
     */
    collected(1),

    /**
     * 多集
     */
    extra_collected(2),

    ;

    private final Integer status;

    CollectionStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
