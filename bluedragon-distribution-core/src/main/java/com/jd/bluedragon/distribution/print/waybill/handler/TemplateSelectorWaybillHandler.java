package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.domain.DmsPaperSize;
import com.jd.bluedragon.distribution.print.domain.TemplateGroupEnum;
import com.jd.bluedragon.distribution.print.service.TemplateSelectService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.dms.utils.BusinessUtil;
@Service
public class TemplateSelectorWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(TemplateSelectorWaybillHandler.class);
    
    /**B网专用面单 **/
    private static final String TEMPlATE_NAME_B2B_MAIN = "dms-b2b-new";
    /** B网冷链面单 **/
    private static final String TEMPlATE_NAME_B2B_COLD = "dms-b2b-m";
    /** TC面单 **/
    private static final String TEMPlATE_NAME_TC = "dms-b2b-m";
    /** C网统一面单-10*11 **/
    private static final String TEMPlATE_NAME_C_MAIN = "dms-unite-m";
    /** C网统一面单-10*10 **/
    private static final String TEMPlATE_NAME_C1010_MAIN = "dms-unite1010-m";
    /** 一号店面单 -10*11**/
    private static final String TEMPlATE_NAME_C_BUSINESS = "dms-unite-business-m";
    /** 一号店面单 10*10 **/
    private static final String TEMPlATE_NAME_C1010_BUSINESS = "dms-unite1010-business-m";
    /** 招商银行面单**/
    private static final String TEMPlATE_NAME_C_CMBC = "dms-nopaperyhd-m";

    /** 10*5的小包裹标签 **/
    private static final String TEMPLATE_NAME_10_5 = "dms-haspaper15-m";
	
	@Autowired
	@Qualifier("templateSelectService")
	private TemplateSelectService templateSelectService;
	
    @Autowired
    private SiteService siteService;

	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("标签打印-计算包裹标签模板及版本");
        String templateName = context.getRequest().getTemplateName();
        /**
         * 标识是否需求匹配模板
         */
        boolean needMatchTemplate = StringUtils.isBlank(templateName);
        Integer siteCode = context.getRequest().getSiteCode();
        String waybillSign = context.getWaybill().getWaybillSign();
        String paperSizeCode = context.getRequest().getPaperSizeCode();
        BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
        if(BusinessUtil.isTc(waybillSign)){
        	basePrintWaybill.setTemplateGroupCode(TemplateGroupEnum.TEMPLATE_GROUP_CODE_TC);
        }else if(BusinessUtil.isB2b(waybillSign)){
        	basePrintWaybill.setTemplateGroupCode(TemplateGroupEnum.TEMPLATE_GROUP_CODE_B);
        }else{
        	basePrintWaybill.setTemplateGroupCode(TemplateGroupEnum.TEMPLATE_GROUP_CODE_C);
        }
        //只有无纸化标识为false，才返回小标签
        if(DmsPaperSize.PAPER_SIZE_CODE_1005.equals(paperSizeCode)){
            templateName = TEMPLATE_NAME_10_5;
        }else{
            if (needMatchTemplate) {
                if (TemplateGroupEnum.TEMPLATE_GROUP_CODE_TC.equals(basePrintWaybill.getTemplateGroupCode())) {
                    //TC模板
                    templateName = TEMPlATE_NAME_TC;
                }else if (TemplateGroupEnum.TEMPLATE_GROUP_CODE_B.equals(basePrintWaybill.getTemplateGroupCode())) {
                    if(BusinessUtil.isSignChar(waybillSign, 54, '2')){
                        //冷链模板
                        templateName = TEMPlATE_NAME_B2B_COLD;
                    }else {
                        templateName = TEMPlATE_NAME_B2B_MAIN;
                    }
                } else {
                    //C网面单
                    //一号店模板
                    if (Constants.BUSINESS_ALIAS_YHD.equals(context.getBasePrintWaybill().getDmsBusiAlias())) {
                        templateName = TEMPlATE_NAME_C_BUSINESS;
                        //10*10模板
                        if(DmsPaperSize.PAPER_SIZE_CODE_1010.equals(paperSizeCode)){
                        	templateName = TEMPlATE_NAME_C1010_BUSINESS;
                        }
                    } else if (Constants.BUSINESS_ALIAS_CMBC.equals(context.getBasePrintWaybill().getDmsBusiAlias())) {
                        //招商银行使用老模板
                        templateName = TEMPlATE_NAME_C_CMBC;
                    } else {
                        //C网统一模板
                        templateName = TEMPlATE_NAME_C_MAIN;
                        //10*10模板
                        if(DmsPaperSize.PAPER_SIZE_CODE_1010.equals(paperSizeCode)){
                        	templateName = TEMPlATE_NAME_C1010_MAIN;
                        }
                    }
                }
            }
        }
        //设置模板纸张大小编码
        basePrintWaybill.setTemplatePaperSizeCode(paperSizeCode);
        //设置启用新模板标识--默认为启用
        basePrintWaybill.setUseNewTemplate(Boolean.TRUE);
        //查询在黑名单的分拣中心，设置为false
        if(siteService.getSiteCodesFromSysConfig(SysConfigService.SYS_CONFIG_NAME_DMS_SITE_CODES_NONUSE_NEW_TEMPLATE)
                .contains(context.getRequest().getDmsSiteCode())){
            basePrintWaybill.setUseNewTemplate(Boolean.FALSE);
        }
        //得到业务模板
        //根据key查config
        if (needMatchTemplate && siteCode != null) {
            String temporaryTemplateName = templateSelectService.getMatchTemplate(templateName, siteCode);
            if (StringUtils.isNotBlank(temporaryTemplateName)) {
                templateName = temporaryTemplateName;
            }
        }
        basePrintWaybill.setTemplateName(templateName);
		return context.getResult();
	}
}
