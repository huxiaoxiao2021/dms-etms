package com.jd.bluedragon.distribution.reverse.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 出大库报文 主单据信息
 *
 * @author litonggang
 */
@Data
@NoArgsConstructor
public class VmiDownStreamDtcMessage implements Serializable {

    private static final long serialVersionUID = -2851964453190340297L;

    /**
     * 运单号
     */
    private String busiOrderCode;
    /**
     * 配送中心编码
     */
    private Integer cky2;
    /**
     * 报损数量，商品丢失数量默认：0
     */
    private Integer lossQuantity;
    /**
     * 收货时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private String operateTime;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 机构号
     */
    private Integer orgId;
    /**
     * 包裹号，逗号分隔，订单的实扫包裹号；如有报丢，此处为所有出库包裹号
     */
    private String packageCodes;
    /**
     * 批次编号，青龙发货批次
     */
    private String sendCode;
    /**
     * 库房号
     */
    private Integer storeId;
    /**
     * 收货人
     */
    private String userName;
    /**
     * 订单总数，仅供上海亚一仓使用
     */
    private Integer orderSum;
    /**
     * 包裹总数，仅供上海亚一仓使用
     */
    private Integer packSum;
    /**
     * 病单标识，非上海亚一仓使用，病单标识 true为病单，按包裹维度发送数据对应的packageCodes中只有一个包裹
     */
    private Boolean sickWaybill;
    /**
     * 病单需要，非上海亚一仓使用，病单需要，对分拣无意义
     */
    private String token;
    /**
     * 运单类型，非上海亚一仓使用，运单类型，取自运单属性中的waybillType
     */
    private Integer xniType;
    /**
     * 门店编码，非上海亚一仓使用，取自运单属性中的shopCode
     */
    private String venderId;
    /**
     * 退仓类型编码，0：客退入，为1：病单入，为2：预售入
     */
    private Integer guestBackType;

    private VmiDownStreamDtcProducts downStreamDtcProducts;
    /**
     * 预售标识
     */
    private Integer preSellType;
    
    private Integer isInStore;
}
