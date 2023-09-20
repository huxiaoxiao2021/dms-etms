package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.dms.CancelWaybillJsfManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.jy.enums.SiteTypeLevel;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.dms.ver.domain.JsfResponse;
import com.jd.dms.ver.domain.WaybillCancelJsfResponse;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: InterceptSiteTypePrinrHandler
 * @Description: 站点类型拦截打印
 * @author: 陈亚国
 * @date: 2023年09月20日 上午9:18:31
 */
@Service("interceptSiteTypePrinrHandler")
public class InterceptSiteTypePrinrHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Logger log = LoggerFactory.getLogger(InterceptSiteTypePrinrHandler.class);

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;


	@Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.InterceptWaybillHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
	public JdResult<String> handle(WaybillPrintContext context) {
		log.info("站定类型打印拦截拦截信息处理");
        InterceptResult<String> result = context.getResult();
        //终端包裹补打功能限制
        if(interceptPackageReprint(context)){
            result.toFail(SortingResponse.PACKAGE_PRINT_BAN_CODE,SortingResponse.PACKAGE_PRINT_BAN_MESSAGE);
        }
		return result;
	}

    /**
     * 包裹补打拦截
     * @param context
     * @return
     */
    private boolean interceptPackageReprint(WaybillPrintContext context){
        log.info("interceptPackageReprint 操作类型-{}",context.getRequest().getOperateType());

        boolean terminalSitePackagePrintLimitSwitch = uccPropertyConfiguration.isTerminalSitePackagePrintLimitSwitch();
        if(!terminalSitePackagePrintLimitSwitch){
            log.warn("终端站点包裹补打功能拦截功能关闭!");
            return false;
        }

        if(WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType().equals(context.getRequest().getOperateType())){
            Integer siteCode = context.getRequest().getSiteCode();
            BaseSiteInfoDto baseSite = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
            log.info("包裹补打 interceptPackageReprint 站点信息-{}", JSON.toJSONString(baseSite));
            if(baseSite == null) {
                return false;
            }
            //判断一级站点类型是营业部、第三方
            if(!(SiteTypeLevel.SiteTypeOneLevelEnum.TERMINAL_SITE.getCode().equals(baseSite.getSiteType())
                    ||SiteTypeLevel.SiteTypeOneLevelEnum.THIRD_PARTY.getCode().equals(baseSite.getSiteType()))){
                return false;
            }
            //判断二级站点类型是 营业部、便民服务点
            if(SiteTypeLevel.SiteTypeTwoLevelEnum.TERMINAL_SITE.getCode().equals(baseSite.getSubType())
                    || SiteTypeLevel.SiteTypeTwoLevelEnum.CONVENIENT_SERVICE_POINT.getCode().equals(baseSite.getSubType())
                    || SiteTypeLevel.SiteTypeTwoLevelEnum.DEEP_COOPERATION_SELF_PICKUP_CABINETS.getCode().equals(baseSite.getSubType())){
                return true;
            }
            if(SiteTypeLevel.SiteTypeTwoLevelEnum.CAMPUS_JD_SCHOOL.getCode().equals(baseSite.getSubType())) {
                //
                if (SiteTypeLevel.SiteTypeThreeLevelEnum.CAMPUS_SCHOOL.getCode().equals(baseSite.getThirdType())
                        || SiteTypeLevel.SiteTypeThreeLevelEnum.JD_STAR_DISTRIBUTION.equals(baseSite.getThirdType())) {
                    return true;
                }
            }else if(SiteTypeLevel.SiteTypeTwoLevelEnum.SHARE_DISTRIBUTION_STATION.getCode().equals(baseSite.getSubType())){
                if (SiteTypeLevel.SiteTypeThreeLevelEnum.TOWN_SHARE_DISTRIBUTION_STATION.getCode().equals(baseSite.getThirdType())
                        || SiteTypeLevel.SiteTypeThreeLevelEnum.CITY_SHARE_DISTRIBUTION_STATION.equals(baseSite.getThirdType())) {
                    return true;
                }
            }
        }
        return  false;
    }
}
