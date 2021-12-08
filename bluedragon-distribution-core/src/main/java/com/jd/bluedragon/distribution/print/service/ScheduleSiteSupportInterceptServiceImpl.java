package com.jd.bluedragon.distribution.print.service;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.WaybillForPreSortOnSiteRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.waybill.handler.ScheduleSiteSupportInterceptHandler;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SiteHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.ws.rs.HEAD;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/21 16:12
 */
@Service("scheduleSiteSupportInterceptService")
public class ScheduleSiteSupportInterceptServiceImpl implements ScheduleSiteSupportInterceptService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleSiteSupportInterceptHandler.class);


    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 预分拣目的站点滑道信息校验
     * @param prepareSiteCode  预分拣目的站点
     * @param startSiteCode  预分拣始发站点
     * @return
     */
    @Override
    @JProfiler(jKey = "com.jd.bluedragon.distribution.print.service.ScheduleSiteSupportInterceptServiceImpl.checkCrossInfo",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<String> checkCrossInfo(String waybillSign, String sendPay , String waybillCode, Integer prepareSiteCode, Integer startSiteCode) {
        InvokeResult<String> result = new InvokeResult<>();
        if(!uccPropertyConfiguration.getBackDispatchCheck()){
            return result;
        }

        try {
            //获取始发道口类型
            Integer originalCrossType = BusinessUtil.getOriginalCrossType(waybillSign, sendPay);
            BaseDmsStore baseDmsStore = new BaseDmsStore();
            JdResult<CrossPackageTagNew> jdResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore,prepareSiteCode,startSiteCode,originalCrossType);
            if(!jdResult.isSucceed()) {
                LOGGER.warn("com.jd.bluedragon.distribution.print.service.ScheduleSiteSupportInterceptServiceImpl.checkCrossInfo-->预分拣站点滑道信息返回失败 :{}，运单号:{}", jdResult.getMessage(),waybillCode);
                result.customMessage(JdResponse.CODE_TARGET_SITE_NO_ROUTE_CONFIRM, JdResponse.MESSAGE_TARGET_SITE_NO_ROUTE_CONFIRM);
                return result;
            }

            if(jdResult.getData()==null || StringUtils.isEmpty(jdResult.getData().getDestinationCrossCode())){
                LOGGER.warn("com.jd.bluedragon.distribution.print.service.ScheduleSiteSupportInterceptServiceImpl.checkCrossInfo-->预分拣站点没有滑道信息 :{}，运单号:{}", jdResult.getMessage(),waybillCode);
                result.customMessage(JdResponse.CODE_TARGET_SITE_NO_ROUTE_CONFIRM, JdResponse.MESSAGE_TARGET_SITE_NO_ROUTE_CONFIRM);
                return result;
            }
        }catch (Exception e){
            LOGGER.error("com.jd.bluedragon.distribution.print.service.ScheduleSiteSupportInterceptServiceImpl.checkCrossInfo-->获取预分拣滑道信息获取异常 waybillCode：{},prepareSiteCode:{},startSiteCode:{}",
                    waybillCode,prepareSiteCode,startSiteCode);
            result.customMessage(JdResponse.CODE_BASIC_SITE_CODE_ERROR, JdResponse.MESSAGE_BASIC_SITE_CODE_ERRPR);
            return result;
        }
        return result;
    }

    /**
     * 校验反调度是否同城
     * @param waybillForPreSortOnSiteRequest
     * @param waybill
     * @param userInfo
     * @return
     */
    @Override
    public InvokeResult<Boolean> checkSameCity(WaybillForPreSortOnSiteRequest waybillForPreSortOnSiteRequest, Waybill waybill, BaseStaffSiteOrgDto userInfo) {
        InvokeResult<Boolean> result = new InvokeResult<>();

        if (StringUtils.isEmpty(uccPropertyConfiguration.getScheduleSiteCheckSameCity())
                || !Constants.SWITCH_OPEN.equals(uccPropertyConfiguration.getScheduleSiteCheckSameCity())) {
            return result;
        }

        try {
            if (userInfo.getSiteCode() != null && userInfo.getSiteCode() > 0) {
                BaseSiteInfoDto curSite = baseMajorManager.getBaseSiteInfoBySiteId(userInfo.getSiteCode());
                // 操作人场地类型是分拣中心
                if (SiteHelper.siteIsSortingCenter(curSite)) {

                    // 判断是外单
                    if (BusinessUtil.isSignChar(waybill.getWaybillSign(), WaybillSignConstants.POSITION_1, WaybillSignConstants.CHAR_1_3)) {

                        // 返调度站点类型是营业部或自提点
                        BaseStaffSiteOrgDto destSite = baseMajorManager.getBaseSiteBySiteId(waybillForPreSortOnSiteRequest.getSiteOfSchedulingOnSite());
                        if (null == destSite) {
                            return result;
                        }

                        if (waybill.getOldSiteId() == null || waybill.getOldSiteId() < 0) {
                            return result;
                        }

                        BaseStaffSiteOrgDto oldPreSite = baseMajorManager.getBaseSiteBySiteId(waybill.getOldSiteId());
                        if (null == oldPreSite) {
                            return result;
                        }

                        if (Arrays.asList(Constants.BASE_SITE_SITE, Constants.BASE_SITE_TYPE_ZT).contains(destSite.getSiteType())
                                && Arrays.asList(Constants.BASE_SITE_SITE, Constants.BASE_SITE_TYPE_ZT).contains(oldPreSite.getSiteType())) {

                            // 获取原站点和反调度后的站点的四级地址，对于直辖市（北京市，上海市，天津市，重庆市）比较一级地址是否相同，不相同则提示话术“仅允许同城范围进行反调度操作”。
                            // 对于非直辖市，比较二级地址是否相同，不相同则提示话术“仅允许同城范围进行反调度操作”
                            boolean notSameCity = false;
                            if (!AreaHelper.isMunicipality(oldPreSite.getProvinceId()) && !AreaHelper.isMunicipality(destSite.getProvinceId())) {
                                if (!Objects.equals(oldPreSite.getCityId(), destSite.getCityId())) {
                                    notSameCity = true;
                                }
                            }
                            else {
                                if (!Objects.equals(oldPreSite.getProvinceId(), destSite.getProvinceId())) {
                                    notSameCity = true;
                                }
                            }
                            if (notSameCity) {
                                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "仅允许同城范围进行返调度操作");
                                return result;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("返调度校验是否同城失败! req:{}", JsonHelper.toJson(waybillForPreSortOnSiteRequest), e);
            result.error("返调度校验是否同城失败！请联系分拣小秘");
        }

        return result;
    }
}
    
