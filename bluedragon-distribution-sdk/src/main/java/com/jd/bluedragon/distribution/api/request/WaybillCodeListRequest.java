package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.List;

public class WaybillCodeListRequest extends JdRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号列表
     */
    private List<String> waybillCodeList;

    public List<String> getWaybillCodeList() {
        return waybillCodeList;
    }

    public void setWaybillCodeList(List<String> waybillCodeList) {
        this.waybillCodeList = waybillCodeList;
    }
}
