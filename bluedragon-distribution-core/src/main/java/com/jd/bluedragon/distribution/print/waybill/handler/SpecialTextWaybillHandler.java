package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ldop.center.api.print.dto.WaybillPrintDataDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

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
	private static final Log logger= LogFactory.getLog(SpecialTextWaybillHandler.class);
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
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("包裹标签打印-站点名称显示处理");
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
        } else if(printInfo.getPrepareSiteCode()==null
                || (printInfo.getPrepareSiteCode()<=PREPARE_SITE_CODE_NOTHING
                    && printInfo.getPrepareSiteCode() > PREPARE_SITE_CODE_OVER_LINE)){
        	// 空或者-100~0  超区
        	printInfo.setPrepareSiteCode(PREPARE_SITE_CODE_NOTHING);
        	printInfo.setPrepareSiteName(PREPARE_SITE_NAME_NOTHING);
            logger.warn(" 没有获取预分拣站点(未定位门店),"+printInfo.getWaybillCode());
            // -2或者<-100 超区
        }else if(PREPARE_SITE_CODE_OVER_AREA.equals(printInfo.getPrepareSiteCode())
                || printInfo.getPrepareSiteCode().intValue() < PREPARE_SITE_CODE_OVER_LINE.intValue()){
        	printInfo.setPrepareSiteCode(printInfo.getPrepareSiteCode());
        	printInfo.setPrepareSiteName(PREPARE_SITE_NAME_OVER_AREA);
            logger.warn(" 没有获取预分拣站点(细分超区)," + printInfo.getPrepareSiteCode() + ","+printInfo.getWaybillCode());
        }
        //设置预分拣站点名称
        if(null == printInfo.getPrepareSiteName() && null != prepareSiteCode){
            BaseStaffSiteOrgDto site= baseService.getSiteBySiteID(prepareSiteCode);
            if(null!=site){
                printInfo.setPrepareSiteName(site.getSiteName());
            }
        }

        /** 调用外单接口获取始发站点、目的站点和路由信息 **/
        //获取waybillSign
        String waybillSign = printInfo.getWaybillSign();
        if(StringHelper.isEmpty(printInfo.getWaybillSign())){
            logger.error("SpecialTextWaybillHandler-->获取waybillSign为空,无法判断是否是同城单日达面单.");
        } else {
            //根据waybill_sign判断同城当日达 第55位等于0 （表示非生鲜专送）且第16位等于1 （表示当日达）且第31位等于2 （表示同城配送）
            if(BusinessHelper.isSignChar(waybillSign,55,'0') &&
                    BusinessHelper.isSignChar(waybillSign,16,'1') &&
                    BusinessHelper.isSignChar(waybillSign,31,'2')){

                //设置始发站点及始发路由，并将笼车号设为空字符串
                printInfo.setOriginalDmsCode(null);
                printInfo.setOriginalDmsName("");
                printInfo.setOriginalCrossCode("");
                printInfo.setOriginalTabletrolley("");

                //设置目的站点及目的路由，并将笼车号设为空字符串
                printInfo.setPurposefulDmsCode(null);
                printInfo.setPurposefulDmsName("");
                printInfo.setPurposefulCrossCode("");
                printInfo.setPurposefulTableTrolley("");

                //设置模板
                printInfo.setTemplateName("dms-vonebody-s1");
                String busiCode = printInfo.getBusiCode();
                String waybillCode = printInfo.getWaybillCode();
                List<WaybillPrintDataDTO> waybillPrintData = ldopManager.getPrintDataForCityOrder(busiCode,waybillCode);
                if(!waybillPrintData.isEmpty()){
                    WaybillPrintDataDTO print = waybillPrintData.get(0);
                    //设置始发站点及始发路由，并将笼车号设为空字符串
                    printInfo.setOriginalDmsCode(print.getStartCenterSiteId());
                    printInfo.setOriginalDmsName(print.getStartCenterSiteName());
                    printInfo.setOriginalCrossCode(print.getStartCenterSiteRouteCode());
                    printInfo.setOriginalTabletrolley("");

                    //设置目的站点及目的路由，并将笼车号设为空字符串
                    printInfo.setPurposefulDmsCode(print.getEndCenterSiteId());
                    printInfo.setPurposefulDmsName(print.getEndCenterSiteName());
                    printInfo.setPurposefulCrossCode(print.getEndCenterSiteRouteCode());
                    printInfo.setPurposefulTableTrolley("");
                }
            }
        }
		return context.getResult();
	}
}
