package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.dms.common.constants.JyConstants;
import com.jd.bluedragon.distribution.jy.exception.JyDemotionException;
import com.jd.bluedragon.distribution.jy.service.config.JyDemotionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseGoodsAreaNextSiteDto;
import com.jd.ql.basic.dto.ResultDTO;
import com.jd.ql.basic.ws.BasicGoodsAreaWS;
import com.jd.ql.dms.common.constants.JyConstants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.realtime.api.unload.IUnloadVehicleJsfService;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.base.ServiceResult;
import com.jdl.jy.realtime.model.es.unload.JySealCarDetail;
import com.jdl.jy.realtime.model.es.unload.JyUnloadTaskWaybillAgg;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jd.bluedragon.common.dto.base.response.JdCResponse.MESSAGE_SUCCESS;

/**
 * @ClassName JyUnloadVehicleManagerImpl
 * @Description
 * @Author wyh
 * @Date 2022/4/6 19:45
 **/
@Service("jyUnloadVehicleManager")
public class JyUnloadVehicleManagerImpl implements IJyUnloadVehicleManager {

    private static final Logger log = LoggerFactory.getLogger(JyUnloadVehicleManagerImpl.class);

    @Autowired
    @Qualifier("jyUnloadVehicleJsfService")
    private IUnloadVehicleJsfService unloadVehicleJsfService;
    @Autowired
    private BasicGoodsAreaWS basicGoodsAreaWS;

