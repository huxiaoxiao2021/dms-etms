package com.jd.bluedragon.distribution.print.service;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.PackageTemplate;
import com.jd.bluedragon.distribution.api.domain.TemporaryPackageTemplate;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("templateSelectService")
public class TemplateSelectServiceImpl implements TemplateSelectService {
    private final Logger logger = Logger.getLogger(TemplateSelectServiceImpl.class);

    @Autowired
    SysConfigService sysConfigService;

    /**B网专用面单 **/
    private static final String TEMPlATE_NAME_B2B_MAIN = "dms-b2b-m";
    /** B网营业厅面单 **/
    private static final String TEMPlATE_NAME_B2B_BUSINESS_HALL = "dms-b2b-new";
    /** B网冷链面单 **/
    private static final String TEMPlATE_NAME_B2B_COLD = "dms-b2b-m";
    /** TC面单 **/
    private static final String TEMPlATE_NAME_TC = "dms-b2b-m";
    /** C网专用面单 **/
    private static final String TEMPlATE_NAME_C_MAIN = "dms-unite-m";
    /** 一号店面单 **/
    private static final String TEMPlATE_NAME_C_BUSINESS = "dms-unite-business";
    /** 招商银行面单**/
    private static final String TEMPlATE_NAME_C_CMBC = "dms-nopaperyhd-m";

    /** 10*5的小包裹标签 **/
    private static final String TEMPLATE_NAME_10_5 = "dms-haspaper15-m";



    @Override
    public void handle(WaybillPrintContext context) {
        String templateName = context.getRequest().getTemplateName();
        Integer siteCode = context.getRequest().getSiteCode();
        String waybillSign = context.getWaybill().getWaybillSign();
        boolean noPaperFlag = context.getRequest().getNopaperFlg();
        if(noPaperFlag){
            if (StringUtils.isBlank(templateName)) {
                if (BusinessUtil.isSignInChars(waybillSign, 40, '1', '2', '3')) {
                    //B网模板
                    if (!BusinessUtil.isSignChar(waybillSign, 54, '2')) {
                        if (BusinessUtil.isSignChar(waybillSign, 62, '1')) {
                            //B网营业厅面单
                            templateName = TEMPlATE_NAME_B2B_BUSINESS_HALL;
                        } else if (BusinessUtil.isSignChar(waybillSign, 62, '0')) {
                            //B网专用面单
                            templateName = TEMPlATE_NAME_B2B_MAIN;
                        }
                    } else {
                        //冷链模板
                        templateName = TEMPlATE_NAME_B2B_COLD;
                    }
                } else if (BusinessUtil.isSignChar(waybillSign, 89, '1')) {
                    //TC模板
                    templateName = TEMPlATE_NAME_TC;
                } else {
                    //C网面单
                    //一号店模板
                    if (Constants.BUSINESS_ALIAS_YHD.equals(context.getResponse().getDmsBusiAlias())) {
                        templateName = TEMPlATE_NAME_C_BUSINESS;
                    } else if (Constants.BUSINESS_ALIAS_CMBC.equals(context.getResponse().getDmsBusiAlias())) {
                        //招商银行使用老模板
                        templateName = TEMPlATE_NAME_C_CMBC;
                    } else {
                        //C网统一模板
                        templateName = TEMPlATE_NAME_C_MAIN;
                    }

                }
            }
        }else {
            //小标签
            templateName = TEMPLATE_NAME_10_5;
        }


        //得到业务模板
        //根据key查config
        if (siteCode != null) {
            String temporaryTemplateName = getMatchTemplate(templateName, siteCode);
            if (StringUtils.isNotBlank(temporaryTemplateName)) {
                templateName = temporaryTemplateName;
            }
        }

        context.getResponse().setTemplateName(templateName);
    }

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
    @Cache(key = "TemplateSelectServiceImpl.getMatchTemplate@args0@args1", memoryEnable = false, memoryExpiredTime = 10 * 60 * 1000, redisEnable = true)
    private String getMatchTemplate(String templateName, Integer siteCode) {
        String matchTemplateName = null;

        //1.从sysConfig表中查出业务模板对应的配置
        List<SysConfig> sysConfigs = sysConfigService.getListByConfigName(templateName);
        if (sysConfigs != null && !sysConfigs.isEmpty()) {
            String content = sysConfigs.get(0).getConfigContent();
            if (StringUtils.isNotBlank(content)) {
                //反序列化成包裹标签模板对象PackageTemplate
                PackageTemplate packageTemplate = JSON.parseObject(content, PackageTemplate.class);
                String releaseTemplateName = packageTemplate.getReleaseTemplateName();
                //如果没有测试模板的信息则返回正式模板
                if (packageTemplate.getTemporaryTemplate() == null || packageTemplate.getTemporaryTemplate().size() < 1) {
                    matchTemplateName = releaseTemplateName;
                } else {
                    //能够查到测试模板的信息，则循环测试模板，获取匹配的模板
                    List<TemporaryPackageTemplate> temporaryTemplateList = packageTemplate.getTemporaryTemplate();
                    for (TemporaryPackageTemplate template : temporaryTemplateList) {
                        if (template.getSiteCodeList() == null || template.getSiteCodeList().size() < 1) {
                            continue;
                        }
                        //如果当前测试模板的适用分拣中心列表中包含当前分拣中心，返回该测试模板
                        if (template.getSiteCodeList().contains(siteCode)) {
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