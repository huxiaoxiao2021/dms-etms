package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.exception.SealVehicleTaskBusinessException;
import com.jd.bluedragon.distribution.jy.exception.JyDemotionException;
import com.jd.bluedragon.distribution.jy.service.config.JyDemotionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.realtime.api.seal.ISealVehicleService;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.base.ServiceResult;
import com.jdl.jy.realtime.model.es.seal.SealCarMonitor;
import com.jdl.jy.realtime.model.query.seal.SealVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.seal.SealVehicleTaskResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName JyUnSealVehicleManagerImpl
 * @Description
 * @Author wyh
 * @Date 2022/3/9 19:49
 **/
@Service("jyUnSealVehicleManager")
public class JyUnSealVehicleManagerImpl implements IJyUnSealVehicleManager {

    private static final Logger log = LoggerFactory.getLogger(JyUnSealVehicleManagerImpl.class);

    @Autowired
    @Qualifier("jySealVehicleService")
    private ISealVehicleService sealVehicleService;

    @Autowired
    private UccPropertyConfiguration uccConfig;

    @Autowired
    private JyDemotionService jyDemotionService;

    @Override
    public SealVehicleTaskResponse querySealTask(Pager<SealVehicleTaskQuery> pager) {
        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.jySealVehicleManager.querySealCarByStatus");

        try {
            if (uccConfig.getSealTaskForceFallback() == Constants.YN_YES) {
                log.info("解封车任务列表服务强制降级. query:{}", JsonHelper.toJson(pager));
                throw new SealVehicleTaskBusinessException("解封车任务列表服务强制降级");
            }

            ServiceResult<SealVehicleTaskResponse> serviceResult = sealVehicleService.querySealCarByStatus(pager);

            if (log.isInfoEnabled()) {
                log.info("查询待解封车任务. query:{}, response:{}", JsonHelper.toJson(pager), JsonHelper.toJson(serviceResult));
            }

            if (serviceResult.retFail()) {
                log.error("查询待解封车任务失败. {}-{}", JsonHelper.toJson(pager), JsonHelper.toJson(serviceResult));
                throw new SealVehicleTaskBusinessException(serviceResult.getMessage());
            }

            return serviceResult.getData();
        }
        catch (Exception e) {
            log.error("查询解封车任务异常. {}", JsonHelper.toJson(pager), e);
            Profiler.functionError(ump);
            throw new SealVehicleTaskBusinessException(e.getMessage(), e);
        }
        finally {
            Profiler.registerInfoEnd(ump);
        }
    }

    @Override
    public SealCarMonitor querySealTaskInfo(SealTaskInfoRequest request) {

        if(jyDemotionService.checkIsDemotion(JyDemotionService.JY_SEAL_CAR_MONITOR_IS_DEMOTION)){
            throw new JyDemotionException("封车：查询封车任务详情已降级!");
        }

        SealCarMonitor search = new SealCarMonitor();
        search.setEndSiteId(request.getCurrentOperate().getSiteCode());
        search.setSealCarCode(request.getSealCarCode());

        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.jySealVehicleManager.querySealTaskInfo");
        try {
            ServiceResult<List<SealCarMonitor>> serviceResult = sealVehicleService.querySealVehicle(search);
            if (serviceResult.retFail()) {
                log.error("查询解封车任务详情失败. {}-{}", JsonHelper.toJson(request), JsonHelper.toJson(serviceResult));
                return null;
            }
            if (CollectionUtils.isEmpty(serviceResult.getData())) {
                log.error("根据封车编码未查询到封车数据. {}-{}", JsonHelper.toJson(request), JsonHelper.toJson(serviceResult));
                return null;
            }

            return serviceResult.getData().get(0);
        }
        catch (Exception e) {
            log.error("查询解封车任务详情异常. {}", JsonHelper.toJson(request), e);
            Profiler.functionError(ump);
        }
        finally {
            Profiler.registerInfoEnd(ump);
        }

        return null;
    }
}
