package com.jd.bluedragon.distribution.collect.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 直送分拣揽收消息实体
 *
 * @author hujiping
 * @date 2024/3/12 4:46 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DirectDeliverySortCollectMQ implements Serializable {
    
    private static final long serialVersionUID = -9062704408138457497L;

    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 操作场地ID
     */
    private Integer operateSiteCode;
    /**
     * 操作场地名称
     */
    private String operateSiteName;
    /**
     * 操作人ID
     */
    private Integer operateUserId;
    /**
     * 操作人名称
     */
    private String operateUserName;
    /**
     * 包裹重量：kg
     */
    private BigDecimal weight;
    /**
     * 包裹长：cm
     */
    private BigDecimal length;
    /**
     * 包裹宽：cm
     */
    private BigDecimal width;
    /**
     * 包裹高：cm
     */
    private BigDecimal high;
    /**
     * 包裹体积：cm
     */
    private BigDecimal volume;
    /**
     * 操作时间
     */
    private String operateTime;
}
