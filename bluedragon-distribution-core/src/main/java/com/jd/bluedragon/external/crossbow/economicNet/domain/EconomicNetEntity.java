package com.jd.bluedragon.external.crossbow.economicNet.domain;

import java.util.List;

/**
 * <p>
 *     经济网实体请求对象 外层
 *
 * @author wuzuxiang
 * @since 2020/1/16
 **/
public class EconomicNetEntity<T> {

    /**
     * 合作者ID
     */
    private String partnerId;

    /**
     * 签名
     */
    private String sign;

    /**
     * 数据集合
     */
    private List<T> data;

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
