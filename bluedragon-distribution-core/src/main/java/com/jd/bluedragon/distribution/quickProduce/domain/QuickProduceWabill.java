package com.jd.bluedragon.distribution.quickProduce.domain;

import com.jd.bluedragon.common.domain.Waybill;

/**
 * Created by yanghongqiang on 2015/9/7.
 * 三方快递快生接口中对象（数据来源：订单中间键，台帐，外单）
 */
public class QuickProduceWabill {
    private static final long serialVersionUID = 6063221964438923598L;

    private Waybill waybill;

    private JoinDetail joinDetail;

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public JoinDetail getJoinDetail() {
        return joinDetail;
    }

    public void setJoinDetail(JoinDetail joinDetail) {
        this.joinDetail = joinDetail;
    }
}
