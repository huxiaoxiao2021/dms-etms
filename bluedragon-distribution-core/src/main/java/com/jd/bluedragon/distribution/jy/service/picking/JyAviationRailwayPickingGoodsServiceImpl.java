package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.constants.BarCodeFetchPickingTaskRuleEnum;
import com.jd.bluedragon.distribution.jy.dto.common.BoxNextSiteDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodScanTaskBodyDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntityCondition;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;
import com.jd.bluedragon.distribution.jy.service.common.CommonService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


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
            //防重提货校验【siteId + barCode】  该防重规则一个场地仅能提货一次，如果需要支持多次，把防重逻辑放在查找待提任务之后，改成【siteId+bizId锁】或者【bizId+barCode锁】
            if(!this.repeatScanCheck(request.getBarCode(), request.getCurrentOperate().getSiteCode(),  res)) {
                logWarn("重复提货，request={},res={}", JsonHelper.toJson(request), JsonHelper.toJson(res));
                return res;
            }
            //确认待提货任务
            JyBizTaskPickingGoodEntity taskPickingGoodEntity = this.getTaskPickingGoodEntity(request, resData);
            //提货并发货执行
            if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
                if(!misSendingCheck(request, res)) {
                    return res;
                }
                if(!doPickingSendGoods(request, res, taskPickingGoodEntity)) {
                    return res;
                }
            }
            //仅提货逻辑
            else {
                if(!doPickingGoods(request, res, taskPickingGoodEntity)) {
                    return res;
                };
            }
            this.pickingGoodTaskUpdate(request, resData, taskPickingGoodEntity);
            //统计字段维护
            jyPickingTaskAggsService.updatePickingGoodStatistics(request, resData,taskPickingGoodEntity);
            //提货防重
            pickingGoodsCacheService.saveCachePickingGoodScan(request.getBarCode(), request.getCurrentOperate().getSiteCode());

            //返回结果处理
