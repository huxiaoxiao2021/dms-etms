package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingGoodsReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.AirRailTaskCountDto;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingGoodsRes;
import com.jd.bluedragon.distribution.jy.constants.BarCodeFetchPickingTaskRuleEnum;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingTaskAggsDao;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingTaskSendAggsDao;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskAggsEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskSendAggsEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public static final Integer GET_LOCK_FAIL_CODE = 40001;
    public static final String GET_LOCK_FAIL_MSG = "获取锁失败";

    @Autowired
    private JyPickingTaskAggsDao jyPickingTaskAggsDao;
    @Autowired
    private JyPickingTaskSendAggsDao jyPickingTaskSendAggsDao;
    @Autowired
    private JyPickingTaskAggsCacheService cacheService;
    @Autowired
    private JyBizTaskPickingGoodService jyBizTaskPickingGoodService;
    @Autowired
    private JyPickingSendRecordService jyPickingSendRecordService;

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
     * 1、按提货任务agg统计
     * a: 待提件数统计 = 待提总数 - 待提已提件数
     * 待提总数：初始化生成
     * 待提已提件数：实际提货的待提包裹件数 + 实际提货的待提箱件数
     * b: 已提总数 = 待提已提件数 + 多提件数
     * c: 多提件数 = 多提包裹件数 + 多提箱件数
     *
     * 2、按流向待发数据agg统计
     * a: 待发件数统计 = 待发总件数 - 待发已发总件数
     * 待发总件数：初始化生成
     * 待发已发总件数： 实际提货发货的待发包裹件数 + 实际提货发货的待发箱件数
     * b: 实发总件数 = 待发已发总件数 + 多发总件数
     * c: 多发总件数 = 多发包裹件数 + 多发箱件数
     * d: 强发件数 = 强发包裹件数 + 强发箱件数 【目前场景不需要，DB定义了该字段，可暂时不做加工】
     *
     * 上面是提货任务维度统计
     * 下面提货发货流向统计
     * 1、流向内实发发货的需要提货的包裹件数
     * 2、流向内实发发货的需要提货的箱件数
     * 3、流向内实发发货的多货的包裹件数
     * 4、流向内实发发货的多货的箱件数
     */
    @Override
    public void saveCacheAggStatistics(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity entity) {
        String bizId = entity.getBizId();
        Long siteId = (long)request.getCurrentOperate().getSiteCode();

        if(BusinessUtil.isBoxcode(request.getBarCode())) {
            //实际提货的待提箱件数
            cacheService.incrRealScanWaitPickingBoxNum(bizId, siteId);
            if(!BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource())) {
                //多提箱件数
                cacheService.incrRealScanMorePickingBoxNum(bizId, siteId);
            }
            if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
                //实际提货发货的待提箱件数
//                cacheService.incrRealScanWaitSendBoxNum(bizId, siteId);
                //实际提货发货的待提箱的件数【流向维度】
                cacheService.incrRealScanFlowWaitSendBoxNum(bizId, siteId, request.getNextSiteId());

                if(!BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource())) {
                    //多提发的箱件数
//                    cacheService.incrRealScanMoreSendBoxNum(bizId, siteId);
                    //多提发的箱件数【流向维度】
                    cacheService.incrRealScanFlowMoreSendBoxNum(bizId, siteId, request.getNextSiteId());
                }
                //强发箱件数

            }
        }else if(WaybillUtil.isPackageCode(request.getBarCode())) {
            //实际提货的待提包裹件数
            cacheService.incrRealScanWaitPickingPackageNum(bizId, siteId);
            if(!BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource())) {
                //多提包裹件数
                cacheService.incrRealScanMorePickingPackageNum(bizId, siteId);
            }
            if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
                //实际提货发货的待提包裹件数
