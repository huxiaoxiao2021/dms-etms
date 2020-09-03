package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bk.common.util.string.StringUtils;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ldop.center.api.print.dto.WaybillPrintDataDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @ClassName: SpecialSiteWaybillHandler
 * @Description: 包裹标签打印-特殊字段处理
 * @author: wuyoude
 * @date: 2018年2月5日 下午5:36:29
 *
 */
@Service
public class SpecialTextWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Logger log = LoggerFactory.getLogger(SpecialTextWaybillHandler.class);
    /**
     * 预分拣站点编码-未定位门店(0)
     */
    private static Integer PREPARE_SITE_CODE_NOTHING = 0;
    /**
     * 预分拣站点编码-未定位门店(0)
     */
    private static String PREPARE_SITE_NAME_NOTHING = "未定位门店";
    /**
     * 预分拣站点编码-超区分界线(-100)
     */
    private static Integer PREPARE_SITE_CODE_OVER_LINE = -100;
    /**
     * 预分拣站点编码-超区(-2)
     */
    private static Integer PREPARE_SITE_CODE_OVER_AREA = -2;
    /**
     * 预分拣站点名称-超区(-2)
     */
    private static String PREPARE_SITE_NAME_OVER_AREA = "超区";
    /**
     * 预分拣站点编码-EMS全国直发(999999999)
     */
    private static Integer PREPARE_SITE_CODE_EMS_DIRECT = 999999999;
    /**
     * 预分拣站点名称-EMS全国直发(999999999)
     */
    private static String PREPARE_SITE_NAME_EMS_DIRECT = "EMS全国直发";

    @Autowired
    private BaseService baseService;

    @Autowired
    private LDOPManager ldopManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		log.debug("包裹标签打印-站点名称显示处理");
		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
		WaybillPrintResponse printInfo = context.getResponse();
		//预分拣站点
		Integer prepareSiteCode = printInfo.getPrepareSiteCode();
		//指定的站点>0则，以指定的站点为预分拣站点
        if(NumberHelper.gt0(targetSiteCode)){
        	printInfo.setPrepareSiteCode(targetSiteCode);
        	prepareSiteCode = targetSiteCode;
        }
        //EMS全国直发
        if(PREPARE_SITE_CODE_EMS_DIRECT.equals(prepareSiteCode)){
        	printInfo.setPrepareSiteName(PREPARE_SITE_NAME_EMS_DIRECT);
        	printInfo.setPrintSiteName(PREPARE_SITE_NAME_EMS_DIRECT);
        } else if(printInfo.getPrepareSiteCode()==null
                || (printInfo.getPrepareSiteCode()<=PREPARE_SITE_CODE_NOTHING
                    && printInfo.getPrepareSiteCode() > PREPARE_SITE_CODE_OVER_LINE)){
        	// 空或者-100~0  超区
        	printInfo.setPrepareSiteCode(PREPARE_SITE_CODE_NOTHING);
        	printInfo.setPrepareSiteName(PREPARE_SITE_NAME_NOTHING);
        	printInfo.setPrintSiteName(PREPARE_SITE_NAME_NOTHING);
            log.warn(" 没有获取预分拣站点(未定位门店):{}", printInfo.getWaybillCode());
            // -2或者<-100 超区
        }else if(PREPARE_SITE_CODE_OVER_AREA.equals(printInfo.getPrepareSiteCode())
                || printInfo.getPrepareSiteCode().intValue() < PREPARE_SITE_CODE_OVER_LINE.intValue()){
//        	printInfo.setPrepareSiteCode(printInfo.getPrepareSiteCode());
        	printInfo.setPrepareSiteName(PREPARE_SITE_NAME_OVER_AREA);
        	printInfo.setPrintSiteName(PREPARE_SITE_NAME_OVER_AREA);
            log.warn(" 没有获取预分拣站点(细分超区):{}-{}", printInfo.getPrepareSiteCode() , printInfo.getWaybillCode());
        }
        //设置预分拣站点名称
        if(null == printInfo.getPrepareSiteName() && null != prepareSiteCode){
            BaseStaffSiteOrgDto site= baseService.getSiteBySiteID(prepareSiteCode);
            if(null!=site){
                printInfo.setPrepareSiteName(site.getSiteName());
                printInfo.setPrintSiteName(site.getSiteName());
            }
        }

        //新通路订单预分拣站点替换为代配站点（运单中的backupSiteId字段）
        if(BusinessHelper.isNewPathWay(printInfo.getSendPay()) && printInfo.getBackupSiteId() != null && printInfo.getBackupSiteId() > 0){
            printInfo.setPrepareSiteCode(printInfo.getBackupSiteId());
            printInfo.setPrepareSiteName(printInfo.getBackupSiteName());
            printInfo.setPrintSiteName(printInfo.getBackupSiteName());
        }

        /** 调用外单接口获取始发站点、目的站点和路由信息 **/
        //获取waybillSign
        String waybillSign = printInfo.getWaybillSign();
        if(StringHelper.isEmpty(waybillSign)){
            log.warn("SpecialTextWaybillHandler-->获取waybillSign为空,无法判断是否是同城单日达面单.");
        } else {
            //根据waybill_sign判断同城当日达 第55位等于0 （表示非生鲜专送）且第16位等于1 （表示当日达）且第31位等于2 （表示同城配送）且第63位等于1 （中心站网络）
            if(BusinessHelper.isSameCityOneDay(waybillSign)){
                //设置始发站点及始发路由，并将笼车号设为空字符串
                printInfo.setOriginalDmsCode(null);
                printInfo.setOriginalDmsName("");
                printInfo.setOriginalCrossCode("");
                printInfo.setOriginalTabletrolley("");
                printInfo.setOriginalTabletrolleyCode("");

                //设置目的站点及目的路由，并将笼车号设为空字符串
                printInfo.setPurposefulDmsCode(null);
                printInfo.setPurposefulDmsName("");
                printInfo.setDestinationDmsName("");
                printInfo.setPurposefulCrossCode("");
                printInfo.setDestinationCrossCode("");
                printInfo.setPurposefulTableTrolley("");
                printInfo.setDestinationTabletrolleyCode("");

//                设置模板
                printInfo.setTemplateName("dms-vonebody-s1");
                String busiCode = context.getBusiCode();
                String waybillCode = printInfo.getWaybillCode();
                List<WaybillPrintDataDTO> waybillPrintData = ldopManager.getPrintDataForCityOrder(busiCode,waybillCode);
                if(!waybillPrintData.isEmpty()){
                    WaybillPrintDataDTO print = waybillPrintData.get(0);
                    //设置始发站点及始发路由，并将笼车号设为空字符串
                    printInfo.setOriginalDmsCode(print.getStartCenterSiteId());
                    printInfo.setOriginalDmsName(print.getStartCenterSiteName());
                    printInfo.setOriginalCrossCode(print.getStartCenterSiteRouteCode());

                    //设置目的站点及目的路由，并将笼车号设为空字符串
                    printInfo.setPurposefulDmsCode(print.getEndCenterSiteId());
                    printInfo.setPurposefulDmsName(print.getEndCenterSiteName());
                    printInfo.setDestinationDmsName(print.getEndCenterSiteName());
                    printInfo.setPurposefulCrossCode(print.getEndCenterSiteRouteCode());
                    printInfo.setDestinationCrossCode(print.getEndCenterSiteRouteCode());
                }
            }
        }

        log.debug("包裹标签打印-特殊字符处理");
        WaybillPrintRequest request = context.getRequest();
        BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
        BigWaybillDto bigWaybillDto = context.getBigWaybillDto();
        String popularizeMatrixCode = getPopularizeMatrixCode(request,basePrintWaybill,bigWaybillDto);
        if(StringUtils.isNotBlank(popularizeMatrixCode)){
            printInfo.setPopularizeMatrixCode(popularizeMatrixCode);
        }
		return context.getResult();
	}

    /**
     * 获取面单二维码占位符字符串
     * @param request
     * @param basePrintWaybill
     * @param bigWaybillDto
     * @return
     */
    private String getPopularizeMatrixCode(WaybillPrintRequest request,BasePrintWaybill basePrintWaybill,
                                           BigWaybillDto bigWaybillDto) {
        /** 调用运单获取原单信息 */
        Waybill waybill = bigWaybillDto==null?null:bigWaybillDto.getWaybill();
        String waybillCode = waybill==null?null:waybill.getWaybillCode();
        String dmsBusiAlias = basePrintWaybill==null?null:basePrintWaybill.getDmsBusiAlias();
        try {
            //换单打印且银行用户面单二维码显示原单号
            if(WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT_TYPE.equals(request.getOperateType())
                    && Constants.BUSINESS_ALIAS_CMBC.equals(dmsBusiAlias)){
                BaseEntity<Waybill> baseEntity = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
                if(baseEntity != null && baseEntity.getResultCode() == Constants.RESULT_SUCCESS
                        && baseEntity.getData() != null
                        && StringUtils.isNotBlank(baseEntity.getData().getWaybillCode())){
                    //原单号
                    return baseEntity.getData().getWaybillCode();
                }
            }
        }catch (Exception e){
            log.error("根据运单号:{0},获取原单信息异常!",waybillCode);
        }
	    return null;
    }
}
