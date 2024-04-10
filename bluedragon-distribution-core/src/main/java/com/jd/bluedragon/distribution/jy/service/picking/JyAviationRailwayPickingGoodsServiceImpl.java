package com.jd.bluedragon.distribution.jy.service.picking;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.basedata.response.StreamlinedBasicSite;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyPickingSendBatchCodeStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum;
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
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodSubsidiaryEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

    private final String SYS_UPDATE_USER_ERP = "sys";
    private final String SYS_UPDATE_USER_NAME = "sys";


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
    @Autowired
    private RouterService routerService;
    @Autowired
    private PositionQueryJsfManager positionQueryJsfManager;
    @Autowired
    private SendMDao sendmDao;


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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.pickingGoodsScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<PickingGoodsRes> pickingGoodsScan(PickingGoodsReq request) {
        InvokeResult<PickingGoodsRes> res = new InvokeResult<>();
        PickingGoodsRes resData = new PickingGoodsRes();
        res.setData(resData);
        AirRailTaskAggDto airRailTaskAggDto = new AirRailTaskAggDto();
        resData.setAirRailTaskAggDto(airRailTaskAggDto);
        if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
            SendFlowDto sendFlowDto = new SendFlowDto();
            resData.setSendFlowDto(sendFlowDto);
        }
        //必填业务参数校验
        InvokeResult<String> paramCheckRes = new InvokeResult<>();
        if(!paramCheckService.pickingGoodScanParamCheck(request, paramCheckRes)) {
            res.parameterError(paramCheckRes.getMessage());
            return res;
        }
        //场地barCode锁
        if(!pickingGoodsCacheService.lockPickingGoodScan(request.getBarCode(), request.getCurrentOperate().getSiteCode())) {
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

            //异步相关处理
            this.sendJyPickingGoodScanMq(request, resData, taskPickingGoodEntity);

            //提货成功save缓存
            this.pickingGoodScanCache(request, resData,taskPickingGoodEntity);

            //返回结果处理
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
        if(!jyPickingSendDestinationService.existSendNextSite((long)request.getCurrentOperate().getSiteCode(), request.getNextSiteId(), request.getTaskType())){
            String siteName = this.getSiteNameBySiteId(request.getNextSiteId().intValue());
            res.parameterError(String.format("发货场地[%s|%s]未维护或已被删除，请先添加发货流向", request.getNextSiteId(), siteName));
            return false;
        }
        if(!misSendingCheck(request, res)) {
            return false;
        }
        return true;
    }


    private void convertPickingTask(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity taskPickingGoodEntity) {

        if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
            SendFlowDto sendFlowDto = this.fetchAggByPickingSendNextSite((long)request.getCurrentOperate().getSiteCode(), request.getTaskType(), null, request.getNextSiteId());

            SendFlowDto sendFlowDtoData = resData.getSendFlowDto();
            sendFlowDtoData.setMultipleScanNum(sendFlowDto.getMultipleScanNum());
            sendFlowDtoData.setWaitScanNum(sendFlowDto.getWaitScanNum());
            sendFlowDtoData.setHaveScannedNum(sendFlowDto.getHaveScannedNum());
            sendFlowDtoData.setCountFlag(sendFlowDto.getCountFlag());
            sendFlowDtoData.setSendCode(resData.getBatchCode());
            sendFlowDtoData.setPickingSiteId(request.getCurrentOperate().getSiteCode());
            sendFlowDtoData.setNextSiteId(request.getNextSiteId().intValue());
        }else {
            AirRailTaskAggDto airRailTaskAggDto = resData.getAirRailTaskAggDto();
            BeanUtils.copyProperties(taskPickingGoodEntity, airRailTaskAggDto);
            //待提总数
            Integer waitPickingTotalNum = this.getInitWaitPickingTotalItemNum(taskPickingGoodEntity.getBizId(), (long)request.getCurrentOperate().getSiteCode());
            airRailTaskAggDto.setInitWaitScanTotalNum(waitPickingTotalNum);

            //交接已提总数
            int realScanWaitPackageNum = aggsCacheService.getValueRealScanWaitPickingPackageNum(taskPickingGoodEntity.getBizId(), (long)request.getCurrentOperate().getSiteCode());
            int realScanWaitBoxNum = aggsCacheService.getValueRealScanWaitPickingBoxNum(taskPickingGoodEntity.getBizId(), (long)request.getCurrentOperate().getSiteCode());
            int handoverScanNum = realScanWaitPackageNum + realScanWaitBoxNum;
            airRailTaskAggDto.setHandoverScanTotalNum(handoverScanNum);

            //当前待提【待提总数-交接已提总数】
            int waitPickingTotalNumTemp = waitPickingTotalNum - handoverScanNum;
            airRailTaskAggDto.setWaitScanTotal(NumberHelper.gt0(waitPickingTotalNumTemp) ? waitPickingTotalNumTemp : 0);

            //多提总数
            int morePickingPackageNum = aggsCacheService.getValueRealScanMorePickingPackageNum(taskPickingGoodEntity.getBizId(), (long)request.getCurrentOperate().getSiteCode());
            int morePickingBoxNum = aggsCacheService.getValueRealScanMorePickingBoxNum(taskPickingGoodEntity.getBizId(), (long)request.getCurrentOperate().getSiteCode());
            int morePickingTotalNumTemp = morePickingPackageNum + morePickingBoxNum;
            Integer morePickingTotalNum = (BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode()).equals(resData.getTaskSource()) ? 0 : 1;
            airRailTaskAggDto.setMultipleScanTotal(NumberHelper.gt(morePickingTotalNumTemp, morePickingTotalNum) ? morePickingTotalNumTemp : morePickingTotalNum);

            //已提【交接已提总数+多提总数】
            int realPickingTotalNumTemp = handoverScanNum + morePickingTotalNumTemp;
            int realPickingTotalNum = 1;
            airRailTaskAggDto.setHaveScannedTotal(NumberHelper.gt(realPickingTotalNumTemp, realPickingTotalNum) ? realPickingTotalNumTemp : realPickingTotalNum);
        }

    }
    //初始化后的待提总件数
    private Integer getInitWaitPickingTotalItemNum(String bizId, Long siteId) {
        Integer num = aggsCacheService.getCacheInitWaitPickingTotalItemNum(bizId, siteId);
        if(!NumberHelper.gt0(num)) {
            List<String> bizIdList = Arrays.asList(bizId);
            List<PickingSendGoodAggsDto> list = jyPickingTaskAggsService.waitPickingInitTotalNum(bizIdList, siteId, null);
            if(CollectionUtils.isNotEmpty(list)) {
                num = list.get(0).getWaitSendTotalNum();
            }else {
                num = 0;
            }
        }
        return num;
    }

    //初始化后的待发总件数
    private Integer getInitWaitSendTotalItemNum(String bizId, Long siteId, Long nextSiteId) {
        Integer num = aggsCacheService.getCacheInitWaitSendTotalItemNum(bizId, siteId, nextSiteId);
        if(!NumberHelper.gt0(num)) {
            List<String> bizIdList = Arrays.asList(bizId);
            List<PickingSendGoodAggsDto> list = jyPickingTaskAggsService.waitPickingInitTotalNum(bizIdList, siteId, nextSiteId);
            if(CollectionUtils.isNotEmpty(list)) {
                num = list.get(0).getWaitSendTotalNum();
            }else {
                num = 0;
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

        scanDto.setPickingSiteId((long)request.getCurrentOperate().getSiteCode());
        scanDto.setGroupCode(request.getGroupCode());
        scanDto.setSendGoodFlag(request.getSendGoodFlag());
        if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
            scanDto.setNextSiteId(request.getNextSiteId());
        }
        scanDto.setMoreScanFlag(!BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(pickingGoodsRes.getTaskSource()));
        scanDto.setUser(request.getUser());
        scanDto.setBoxConfirmNextSiteKey(request.getBoxConfirmNextSiteKey());
        scanDto.setForceSendFlag(request.getForceSendFlag());
        scanDto.setOperateTime(pickingGoodsRes.getOperateTime());

        scanDto.setSysTime(System.currentTimeMillis());
        String msg = JsonHelper.toJson(scanDto);
        logInfo("提货扫描异步消息生产，businessId={},msg={}", request.getBarCode(), msg);
        pickingGoodScanProducer.sendOnFailPersistent(request.getBarCode(), msg);
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
            if(!Objects.isNull(taskPickingGoodEntity) && !PickingGoodStatusEnum.PICKING_COMPLETE.getCode().equals(taskPickingGoodEntity.getStatus()) && !JyBizTaskPickingGoodEntity.INTERCEPT_FLAG.equals(taskPickingGoodEntity.getIntercept())) {
                logInfo("扫描单据{}查提货任务为空-待提任务取上次扫描任务{}", request.getBarCode(), JsonHelper.toJson(taskPickingGoodEntity));
                resData.setTaskSource(BarCodeFetchPickingTaskRuleEnum.LAST_PICKING_TASK.getCode());
                return taskPickingGoodEntity;
            }else {
                logInfo("提货扫描查询上一次扫描提货任务无效，走无任务模式，request={}", JsonHelper.toJson(request));
            }
        }
        //待提货任务为空时生成自建待提任务
        JyBizTaskPickingGoodEntity manualCreateTask = jyBizTaskPickingGoodService.findLatestEffectiveManualCreateTask((long)request.getCurrentOperate().getSiteCode(), request.getTaskType());
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
        bodyDto.setBatchCode(jyPickingSendDestinationService.findOrGenerateBatchCode(
                (long)request.getCurrentOperate().getSiteCode(), request.getNextSiteId(), request.getUser(), request.getTaskType()));
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
        List<PickingGoodScanTaskBodyDto> mqBodyList = Arrays.asList(bodyDto);
        String bodyJson = JsonHelper.toJson(mqBodyList);
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBody(bodyJson);
        taskRequest.setBoxCode(request.getBarCode());
        taskRequest.setKeyword1(String.valueOf(request.getCurrentOperate().getSiteCode()));
        taskRequest.setOperateTime(DateHelper.formatDateTime(new Date()));
        taskRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskRequest.setSiteName(request.getCurrentOperate().getSiteName());
        taskRequest.setType(Task.TASK_TYPE_OFFLINE);
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
        if(Boolean.TRUE.equals(request.getForceSendFlag()) || Boolean.TRUE.equals(request.getSendNextSiteSwitch())) {
            return true;
        }
        PickingGoodsRes resData = res.getData();

        Integer routerNextSiteId = null;
        String routerNextSiteName = null;
        String boxConfirmNextSiteKey = null;
        if(!BusinessUtil.isBoxcode(request.getBarCode())) {
            BaseStaffSiteOrgDto dto = routerService.getRouteNextSiteByWaybillCode(request.getCurrentOperate().getSiteCode(), WaybillUtil.getWaybillCode(request.getBarCode()));
            if(!Objects.isNull(dto)) {
                routerNextSiteName = dto.getSiteName();
                routerNextSiteId = dto.getSiteCode();
            }
        }else {
            BoxNextSiteDto boxNextSiteDto = routerService.getRouteNextSiteByBox(request.getCurrentOperate().getSiteCode(), request.getBarCode());
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
        if(!request.getNextSiteId().equals(routerNextSiteId.longValue())) {
            resData.setNextSiteSupportSwitch(jyPickingSendDestinationService.existSendNextSite((long)request.getCurrentOperate().getSiteCode(), routerNextSiteId.longValue(), request.getTaskType()));
            resData.setRouterNextSiteId(routerNextSiteId);
            resData.setRouterNextSiteName(routerNextSiteName);
            resData.setBoxConfirmNextSiteKey(boxConfirmNextSiteKey);
            String msg = String.format(PickingGoodsRes.CODE_30001_MSG_1, routerNextSiteName, request.getNextSiteName());
            res.customMessage(PickingGoodsRes.CODE_30001, msg);
            return false;
        }
        resData.setBoxConfirmNextSiteKey(boxConfirmNextSiteKey);
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
        List<PickingGoodScanTaskBodyDto> bodyList = Arrays.asList(bodyDto);
        String bodyJson = JsonHelper.toJson(bodyList);

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBody(bodyJson);
        taskRequest.setBoxCode(request.getBarCode());
        taskRequest.setKeyword1(String.valueOf(request.getCurrentOperate().getSiteCode()));
        taskRequest.setOperateTime(DateHelper.formatDateTime(new Date()));
        taskRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskRequest.setSiteName(request.getCurrentOperate().getSiteName());
        taskRequest.setType(Task.TASK_TYPE_OFFLINE);
        taskRequest.setUserCode(request.getUser().getUserCode());
        taskRequest.setUserName(request.getUser().getUserName());
        taskService.add(taskRequest);
        res.getData().setOperateTime(date.getTime());
        return true;
    }

    @Override
    public JyBizTaskPickingGoodEntity fetchWaitPickingBizIdByBarCode(Long siteCode, String barCode) {
        String bizId = jyPickingSendRecordService.fetchWaitPickingBizIdByBarCode(siteCode, barCode);
        if(StringUtils.isBlank(bizId)) {
            logInfo("{}|{}未查到待提货任务BizId", barCode, siteCode);
            return null;
        }
        JyBizTaskPickingGoodEntity pickingGoodEntity = jyBizTaskPickingGoodService.findByBizIdWithYn(bizId, false);
        if(!PickingGoodStatusEnum.PICKING_COMPLETE.getCode().equals(pickingGoodEntity.getStatus()) && !JyBizTaskPickingGoodEntity.INTERCEPT_FLAG.equals(pickingGoodEntity.getIntercept())) {
            return pickingGoodEntity;
        }
        logInfo("{}|{}未查到待提货任务", barCode, siteCode);
        return null;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.finishPickGoods",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Void> finishPickGoods(FinishPickGoodsReq req) {
        InvokeResult<Void> ret = new InvokeResult<>();
        if (StringUtils.isEmpty(req.getBizId())) {
            ret.parameterError("参数错误：提货任务BizId不能为空！");
            return ret;
        }
        closeScheduleTask(req.getBizId(), req.getUser().getUserErp(), req.getUser().getUserName());
        boolean success = jyBizTaskPickingGoodService.finishPickingTaskByBizId(req.getBizId(), PickingCompleteNodeEnum.COMPLETE_BTN.getCode(), req.getUser());
        if (!success) {
            log.warn("jyBizTaskPickingGoodService 根据bizId={} 完成提货任务状态失败！", req.getBizId());
        }
        return ret;
    }

    private boolean closeScheduleTask(String bizId, String updateUserErp, String updateUserName) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(bizId);
        req.setTaskType(String.valueOf(JyScheduleTaskTypeEnum.PICKING.getCode()));
        req.setOpeUser(updateUserErp);
        req.setOpeUserName(updateUserName);
        req.setOpeTime(new Date());
        JyScheduleTaskResp res = jyScheduleTaskManager.closeScheduleTask(req);
        if(Objects.isNull(res)) {
            log.error("提货岗关闭调度任务返回null, 参数={}", JsonUtils.toJSONString(req));
            throw new JyBizException("关闭调度任务异常返回null:" + bizId);
        }
        return true;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.submitException",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Void> submitException(ExceptionSubmitReq req) {
        InvokeResult<Void> ret = new InvokeResult<>();
        if (StringUtils.isEmpty(req.getBizId())) {
            ret.parameterError("参数错误：提货任务BizId不能为空！");
            return ret;
        }

        JyBizTaskPickingGoodEntity entity = jyBizTaskPickingGoodService.findByBizIdWithYn(req.getBizId(), false);
        if(Objects.isNull(entity)) {
            ret.parameterError("提货任务不存在");
            return ret;
        }
        if(PickingGoodStatusEnum.PICKING_COMPLETE.getCode().equals(entity.getStatus()) && PickingCompleteNodeEnum.EXCEPTION_BTN.getCode() != entity.getCompleteNode()) {
            ret.error("该任务已经完成，无需提报异常");
            return ret;
        }
        if(Objects.isNull(entity.getCompleteNode()) || PickingCompleteNodeEnum.EXCEPTION_BTN.getCode() != entity.getCompleteNode()) {
            jyBizTaskPickingGoodService.finishPickingTaskByBizId(req.getBizId(), PickingCompleteNodeEnum.EXCEPTION_BTN.getCode(), req.getUser());
        }

        this.closeScheduleTask(req.getBizId(), req.getUser().getUserErp(), req.getUser().getUserName());

        String title = "空铁提货任务异常提报提醒";
        String template = "航班号%s提货差异异常，待提件数、已提件数、多提件数，请尽快联系上游排查";
        String content = String.format(template, req.getServiceNumber());
        String positionCode = req.getCurrentOperate().getOperatorData().getPositionCode();
        InvokeResult invokeResult = positionQueryJsfManager.pushInfoToPositionMainErp(req.getUser().getUserErp(), positionCode, title, content);
        if(!invokeResult.codeSuccess()) {
            log.error("异常提报推送网格负责人失败,req={}, invokeResult={]", JSON.toJSONString(req), JSON.toJSONString(invokeResult));
            ret.error("异常提报推送网格负责人失败");
            return ret;
        }
        return ret;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.listSendFlowInfo",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
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

        if (SendFlowDisplayEnum.COUNT.getCode().equals(req.getDisplayType())) {
            JyPickingTaskBatchQueryDto queryDto = buildBatchQueryDto(req);
            List<String> bizList = jyBizTaskPickingGoodService.listAllBizByPickingSiteId(queryDto);
            if (CollectionUtils.isEmpty(bizList)) {
                return invokeResult;
            }
            CallerInfo info = Profiler.registerInfo("JyAviationRailwayPickingGoodsServiceImpl.countSendFlowInfo", Constants.UMP_APP_NAME_DMSWEB,false, true);
            try {
                for (SendFlowDto dto : dtoList) {
                    // 流向待提数
                    int waitScanNum = 0;
                    // 流向已提数
                    int haveScannedNum = 0;
                    // 流向多提数
                    int multipleScanNum = 0;
                    // 流向对应biz任务提货数据
                    List<PickingSendGoodAggsDto> aggsDtoList = jyPickingTaskAggsService.findPickingAgg(bizList, (long) req.getCurrentOperate().getSiteCode(), (long) dto.getNextSiteId());
                    for (PickingSendGoodAggsDto aggsDto : aggsDtoList) {
                        waitScanNum += aggsDto.getWaitSendTotalNum();
                        haveScannedNum += aggsDto.getRealSendTotalNum();
                        multipleScanNum += aggsDto.getMoreSendTotalNum();
                    }

                    dto.setWaitScanNum(waitScanNum);
                    dto.setHaveScannedNum(haveScannedNum);
                    dto.setMultipleScanNum(multipleScanNum);
                    dto.setCountFlag(true);
                }
            } catch (Exception e) {
                log.warn("统计流向数据异常 req={}", JsonHelper.toJson(req), e);
                Profiler.functionError(info);
            }finally {
                Profiler.registerInfoEnd(info);
            }
        }

        return invokeResult;
    }


    /**
     * 查询提货场地统计数据  或  提货场地发一个具体流向的统计
     * @param pickingSiteId 必填：提货场地
     * @param taskType 可选：空提任务类型，为空时默认航空任务类型
     * @param endNodeCodeList 可选：目的机场列表： 非空时作为统计条件
     * @param nextSiteId   可选：发货流向， 非空是作为统计条件
     */
    private SendFlowDto fetchAggByPickingSendNextSite(Long pickingSiteId, Integer taskType, List<String> endNodeCodeList, Long nextSiteId) {
        SendFlowDto res = new SendFlowDto();
        res.setPickingSiteId(pickingSiteId.intValue());
        res.setNextSiteId(nextSiteId.intValue());
        res.setCountFlag(true);

        List<String> bizIdList = queryNeedfulAggBizId(pickingSiteId, endNodeCodeList, taskType);
        if(CollectionUtils.isEmpty(bizIdList)) {
            res.setHaveScannedNum(0);
            res.setMultipleScanNum(0);
            res.setWaitScanNum(0);
            return res;
        }

        int waitScanNum = 0;
        int haveScannedNum = 0;
        int multipleScanNum = 0;
        List<PickingSendGoodAggsDto> aggsDtoList = jyPickingTaskAggsService.findPickingAgg(bizIdList, pickingSiteId, nextSiteId);
        for (PickingSendGoodAggsDto aggsDto : aggsDtoList) {
            waitScanNum += aggsDto.getWaitSendTotalNum();
            haveScannedNum += aggsDto.getRealSendTotalNum();
            multipleScanNum += aggsDto.getMoreSendTotalNum();
        }
        res.setWaitScanNum(waitScanNum);
        res.setHaveScannedNum(haveScannedNum);
        res.setMultipleScanNum(multipleScanNum);
        return res;
    }

    /**
     * 查询统计需要的bizId,
     * 任务列表多个统计需要查找最近几天的bizId, 根据bizId，反查统计数据
     * @param pickingSiteId
     * @param endNodeCodeList
     * @return
     */
    private List<String> queryNeedfulAggBizId(Long pickingSiteId, List<String> endNodeCodeList, Integer taskType) {
        JyPickingTaskBatchQueryDto queryDto = new JyPickingTaskBatchQueryDto();
        queryDto.setPickingSiteId(pickingSiteId);
        queryDto.setTaskType(Objects.isNull(taskType) ? PickingGoodTaskTypeEnum.AVIATION.getCode() : taskType);
        queryDto.setCreateTime(this.getStartTime());
        if(!CollectionUtils.isEmpty(endNodeCodeList)) {
            queryDto.setPickingNodeCodeList(endNodeCodeList);
        }
        return jyBizTaskPickingGoodService.listAllBizByPickingSiteId(queryDto);
    }


    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.addSendFlow",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Void> addSendFlow(SendFlowAddReq req) {
        InvokeResult<Void> ret = new InvokeResult<>();
        if (CollectionUtils.isEmpty(req.getSiteList())) {
            ret.parameterError("所选流向场地不能为空！");
            return ret;
        }
        for (StreamlinedBasicSite site : req.getSiteList()) {
            if (site.getSiteCode() == req.getCurrentOperate().getSiteCode()) {
                ret.parameterError("不能添加流向为本场地的流向！");
                return ret;
            }
        }
        InvokeResult<Boolean> success = jyPickingSendDestinationService.addSendFlow(req);
        if (!success.codeSuccess()) {
            ret.customMessage(success.getCode(), success.getMessage());
        }
        return ret;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.deleteSendFlow",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Void> deleteSendFlow(SendFlowDeleteReq req) {
        InvokeResult<Void> ret = new InvokeResult<>();
        if (req.getNextSiteId() == null) {
            ret.parameterError("所选流向场地id不能为空！");
            return ret;
        }
        if (req.getTaskType() == null) {
            ret.parameterError("提货任务类型不能为空！");
            return ret;
        }
        JyPickingSendDestinationDetailEntity entity = jyPickingSendDestinationService.getSendDetailBySiteId((long) req.getCurrentOperate().getSiteCode(), (long) req.getNextSiteId(), req.getTaskType());
        if (entity != null && StringUtils.isNotEmpty(entity.getSendCode())) {
            ret.customMessage(InvokeResult.AIR_RAIL_SEND_FLOW_DELETE_FAIL_CODE, InvokeResult.AIR_RAIL_SEND_FLOW_DELETE_FAIL_MESSAGE);
            return ret;
        }
        jyPickingSendDestinationService.deleteSendFlow(req);
        return ret;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.completePickingSendTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<FinishSendTaskRes> completePickingSendTask(FinishSendTaskReq req) {
        InvokeResult<FinishSendTaskRes> res = new InvokeResult<>();
        FinishSendTaskRes resData = new FinishSendTaskRes();
        res.setData(resData);
        if (!this.finishPickingSendTaskBaseCheck(req, res)) {
            return res;
        }
        int curSiteId = req.getCurrentOperate().getSiteCode();
        Integer nextSiteId = req.getNextSiteId();
        if(!pickingGoodsCacheService.lockPickingSendTaskComplete(curSiteId, nextSiteId)) {
            res.error("该流向正在操作任务完成，请稍后重试");
            return res;
        }
        String sendCode = null;
        try{
            sendCode = jyPickingSendDestinationService.fetchLatestNoCompleteBatchCode((long)curSiteId, nextSiteId.longValue(), req.getTaskType());
            if (StringUtils.isEmpty(sendCode)) {
                res.customMessage(InvokeResult.AVRA_SEND_TASK_FINISHED_CODE, InvokeResult.AVRA_SEND_TASK_FINISHED_MESSAGE);
                return res;
            }
            req.setSendCode(sendCode);

            Integer scanItemNum = sendmDao.countBoxCodeNumBySendCode(sendCode, curSiteId);
            req.setScanItemNum(scanItemNum);

            resData.setSendCode(sendCode);
            resData.setScanItemNum(scanItemNum);
            jyPickingSendDestinationService.finishSendTask(req);
            return res;
        }catch (Exception ex) {
            log.error("提货发货任务完成异常，请联系分拣小秘,req={},sendCode={},errMsg={}", JSON.toJSONString(req), sendCode, ex.getMessage(), ex);
            res.error("提货发货任务完成异常，请联系分拣小秘");
            return res;
        }finally {
            pickingGoodsCacheService.unlockPickingSendTaskComplete(curSiteId, nextSiteId);
        }
    }


    //check: true-success; false-error
    private boolean finishPickingSendTaskBaseCheck(FinishSendTaskReq req, InvokeResult<FinishSendTaskRes> invokeResult) {
        if(Objects.isNull(req) || Objects.isNull(invokeResult)) {
            return true;
        }
        if (req.getNextSiteId() == null) {
            invokeResult.error("目的场地编码不能为空！");
            return false;
        }
        if(!PickingGoodTaskTypeEnum.legalCheck(req.getTaskType())) {
            invokeResult.error("任务类型不合法");
            return false;
        }
        return true;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.listAirRailTaskSummary",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<AirRailTaskRes> listAirRailTaskSummary(AirRailTaskSummaryReq req) {
        InvokeResult<AirRailTaskRes> ret = new InvokeResult<>();
        listAirRailTaskSummaryCheck(req, ret);
        if (!ret.codeSuccess()) {
            return ret;
        }
        AirRailTaskRes res = new AirRailTaskRes();
        ret.setData(res);
        Long currentSiteId = (long) req.getCurrentOperate().getSiteCode();
        // 转换搜索关键字
        List<String> bizIdList = resolveKeyword(req.getKeyword(), req.getCurrentOperate().getSiteCode(), req.getTaskType());
        // count当前场地各个status的任务数
        calculateCountStatus(req, bizIdList, res);
//        todo laoqingchang
        // 根据搜索关键字查询bizId没有查到则返回
        if (StringUtils.isNotEmpty(req.getKeyword()) && CollectionUtils.isEmpty(bizIdList)) {
            return ret;
        }

        // 按机场/车站分组逻辑分页查询
        JyPickingTaskGroupQueryDto groupQueryDto = buildGroupQueryDto(req, bizIdList);
        List<JyBizTaskPickingGoodEntity> groupedTask = jyBizTaskPickingGoodService.listTaskGroupByPickingNodeCode(groupQueryDto);
        if (CollectionUtils.isEmpty(groupedTask)) {
            return ret;
        }
        // 查询明细数据
        JyPickingTaskBatchQueryDto batchQueryDto = buildBatchQueryDto(req, bizIdList, groupedTask);
        List<JyBizTaskPickingGoodEntity> taskDetail = jyBizTaskPickingGoodService.listTaskByPickingNodeCode(batchQueryDto);
        if (CollectionUtils.isEmpty(taskDetail)) {
            return ret;
        }
        // 查询统计数据
        List<String> bizList = taskDetail.stream().map(JyBizTaskPickingGoodEntity::getBizId).distinct().collect(Collectors.toList());
        List<PickingSendGoodAggsDto> aggsDtoList = jyPickingTaskAggsService.findPickingAgg(bizList, currentSiteId, null);
        // 计算统计总和
        calculateSummaryResponse(res, taskDetail, aggsDtoList);

        return ret;
    }

    /**
     * 将搜索关键字转化成提货任务bizId
     * @param keyword
     * @param currentSiteId
     * @param taskType
     * @return
     */
    private List<String> resolveKeyword(String keyword, Integer currentSiteId, Integer taskType) {
        List<String> bizList = new ArrayList<>();
        if (StringUtils.isEmpty(keyword)) {
            return bizList;
        }
        Integer lastSiteId = null;
        if (WaybillUtil.isPackageCode(keyword)) {
            String waybillCode = WaybillUtil.getWaybillCode(keyword);
            RouteNextDto routeNextDto = routerService.matchNextNodeAndLastNodeByRouter(currentSiteId, waybillCode, null);
            if (routeNextDto == null) {
                return bizList;
            }
            lastSiteId = routeNextDto.getFirstLastSiteId();
        } else if (NumberUtils.isDigits(keyword)){
            lastSiteId = Integer.valueOf(keyword);
        }
        if (lastSiteId == null) {
            return bizList;
        }
        JyBizTaskPickingGoodSubsidiaryEntity entity = new JyBizTaskPickingGoodSubsidiaryEntity();
        entity.setStartSiteId((long) lastSiteId);
        entity.setEndSiteId((long) currentSiteId);
        entity.setTaskType(taskType);
        entity.setCreateTime(getStartTime());
        bizList = jyBizTaskPickingGoodService.listBizIdByLastSiteId(entity);
        return bizList.stream().distinct().collect(Collectors.toList());
    }

    private void calculateCountStatus(AirRailTaskSummaryReq req, List<String> bizIdList, AirRailTaskRes res) {
        List<AirRailTaskCountDto> countDtoList;
        if (StringUtils.isNotEmpty(req.getKeyword()) && CollectionUtils.isEmpty(bizIdList)) {
            countDtoList = new ArrayList<>();
            for (PickingGoodStatusEnum statusEnum : PickingGoodStatusEnum.values()) {
                AirRailTaskCountDto countDto = new AirRailTaskCountDto();
                countDto.setStatus(statusEnum.getCode());
                countDto.setStatusName(statusEnum.getName());
                countDto.setCount(Constants.NUMBER_ZERO);

                countDtoList.add(countDto);
            }
        } else {
            AirRailTaskCountQueryDto countQueryDto = buildCountQueryDto(req, bizIdList);
            countDtoList = jyBizTaskPickingGoodService.countAllStatusByPickingSiteId(countQueryDto);
        }

        res.setCountDtoList(countDtoList);
    }

    private Date getStartTime() {
        return DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -dmsConfigManager.getUccPropertyConfiguration().getJyBizTaskPickingGoodTimeRange());
    }

    private Date getPlanArriveTime() {
        return DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -dmsConfigManager.getUccPropertyConfiguration().getPickingPlanArriveTimeRange());
    }

    private Date getRealArriveTime() {
        return DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -dmsConfigManager.getUccPropertyConfiguration().getPickingRealArriveTimeRange());
    }

    private Date getPickingFinishTime() {
        return DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -dmsConfigManager.getUccPropertyConfiguration().getPickingFinishTimeRange());
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
        if (req.getPageSize() == null || req.getPageNum() == null) {
            invokeResult.parameterError("分页参数不能为空！");
            return;
        }
        if (req.getKeyword() != null) {
            req.setKeyword(req.getKeyword().trim());
        }
    }

    private AirRailTaskCountQueryDto buildCountQueryDto(AirRailTaskSummaryReq req, List<String> bizIdList) {
        AirRailTaskCountQueryDto queryDto = new AirRailTaskCountQueryDto();
        queryDto.setPickingSiteId(req.getCurrentOperate().getSiteCode());
        queryDto.setTaskType(req.getTaskType());
        queryDto.setBizIdList(bizIdList);
        queryDto.setCreateTime(getStartTime());
        queryDto.setNodePlanArriveTime(getPlanArriveTime());
        queryDto.setNodeRealArriveTime(getRealArriveTime());
        queryDto.setPickingCompleteTime(getPickingFinishTime());

        return queryDto;
    }

    private JyPickingTaskGroupQueryDto buildGroupQueryDto(AirRailTaskSummaryReq req, List<String> bizIdList) {
        JyPickingTaskGroupQueryDto queryDto = new JyPickingTaskGroupQueryDto();
        queryDto.setPickingSiteId((long) req.getCurrentOperate().getSiteCode());
        queryDto.setStatus(req.getStatus());
        queryDto.setTaskType(req.getTaskType());
        queryDto.setCreateTime(getStartTime());
        queryDto.setNodePlanArriveTime(getPlanArriveTime());
        queryDto.setNodeRealArriveTime(getRealArriveTime());
        queryDto.setPickingCompleteTime(getPickingFinishTime());
        queryDto.setBizIdList(bizIdList);

        queryDto.setOffset((req.getPageNum() - 1) * req.getPageSize());
        queryDto.setLimit(req.getPageSize());
        return queryDto;
    }

    private JyPickingTaskBatchQueryDto buildBatchQueryDto(AirRailTaskSummaryReq req, List<String> bizIdList, List<JyBizTaskPickingGoodEntity> groupByTask) {
        JyPickingTaskBatchQueryDto queryDto = new JyPickingTaskBatchQueryDto();
        queryDto.setPickingSiteId((long) req.getCurrentOperate().getSiteCode());
        queryDto.setStatus(req.getStatus());
        queryDto.setTaskType(req.getTaskType());
        queryDto.setOffset(0);
        queryDto.setLimit(1024);
        queryDto.setCreateTime(getStartTime());
        queryDto.setBizIdList(bizIdList);
        queryDto.setNodePlanArriveTime(getPlanArriveTime());
        queryDto.setNodeRealArriveTime(getRealArriveTime());
        queryDto.setPickingCompleteTime(getPickingFinishTime());

        List<String> pickingNodeCodes = groupByTask.stream().map(JyBizTaskPickingGoodEntity::getEndNodeCode).distinct().collect(Collectors.toList());
        queryDto.setPickingNodeCodeList(pickingNodeCodes);
        return queryDto;
    }

    private JyPickingTaskBatchQueryDto buildBatchQueryDto(SendFlowReq req) {
        JyPickingTaskBatchQueryDto queryDto = new JyPickingTaskBatchQueryDto();
        queryDto.setPickingSiteId((long) req.getCurrentOperate().getSiteCode());
        queryDto.setCreateTime(getStartTime());
        queryDto.setTaskType(req.getTaskType() == null ? PickingGoodTaskTypeEnum.AVIATION.getCode() : req.getTaskType());

        return queryDto;
    }

    private void calculateSummaryResponse(AirRailTaskRes res, List<JyBizTaskPickingGoodEntity> taskDetail, List<PickingSendGoodAggsDto> aggsDtoList) {
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
            airRailDto.setTaskNum(taskList.size());
            airRailDto.setPickingNodeCode(taskList.get(0).getEndNodeCode());
            airRailDto.setPickingNodeName(taskList.get(0).getEndNodeName());

            airRailDtoList.add(airRailDto);
        }
        res.setAirRailDtoList(airRailDtoList);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.listAirRailTaskAgg",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<AirRailTaskAggRes> listAirRailTaskAgg(AirRailTaskAggReq req) {
        InvokeResult<AirRailTaskAggRes> ret = new InvokeResult<>();
        AirRailTaskAggRes res = new AirRailTaskAggRes();
        List<AirRailTaskAggDto> taskAggDtoList = new ArrayList<>();
        res.setTaskAggDtoList(taskAggDtoList);
        listAirRailTaskAggCheck(req, ret);
        ret.setData(res);
        if (!ret.codeSuccess()) {
            return ret;
        }

        long currentSiteId = req.getCurrentOperate().getSiteCode();
        List<String> bizIdList = resolveKeyword(req.getKeyword(), req.getCurrentOperate().getSiteCode(), req.getTaskType());
        JyPickingTaskBatchQueryDto batchQueryDto = buildBatchQueryDto(req, bizIdList);
        // 根据搜索关键字查询bizId没有查到则返回
        if (StringUtils.isNotEmpty(req.getKeyword()) && CollectionUtils.isEmpty(batchQueryDto.getBizIdList())) {
            return ret;
        }
        // 查询分页数据
        List<JyBizTaskPickingGoodEntity> pageDetail = jyBizTaskPickingGoodService.listTaskByPickingNodeCode(batchQueryDto);

        // 查询上游信息
        List<String> bizList = pageDetail.stream().map(JyBizTaskPickingGoodEntity::getBizId).distinct().collect(Collectors.toList());
        List<JyBizTaskPickingGoodSubsidiaryEntity> subsidiaryEntityList = jyBizTaskPickingGoodService.listBatchInfoByBizId(bizList);
        // 查询统计数据
        List<String> allBizList = jyBizTaskPickingGoodService.listAllBizByPickingSiteId(batchQueryDto);
        List<PickingSendGoodAggsDto> aggsDtoList = jyPickingTaskAggsService.findPickingAgg(allBizList, currentSiteId, null);
        // 计算统计总和
        calculateAggResponse(res, pageDetail, aggsDtoList, subsidiaryEntityList);
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
        if (req.getKeyword() != null) {
            req.setKeyword(req.getKeyword().trim());
        }
    }

    private JyPickingTaskBatchQueryDto buildBatchQueryDto(AirRailTaskAggReq req, List<String> bizIdList) {
        JyPickingTaskBatchQueryDto queryDto = new JyPickingTaskBatchQueryDto();
        queryDto.setPickingSiteId((long) req.getCurrentOperate().getSiteCode());
        queryDto.setStatus(req.getStatus());
        queryDto.setTaskType(req.getTaskType());

        if (req.getPageNum() == null) {
            req.setPageNum(Constants.DEFAULT_PAGE_NO);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        }
        queryDto.setOffset((req.getPageNum() - 1) * req.getPageSize());
        queryDto.setLimit(req.getPageSize());
        queryDto.setBizIdList(bizIdList);
        queryDto.setCreateTime(getStartTime());
        queryDto.setNodePlanArriveTime(getPlanArriveTime());
        queryDto.setNodeRealArriveTime(getRealArriveTime());
        queryDto.setPickingCompleteTime(getPickingFinishTime());

        if (StringUtils.isNotEmpty(req.getPickingNodeCode())) {
            List<String> pickingNodeCodes = Collections.singletonList(req.getPickingNodeCode());
            queryDto.setPickingNodeCodeList(pickingNodeCodes);
        }
        return queryDto;
    }

    private void calculateAggResponse(AirRailTaskAggRes res, List<JyBizTaskPickingGoodEntity> taskDetail, List<PickingSendGoodAggsDto> aggsDtoList, List<JyBizTaskPickingGoodSubsidiaryEntity> subsidiaryEntityList) {

        // 当前场地总数待提数量
        int currentSiteWaitScan = 0;
        // 当前场地已提总数
        int currentSiteHaveScanned = 0;
        // 当前场地多提总数
        int currentSiteMultipleScan = 0;
        for(PickingSendGoodAggsDto dto : aggsDtoList) {
            currentSiteWaitScan += dto.getWaitSendTotalNum();
            currentSiteHaveScanned += dto.getRealSendTotalNum();
            currentSiteMultipleScan += dto.getMoreSendTotalNum();
        }
        res.setCurrentSiteWaitScan(currentSiteWaitScan);
        res.setCurrentSiteHaveScanned(currentSiteHaveScanned);
        res.setCurrentSiteMultipleScan(currentSiteMultipleScan);

        List<AirRailTaskAggDto> taskAggDtoList = res.getTaskAggDtoList();
        Map<String, PickingSendGoodAggsDto> aggMappedByBizId = aggsDtoList.stream().collect(Collectors.toMap(PickingSendGoodAggsDto::getBizId, item -> item, (first, second) -> first));
        Map<String, List<JyBizTaskPickingGoodSubsidiaryEntity>> batchInfoGroupedByBizId = subsidiaryEntityList.stream().collect(Collectors.groupingBy(JyBizTaskPickingGoodSubsidiaryEntity::getBizId));

        for (JyBizTaskPickingGoodEntity task : taskDetail) {
            AirRailTaskAggDto dto = new AirRailTaskAggDto();
            dto.setBizId(task.getBizId());
            dto.setPickingTime(task.getPickingStartTime());
            dto.setNoTaskFlag(Constants.NUMBER_ONE.equals(task.getManualCreatedFlag()));
            dto.setNodePlanArriveTime(task.getNodePlanArriveTime());
            dto.setNodeRealArriveTime(task.getNodeRealArriveTime());
            dto.setPickingCompleteTime(task.getPickingCompleteTime());
            dto.setServiceNumber(task.getServiceNumber());

            List<JyBizTaskPickingGoodSubsidiaryEntity> batchInfoList = batchInfoGroupedByBizId.get(task.getBizId());
            if (CollectionUtils.isNotEmpty(batchInfoList)) {
                List<Integer> startSiteIdList = batchInfoList.stream().map(item -> item.getStartSiteId().intValue()).distinct().collect(Collectors.toList());
                List<String> startSiteNameList = new ArrayList<>();
                for (Integer siteId : startSiteIdList) {
                    BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(siteId);
                    if (siteOrgDto != null) {
                        startSiteNameList.add(siteOrgDto.getSiteName());
                    }
                }
                dto.setStartSiteIdList(startSiteIdList);
                dto.setStartSiteNameList(startSiteNameList);
            }

            if (task.getNodeRealArriveTime() == null && PickingGoodStatusEnum.TO_PICKING.getCode().equals(task.getStatus())) {
                dto.setWaitScanTotal(task.getCargoNumber());
                dto.setHaveScannedTotal(0);
                dto.setMultipleScanTotal(0);

                taskAggDtoList.add(dto);
                continue;
            }
            PickingSendGoodAggsDto aggsDto = aggMappedByBizId.get(task.getBizId());
            if (aggsDto != null) {
                dto.setWaitScanTotal(aggsDto.getWaitSendTotalNum());
                dto.setHaveScannedTotal(aggsDto.getRealSendTotalNum());
                dto.setMultipleScanTotal(aggsDto.getMoreSendTotalNum());
            }

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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.finishTaskWhenWaitScanEqZero",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public void finishTaskWhenWaitScanEqZero() {
        Date startTime = getStartTime();
        Date pickingStartTime = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -dmsConfigManager.getPropertyConfig().getPickingGoodTaskWaitScanEq0TimeRange());
        JyBizTaskPickingGoodQueryDto pickingGoodQueryDto = new JyBizTaskPickingGoodQueryDto();
        pickingGoodQueryDto.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
        pickingGoodQueryDto.setStatus(PickingGoodStatusEnum.PICKING.getCode());
        // 兜底时间
        pickingGoodQueryDto.setStartTime(startTime);
        // 业务卡控时间
        pickingGoodQueryDto.setPickingStartTime(pickingStartTime);
        pickingGoodQueryDto.setLimit(1000);

        int pageNumber = 1;
        List<String> bizIdList;
        do {
            pickingGoodQueryDto.setOffset((pageNumber - 1) * pickingGoodQueryDto.getLimit());
            logInfo("pageRecentCreatedNoManualPickingBiz req = {}", JsonHelper.toJson(pickingGoodQueryDto));
            bizIdList = jyBizTaskPickingGoodService.pageRecentCreatedNoManualPickingBiz(pickingGoodQueryDto);
            logInfo("pageRecentCreatedNoManualPickingBiz resp = {}", JsonHelper.toJson(bizIdList));
            if (CollectionUtils.isEmpty(bizIdList)) {
                return;
            }
            JyPickingTaskAggQueryDto aggQueryDto = new JyPickingTaskAggQueryDto();
            aggQueryDto.setBizIdList(bizIdList);
            bizIdList = jyPickingTaskAggsService.filterRecentWaitScanEqZeroBiz(aggQueryDto);
            logInfo("filterRecentWaitScanEqZeroBiz resp = {}", JsonHelper.toJson(bizIdList));

            if (CollectionUtils.isEmpty(bizIdList)) {
                return;
            }
            batchFinishPickingTaskByBizId(bizIdList, PickingCompleteNodeEnum.WAIT_SCAN_0.getCode());
            pageNumber++;
        } while (CollectionUtils.isNotEmpty(bizIdList));
    }

    private void batchFinishPickingTaskByBizId(List<String> bizIdList, Integer completeNode) {
        bizIdList.forEach(item -> {
            closeScheduleTask(item, SYS_UPDATE_USER_ERP, SYS_UPDATE_USER_NAME);
        });
        Date now = new Date();
        JyPickingTaskBatchUpdateDto updateDto = new JyPickingTaskBatchUpdateDto();
        updateDto.setBizIdList(bizIdList);
        updateDto.setStatus(PickingGoodStatusEnum.PICKING_COMPLETE.getCode());
        updateDto.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
        updateDto.setCompleteNode(completeNode);
        updateDto.setPickingCompleteTime(now);
        updateDto.setUpdateTime(now);
        jyBizTaskPickingGoodService.batchFinishPickingTaskByBizId(updateDto);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsServiceImpl.finishTaskWhenTimeExceed",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public void finishTaskWhenTimeExceed() {
        Date startTime = getStartTime();
        Date endTime = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -dmsConfigManager.getPropertyConfig().getPickingGoodTaskManualTimeRange());
        JyBizTaskPickingGoodQueryDto queryDto = new JyBizTaskPickingGoodQueryDto();
        int pageNumber = 1;
        queryDto.setLimit(1000);
        // 兜底时间
        queryDto.setStartTime(startTime);
        // 业务卡控时间
        queryDto.setEndTime(endTime);
        queryDto.setStatusList(Arrays.asList(PickingGoodStatusEnum.TO_PICKING.getCode(), PickingGoodStatusEnum.PICKING.getCode()));
        queryDto.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
        List<String> bizIdList;
        do {
            queryDto.setOffset((pageNumber - 1) * queryDto.getLimit());
            logInfo("finishTaskWhenTimeExceed req={}", JsonHelper.toJson(queryDto));
            bizIdList = jyBizTaskPickingGoodService.pageRecentCreatedManualBizId(queryDto);
            if (CollectionUtils.isEmpty(bizIdList)) {
                return;
            }
            logInfo("finishTaskWhenTimeExceed resp={}", JsonHelper.toJson(bizIdList));
            batchFinishPickingTaskByBizId(bizIdList, PickingCompleteNodeEnum.TIME_EXECUTE.getCode());
            pageNumber++;
        } while (CollectionUtils.isNotEmpty(bizIdList));
    }

    @Override
    public InvokeResult<PickingSendBatchCodeDetailRes> pageFetchSendBatchCodeDetailList(PickingSendBatchCodeDetailReq req) {
        InvokeResult<PickingSendBatchCodeDetailRes> res = new InvokeResult<>();
        if(!pageFetchSendBatchCodeDetailListBaseCheck(req, res)) {
            return res;
        }

        res = jyPickingSendDestinationService.pageFetchSendBatchCodeDetailList(req);
        return res;
    }
    //校验;true 放行  false 拦截
    private boolean pageFetchSendBatchCodeDetailListBaseCheck(PickingSendBatchCodeDetailReq req, InvokeResult<PickingSendBatchCodeDetailRes> res) {
        if(Objects.isNull(req) || Objects.isNull(res)) {
            res.success();
            return true;
        }
        if(Objects.isNull(req.getCurrentOperate()) || Objects.isNull(req.getCurrentOperate().getSiteCode())) {
            res.error("当前操作人站点不能为空");
            return false;
        }
        if(Objects.isNull(req.getNextSiteId())) {
            res.error("下一站点不能为空");
            return false;
        }
        if(!PickingGoodTaskTypeEnum.legalCheck(req.getTaskType())) {
            res.error("任务类型不合法");
            return false;
        }
        if(Objects.isNull(req.getBatchCodeStatus())) {
            req.setBatchCodeStatus(JyPickingSendBatchCodeStatusEnum.TO_SEAL.getCode());
        }
        return true;
    }

    @Override
    public InvokeResult delBatchCodes(DelBatchCodesReq req) {
        InvokeResult res = new InvokeResult();
        if(!this.delBatchCodesBaseCheck(req, res)) {
            return res;
        }
        if(!pickingGoodsCacheService.lockPickingSendBatchCodeDel(req.getCurrentOperate().getSiteCode(), req.getNextSiteId())) {
            res.error("该流向正在操作任务删除，请稍后重试");
            return res;
        }
        try{
            jyPickingSendDestinationService.delBatchCodes(req);
        }catch (Exception ex) {
            log.error("提发批次删除服务异常：req={},errMsg={}", JSON.toJSONString(req), ex.getMessage(), ex);
            res.error("提发批次删除服务异常");
            return res;
        }finally {
            pickingGoodsCacheService.unlockPickingSendBatchCodeDel(req.getCurrentOperate().getSiteCode(), req.getNextSiteId());
        }
        return res;
    }
    //校验;true 放行  false 拦截
    private boolean delBatchCodesBaseCheck(DelBatchCodesReq req, InvokeResult res) {
        if(Objects.isNull(req) || Objects.isNull(res)) {
            return true;
        }
        if(Objects.isNull(req.getCurrentOperate()) || Objects.isNull(req.getCurrentOperate().getSiteCode())) {
            res.error("当前操作人站点不能为空");
            return false;
        }
        if(CollectionUtils.isEmpty(req.getSendCodeList())) {
            res.error("批次号不能为空");
            return false;
        }
        if(Objects.isNull(req.getNextSiteId())) {
            res.error("下一站点不能为空");
            return false;
        }
        if(!PickingGoodTaskTypeEnum.legalCheck(req.getTaskType())) {
            res.error("任务类型不合法");
            return false;
        }
        res.success();
        return true;
    }
}
