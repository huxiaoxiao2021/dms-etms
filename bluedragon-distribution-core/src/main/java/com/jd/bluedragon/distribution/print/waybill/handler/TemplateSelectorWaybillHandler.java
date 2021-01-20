package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.*;
import com.jd.bluedragon.distribution.print.service.TemplateSelectService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

@Service
public class TemplateSelectorWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Logger log = LoggerFactory.getLogger(TemplateSelectorWaybillHandler.class);
    
    /**B网统一面单 **/
    private static final String TEMPlATE_NAME_B2B_MAIN = "dms-b2b-unite";
    /**大件模板 **/
    private static final String TEMPlATE_NAME_DJ_JDB_MAIN = "dms-dj-jdb-m";
    /** TC面单 **/
    private static final String TEMPlATE_NAME_TC = "dms-b2b-m";
    /** C网统一面单-10*11 **/
    private static final String TEMPlATE_NAME_C_MAIN = "dms-unite1011-new-v1";
    /** C网统一面单-10*10 **/
    private static final String TEMPlATE_NAME_C1010_MAIN = "dms-unite1010-new-v1";
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

    @Value("${time.dubboEleven}")
    private String timeDubboEleven;//双十一期间需要达标的日期, 以逗号分隔
    @Value("${mask.dubboEleven}")
    private String maskDubboEleven;//每天对应的标签


	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		log.debug("标签打印-计算包裹标签模板及版本");
        String templateName = context.getRequest().getTemplateName();
        /**
         * 标识是否需求匹配模板
         */
        boolean needMatchTemplate = StringUtils.isBlank(templateName);
        Integer siteCode = context.getRequest().getSiteCode();
        Integer operateType = context.getRequest().getOperateType();
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
            	//冷链合伙人打印，指定为冷链模板
            	if(WaybillPrintOperateTypeEnum.COLD_CHAIN_PRINT.getType().equals(operateType)){
            		templateName = TEMPlATE_NAME_B2B_MAIN;
            	}else if(WaybillPrintOperateTypeEnum.PDF_DJ_JDB_PACKAGE_REPRINT.getType().equals(operateType)){
            	    //大件-京东帮打印，指定模板
                    templateName = TEMPlATE_NAME_DJ_JDB_MAIN;
                }else if(BusinessUtil.isBusinessNet(waybillSign)){
            	    //经济网模板
                    templateName = TEMPlATE_NAME_C1010_MAIN;
                }else if (TemplateGroupEnum.TEMPLATE_GROUP_CODE_TC.equals(basePrintWaybill.getTemplateGroupCode())) {
                    //TC模板
                    templateName = TEMPlATE_NAME_TC;
                }else if (TemplateGroupEnum.TEMPLATE_GROUP_CODE_B.equals(basePrintWaybill.getTemplateGroupCode())) {
                    //B网面单统一
                    templateName = TEMPlATE_NAME_B2B_MAIN;
                } else {
                    //C网面单
                    //一号店模板
                    if (Constants.BUSINESS_ALIAS_YHD.equals(context.getBasePrintWaybill().getDmsBusiAlias())) {
                        templateName = TEMPlATE_NAME_C_BUSINESS;
                        //10*10模板
                        if(DmsPaperSize.PAPER_SIZE_CODE_1010.equals(paperSizeCode)){
                        	templateName = TEMPlATE_NAME_C1010_BUSINESS;
                        }
                    } else if (Constants.BUSINESS_ALIAS_CMBC.equals(context.getBasePrintWaybill().getDmsBusiAlias())
                                && !BusinessUtil.isLetterExpress(waybillSign)) {
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
        // 先进先出打标
        this.setMask(context,templateName);

        //得到业务模板
        //根据key查config
        if (needMatchTemplate) {
        	LabelTemplate matchedTemplate = templateSelectService.getMatchLabelTemplate(templateName, siteCode);
            if (matchedTemplate != null && StringUtils.isNotBlank(matchedTemplate.getTemplateName())) {
                templateName = matchedTemplate.getTemplateName();
                if(matchedTemplate.getTemplateVersion() != null){
                	basePrintWaybill.setTemplateVersionStr(matchedTemplate.getTemplateVersion().toString());
                }
            }
        }
        basePrintWaybill.setTemplateName(templateName);
		return context.getResult();
	}

    /**
     * 先进先出打标
     * @param context
     */
    private void setMask(WaybillPrintContext context,String templateName) {
        if (! (TEMPlATE_NAME_C1010_MAIN.equals(templateName)  || TEMPlATE_NAME_C_MAIN.equals(templateName))){
            return;
        }
        if (context == null || context.getBigWaybillDto() == null || context.getBigWaybillDto().getWaybill() == null ||
                context.getBigWaybillDto().getWaybill().getRequireTime() == null){
            return;
        }
        String currentDate = DateHelper.formatDate(context.getBigWaybillDto().getWaybill().getRequireTime(),DateHelper.DATE_FORMAT_YYYYMMDD);
        List<String> dubboElevenTimes = Arrays.asList(this.timeDubboEleven.split(","));
        List<String> masks = Arrays.asList(this.maskDubboEleven.split(","));
        if (CollectionUtils.isEmpty(dubboElevenTimes) || CollectionUtils.isEmpty(masks) || !dubboElevenTimes.contains(currentDate)){
            return;
        }
        int index = dubboElevenTimes.indexOf(currentDate);
        String mask = index < masks.size() ? masks.get(index) : null;
        if (!StringUtils.isEmpty(mask)){
            context.getBasePrintWaybill().setTransportModeFlag(mask);
        }
    }
}
