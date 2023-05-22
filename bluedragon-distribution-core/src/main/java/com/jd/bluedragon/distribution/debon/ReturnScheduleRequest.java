package com.jd.bluedragon.distribution.debon;

import com.jd.bluedragon.distribution.open.entity.OperatorInfo;
import com.jd.bluedragon.distribution.open.entity.RequestProfile;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/19 18:04
 * @Description:
 */
public class ReturnScheduleRequest implements Serializable {

    /**
     * 数据调用信息
     */
    private RequestProfile requestProfile;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    /**
     * 运单号
     */
    private String waybillCode;

    public RequestProfile getRequestProfile() {
        return requestProfile;
    }

    public void setRequestProfile(RequestProfile requestProfile) {
        this.requestProfile = requestProfile;
    }

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public void setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
