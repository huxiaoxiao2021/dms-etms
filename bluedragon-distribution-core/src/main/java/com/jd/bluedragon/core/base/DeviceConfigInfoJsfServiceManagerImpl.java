package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.common.constant.WeightValidateSwitchEnum;
import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/11/17 19:06
 */
@Service("deviceConfigInfoJsfServiceManager")
public class DeviceConfigInfoJsfServiceManagerImpl implements DeviceConfigInfoJsfServiceManager{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DeviceConfigInfoJsfService deviceConfigInfoJsfService;

    /**
     * 全国维度
     * @param weightValidateSwitchEnum
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BASE.DeviceConfigInfoJsfServiceManagerImpl.maintainWeightSwitch",mState = JProEnum.TP)
    public BaseDmsAutoJsfResponse maintainWeightSwitch(WeightValidateSwitchEnum weightValidateSwitchEnum) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DeviceConfigInfoJsfServiceManagerImpl.maintainWeightSwitch", false, true);
        BaseDmsAutoJsfResponse baseDmsAutoJsfResponse = null;
        try {
            if(log.isInfoEnabled()){
                log.info("调用分拣机全国开关入参:WeightValidateSwitchEnum:{}",JsonHelper.toJsonMs(weightValidateSwitchEnum));
            }
            baseDmsAutoJsfResponse = deviceConfigInfoJsfService.maintainWeightSwitch(weightValidateSwitchEnum);
            if(log.isInfoEnabled()){
                log.info("调用分拣机全国开关入参WeightValidateSwitchEnum:{},出参baseDmsAutoJsfResponse:{}",
                        JsonHelper.toJsonMs(weightValidateSwitchEnum),JsonHelper.toJsonMs(baseDmsAutoJsfResponse));
            }
            return baseDmsAutoJsfResponse;
        }catch (Exception e){
            log.error("调用分拣机全国拦截接口异常",e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return  baseDmsAutoJsfResponse;
    }

    /**
     * 站点维度
     * @param siteCodesArray
     * @param weightValidateSwitchEnum
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BASE.DeviceConfigInfoJsfServiceManagerImpl.maintainSiteWeightSwitch",mState = JProEnum.TP)
    public BaseDmsAutoJsfResponse maintainSiteWeightSwitch(Integer[] siteCodesArray, WeightValidateSwitchEnum weightValidateSwitchEnum) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DeviceConfigInfoJsfServiceManagerImpl.maintainSiteWeightSwitch", false, true);
        BaseDmsAutoJsfResponse baseDmsAutoJsfResponse = null;
        try {
            if(log.isInfoEnabled()){
                log.info("调用分拣机开关入参:siteCodesArray:{},WeightValidateSwitchEnum:{}", JsonHelper.toJsonMs(siteCodesArray),JsonHelper.toJsonMs(weightValidateSwitchEnum));
            }
            baseDmsAutoJsfResponse = deviceConfigInfoJsfService.maintainSiteWeightSwitch(siteCodesArray,weightValidateSwitchEnum);

            if(log.isInfoEnabled()){
                log.info("调用分拣机开关入参:siteCodesArray:{},WeightValidateSwitchEnum:{},出参longBaseDmsAutoJsfResponse:{}",JsonHelper.toJsonMs(siteCodesArray),
                        JsonHelper.toJsonMs(weightValidateSwitchEnum),JsonHelper.toJsonMs(baseDmsAutoJsfResponse));
            }
            return baseDmsAutoJsfResponse;
        }catch (Exception e){
            log.error("调用分拣机站点拦截接口异常,入参siteCodesArray:{},WeightValidateSwitchEnum:{}",siteCodesArray,weightValidateSwitchEnum,e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return baseDmsAutoJsfResponse;
    }
}
    
