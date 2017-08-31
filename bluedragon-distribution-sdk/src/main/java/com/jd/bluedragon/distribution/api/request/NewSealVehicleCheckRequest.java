package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * 封车前检查请求体，主要校验批次号和运力编码
 * Created by shipeilin on 2017/8/31.
 */
public class NewSealVehicleCheckRequest extends JdRequest {

    private static final long serialVersionUID = -4900034488418821321L;

    private String transportCode;    //运力编码

    private String batchCode;         //批次号

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }
}
