package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CancelSendTaskResp implements Serializable {

    private static final long serialVersionUID = 3813878446640603317L;

    /**
     * 取消的运单详情集合
     */
    private List<CancelSendWaybillDto> cancelWaybillList;

    /**
     * 取消的容器维度编号 按包裹 就是包裹号,按运单 就是运单号 ,按箱就是箱号，按板就是板号
     */
    private String cancelCode;

    public String getCancelCode() {
        return cancelCode;
    }

    public void setCancelCode(String cancelCode) {
        this.cancelCode = cancelCode;
    }

    public List<CancelSendWaybillDto> getCancelWaybillList() {
        return cancelWaybillList;
    }

    public void setCancelWaybillList(List<CancelSendWaybillDto> cancelWaybillList) {
        this.cancelWaybillList = cancelWaybillList;
    }
}
