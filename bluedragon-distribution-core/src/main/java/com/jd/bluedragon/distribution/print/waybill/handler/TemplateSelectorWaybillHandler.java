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
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    private static final String TEMPlATE_NAME_C_MAIN = "dms-unite1011-new";
    /** C网统一面单-10*10 **/
    private static final String TEMPlATE_NAME_C1010_MAIN = "dms-unite1010-new";
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

    @Value("${begin.time.dubboEleven}")
    private Integer bginTimeOFDubboEleven;//开始时间
    @Value("${num.day.continued}")
    private Integer numDayContinued;//持续时间

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
        // 双十一1234项目使用四天后面删除此处代码
        if(isInDubboElevenTime() &&
                (templateName == this.TEMPlATE_NAME_C1010_MAIN || templateName == this.TEMPlATE_NAME_C_MAIN)){
            setMask(context);
        }
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
     * 打标
     * 双十一第一天是1，逐天加1
     * @param context
     */
    private void setMask(WaybillPrintContext context) {
        Long currentTime = System.currentTimeMillis()/1000;//当前时间
        Long beginTime = new Long(bginTimeOFDubboEleven);//双十一开始时间
        for(int i =0;i < this.numDayContinued;i++){
           if (beginTime <= currentTime && currentTime <= beginTime + 86400){
               context.getBasePrintWaybill().setTransportTypeText("" + (i+1));
               break;
           }
            beginTime += 86400;
        }

    }

    /**
     * 判断是否在双十一期间
     * 双十一期间的定义（开始时间 <= 当前时间 <=开始时间+持续天数）
     * 如果在双十一期间返回 true 否则返回 false
     * @return
     */
    private boolean isInDubboElevenTime() {
	    if (null == this.bginTimeOFDubboEleven || null == this.numDayContinued){
	        return Boolean.FALSE;
        }
	    Long currentTime = System.currentTimeMillis()/1000;//当前时间
	    Long bginTimeOFDubboElevenLong = new Long(bginTimeOFDubboEleven);//双十一开始时间
	    //当前时间戳
        if (currentTime >= bginTimeOFDubboElevenLong && currentTime <= bginTimeOFDubboElevenLong + this.numDayContinued * 86400){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
