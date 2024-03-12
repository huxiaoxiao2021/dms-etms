package com.jd.bluedragon.distribution.collect.domain;

import com.jd.etms.waybill.dto.BigWaybillDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 直送分拣揽收请求体
 *
 * @author hujiping
 * @date 2024/2/29 10:32 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DirectDeliverySortCollectRequest {
    
    /**
     * 包裹号|运单号
     */
    private String packOrWaybillCode;

    /**
     * 操作场地ID
     */
    private Integer operateSiteCode;

    /**
     * 操作场地名称
     */
    private String operateSiteName;

    /**
     * 操作场地ID
     */
    private Integer operateUserId;

    /**
     * 操作人ERP
     */
    private String operateUserErp;

    /**
     * 操作人名称
     */
    private String operateUserName;

    /**
     * 揽收包裹重量:CM
     */
    private BigDecimal weight;

    /**
     * 揽收包裹长:CM
     */
    private BigDecimal length;

    /**
     * 揽收包裹宽:CM
     */
    private BigDecimal width;

    /**
     * 揽收包裹高:CM
     */
    private BigDecimal high;

    /**
     * 揽收包裹体积:CM3
     */
    private BigDecimal volume;

    /**
     * 操作时间
     */
    private String operateTime;

    /**
     * 运单对象
     */
    private BigWaybillDto bigWaybillDto;

}
