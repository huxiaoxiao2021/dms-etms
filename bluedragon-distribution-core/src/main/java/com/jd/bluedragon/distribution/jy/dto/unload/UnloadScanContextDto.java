package com.jd.bluedragon.distribution.jy.dto.unload;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.etms.waybill.domain.Waybill;

import java.io.Serializable;

/**
 * 卸车扫描上下文内容
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-08-07 09:34:19 周一
 */
public class UnloadScanContextDto implements Serializable {

    private static final long serialVersionUID = 5418914501044409289L;

    /**
     * 原始提交入参
     */
    private UnloadScanRequest unloadScanRequest;

    /**
     * 运单数据
     */
    private Waybill waybill;

    public UnloadScanRequest getUnloadScanRequest() {
        return unloadScanRequest;
    }

    public void setUnloadScanRequest(UnloadScanRequest unloadScanRequest) {
        this.unloadScanRequest = unloadScanRequest;
    }

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
