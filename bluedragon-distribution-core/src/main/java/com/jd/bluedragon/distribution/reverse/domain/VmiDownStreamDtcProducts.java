package com.jd.bluedragon.distribution.reverse.domain;

import com.google.common.collect.Lists;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.List;

/**
 * 出大库报文 商品明细信息
 *
 * @author litonggang
 */
public class VmiDownStreamDtcProducts implements Serializable {

    private final List<VmiDownStreamDtcProduct> downStreamDtcProductList;

    public VmiDownStreamDtcProducts() {
        downStreamDtcProductList = Lists.newArrayList();
    }

    public List<VmiDownStreamDtcProduct> getDownStreamDtcProductList() {
        return downStreamDtcProductList;
    }

    public void addDownStreamDtcProduct(VmiDownStreamDtcProduct product) {
        this.downStreamDtcProductList.add(product);
    }
}
