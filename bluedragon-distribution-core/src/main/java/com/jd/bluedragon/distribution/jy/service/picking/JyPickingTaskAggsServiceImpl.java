package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingGoodsReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingGoodsRes;
import com.jd.bluedragon.distribution.jy.constants.BarCodeFetchPickingTaskRuleEnum;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingTaskAggsDao;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingTaskSendAggsDao;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.CalculateWaitPickingItemNumDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingGoodScanDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingTaskAggQueryDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingSendGoodAggsDto;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskAggsEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskSendAggsEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private JyPickingTaskAggsCacheService jyPickingTaskAggsCacheService;
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
     * 1、按提货任务agg统计 todo 该方法涉及多个redis自增，非幂等
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
     * c: 多发总件数 = 多发包裹件数 + 多发箱件数  (强发)
     *
     * 上面是提货任务维度统计
     * 下面提货发货流向统计
     * 1、流向内实发发货的需要提货的包裹件数
     * 2、流向内实发发货的需要提货的箱件数
     * 3、流向内实发发货的多发货的包裹件数
     * 4、流向内实发发货的多发货的箱件数
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyPickingTaskAggsServiceImpl.saveCacheAggStatistics",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public void saveCacheAggStatistics(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity entity) {
        String bizId = entity.getBizId();
        Long siteId = (long)request.getCurrentOperate().getSiteCode();

        if(BusinessUtil.isBoxcode(request.getBarCode())) {
            Integer morePickingBox = 0;
            Integer handoverPickingBox = 0;
            Integer moreSendBox = 0;
            Integer handoverSendBox = 0;
            if(!BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource())) {
                //多提箱件数
                morePickingBox = jyPickingTaskAggsCacheService.incrRealScanMorePickingBoxNum(bizId, siteId);
            }else {
                //交接提箱件数
                handoverPickingBox = jyPickingTaskAggsCacheService.incrRealScanWaitPickingBoxNum(bizId, siteId);
            }
            if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
                if(Boolean.TRUE.equals(request.getForceSendFlag())) {
                    //多发的箱件数【流向维度】
                    moreSendBox = jyPickingTaskAggsCacheService.incrRealScanFlowMoreSendBoxNum(bizId, siteId, request.getNextSiteId());
                }else {
                    //交接提发箱的件数【流向维度】
                    handoverSendBox = jyPickingTaskAggsCacheService.incrRealScanFlowWaitSendBoxNum(bizId, siteId, request.getNextSiteId());
                }
                //强发箱件数
                logInfo("提货箱件数redis计数加工，bizId={},多提箱件数={}，交接提箱件数={}，发货流向为{}，多发箱件数={}，交接发箱件数={}",
                        bizId, morePickingBox, handoverPickingBox, request.getNextSiteId(), moreSendBox, handoverSendBox);
            }
        }else if(WaybillUtil.isPackageCode(request.getBarCode())) {
            Integer morePickingPackage = 0;
            Integer handoverPickingPackage = 0;
            Integer moreSendPackage = 0;
            Integer handoverSendPackage = 0;
            if(!BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource())) {
                //多提包裹件数
                morePickingPackage = jyPickingTaskAggsCacheService.incrRealScanMorePickingPackageNum(bizId, siteId);
            }else {
                //交接提包裹件数
                handoverPickingPackage = jyPickingTaskAggsCacheService.incrRealScanWaitPickingPackageNum(bizId, siteId);
            }
            if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
                if(Boolean.TRUE.equals(request.getForceSendFlag())) {
                    //多发的包裹件数【流向维度】
                    moreSendPackage = jyPickingTaskAggsCacheService.incrRealScanFlowMoreSendPackageNum(bizId, siteId, request.getNextSiteId());
                }else {
                    //交接提发的包裹件数【流向维度】
                    handoverSendPackage = jyPickingTaskAggsCacheService.incrRealScanFlowWaitSendPackageNum(bizId, siteId, request.getNextSiteId());
                }
                //强发包裹件数
            }
            logInfo("提货包裹件数redis计数加工，bizId={},多提包裹件数={}，交接提包裹件数={}，发货流向为{}，多发包裹件数={}，交接发包裹件数={}",
                    bizId, morePickingPackage, handoverPickingPackage, request.getNextSiteId(), moreSendPackage, handoverSendPackage);

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

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyPickingTaskAggsServiceImpl.waitPickingInitTotalNum",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<PickingSendGoodAggsDto> waitPickingInitTotalNum(List<String> bizIdList, Long siteId, Long sendNextSiteId) {
        List<PickingSendGoodAggsDto> res = new ArrayList<>();
        List<String> nullCacheBizIdList = new ArrayList<>();

        bizIdList.forEach(bizId -> {
            Integer num = null;
            if(Objects.isNull(sendNextSiteId)) {
                num = jyPickingTaskAggsCacheService.getCacheInitWaitPickingTotalItemNum(bizId, siteId);
            }else {
                num = jyPickingTaskAggsCacheService.getCacheInitWaitSendTotalItemNum(bizId, siteId, sendNextSiteId);
            }
            if(NumberHelper.gt0(num)) {
                PickingSendGoodAggsDto aggs = new PickingSendGoodAggsDto();
                aggs.setBizId(bizId);
                aggs.setWaitSendTotalNum(num);
                res.add(aggs);
            }else {
                nullCacheBizIdList.add(bizId);
            }
        });
        if(!CollectionUtils.isEmpty(nullCacheBizIdList)) {
            //剔除自建任务ID,自建任务没有待扫
            List<String> manualCreateTaskBizIds = jyBizTaskPickingGoodService.findManualCreateTaskBizIds(nullCacheBizIdList);
            if(!CollectionUtils.isEmpty(manualCreateTaskBizIds)) {
                nullCacheBizIdList.removeAll(manualCreateTaskBizIds);
            }
        }
        if(!CollectionUtils.isEmpty(nullCacheBizIdList)) {
            if(Objects.isNull(sendNextSiteId)) {
                List<JyPickingTaskAggsEntity> pickingAggsEntityList = jyPickingTaskAggsDao.findByBizIdList(nullCacheBizIdList, siteId);
                pickingAggsEntityList.forEach(entity -> {
                    PickingSendGoodAggsDto aggs = new PickingSendGoodAggsDto();
                    aggs.setBizId(entity.getBizId());
                    aggs.setWaitSendTotalNum(entity.getWaitScanTotalCount());
                    res.add(aggs);
                    jyPickingTaskAggsCacheService.saveCacheInitWaitPickingTotalItemNum(entity.getBizId(), siteId, entity.getWaitScanTotalCount());
                    logInfo("任务维度待提件数agg查询同步到redis计数加工：bizId={},num={}", entity.getBizId(), entity.getWaitScanTotalCount());
                });
            }else {
                List<JyPickingTaskSendAggsEntity> sendAggsEntityList = jyPickingTaskSendAggsDao.findByBizIdList(nullCacheBizIdList, siteId, sendNextSiteId);
                sendAggsEntityList.forEach(entity -> {
                    PickingSendGoodAggsDto aggs = new PickingSendGoodAggsDto();
                    aggs.setBizId(entity.getBizId());
                    aggs.setWaitSendTotalNum(entity.getWaitScanTotalCount());
                    res.add(aggs);
                    jyPickingTaskAggsCacheService.saveCacheInitWaitSendTotalItemNum(entity.getBizId(), siteId, sendNextSiteId, entity.getWaitScanTotalCount());
                    logInfo("流向维度待提件数agg查询同步到redis计数加工：bizId={},nextSiteId={}, num={}", entity.getBizId(), sendNextSiteId, entity.getWaitScanTotalCount());

                });
            }

        }

        List<String> bizListSource = new ArrayList<>(bizIdList);
        List<String> existWaitScanBizIdList = res.stream().map(PickingSendGoodAggsDto::getBizId).collect(Collectors.toList());
        bizListSource.removeAll(existWaitScanBizIdList);
        bizListSource.forEach(bizId -> {
            PickingSendGoodAggsDto aggs = new PickingSendGoodAggsDto();
            aggs.setBizId(bizId);
            aggs.setWaitSendTotalNum(0);
            res.add(aggs);
        });
        return res;
    }

    @Override
    public List<String> filterRecentWaitScanEqZeroBiz(JyPickingTaskAggQueryDto queryDto) {
        return jyPickingTaskAggsDao.filterRecentWaitScanEqZeroBiz(queryDto);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyPickingTaskAggsServiceImpl.findPickingAgg",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<PickingSendGoodAggsDto> findPickingAgg(List<String> bizIdList, Long siteId, Long sendNextSiteId) {
        logInfo("findPickingAgg提货岗统计bizId.size={}", CollectionUtils.isEmpty(bizIdList) ? 0 : bizIdList.size());
        List<PickingSendGoodAggsDto> res = this.waitPickingInitTotalNum(bizIdList, siteId, sendNextSiteId);
        res.forEach(pickingSendDto -> {
            Integer handoverScanTotalNum = this.getRealPickingHandoverScanTotalNum(pickingSendDto.getBizId(), siteId, sendNextSiteId);
            Integer moreScanTotalNum = this.getRealPickingMoreScanTotalNum(pickingSendDto.getBizId(), siteId, sendNextSiteId);
            Integer initShouldScanTotalNum = pickingSendDto.getWaitSendTotalNum();
            Integer waitScanTotalNum = initShouldScanTotalNum - handoverScanTotalNum >= 0 ? initShouldScanTotalNum - handoverScanTotalNum : 0;

            pickingSendDto.setWaitSendTotalNum(waitScanTotalNum);
            pickingSendDto.setRealSendTotalNum(handoverScanTotalNum + moreScanTotalNum);
            pickingSendDto.setMoreSendTotalNum(moreScanTotalNum);
        });
        return res;
    }
    //实际扫描应交接总件数
    private int getRealPickingHandoverScanTotalNum(String bizId, Long siteId, Long sendNextSiteId) {
        Integer handoverPackageNum;
        Integer handoverBoxNum;
        if(!Objects.isNull(sendNextSiteId)) {
            handoverPackageNum = jyPickingTaskAggsCacheService.getValueRealScanFlowWaitSendPackageNum(bizId, siteId, sendNextSiteId);
            handoverBoxNum = jyPickingTaskAggsCacheService.getValueRealScanFlowWaitSendBoxNum(bizId, siteId, sendNextSiteId);
        }else {
            handoverPackageNum = jyPickingTaskAggsCacheService.getValueRealScanWaitPickingPackageNum(bizId, siteId);
            handoverBoxNum = jyPickingTaskAggsCacheService.getValueRealScanWaitPickingBoxNum(bizId, siteId);
        }
        return handoverPackageNum + handoverBoxNum;
    }
    //实际扫描多扫总件数
    private int getRealPickingMoreScanTotalNum(String bizId, Long siteId, Long sendNextSiteId) {
        if(!Objects.isNull(sendNextSiteId)) {
            Integer moreScanPackageNum = jyPickingTaskAggsCacheService.getValueRealScanFlowMoreSendPackageNum(bizId, siteId, sendNextSiteId);
            Integer moreScanBoxNum = jyPickingTaskAggsCacheService.getValueRealScanFlowMoreSendBoxNum(bizId, siteId, sendNextSiteId);
            return moreScanPackageNum + moreScanBoxNum;
        }else {
            Integer handoverPackageNum = jyPickingTaskAggsCacheService.getValueRealScanMorePickingPackageNum(bizId, siteId);
            Integer handoverBoxNum = jyPickingTaskAggsCacheService.getValueRealScanMorePickingBoxNum(bizId, siteId);
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
        jyPickingTaskAggsCacheService.saveCacheInitWaitPickingTotalItemNum(paramDto.getBizId(), paramDto.getPickingSiteId(), totalNum);
        logInfo("任务维度待提件数初始化到redis计数加工：bizId={},num={}, 批次号={}", paramDto.getBizId(), totalNum, paramDto.getCode());


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

        //todo zcf 考虑agg待提实操加工一个方法，避免多处加工逻辑
        jyPickingTaskAggsCacheService.saveCacheInitWaitSendTotalItemNum(paramDto.getBizId(), paramDto.getPickingSiteId(),  paramDto.getNextSiteId(), totalNum);
        logInfo("流向维度待提件数初始化到redis计数加工：bizId={}, nextSiteId={}, num={}, 批次号={}", paramDto.getBizId(), paramDto.getNextSiteId(), totalNum, paramDto.getCode());

    }

    @Override
    public void updatePickingAggScanStatistics(JyPickingGoodScanDto param) {
        JyPickingTaskAggsEntity aggDtoRes = this.findTaskPickingAgg(param.getPickingSiteId(), param.getBizId());
        if(Objects.isNull(aggDtoRes)) {
            this.scanInsertPickingStatistics(param);
        }else {
            this.scanUpdatePickingStatistics(param, aggDtoRes);
        }
    }

    private void scanInsertPickingStatistics(JyPickingGoodScanDto param){
        boolean moreScanFlag = Boolean.TRUE.equals(param.getMoreScanFlag());
        JyPickingTaskAggsEntity insertEntity = new JyPickingTaskAggsEntity(param.getPickingSiteId(), param.getBizId());
        insertEntity.setPickingNodeCode(param.getEndNodeCode());
        insertEntity.setHandoverScanPackageCount(0);
        insertEntity.setHandoverScanBoxCount(0);
        insertEntity.setHandoverScanTotalCount(0);
        insertEntity.setScanPackageTotalCount(0);
        insertEntity.setScanBoxTotalCount(0);
        insertEntity.setScanTotalCount(0);
        insertEntity.setMoreScanPackageCount(0);
        insertEntity.setMoreScanBoxCount(0);
        insertEntity.setMoreScanTotalCount(0);
        insertEntity.setSendPackageCount(0);
        insertEntity.setSendBoxCount(0);
        insertEntity.setSendTotalCount(0);
        insertEntity.setMoreSendBoxCount(0);
        insertEntity.setMoreSendPackageCount(0);
        insertEntity.setMoreSendTotalCount(0);

        if(!moreScanFlag) {
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                insertEntity.setHandoverScanBoxCount(1);
            }else {
                insertEntity.setHandoverScanPackageCount(1);
            }
            insertEntity.setHandoverScanTotalCount(insertEntity.getHandoverScanBoxCount() + insertEntity.getHandoverScanPackageCount());
        }
        if(moreScanFlag) {
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                insertEntity.setMoreScanBoxCount(1);
            }else {
                insertEntity.setMoreScanPackageCount(1);
            }
            insertEntity.setMoreScanTotalCount(insertEntity.getMoreScanBoxCount() + insertEntity.getMoreScanPackageCount());
        }
        //实扫提货统计【总提货=交际提+已提】
        insertEntity.setScanBoxTotalCount(insertEntity.getHandoverScanBoxCount() + insertEntity.getMoreScanBoxCount());
        insertEntity.setScanPackageTotalCount(insertEntity.getHandoverScanPackageCount() + insertEntity.getMoreScanPackageCount());
        insertEntity.setScanTotalCount(insertEntity.getScanBoxTotalCount() + insertEntity.getScanPackageTotalCount());

        //提货并发货统计发货
        if(Boolean.TRUE.equals(param.getSendGoodFlag())) {

            //扫描发货统计
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                insertEntity.setSendBoxCount(1);
            }else {
                insertEntity.setSendPackageCount(1);
            }
            insertEntity.setSendTotalCount(insertEntity.getSendBoxCount() + insertEntity.getSendPackageCount());

            //多扫发货统计
            if(Boolean.TRUE.equals(param.getForceSendFlag())) {
                if(BusinessUtil.isBoxcode(param.getBarCode())) {
                    insertEntity.setMoreSendBoxCount(1);
                }else {
                    insertEntity.setMoreSendPackageCount(1);
                }
                insertEntity.setMoreSendTotalCount(insertEntity.getMoreSendBoxCount() + insertEntity.getMoreSendPackageCount());
            }
            //强发统计
        }
        insertEntity.setCreateTime(new Date());
        insertEntity.setUpdateTime(insertEntity.getCreateTime());
        jyPickingTaskAggsDao.insertSelective(insertEntity);
        logInfo("提货任务维度agg实操统计插入barCode={}，insertEntity={}", param.getBarCode(), JsonHelper.toJson(insertEntity));

    }

   private void scanUpdatePickingStatistics(JyPickingGoodScanDto param, JyPickingTaskAggsEntity aggDtoRes){
        boolean moreScanFlag = Boolean.TRUE.equals(param.getMoreScanFlag());

        JyPickingTaskAggsEntity updateEntity = new JyPickingTaskAggsEntity(param.getPickingSiteId(), param.getBizId());
        BeanUtils.copyProperties(aggDtoRes, updateEntity);

        updateEntity.setUpdateTime(new Date());

        //交接扫描提货统计
        if(!moreScanFlag) {
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                updateEntity.setHandoverScanBoxCount(aggDtoRes.getHandoverScanBoxCount() + 1);
            }else {
                updateEntity.setHandoverScanPackageCount(aggDtoRes.getHandoverScanPackageCount() + 1);
            }
            updateEntity.setHandoverScanTotalCount(updateEntity.getHandoverScanBoxCount() + updateEntity.getHandoverScanPackageCount());
        }

        //多扫提货统计
        if(moreScanFlag) {
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                updateEntity.setMoreScanBoxCount(aggDtoRes.getMoreScanBoxCount() + 1);
            }else {
                updateEntity.setMoreScanPackageCount(aggDtoRes.getMoreScanPackageCount() + 1);
            }
            updateEntity.setMoreScanTotalCount(updateEntity.getMoreScanBoxCount() + updateEntity.getMoreScanPackageCount());
        }

        //实扫提货统计【总提货=交际提+已提】
        updateEntity.setScanBoxTotalCount(updateEntity.getHandoverScanBoxCount() + updateEntity.getMoreScanBoxCount());
        updateEntity.setScanPackageTotalCount(updateEntity.getHandoverScanPackageCount() + updateEntity.getMoreScanPackageCount());
        updateEntity.setScanTotalCount(updateEntity.getScanBoxTotalCount() + updateEntity.getScanPackageTotalCount());

        //提货并发货统计发货
        if(Boolean.TRUE.equals(param.getSendGoodFlag())) {

            //扫描发货统计
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                updateEntity.setSendBoxCount(aggDtoRes.getSendBoxCount() + 1);
            }else {
                updateEntity.setSendPackageCount(aggDtoRes.getSendPackageCount() + 1);
            }
            updateEntity.setSendTotalCount(updateEntity.getSendBoxCount() + updateEntity.getSendPackageCount());

            //多扫发货统计
            if(Boolean.TRUE.equals(param.getForceSendFlag())) {
                if(BusinessUtil.isBoxcode(param.getBarCode())) {
                    updateEntity.setMoreSendBoxCount(aggDtoRes.getMoreSendBoxCount() + 1);
                }else {
                    updateEntity.setMoreSendPackageCount(aggDtoRes.getMoreSendPackageCount() + 1);
                }
                updateEntity.setMoreSendTotalCount(updateEntity.getMoreSendBoxCount() + updateEntity.getMoreSendPackageCount());
            }

            //强发统计
        }

        jyPickingTaskAggsDao.updateScanStatistics(updateEntity);
        logInfo("提货任务维度agg实操统计修改bizId={}，修改前={}，修改后计数={}", param.getBizId(), JsonHelper.toJson(aggDtoRes), JsonHelper.toJson(updateEntity));
    }

    @Override
    public void updatePickingSendAggScanStatistics(JyPickingGoodScanDto param) {
        JyPickingTaskSendAggsEntity aggDtoRes = this.findTaskPickingSendAgg(param.getPickingSiteId(), param.getNextSiteId(), param.getBizId());
        if(Objects.isNull(aggDtoRes)) {
            this.scanInsertPickingSendStatistics(param);
        }else {
            this.scanUpdatePickingSendStatistics(param, aggDtoRes);
        }
    }

    private void scanInsertPickingSendStatistics(JyPickingGoodScanDto param){
        boolean moreScanFlag = Boolean.TRUE.equals(param.getMoreScanFlag());

        JyPickingTaskSendAggsEntity insertEntity = new JyPickingTaskSendAggsEntity(param.getPickingSiteId(), param.getNextSiteId(), param.getBizId());
        insertEntity.setPickingNodeCode(param.getEndNodeCode());
        insertEntity.setHandoverScanPackageCount(0);
        insertEntity.setHandoverScanBoxCount(0);
        insertEntity.setHandoverScanTotalCount(0);
        insertEntity.setScanPackageTotalCount(0);
        insertEntity.setScanBoxTotalCount(0);
        insertEntity.setScanTotalCount(0);
        insertEntity.setMoreScanPackageCount(0);
        insertEntity.setMoreScanBoxCount(0);
        insertEntity.setMoreScanTotalCount(0);
        insertEntity.setSendPackageCount(0);
        insertEntity.setSendBoxCount(0);
        insertEntity.setSendTotalCount(0);
        insertEntity.setMoreSendBoxCount(0);
        insertEntity.setMoreSendPackageCount(0);
        insertEntity.setMoreSendTotalCount(0);


        if(!moreScanFlag) {
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                insertEntity.setHandoverScanBoxCount(1);
            }else {
                insertEntity.setHandoverScanPackageCount(1);
            }
            insertEntity.setHandoverScanTotalCount(insertEntity.getHandoverScanBoxCount() + insertEntity.getHandoverScanPackageCount());
        }
        if(moreScanFlag) {
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                insertEntity.setMoreScanBoxCount(1);
            }else {
                insertEntity.setMoreScanPackageCount(1);
            }
            insertEntity.setMoreScanTotalCount(insertEntity.getMoreScanBoxCount() + insertEntity.getMoreScanPackageCount());
        }
        //实扫提货统计【总提货=交际提+已提】
        insertEntity.setScanBoxTotalCount(insertEntity.getHandoverScanBoxCount() + insertEntity.getMoreScanBoxCount());
        insertEntity.setScanPackageTotalCount(insertEntity.getHandoverScanPackageCount() + insertEntity.getMoreScanPackageCount());
        insertEntity.setScanTotalCount(insertEntity.getScanBoxTotalCount() + insertEntity.getScanPackageTotalCount());

        //提货并发货统计发货
        if(Boolean.TRUE.equals(param.getSendGoodFlag())) {

            //扫描发货统计
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                insertEntity.setSendBoxCount(1);
            }else {
                insertEntity.setSendPackageCount(1);
            }
            insertEntity.setSendTotalCount(insertEntity.getSendBoxCount() + insertEntity.getSendPackageCount());

            //多扫发货统计
            if(moreScanFlag) {
                if(BusinessUtil.isBoxcode(param.getBarCode())) {
                    insertEntity.setMoreSendBoxCount(1);
                }else {
                    insertEntity.setMoreSendPackageCount(1);
                }
                insertEntity.setMoreSendTotalCount(insertEntity.getMoreSendBoxCount() + insertEntity.getMoreSendPackageCount());
            }
            //强发统计
        }

        insertEntity.setCreateTime(new Date());
        insertEntity.setUpdateTime(insertEntity.getCreateTime());
        jyPickingTaskSendAggsDao.insertSelective(insertEntity);
        logInfo("提货并发货任务维度agg实操统计插入barCode={}，insertEntity={}", param.getBarCode(), JsonHelper.toJson(insertEntity));


    }
    private void scanUpdatePickingSendStatistics(JyPickingGoodScanDto param, JyPickingTaskSendAggsEntity aggDtoRes){
        boolean moreScanFlag = Boolean.TRUE.equals(param.getMoreScanFlag());

        JyPickingTaskSendAggsEntity updateEntity = new JyPickingTaskSendAggsEntity(param.getPickingSiteId(), param.getNextSiteId(), param.getBizId());
        BeanUtils.copyProperties(aggDtoRes, updateEntity);

        updateEntity.setUpdateTime(new Date());

        //交接扫描提货统计
        if(!moreScanFlag) {
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                updateEntity.setHandoverScanBoxCount(aggDtoRes.getHandoverScanBoxCount() + 1);
            }else {
                updateEntity.setHandoverScanPackageCount(aggDtoRes.getHandoverScanPackageCount() + 1);
            }
            updateEntity.setHandoverScanTotalCount(updateEntity.getHandoverScanBoxCount() + updateEntity.getHandoverScanPackageCount());
        }

        //多扫提货统计
        if(moreScanFlag) {
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                updateEntity.setMoreScanBoxCount(aggDtoRes.getMoreScanBoxCount() + 1);
            }else {
                updateEntity.setMoreScanPackageCount(aggDtoRes.getMoreScanPackageCount() + 1);
            }
            updateEntity.setMoreScanTotalCount(updateEntity.getMoreScanBoxCount() + updateEntity.getMoreScanPackageCount());
        }

        //实扫提货统计【总提货=交际提+已提】
        updateEntity.setScanBoxTotalCount(updateEntity.getHandoverScanBoxCount() + updateEntity.getMoreScanBoxCount());
        updateEntity.setScanPackageTotalCount(updateEntity.getHandoverScanPackageCount() + updateEntity.getMoreScanPackageCount());
        updateEntity.setScanTotalCount(updateEntity.getScanBoxTotalCount() + updateEntity.getScanPackageTotalCount());

        //提货并发货统计发货
        if(Boolean.TRUE.equals(param.getSendGoodFlag())) {

            //扫描发货统计
            if(BusinessUtil.isBoxcode(param.getBarCode())) {
                updateEntity.setSendBoxCount(aggDtoRes.getSendBoxCount() + 1);
            }else {
                updateEntity.setSendPackageCount(aggDtoRes.getSendPackageCount() + 1);
            }
            updateEntity.setSendTotalCount(updateEntity.getSendBoxCount() + updateEntity.getSendPackageCount());

            //多扫发货统计
            if(moreScanFlag) {
                if(BusinessUtil.isBoxcode(param.getBarCode())) {
                    updateEntity.setMoreSendBoxCount(aggDtoRes.getMoreSendBoxCount() + 1);
                }else {
                    updateEntity.setMoreSendPackageCount(aggDtoRes.getMoreSendPackageCount() + 1);
                }
                updateEntity.setMoreSendTotalCount(updateEntity.getMoreSendBoxCount() + updateEntity.getMoreSendPackageCount());
            }

            //强发统计
        }
        jyPickingTaskSendAggsDao.updateScanStatistics(updateEntity);
        logInfo("提货发货任务流向维度agg实操统计修改bizId={}，nextSiteId={}，修改前={}，修改后计数={}", param.getBizId(), param.getNextSiteId(), JsonHelper.toJson(aggDtoRes), JsonHelper.toJson(updateEntity));

    }
}
