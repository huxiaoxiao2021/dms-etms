package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.common.BoxNextSiteDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodScanCacheDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodScanTaskBodyDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
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
        //查找待提货任务
        JyBizTaskPickingGoodEntity taskPickingGoodEntity = null;
        InvokeResult<JyBizTaskPickingGoodEntity> pickingGoodEntityInvokeResult = this.fetchPickingTaskByBarCode((long)request.getCurrentOperate().getSiteCode(), request.getBarCode());
        if(!pickingGoodEntityInvokeResult.codeSuccess()) {
            res.error(pickingGoodEntityInvokeResult.getMessage());
            return res;
        }else {
            if(!Objects.isNull(pickingGoodEntityInvokeResult.getData())) {
                //存在待提任务场景
                taskPickingGoodEntity = pickingGoodEntityInvokeResult.getData();
            }else if(StringUtils.isNotBlank(request.getLastScanTaskBizId())) {
                //不存在待提任务取上次扫描任务
                taskPickingGoodEntity = jyBizTaskPickingGoodService.findByBizIdWithYn(request.getLastScanTaskBizId(), false);
            }
            if(Objects.isNull(taskPickingGoodEntity)) {
                //待提货任务为空时生成自建待提任务
                taskPickingGoodEntity = jyBizTaskPickingGoodService.generateManualCreateTask(request);
            }
        }
        //防重复提货校验【BizId + barCode】
        if(!this.repeatScanCheck(request.getBarCode(), taskPickingGoodEntity, res)) {
            if(log.isInfoEnabled()) {
                log.warn("重复提货，request={},res={}", JsonHelper.toJson(request), JsonHelper.toJson(res));
            }
            return res;
        }
        //提货并发货
        if(Boolean.TRUE.equals(request.getSendGoodFlag())) {
            //发货流向校验
            if(!misSendingCheck(request, res)) {
                return res;
            }
            //则执行发货逻辑
            if(!doSendGoods(request, res)) {
                return res;
            }
        }
        //提货逻辑
        else {
            if(!doPickingGoods(request, res)) {
                return res;
            };
        }

        this.saveCachePickingGoodScan(request, resData, taskPickingGoodEntity);


        //返回结果处理
//        todo zcf
        return res;
    }

    //扫描缓存记录
    private void saveCachePickingGoodScan(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity taskPickingGoodEntity) {
        PickingGoodScanCacheDto cacheDto = new PickingGoodScanCacheDto();
        cacheDto.setBizId(taskPickingGoodEntity.getBizId());
        cacheDto.setBarCode(request.getBarCode());
        cacheDto.setCreateSiteId(request.getCurrentOperate().getSiteCode());
        cacheDto.setOperatorErp(request.getUser().getUserErp());
        cacheDto.setSendFlag(request.getSendGoodFlag());
        cacheDto.setOperateTime(resData.getOperateTime());
        pickingGoodsCacheService.saveCachePickingGoodScan(cacheDto);
    }
    //重复扫描校验
    private boolean repeatScanCheck(String barCode, JyBizTaskPickingGoodEntity taskPickingGoodEntity, InvokeResult<PickingGoodsRes> res) {
        PickingGoodScanCacheDto cacheDto = pickingGoodsCacheService.getCachePickingGoodScanValue(barCode, taskPickingGoodEntity.getBizId());
        //todo zcf 缓存失效后查库做缓存续期
        if(!Objects.isNull(cacheDto)) {
            boolean sendFlag = Boolean.TRUE.equals(cacheDto.getSendFlag());
            boolean manualCreateFlag = Constants.NUMBER_ONE.equals(taskPickingGoodEntity.getManualCreatedFlag());
            if(!sendFlag) {
                if(manualCreateFlag) {
                    res.parameterError(String.format("该包裹[箱号]已经在自建任务中提货，请勿重复提货"));
                }else {
                    res.parameterError(String.format("该包裹[箱号]已经在任务【%s】中提货，请勿重复提货", taskPickingGoodEntity.getServiceNumber()));
                }
            }else {
                if(manualCreateFlag) {
                    res.parameterError(String.format("该包裹[箱号]已经在自建任务中提货并发货，请勿重复提货"));
                }else {
                    res.parameterError(String.format("该包裹[箱号]已经在任务【%s】中提货并发货，请勿重复提货", taskPickingGoodEntity.getServiceNumber()));
                }
            }
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
    private boolean doSendGoods(PickingGoodsReq request, InvokeResult<PickingGoodsRes> res) {
        TaskRequest taskRequest = new TaskRequest();
        taskService.add(taskRequest);
//        todo zcf
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
            String msg = String.format(PickingGoodsRes.CODE_30001_MSG_1, routerNextSiteId, request.getNextSiteId());
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
    private boolean doPickingGoods(PickingGoodsReq request, InvokeResult<PickingGoodsRes> res) {
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


    public InvokeResult<JyBizTaskPickingGoodEntity> fetchPickingTaskByBarCode(Long siteCode, String barCode) {
        InvokeResult<JyBizTaskPickingGoodEntity> res = new InvokeResult<>();
        if(Objects.isNull(siteCode) || StringUtils.isBlank(barCode)) {
            res.parameterError("查询待提任务参数不合法");
            return res;
        }
        InvokeResult<String> taskBizIdRes = jyPickingSendRecordService.fetchPickingBizIdByBarCode(siteCode, barCode);
        if(!taskBizIdRes.codeSuccess()) {
            res.error(taskBizIdRes.getMessage());
            return res;
        }
        if(StringUtils.isBlank(taskBizIdRes.getData())) {
            res.setMessage("未查到待提货任务BizId");
            return res;
        }

        JyBizTaskPickingGoodEntity pickingGoodEntity = jyBizTaskPickingGoodService.findByBizIdWithYn(taskBizIdRes.getData(), false);
        if(!PickingGoodStatusEnum.PICKING_COMPLETE.getCode().equals(pickingGoodEntity) && !JyBizTaskPickingGoodEntity.INTERCEPT_FLAG.equals(pickingGoodEntity.getIntercept())) {
            res.setData(pickingGoodEntity);
        }else {
            res.setMessage("未查到待提货任务");
        }
        return res;
    }

    @Override
    public InvokeResult<Void> finishPickGoods(FinishPickGoodsReq req) {
        InvokeResult<Void> ret = new InvokeResult<>();
        if (StringUtils.isEmpty(req.getBizId())) {
            ret.parameterError("参数错误：提货任务BizId不能为空！");
        }
        return null;
    }

    @Override
    public InvokeResult<Void> submitException(ExceptionSubmitReq req) {
        return null;
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
