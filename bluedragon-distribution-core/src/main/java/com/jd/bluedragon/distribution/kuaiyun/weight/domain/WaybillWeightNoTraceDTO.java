package com.jd.bluedragon.distribution.kuaiyun.weight.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运单称重 用于发送mq消息给运单
 * <p>
 *     区别于 WaybillWeightDTO，此对象只上传运单称重流水，不发全程跟踪
 * </p>
 *
 * @author hujiping
 * @date 2024/4/1 2:28 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WaybillWeightNoTraceDTO implements Serializable {
    
    private static final long serialVersionUID = 6383155356363656683L;
    
    // 运单号
    private String waybillCode;
    // 重量：kg
    private BigDecimal weight;
    // 长：cm
    private BigDecimal pLength;
    // 宽：cm
    private BigDecimal pWidth;
    // 高：cm
    private BigDecimal pHigh;
    // 操作人ID
    private Integer operatorId;
    // 操作人名称
    private String operatorName;
    // 操作场地ID
    private Integer opeSiteId;
    // 操作场地名称
    private String opeSiteName;
    // 操作时间戳
    private Long operateTimeMillis;
}
