package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingGoodsReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingGoodsRes;
import com.jd.bluedragon.distribution.jy.constants.BarCodeFetchPickingTaskRuleEnum;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingTaskAggsDao;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingTaskSendAggsDao;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodAggsDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingSendGoodAggsDto;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskAggsEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskSendAggsEntity;
import com.jd.bluedragon.utils.NumberHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 空铁提货统计层服务
 *
 * aggs write DB
 * aggs read redis
 *
 * @Author zhengchengfa
 * @Date 2023/12/6 20:16
 * @Description
 */
@Service
public class JyPickingTaskAggsServiceImpl implements JyPickingTaskAggsService{

    private static final Logger log = LoggerFactory.getLogger(JyPickingTaskAggsServiceImpl.class);

    @Autowired
    private JyPickingTaskAggsDao jyPickingTaskAggsDao;
    @Autowired
    private JyPickingTaskSendAggsDao jyPickingTaskSendAggsDao;
    @Autowired
    private JyPickingTaskAggsCacheService cacheService;
    @Autowired
    private JyPickingTaskAggsTransactionManager transactionManager;


    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }


    /**
     * 目前有三种维度统计
     * 1、提货任务BizId维度缓存A: 统计数据直接存储缓存，使用时直接读取redis使用
     * 2、提货机场维度：该统计数据无法按机场直接缓存记录，实际查询是按机场查最近时间范围的提货任务做聚合， 对提货任务list遍历读取redis-A缓存数据做聚合
     * 3、发货流向维度：无法直接redis存储，业务场景按照发货流向反推最近时间范围的提货任务集合，查该集合内发货统计， 新生成一个缓存B: 操作场地+发货流向+bizId任务维度
     *      统计时按提货任务list遍历取redis-B缓存数据做聚合
     *
     * 缓存加工逻辑两种方案：
     * 1、同步加工+异步刷数[异常数据+闭环节点触发刷数]  此方案需要考虑缓存读取加工在一个事务： 同步处理+1 -1 异步回刷走count
     * 2、直接触发异步计算，同步取缓存值后+1 -1  只能保证最终一致，中间过程是弱一致性
     *
     */
    @Override
    public void updatePickingGoodStatistics(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity entity) {
        Integer curSiteId = request.getCurrentOperate().getSiteCode();

        JyPickingTaskAggsEntity pickingTaskAggsEntity = new JyPickingTaskAggsEntity(curSiteId.longValue(), entity.getBizId());
        JyPickingTaskSendAggsEntity pickingTaskSendAggsEntity = new JyPickingTaskSendAggsEntity(curSiteId.longValue(), request.getNextSiteId().longValue(), entity.getBizId());
        transactionManager.updatePickingGoodAggs(pickingTaskAggsEntity, pickingTaskSendAggsEntity);

        this.taskPickingAggCache(request, resData, entity);

        //提货任务发货流向维度缓存
        if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
            this.taskPickingSendAggCache(request, resData, entity);
        }
    }

    /**
     * 提货任务维度缓存加工
     * @param request
     * @param resData
     * @param entity
     */
    private void taskPickingAggCache(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity entity) {
        Integer curSiteId = request.getCurrentOperate().getSiteCode();

        PickingGoodAggsDto aggDto = new PickingGoodAggsDto();
        PickingGoodAggsDto cacheAggDto = cacheService.getCacheTaskPickingAgg(curSiteId, entity.getBizId());
        if(Objects.isNull(cacheAggDto)) {
            cacheAggDto = new PickingGoodAggsDto();
            Integer waitPickingNum = 0;//todo zcf 需要确认待提取航空的还是运输的还是从表里聚合出来， 任务模型确认之后才能确定该逻辑
            cacheAggDto.setWaitPickingTotalNum(waitPickingNum);
            cacheAggDto.setRealPickingTotalNum(1);
            cacheAggDto.setMorePickingTotalNum(BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource()) ? 1 : 0);
        }else {
            cacheAggDto.setRealPickingTotalNum(cacheAggDto.getRealPickingTotalNum() + 1);
            if(BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource())) {
                Integer waitPickingNum = cacheAggDto.getWaitPickingTotalNum() - 1;
                if(!NumberHelper.gte(waitPickingNum, 0)) {
                    cacheAggDto.setWaitPickingTotalNum(0);
                    //todo zcf 这里待提数据出现负数时怀疑统计数据出现偏差，触发异步刷数（同任务完成）  如果考虑此种方案，需要保证redis缓存查改在一个事务
                }else {
                    cacheAggDto.setWaitPickingTotalNum(waitPickingNum);
                }
            }else {
                cacheAggDto.setMorePickingTotalNum(cacheAggDto.getMorePickingTotalNum() + 1);
            }
        }
        //提货任务维度缓存
        cacheService.saveCacheTaskPickingAgg(curSiteId, entity.getBizId(), aggDto);
    }

    /**
     * 发货流向维度提货任务维度缓存加工
     * @param request
     * @param resData
     * @param entity
     */
    private void taskPickingSendAggCache(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity entity) {
        Integer curSiteId = request.getCurrentOperate().getSiteCode();
        PickingSendGoodAggsDto aggDto = new PickingSendGoodAggsDto();
        PickingSendGoodAggsDto cacheAggDto = cacheService.getCacheTaskPickingSendAgg(curSiteId, request.getNextSiteId(), entity.getBizId());
        if(Objects.isNull(cacheAggDto)) {
            cacheAggDto = new PickingSendGoodAggsDto();
            Integer waitSendNum = 0;//todo zcf 明细加工完成之后才能知道最终待发数量
            cacheAggDto.setWaitSendTotalNum(waitSendNum);
            cacheAggDto.setRealSendTotalNum(1);
            cacheAggDto.setMoreSendTotalNum(BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource()) ? 1 : 0);
            cacheAggDto.setForceSendTotalNum(Boolean.TRUE.equals(request.getForceSendFlag()) ? 1 : 0);
        }else {
            cacheAggDto.setRealSendTotalNum(cacheAggDto.getRealSendTotalNum() + 1);
            if(BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource())) {
                Integer waitSendNum = cacheAggDto.getWaitSendTotalNum() - 1;
                if(NumberHelper.gte(waitSendNum, 0)) {
                    cacheAggDto.setWaitSendTotalNum(waitSendNum);
                }else {
                    cacheAggDto.setRealSendTotalNum(0);
                    //todo zcf 这里待发数据出现负数时怀疑统计数据出现偏差，触发异步刷数（同任务完成）  如果考虑此种方案，需要保证redis缓存查改在一个事务
                }
            }else {
                cacheAggDto.setMoreSendTotalNum(cacheAggDto.getMoreSendTotalNum() + 1);
            }
            if(Boolean.TRUE.equals(request.getForceSendFlag())) {
                cacheAggDto.setForceSendTotalNum(cacheAggDto.getForceSendTotalNum() + 1);
            }
        }
        cacheService.saveCacheTaskPickingSendAgg(curSiteId, request.getNextSiteId(), entity.getBizId(), aggDto);

    }


    @Override
    public PickingGoodAggsDto findTaskPickingAgg(Integer curSiteId, String bizId) {
        PickingGoodAggsDto aggsDto = cacheService.getCacheTaskPickingAgg(curSiteId, bizId);
        if(Objects.isNull(aggsDto)) {
            //必须保证查询之前该cache做好初始化，存在批量任务查询聚合，该redis不允许有查询为空时查DB兜底的逻辑,查询null时只能返回0保证服务可用
            logWarn("提货任务统计查询redis没有初始化，此处存在有批量调用，不允许查DB补偿, 所有统计数据为0保证可用，site={},bizId={}", curSiteId, bizId);
            aggsDto = new PickingGoodAggsDto();
        }
        return aggsDto;
    }

    @Override
    public PickingSendGoodAggsDto findTaskPickingSendAgg(Integer curSiteId, Integer nextSiteId, String bizId) {
        PickingSendGoodAggsDto aggsDto = cacheService.getCacheTaskPickingSendAgg(curSiteId, nextSiteId, bizId);
        if(Objects.isNull(aggsDto)) {
            //todo zcf 查DB返回并做redis续期
        }
        return aggsDto;
    }


}
