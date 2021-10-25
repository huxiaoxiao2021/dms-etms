package com.jd.bluedragon.distribution.print.waybill.handler;

import com.google.common.collect.ImmutableList;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.*;
import com.jd.bluedragon.distribution.print.service.TemplateSelectService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.sdk.modules.configCenter.GuaranteeConfigApi;
import com.jd.bluedragon.sdk.modules.quarantine.dto.BaseResult;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
import java.util.Map;

import static com.jd.bluedragon.sdk.modules.quarantine.dto.BaseResult.SUCCESS_CODE;
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

    @Autowired
    private GuaranteeConfigApi guaranteeConfigApi;


	@Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.TemplateSelectorWaybillHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
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
        if (context == null || context.getBigWaybillDto() == null || context.getBigWaybillDto().getWaybill() == null){
            return;
        }
        String waybillCode = context.getWaybill().getWaybillCode();
        List<String> barcodes = ImmutableList.of(waybillCode);
        CallerInfo info = Profiler.registerInfo("DMS.BASE.TemplateSelectorWaybillHandler.setMask", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            BaseResult<Map<String, String>> baseResult = guaranteeConfigApi.getBatchGuaranteeFlagByBarcode(barcodes);
            if(baseResult != null ){
                if(SUCCESS_CODE == baseResult.getStatusCode() && MapUtils.isNotEmpty(baseResult.getData()) &&
                        StringUtils.isNotBlank(baseResult.getData().get(waybillCode))){
                    context.getBasePrintWaybill().setTransportModeFlag(baseResult.getData().get(waybillCode));
                }else if(SUCCESS_CODE != baseResult.getStatusCode()){
                    log.error("单号:{}获取大促配置时失败返回编码:{}消息:{}", waybillCode, baseResult.getStatusCode(),
                            baseResult.getStatusMessage());
                }
            }
        }catch (Exception e){
            log.error("获取大促配置时异常",e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }



    }
}
