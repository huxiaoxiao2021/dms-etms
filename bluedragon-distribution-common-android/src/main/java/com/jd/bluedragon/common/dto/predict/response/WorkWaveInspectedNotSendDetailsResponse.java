package com.jd.bluedragon.common.dto.predict.response;

import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanPackage;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanWaybill;

import java.io.Serializable;
import java.util.List;

public class WorkWaveInspectedNotSendDetailsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private Integer pageSize;

    public List<SendVehicleToScanWaybill> getWaybillCodes() {
        return waybillCodes;
    }

    public void setWaybillCodes(List<SendVehicleToScanWaybill> waybillCodes) {
        this.waybillCodes = waybillCodes;
    }

    public List<SendVehicleToScanPackage> getPackageCodes() {
        return packageCodes;
    }

    public void setPackageCodes(List<SendVehicleToScanPackage> packageCodes) {
        this.packageCodes = packageCodes;
    }

    private List<SendVehicleToScanWaybill> waybillCodes;
    private List<SendVehicleToScanPackage> packageCodes;

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

}
