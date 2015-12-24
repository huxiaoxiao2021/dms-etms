package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

/**
 * Created by wangtingwei on 2015/12/23.
 */
public interface WaybillPrintService {
    /**
     * 获取打印运单信息
     * @param dmsCode           始发分拣中心编号
     * @param waybillCode       运单号
     * @param targetSiteCode    目的站点【大于0时，表示反调度站点】
     * @return
     */
    InvokeResult<PrintWaybill> getPrintWaybill(Integer dmsCode,String waybillCode,Integer targetSiteCode);
}
