package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jsf.boxlimit.BoxLimitConfigManager;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageFlowEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageQuery;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskCollectPackageStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.MixBoxTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.middleend.sorting.service.ISortingService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf;
import com.jdl.basic.api.enums.FlowDirectionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;
import static com.jd.bluedragon.distribution.task.domain.Task.TASK_TYPE_SORTING;

@Service
@Slf4j
public class JyCollectPackageServiceImpl implements JyCollectPackageService{

    @Autowired
    private SortingService sortingService;
    @Autowired
    private BoxService boxService;
    @Autowired
    private WaybillCacheService waybillCacheService;
    @Autowired
    private JyBizTaskCollectPackageService jyBizTaskCollectPackageService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ISortingService dmsSortingService;
    @Autowired
    BoxLimitConfigManager boxLimitConfigManager;
    @Autowired
    private CycleBoxService cycleBoxService;
    @Autowired
    JimDbLock jimDbLock;

    @Autowired
    private JyBizTaskCollectPackageFlowService jyBizTaskCollectPackageFlowService;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Override
    public InvokeResult<CollectPackageResp> collectPackage(CollectPackageReq request) {
        //基础校验
        collectPackageBaseCheck(request);
        //集包业务校验
        collectPackageBizCheck(request);
        //执行集包
        CollectPackageResp response =new CollectPackageResp();
        execCollectPackage(request,response);

        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,response);
    }

    private void execCollectPackage(CollectPackageReq request, CollectPackageResp response) {
        String boxLockKey = String.format(Constants.JY_COLLECT_BOX_LOCK_PREFIX, request.getBoxCode());
        if (!jimDbLock.lock(boxLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
            throw new JyBizException("当前系统繁忙,请稍后再试！");
        }
        JyBizTaskCollectPackageEntity collectPackageTask =jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (ObjectHelper.isEmpty(collectPackageTask)){
            throw new JyBizException("集包任务不存在或者已经过期，请刷新界面！");
        }
        if (JyBizTaskCollectPackageStatusEnum.CANCEL.equals(collectPackageTask.getTaskStatus())){
            throw new JyBizException("集包任务作废-操作批量取消！");
        }
        try {
            boolean syncExec = false;
            if (syncExec){
                Task task =assembleSortingTask(request);
                dmsSortingService.doSorting(task);
            }else {
                TaskRequest taskRequest =assembleTaskRequest(request);
                taskService.add(taskRequest);
            }
            //保存集包扫描记录
            saveJyCollectPackageScanRecord(request);
            response.setEndSiteId(request.getEndSiteId());
        }finally {
            jimDbLock.releaseLock(boxLockKey, request.getRequestId());
        }
    }

    private Task assembleSortingTask(CollectPackageReq request) {
        Task task =new Task();
        task.setTableName(Task.getTableName(TASK_TYPE_SORTING));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setType(TASK_TYPE_SORTING);
        task.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        task.setKeyword1(String.valueOf(request.getCurrentOperate().getSiteCode()));
        task.setKeyword2(request.getBoxCode());
        task.setBoxCode(request.getBoxCode());

        //TODO
        //taskService.initOthers(jsonVal, task);
        //task.setBody(jsonVal);


        task.setReceiveSiteCode(request.getEndSiteId().intValue());
        String ownSign = BusinessHelper.getOwnSign();
        task.setOwnSign(ownSign);
        taskService.initFingerPrint(task);
        return task;
    }

    private void saveJyCollectPackageScanRecord(CollectPackageReq request) {
    }

    private TaskRequest assembleTaskRequest(CollectPackageReq request) {
        TaskRequest taskRequest =new TaskRequest();
        taskRequest.setBoxCode(request.getBoxCode());
        taskRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskRequest.setReceiveSiteCode(request.getEndSiteId().intValue());
        taskRequest.setType(TASK_TYPE_SORTING);
        taskRequest.setKeyword1(String.valueOf(request.getCurrentOperate().getSiteCode()));
        taskRequest.setKeyword2(request.getBoxCode());
        taskRequest.setBody("");
        return taskRequest;
    }

    private void collectPackageBizCheck(CollectPackageReq request) {
        //校验箱号：是否存在 +是否已打印+状态合法性+是否已经发货
        boxCheck(request);
        //流向校验
        flowCheck(request);
        //sorting拦截器链
        execInterceptorChain(request);
    }

    private void flowCheck(CollectPackageReq request) {
        JyBizTaskCollectPackageEntity collectPackageTask =jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (ObjectHelper.isEmpty(collectPackageTask)){
            throw new JyBizException("集包任务不存在或者已经过期，请刷新界面！");
        }
        if (JyBizTaskCollectPackageStatusEnum.CANCEL.equals(collectPackageTask.getTaskStatus())){
            throw new JyBizException("集包任务作废-操作批量取消！");
        }
        if (request.getForceCollectPackage()){
            request.setEndSiteId(collectPackageTask.getEndSiteId());
            return;
        }

        //查询包裹路由信息
        String router =waybillCacheService.getRouterByWaybillCode(WaybillUtil.getWaybillCode(request.getBarCode()));
        if (!ObjectHelper.isNotNull(router)){
            log.info("未获取到运单的路由信息,startSiteId:{}",request.getCurrentOperate().getSiteCode());
            throw new JyBizException("未获取到运单的路由信息！");
        }
        if (MixBoxTypeEnum.MIX_DISABLE.getCode().equals(collectPackageTask.getMixBoxType())){
            //校验路由是否存在于允许集包的流向集合中
            List<Integer> flowList =new ArrayList<>();
            flowList.add(collectPackageTask.getEndSiteId().intValue());
            checkRouterIfExitInCollectFlowList(router,flowList,request,collectPackageTask);
        } else {
            //查询可集的流向集合信息
            List<Integer> flowList =queryMixBoxFlowList(request);
            if (CollectionUtils.isEmpty(flowList)){
                BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseService.getSiteBySiteID(collectPackageTask.getEndSiteId().intValue());
                if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())){
                    throw new JyBizException(FORCE_COLLECT_PACKAGE_WARNING,"路由节点不在允许集包的流向内，是否强制集往【"+baseStaffSiteOrgDto.getSiteName()+"】？");
                }
                throw new JyBizException("路由节点不在允许集包的流向内，禁止集包！");
            }
            //校验路由信息是否 在可集包的流向集合内
            checkRouterIfExitInCollectFlowList(router,flowList,request,collectPackageTask);
        }
    }

    /**
     * 查询混装的流向集合（查询混装的集包的流向集合）
     * @return
     */
    private List<Integer> queryMixBoxFlowList(CollectPackageReq req) {
        CollectBoxFlowDirectionConf con =assembleCollectBoxFlowDirectionConf(req);
        List<CollectBoxFlowDirectionConf> collectBoxFlowDirectionConfList =boxLimitConfigManager.listCollectBoxFlowDirection(con);//TODO 替换成查询任务的流向集合
        if (CollectionUtils.isEmpty(collectBoxFlowDirectionConfList)){
            throw new JyBizException("未查询到对应目的地的可混装的流向集合！");
        }
        List<Integer> endSiteIdList =new ArrayList<>();
        for (CollectBoxFlowDirectionConf conf:collectBoxFlowDirectionConfList){
            endSiteIdList.add(conf.getEndSiteId());
        }
        return endSiteIdList;
    }

    private CollectBoxFlowDirectionConf assembleCollectBoxFlowDirectionConf(CollectPackageReq req) {
        CollectBoxFlowDirectionConf conf =new CollectBoxFlowDirectionConf();
        conf.setStartSiteId(req.getCurrentOperate().getSiteCode());
        conf.setBoxReceiveId(req.getBoxReceiveId().intValue());
        conf.setFlowType(FlowDirectionTypeEnum.OUT_SITE.getCode());
        return conf;
    }

    /**
     * 校验路由节点是否在可混装的流向集合内
     * @param router
     * @param flowList
     * @param request
     * @param collectPackageTask
     */
    private void checkRouterIfExitInCollectFlowList(String router, List<Integer> flowList,CollectPackageReq request,JyBizTaskCollectPackageEntity collectPackageTask) {
        List<Integer> nextNodeList =getNextNodeList(request.getCurrentOperate().getSiteCode(),router);
        if (CollectionUtils.isEmpty(nextNodeList)){
            log.info("集包扫描获取路由信息：currentSiteCode:{},router:{}",request.getCurrentOperate().getSiteCode(),router);
            throw new JyBizException("未获取到有效的路由节点信息！");
        }
        boolean collectEnable =false;
        for (Integer nextNode :nextNodeList){
            if (flowList.contains(nextNode)){
                request.setEndSiteId(Long.valueOf(nextNode));
                collectEnable =true;
                break;
            }
        }
        if (!collectEnable){
            BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseService.getSiteBySiteID(collectPackageTask.getEndSiteId().intValue());
            if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())){
                throw new JyBizException(FORCE_COLLECT_PACKAGE_WARNING,"路由节点不在允许集包的流向内，是否强制集往【"+baseStaffSiteOrgDto.getSiteName()+"】？");
            }
            throw new JyBizException("路由节点不在允许集包的流向内，禁止集包！");
        }
    }

  private List<Integer> getNextNodeList(Integer startSiteId, String router) {
    int index = router.indexOf(String.valueOf(startSiteId));
    if (index != -1) {
        router = router.substring(index);
        return Arrays.stream(router.split("\\|"))
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    throw new JyBizException("运单路由信息不包含当前场地！");
}


    private void execInterceptorChain(CollectPackageReq request) {
        if (request.getForceCollectPackage()){
            return;
        }
        if (request.getSkipInterceptChain()){
            return;
        }

        PdaOperateRequest pdaOperateRequest =assemblePdaOperateRequest(request);
        SortingJsfResponse response =sortingService.check(pdaOperateRequest);

        if(!Objects.equals(response.getCode(), SortingResponse.CODE_OK)){
            if(response.getCode() >= 30000 && response.getCode() <= 40000){
               throw new JyBizException(CONFIRM_COLLECT_PACKAGE_WARNING,response.getMessage());
            }
            //todo 存储拦截扫描记录
            throw new JyBizException(response.getMessage());
        }

    }

    private PdaOperateRequest assemblePdaOperateRequest(CollectPackageReq request) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setBoxCode(request.getBoxCode());
        pdaOperateRequest.setBusinessType(DmsConstants.BUSSINESS_TYPE_POSITIVE);
        //TODO 是否需要校验运单集齐
        //pdaOperateRequest.setIsGather(0);
        //pdaOperateRequest.setOperateType(request.getOperateType());
        pdaOperateRequest.setPackageCode(request.getBarCode());
        pdaOperateRequest.setReceiveSiteCode(request.getEndSiteId().intValue());
        pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        pdaOperateRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
        pdaOperateRequest.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(),DateUtil.FORMAT_DATE_TIME));
        pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
        pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
        pdaOperateRequest.setSkipFilter(true);
        return pdaOperateRequest;
    }

    private void boxCheck(CollectPackageReq request) {
        Box box = boxService.findBoxByCode(request.getBoxCode());
        if (box == null) {
            throw new JyBizException("该箱号不存在或者已过期！");
        }
        if (Box.STATUS_DEFALUT.intValue() == box.getStatus().intValue()) {
            throw new JyBizException("该箱号未打印！");
        }
        if (box.getCode().length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT){
            throw new JyBizException("箱号超长！");
        }
        //判断箱子是否已发货
        if (boxService.checkBoxIsSent(request.getBoxCode(), box.getCreateSiteCode())) {
            throw new JyBizException("该箱号已经发货，禁止继续集包！");
        }
        request.setBoxReceiveId(Long.valueOf(box.getReceiveSiteCode()));
    }

    private void collectPackageBaseCheck(CollectPackageReq request) {
        if (!ObjectHelper.isNotNull(request.getBizId())){
            throw new JyBizException("参数错误：缺失任务bizId！");
        }
        if (!ObjectHelper.isNotNull(request.getBoxCode())){
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isBoxcode(request.getBoxCode())){
            throw new JyBizException("参数错误：不支持该类型箱号！");
        }
        if (!ObjectHelper.isNotNull(request.getBarCode())){
            throw new JyBizException("参数错误：缺失包裹号！");
        }
        if (!WaybillUtil.isPackageCode(request.getBarCode())){
            throw new JyBizException("参数错误：包裹号类型错误，请扫描正确的包裹号码！");
        }
    }

    @Override
    public InvokeResult<CollectPackageTaskResp> listCollectPackageTask(CollectPackageTaskReq request) {
        InvokeResult<CollectPackageTaskResp> result = new InvokeResult<>();
        CollectPackageTaskResp resp = new CollectPackageTaskResp();
        result.setData(resp);
        // 参数校验
        if (!checkCollectPackageTaskReq(request, result)) {
            return result;
        }
        // 根据状态查询任务总数
        JyBizTaskCollectPackageQuery query = new JyBizTaskCollectPackageQuery();
        query.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        Date time = DateHelper.addHoursByDay(new Date(), -Double.valueOf(dmsConfigManager.getPropertyConfig().getJyCollectPackageTaskQueryTimeLimit()));
        query.setCreateTime(time);
        resp.setCollectPackStatusCountList(jyBizTaskCollectPackageService.queryTaskStatusCount(query));
        resp.setCollectPackTaskDtoList(getCollectPackageFlowDtoList(getPageQuery(request,time)));
        return result;
    }
    private List<CollectPackageTaskDto> getCollectPackageFlowDtoList(JyBizTaskCollectPackageQuery query) {
        // 查询集包任务列表
        List<JyBizTaskCollectPackageEntity> taskList = jyBizTaskCollectPackageService.pageQueryTask(query);
        if (CollectionUtils.isEmpty(taskList)) {
            return new ArrayList<>();
        }

        // 获取箱号列表 批量获取绑定集包袋信息
        List<String> boxCodeList = taskList.stream().map(JyBizTaskCollectPackageEntity::getBoxCode).collect(Collectors.toList());
        List<BoxMaterialRelation> boxMaterialRelationList = cycleBoxService.getBoxMaterialRelationList(boxCodeList);
        HashMap<String, String> boxMaterialRelationMap = getBoxMaterialRelationMap(boxMaterialRelationList);

        // 批量获取流向信息
        List<String> bizIds = taskList.stream().map(JyBizTaskCollectPackageEntity::getBizId).collect(Collectors.toList());
        HashMap<String, List<CollectPackageFlowDto>> flowMap = getFlowMapByTask(bizIds);

        // 组装任务
        List<CollectPackageTaskDto> collectPackTaskDtoList = taskList.stream().map(task -> {
            CollectPackageTaskDto taskDto = new CollectPackageTaskDto();
            BeanUtils.copyProperties(task, taskDto);
            // 查询集包袋号
            taskDto.setMaterialCode(boxMaterialRelationMap.get(task.getBoxCode()));
            // todo 统计数据
            taskDto.setInterceptCount(1);
            taskDto.setScanCount(1);
            // 流向信息
            taskDto.setCollectPackageFlowDtoList(flowMap.get(task.getBizId()));
            return taskDto;
        }).collect(Collectors.toList());
        return collectPackTaskDtoList;
    }

    private HashMap<String, String> getBoxMaterialRelationMap(List<BoxMaterialRelation> boxMaterialRelationList) {
        if (CollectionUtils.isEmpty(boxMaterialRelationList)) {
            return new HashMap<>();
        }
        HashMap<String, String> boxMaterialRelationMap = new HashMap<>();
        boxMaterialRelationList.stream().map(item -> boxMaterialRelationMap.put(item.getBoxCode(),item.getMaterialCode()));
        return boxMaterialRelationMap;
    }

    /**
     * 根据任务获取流向信息
     * @param bizIds
     * @return
     */
    private HashMap<String, List<CollectPackageFlowDto>> getFlowMapByTask(List<String> bizIds) {
        HashMap<String, List<CollectPackageFlowDto>> flowMap = new HashMap<>();
        // 如果支持混装，查询流向集合
        List<JyBizTaskCollectPackageFlowEntity> flowList = jyBizTaskCollectPackageFlowService.queryListByBizIds(bizIds);
        if (!CollectionUtils.isEmpty(flowList)) {
            for (JyBizTaskCollectPackageFlowEntity entity : flowList) {
                CollectPackageFlowDto collectPackageFlowDto = new CollectPackageFlowDto();
                collectPackageFlowDto.setEndSiteId(entity.getEndSiteId());
                collectPackageFlowDto.setEndSiteName(entity.getEndSiteName());
                List<CollectPackageFlowDto> flowDtoList = flowMap.get(entity.getCollectPackageBizId());
                if (CollectionUtils.isEmpty(flowDtoList)) {
                    flowMap.put(entity.getCollectPackageBizId(), new ArrayList<>());
                    flowDtoList = flowMap.get(entity.getCollectPackageBizId());
                }
                flowDtoList.add(collectPackageFlowDto);
            }
        }
        return flowMap;
    }

    private boolean checkCollectPackageTaskReq(CollectPackageTaskReq request, InvokeResult<CollectPackageTaskResp> resp) {
        if (request.getCurrentOperate() == null || request.getCurrentOperate().getSiteCode() <= 0) {
            resp.setCode(RESULT_THIRD_ERROR_CODE);
            resp.setMessage("未获取到当前站点信息！");
            return false;
        }

        if (request.getTaskStatus() != null && request.getTaskStatus() < 0) {
            resp.setCode(RESULT_THIRD_ERROR_CODE);
            resp.setMessage("未获取到任务状态！");
            return false;
        }
        
        if (request.getPageNo() == null || request.getPageNo() <= 0 || request.getPageSize() == null || request.getPageSize() <= 0) {
            resp.setCode(RESULT_THIRD_ERROR_CODE);
            resp.setMessage("未获取到分页参数！");
            return false;
        }
        return true;
    }
    
    private JyBizTaskCollectPackageQuery getPageQuery(CollectPackageTaskReq request, Date time) {
        JyBizTaskCollectPackageQuery query = new JyBizTaskCollectPackageQuery();
        query.setTaskStatus(request.getTaskStatus());
        query.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        query.setOffset((request.getPageNo() - 1) * request.getPageSize());
        query.setLimit(request.getPageSize());
        query.setCreateTime(time);
        return query;
    }

    @Override
    public InvokeResult<TaskDetailResp> queryTaskDetail(TaskDetailReq request) {
        InvokeResult<TaskDetailResp> result = new InvokeResult<>();
        TaskDetailResp resp = new TaskDetailResp();
        result.setData(resp);
        JyBizTaskCollectPackageEntity task = jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (task == null) {
            result.setCode(RESULT_NULL_CODE);
            result.setMessage(request.getBoxCode() + ":未获取到集包任务！");
            return result;
        }
        CollectPackageTaskDto taskDto = new CollectPackageTaskDto();
        BeanUtils.copyProperties(task, taskDto);
        // 查询集包袋号
        taskDto.setMaterialCode(cycleBoxService.getBoxMaterialRelation(task.getBoxCode()));

        // todo 查询统计数据
        taskDto.setInterceptCount(1);
        taskDto.setInterceptCount(1);
        // 流向信息
        HashMap<String, List<CollectPackageFlowDto>> flowInfo = getFlowMapByTask(Collections.singletonList(taskDto.getBizId()));
        taskDto.setCollectPackageFlowDtoList(flowInfo.get(task.getBizId()));
        resp.setCollectPackageTaskDto(taskDto);
        return result;
    }

    @Override
    public InvokeResult<SealingBoxResp> sealingBox(SealingBoxReq request) {
        InvokeResult<SealingBoxResp> result = new InvokeResult<>();
        if (!checkSealingReq(request, result)) {
            return result;
        }

        List<String> bizIds = request.getSealingBoxDtoList().stream().map(SealingBoxDto::getBizId).collect(Collectors.toList());
        JyBizTaskCollectPackageQuery query = new JyBizTaskCollectPackageQuery();
        query.setBizIds(bizIds);
        query.setTaskStatus(JyBizTaskCollectPackageStatusEnum.SEALED.getCode());
        log.info("开始更新集包任务状态：{}", JsonHelper.toJson(query));
        if (!jyBizTaskCollectPackageService.updateStatusByBizIds(query)) {
            log.info("批量封箱失败：{}", JsonHelper.toJson(query));
            result.setCode(RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("批量封箱失败!");
            return result;
        }
        return result;
    }

    private boolean checkSealingReq(SealingBoxReq request, InvokeResult<SealingBoxResp> result) {
        if (request == null || CollectionUtils.isEmpty(request.getSealingBoxDtoList())) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("未获取到任务信息！");
            return false;
        }
        return true;
    }

    @Override
    public InvokeResult bindCollectBag(BindCollectBagReq request) {
        checkBindCollectBagReq(request);
        BoxMaterialRelationRequest req = assembleBoxMaterialRelationRequest(request);
        InvokeResult bindMaterialResp = cycleBoxService.boxMaterialRelationAlter(req);
        if (!bindMaterialResp.codeSuccess()) {
            return new InvokeResult(SERVER_ERROR_CODE, bindMaterialResp.getMessage());
        }
        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE);
    }

    private BoxMaterialRelationRequest assembleBoxMaterialRelationRequest(BindCollectBagReq request) {
        BoxMaterialRelationRequest req = new BoxMaterialRelationRequest();
        req.setUserCode(request.getUser().getUserCode());
        req.setUserName(request.getUser().getUserName());
        req.setOperatorERP(request.getUser().getUserErp());
        req.setSiteCode(request.getCurrentOperate().getSiteCode());
        req.setSiteName(request.getCurrentOperate().getSiteName());
        req.setBoxCode(request.getBoxCode());
        req.setMaterialCode(request.getMaterialCode());
        req.setBindFlag(Constants.CONSTANT_NUMBER_ONE);
        return req;
    }

    private void checkBindCollectBagReq(BindCollectBagReq request) {
        if (!ObjectHelper.isNotNull(request.getBoxCode())){
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isBoxcode(request.getBoxCode())){
            throw new JyBizException("参数错误：非法的箱号！");
        }
        if (!ObjectHelper.isNotNull(request.getMaterialCode())){
            throw new JyBizException("参数错误：缺失集包袋号！");
        }
        if (!BusinessUtil.isCollectionBag(request.getMaterialCode())){
            throw new JyBizException("参数错误：非法的集包袋号！");
        }
    }

    @Override
    public InvokeResult<CancelCollectPackageResp> cancelCollectPackage(CancelCollectPackageReq request) {
        checkCancelCollectPackageReq(request);
        if(request.getCancelAllFlag()){
            String boxLockKey = String.format(Constants.JY_COLLECT_BOX_LOCK_PREFIX, request.getBoxCode());
            if (!jimDbLock.lock(boxLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
                throw new JyBizException("当前系统繁忙,请稍后再试！");
            }
            try {
                //发消息-按照包裹发消息
                //然后把箱号关闭，不让用了
            }finally {
                jimDbLock.releaseLock(boxLockKey,request.getRequestId());
            }

        }else {

        }


        /*SortingRequest params = new SortingRequest();
        params.setPackageCode(request.getPackageCode());
        params.setUserCode(request.getUser().getUserCode());
        params.setUserName(request.getUser().getUserName());
        params.setBusinessType(request.getBusinessType());
        params.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(),DateUtil.FORMAT_DATE_TIME));
        params.setSiteCode(request.getCurrentOperate().getSiteCode());
        params.setSiteName(request.getCurrentOperate().getSiteName());
        if(BusinessUtil.isBoxcode(request.getPackageCode())){
            params.setBoxCode(request.getPackageCode());
        }*/
        return null;
    }

    @Override
    public InvokeResult<CollectPackageTaskResp> searchPackageTask(SearchPackageTaskReq request) {
        InvokeResult<CollectPackageTaskResp> result = new InvokeResult<>();
        if (!checkSearchPackageTaskReq(request,result)) {
            return result;
        }
        CollectPackageTaskResp resp = new CollectPackageTaskResp();
        result.setData(resp);
        JyBizTaskCollectPackageQuery searchPageQuery = getSearchPageQuery(request);
        log.info("集包任务检索请求：{}", JsonHelper.toJson(searchPageQuery));
        resp.setCollectPackStatusCountList(jyBizTaskCollectPackageService.queryTaskStatusCount(searchPageQuery));
        resp.setCollectPackTaskDtoList(getCollectPackageFlowDtoList(searchPageQuery));
        return result;
    }

    private boolean checkSearchPackageTaskReq(SearchPackageTaskReq request, InvokeResult<CollectPackageTaskResp> result) {
        if (StringUtils.isEmpty(request.getBarCode())
                || (!WaybillUtil.isPackageCode(request.getBarCode())
                && !BusinessHelper.isBoxcode(request.getBarCode()))) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("只支持扫描包裹号或者箱号！");
            return false;
        }
        return true;
    }

    private JyBizTaskCollectPackageQuery getSearchPageQuery(SearchPackageTaskReq request) {
        JyBizTaskCollectPackageQuery query = new JyBizTaskCollectPackageQuery();
        query.setTaskStatus(request.getTaskStatus());
        query.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        query.setOffset((request.getPageNo() - 1) * request.getPageSize());
        query.setLimit(request.getPageSize());
        
        // 如果是箱号
        if (BusinessHelper.isBoxcode(request.getBarCode())) {
            query.setBoxCode(request.getBarCode());
        }else if (WaybillUtil.isPackageCode(request.getBarCode())){
            // 如果是包裹号，按流向查询 todo 获取目的地站点
            query.setEndSiteId(0L);
        }
        Date time = DateHelper.addHoursByDay(new Date(), -Double.valueOf(dmsConfigManager.getPropertyConfig().getJyCollectPackageTaskQueryTimeLimit()));
        query.setCreateTime(time);
        return query;
    }
    
    private void checkCancelCollectPackageReq(CancelCollectPackageReq request) {

    }
}
