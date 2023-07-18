package com.jd.bluedragon.distribution.reverse.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * 出大库报文 商品明细信息
 *
 * @author litonggang
 */
@Data
@NoArgsConstructor
public class VmiDownStreamDtcProduct implements Serializable {

    /**
     * 商品ID，商品sku
     */
    private Long productId;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品数量，sku对应商品实际数量， productLoss+productNum=sku对应商品订单数量
     */
    private Integer productNum;
    /**
     * 报损数量，sku对应商品丢失数量，默认：0
     */
    private Integer productLoss;
    /**
     * 商品单价
     */
    private String productPrice;
}
