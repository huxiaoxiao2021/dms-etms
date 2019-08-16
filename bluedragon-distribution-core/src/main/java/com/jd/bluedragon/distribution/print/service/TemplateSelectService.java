package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.LabelTemplate;


public interface TemplateSelectService {
    /**
     * 根据模板标识和站点编码获取实际模板信息
     * @param templateKey
     * @param siteCode
     * @return
     */
    LabelTemplate getMatchLabelTemplate(String templateKey, Integer siteCode);
}
