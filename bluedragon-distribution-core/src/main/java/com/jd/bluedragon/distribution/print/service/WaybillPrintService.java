package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;

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
    InvokeResult<WaybillPrintResponse> getPrintWaybill(Integer dmsCode,String waybillCode,Integer targetSiteCode);
    /**
     * 处理打标信息
     * @param signStr 打标字段
     * @param target 目标对象
     * @param signConfigName 打标对应的字典表配置名称
     */
    void dealSignTexts(String signStr,BasePrintWaybill target,String signConfigName);
}
