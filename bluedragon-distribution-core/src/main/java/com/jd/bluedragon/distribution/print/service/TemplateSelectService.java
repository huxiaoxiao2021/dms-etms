package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;

public interface TemplateSelectService {
    /**
     * 模板选择
     * @param context
     */
    String handle(final WaybillPrintContext context);
    /**
     * 根据模板名称和站点编码获取实际模板名称
     * @param templateName
     * @param siteCode
     * @return
     */
    String getMatchTemplate(String templateName, Integer siteCode);
}
