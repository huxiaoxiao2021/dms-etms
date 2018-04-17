package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.service.ComposeService;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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
	@Qualifier("specialSiteComposeService")
	private ComposeService specialSiteComposeService;
    @Autowired
    private BaseService baseService;
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
		return context.getResult();
	}
}
