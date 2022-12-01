package com.jd.bluedragon.common.dto.predict.response;

import com.jd.bluedragon.common.dto.predict.model.InspectedNotSendBarCode;

import java.io.Serializable;
import java.util.List;

public class WorkWaveInspectedNotSendDetailsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private Integer pageSize;
    private List<InspectedNotSendBarCode> waybillCodes;
    private List<InspectedNotSendBarCode> packageCodes;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<InspectedNotSendBarCode> getWaybillCodes() {
        return waybillCodes;
    }

    public void setWaybillCodes(List<InspectedNotSendBarCode> waybillCodes) {
        this.waybillCodes = waybillCodes;
    }

    public List<InspectedNotSendBarCode> getPackageCodes() {
        return packageCodes;
    }

    public void setPackageCodes(List<InspectedNotSendBarCode> packageCodes) {
        this.packageCodes = packageCodes;
    }
}
