package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendFlowDisplayEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.*;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.constants.BarCodeFetchPickingTaskRuleEnum;
import com.jd.bluedragon.distribution.jy.constants.PickingCompleteNodeEnum;
import com.jd.bluedragon.distribution.jy.dto.common.BoxNextSiteDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.jy.service.common.CommonService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Author zhengchengfa
 * @Date 2023/12/4 13:29
 * @Description
 */

@Service
public class JyAviationRailwayPickingGoodsServiceImpl implements JyAviationRailwayPickingGoodsService{

    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwayPickingGoodsServiceImpl.class);


    @Autowired
    private JyAviationRailwayPickingGoodsParamCheckService paramCheckService;
    //空铁提货缓存服务
    @Autowired
    private JyAviationRailwayPickingGoodsCacheService pickingGoodsCacheService;
    //空铁提货任务服务
    @Autowired
    private JyBizTaskPickingGoodService jyBizTaskPickingGoodService;
    //空铁提货统计层服务
    @Autowired
    private JyPickingTaskAggsService jyPickingTaskAggsService;
    //空铁提货发货服务
    @Autowired
    private JyPickingSendDestinationService jyPickingSendDestinationService;
    //空铁提发记录服务
    @Autowired
    private JyPickingSendRecordService jyPickingSendRecordService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private CommonService commonService;
    @Resource
    @Qualifier("jyPickingGoodScanProducer")
    private DefaultJMQProducer pickingGoodScanProducer;
    @Autowired
    private JyPickingTaskAggsCacheService aggsCacheService;
    @Autowired
    private JyGroupSortCrossDetailService jyGroupSortCrossDetailService;
    @Autowired
    private DmsConfigManager dmsConfigManager;
    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

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

    @Override
    public InvokeResult<PickingGoodsRes> pickingGoodsScan(PickingGoodsReq request) {
        InvokeResult<PickingGoodsRes> res = new InvokeResult<>();
        PickingGoodsRes resData = new PickingGoodsRes();
        res.setData(resData);
        AirRailTaskAggDto airRailTaskAggDto = new AirRailTaskAggDto();
        resData.setAirRailTaskAggDto(airRailTaskAggDto);

        //必填业务参数校验
        InvokeResult<String> paramCheckRes = new InvokeResult<>();
        if(!paramCheckService.pickingGoodScanParamCheck(request, paramCheckRes)) {
            res.parameterError(paramCheckRes.getMessage());
            return res;
        }
        //场地barCode锁
        if(pickingGoodsCacheService.lockPickingGoodScan(request.getBarCode(), request.getCurrentOperate().getSiteCode())) {
            res.error("该单据【包裹】被多人提货操作中，请稍后操作");
            return res;
        }
        try{
            //确认待提货任务
            JyBizTaskPickingGoodEntity taskPickingGoodEntity = this.getTaskPickingGoodEntity(request, resData);
            //防重提货校验
            if(!this.repeatScanCheck(request.getBarCode(), request.getCurrentOperate().getSiteCode(),  taskPickingGoodEntity.getBizId(), res)) {
                logWarn("重复提货，request={},res={}", JsonHelper.toJson(request), JsonHelper.toJson(res));
                return res;
            }
            //提货并发货执行
            if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
                if(!this.sendGoodBusinessCheck(request, res)) {
                    return res;
                }
                this.doPickingSendGoods(request, res, taskPickingGoodEntity);
            }
            //仅提货逻辑
            else {
                this.doPickingGoods(request, res, taskPickingGoodEntity);
            }
            //提货记录 todo 考虑异步处理，避免和任务初始化并发时互相补偿， 和扫描不是一把锁
            this.savePickingRecord(request, resData, taskPickingGoodEntity);

            //异步相关处理
            this.sendJyPickingGoodScanMq(request, resData, taskPickingGoodEntity);

            //提货成功save缓存
            this.pickingGoodScanCache(request, resData,taskPickingGoodEntity);

            //返回结果处理  todo zcf 这里只有待提任务的，没有考虑提发的时候统计数据
            this.convertPickingTask(request, resData, taskPickingGoodEntity);

            return res;
        }catch (Exception e) {
            log.error("空铁提货服务异常，request={},errMsg={}", JsonHelper.toJson(request), e.getMessage(), e);
            res.error("空铁提货服务异常");
            return res;
        }finally {
            pickingGoodsCacheService.unlockPickingGoodScan(request.getBarCode(), request.getCurrentOperate().getSiteCode());
        }

    }

    /**
     * 提货发货校验
     */
    private boolean sendGoodBusinessCheck(PickingGoodsReq request, InvokeResult<PickingGoodsRes> res) {
        if(!jyPickingSendDestinationService.existSendNextSite((long)request.getCurrentOperate().getSiteCode(), request.getNextSiteId())){
            String siteName = this.getSiteNameBySiteId(request.getNextSiteId().intValue());
            res.parameterError(String.format("发货场地[%s|%s]未维护，请先添加发货流向", request.getNextSiteId(), siteName));
            return false;
        }
        if(!misSendingCheck(request, res)) {
            return false;
        }
        return true;
    }

    private void savePickingRecord(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity taskEntity) {
        //待提
        if(BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(resData.getTaskSource())) {
            JyPickingSendRecordEntity updateEntity = new JyPickingSendRecordEntity((long)request.getCurrentOperate().getSiteCode());
            updateEntity.setBizId(taskEntity.getBizId());
            updateEntity.setWaitScanCode(request.getBarCode());
            updateEntity.setScanCode(request.getBarCode());
            updateEntity.setUpdateTime(new Date());
            updateEntity.setScanFlag(Constants.NUMBER_ONE);
            updateEntity.setMoreScanFlag(Constants.NUMBER_ZERO);
            updateEntity.setPickingUserErp(request.getUser().getUserErp());
            updateEntity.setPickingUserName(request.getUser().getUserName());
            updateEntity.setPickingTime(new Date(resData.getOperateTime()));
            if(BusinessUtil.isBoxcode(request.getBarCode())) {
                updateEntity.setScanCodeType(JyPickingSendRecordEntity.SCAN_BOX);
            }
            else if(WaybillUtil.isPackageCode(request.getBarCode())) {
                updateEntity.setScanCodeType(JyPickingSendRecordEntity.SCAN_PACKAGE);
            }
            if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
                updateEntity.setSendFlag(Constants.NUMBER_ONE);
                updateEntity.setRealNextSiteId(request.getNextSiteId());
                updateEntity.setBoxRealFlowKey(resData.getBoxConfirmNextSiteKey());
                updateEntity.setMoreSendFlag(Constants.NUMBER_ZERO);
                updateEntity.setForceSendFlag(Boolean.TRUE.equals(request.getForceSendFlag()) ? Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                updateEntity.setSendTime(new Date(resData.getOperateTime()));
                updateEntity.setSendUserErp(updateEntity.getPickingUserErp());
                updateEntity.setSendUserName(updateEntity.getPickingUserName());
            }

            jyPickingSendRecordService.updatePickingGoodRecordByWaitScanCode(updateEntity);
        }
        //多提
        else {
            JyPickingSendRecordEntity insertEntity = new JyPickingSendRecordEntity();
            insertEntity.setBizId(taskEntity.getBizId());
            insertEntity.setPickingSiteId((long)request.getCurrentOperate().getSiteCode());
            insertEntity.setPickingNodeCode(taskEntity.getEndNodeCode());
            insertEntity.setWaitScanCode(null);
            insertEntity.setWaitScanCodeType(null);
            insertEntity.setInitNextSiteId(null);
            insertEntity.setBoxInitFlowKey(null);
            insertEntity.setWaitScanFlag(Constants.NUMBER_ZERO);
            insertEntity.setScanCode(request.getBarCode());
            insertEntity.setScanFlag(Constants.NUMBER_ONE);
            insertEntity.setMoreScanFlag(Constants.NUMBER_ONE);
            insertEntity.setPickingUserErp(request.getUser().getUserErp());
            insertEntity.setPickingUserName(request.getUser().getUserName());
            insertEntity.setPickingTime(new Date(resData.getOperateTime()));
            insertEntity.setCreateTime(new Date());
            insertEntity.setUpdateTime(insertEntity.getUpdateTime());
            if(BusinessUtil.isBoxcode(request.getBarCode())) {
                //多提箱号时同步存储一条已扫箱号数据，保证定时agg统计准确，箱内明细如果有需要保存，触发异步去存储， agg去重后统计不影响
                insertEntity.setScanCodeType(JyPickingSendRecordEntity.SCAN_BOX);
            }
            else if(WaybillUtil.isPackageCode(request.getBarCode())) {
                insertEntity.setScanCodeType(JyPickingSendRecordEntity.SCAN_PACKAGE);
                insertEntity.setPackageCode(request.getBarCode());
                insertEntity.setWaybillCode(WaybillUtil.getWaybillCode(request.getBarCode()));
            }
            if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
                insertEntity.setSendFlag(Constants.NUMBER_ONE);
                insertEntity.setRealNextSiteId(request.getNextSiteId());
                insertEntity.setBoxRealFlowKey(resData.getBoxConfirmNextSiteKey());
                insertEntity.setMoreSendFlag(Constants.NUMBER_ONE);
                insertEntity.setForceSendFlag(Boolean.TRUE.equals(request.getForceSendFlag()) ? Constants.NUMBER_ONE : Constants.NUMBER_ZERO);
                insertEntity.setSendTime(new Date(resData.getOperateTime()));
                insertEntity.setSendUserErp(insertEntity.getPickingUserErp());
                insertEntity.setSendUserName(insertEntity.getPickingUserName());
            }
            jyPickingSendRecordService.savePickingRecord(insertEntity);
        }

    }

    private void convertPickingTask(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity taskPickingGoodEntity) {
        AirRailTaskAggDto airRailTaskAggDto = resData.getAirRailTaskAggDto();
        BeanUtils.copyProperties(airRailTaskAggDto, taskPickingGoodEntity);
        Integer waitPickingTotalNum = this.getInitWaitPickingTotalItemNum(taskPickingGoodEntity.getBizId(), (long)request.getCurrentOperate().getSiteCode());
        Integer realPickingTotalNum = 1;
        Integer morePickingTotalNum = (BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode()).equals(resData.getTaskSource()) ? 1 : 0;
        //待提
        int realScanWaitPackageNum = aggsCacheService.getValueRealScanWaitPickingPackageNum(taskPickingGoodEntity.getBizId(), (long)request.getCurrentOperate().getSiteCode());
        int realScanWaitBoxNum = aggsCacheService.getValueRealScanWaitPickingBoxNum(taskPickingGoodEntity.getBizId(), (long)request.getCurrentOperate().getSiteCode());
        int waitPickingTotalNumTemp = waitPickingTotalNum - (realScanWaitPackageNum + realScanWaitBoxNum);
        airRailTaskAggDto.setWaitScanTotal(NumberHelper.gt(waitPickingTotalNumTemp, waitPickingTotalNum) ? waitPickingTotalNumTemp : waitPickingTotalNum);
        //已提
        int morePickingPackageNum = aggsCacheService.getValueRealScanMorePickingPackageNum(taskPickingGoodEntity.getBizId(), (long)request.getCurrentOperate().getSiteCode());
        int morePickingBoxNum = aggsCacheService.getValueRealScanMorePickingBoxNum(taskPickingGoodEntity.getBizId(), (long)request.getCurrentOperate().getSiteCode());
        Integer realPickingTotalNumTemp = realScanWaitPackageNum + realScanWaitBoxNum + morePickingPackageNum + morePickingBoxNum;
        airRailTaskAggDto.setHaveScannedTotal(NumberHelper.gt(realPickingTotalNumTemp, realPickingTotalNum) ? realPickingTotalNumTemp : realPickingTotalNum);
        //多提
        int morePickingTotalNumTemp = morePickingPackageNum + morePickingBoxNum;
        airRailTaskAggDto.setMultipleScanTotal(NumberHelper.gt(morePickingTotalNumTemp, morePickingTotalNum) ? morePickingTotalNumTemp : morePickingTotalNum);

    }
    //初始化后的待提总件数
    private Integer getInitWaitPickingTotalItemNum(String bizId, Long siteId) {
        Integer num = aggsCacheService.getCacheInitWaitPickingTotalItemNum(bizId, siteId);
        if(!NumberHelper.gt0(num)) {
            List<String> bizIdList = Arrays.asList(bizId);
            List<PickingSendGoodAggsDto> list = jyPickingTaskAggsService.waitPickingInitTotalNum(bizIdList, siteId, null);
            if(CollectionUtils.isNotEmpty(list)) {
                num = list.get(0).getWaitSendTotalNum();
            }

        }
        return num;
    }

    private void pickingGoodScanCache(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity taskPickingGoodEntity) {
        //统计字段维护
        jyPickingTaskAggsService.saveCacheAggStatistics(request, resData,taskPickingGoodEntity);
        //提货防重
        pickingGoodsCacheService.saveCachePickingGoodScan(request.getBarCode(), taskPickingGoodEntity.getBizId(), request.getCurrentOperate().getSiteCode());

    }

    private void sendJyPickingGoodScanMq(PickingGoodsReq request, PickingGoodsRes pickingGoodsRes, JyBizTaskPickingGoodEntity taskPickingGoodEntity) {
        JyPickingGoodScanDto scanDto = new JyPickingGoodScanDto();
        scanDto.setBarCode(request.getBarCode());
        scanDto.setBizId(taskPickingGoodEntity.getBizId());
        scanDto.setSiteId((long)request.getCurrentOperate().getSiteCode());
        scanDto.setOperatorTime(pickingGoodsRes.getOperateTime());
        scanDto.setGroupCode(request.getGroupCode());
        pickingGoodScanProducer.sendOnFailPersistent(request.getBarCode(), com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(scanDto));
    }


    /**
     * 获取待提任务：三种方式
     * 1、按待扫匹配到待提任务
     * 2、匹配不到待提任务时， 取入参中上一次扫描的待提任务bizId 做待提任务  多提数据
     * 3、前面两种匹配不上，PDA端做自建任务生成
     * @param request
     * @return
     */
    private JyBizTaskPickingGoodEntity getTaskPickingGoodEntity(PickingGoodsReq request, PickingGoodsRes resData) {
        JyBizTaskPickingGoodEntity pickingGoodEntity = this.fetchWaitPickingBizIdByBarCode((long)request.getCurrentOperate().getSiteCode(), request.getBarCode());
        if(!Objects.isNull(pickingGoodEntity)) {
            //存在待提任务场景
            logInfo("扫描单据{}查提货任务-待提任务{}", request.getBarCode(), JsonHelper.toJson(pickingGoodEntity));
            resData.setTaskSource(BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode());
            return pickingGoodEntity;
        }else if(StringUtils.isNotBlank(request.getLastScanTaskBizId())) {
            //不存在待提任务取上次扫描任务
            JyBizTaskPickingGoodEntity taskPickingGoodEntity = jyBizTaskPickingGoodService.findByBizIdWithYn(request.getLastScanTaskBizId(), false);
            logInfo("扫描单据{}查提货任务为空-待提任务取上次扫描任务{}", request.getBarCode(), JsonHelper.toJson(taskPickingGoodEntity));
            resData.setTaskSource(BarCodeFetchPickingTaskRuleEnum.LAST_PICKING_TASK.getCode());
            return taskPickingGoodEntity;
        }
        //待提货任务为空时生成自建待提任务
        JyBizTaskPickingGoodEntity manualCreateTask = jyBizTaskPickingGoodService.findLatestEffectiveManualCreateTask((long)request.getCurrentOperate().getSiteCode());
        if(!Objects.isNull(manualCreateTask)) {
            logInfo("扫描单据{}查提货任务为空-待提任务取自已存在的自建任务{}", request.getBarCode(), JsonHelper.toJson(manualCreateTask));
            resData.setTaskSource(BarCodeFetchPickingTaskRuleEnum.MANUAL_CREATE_TASK_EXIST.getCode());
            return manualCreateTask;
        }
        JyBizTaskPickingGoodEntity taskPickingGoodEntity = jyBizTaskPickingGoodService.generateManualCreateTask(request.getCurrentOperate(), request.getUser(), request.getTaskType());
        logInfo("扫描单据{}查提货任务为空-待提任务取默认生成任务{}", request.getBarCode(), JsonHelper.toJson(taskPickingGoodEntity));
        resData.setTaskSource(BarCodeFetchPickingTaskRuleEnum.MANUAL_CREATE_TASK_GENERATE.getCode());
        return taskPickingGoodEntity;

    }

    /**
     * 重复扫描校验
     * 按场地+包裹号做防重，一个机场分拣中心只能提货一次
     * @param barCode
     * @param res true： 校验通过  false: 重复扫描进行拦截，校验不通过
     * @return
     */
    private boolean repeatScanCheck(String barCode, Integer siteId, String bizId, InvokeResult<PickingGoodsRes> res) {
        if(!pickingGoodsCacheService.getCachePickingGoodScanValue(barCode, bizId, siteId)) {
            JyPickingSendRecordEntity recordEntity = jyPickingSendRecordService.latestPickingRecord(siteId.longValue(), bizId, barCode);
            if(!Objects.isNull(recordEntity)) {
                logInfo("提货扫描redis防重查询为空，查DB最近一次提货记录,barCode={},siteId={},提货记录：{}", barCode, siteId, JsonHelper.toJson(recordEntity));
                pickingGoodsCacheService.saveCachePickingGoodScan(barCode, bizId, siteId);
                res.parameterError(String.format("该包裹[箱号]已经提货，请勿重复扫描", barCode));
                return false;
            }
        }else {
            res.parameterError(String.format("该包裹[箱号]已经提货，请勿重复扫描", barCode));
            return false;
        }
        return true;
    }

    /**
     * 发货服务
     * @param request
     * @param res
     * @return
     */
    private boolean doPickingSendGoods(PickingGoodsReq request, InvokeResult<PickingGoodsRes> res, JyBizTaskPickingGoodEntity taskPickingGoodEntity) {
        PickingGoodScanTaskBodyDto bodyDto = new PickingGoodScanTaskBodyDto();
        bodyDto.setBoxCode(request.getBarCode());
        bodyDto.setBusinessType(10);
        bodyDto.setTaskType(Task.TASK_TYPE_AR_RECEIVE_AND_SEND);
        bodyDto.setBatchCode(jyPickingSendDestinationService.findOrGenerateBatchCode((long)request.getCurrentOperate().getSiteCode(), request.getNextSiteId(), request.getUser()));
        bodyDto.setReceiveSiteCode(request.getNextSiteId().intValue());
        //
        bodyDto.setUserErp(request.getUser().getUserErp());
        bodyDto.setUserCode(request.getUser().getUserCode());
        bodyDto.setUserName(request.getUser().getUserName());
        //
        bodyDto.setSiteCode(request.getCurrentOperate().getSiteCode());
        bodyDto.setSiteName(request.getCurrentOperate().getSiteName());
        //
        Date date = new Date();
        bodyDto.setOperateTime(DateHelper.formatDateTime(date));
        bodyDto.setBizId(taskPickingGoodEntity.getBizId());
        String bodyJson = JsonHelper.toJson(bodyDto);

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBody(bodyJson);
        taskRequest.setBoxCode(request.getBarCode());
        taskRequest.setKeyword1(String.valueOf(request.getCurrentOperate().getSiteCode()));
        taskRequest.setOperateTime(DateHelper.formatDateTime(new Date()));
        taskRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskRequest.setSiteName(request.getCurrentOperate().getSiteName());
        taskRequest.setType(Task.TASK_TYPE_OFFLINE);// todo zcf 1800离线模式
        taskRequest.setUserCode(request.getUser().getUserCode());
        taskRequest.setUserName(request.getUser().getUserName());
        taskService.add(taskRequest);
        res.getData().setOperateTime(date.getTime());
        res.getData().setBatchCode(bodyDto.getBatchCode());
        return true;
    }

    /**
     * 发货流向错发校验
     * @param request
     * @param res
     * @return
     */
    private boolean misSendingCheck(PickingGoodsReq request, InvokeResult<PickingGoodsRes> res) {
        if(Boolean.TRUE.equals(request.getForceSendFlag())) {
            return true;
        }
        PickingGoodsRes resData = res.getData();

        Integer routerNextSiteId = null;
        String routerNextSiteName = null;
        String boxConfirmNextSiteKey = null;
        if(!BusinessUtil.isBoxcode(request.getBarCode())) {
            BaseStaffSiteOrgDto dto = commonService.getRouteNextSiteByWaybillCode(request.getCurrentOperate().getSiteCode(), WaybillUtil.getWaybillCode(request.getBarCode()));
            if(!Objects.isNull(dto)) {
                routerNextSiteName = dto.getSiteName();
                routerNextSiteId = dto.getSiteCode();
            }
        }else {
            BoxNextSiteDto boxNextSiteDto = commonService.getRouteNextSiteByBox(request.getCurrentOperate().getSiteCode(), request.getBarCode());
            if(!Objects.isNull(boxNextSiteDto)) {
                routerNextSiteId = boxNextSiteDto.getNextSiteId();
                routerNextSiteName = boxNextSiteDto.getNextSiteName();
                boxConfirmNextSiteKey = boxNextSiteDto.getBoxConfirmNextSiteKey();
            }
        }

        //流向为空
        if(Objects.isNull(routerNextSiteId)) {
            resData.setNextSiteSupportSwitch(false);
            res.customMessage(PickingGoodsRes.CODE_30001, PickingGoodsRes.CODE_30001_MSG_2);
            return false;
        }
        //流向不一致
        if(!request.getNextSiteId().equals(routerNextSiteId)) {
            resData.setNextSiteSupportSwitch(jyPickingSendDestinationService.existSendNextSite((long)request.getCurrentOperate().getSiteCode(), routerNextSiteId.longValue()));
            resData.setRouterNextSiteId(routerNextSiteId);
            resData.setRouterNextSiteName(routerNextSiteName);
            resData.setBoxConfirmNextSiteKey(boxConfirmNextSiteKey);
            String msg = String.format(PickingGoodsRes.CODE_30001_MSG_1, routerNextSiteName, request.getNextSiteName());
            res.customMessage(PickingGoodsRes.CODE_30001, msg);
            return false;
        }

        return true;
    }

    /**
     * 根据场地编码获取场地名称
     * @param siteId
     * @return
     */
    private String getSiteNameBySiteId(Integer siteId){
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(siteId);
        if(Objects.isNull(dto)) {
            throw new JyBizException(String.format("根据场地编码%s查询场地不存在", siteId));
        }
        return dto.getSiteName();
    }

    /**
     * 提货服务
     * @param request
     * @param res
     * @return
     */
    private boolean doPickingGoods(PickingGoodsReq request, InvokeResult<PickingGoodsRes> res, JyBizTaskPickingGoodEntity taskPickingGoodEntity) {
        PickingGoodScanTaskBodyDto bodyDto = new PickingGoodScanTaskBodyDto();
        bodyDto.setBoxCode(request.getBarCode());
        bodyDto.setBusinessType(10);
        bodyDto.setTaskType(Task.TASK_TYPE_AR_RECEIVE);
        //
        bodyDto.setUserErp(request.getUser().getUserErp());
        bodyDto.setUserCode(request.getUser().getUserCode());
        bodyDto.setUserName(request.getUser().getUserName());
        //
        bodyDto.setSiteCode(request.getCurrentOperate().getSiteCode());
        bodyDto.setSiteName(request.getCurrentOperate().getSiteName());
        //
        Date date = new Date();
        bodyDto.setOperateTime(DateHelper.formatDateTime(date));

        bodyDto.setBizId(taskPickingGoodEntity.getBizId());
        String bodyJson = JsonHelper.toJson(bodyDto);

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBody(bodyJson);
        taskRequest.setBoxCode(request.getBarCode());
        taskRequest.setKeyword1(String.valueOf(request.getCurrentOperate().getSiteCode()));
        taskRequest.setOperateTime(DateHelper.formatDateTime(new Date()));
        taskRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskRequest.setSiteName(request.getCurrentOperate().getSiteName());
        taskRequest.setType(Task.TASK_TYPE_OFFLINE);// todo zcf 1800离线模式
        taskRequest.setUserCode(request.getUser().getUserCode());
        taskRequest.setUserName(request.getUser().getUserName());
        taskService.add(taskRequest);
        res.getData().setOperateTime(date.getTime());
        return true;
    }


    public JyBizTaskPickingGoodEntity fetchWaitPickingBizIdByBarCode(Long siteCode, String barCode) {
        String bizId = jyPickingSendRecordService.fetchWaitPickingBizIdByBarCode(siteCode, barCode);
        if(StringUtils.isBlank(bizId)) {
            logInfo("{}|{}未查到待提货任务BizId", barCode, siteCode);
            return null;
        }
        JyBizTaskPickingGoodEntity pickingGoodEntity = jyBizTaskPickingGoodService.findByBizIdWithYn(bizId, false);
        if(!PickingGoodStatusEnum.PICKING_COMPLETE.getCode().equals(pickingGoodEntity) && !JyBizTaskPickingGoodEntity.INTERCEPT_FLAG.equals(pickingGoodEntity.getIntercept())) {
            return pickingGoodEntity;
        }
        logInfo("{}|{}未查到待提货任务", barCode, siteCode);
        return null;
    }

    @Override
    public InvokeResult<Void> finishPickGoods(FinishPickGoodsReq req) {
        InvokeResult<Void> ret = new InvokeResult<>();
        if (StringUtils.isEmpty(req.getBizId())) {
            ret.parameterError("参数错误：提货任务BizId不能为空！");
            return ret;
        }
        boolean success = jyBizTaskPickingGoodService.updateStatusByBizId(req.getBizId(), PickingCompleteNodeEnum.COMPLETE_BTN.getCode());
        if (!success) {
            log.warn("jyBizTaskPickingGoodService 根据bizId={} 完成提货任务状态失败！", req.getBizId());
        }
        return ret;
    }

    @Override
    public InvokeResult<Void> submitException(ExceptionSubmitReq req) {
        InvokeResult<Void> ret = new InvokeResult<>();
        if (StringUtils.isEmpty(req.getBizId())) {
            ret.parameterError("参数错误：提货任务BizId不能为空！");
            return ret;
        }
        // 当前异常上报只将任务状态修改为完成
        boolean success = jyBizTaskPickingGoodService.updateStatusByBizId(req.getBizId(), PickingCompleteNodeEnum.EXCEPTION_BTN.getCode());
        if (!success) {
            log.warn("jyBizTaskPickingGoodService 根据bizId={} 完成提货任务状态失败！", req.getBizId());
        }
        return ret;
    }

    @Override
    public InvokeResult<SendFlowRes> listSendFlowInfo(SendFlowReq req) {
        InvokeResult<SendFlowRes> invokeResult = new InvokeResult<>();
        if (req.getDisplayType() == null) {
            invokeResult.parameterError("展示类型不能为空！");
            return invokeResult;
        }
        SendFlowRes res = new SendFlowRes();
        invokeResult.setData(res);

        List<SendFlowDto> dtoList = jyPickingSendDestinationService.listSendFlowInfo(req);
        res.setFlowDtoList(dtoList);
        if (CollectionUtils.isEmpty(dtoList)) {
            return invokeResult;
        }
        // todo
        if (SendFlowDisplayEnum.COUNT.getCode().equals(req.getDisplayType())) {

        }

        return invokeResult;
    }

    @Override
    public InvokeResult<Void> addSendFlow(SendFlowAddReq req) {
        InvokeResult<Void> ret = new InvokeResult<>();
        if (CollectionUtils.isEmpty(req.getSiteList())) {
            ret.parameterError("所选流向场地不能为空！");
            return ret;
        }
        boolean success = jyPickingSendDestinationService.addSendFlow(req);
        if (!success) {
            ret.customMessage(InvokeResult.AIR_RAIL_SEND_FLOW_ADD_FAIL_CODE, InvokeResult.AIR_RAIL_SEND_FLOW_ADD_FAIL_MESSAGE);
        }
        return ret;
    }

    @Override
    public InvokeResult<Void> deleteSendFlow(SendFlowDeleteReq req) {
        InvokeResult<Void> ret = new InvokeResult<>();
        if (req.getNextSiteId() == null) {
            ret.parameterError("所选流向场地id不能为空！");
            return ret;
        }
        jyPickingSendDestinationService.deleteSendFlow(req);
        return ret;
    }

    @Override
    public InvokeResult<Void> finishSendTask(FinishSendTaskReq req) {
        InvokeResult<Void> ret = new InvokeResult<>();
        finishSendTaskCheck(req, ret);
        if (!ret.codeSuccess()) {
            return ret;
        }
        jyPickingSendDestinationService.finishSendTask(req);
        return ret;
    }

    private void finishSendTaskCheck(FinishSendTaskReq req, InvokeResult<Void> invokeResult) {
        if (StringUtils.isEmpty(req.getSendCode())) {
            invokeResult.parameterError("批次号不能为空！");
        }
        if (req.getNextSiteId() == null) {
            invokeResult.parameterError("目的场地编码不能为空！");
        }
        if (req.getTaskType() == null) {
            invokeResult.parameterError("任务类型不能为空！");
        }
    }

    @Override
    public InvokeResult<AirRailTaskRes> listAirRailTaskSummary(AirRailTaskSummaryReq req) {
        InvokeResult<AirRailTaskRes> ret = new InvokeResult<>();
        listAirRailTaskSummaryCheck(req, ret);
        if (!ret.codeSuccess()) {
            return ret;
        }
        AirRailTaskRes res = new AirRailTaskRes();
        ret.setData(res);
        Long currentSiteId = (long) req.getCurrentOperate().getSiteCode();
        // count当前场地各个status的任务数
        AirRailTaskCountQueryDto countQueryDto = buildCountQueryDto(req);
        List<AirRailTaskCountDto> countDtoList = jyBizTaskPickingGoodService.countAllStatusByPickingSiteId(countQueryDto);
        if (CollectionUtils.isEmpty(countDtoList)) {
            return ret;
        }
        // 按机场/车站分组逻辑分页查询
        JyPickingTaskGroupQueryDto groupQueryDto = buildGroupQueryDto(req);
        List<JyBizTaskPickingGoodEntity> groupedTask = jyBizTaskPickingGoodService.listTaskGroupByPickingNodeCode(groupQueryDto);
        if (CollectionUtils.isEmpty(groupedTask)) {
            return ret;
        }
        // 查询明细数据
        JyPickingTaskBatchQueryDto batchQueryDto = buildBatchQueryDto(req, groupedTask);
        List<JyBizTaskPickingGoodEntity> taskDetail = jyBizTaskPickingGoodService.listTaskByPickingNodeCode(batchQueryDto);
        if (CollectionUtils.isEmpty(taskDetail)) {
            return ret;
        }
        // 查询统计数据
        List<String> bizList = taskDetail.stream().map(JyBizTaskPickingGoodEntity::getBizId).distinct().collect(Collectors.toList());
        List<PickingSendGoodAggsDto> aggsDtoList = jyPickingTaskAggsService.findPickingAgg(bizList, currentSiteId, null);
        // 计算统计总和
        calculateSummaryResponse(res, countDtoList, taskDetail, aggsDtoList);

        return ret;
    }

    private void listAirRailTaskSummaryCheck(AirRailTaskSummaryReq req, InvokeResult<AirRailTaskRes> invokeResult) {
        if (req.getTaskType() == null) {
            invokeResult.parameterError("提货任务类型不能为空！");
            return;
        }
        if (req.getStatus() == null) {
            invokeResult.parameterError("提货任务状态不能为空！");
            return;
        }
    }

    private AirRailTaskCountQueryDto buildCountQueryDto(AirRailTaskSummaryReq req) {
        AirRailTaskCountQueryDto queryDto = new AirRailTaskCountQueryDto();
        queryDto.setPickingSiteId(req.getCurrentOperate().getSiteCode());
        queryDto.setTaskType(req.getTaskType());

        Date startTime = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -dmsConfigManager.getUccPropertyConfiguration().getJyBizTaskPickingGoodTimeRange());
        queryDto.setCreateTime(startTime);

        return queryDto;
    }

    private JyPickingTaskGroupQueryDto buildGroupQueryDto(AirRailTaskSummaryReq req) {
        JyPickingTaskGroupQueryDto queryDto = new JyPickingTaskGroupQueryDto();
        queryDto.setPickingSiteId((long) req.getCurrentOperate().getSiteCode());
        queryDto.setStatus(req.getStatus());
        queryDto.setTaskType(req.getTaskType());

        Date startTime = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -dmsConfigManager.getUccPropertyConfiguration().getJyBizTaskPickingGoodTimeRange());
        queryDto.setCreateTime(startTime);

        queryDto.setOffset((req.getPageNum() - 1) * req.getPageSize());
        queryDto.setLimit(req.getPageSize());
        return queryDto;
    }

    private JyPickingTaskBatchQueryDto buildBatchQueryDto(AirRailTaskSummaryReq req, List<JyBizTaskPickingGoodEntity> groupByTask) {
        JyPickingTaskBatchQueryDto queryDto = new JyPickingTaskBatchQueryDto();
        queryDto.setPickingSiteId((long) req.getCurrentOperate().getSiteCode());
        queryDto.setStatus(req.getStatus());
        queryDto.setTaskType(req.getTaskType());

        Date startTime = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -dmsConfigManager.getUccPropertyConfiguration().getJyBizTaskPickingGoodTimeRange());
        queryDto.setCreateTime(startTime);

        List<String> pickingNodeCodes = groupByTask.stream().map(JyBizTaskPickingGoodEntity::getEndNodeCode).distinct().collect(Collectors.toList());
        queryDto.setPickingNodeCodeList(pickingNodeCodes);
        return queryDto;
    }

    private void calculateSummaryResponse(AirRailTaskRes res, List<AirRailTaskCountDto> countDtoList, List<JyBizTaskPickingGoodEntity> taskDetail, List<PickingSendGoodAggsDto> aggsDtoList) {
        res.setCountDtoList(countDtoList);
        for (AirRailTaskCountDto dto : countDtoList) {
            dto.setStatusName(PickingGoodStatusEnum.getNameByCode(dto.getStatus()));
        }
        Map<String, PickingSendGoodAggsDto> aggMappedByBizId = aggsDtoList.stream().collect(Collectors.toMap(PickingSendGoodAggsDto::getBizId, item -> item, (first, second) -> first));
        Map<String, List<JyBizTaskPickingGoodEntity>> groupedTaskDetail = taskDetail.stream().collect(Collectors.groupingBy(JyBizTaskPickingGoodEntity::getEndNodeCode));
        List<AirRailDto> airRailDtoList = new ArrayList<>();
        for (List<JyBizTaskPickingGoodEntity> taskList : groupedTaskDetail.values()) {
            AirRailDto airRailDto = new AirRailDto();
            // 待提航班/车次任务数
            int waitScanTaskNum = aggsDtoList.size();
            // 是否无任务分组
            Boolean noTaskFlag = Constants.NUMBER_ONE.equals(taskList.get(0).getManualCreatedFlag());

            // 待提总件数
            int waitScanTotal = 0;

            // 已提总件数
            int haveScannedTotal = 0;

            // 多提总件数
            int multipleScanTotal = 0;
            for (JyBizTaskPickingGoodEntity task : taskList) {
                // 实际到达时间为空 待提取cargoNum
                if (task.getNodeRealArriveTime() == null && PickingGoodStatusEnum.TO_PICKING.getCode().equals(task.getStatus())) {
                    waitScanTaskNum += task.getCargoNumber();
                    continue;
                }
                PickingSendGoodAggsDto aggsDto = aggMappedByBizId.get(task.getBizId());
                if (aggsDto == null) {
                    logInfo("JyAviationRailwayPickingGoodsServiceImpl.calculateResponse根据bizId{}找不到统计数据", task.getBizId());
                    continue;
                }
                waitScanTotal += aggsDto.getWaitSendTotalNum();
                haveScannedTotal += aggsDto.getRealSendTotalNum();
                multipleScanTotal += aggsDto.getMoreSendTotalNum();
            }
            airRailDto.setNoTaskFlag(noTaskFlag);
            airRailDto.setWaitScanTaskNum(waitScanTaskNum);
            airRailDto.setWaitScanTotal(waitScanTotal);
            airRailDto.setHaveScannedTotal(haveScannedTotal);
            airRailDto.setMultipleScanTotal(multipleScanTotal);

            airRailDtoList.add(airRailDto);
        }
        res.setAirRailDtoList(airRailDtoList);
    }

    @Override
    public InvokeResult<AirRailTaskAggRes> listAirRailTaskAgg(AirRailTaskAggReq req) {
        InvokeResult<AirRailTaskAggRes> ret = new InvokeResult<>();
        AirRailTaskAggRes res = new AirRailTaskAggRes();
        List<AirRailTaskAggDto> taskAggDtoList = new ArrayList<>();
        res.setTaskAggDtoList(taskAggDtoList);
        listAirRailTaskAggCheck(req, ret);
        if (!ret.codeSuccess()) {
            return ret;
        }

        long currentSiteId = req.getCurrentOperate().getSiteCode();
        JyPickingTaskBatchQueryDto batchQueryDto = buildBatchQueryDto(req);
        List<JyBizTaskPickingGoodEntity> taskDetail = jyBizTaskPickingGoodService.listTaskByPickingNodeCode(batchQueryDto);
        if (CollectionUtils.isEmpty(taskDetail)) {
            return ret;
        }

        List<String> bizList = taskDetail.stream().map(JyBizTaskPickingGoodEntity::getBizId).distinct().collect(Collectors.toList());
        List<PickingSendGoodAggsDto> aggsDtoList = jyPickingTaskAggsService.waitPickingInitTotalNum(bizList, currentSiteId, null);
        // 计算统计总和
        calculateAggResponse(res, taskDetail, aggsDtoList);
        return ret;
    }

    private void listAirRailTaskAggCheck(AirRailTaskAggReq req, InvokeResult<AirRailTaskAggRes> invokeResult) {
        if (req.getTaskType() == null) {
            invokeResult.parameterError("提货任务类型不能为空！");
            return;
        }
        if (req.getStatus() == null) {
            invokeResult.parameterError("提货任务状态不能为空！");
            return;
        }
        if (StringUtils.isEmpty(req.getPickingNodeCode())) {
            invokeResult.parameterError("提货机场或车站编码不能为空！");
            return;
        }
    }

    private JyPickingTaskBatchQueryDto buildBatchQueryDto(AirRailTaskAggReq req) {
        JyPickingTaskBatchQueryDto queryDto = new JyPickingTaskBatchQueryDto();
        queryDto.setPickingSiteId((long) req.getCurrentOperate().getSiteCode());
        queryDto.setStatus(req.getStatus());
        queryDto.setTaskType(req.getTaskType());

        Date startTime = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -dmsConfigManager.getUccPropertyConfiguration().getJyBizTaskPickingGoodTimeRange());
        queryDto.setCreateTime(startTime);

        List<String> pickingNodeCodes = Collections.singletonList(req.getPickingNodeCode());
        queryDto.setPickingNodeCodeList(pickingNodeCodes);
        return queryDto;
    }

    private void calculateAggResponse(AirRailTaskAggRes res, List<JyBizTaskPickingGoodEntity> taskDetail, List<PickingSendGoodAggsDto> aggsDtoList) {
        List<AirRailTaskAggDto> taskAggDtoList = res.getTaskAggDtoList();
        Map<String, PickingSendGoodAggsDto> aggMappedByBizId = aggsDtoList.stream().collect(Collectors.toMap(PickingSendGoodAggsDto::getBizId, item -> item, (first, second) -> first));

        for (JyBizTaskPickingGoodEntity task : taskDetail) {
            AirRailTaskAggDto dto = new AirRailTaskAggDto();
            dto.setBizId(task.getBizId());
            dto.setPickingTime(task.getPickingStartTime());
            dto.setNoTaskFlag(Constants.NUMBER_ONE.equals(task.getManualCreatedFlag()));
            dto.setStartSiteId(task.getStartSiteId().intValue());
            dto.setNodePlanArriveTime(task.getNodePlanArriveTime());
            dto.setNodeRealArriveTime(task.getNodeRealArriveTime());
            dto.setServiceNumber(task.getServiceNumber());

            PickingSendGoodAggsDto aggsDto = aggMappedByBizId.get(task.getBizId());
            if (aggsDto != null) {
                dto.setWaitScanTotal(aggsDto.getWaitSendTotalNum());
                dto.setHaveScannedTotal(aggsDto.getRealSendTotalNum());
                dto.setMultipleScanTotal(aggsDto.getMoreSendTotalNum());
            }

            BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(task.getStartSiteId().intValue());
            dto.setStartSiteName(siteOrgDto == null ? Constants.EMPTY_FILL : siteOrgDto.getSiteName());

            taskAggDtoList.add(dto);
        }
    }

