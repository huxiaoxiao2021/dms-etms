package com.jd.bluedragon.distribution.jy.service.collectpackage;

import IceInternal.Ex;
import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.common.dto.comboard.request.ExcepScanDto;
import com.jd.bluedragon.common.dto.sorting.request.PackSortTaskBody;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.boxlimit.BoxLimitConfigManager;
import com.jd.bluedragon.core.jsf.collectpackage.CollectPackageManger;
import com.jd.bluedragon.core.jsf.collectpackage.dto.ListTaskStatisticDto;
import com.jd.bluedragon.core.jsf.collectpackage.dto.ListTaskStatisticQueryDto;
import com.jd.bluedragon.core.jsf.collectpackage.dto.StatisticsUnderTaskDto;
import com.jd.bluedragon.core.jsf.collectpackage.dto.StatisticsUnderTaskQueryDto;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.constants.BoxTypeEnum;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageFlowEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageQuery;
import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.BatchCancelCollectPackageMqDto;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CancelCollectPackageDto;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CollectScanDto;
import com.jd.bluedragon.distribution.jy.enums.BoxTransportTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.CollectPackageExcepScanEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskCollectPackageStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.MixBoxTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.middleend.sorting.service.ISortingService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingBizSourceEnum;
import com.jd.bluedragon.distribution.sorting.domain.SortingQuery;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf;
import com.jdl.basic.api.enums.FlowDirectionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.box.constants.BoxTypeEnum.getFromCode;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;
import static com.jd.bluedragon.distribution.task.domain.Task.TASK_TYPE_SORTING;

@Service
@Slf4j
public class JyCollectPackageServiceImpl implements JyCollectPackageService {

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

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;

    @Autowired
    JyCollectPackageScanRecordService jyCollectPackageScanRecordService;
    @Autowired
    @Qualifier("batchCancelCollectPackageProduce")
    private DefaultJMQProducer batchCancelCollectPackageProduce;

    private static final Integer SEALING_BOX_LIMIT = 100;

    @Autowired
    private CollectPackageManger collectPackageManger;

    @Autowired
    private RouterService routerService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;


