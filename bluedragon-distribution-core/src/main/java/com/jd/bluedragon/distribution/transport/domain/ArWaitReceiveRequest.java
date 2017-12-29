package com.jd.bluedragon.distribution.transport.domain;

import com.jd.ql.dms.common.domain.JdRequest;

/**
 * 待提货查询请求参数
 * Created by xumei3 on 2017/12/29.
 */
public class ArWaitReceiveRequest extends JdRequest {
    /** 始发城市id **/
    public Integer startCityId;

    /** 目的城市id **/
    public Integer endCityid;

    /** 航空/铁路单号 **/
    public String orderNo ;

    /** 运力名称 **/
    public String transportName ;


    public Integer getEndCityid() {
        return endCityid;
    }

    public void setEndCityid(Integer endCityid) {
        this.endCityid = endCityid;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getStartCityId() {
        return startCityId;
    }

    public void setStartCityId(Integer startCityId) {
        this.startCityId = startCityId;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }
}
