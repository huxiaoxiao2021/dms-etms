package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.api.domain.PackageTemplate;
import com.jd.bluedragon.distribution.api.domain.TemporaryPackageTemplate;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.base.service.UserService;
import com.jd.bluedragon.distribution.print.domain.LabelTemplate;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("templateSelectService")
public class TemplateSelectServiceImpl implements TemplateSelectService {
    private final Logger log = LoggerFactory.getLogger(TemplateSelectServiceImpl.class);

    @Autowired
    SysConfigService sysConfigService;
    @Autowired
    private UserService userService;

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
    @Cache(key = "TemplateSelectServiceImpl.getMatchLabelTemplate@args0@args1", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = false)
    public LabelTemplate getMatchLabelTemplate(String templateKey, Integer siteCode) {
    	LabelTemplate matchedTemplate = new LabelTemplate();
    	boolean matched = false;
        //1.从sysConfig表中查出业务模板对应的配置
        List<SysConfig> sysConfigs = sysConfigService.getListByConfigName(templateKey);
        if (sysConfigs != null && !sysConfigs.isEmpty()) {
            String content = sysConfigs.get(0).getConfigContent();
            if (StringUtils.isNotBlank(content)) {
                //反序列化成包裹标签模板对象PackageTemplate
                PackageTemplate packageTemplate = JsonHelper.fromJson(content, PackageTemplate.class);
                //如果没有测试模板的信息则返回正式模板
                if (packageTemplate !=null){
                	if(siteCode != null
                		&& packageTemplate.getTemporaryTemplate() != null 
                		&& !packageTemplate.getTemporaryTemplate().isEmpty()) {
	                    //能够查到测试模板的信息，则循环测试模板，获取匹配的模板
	                    List<TemporaryPackageTemplate> temporaryTemplateList = packageTemplate.getTemporaryTemplate();
	                    for (TemporaryPackageTemplate template : temporaryTemplateList) {
	                    	//判断是否限制运行环境
	                    	boolean runningModeEnable = Boolean.TRUE.equals(template.getAllRunningModeEnable());
	                        if(!runningModeEnable 
	                        		&& template.getRunningModeList() != null 
	                        		&& template.getRunningModeList().size() > 0
	                        		&& template.getRunningModeList().contains(userService.getServerRunningMode())){
	                        	runningModeEnable = true;
	                        }
	                    	if (runningModeEnable
	                    			&& template.getSiteCodeList() != null 
	                    			&& template.getSiteCodeList().size() > 0 
									&& template.getSiteCodeList().contains(siteCode)) {
	                            //如果当前测试模板的适用分拣中心列表中包含当前分拣中心，返回该测试模板
	                            matchedTemplate.setTemplateName(template.getTemporaryTemplateName());
	                            matchedTemplate.setTemplateVersion(template.getTemporaryTemplateVersion());
	                            matched = true;
	                        }
	                    }
                	}
                	if(!matched){
                        matchedTemplate.setTemplateName(packageTemplate.getReleaseTemplateName());
                        matchedTemplate.setTemplateVersion(packageTemplate.getReleaseTemplateVersion());
                        matched = true;
                	}
                }
            }
        }
        if(!matched){
        	matchedTemplate.setTemplateName(templateKey);
        }
        log.info("包裹标签模板选择，根据业务模板:{},站点ID:{}获取到的模板名称为:{}",templateKey,siteCode, JsonHelper.toJson(matchedTemplate));
        return matchedTemplate;
    }
}