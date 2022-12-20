package com.jd.bluedragon.distribution.jy.manager;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.ql.dms.common.constants.JyConstants;
import com.jd.bluedragon.distribution.jy.exception.JyDemotionException;
import com.jd.bluedragon.distribution.jy.service.config.JyDemotionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.realtime.api.send.ISendVehicleJsfService;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.base.ServiceResult;
import com.jdl.jy.realtime.model.query.send.SendVehiclePackageDetailQuery;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.send.SendBarCodeDetailVo;
import com.jdl.jy.realtime.model.vo.send.SendVehiclePackageDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @ClassName JySendVehicleJsfManagerImpl
 * @Description
 * @Author wyh
 * @Date 2022/6/14 14:14
 **/
@Component
public class JySendVehicleJsfManagerImpl implements IJySendVehicleJsfManager {

    private static final Logger log = LoggerFactory.getLogger(JySendVehicleJsfManagerImpl.class);

    @Autowired
    @Qualifier("jySendVehicleJsfService")
    private ISendVehicleJsfService sendVehicleJsfService;

    @Autowired
    private JyDemotionService jyDemotionService;

    @Override
    public Pager<SendBarCodeDetailVo> queryByCondition(Pager<SendVehicleTaskQuery> queryPager) {
        if(jyDemotionService.checkIsDemotion(JyConstants.JY_VEHICLE_SEND_DETAIL_IS_DEMOTION)){
            throw new JyDemotionException("发车：查询发车任务包裹明细已降级!");
        }
        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJySendVehicleManager.queryByCondition");
        try {
            ServiceResult<Pager<SendBarCodeDetailVo>> serviceResult = sendVehicleJsfService.queryByCondition(queryPager);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            }
            else {
                log.warn("分页查询发车包裹失败. {}. {}", JsonHelper.toJson(queryPager), JsonHelper.toJson(serviceResult));
            }
        }
        catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询发车包裹明细异常. {}", JsonHelper.toJson(queryPager), ex);
        }
        Profiler.registerInfoEnd(ump);

        return null;
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.JySendVehicleJsfManagerImpl.querySendVehicleToScanPackageDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Pager<SendVehiclePackageDetailVo> querySendVehicleToScanPackageDetail(Pager<SendVehiclePackageDetailQuery> queryPager) {
        if(jyDemotionService.checkIsDemotion(JyConstants.JY_VEHICLE_SEND_DETAIL_IS_DEMOTION)){
            throw new JyDemotionException("发车：查询发车任务待扫包裹列表已降级!");
        }
        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJySendVehicleManager.querySendVehicleToScanPackageDetail");
        try {
            log.info("查询发车任务待扫包裹列表入参-{}", JSON.toJSONString(queryPager));
            ServiceResult<Pager<SendVehiclePackageDetailVo>> serviceResult = sendVehicleJsfService.querySendVehicleToScanPackageDetail(queryPager);
            log.info("查询发车任务待扫包裹列表结果-{}",JSON.toJSONString(serviceResult.getMessage()));
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            }
            else {
                log.warn("分页查询发车任务待扫包裹列表失败. {}. {}", JsonHelper.toJson(queryPager), JsonHelper.toJson(serviceResult));
            }
        }
        catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("分页查询发车任务待扫包裹列表异常. {}", JsonHelper.toJson(queryPager), ex);
        }
        Profiler.registerInfoEnd(ump);

        return null;
    }

}
