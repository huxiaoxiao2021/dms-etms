package com.jd.bluedragon.core.base;

import com.jd.b2b.wt.assemble.sdk.req.HandoverBillPrintReq;
import com.jd.b2b.wt.assemble.sdk.resp.HandoverBillResp;
import com.jd.b2b.wt.assemble.sdk.resp.HandoverDetailResp;

import java.util.List;

/**
 * @author lijie
 * @date 2019/8/22 16:53
 */
public interface HandoverBillPrintManager {
    /**
     * 查询交接单打印明细
     * @param handoverBillPrintReq
     * @return
     */
    HandoverBillResp searchHandoverDetail(HandoverBillPrintReq handoverBillPrintReq);

    /**
     * 查询交接单能否打印
     */
    Boolean searchHandoverisCanPrint(HandoverBillPrintReq handoverBillPrintReq);

    /**
     *打印交接单
     */
    List<HandoverDetailResp> dismantlePrint(HandoverBillPrintReq handoverBillPrint);
}
