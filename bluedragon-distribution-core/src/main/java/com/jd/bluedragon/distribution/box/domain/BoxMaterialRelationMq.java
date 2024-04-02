package com.jd.bluedragon.distribution.box.domain;

import lombok.Data;

import java.util.Date;

/**
 * 包裹绑定集包袋
 */
@Data
public class BoxMaterialRelationMq {
    // 容器编码
    private String containerCode;

    // 容器类型 1、箱号
    private Integer containerType;

    // 物资编码
    private String materialsCode;

    // 操作人id
    private Integer operatorId;

    // 操作站点
    private Integer operateSiteId;

    // 操作时间
    private Date operateTime;
}
