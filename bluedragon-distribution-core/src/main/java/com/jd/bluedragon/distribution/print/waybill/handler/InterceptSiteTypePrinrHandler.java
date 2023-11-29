package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.workStation.JyUserManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.JdSiteTypeConfig;
import com.jd.bluedragon.distribution.print.domain.JdStdPositionCodeConfig;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.common.utils.Result;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private JyUserManager jyUserManager;


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
            if(interceptSitePackageReprint(baseSite,context)){
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
     * 校验场地是否允许包裹补打
     * true 拦截 false: 不拦截
     * @return
     */
    private boolean interceptSitePackageReprint(BaseSiteInfoDto baseSite,WaybillPrintContext context){

        String userERP = context.getRequest().getUserERP();
        if(StringUtils.isBlank(userERP)){
            log.info("userERP为空");
            return false;
        }
        SysConfig siteConfig = sysConfigService.findConfigContentByConfigName(Constants.PACKAGE_PRINT_LIMIT_SITE_TYPE_CONFIG);
        if(siteConfig == null){
            log.warn("获取包裹补打限制站点配置为空！");
            return false;
        }
        JdSiteTypeConfig jdSiteTypeConfig = com.alibaba.fastjson.JSON.parseObject(siteConfig.getConfigContent(), JdSiteTypeConfig.class);
        if(jdSiteTypeConfig == null){
            log.warn("获取包裹补打限制站点配置为空！");
            return false;
        }
        boolean checkSwitch = jdSiteTypeConfig.isCheckSwitch();
        if(!checkSwitch){
            log.warn("分拣中心、接货仓、退货组 包裹补打功能拦截功能关闭!");
        }
        SysConfig positionConfig = sysConfigService.findConfigContentByConfigName(Constants.PACKAGE_PRINT_LIMIT_POSITION_CODE_TYPE_CONFIG);
        if(positionConfig == null){
            log.warn("获取标准岗位编码限制配置为空！");
            return false;
        }
        JdStdPositionCodeConfig jdStdPositionCodeConfig = com.alibaba.fastjson.JSON.parseObject(positionConfig.getConfigContent(), JdStdPositionCodeConfig.class);
        if(jdStdPositionCodeConfig == null){
            log.warn("获取标准岗位编码限制配置为空！");
            return false;
        }

        log.info("获取包裹补打限制站点配置-{}",JSON.toJSONString(jdSiteTypeConfig));
        log.info("获取标准岗位编码限制配置-{}",JSON.toJSONString(jdStdPositionCodeConfig));
        List<Integer> sortTypes = jdSiteTypeConfig.getSortTypes();
        List<Integer> sortSubTypes = jdSiteTypeConfig.getSortSubTypes();
        List<String> positionCodes = jdStdPositionCodeConfig.getPositionCodes();
        log.info("sortTypes -{} sortSubTypes-{} positionCodes-{}",JSON.toJSONString(sortTypes),JSON.toJSONString(sortSubTypes),JSON.toJSON(positionCodes));
        boolean matchSiteType = matchSiteType(sortTypes, sortTypes, baseSite);
        if(matchSiteType){
            Result<JyUserDto> jyUserDtoResult = jyUserManager.queryByUserErp(userERP);
            JyUserDto data = jyUserDtoResult.getData();
            if(data == null){
                //操作人ERP不在人资花名册里面
                return true;
            }
            if(CollectionUtils.isNotEmpty(positionCodes) ){
                //在花名册但是角色不是【异常岗】人员
                return !positionCodes.contains(data.getStdPositionCode());
            }
        }
        return false;
    }

    /**
     * 当前站点类型是否匹配配置
     * @param sortTypes
     * @param sortSubTypes
     * @param baseSite
     * @return true 匹配  false 不匹配 命中其一即可
     */
    private boolean matchSiteType(List<Integer> sortTypes,List<Integer> sortSubTypes,BaseSiteInfoDto baseSite){

        boolean f1 =false;
        boolean f2 =false;
        if(CollectionUtils.isNotEmpty(sortTypes)){
            f1 =sortTypes.contains(baseSite.getSortType());
        }
        if(CollectionUtils.isNotEmpty(sortSubTypes)){
            f2 = sortSubTypes.contains(baseSite.getSortSubType());
        }
        return (f1 || f2);
    }

}
