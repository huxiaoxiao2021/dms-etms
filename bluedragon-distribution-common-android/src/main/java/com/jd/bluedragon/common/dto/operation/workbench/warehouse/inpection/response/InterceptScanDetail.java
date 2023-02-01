package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response;

import java.io.Serializable;
import java.util.List;

/**
 * 已扫拦截明细
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-10-09 14:19:52 周日
 */
public class InterceptScanDetail implements Serializable {

    private static final long serialVersionUID = 7487327661662752184L;
    /**
     * 已扫拦截数量
     */
    private Long interceptScanCount;

    /**
     * 包裹列表
     */
    private List<InspectionScanBarCode> barCodeList;

    public List<InspectionScanBarCode> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<InspectionScanBarCode> barCodeList) {
        this.barCodeList = barCodeList;
    }
}