//                cacheService.incrRealScanWaitSendPackageNum(bizId, siteId);
                //实际提货发货的待提包裹件数【流向维度】
                cacheService.incrRealScanFlowWaitSendPackageNum(bizId, siteId, request.getNextSiteId());
                if(!BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource())) {
                    //多提发的包裹件数
//                    cacheService.incrRealScanMoreSendPackageNum(bizId, siteId);
                    //多提发的包裹件数【流向维度】
                    cacheService.incrRealScanFlowMoreSendPackageNum(bizId, siteId, request.getNextSiteId());
                }
                //强发包裹件数
            }
        }  else {
            logWarn("提货的类型非箱号、非包裹号。不做计算， request={},bizId={}", JsonHelper.toJson(request), entity.getBizId());
        }
    }


    @Override
    public JyPickingTaskAggsEntity findTaskPickingAgg(Long curSiteId, String bizId) {
        return jyPickingTaskAggsDao.findByBizId(bizId, curSiteId);
    }

    @Override
    public JyPickingTaskSendAggsEntity findTaskPickingSendAgg(Long curSiteId, Long nextSiteId, String bizId) {
        return jyPickingTaskSendAggsDao.findByBizIdAndNextSite(curSiteId, nextSiteId, bizId);
    }

//    @Override
//    public void aggRefresh(String bizId, Long nextSiteId) {
//        //todo zcf 回算agg重新梳理下逻辑
//        if(StringUtils.isBlank(bizId)) {
//            logWarn("提货任务统计回算:参数bizId为空,放弃回算");
//            return ;
//        }
//
//        JyBizTaskPickingGoodEntity entity = jyBizTaskPickingGoodService.findByBizIdWithYn(bizId, false);
//        if(Objects.isNull(entity)) {
//            logWarn("提货任务统计回算:根据bizId：{}查询提货任务为空,放弃回算", bizId);
//            return ;
//        }
//        if(PickingGoodStatusEnum.PICKING_COMPLETE.getCode().equals(entity.getStatus())) {
//            if(Objects.isNull(entity.getPickingCompleteTime())) {
//                logWarn("提货任务统计回算:根据bizId：{}查询提货任务已经完成但是没有完成时间,放弃回算", bizId);
//                return ;
//            }
//            if(DateHelper.betweenHours(entity.getPickingCompleteTime(), new Date()) > 24) {
//                logWarn("提货任务统计回算:根据bizId：{}查询提货任务已经完成超过24小时,不支持回算", bizId);
//                return ;
//            }
//        }
//
//        if(!cacheService.saveLockPickingGoodTask(bizId)) {
//            logWarn("提货任务统计回算获取锁失败，bizId={}", bizId);
//            throw new JyBizException("未获取到锁:" + bizId);
//        }
//        try{
//            PickingGoodTaskStatisticsDto statisticsDto = jyPickingSendRecordService.statisticsByBizId(entity.getNextSiteId(), bizId, nextSiteId);
//            aggTransactionManager.updatePickingGoodAggs(statisticsDto);
//        }catch (Exception e) {
//            log.error("提货任务统计回算服务异常，bizId={}， errMsg={}", bizId, e.getMessage(), e);
//            throw new JyBizException("提货任务统计回算服务异常：" + bizId);
//        }finally {
//            cacheService.unlockLockPickingGoodTask(bizId);
//        }
//    }

    @Override
    public List<PickingSendGoodAggsDto> waitPickingInitTotalNum(List<String> bizIdList, Long siteId, Long sendNextSiteId) {
        List<PickingSendGoodAggsDto> res = new ArrayList<>();
        List<String> nullCacheBizId = new ArrayList<>();

        bizIdList.forEach(bizId -> {
            Integer num = null;
            if(Objects.isNull(sendNextSiteId)) {
                num = cacheService.getCacheInitWaitPickingTotalItemNum(bizId, siteId);
            }else {
                num = cacheService.getCacheInitWaitSendTotalItemNum(bizId, siteId, sendNextSiteId);
            }
            if(NumberHelper.gt0(num)) {
                PickingSendGoodAggsDto aggs = new PickingSendGoodAggsDto();
                aggs.setBizId(bizId);
                aggs.setWaitSendTotalNum(num);
                res.add(aggs);
            }else {
                nullCacheBizId.add(bizId);
            }
        });

        if(!CollectionUtils.isEmpty(nullCacheBizId)) {
            if(Objects.isNull(sendNextSiteId)) {
                List<JyPickingTaskAggsEntity> pickingAggsEntityList = jyPickingTaskAggsDao.findByBizIdList(nullCacheBizId, siteId);
                pickingAggsEntityList.forEach(entity -> {
                    PickingSendGoodAggsDto aggs = new PickingSendGoodAggsDto();
                    aggs.setBizId(entity.getBizId());
                    aggs.setWaitSendTotalNum(entity.getWaitScanTotalCount());
                    res.add(aggs);
                    cacheService.saveCacheInitWaitPickingTotalItemNum(entity.getBizId(), siteId, entity.getWaitScanTotalCount());
                });
            }else {
                List<JyPickingTaskSendAggsEntity> sendAggsEntityList = jyPickingTaskSendAggsDao.findByBizIdList(nullCacheBizId, siteId, sendNextSiteId);
                sendAggsEntityList.forEach(entity -> {
                    PickingSendGoodAggsDto aggs = new PickingSendGoodAggsDto();
                    aggs.setBizId(entity.getBizId());
                    aggs.setWaitSendTotalNum(entity.getWaitScanTotalCount());
                    res.add(aggs);
                    cacheService.saveCacheInitWaitSendTotalItemNum(entity.getBizId(), siteId, sendNextSiteId, entity.getWaitScanTotalCount());
                });
            }

        }
        return res;
    }

    @Override
    public List<String> pageRecentWaitScanEqZero(JyPickingTaskAggQueryDto queryDto) {
        return jyPickingTaskAggsDao.pageRecentWaitScanEqZero(queryDto);
    }

    @Override
    public List<PickingSendGoodAggsDto> findPickingAgg(List<String> bizIdList, Long siteId, Long sendNextSiteId) {
        List<PickingSendGoodAggsDto> res = this.waitPickingInitTotalNum(bizIdList, siteId, sendNextSiteId);
        res.forEach(pickingSendDto -> {
            pickingSendDto.setRealSendTotalNum(this.getRealPickingTotalNum(pickingSendDto.getBizId(), siteId, sendNextSiteId));
            pickingSendDto.setMoreSendTotalNum(this.getRealPickingMoreScanTotalNum(pickingSendDto.getBizId(), siteId, sendNextSiteId));
        });
        return res;
    }
    //实际扫描总件数
    private int getRealPickingTotalNum(String bizId, Long siteId, Long sendNextSiteId) {
        return this.getRealPickingHandoverScanTotalNum(bizId, siteId, sendNextSiteId)
                + this.getRealPickingMoreScanTotalNum(bizId, siteId, sendNextSiteId);
    }
    //实际扫描应交接总件数
    private int getRealPickingHandoverScanTotalNum(String bizId, Long siteId, Long sendNextSiteId) {
        Integer handoverPackageNum;
        Integer handoverBoxNum;
        if(!Objects.isNull(sendNextSiteId)) {
            handoverPackageNum = cacheService.getValueRealScanFlowWaitSendPackageNum(bizId, siteId, sendNextSiteId);
            handoverBoxNum = cacheService.getValueRealScanFlowWaitSendBoxNum(bizId, siteId, sendNextSiteId);
        }else {
            handoverPackageNum = cacheService.getValueRealScanWaitPickingPackageNum(bizId, siteId);
            handoverBoxNum = cacheService.getValueRealScanWaitPickingBoxNum(bizId, siteId);
        }
        return handoverPackageNum + handoverBoxNum;
    }
    //实际扫描多扫总件数
    private int getRealPickingMoreScanTotalNum(String bizId, Long siteId, Long sendNextSiteId) {
        if(!Objects.isNull(sendNextSiteId)) {
            Integer moreScanPackageNum = cacheService.getValueRealScanFlowMoreSendPackageNum(bizId, siteId, sendNextSiteId);
            Integer moreScanBoxNum = cacheService.getValueRealScanFlowMoreSendBoxNum(bizId, siteId, sendNextSiteId);
            return moreScanPackageNum + moreScanBoxNum;
        }else {
            Integer handoverPackageNum = cacheService.getValueRealScanMorePickingPackageNum(bizId, siteId);
            Integer handoverBoxNum = cacheService.getValueRealScanMorePickingBoxNum(bizId, siteId);
            return handoverPackageNum + handoverBoxNum;
        }
    }

    @Override
    public void updatePickingAggWaitScanItemNum(CalculateWaitPickingItemNumDto paramDto) {
        JyPickingTaskAggsEntity entity = jyPickingTaskAggsDao.findByBizId(paramDto.getBizId(), paramDto.getPickingSiteId());
        Integer totalNum;
        if(Objects.isNull(entity)) {
            totalNum = paramDto.getWaitPickingItemNum();
            JyPickingTaskAggsEntity insertEntity = new JyPickingTaskAggsEntity(paramDto.getPickingSiteId(), paramDto.getBizId());
            insertEntity.setCreateTime(new Date());
            insertEntity.setUpdateTime(insertEntity.getCreateTime());
            insertEntity.setWaitScanTotalCount(totalNum);
            jyPickingTaskAggsDao.insertSelective(insertEntity);
        }else {
            totalNum = entity.getWaitScanTotalCount() + paramDto.getWaitPickingItemNum();
            jyPickingTaskAggsDao.updatePickingAggWaitScanItemNum(paramDto.getBizId(), paramDto.getPickingSiteId(), totalNum);
        }
        cacheService.saveCacheInitWaitPickingTotalItemNum(paramDto.getBizId(), paramDto.getPickingSiteId(), totalNum);

    }

    @Override
    public void updatePickingSendAggWaitScanItemNum(CalculateWaitPickingItemNumDto paramDto) {
        JyPickingTaskSendAggsEntity entity = jyPickingTaskSendAggsDao.findByBizIdAndNextSite(paramDto.getPickingSiteId(),  paramDto.getNextSiteId(), paramDto.getBizId());
        Integer totalNum;
        if(Objects.isNull(entity)) {
            totalNum = paramDto.getWaitPickingItemNum();
            JyPickingTaskSendAggsEntity insertEntity = new JyPickingTaskSendAggsEntity(paramDto.getPickingSiteId(),  paramDto.getNextSiteId(), paramDto.getBizId());
            insertEntity.setCreateTime(new Date());
            insertEntity.setUpdateTime(insertEntity.getCreateTime());
            insertEntity.setWaitScanTotalCount(totalNum);
            jyPickingTaskSendAggsDao.insertSelective(insertEntity);
        }else {
            totalNum = entity.getWaitScanTotalCount() + paramDto.getWaitPickingItemNum();
            jyPickingTaskSendAggsDao.updatePickingAggWaitScanItemNum(paramDto.getPickingSiteId(),  paramDto.getNextSiteId(), paramDto.getBizId(), totalNum);
        }
        cacheService.saveCacheInitWaitSendTotalItemNum(paramDto.getBizId(), paramDto.getPickingSiteId(),  paramDto.getNextSiteId(), totalNum);
    }

    @Override
    public void updatePickingAggScanStatistics(JyPickingGoodScanDto param) {
        boolean moreScanFlag = Boolean.TRUE.equals(param.getMoreScanFlag());
        JyPickingTaskAggsEntity aggDtoRes = this.findTaskPickingAgg(param.getSiteId(), param.getBizId());
        if(Objects.isNull(aggDtoRes)) {
            log.error("理论上不会出现暂时不考虑补偿，扫描前初始化会前置插入，提货统计param={}", JsonHelper.toJson(param));
            return;
        }
        JyPickingTaskAggsEntity updateEntity = new JyPickingTaskAggsEntity(param.getSiteId(), param.getBizId());
        if(BusinessUtil.isBoxcode(param.getBarCode())) {
            if(moreScanFlag) {
                updateEntity.setMoreScanBoxCount(aggDtoRes.getMoreScanBoxCount() + 1);
            }else {
                updateEntity.setHandoverScanBoxCount(aggDtoRes.getHandoverScanBoxCount() + 1);
            }
            updateEntity.setScanBoxCount(updateEntity.getScanBoxCount() + 1);
        }else {
            if(moreScanFlag) {
                updateEntity.setMoreScanPackageCount(aggDtoRes.getMoreScanPackageCount() + 1);
            }else {
                updateEntity.setHandoverScanPackageCount(aggDtoRes.getHandoverScanPackageCount() + 1);
            }
            updateEntity.setScanPackageCount(updateEntity.getScanPackageCount() + 1);
        }
        updateEntity.setScanTotalCount(updateEntity.getScanTotalCount() + 1);
        updateEntity.setUpdateTime(new Date());

        if(Boolean.TRUE.equals(param.getSendGoodFlag())) {
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                if(moreScanFlag) {
                    updateEntity.setMoreSendBoxCount(aggDtoRes.getMoreSendBoxCount() + 1);
                }
                updateEntity.setSendBoxCount(updateEntity.getSendBoxCount() + 1);
            }else {
                if(moreScanFlag) {
                    updateEntity.setMoreSendPackageCount(aggDtoRes.getMoreSendPackageCount() + 1);
                }
                updateEntity.setSendPackageCount(updateEntity.getSendPackageCount() + 1);
            }
        }
        jyPickingTaskAggsDao.updateScanStatistics(updateEntity);
        logInfo("提货任务维度agg实操统计修改bizId={}，修改前={}，修改后计数={}", param.getBizId(), JsonHelper.toJson(aggDtoRes), JsonHelper.toJson(updateEntity));

    }

    @Override
    public void updatePickingSendAggScanStatistics(JyPickingGoodScanDto param) {
        boolean moreScanFlag = Boolean.TRUE.equals(param.getMoreScanFlag());
        JyPickingTaskSendAggsEntity aggDtoRes = this.findTaskPickingSendAgg(param.getSiteId(), param.getNextSiteId(), param.getBizId());
        if(Objects.isNull(aggDtoRes)) {
            log.error("理论上不会出现暂时不考虑补偿，扫描前初始化会前置插入，提货发货统计param={}", JsonHelper.toJson(param));
            return;
        }
        JyPickingTaskSendAggsEntity updateEntity = new JyPickingTaskSendAggsEntity(param.getSiteId(), param.getNextSiteId(), param.getBizId());
        if(BusinessUtil.isBoxcode(param.getBarCode())) {
            if(moreScanFlag) {
                updateEntity.setMoreScanBoxCount(aggDtoRes.getMoreScanBoxCount() + 1);
            }else {
                updateEntity.setHandoverScanBoxCount(aggDtoRes.getHandoverScanBoxCount() + 1);
            }
            updateEntity.setScanBoxCount(updateEntity.getScanBoxCount() + 1);
        }else {
            if(moreScanFlag) {
                updateEntity.setMoreScanPackageCount(aggDtoRes.getMoreScanPackageCount() + 1);
            }else {
                updateEntity.setHandoverScanPackageCount(aggDtoRes.getHandoverScanPackageCount() + 1);
            }
            updateEntity.setScanPackageCount(updateEntity.getScanPackageCount() + 1);
        }
        updateEntity.setScanTotalCount(updateEntity.getScanTotalCount() + 1);
        updateEntity.setUpdateTime(new Date());

        if(Boolean.TRUE.equals(param.getSendGoodFlag())) {
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                if(moreScanFlag) {
                    updateEntity.setMoreSendBoxCount(aggDtoRes.getMoreSendBoxCount() + 1);
                }
                updateEntity.setSendBoxCount(updateEntity.getSendBoxCount() + 1);
            }else {
                if(moreScanFlag) {
                    updateEntity.setMoreSendPackageCount(aggDtoRes.getMoreSendPackageCount() + 1);
                }
                updateEntity.setSendPackageCount(updateEntity.getSendPackageCount() + 1);
            }
        }
        jyPickingTaskSendAggsDao.updateScanStatistics(updateEntity);
        logInfo("提货发货任务流向维度agg实操统计修改bizId={}，nextSiteId={}，修改前={}，修改后计数={}", param.getBizId(), param.getNextSiteId(), JsonHelper.toJson(aggDtoRes), JsonHelper.toJson(updateEntity));

    }
}