//        todo zcf
            return res;
        }catch (Exception e) {
            log.error("空铁提货服务异常，request={},errMsg={}", JsonHelper.toJson(request), e.getMessage(), e);
            res.error("空铁提货服务异常");
            return res;
        }finally {
            pickingGoodsCacheService.unlockPickingGoodScan(request.getBarCode(), request.getCurrentOperate().getSiteCode());
        }

    }

    private void pickingGoodTaskUpdate(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity entity) {
        if(PickingGoodStatusEnum.TO_PICKING.getCode().equals(entity.getStatus())) {
            JyBizTaskPickingGoodEntityCondition updateEntity = new JyBizTaskPickingGoodEntityCondition();
            updateEntity.setBizId(entity.getBizId());
            updateEntity.setStatus(PickingGoodStatusEnum.PICKING.getCode());
            updateEntity.setPickingStartTime(new Date(resData.getOperateTime()));
            updateEntity.setUpdateTime(new Date());
            updateEntity.setUpdateUserErp(request.getUser().getUserErp());
            updateEntity.setUpdateUserName(request.getUser().getUserName());
            jyBizTaskPickingGoodService.updateTaskByBizIdWithCondition(updateEntity);
        }
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
        JyBizTaskPickingGoodEntity taskPickingGoodEntity = jyBizTaskPickingGoodService.generateManualCreateTask(request);
        logInfo("扫描单据{}查提货任务为空-待提任务取默认生成任务{}", request.getBarCode(), JsonHelper.toJson(taskPickingGoodEntity));
        resData.setTaskSource(BarCodeFetchPickingTaskRuleEnum.MANUAL_CREATE_TASK.getCode());
        return taskPickingGoodEntity;

    }

    /**
     * 重复扫描校验
     * 按场地+包裹号做防重，一个机场分拣中心只能提货一次
     * @param barCode
     * @param res true： 校验通过  false: 重复扫描进行拦截，校验不通过
     * @return
     */
    private boolean repeatScanCheck(String barCode, Integer siteId, InvokeResult<PickingGoodsRes> res) {
        if(!pickingGoodsCacheService.getCachePickingGoodScanValue(barCode, siteId)) {
            JyPickingSendRecordEntity recordEntity = jyPickingSendRecordService.latestPickingRecord(siteId.longValue(), barCode);
            if(!Objects.isNull(recordEntity)) {
                logInfo("提货扫描redis防重查询为空，查DB最近一次提货记录,barCode={},siteId={},提货记录：{}", barCode, siteId, JsonHelper.toJson(recordEntity));
                pickingGoodsCacheService.saveCachePickingGoodScan(barCode, siteId);
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
        bodyDto.setBatchCode(jyPickingSendDestinationService.fetchSendingBatchCode(request.getCurrentOperate().getSiteCode(), request.getNextSiteId()));
        bodyDto.setReceiveSiteCode(request.getNextSiteId());
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

        Integer routerNextSiteId;
        String routerNextSiteName;
        String boxConfirmNextSiteKey = null;
        if(!BusinessUtil.isBoxcode(request.getBarCode())) {
            BaseStaffSiteOrgDto dto = commonService.getRouteNextSiteByWaybillCode(request.getCurrentOperate().getSiteCode(), WaybillUtil.getWaybillCode(request.getBarCode()));
            routerNextSiteName = dto.getSiteName();
            routerNextSiteId = dto.getSiteCode();
        }else {
            BoxNextSiteDto boxNextSiteDto = commonService.getRouteNextSiteByBox(request.getCurrentOperate().getSiteCode(), request.getBarCode());
            routerNextSiteId = boxNextSiteDto.getNextSiteId();
            routerNextSiteName = boxNextSiteDto.getNextSiteName();
            boxConfirmNextSiteKey = boxNextSiteDto.getBoxConfirmNextSiteKey();
        }

        //流向为空
        if(Objects.isNull(routerNextSiteId)) {
            resData.setNextSiteSupportSwitch(false);
            res.customMessage(PickingGoodsRes.CODE_30001, PickingGoodsRes.CODE_30001_MSG_2);
            return false;
        }
        //流向不一致
        if(!request.getNextSiteId().equals(routerNextSiteId)) {
            List<Integer> nextSiteIdList = new ArrayList<>();//todo zcf 调用已维护发货流向接口
            //
            resData.setNextSiteSupportSwitch(nextSiteIdList.contains(routerNextSiteId));
            resData.setRealNextSiteId(routerNextSiteId);
            resData.setRealNextSiteName(routerNextSiteName);
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
        BaseStaffSiteOrgDto dto = null;
        try{
            dto = baseMajorManager.getBaseSiteBySiteId(siteId);
            return dto.getSiteName();
        }catch (Exception e) {
            log.error("根据场地编码{}获取场地名称服务异常，res={},errMsg={}", siteId, JsonHelper.toJson(dto), e.getMessage(), e);
            throw new JyBizException();
        }
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
        boolean success = jyBizTaskPickingGoodService.updateStatusByBizId(req.getBizId(), PickingGoodStatusEnum.PICKING_COMPLETE.getCode());
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
        boolean success = jyBizTaskPickingGoodService.updateStatusByBizId(req.getBizId(), PickingGoodStatusEnum.PICKING_COMPLETE.getCode());
        if (!success) {
            log.warn("jyBizTaskPickingGoodService 根据bizId={} 完成提货任务状态失败！", req.getBizId());
        }
        return ret;
    }

    @Override
    public InvokeResult<SendFlowRes> listSendFlowInfo(SendFlowReq req) {
        return null;
    }

    @Override
    public InvokeResult<Void> addSendFlow(SendFlowAddReq req) {
        return null;
    }

    @Override
    public InvokeResult<Void> deleteSendFlow(SendFlowDeleteReq req) {
        return null;
    }

    @Override
    public InvokeResult<Void> finishSendTask(FinishSendTaskReq req) {
        return null;
    }

    @Override
    public InvokeResult<AirportTaskRes> listAirportTask(AirportTaskReq req) {
        return null;
    }

    @Override
    public InvokeResult<List<AirportTaskAggDto>> listAirportTaskAgg(AirportTaskAggReq req) {
        return null;
    }
}