//    @Override
//    public boolean isFirstScanInTask(String bizId, Long siteId) {
//        if (pickingGoodsCacheService.lockPickingGoodTaskFirstScan(bizId, siteId)) {
//            Integer count = jyPickingSendRecordService.countTaskRealScanItemNum(bizId, siteId);
//            if (!NumberHelper.gt0(count)) {
//                logInfo("提货任务{}首次扫描.{}", bizId);
//                return true;
//            }
//        }
//        return false;
//    }

    @Override
    public void finishTaskWhenWaitScanEqZero() {
        Date now = new Date();
        Date startTime = DateUtils.addDays(DateUtils.truncate(now, Calendar.DATE), -dmsConfigManager.getPropertyConfig().getPickingGoodTaskWaitScanEq0TimeRange());
        Date endTime = DateUtils.truncate(now, Calendar.DATE);
        JyPickingTaskAggQueryDto queryDto = new JyPickingTaskAggQueryDto();
        int pageNumber = 1;
        queryDto.setLimit(1000);
        queryDto.setStartTime(startTime);
        queryDto.setEndTime(endTime);
        List<String> bizIdList;
        do {
            queryDto.setOffset((pageNumber - 1) * queryDto.getLimit());
            bizIdList = jyPickingTaskAggsService.pageRecentWaitScanEqZero(queryDto);
            if (CollectionUtils.isEmpty(bizIdList)) {
                return;
            }
            JyPickingTaskBatchUpdateDto updateDto = new JyPickingTaskBatchUpdateDto();
            updateDto.setBizIdList(bizIdList);
            updateDto.setStatus(PickingGoodStatusEnum.PICKING_COMPLETE.getCode());
            updateDto.setCompleteNode(PickingCompleteNodeEnum.WAIT_SCAN_0.getCode());
            jyBizTaskPickingGoodService.batchUpdateStatusByBizId(updateDto);
            pageNumber++;
        } while (CollectionUtils.isNotEmpty(bizIdList));
    }

    @Override
    public void finishTaskWhenExceed24Hours() {
        Date now = new Date();
        Date startTime = DateUtils.addDays(DateUtils.truncate(now, Calendar.DATE), -dmsConfigManager.getPropertyConfig().getPickingGoodTaskManualTimeRange());
        Date endTime = DateUtils.truncate(now, Calendar.DATE);
        JyBizTaskPickingGoodQueryDto queryDto = new JyBizTaskPickingGoodQueryDto();
        int pageNumber = 1;
        queryDto.setLimit(1000);
        queryDto.setStartTime(startTime);
        queryDto.setEndTime(endTime);
        List<String> bizIdList;
        do {
            queryDto.setOffset((pageNumber - 1) * queryDto.getLimit());
            bizIdList = jyBizTaskPickingGoodService.pageRecentCreatedManualBizId(queryDto);
            if (CollectionUtils.isEmpty(bizIdList)) {
                return;
            }
            JyPickingTaskBatchUpdateDto updateDto = new JyPickingTaskBatchUpdateDto();
            updateDto.setBizIdList(bizIdList);
            updateDto.setStatus(PickingGoodStatusEnum.PICKING_COMPLETE.getCode());
            updateDto.setCompleteNode(PickingCompleteNodeEnum.TIME_EXECUTE.getCode());
            jyBizTaskPickingGoodService.batchUpdateStatusByBizId(updateDto);
            pageNumber++;
        } while (CollectionUtils.isNotEmpty(bizIdList));
    }
}
