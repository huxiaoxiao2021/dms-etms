package com.jd.bluedragon.distribution.track.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.request.WaybillTrackReqVO;
import com.jd.bluedragon.distribution.api.response.WaybillTrackResVO;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.track.WaybillTrackQueryService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.jim.cli.Cluster;
import com.jd.ql.erp.util.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 查询运单全程跟踪接口实现
 *
 * @author hujiping
 * @date 2023/3/7 4:03 PM
 */
@Service("waybillTrackQueryService")
public class WaybillTrackQueryServiceImpl implements WaybillTrackQueryService {

    private final Logger logger = LoggerFactory.getLogger(WaybillTrackQueryServiceImpl.class);

    private static final String WAYBILL_TRACK = "waybill_track_";
    private static final String WAYBILL_TRACK_LOCK = "waybill_track_lock";
    private static final Long WAYBILL_TRACK_HISTORY_NUM = 10L;
    private final static long REDIS_CACHE_EXPIRE_TIME = 5 * 60;
    
    @Autowired
    private WaybillTraceManager waybillTraceManager;
    
    @Autowired
    private WaybillPackageApi waybillPackageApi;

    @Autowired
    @Qualifier("redisClient")
    private Cluster redisClient;

    @Override
    public InvokeResult<List<String>> queryPackageCodes(String waybillCode) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        logger.info("运单:{}查询包裹号列表开始", waybillCode);
        BaseEntity<List<String>> entity = waybillPackageApi.getWaybillPackageCodes(waybillCode);
        logger.info("运单:{}查询包裹号列表，结果：{}", waybillCode, JsonHelper.toJson(entity));
        if(entity == null){
            return null;
        }
        if(entity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode()){
            result.setData(entity.getData());
            return result;
        }
        result.error(entity.getMessage());
        return result;
    }

    @Override
    public InvokeResult<List<String>> queryWaybillTrackHistory(String erp) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        logger.info("操作人:{}全程跟踪历史记录查询开始", erp);
        String key = WAYBILL_TRACK + erp;
        Set<String> historySet = redisClient.zRevRange(key, 0, WAYBILL_TRACK_HISTORY_NUM);
        logger.info("操作人:{}全程跟踪历史记录:{}", erp, JsonHelper.toJson(historySet));
        result.setData(Lists.newArrayList(historySet));
        return result;
    }

    @Override
    public InvokeResult<List<WaybillTrackResVO>> queryWaybillTrack(WaybillTrackReqVO waybillTrackReqVO) {
        InvokeResult<List<WaybillTrackResVO>> result = new InvokeResult<>();
        BaseEntity<List<PackageState>> entity = waybillTraceManager.getAllOperations(waybillTrackReqVO.getBarCode());
        if(entity == null){
            return null;
        }
        if(entity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode()){
            //查询成功则 记录历史
            recodHistory(waybillTrackReqVO);
            List<WaybillTrackResVO> waybillTrackResponsVOS = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(entity.getData())){
                for (PackageState packageState:entity.getData()){
                    WaybillTrackResVO res = new WaybillTrackResVO();
                    BeanUtils.copyProperties(packageState,res);
                    waybillTrackResponsVOS.add(res);
                }
            }
            result.setData(waybillTrackResponsVOS);
            return result;
        }
        result.error(entity.getMessage());
        return result;
    }

    private void recodHistory(WaybillTrackReqVO waybillTrackReqVO) {
        String lockKey = WAYBILL_TRACK_LOCK + waybillTrackReqVO.getErp();
        if (redisClient.set(lockKey, "1", REDIS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS, false)) {
            try {
                String key = WAYBILL_TRACK + waybillTrackReqVO.getErp();
                redisClient.zAdd(key, System.currentTimeMillis(), waybillTrackReqVO.getBarCode());
                Long count = redisClient.zCard(key);
                redisClient.zRemRangeByScore(key, WAYBILL_TRACK_HISTORY_NUM, count);
            } catch (Exception e) {
                logger.error("[全程跟踪查询记录]设置Redis并发锁时发生异常，waybillTrackReq:{}", JsonHelper.toJson(waybillTrackReqVO), e);
            } finally {
                redisClient.del(lockKey);
            }
        }

    }
}
