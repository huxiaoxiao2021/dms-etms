package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;

public interface TemplateSelectService {
    /**
     * 模板选择
     * @param context
     */
    String handle(final WaybillPrintContext context);
}
