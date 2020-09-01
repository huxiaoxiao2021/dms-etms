package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;

/**
 * 面单参数获取接口
 *
 * @author: hujiping
 * @date: 2020/8/31 19:36
 */
public interface PaperSheetParamGainedService {

    /**
     * 获取集包地
     * @param context
     * @return
     */
    String getMixedSiteName(WaybillPrintContext context);

}