    @Autowired
    private JyDemotionService jyDemotionService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleManagerImpl.queryToScanBarCodeDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Pager<JyVehicleTaskUnloadDetail> queryToScanBarCodeDetail(Pager<JyVehicleTaskUnloadDetail> query) {
        if(jyDemotionService.checkIsDemotion(JyConstants.JY_VEHICLE_TASK_UNLOAD_DETAIL_IS_DEMOTION)){
            throw new JyDemotionException("卸车：查询待扫包裹明细已降级!");
        }
        Pager<JyVehicleTaskUnloadDetail> unloadDetails = new Pager<>();
        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleManager.queryToScanBarCodeDetail");
        try {
            ServiceResult<Pager<JyVehicleTaskUnloadDetail>> serviceResult = unloadVehicleJsfService.queryToScanBarCodeDetail(query);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            } else {
                log.warn("分页查询卸车待扫描包裹失败. {}. {}", JsonHelper.toJson(query), JsonHelper.toJson(serviceResult));
            }
        } catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询卸车待扫包裹明细异常. {}", JsonHelper.toJson(query), ex);
        }
        Profiler.registerInfoEnd(ump);

        return unloadDetails;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleManagerImpl.queryInterceptBarCodeDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Pager<JyVehicleTaskUnloadDetail> queryInterceptBarCodeDetail(Pager<JyVehicleTaskUnloadDetail> query) {
        if(jyDemotionService.checkIsDemotion(JyConstants.JY_VEHICLE_TASK_UNLOAD_DETAIL_IS_DEMOTION)){
            throw new JyDemotionException("卸车：查询拦截包裹明细已降级!");
        }
        Pager<JyVehicleTaskUnloadDetail> unloadDetails = new Pager<>();
        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleManager.queryInterceptBarCodeDetail");
        try {
            ServiceResult<Pager<JyVehicleTaskUnloadDetail>> serviceResult = unloadVehicleJsfService.queryInterceptBarCodeDetail(query);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            } else {
                log.warn("分页查询卸车拦截包裹失败. {}. {}", JsonHelper.toJson(query), JsonHelper.toJson(serviceResult));
            }
        } catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询卸车拦截包裹异常. {}", JsonHelper.toJson(query), ex);
        }
        Profiler.registerInfoEnd(ump);

        return unloadDetails;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleManagerImpl.queryMoreScanBarCodeDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Pager<JyVehicleTaskUnloadDetail> queryMoreScanBarCodeDetail(Pager<JyVehicleTaskUnloadDetail> query) {
        if(jyDemotionService.checkIsDemotion(JyConstants.JY_VEHICLE_TASK_UNLOAD_DETAIL_IS_DEMOTION)){
            throw new JyDemotionException("卸车：查询多扫包裹明细已降级!");
        }
        Pager<JyVehicleTaskUnloadDetail> unloadDetails = new Pager<>();

        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleManager.queryMoreScanBarCodeDetail");
        try {
            ServiceResult<Pager<JyVehicleTaskUnloadDetail>> serviceResult = unloadVehicleJsfService.queryMoreScanBarCodeDetail(query);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            } else {
                log.warn("分页查询卸车多扫包裹失败. {}. {}", JsonHelper.toJson(query), JsonHelper.toJson(serviceResult));
            }
        } catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询卸车多扫包裹异常. {}", JsonHelper.toJson(query), ex);
        }
        Profiler.registerInfoEnd(ump);

        return unloadDetails;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleManagerImpl.queryMoreScanAndToScanBarCodeDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Pager<JyVehicleTaskUnloadDetail> queryMoreScanAndToScanBarCodeDetail(Pager<JyVehicleTaskUnloadDetail> query) {
        if(jyDemotionService.checkIsDemotion(JyConstants.JY_VEHICLE_TASK_UNLOAD_DETAIL_IS_DEMOTION)){
            throw new JyDemotionException("卸车：查询多扫和待扫包裹明细已降级!");
        }
        Pager<JyVehicleTaskUnloadDetail> unloadDetails = new Pager<>();

        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleManager.queryMoreScanAndToScanBarCodeDetail");
        try {
            ServiceResult<Pager<JyVehicleTaskUnloadDetail>> serviceResult = unloadVehicleJsfService.queryMoreScanAndToScanBarCodeDetail(query);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            } else {
                log.warn("分页查询卸车多扫待扫包裹失败. {}. {}", JsonHelper.toJson(query), JsonHelper.toJson(serviceResult));
            }
        } catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询卸车多扫待扫包裹异常. {}", JsonHelper.toJson(query), ex);
        }
        Profiler.registerInfoEnd(ump);

        return unloadDetails;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleManagerImpl.findUnloadDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyVehicleTaskUnloadDetail> findUnloadDetail(JyVehicleTaskUnloadDetail query) {
        if(jyDemotionService.checkIsDemotion(JyConstants.JY_VEHICLE_TASK_UNLOAD_DETAIL_IS_DEMOTION)){
            throw new JyDemotionException("卸车：查询卸车包裹数据已降级!");
        }
        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleManager.findUnloadDetail");
        try {
            ServiceResult<List<JyVehicleTaskUnloadDetail>> serviceResult = unloadVehicleJsfService.findSealCarCode(query);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            }
        } catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询一条卸车明细异常. {}", JsonHelper.toJson(query), ex);
        }
        Profiler.registerInfoEnd(ump);

        return null;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleManagerImpl.querySearCarDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Pager<JySealCarDetail> querySearCarDetail(Pager<JySealCarDetail> query) {

        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleManager.querySearCarDetail");
        try {
            ServiceResult<Pager<JySealCarDetail>> serviceResult = unloadVehicleJsfService.querySearCarDetail(query);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            }
        }
        catch (Exception ex) {
            Profiler.functionError(ump);
            log.error("查询封车明细异常. {}", JsonHelper.toJson(query), ex);
        }
        Profiler.registerInfoEnd(ump);

        return null;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleManagerImpl.getGoodsAreaCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Cache(key = "IJyUnloadVehicleManager.getGoodsAreaCode@args0@args1", memoryEnable = true, memoryExpiredTime = 3 * 60 * 1000, redisEnable = true, redisExpiredTime = 5 * 60 * 1000)
    public String getGoodsAreaCode(Integer currentSiteCode, Integer nextSiteCode) {
        BaseGoodsAreaNextSiteDto goodsAreaNextSiteDto = new BaseGoodsAreaNextSiteDto();
        goodsAreaNextSiteDto.setSiteCode(currentSiteCode);
        goodsAreaNextSiteDto.setNextSiteCode(nextSiteCode);
        ResultDTO<BaseGoodsAreaNextSiteDto> resultDTO = basicGoodsAreaWS.getGoodsAreaNextSite(goodsAreaNextSiteDto);
        if (resultDTO != null && MESSAGE_SUCCESS.equals(resultDTO.getCode())) {
            if (resultDTO.getData() == null) {
                log.warn("调用基础资料查询获取编码失败,返回：{}" + JsonUtils.toJSONString(resultDTO));
            } else {
                log.info("调用基础资料查询获取编码成功,返回：{}", JsonUtils.toJSONString(resultDTO));
                String goodsAreaCode = resultDTO.getData().getAreaNo();
                if (ObjectHelper.isNotNull(goodsAreaCode)) {
                    return goodsAreaCode;
                }
            }
        }
        log.warn("调用基础资料查询获取编码失败，当前场地：{}，下一场地：{}", currentSiteCode, nextSiteCode);
        return null;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleManagerImpl.queryUnloadTaskWaybill", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Pager<JyUnloadTaskWaybillAgg> queryUnloadTaskWaybill(Pager<JyVehicleTaskUnloadDetail> query) {
        Pager<JyUnloadTaskWaybillAgg> waybillAggPager = new Pager<>();

        try {
            ServiceResult<Pager<JyUnloadTaskWaybillAgg>> serviceResult = unloadVehicleJsfService.queryUnloadTaskWaybill(query);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            } else {
                log.warn("查询卸车岗统计下钻运单明细. {}. {}", JsonHelper.toJson(query), JsonHelper.toJson(serviceResult));
            }
        } catch (Exception ex) {
            log.error("查询卸车岗统计下钻运单明细. {}", JsonHelper.toJson(query), ex);
        }
        return waybillAggPager;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleManagerImpl.queryShouldScanCountByBarCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Long queryShouldScanCountByBarCode(Pager<JyVehicleTaskUnloadDetail> query) {
        try {
            ServiceResult<Long> serviceResult = unloadVehicleJsfService.queryShouldScanCountByBarCode(query);
            if (serviceResult.retSuccess()) {
                return serviceResult.getData();
            } else {
                log.warn("卸车任务根据barCode查询应扫包裹数量失败:query={},serviceResult={}", JsonHelper.toJson(query), JsonHelper.toJson(serviceResult));
            }
        } catch (Exception ex) {
            log.error("卸车任务根据barCode查询应扫包裹数量异常:query={}", JsonHelper.toJson(query), ex);
        }
        return 0L;
    }
}
