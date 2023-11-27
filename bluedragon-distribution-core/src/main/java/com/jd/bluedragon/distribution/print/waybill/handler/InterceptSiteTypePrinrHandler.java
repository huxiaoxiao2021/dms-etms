package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private DmsConfigManager dmsConfigManager;

    @Value("#{'${siteTypeOfTwoLevelList}'.split(',')}")
    private List<Integer> siteTypeOfTwoLevelList;

    @Value("#{'${siteTypeOfThreeLevelList}'.split(',')}")
    private List<Integer> siteTypeOfThreeLevelList;



	@Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.InterceptWaybillHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
	public JdResult<String> handle(WaybillPrintContext context) {
		if(log.isInfoEnabled()){
            log.info("interceptPackageReprint 操作类型-{}-单号-{}",context.getRequest().getOperateType(),context.getRequest().getBarCode());
        }
        InterceptResult<String> result = context.getResult();

        if(WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType().equals(context.getRequest().getOperateType())){
            Integer siteCode = context.getRequest().getSiteCode();
            BaseSiteInfoDto baseSite = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
            log.info("包裹补打  站点信息-{}", JSON.toJSONString(baseSite));
            if(baseSite == null) {
                result.toFail(SortingResponse.PACKAGE_PRINT_BAN_CODE,"站点信息为空!");
                return result;
            }
            //终端包裹补打功能限制
            if(interceptTerminalSitePackageReprint(baseSite)){
                log.warn("命中拦截逻辑-{}",context.getRequest().getBarCode());
                result.toFail(SortingResponse.PACKAGE_PRINT_BAN_CODE,SortingResponse.PACKAGE_PRINT_BAN_MESSAGE_1);
                return result;
            }
            //分拣中心、接货仓、退货组包裹补打功能限制
            if(interceptSortOrWarehouseOrReturngroupSitePackageReprint(baseSite)){
                result.toFail(SortingResponse.PACKAGE_PRINT_BAN_CODE,SortingResponse.PACKAGE_PRINT_BAN_MESSAGE_2);
                return result;
            }
        }

		return result;
	}

    /**
     * 包裹补打拦截
     * true:拦截 false:不拦截
     * @return
     */
    private boolean interceptTerminalSitePackageReprint(BaseSiteInfoDto baseSite){


        boolean terminalSitePackagePrintLimitSwitch = dmsConfigManager.getPropertyConfig().isTerminalSitePackagePrintLimitSwitch();
        if(!terminalSitePackagePrintLimitSwitch){
            log.warn("终端站点包裹补打功能拦截功能关闭!");
            return false;
        }
        log.info("siteTypeOfTwoLevelList-{}   siteTypeOfThreeLevelList-{} ",JSON.toJSONString(siteTypeOfTwoLevelList),JSON.toJSONString(siteTypeOfThreeLevelList));
        if(siteTypeOfTwoLevelList.contains(baseSite.getSubType()) || siteTypeOfThreeLevelList.contains(baseSite.getThirdType())){
            return true;
        }

        return  false;
    }

    /**
     * 校验场地 分拣中心、接货仓、退货组 中异常岗人员 是否允许包裹补打
     * true 拦截 false: 不拦截
     * @return
     */
    private boolean interceptSortOrWarehouseOrReturngroupSitePackageReprint(BaseSiteInfoDto baseSite){

        boolean sortOrWarehouseOrReturngroupPackagePrintLimitSwitch = dmsConfigManager.getPropertyConfig().isTerminalSitePackagePrintLimitSwitch();
        if(!sortOrWarehouseOrReturngroupPackagePrintLimitSwitch){
            log.warn("分拣中心、接货仓、退货组 包裹补打功能拦截功能关闭!");
            return false;
        }


        return false;
    }

}
