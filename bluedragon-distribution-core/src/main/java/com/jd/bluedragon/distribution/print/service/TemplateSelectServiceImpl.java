package com.jd.bluedragon.distribution.print.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.domain.PackageTemplate;
import com.jd.bluedragon.distribution.api.domain.TemporaryPackageTemplate;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;


@Service("templateSelectService")
public class TemplateSelectServiceImpl implements TemplateSelectService {
    private final Logger logger = Logger.getLogger(TemplateSelectServiceImpl.class);

    @Autowired
    SysConfigService sysConfigService;

    /**
     * 获取站点对应的模板名
     * 背景：在有需求要求变更模板时，需要在线上的模板和新模板之间切换，为避免问题，提供一个按分拣中心切换的功能
     * 待测试通过后，将新模板设置成正式模板
     * 查sysConfig表，key值是业务对应的模板名，content是临时模板（新模板）的信息
     * 验证当前操作的分拣中心是否在测试模板的分拣中心列表里，如果是返回对应的测试模板
     *
     * @param templateName
     * @param siteCode
     * @return
     */
    @Cache(key = "TemplateSelectServiceImpl.getMatchTemplate@args0@args1", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = false)
    public String getMatchTemplate(String templateName, Integer siteCode) {
        String matchTemplateName = null;

        //1.从sysConfig表中查出业务模板对应的配置
        List<SysConfig> sysConfigs = sysConfigService.getListByConfigName(templateName);
        if (sysConfigs != null && !sysConfigs.isEmpty()) {
            String content = sysConfigs.get(0).getConfigContent();
            if (StringUtils.isNotBlank(content)) {
                //反序列化成包裹标签模板对象PackageTemplate
                PackageTemplate packageTemplate = JsonHelper.fromJson(content, PackageTemplate.class);
                String releaseTemplateName = packageTemplate.getReleaseTemplateName();
                //如果没有测试模板的信息则返回正式模板
                if (packageTemplate.getTemporaryTemplate() == null || packageTemplate.getTemporaryTemplate().size() < 1) {
                    matchTemplateName = releaseTemplateName;
                } else {
                    //能够查到测试模板的信息，则循环测试模板，获取匹配的模板
                    List<TemporaryPackageTemplate> temporaryTemplateList = packageTemplate.getTemporaryTemplate();
                    for (TemporaryPackageTemplate template : temporaryTemplateList) {
                        if (template.getSiteCodeList() != null && template.getSiteCodeList().size() > 0 &&
                                template.getSiteCodeList().contains(siteCode)) {
                            //如果当前测试模板的适用分拣中心列表中包含当前分拣中心，返回该测试模板
                            return template.getTemporaryTemplateName();
                        }
                    }
                    matchTemplateName = releaseTemplateName;
                }
            }
        }

        logger.info("包裹标签模板选择，根据业务模板:" + templateName + ",站点ID:" + siteCode + "获取到的模板名称为:" + matchTemplateName);
        return matchTemplateName;
    }
}