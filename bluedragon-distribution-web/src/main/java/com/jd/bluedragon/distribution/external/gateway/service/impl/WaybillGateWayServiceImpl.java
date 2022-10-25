package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.waybill.request.WaybillTrackReq;
import com.jd.bluedragon.common.dto.waybill.request.WaybillTrackResponse;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.base.BaseResource;
import com.jd.bluedragon.external.gateway.service.WaybillGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.jim.cli.Cluster;
import com.jd.ql.erp.util.BeanUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 运单相关
 * @author : xumigen
 * @date : 2019/7/16
 */
public class WaybillGateWayServiceImpl implements WaybillGateWayService {
    private static final Logger log = LoggerFactory.getLogger(WaybillGateWayServiceImpl.class);

    @Resource
    private BaseResource baseResource;
    @Autowired
    private WaybillTraceManager waybillTraceManager;
    @Autowired
    private WaybillPackageApi waybillPackageApi;

    private static final String WAYBILL_TRACK = "waybill_track_";
    private static final String WAYBILL_TRACK_LOCK = "waybill_track_lock";
    private static final Long WAYBILL_TRACK_HISTORY_NUM = 10L;
    private final static long REDIS_CACHE_EXPIRE_TIME = 5 * 60;

    @Autowired
    @Qualifier("redisClient")
    private Cluster redisClient;
    @Override
    @BusinessLog(sourceSys = 1,bizType = 2006,operateType = 20061)
    @JProfiler(jKey = "DMSWEB.WaybillGateWayServiceImpl.getPerAndSfSiteByWaybill", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<List<Integer>> getPerAndSfSiteByWaybill(String waybillCode){
        InvokeResult<List<Integer>> invokeResult = baseResource.perAndSelfSite(waybillCode);
        JdCResponse<List<Integer>> jdCResponse = new JdCResponse<>();
        if(InvokeResult.RESULT_SUCCESS_CODE == invokeResult.getCode()){
            jdCResponse.toSucceed(invokeResult.getMessage());
            jdCResponse.setData(invokeResult.getData());
            return jdCResponse;
        }
        jdCResponse.toError(invokeResult.getMessage());
        return jdCResponse;
    }

    /**
     * 查询运单包裹列表，顺序从小到大
     * @param waybillCode
     * @return
     */
    @Override
    public JdCResponse<List<String>> queryPackageCodes(String waybillCode) {
        log.info("运单【"+waybillCode+"】查询包裹号列表开始");
        BaseEntity<List<String>> entity = waybillPackageApi.getWaybillPackageCodes(waybillCode);
        log.info("运单【"+waybillCode+"】查询包裹号列表，结果："+JsonHelper.toJson(entity));
        if(entity == null){
            return null;
        }
        JdCResponse<List<String>> jdCResponse = new JdCResponse<>();
        if(entity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode()){
            jdCResponse.setData(entity.getData());
            jdCResponse.toSucceed(entity.getMessage());
            return jdCResponse;
        }
        jdCResponse.toError(entity.getMessage());
        return jdCResponse;
    }

    /**
     * 运单全程跟踪历史记录
     * @param erp
     * @return
     */
    @Override
    public JdCResponse<List<String>> queryWaybillTrackHistory(String erp) {
        log.info("操作人【"+erp+"】全程跟踪历史记录查询开始");
        String key = WAYBILL_TRACK + erp;
        Set<String> historySet = redisClient.zRevRange(key, 0, WAYBILL_TRACK_HISTORY_NUM);
        log.info("操作人【"+erp+"】全程跟踪历史记录:"+JsonHelper.toJson(historySet));
        JdCResponse<List<String>> jdCResponse = new JdCResponse<>();
        jdCResponse.setData(Lists.newArrayList(historySet));
        jdCResponse.toSucceed();
        return jdCResponse;
    }

    @Override
    public JdCResponse<List<WaybillTrackResponse>> queryWaybillTrack(WaybillTrackReq waybillTrackReq) {
        BaseEntity<List<PackageState>> entity = waybillTraceManager.getAllOperations(waybillTrackReq.getBarCode());
        if(entity == null){
            return null;
        }
        JdCResponse<List<WaybillTrackResponse>> jdCResponse = new JdCResponse<>();
        if(entity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode()){
            //查询成功则 记录历史
            recodHistory(waybillTrackReq);
            List<WaybillTrackResponse> waybillTrackResponses = Lists.newArrayList();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(entity.getData())){
                for (PackageState packageState:entity.getData()){
                    WaybillTrackResponse res = new WaybillTrackResponse();
                    BeanUtils.copyProperties(packageState,res);

                    waybillTrackResponses.add(res);
                }
            }
            jdCResponse.setData(waybillTrackResponses);
            jdCResponse.toSucceed(entity.getMessage());
            return jdCResponse;
        }
        jdCResponse.toError(entity.getMessage());
        return jdCResponse;
    }

    private void recodHistory(WaybillTrackReq waybillTrackReq) {
        String lockKey = WAYBILL_TRACK_LOCK + waybillTrackReq.getErp();
        if (redisClient.set(lockKey, "1", REDIS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS, false)) {
            try {
                String key = WAYBILL_TRACK + waybillTrackReq.getErp();
                redisClient.zAdd(key, System.currentTimeMillis(), waybillTrackReq.getBarCode());
                Long count = redisClient.zCard(key);
                redisClient.zRemRangeByScore(key, WAYBILL_TRACK_HISTORY_NUM, count);
            } catch (Exception e) {
                log.error("[全程跟踪查询记录]设置Redis并发锁时发生异常，waybillTrackReq:{}", JsonHelper.toJson(waybillTrackReq), e);
            } finally {
                redisClient.del(lockKey);
            }
        }

    }

}
