package com.jd.bluedragon.distribution.print.service;


public interface TemplateSelectService {
    /**
     * 根据模板名称和站点编码获取实际模板名称
     * @param templateName
     * @param siteCode
     * @return
     */
    String getMatchTemplate(String templateName, Integer siteCode);
}
