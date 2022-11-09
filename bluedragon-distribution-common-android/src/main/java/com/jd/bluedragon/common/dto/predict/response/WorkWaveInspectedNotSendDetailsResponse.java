package com.jd.bluedragon.common.dto.predict.response;

import java.io.Serializable;
import java.util.List;

public class WorkWaveInspectedNotSendDetailsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private Integer pageSize;
    private List<String> waybillCodes;
    private List<String> packageCodes;

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

    public List<String> getWaybillCodes() {
        return waybillCodes;
    }

    public void setWaybillCodes(List<String> waybillCodes) {
        this.waybillCodes = waybillCodes;
    }

    public List<String> getPackageCodes() {
        return packageCodes;
    }

    public void setPackageCodes(List<String> packageCodes) {
        this.packageCodes = packageCodes;
    }
}