    /**
     * 集包
     *
     * @param request 集包请求对象
     * @return InvokeResult<CollectPackageResp> 响应结果对象，包含集包响应信息
     */
    @Override
    public InvokeResult<CollectPackageResp> collectPackage(CollectPackageReq request) {
        //基础校验
        collectPackageBaseCheck(request);
        //集包业务校验
        collectPackageBizCheck(request);
        //执行集包
        CollectPackageResp response = new CollectPackageResp();
        execCollectPackage(request, response);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, response);
    }

    /**
     * 执行集包操作
     * @param request 集包请求对象
     * @param response 集包响应对象
     */
    private void execCollectPackage(CollectPackageReq request, CollectPackageResp response) {
        String boxLockKey = String.format(Constants.JY_COLLECT_BOX_LOCK_PREFIX, request.getBoxCode());
        if (!jimDbLock.lock(boxLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
            throw new JyBizException("当前系统繁忙,请稍后再试！");
        }
        JyBizTaskCollectPackageEntity collectPackageTask = jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (ObjectHelper.isEmpty(collectPackageTask)) {
            throw new JyBizException("集包任务不存在，请刷新界面！");
        }
        if (ObjectHelper.isNotNull(collectPackageTask.getCreateTime())){
            Date time = DateHelper.addHoursByDay(new Date(), -Double.valueOf(dmsConfigManager.getPropertyConfig().getJyCollectPackageTaskQueryTimeLimit()));
            if (collectPackageTask.getCreateTime().compareTo(time) <0){
                throw new JyBizException("集包任务已过期，请打印新箱号集包！");
            }
        }
        if (JyBizTaskCollectPackageStatusEnum.CANCEL.getCode().equals(collectPackageTask.getTaskStatus())) {
            throw new JyBizException("集包任务已作废-操作了批量取消！");
        }
        try {
            boolean syncExec = false;
            if (syncExec) {
                Task task = assembleSortingTask(request);
                dmsSortingService.doSorting(task);
            } else {
                TaskRequest taskRequest = assembleTaskRequest(request);
                taskService.add(taskRequest);
            }
            //保存集包扫描记录
            saveJyCollectPackageScanRecord(request);
            response.setEndSiteId(request.getEndSiteId());
            checkIfNeedUpdateStatus(request, collectPackageTask);
        } finally {
            jimDbLock.releaseLock(boxLockKey, request.getRequestId());
        }
    }

    private void checkIfNeedUpdateStatus(CollectPackageReq request, JyBizTaskCollectPackageEntity collectPackageTask) {
        if (JyBizTaskCollectPackageStatusEnum.TO_COLLECT.getCode().equals(collectPackageTask.getTaskStatus())){
            JyBizTaskCollectPackageEntity entity =new JyBizTaskCollectPackageEntity();
            entity.setId(collectPackageTask.getId());
            entity.setTaskStatus(JyBizTaskCollectPackageStatusEnum.COLLECTING.getCode());
            entity.setUpdateTime(new Date());
            entity.setUpdateUserErp(request.getUser().getUserErp());
            entity.setUpdateUserName(request.getUser().getUserName());
            jyBizTaskCollectPackageService.updateById(entity);
        }
    }

    private Task assembleSortingTask(CollectPackageReq request) {
        Task task = new Task();
        task.setTableName(Task.getTableName(TASK_TYPE_SORTING));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setType(TASK_TYPE_SORTING);
        task.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        task.setKeyword1(String.valueOf(request.getCurrentOperate().getSiteCode()));
        task.setKeyword2(request.getBoxCode());
        task.setBoxCode(request.getBoxCode());

        String taskBody = assembleTaskBody(request);
        task.setBody(taskBody);
        taskService.initOthers(taskBody, task);
        task.setReceiveSiteCode(request.getBoxReceiveId().intValue());
        String ownSign = BusinessHelper.getOwnSign();
        task.setOwnSign(ownSign);
        taskService.initFingerPrint(task);
        return task;
    }

    private String assembleTaskBody(CollectPackageReq request) {
        PackSortTaskBody taskBody = new PackSortTaskBody();
        taskBody.setBoxCode(request.getBoxCode());
        //TODO
        //taskBody.setBusinessType(getBusinessTypeBySiteType(boxReceiveSiteType).getType());
        taskBody.setFeatureType(0);
        taskBody.setIsCancel(0);
        taskBody.setIsLoss(0);
        taskBody.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskBody.setSiteName(request.getCurrentOperate().getSiteName());
        taskBody.setPackageCode(request.getBarCode());
        taskBody.setReceiveSiteCode(request.getBoxReceiveId().intValue());
        taskBody.setReceiveSiteName(request.getBoxReceiveName());
        taskBody.setUserCode(request.getUser().getUserCode());
        taskBody.setUserName(request.getUser().getUserName());
        taskBody.setBizSource(SortingBizSourceEnum.ANDROID_SORTING.getCode());
        String body = JsonHelper.toJson(taskBody);
        return Constants.PUNCTUATION_OPEN_BRACKET + body + Constants.PUNCTUATION_CLOSE_BRACKET;
    }

    /**
     * 保存集包扫描记录
     *
     * @param request 集包请求对象
     */
    private void saveJyCollectPackageScanRecord(CollectPackageReq request) {
        JyCollectPackageEntity jyCollectPackageEntity = converJyCollectPackageEntity(request, false, request.getForceCollectPackage()?true:false);
        jyCollectPackageScanRecordService.saveJyCollectPackageRecord(jyCollectPackageEntity);
    }

    /**
     * 保存（拦截）类型集包扫描记录
     *
     * @param request 入参参数描述：拦截器请求对象
     */
    private void saveIntercepterJyCollectPackageScanRecord(CollectPackageReq request) {
        JyCollectPackageEntity jyCollectPackageEntity = converJyCollectPackageEntity(request, true, false);
        jyCollectPackageScanRecordService.saveJyCollectPackageRecord(jyCollectPackageEntity);
    }

    /**
     * 保存（强发）集包扫描记录
     *
     * @param request 采集包请求
     */
    private void saveForceJyCollectPackageScanRecord(CollectPackageReq request) {
        JyCollectPackageEntity jyCollectPackageEntity = converJyCollectPackageEntity(request, false, true);
        jyCollectPackageScanRecordService.saveJyCollectPackageRecord(jyCollectPackageEntity);
    }

    private JyCollectPackageEntity converJyCollectPackageEntity(CollectPackageReq request, boolean intecepterFlag, boolean forceFlag) {
        JyCollectPackageEntity jyCollectPackageEntity = new JyCollectPackageEntity();
        jyCollectPackageEntity.setBizId(request.getBizId());
        jyCollectPackageEntity.setPackageCode(request.getBarCode());
        jyCollectPackageEntity.setBoxCode(request.getBoxCode());
        jyCollectPackageEntity.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        jyCollectPackageEntity.setStartSiteName(request.getCurrentOperate().getSiteName());
        jyCollectPackageEntity.setEndSiteId(request.getEndSiteId());
        jyCollectPackageEntity.setEndSiteName(request.getEndSiteName());
        jyCollectPackageEntity.setBoxEndSiteId(request.getBoxReceiveId());
        jyCollectPackageEntity.setBoxEndSiteName(request.getBoxCode());
        Date now = new Date();
        jyCollectPackageEntity.setCreateTime(now);
        jyCollectPackageEntity.setUpdateTime(now);
        jyCollectPackageEntity.setCreateUserErp(request.getUser().getUserErp());
        jyCollectPackageEntity.setUpdateUserErp(request.getUser().getUserErp());
        jyCollectPackageEntity.setCreateUserName(request.getUser().getUserName());
        jyCollectPackageEntity.setUpdateUserName(request.getUser().getUserName());
        jyCollectPackageEntity.setInterceptFlag(intecepterFlag);
        jyCollectPackageEntity.setForceFlag(forceFlag);
        return jyCollectPackageEntity;
    }

    private TaskRequest assembleTaskRequest(CollectPackageReq request) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBoxCode(request.getBoxCode());
        taskRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskRequest.setReceiveSiteCode(request.getBoxReceiveId().intValue());//TODO  这个是箱号目的地
        taskRequest.setType(TASK_TYPE_SORTING);
        taskRequest.setKeyword1(String.valueOf(request.getCurrentOperate().getSiteCode()));
        taskRequest.setKeyword2(request.getBoxCode());
        taskRequest.setBusinessType(10);
        String body = assembleCollectDataBody(request);
        taskRequest.setBody(body);
        return taskRequest;
    }

    private String assembleCollectDataBody(CollectPackageReq request) {
        PackSortTaskBody taskBody = new PackSortTaskBody();
        taskBody.setBoxCode(request.getBoxCode());
        taskBody.setBusinessType(10);
        taskBody.setFeatureType(0);
        taskBody.setIsCancel(0);
        taskBody.setIsLoss(0);
        taskBody.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskBody.setSiteName(request.getCurrentOperate().getSiteName());
        taskBody.setPackageCode(request.getBarCode());
        taskBody.setReceiveSiteCode(request.getBoxReceiveId().intValue());
        taskBody.setReceiveSiteName(request.getBoxReceiveName());
        taskBody.setUserCode(request.getUser().getUserCode());
        taskBody.setUserName(request.getUser().getUserName());
        taskBody.setBizSource(SortingBizSourceEnum.ANDROID_SORTING.getCode());
        List<PackSortTaskBody> bodyList = new ArrayList<>();
        bodyList.add(taskBody);
        return JSON.toJSONString(bodyList);
    }

    private void collectPackageBizCheck(CollectPackageReq request) {
        //重复集包校验
        reCollectCheck(request);
        //校验箱号：是否存在 +是否已打印+状态合法性+是否已经发货
        boxCheck(request);
        //流向校验
        flowCheck(request);
        //sorting拦截器链
        execInterceptorChain(request);
    }

    private void reCollectCheck(CollectPackageReq request) {
        JyCollectPackageEntity query =new JyCollectPackageEntity();
        query.setPackageCode(request.getBarCode());
        query.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        JyCollectPackageEntity entity =jyCollectPackageScanRecordService.queryJyCollectPackageRecord(query);
        if (ObjectHelper.isNotNull(entity)){
            if (ObjectHelper.isNotNull(entity.getBoxCode()) && entity.getBoxCode().equals(request.getBoxCode())){
                throw new JyBizException("该包裹已经在此箱号中,请勿重复集包！");
            }else {
                throw new JyBizException("该包裹已经在"+entity.getBoxCode()+"中集包，如需重新集包，请前去取消后再重新集包！");
            }
        }
    }

    private void flowCheck(CollectPackageReq request) {
        JyBizTaskCollectPackageEntity collectPackageTask = jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (ObjectHelper.isEmpty(collectPackageTask)) {
            throw new JyBizException("集包任务不存在或者已经过期，请刷新界面！");
        }
        if (JyBizTaskCollectPackageStatusEnum.CANCEL.getCode().equals(collectPackageTask.getTaskStatus())) {
            throw new JyBizException("集包任务已作废-操作了批量取消集包！");
        }
        if (request.getForceCollectPackage()) {
            request.setEndSiteId(collectPackageTask.getEndSiteId());
            return;
        }
        //查询包裹的末级分拣中心
        Integer lastDmsId = getLastDmsByPackage(request.getBarCode());
        if (MixBoxTypeEnum.MIX_DISABLE.getCode().equals(collectPackageTask.getMixBoxType())) {
            //校验末级分拣中心是否为箱号目的地
            List<Integer> flowList = Collections.singletonList(collectPackageTask.getEndSiteId().intValue());
            checkLastDmsIfExitInCollectFlowList(lastDmsId, flowList, request, collectPackageTask);
        } else {
            //查询可混集的流向集合信息，校验末级分拣中心是否 在可集包的流向集合内
            List<Integer> flowList = queryMixBoxFlowListUnderTask(request);
            if (CollectionUtils.isEmpty(flowList)) {
                throw new JyBizException("未查询到当前任务的集包流向信息！");
            }
            checkLastDmsIfExitInCollectFlowList(lastDmsId, flowList, request, collectPackageTask);
        }
        if (ObjectHelper.isNotNull(request.getEndSiteId())) {
            BaseStaffSiteOrgDto staffSiteOrgDto = baseService.getSiteBySiteID(request.getEndSiteId().intValue());
            if (ObjectHelper.isNotNull(staffSiteOrgDto) && ObjectHelper.isNotNull(staffSiteOrgDto.getSiteName())) {
                request.setEndSiteName(staffSiteOrgDto.getSiteName());
            }
        }
    }

    private Integer getLastDmsByPackage(String packageCode) {
        Waybill waybill =waybillQueryManager.getWaybillByWayCode(WaybillUtil.getWaybillCode(packageCode));
        if (ObjectHelper.isEmpty(waybill)) {
            throw new JyBizException("未查询到运单数据!");
        }
        if (ObjectHelper.isEmpty(waybill.getOldSiteId())) {
            throw new JyBizException("运单对应的预分拣站点为空!");
        }
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService.getSiteBySiteID(waybill.getOldSiteId());
        if(ObjectHelper.isEmpty(baseStaffSiteOrgDto) || ObjectHelper.isEmpty(baseStaffSiteOrgDto.getDmsId())){
            //todo 这个地方要不要留强制集包的口子呢？
            throw new JyBizException("未获取到末级分拣中心信息!");
        }
        return baseStaffSiteOrgDto.getDmsId();
    }

    /**
     * 检查包裹末级分拣中心是否存在于可集包流向集合中
     * @param dmsId Dms的唯一标识符
     * @param flowSiteList 可以集包的流向列表
     * @param request 集包Request
     * @param collectPackageTask 集包任务实体
     */
    private void checkLastDmsIfExitInCollectFlowList(Integer dmsId, List<Integer> flowSiteList, CollectPackageReq request, JyBizTaskCollectPackageEntity collectPackageTask) {
        boolean collectEnable = false;
        for (Integer flowSite : flowSiteList) {
            if (dmsId.equals(flowSite)) {
                request.setEndSiteId(Long.valueOf(dmsId));
                collectEnable = true;
                break;
            }
        }
        if (!collectEnable) {
            checkIfPermitForceCollectPackage(request,collectPackageTask);
            throw new JyBizException("末级分拣中心不在允许集包的流向内，禁止集包！");
        }
    }

    //判断当前场地是否可以强制集包
    private void checkIfPermitForceCollectPackage(CollectPackageReq request, JyBizTaskCollectPackageEntity collectPackageTask) {
        List<String> siteList = dmsConfigManager.getPropertyConfig().getForceCollectPackageSiteList();
        if (CollectionUtils.isNotEmpty(siteList) && siteList.contains(request.getCurrentOperate().getSiteCode())) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService.getSiteBySiteID(collectPackageTask.getEndSiteId().intValue());
            if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())) {
                throw new JyBizException(FORCE_COLLECT_PACKAGE_WARNING, "末级分拣中心不在允许集包的流向内，是否强制集往【" + baseStaffSiteOrgDto.getSiteName() + "】？");
            }
        }
    }

    private List<Integer> queryMixBoxFlowListUnderTask(CollectPackageReq request) {
        List<JyBizTaskCollectPackageFlowEntity> flowEntityList =jyBizTaskCollectPackageFlowService.queryListByBizIds((Collections.singletonList(request.getBizId())));
        if (CollectionUtils.isNotEmpty(flowEntityList)){
            return flowEntityList.stream().map(jyBizTaskCollectPackageFlowEntity -> jyBizTaskCollectPackageFlowEntity.getEndSiteId().intValue()).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 查询混装的流向集合（查询混装的集包的流向集合）
     *
     * @return
     */
    private List<Integer> queryMixBoxFlowList(CollectPackageReq req) {
        CollectBoxFlowDirectionConf con = assembleCollectBoxFlowDirectionConf(req);
        List<CollectBoxFlowDirectionConf> collectBoxFlowDirectionConfList = boxLimitConfigManager.listCollectBoxFlowDirectionMix(con);//TODO 替换成查询任务的流向集合
        if (CollectionUtils.isEmpty(collectBoxFlowDirectionConfList)) {
            throw new JyBizException("未查询到对应目的地的可混装的流向集合！");
        }
        List<Integer> endSiteIdList = new ArrayList<>();
        for (CollectBoxFlowDirectionConf conf : collectBoxFlowDirectionConfList) {
            endSiteIdList.add(conf.getEndSiteId());
        }
        return endSiteIdList;
    }

    private CollectBoxFlowDirectionConf assembleCollectBoxFlowDirectionConf(CollectPackageReq req) {
        CollectBoxFlowDirectionConf conf = new CollectBoxFlowDirectionConf();
        conf.setStartSiteId(req.getCurrentOperate().getSiteCode());
        conf.setBoxReceiveId(req.getBoxReceiveId().intValue());
        conf.setFlowType(FlowDirectionTypeEnum.OUT_SITE.getCode());
        return conf;
    }

    /**
     * 校验路由节点是否在可混装的流向集合内
     *
     * @param router
     * @param flowList
     * @param request
     * @param collectPackageTask
     */
    private void checkRouterIfExitInCollectFlowList(String router, List<Integer> flowList, CollectPackageReq request, JyBizTaskCollectPackageEntity collectPackageTask) {
        List<Integer> nextNodeList = getNextNodeList(request.getCurrentOperate().getSiteCode(), router);
        if (CollectionUtils.isEmpty(nextNodeList)) {
            log.info("集包扫描获取路由信息：currentSiteCode:{},router:{}", request.getCurrentOperate().getSiteCode(), router);
            throw new JyBizException("未获取到有效的路由节点信息！");
        }
        boolean collectEnable = false;
        for (Integer nextNode : nextNodeList) {
            if (flowList.contains(nextNode)) {
                request.setEndSiteId(Long.valueOf(nextNode));
                collectEnable = true;
                break;
            }
        }
        if (!collectEnable) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService.getSiteBySiteID(collectPackageTask.getEndSiteId().intValue());
            if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())) {
                throw new JyBizException(FORCE_COLLECT_PACKAGE_WARNING, "路由节点不在允许集包的流向内，是否强制集往【" + baseStaffSiteOrgDto.getSiteName() + "】？");
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
        if (request.getForceCollectPackage() || request.getSkipInterceptChain()) {
            return;
        }
        PdaOperateRequest pdaOperateRequest = assemblePdaOperateRequest(request);
        SortingJsfResponse response = sortingService.check(pdaOperateRequest);

        if (!Objects.equals(response.getCode(), SortingResponse.CODE_OK)) {
            if (response.getCode() >= 30000 && response.getCode() <= 40000) {
                throw new JyBizException(CONFIRM_COLLECT_PACKAGE_WARNING, response.getMessage());
            }
            saveIntercepterJyCollectPackageScanRecord(request);
            throw new JyBizException(response.getMessage());
        }
    }

    private PdaOperateRequest assemblePdaOperateRequest(CollectPackageReq request) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setBoxCode(request.getBoxCode());
        pdaOperateRequest.setBusinessType(DmsConstants.BUSSINESS_TYPE_POSITIVE);
        pdaOperateRequest.setIsGather(0);
        //TODO
        //pdaOperateRequest.setOperateType(request.getOperateType());
        pdaOperateRequest.setPackageCode(request.getBarCode());
        pdaOperateRequest.setReceiveSiteCode(request.getEndSiteId().intValue());
        pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        pdaOperateRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
        pdaOperateRequest.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(), DateUtil.FORMAT_DATE_TIME));
        pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
        pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
        pdaOperateRequest.setSkipFilter(true);//TODO
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
        if (box.getCode().length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT) {
            throw new JyBizException("箱号超长！");
        }
        //判断箱子是否已发货
        if (boxService.checkBoxIsSent(request.getBoxCode(), box.getCreateSiteCode())) {
            throw new JyBizException("该箱号已经发货，禁止继续集包！");
        }
        request.setBoxReceiveId(Long.valueOf(box.getReceiveSiteCode()));
        request.setBoxReceiveName(box.getReceiveSiteName());
    }

    private void collectPackageBaseCheck(CollectPackageReq request) {
        if (!ObjectHelper.isNotNull(request.getBizId())) {
            throw new JyBizException("参数错误：缺失任务bizId！");
        }
        if (!ObjectHelper.isNotNull(request.getBoxCode())) {
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isBoxcode(request.getBoxCode())) {
            throw new JyBizException("参数错误：不支持该类型箱号！");
        }
        if (!ObjectHelper.isNotNull(request.getBarCode())) {
            throw new JyBizException("参数错误：缺失包裹号！");
        }
        if (!WaybillUtil.isPackageCode(request.getBarCode())) {
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
        query.setTaskStatusList(Arrays.asList(JyBizTaskCollectPackageStatusEnum.TO_COLLECT.getCode(), JyBizTaskCollectPackageStatusEnum.COLLECTING.getCode(), JyBizTaskCollectPackageStatusEnum.SEALED.getCode()));
        resp.setCollectPackStatusCountList(jyBizTaskCollectPackageService.queryTaskStatusCount(query));
        resp.setCollectPackTaskDtoList(getCollectPackageFlowDtoList(getPageQuery(request, time)));
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

        // 批量获取统计信息
        HashMap<String, List<CollectScanDto>> aggMap = getScanAgg(boxCodeList);

        // 组装任务
        List<CollectPackageTaskDto> collectPackTaskDtoList = taskList.stream().map(task -> {
            CollectPackageTaskDto taskDto = new CollectPackageTaskDto();
            BeanUtils.copyProperties(task, taskDto);
            // 查询集包袋号
            taskDto.setMaterialCode(boxMaterialRelationMap.get(task.getBoxCode()));
            List<CollectScanDto> aggDtoList = aggMap.get(task.getBizId());
            if (!CollectionUtils.isEmpty(aggDtoList)) {
                for (CollectScanDto collectScanDto : aggDtoList) {
                    if (CollectPackageExcepScanEnum.HAVE_SCAN.getCode().equals(collectScanDto.getType())) {
                        taskDto.setScanCount(collectScanDto.getCount());
                    } else if (CollectPackageExcepScanEnum.INTERCEPTED.getCode().equals(collectScanDto.getType())) {
                        taskDto.setInterceptCount(collectScanDto.getCount());
                    }
                }
            }

            // 流向信息
            taskDto.setCollectPackageFlowDtoList(flowMap.get(task.getBizId()));
            BoxTypeEnum boxType = getFromCode(task.getBoxType());
            if (boxType != null) {
                taskDto.setBoxTypeDesc(boxType.getName());
            }
            if (MixBoxTypeEnum.MIX_ENABLE.getCode().equals(task.getMixBoxType())) {
                taskDto.setMixBoxTypeDesc(MixBoxTypeEnum.MIX_ENABLE.getName());
            }else {
                taskDto.setMixBoxTypeDesc("");
            }
            taskDto.setTransportTypeDesc(BoxTransportTypeEnum.getNameByCode(task.getTransportType()));
            return taskDto;
        }).collect(Collectors.toList());
        return collectPackTaskDtoList;
    }

    private HashMap<String, List<CollectScanDto>> getScanAgg(List<String> bizIdList) {
        HashMap<String, List<CollectScanDto>> aggMap = new HashMap<>();
        ListTaskStatisticQueryDto queryDto = new ListTaskStatisticQueryDto();
        List<StatisticsUnderTaskQueryDto> queryDtoList = new ArrayList<>();
        for (String bizId : bizIdList) {
            StatisticsUnderTaskQueryDto dto = new StatisticsUnderTaskQueryDto();
            dto.setBizId(bizId);
            queryDtoList.add(dto);
        }
        queryDto.setStatisticsUnderTaskQueryDtoList(queryDtoList);

        ListTaskStatisticDto listTaskStatisticDto = null;
        try{
            listTaskStatisticDto = collectPackageManger.listTaskStatistic(queryDto);
        }catch (Exception e) {
            log.info("查询统计数据异常：{}", JsonHelper.toJson(queryDto));
            return aggMap;
        }
        if (listTaskStatisticDto == null || CollectionUtils.isEmpty(listTaskStatisticDto.getStatisticsUnderTaskDtoList())) {
            log.info("未获取到统计数据：{}", JsonHelper.toJson(queryDto));
            return aggMap;
        }

        listTaskStatisticDto.getStatisticsUnderTaskDtoList().stream().map(item -> aggMap.put(item.getBizId(), item.getExcepScanDtoList()));
        return aggMap;
    }

    private HashMap<String, String> getBoxMaterialRelationMap(List<BoxMaterialRelation> boxMaterialRelationList) {
        if (CollectionUtils.isEmpty(boxMaterialRelationList)) {
            return new HashMap<>();
        }
        HashMap<String, String> boxMaterialRelationMap = new HashMap<>();
        boxMaterialRelationList.stream().map(item -> boxMaterialRelationMap.put(item.getBoxCode(), item.getMaterialCode()));
        return boxMaterialRelationMap;
    }

    /**
     * 根据任务获取流向信息
     *
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
                    flowDtoList = new ArrayList<>();
                    flowMap.put(entity.getCollectPackageBizId(), flowDtoList);
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

        if (!checkTaskDetailReq(request, result)) {
            return result;
        }

        JyBizTaskCollectPackageEntity task = getTaskDetailByReq(request);
        log.info("根据条件获取集包任务为：task：{}", JsonHelper.toJson(task));
        TaskDetailResp resp = new TaskDetailResp();
        result.setData(resp);
        if (task == null) {
            result.setCode(RESULT_NULL_CODE);
            result.setMessage(request.getBarCode() + "未获取到集包任务！");
            return result;
        }
        if (!Objects.equals(task.getStartSiteId().intValue(), request.getCurrentOperate().getSiteCode())) {
            result.setCode(RESULT_NULL_CODE);
            result.setMessage(request.getBarCode() + "非本场地箱号|包裹！");
            return result;
        }

        CollectPackageTaskDto taskDto = new CollectPackageTaskDto();
        BeanUtils.copyProperties(task, taskDto);
        // 查询集包袋号
        taskDto.setMaterialCode(cycleBoxService.getBoxMaterialRelation(task.getBoxCode()));

        // 统计数据
        HashMap<String, List<CollectScanDto>> scanAgg = getScanAgg(Collections.singletonList(taskDto.getBizId()));
        List<CollectScanDto> collectScanDtos = scanAgg.get(taskDto.getBizId());
        if (!CollectionUtils.isEmpty(collectScanDtos)) {
            for (CollectScanDto collectScanDto : collectScanDtos) {
                if (CollectPackageExcepScanEnum.HAVE_SCAN.getCode().equals(collectScanDto.getType())) {
                    taskDto.setScanCount(collectScanDto.getCount());
                } else if (CollectPackageExcepScanEnum.INTERCEPTED.getCode().equals(collectScanDto.getType())) {
                    taskDto.setInterceptCount(collectScanDto.getCount());
                }
            }
        }

        // 流向信息
        HashMap<String, List<CollectPackageFlowDto>> flowInfo = getFlowMapByTask(Collections.singletonList(taskDto.getBizId()));
        taskDto.setCollectPackageFlowDtoList(flowInfo.get(task.getBizId()));
        resp.setCollectPackageTaskDto(taskDto);
        return result;
    }

    private JyBizTaskCollectPackageEntity getTaskDetailByReq(TaskDetailReq request) {
        if (BusinessUtil.isBoxcode(request.getBarCode())) {
            // 如果是箱号
            return jyBizTaskCollectPackageService.findByBoxCode(request.getBarCode());
        }else if (WaybillUtil.isPackageCode(request.getBarCode())) {
            JyCollectPackageEntity query = new JyCollectPackageEntity();
            query.setPackageCode(request.getBarCode());
            query.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
            JyCollectPackageEntity collectPackage = jyCollectPackageScanRecordService.queryJyCollectPackageRecord(query);
            if (collectPackage == null || StringUtils.isEmpty(collectPackage.getBizId())) {
                return null;
            }
            return jyBizTaskCollectPackageService.findByBizId(collectPackage.getBizId());
        }else {
            return jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        }
    }

    private boolean checkTaskDetailReq(TaskDetailReq request, InvokeResult<TaskDetailResp> result) {
        if (request == null || (StringUtils.isEmpty(request.getBizId()) && StringUtils.isEmpty(request.getBarCode()))) {
            result.setCode(RESULT_NULL_CODE);
            result.setMessage("参数异常！");
            return false;
        }

        if (!StringUtils.isEmpty(request.getBarCode())
                && !WaybillUtil.isPackageCode(request.getBarCode())
                && !BusinessUtil.isBoxcode(request.getBarCode())) {
            result.setCode(RESULT_NULL_CODE);
            result.setMessage("请扫描正确的箱号或包裹号！");
            return false;
        }
        return true;
    }

    @Override
    public InvokeResult<SealingBoxResp> sealingBox(SealingBoxReq request) {
        InvokeResult<SealingBoxResp> result = new InvokeResult<>();
        if (!checkSealingReq(request, result)) {
            return result;
        }

        List<String> bizIds = request.getSealingBoxDtoList().stream().map(SealingBoxDto::getBizId).collect(Collectors.toList());
        List<JyBizTaskCollectPackageEntity> taskList = jyBizTaskCollectPackageService.findByBizIds(bizIds);
        if (CollectionUtils.isEmpty(taskList)) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("未获取到有效的任务信息!");
            return result;
        }

        // 过滤掉已经封箱的箱号数据
        List<Long> ids = new ArrayList<>();
        for (JyBizTaskCollectPackageEntity task : taskList) {
            if (!JyBizTaskCollectPackageStatusEnum.SEALED.getCode().equals(task.getTaskStatus())) {
                ids.add(task.getId());
            }
        }
        if (CollectionUtils.isEmpty(ids)) {
            return result;
        }

        JyBizTaskCollectPackageQuery query = new JyBizTaskCollectPackageQuery();
        query.setIds(ids);
        query.setTaskStatus(JyBizTaskCollectPackageStatusEnum.SEALED.getCode());
        log.info("开始更新集包任务状态：{}", JsonHelper.toJson(query));
        if (!jyBizTaskCollectPackageService.updateStatusByIds(query)) {
            log.info("批量封箱失败：{}", JsonHelper.toJson(query));
            result.setCode(RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("批量封箱失败!");
            return result;
        }
        return result;
    }

    private boolean checkSealingReq(SealingBoxReq request, InvokeResult<SealingBoxResp> result) {
        if (request == null || CollectionUtils.isEmpty(request.getSealingBoxDtoList()) || StringUtils.isEmpty(request.getSealingBoxDtoList().get(0).getBoxCode())) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("未获取到箱信息！");
            return false;
        }

        if (request.getSealingBoxDtoList().size() > SEALING_BOX_LIMIT) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("批量封箱的数量不能超过" + SEALING_BOX_LIMIT + "， 请取消勾选后再提交!");
            return false;
        }

        // 当前不存在封箱场景,只校验第一个是否空箱
        Integer sum = sortingService.getSumByBoxCode(request.getSealingBoxDtoList().get(0).getBoxCode());
        if (sum <= 0) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("该箱号为空箱，不允许封箱！");
            return false;
        }
        // 绑定集包袋校验
        String materialRelation = cycleBoxService.getBoxMaterialRelation(request.getSealingBoxDtoList().get(0).getBoxCode());
        if (StringUtils.isEmpty(materialRelation)) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("该箱号未绑定集包袋，不允许封箱！");
            return false;
        }
        return true;
    }

    /**
     * 绑定集装袋方法，用于将请求绑定到集装袋
     *
     * @param request 绑定集装袋请求对象
     * @return 绑定集装袋响应结果
     */
    @Override
    public InvokeResult bindCollectBag(BindCollectBagReq request) {
        checkBindCollectBagReq(request);
        JyBizTaskCollectPackageEntity entity =jyBizTaskCollectPackageService.findByBoxCode(request.getBoxCode());
        if (ObjectHelper.isEmpty(entity)){
            return new InvokeResult(COLLECT_PACKAGE_TASK_NO_EXIT_CODE,COLLECT_PACKAGE_TASK_NO_EXIT_MESSAGE);
        }

        BoxMaterialRelationRequest req = assembleBoxMaterialRelationRequest(request);
        InvokeResult bindMaterialResp = cycleBoxService.boxMaterialRelationAlter(req);
        if (!bindMaterialResp.codeSuccess()) {
            if(HintCodeConstants.CYCLE_BOX_NOT_BELONG_ERROR.equals(String.valueOf(bindMaterialResp.getCode()))){
                return new InvokeResult(CONFIRM_COLLECT_PACKAGE_WARNING, bindMaterialResp.getMessage());
            }
            return new InvokeResult(SERVER_ERROR_CODE, bindMaterialResp.getMessage());
        }
        updateCollectBag(request, entity);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    }

    private void updateCollectBag(BindCollectBagReq request, JyBizTaskCollectPackageEntity entity) {
        JyBizTaskCollectPackageEntity task =new JyBizTaskCollectPackageEntity();
        task.setId(entity.getId());
        task.setMaterialCode(request.getMaterialCode());
        task.setUpdateTime(new Date());
        task.setUpdateUserErp(request.getUser().getUserErp());
        task.setUpdateUserName(request.getUser().getUserName());
        jyBizTaskCollectPackageService.updateById(task);
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
        req.setForceFlag(request.getForceBindFlag());
        return req;
    }

    private void checkBindCollectBagReq(BindCollectBagReq request) {
        if (!ObjectHelper.isNotNull(request.getBoxCode())) {
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isBoxcode(request.getBoxCode())) {
            throw new JyBizException("参数错误：非法的箱号！");
        }
        if (!ObjectHelper.isNotNull(request.getMaterialCode())) {
            throw new JyBizException("参数错误：缺失集包袋号！");
        }
        if (!BusinessUtil.isCollectionBag(request.getMaterialCode())) {
            throw new JyBizException("参数错误：非法的集包袋号！");
        }
    }

    /**
     * 取消集包功能
     * @param request 取消集包请求对象
     * @return 响应结果对象，包含取消集包响应信息
     * @throws JyBizException 当任务已作废或者过期时抛出异常
     */
    @Override
    public InvokeResult<CancelCollectPackageResp> cancelCollectPackage(CancelCollectPackageReq request) throws JyBizException {
        // 校验取消集包请求对象
        checkCancelCollectPackageReq(request);
        // 根据业务ID查询集包任务实体
        JyBizTaskCollectPackageEntity task = jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        // 判断任务是否为空或者已取消
        if (ObjectHelper.isEmpty(task) || JyBizTaskCollectPackageStatusEnum.CANCEL.getCode().equals(task.getTaskStatus())) {
            throw new JyBizException("该任务已作废或者过期，请勿重复操作！");
        }
        if (request.getCancelAllFlag()) {
            // 构建箱子锁的key
            String boxLockKey = String.format(Constants.JY_COLLECT_BOX_LOCK_PREFIX, request.getBoxCode());
            // 尝试获取箱子锁
            if (!jimDbLock.lock(boxLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
                throw new JyBizException("当前系统繁忙,请稍后再试！");
            }
            try {
                // 异步执行取消集包
                produceBatchCancelCollectPackageMsg(request);
                // 关闭箱号，不允许使用
                closeCollectPackageTask(request, task);
            } finally {
                // 释放箱子锁
                jimDbLock.releaseLock(boxLockKey, request.getRequestId());
            }
        } else {
            // 封装取消单个包裹集包能力的DTO对象
            CancelCollectPackageDto cancelCollectPackageDto = assembleCancelCollectPackageDto(request);
            // 取消集包
            jyBizTaskCollectPackageService.cancelJyCollectPackage(cancelCollectPackageDto);
        }
        // 返回成功响应结果对象
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    }

    private CancelCollectPackageDto assembleCancelCollectPackageDto(CancelCollectPackageReq request) {
        CancelCollectPackageDto cancelCollectPackageDto = new CancelCollectPackageDto();
        cancelCollectPackageDto.setBizId(request.getBizId());
        cancelCollectPackageDto.setPackageCode(request.getBarCode());
        cancelCollectPackageDto.setBoxCode(request.getBoxCode());
        cancelCollectPackageDto.setSiteCode(request.getCurrentOperate().getSiteCode());
        cancelCollectPackageDto.setSiteName(request.getCurrentOperate().getSiteName());
        cancelCollectPackageDto.setUpdateUserErp(request.getUser().getUserErp());
        cancelCollectPackageDto.setUpdateUserName(request.getUser().getUserName());
        cancelCollectPackageDto.setUpdateUserCode(request.getUser().getUserCode());
        return cancelCollectPackageDto;
    }

    private void closeCollectPackageTask(CancelCollectPackageReq req, JyBizTaskCollectPackageEntity task) {
        JyBizTaskCollectPackageEntity updateDto = new JyBizTaskCollectPackageEntity();
        updateDto.setId(task.getId());
        updateDto.setTaskStatus(JyBizTaskCollectPackageStatusEnum.CANCEL.getCode());
        updateDto.setUpdateTime(new Date());
        updateDto.setUpdateUserErp(req.getUser().getUserErp());
        updateDto.setUpdateUserName(req.getUser().getUserName());
        jyBizTaskCollectPackageService.updateById(updateDto);
    }


    private void produceBatchCancelCollectPackageMsg(CancelCollectPackageReq request) {
        // 获取运单包裹数
        SortingQuery query = new SortingQuery();
        query.setBoxCode(request.getBoxCode());
        query.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        query.setPageSize(Constants.PDA_DEFAULT_PAGE_MAXSIZE);
        int pageNo = 1;
        while (true) {
            query.setPageNo(pageNo);
            List<Sorting> sortingList = sortingService.pageQueryByBoxCode(query);
            if (CollectionUtils.isEmpty(sortingList)) {
                break;
            }
            log.info("第{}页查询集包数据{}",pageNo,JsonHelper.toJson(sortingList));
            BatchCancelCollectPackageMqDto msg = assembleBatchCancelCollectPackageMqDto(sortingList, request);
            batchCancelCollectPackageProduce.sendOnFailPersistent(request.getBoxCode(),JsonHelper.toJson(msg));
            log.info("===================={} 成功完成批量取消集包消息发送第{}页 待取消包裹{}============================", request.getBoxCode(),pageNo,JsonHelper.toJson(msg));
            pageNo++;
        }
    }

    private BatchCancelCollectPackageMqDto assembleBatchCancelCollectPackageMqDto(List<Sorting> sortingList, CancelCollectPackageReq request) {
        BatchCancelCollectPackageMqDto batchCancelCollectPackageMqDto = new BatchCancelCollectPackageMqDto();
        batchCancelCollectPackageMqDto.setBizId(request.getBizId());
        batchCancelCollectPackageMqDto.setBoxCode(request.getBoxCode());
        batchCancelCollectPackageMqDto.setSiteCode(request.getCurrentOperate().getSiteCode());
        batchCancelCollectPackageMqDto.setSiteName(request.getCurrentOperate().getSiteName());
        batchCancelCollectPackageMqDto.setUpdateUserErp(request.getUser().getUserErp());
        batchCancelCollectPackageMqDto.setUpdateUserName(request.getUser().getUserName());
        batchCancelCollectPackageMqDto.setUpdateUserCode(request.getUser().getUserCode());
        List<String> packageList = sortingList.stream().map(sorting -> sorting.getPackageCode()).collect(Collectors.toList());
        batchCancelCollectPackageMqDto.setPackageCodeList(packageList);
        return batchCancelCollectPackageMqDto;
    }

    @Override
    public InvokeResult<CollectPackageTaskResp> searchPackageTask(SearchPackageTaskReq request) {
        InvokeResult<CollectPackageTaskResp> result = new InvokeResult<>();
        if (!checkSearchPackageTaskReq(request, result)) {
            return result;
        }
        CollectPackageTaskResp resp = new CollectPackageTaskResp();
        result.setData(resp);
        JyBizTaskCollectPackageQuery searchPageQuery = getSearchPageQuery(request, result);
        if (searchPageQuery == null) {
            return result;
        }
        log.info("集包任务检索请求：{}", JsonHelper.toJson(searchPageQuery));
        resp.setCollectPackStatusCountList(jyBizTaskCollectPackageService.queryTaskStatusCount(searchPageQuery));
        resp.setCollectPackTaskDtoList(getCollectPackageFlowDtoList(searchPageQuery));
        return result;
    }

    @Override
    public InvokeResult<StatisticsUnderTaskQueryResp> queryStatisticsUnderTask(StatisticsUnderTaskQueryReq request) {
        checkStatisticsUnderTaskQueryReq(request);

        //查询任务的扫描类型统计数据
        StatisticsUnderTaskQueryDto taskQueryDto =assembleStatisticsUnderTaskQueryDto(request);
        StatisticsUnderTaskDto taskStatistics =collectPackageManger.queryTaskStatistic(taskQueryDto);

        if (ObjectHelper.isNotNull(taskStatistics)){
            //查询任务流向的聚合统计数据
            StatisticsUnderTaskQueryDto taskFlowQueryDto = assembleStatisticsUnderTaskQueryDto(request);
            StatisticsUnderTaskDto taskFlowStatistic =collectPackageManger.queryTaskFlowStatistic(taskFlowQueryDto);
            //封装返回数据
            StatisticsUnderTaskQueryResp statisticsUnderTaskQueryResp =converToStatisticsUnderTaskQueryResp(taskStatistics,taskFlowStatistic);
            return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,statisticsUnderTaskQueryResp);
        }
        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE);
    }

    private StatisticsUnderTaskQueryResp converToStatisticsUnderTaskQueryResp(StatisticsUnderTaskDto taskStatistics,StatisticsUnderTaskDto taskFlowStatistic) {
        StatisticsUnderTaskQueryResp statisticsUnderTaskQueryResp =new StatisticsUnderTaskQueryResp();
        statisticsUnderTaskQueryResp.setBoxCode(taskStatistics.getBoxCode());
        if (CollectionUtils.isNotEmpty(taskStatistics.getExcepScanDtoList())){
            List<ExcepScanDto> excepScanDtoList = com.jd.bluedragon.utils.BeanUtils.copy(taskStatistics.getExcepScanDtoList(),ExcepScanDto.class);
            statisticsUnderTaskQueryResp.setExcepScanDtoList(excepScanDtoList);
        }
        if (ObjectHelper.isNotNull(taskFlowStatistic) && CollectionUtils.isNotEmpty(taskFlowStatistic.getCollectPackageFlowDtoList())){
            statisticsUnderTaskQueryResp.setCollectPackageFlowDtoList(taskFlowStatistic.getCollectPackageFlowDtoList());
        }
        return statisticsUnderTaskQueryResp;
    }

    private void checkStatisticsUnderTaskQueryReq(StatisticsUnderTaskQueryReq request) {
        if (ObjectHelper.isEmpty(request)){
            throw new JyBizException("参数错误：缺失请求参数！");
        }
        if (ObjectHelper.isEmpty(request.getBizId())){
            throw new JyBizException("参数错误：缺失任务bizId参数！");
        }
        if (ObjectHelper.isEmpty(request.getType())){
            request.setType(CollectPackageExcepScanEnum.INTERCEPTED.getCode());
        }
    }

    private StatisticsUnderTaskQueryDto assembleStatisticsUnderTaskQueryDto(StatisticsUnderTaskQueryReq request) {
        StatisticsUnderTaskQueryDto statisticsUnderTaskQueryDto =new StatisticsUnderTaskQueryDto();
        statisticsUnderTaskQueryDto.setBizId(request.getBizId());
        statisticsUnderTaskQueryDto.setBoxCode(request.getBoxCode());
        statisticsUnderTaskQueryDto.setType(request.getType());
        return statisticsUnderTaskQueryDto;
    }


    @Override
    public InvokeResult<StatisticsUnderFlowQueryResp> queryStatisticsUnderFlow(StatisticsUnderFlowQueryReq request) {
        checkStatisticsUnderFlowQueryReq(request);
        StatisticsUnderFlowQueryReq statisticsUnderFlowQueryReq =assembleStatisticsUnderFlowQueryReq(request);
        StatisticsUnderFlowQueryResp statisticsUnderFlowQueryResp =collectPackageManger.listPackageUnderFlow(statisticsUnderFlowQueryReq);
        if (ObjectHelper.isNotNull(statisticsUnderFlowQueryResp)){
            return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,statisticsUnderFlowQueryResp);
        }
        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE);
    }

    private void checkStatisticsUnderFlowQueryReq(StatisticsUnderFlowQueryReq request) {
        if (ObjectHelper.isEmpty(request)){
            throw new JyBizException("参数错误：缺失请求参数！");
        }
        if (ObjectHelper.isEmpty(request.getBizId())){
            throw new JyBizException("参数错误：缺失任务bizId参数！");
        }
        if (ObjectHelper.isEmpty(request.getEndSiteId())){
            throw new JyBizException("参数错误：缺失任务流向id参数！");
        }
        if (ObjectHelper.isEmpty(request.getPageNo())){
            throw new JyBizException("参数错误：缺失pageNo参数！");
        }
        if (ObjectHelper.isEmpty(request.getPageSize())){
            throw new JyBizException("参数错误：缺失pageSize参数！");
        }
    }

    private StatisticsUnderFlowQueryReq assembleStatisticsUnderFlowQueryReq(StatisticsUnderFlowQueryReq request) {
        StatisticsUnderFlowQueryReq statisticsUnderFlowQueryReq =new StatisticsUnderFlowQueryReq();
        statisticsUnderFlowQueryReq.setBizId(request.getBizId());
        statisticsUnderFlowQueryReq.setEndSiteId(request.getEndSiteId());
        statisticsUnderFlowQueryReq.setPageNo(request.getPageNo());
        statisticsUnderFlowQueryReq.setPageSize(request.getPageSize());
        return statisticsUnderFlowQueryReq;
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

    private JyBizTaskCollectPackageQuery getSearchPageQuery(SearchPackageTaskReq request, InvokeResult<CollectPackageTaskResp> result) {
        JyBizTaskCollectPackageQuery query = new JyBizTaskCollectPackageQuery();
        query.setTaskStatus(request.getTaskStatus());
        query.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        query.setOffset((request.getPageNo() - 1) * request.getPageSize());
        query.setLimit(request.getPageSize());
        query.setTaskStatusList(Arrays.asList(JyBizTaskCollectPackageStatusEnum.TO_COLLECT.getCode(), JyBizTaskCollectPackageStatusEnum.COLLECTING.getCode(), JyBizTaskCollectPackageStatusEnum.SEALED.getCode()));

        // 如果是箱号
        if (BusinessHelper.isBoxcode(request.getBarCode())) {
            query.setBoxCode(request.getBarCode());
        } else if (WaybillUtil.isPackageCode(request.getBarCode())) {
            // 如果是包裹号，按流向查询
            Long endSiteCode = getWaybillNextRouter(WaybillUtil.getWaybillCode(request.getBarCode()), Long.valueOf(request.getCurrentOperate().getSiteCode()));
            if (endSiteCode == null) {
                result.setCode(RESULT_THIRD_ERROR_CODE);
                result.setMessage("未获取到当前包裹的路由信息！");
                return null;
            }
            query.setEndSiteId(endSiteCode);
        }
        Date time = DateHelper.addHoursByDay(new Date(), -Double.valueOf(dmsConfigManager.getPropertyConfig().getJyCollectPackageTaskQueryTimeLimit()));
        query.setCreateTime(time);
        return query;
    }

    public Long getWaybillNextRouter(String waybillCode, Long startSiteId) {
        RouteNextDto routeNextDto = routerService.matchRouterNextNode(startSiteId.intValue(), waybillCode);
        return routeNextDto == null || routeNextDto.getFirstNextSiteId() == null? null : routeNextDto.getFirstNextSiteId().longValue();
    }


    private void checkCancelCollectPackageReq(CancelCollectPackageReq request) {
        if (!ObjectHelper.isNotNull(request.getBoxCode())) {
            throw new JyBizException("参数错误：缺失箱号！");
        }
    }
}
