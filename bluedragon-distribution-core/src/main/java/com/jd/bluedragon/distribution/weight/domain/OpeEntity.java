package com.jd.bluedragon.distribution.weight.domain;

import java.util.List;

/**
 * Created by yanghongqiang on 2015/5/10.
 */
public class OpeEntity {

        /// <summary>
        /// 运单号
        /// </summary>
        private String waybillCode;
        /// <summary>
        /// 回传类型	1、分拣中心称重、测量
        /// </summary>
        private Integer opeType;
        /// <summary>
        /// 存放1个到多个包裹称量明细
        /// </summary>
        private List<OpeObject> opeDetails;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getOpeType() {
        return opeType;
    }

    public void setOpeType(Integer opeType) {
        this.opeType = opeType;
    }

    public List<OpeObject> getOpeDetails() {
        return opeDetails;
    }

    public void setOpeDetails(List<OpeObject> opeDetails) {
        this.opeDetails = opeDetails;
    }
}
