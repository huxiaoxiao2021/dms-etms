package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperatorData;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.common.dto.comboard.request.ExcepScanDto;
import com.jd.bluedragon.common.dto.sorting.request.PackSortTaskBody;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.common.service.WaybillCommonService;
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
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.constants.BoxSubTypeEnum;
import com.jd.bluedragon.distribution.box.constants.BoxTypeEnum;
import com.jd.bluedragon.distribution.box.constants.BoxTypeV2Enum;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.service.BoxRelationService;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageFlowEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageQuery;
import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.BatchCancelCollectPackageMqDto;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CancelCollectPackageDto;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CollectScanDto;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingBizSourceEnum;
import com.jd.bluedragon.distribution.sorting.domain.SortingQuery;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.enums.WaybillVasEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.WaybillVasDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.constants.OperateNodeConstants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf;
import com.jdl.basic.api.enums.FlowDirectionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
import static com.jd.bluedragon.Constants.ORIGINAL_CROSS_TYPE_AIR;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.box.constants.BoxSubTypeEnum.TYPE_BCHK;
import static com.jd.bluedragon.distribution.box.constants.BoxSubTypeEnum.TYPE_BCLY;
import static com.jd.bluedragon.distribution.box.constants.BoxTypeEnum.getFromCode;
import static com.jd.bluedragon.distribution.box.domain.Box.BOX_TRANSPORT_TYPE_AIR;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;
import static com.jd.bluedragon.distribution.task.domain.Task.TASK_TYPE_SORTING;
import static com.jd.bluedragon.dms.utils.BusinessUtil.getOriginalCrossType;
import static com.jd.bluedragon.dms.utils.BusinessUtil.isReverseSite;
import static com.jd.bluedragon.utils.BusinessHelper.isThirdSite;
import static com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf.*;
import static com.jdl.basic.api.enums.WorkSiteTypeEnum.RETURN_CENTER;

@Service("jyCollectPackageService")
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
    protected TaskService taskService;
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
    @Qualifier("collectPackageManger")
    private CollectPackageManger collectPackageManger;

    @Autowired
    private RouterService routerService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Resource
    private FuncSwitchConfigService funcSwitchConfigService;
    @Autowired
    BoxRelationService boxRelationService;

    @Autowired
    private WaybillCommonService waybillCommonService;

    public CollectPackageManger getCollectPackageManger(){
        return this.collectPackageManger;
    }

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

    @Override
    public InvokeResult<CollectPackageResp> collectBox(CollectPackageReq request) {
        //基础校验
        collectBoxBaseCheck(request);
        //集装业务校验
        collectBoxBizCheck(request);
        //执行集装
        CollectPackageResp response = new CollectPackageResp();
        execCollectBox(request, response);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, response);
    }

    @Override
    public InvokeResult<CollectPackageResp> collectPackageForMachine(CollectPackageReq request) {
        return null;
    }

    @Override
    public InvokeResult<CollectPackageResp> collectBoxForMachine(CollectPackageReq request) {
        return null;
    }

    /**
     * 执行集包操作
     * @param request 集包请求对象
     * @param response 集包响应对象
     */
    protected void execCollectPackage(CollectPackageReq request, CollectPackageResp response) {
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
            //执行集包
            TaskRequest taskRequest = assembleTaskRequest(request);
            taskService.add(taskRequest);
            //保存集包扫描记录
            saveJyCollectPackageScanRecord(request);
            checkIfNeedUpdateStatus(request, collectPackageTask);
            response.setEndSiteId(request.getEndSiteId());
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
        JyCollectPackageEntity jyCollectPackageEntity = converJyCollectPackageEntity(request, false, request.getForceCollectPackage());
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
        jyCollectPackageEntity.setBoxEndSiteName(request.getBoxReceiveName());
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

    protected TaskRequest assembleTaskRequest(CollectPackageReq request) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBoxCode(request.getBoxCode());
        taskRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskRequest.setReceiveSiteCode(request.getBoxReceiveId().intValue());
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
        taskBody.setOperatorData(request.getCurrentOperate().getOperatorData());
        List<PackSortTaskBody> bodyList = new ArrayList<>();
        bodyList.add(taskBody);
        return JSON.toJSONString(bodyList);
    }

    public void collectPackageBizCheck(CollectPackageReq request) {
        //重复集包校验
        reCollectCheck(request);
        //校验箱号：是否存在 +是否已打印+状态合法性+是否已经发货
        boxCheck(request);
        //校验箱号类型与包裹目的地站点类型是否匹配
        checkBoxEndSiteMatch(request);
        //流向校验
        flowCheck(request);
        //sorting拦截器链
        execInterceptorChain(request);
        //封箱校验
        sealBoxCheck(request);
    }

    /**
     * 校验箱号类型与包裹目的地站点类型是否匹配
     * @param request
     */
    private void checkBoxEndSiteMatch(CollectPackageReq request) {
        if (!dmsConfigManager.getPropertyConfig().getJyCollectPackCheckBoxEndSiteMatchSwitch()) {
            return;
        }
        BoxTypeV2Enum boxType = BoxTypeV2Enum.getFromCode(request.getBoxType());
        BoxSubTypeEnum boxSubType = BoxSubTypeEnum.getFromCode(request.getBoxSubType());
        Waybill waybill =waybillQueryManager.getWaybillByWayCode(WaybillUtil.getWaybillCode(request.getBarCode()));
        if (ObjectHelper.isEmpty(waybill)) {
            throw new JyBizException("未查询到运单数据!");
        }
        if (Objects.isNull(waybill.getOldSiteId())) {
            throw new JyBizException("运单对应的预分拣站点为空!");
        }

        // 特安类型箱号只能扫描特安标识的运单
        if (BoxTypeV2Enum.TYPE_TA.equals(boxType) && !isTAWaybill(request)) {
            throw new JyBizException("TA开头的箱号，只能扫描特安标识的运单，禁止扫描其他运单!");
        }

        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(waybill.getOldSiteId());
        if (siteInfo == null) {
            throw new JyBizException("未获取到箱号目的地信息!");
        }
        // TC开头的箱号，只能扫描目的地只能是备件库、仓储、退货组、逆向仓、售后仓等
        if (BoxTypeV2Enum.TYPE_TC.equals(boxType)
                && !(isReverseSite(siteInfo.getSiteType())
                || Objects.equals(RETURN_CENTER.getFirstTypesOfThird(), siteInfo.getSortType()))) {
            throw new JyBizException("TC开头的箱号，只能扫描目的地只能是 备件库、仓储、退货组、逆向仓、售后仓等!");
        }

        //BX开头的箱号校验
        if (BoxTypeV2Enum.TYPE_BX.equals(boxType) && !bxBoxEndSiteTypeCheck(siteInfo)) {
            throw new JyBizException("BX开头的箱号，只能扫描目的地只能是三方配送公司");
        }

        //BC开头的箱号校验
        if (BoxTypeV2Enum.TYPE_BC.equals(boxType) && !bcBoxEndSiteTypeCheck(siteInfo, isTAWaybill(request))) {
            throw new JyBizException("BC开头的箱号，只能扫描除目的地是备件库、仓储、退货组、逆向仓、售后仓、三方配送公司，特安标识的运单以外的其它运单!");
        }

        //BC-航空类型的箱号 只能集航空单
        if (TYPE_BCHK.equals(boxSubType) && !bchkBoxCheck(waybill)) {
            throw new JyBizException("BC-航空类型的箱号 只能集航空单!");
        }

        //BC-公路箱号只能集除航空单以外的订单
        if (TYPE_BCLY.equals(boxSubType) && !bclyBoxCheck(waybill)) {
            throw new JyBizException("BC-公路箱号只能集除航空单以外的订单!");
        }
    }

    /**
     * BC-公路箱号只能集除航空单以外的订单
     * @param waybill
     * @return
     */
    private boolean bclyBoxCheck(Waybill waybill) {
        Integer originalCrossType = getOriginalCrossType(waybill.getWaybillSign(), waybill.getSendPay());
        return !ORIGINAL_CROSS_TYPE_AIR.equals(originalCrossType);
    }

    /**
     * BC-航空类型的箱号 只能集航空单
     * @param waybill 运单对象
     */
    private boolean bchkBoxCheck(Waybill waybill) {
        Integer originalCrossType = getOriginalCrossType(waybill.getWaybillSign(), waybill.getSendPay());
        return ORIGINAL_CROSS_TYPE_AIR.equals(originalCrossType);
    }


    /**
     * BC开头的箱号，只能扫描除目的地是 备件库、仓储、退货组、逆向仓、售后仓、 三方配送公司，特安标识的运单以外的其它运单
     * @param siteInfo 入参参数描述
     * @param isTAWaybill 入参参数描述
     */
    private boolean bcBoxEndSiteTypeCheck(BaseStaffSiteOrgDto siteInfo, boolean isTAWaybill) {
        // BC开头的箱号，只能扫描除目的地是 备件库、仓储、逆向仓、售后仓、 三方配送公司，特安标识的运单以外的其它运单
        if (isReverseSite(siteInfo.getSiteType())
                || isThirdSite(siteInfo)
                || isTAWaybill) {
            return false;
        }
        return true;
    }

    /**
     * BX 开头的箱号，只能扫描目的地只能是 三方配送公司
     * @param siteInfo
     */
    public static boolean bxBoxEndSiteTypeCheck(BaseStaffSiteOrgDto siteInfo) {
        return isThirdSite(siteInfo);
    }

    /**
     * 校验是否是特安件
     * @param request
     */
    private boolean isTAWaybill(CollectPackageReq request) {
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        final List<WaybillVasDto> waybillVasList = waybillCommonService.getWaybillVasList(waybillCode);
        if(CollectionUtils.isEmpty(waybillVasList)){
            return false;
        }
        final com.jd.dms.java.utils.sdk.base.Result<Boolean> checkResult = waybillCommonService.checkWaybillVas(waybillCode, WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFETY, waybillVasList);
        if (checkResult.isSuccess()) {
            // 如果非特安件，不允许发货
            return checkResult.getData();
        }
        return false;
    }

    private void sealBoxCheck(CollectPackageReq request) {
        /*if (request.getSkipSealBoxCheck()){
            return;
        }
        if (JyBizTaskCollectPackageStatusEnum.SEALED.getCode().equals(request.getTaskStatus())){
            throw new JyBizException(CONFIRM_COLLECT_PACKAGE_WARNING, "该任务已封箱，是否继续装箱");
        }*/
    }

    public void reCollectCheck(CollectPackageReq request) {
        JyCollectPackageEntity query =new JyCollectPackageEntity();
        query.setPackageCode(request.getBarCode());
        query.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        List<JyCollectPackageEntity> packageEntityList =jyCollectPackageScanRecordService.listJyCollectPackageRecord(query);
        if (CollectionUtils.isNotEmpty(packageEntityList)){
            for (JyCollectPackageEntity entity : packageEntityList){
                if (ObjectHelper.isNotNull(entity.getBoxCode()) && entity.getBoxCode().equals(request.getBoxCode())){
                    throw new JyBizException("该包裹/箱已经在此箱号中,请勿重复集包！");
                }else if (ObjectHelper.isNotNull(entity.getCreateTime())) {
                    Date createTime = entity.getCreateTime();
                    if (System.currentTimeMillis() - createTime.getTime() <=  dmsConfigManager.getPropertyConfig().getReComboardTimeLimit() * 3600L * 1000L) {
                        throw new JyBizException("该包裹/箱子已经在"+entity.getBoxCode()+"中集包，如需重新集包，请在新版取消后再重新集包！");
                    }
                }
            }
        }
    }

    public void flowCheck(CollectPackageReq request) {
        JyBizTaskCollectPackageEntity collectPackageTask = jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (ObjectHelper.isEmpty(collectPackageTask)) {
            throw new JyBizException("集包任务不存在或者已经过期，请刷新界面！");
        }
        if (JyBizTaskCollectPackageStatusEnum.CANCEL.getCode().equals(collectPackageTask.getTaskStatus())) {
            throw new JyBizException("集包任务已作废-操作了批量取消集包！");
        }
        request.setTaskStatus(collectPackageTask.getTaskStatus());
        if (request.getForceCollectPackage()) {
            request.setEndSiteId(collectPackageTask.getEndSiteId());
        } else {
            Waybill waybill =waybillQueryManager.getWaybillByWayCode(WaybillUtil.getWaybillCode(request.getBarCode()));
            if (ObjectHelper.isEmpty(waybill)) {
                throw new JyBizException("未查询到运单数据!");
            }
            //获取包裹预分拣站点
            Integer yufenjian = getYufenjianByPackage(waybill);
            if (!checkYufenjianIfMatchDestination(yufenjian,request,collectPackageTask)){ //如果预分拣站点不匹配箱号目的地，再去判断末级分拣
                //获取包裹的末级分拣中心
                Integer lastDmsId = getLastDmsByPackage(waybill,collectPackageTask);
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
            }
        }
        if (ObjectHelper.isNotNull(request.getEndSiteId())) {
            BaseStaffSiteOrgDto staffSiteOrgDto = baseService.getSiteBySiteID(request.getEndSiteId().intValue());
            if (ObjectHelper.isNotNull(staffSiteOrgDto) && ObjectHelper.isNotNull(staffSiteOrgDto.getSiteName())) {
                request.setEndSiteName(staffSiteOrgDto.getSiteName());
            }
        }
    }

    private boolean checkYufenjianIfMatchDestination(Integer yufenjian, CollectPackageReq request, JyBizTaskCollectPackageEntity collectPackageTask) {
        if (BusinessUtil.isReverse(request.getBusinessType()) && MixBoxTypeEnum.MIX_DISABLE.getCode().equals(collectPackageTask.getMixBoxType())
                && !yufenjian.equals(collectPackageTask.getEndSiteId().intValue())){
            throw new JyBizException("逆向退仓/备件库：包裹预分拣站点与箱号目的地不一致，禁止集包！");
        }
        //预分拣==箱号目的地
        if (yufenjian.equals(collectPackageTask.getEndSiteId().intValue())){
            request.setEndSiteId(collectPackageTask.getEndSiteId());
            return true;
        }
        //判断是否存在大小站关系- 看看预分拣-归属的大站是否等于 箱号目的地
        final Integer parentSiteId = baseService.getMappingSite(yufenjian);
        if (ObjectHelper.isNotNull(parentSiteId) && parentSiteId.equals(collectPackageTask.getEndSiteId().intValue())) {
            request.setEndSiteId(collectPackageTask.getEndSiteId());
            return true;
        }
        return false;
    }

    private Integer getYufenjianByPackage(Waybill waybill) {
        if (ObjectHelper.isEmpty(waybill.getOldSiteId())) {
            throw new JyBizException("运单对应的预分拣站点为空!");
        }
        return waybill.getOldSiteId();
    }

    private Integer getLastDmsByPackage(Waybill waybill,JyBizTaskCollectPackageEntity task) {
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService.getSiteBySiteID(waybill.getOldSiteId());
        if(ObjectHelper.isEmpty(baseStaffSiteOrgDto)){
            log.info("jy getLastDmsByPackage：{}",JsonHelper.toJson(baseStaffSiteOrgDto));
            throw new JyBizException("未获取到运单对应预分拣站点信息!");
        }
        //判断终点是逆向站点
        if (isReverseSite(baseStaffSiteOrgDto.getSiteType())){
            return task.getEndSiteId().intValue();
        }else {
            if(ObjectHelper.isEmpty(baseStaffSiteOrgDto.getDmsId())){
                log.info("jy getLastDmsByPackage：{},{}",waybill.getWaybillCode(),JsonHelper.toJson(baseStaffSiteOrgDto));
                throw new JyBizException("未获取到末级分拣中心信息!");
            }
            return baseStaffSiteOrgDto.getDmsId();
        }
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
            log.info("集包箱号:{},包裹号:{} 对应末级分拣:{},不在允许集包的流向:{}",request.getBoxCode(),request.getBarCode(),dmsId,JsonHelper.toJson(flowSiteList));
            checkIfPermitForceCollectPackage(request,collectPackageTask);
            throw new JyBizException("末级分拣中心不在允许集包的流向内，禁止集包！");
        }
    }

    //判断当前场地是否可以强制集包
    private void checkIfPermitForceCollectPackage(CollectPackageReq request, JyBizTaskCollectPackageEntity collectPackageTask) {
        List<String> siteList = dmsConfigManager.getPropertyConfig().getForceCollectPackageSiteList();
        if (CollectionUtils.isNotEmpty(siteList) && (siteList.contains(Constants.TOTAL_URL_INTERCEPTOR) || siteList.contains(String.valueOf(request.getCurrentOperate().getSiteCode())))) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService.getSiteBySiteID(collectPackageTask.getEndSiteId().intValue());
            if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())) {
                throw new JyBizException(FORCE_COLLECT_PACKAGE_WARNING, "末级分拣中心不在允许集包的流向内，是否强制集往【" + baseStaffSiteOrgDto.getSiteName() + "】？");
            }
        }
    }

    private List<Integer> queryMixBoxFlowListUnderTask(CollectPackageReq request) {
        List<JyBizTaskCollectPackageFlowEntity> flowEntityList =jyBizTaskCollectPackageFlowService.queryListByBizIds((Collections.singletonList(request.getBizId())));
        if (CollectionUtils.isNotEmpty(flowEntityList)){
            List<Integer> flowList = flowEntityList.stream().map(jyBizTaskCollectPackageFlowEntity -> jyBizTaskCollectPackageFlowEntity.getEndSiteId().intValue()).collect(Collectors.toList());
            if (ObjectHelper.isNotNull(request.getBoxReceiveId())){
                flowList.add(request.getBoxReceiveId().intValue());
            }
            return flowList;
        }
        return null;
    }

    /**
     * 查询混装的流向集合（查询混装的集包的流向集合）
     *
     * @return
     */
//    private List<Integer> queryMixBoxFlowList(CollectPackageReq req) {
//        CollectBoxFlowDirectionConf con = assembleCollectBoxFlowDirectionConf(req.getCurrentOperate().getSiteCode(),req.getBoxReceiveId().intValue(),null);
//        List<CollectBoxFlowDirectionConf> collectBoxFlowDirectionConfList = boxLimitConfigManager.listCollectBoxFlowDirection(con, Arrays.asList(COLLECT_CLAIM_MIX, COLLECT_CLAIM_SPECIFY_MIX));//TODO 替换成查询任务的流向集合
//        if (CollectionUtils.isEmpty(collectBoxFlowDirectionConfList)) {
//            throw new JyBizException("未查询到对应目的地的可混装的流向集合！");
//        }
//        List<Integer> endSiteIdList = new ArrayList<>();
//        for (CollectBoxFlowDirectionConf conf : collectBoxFlowDirectionConfList) {
//            endSiteIdList.add(conf.getEndSiteId());
//        }
//        return endSiteIdList;
//    }

    private CollectBoxFlowDirectionConf assembleCollectBoxFlowDirectionConf(Integer siteCode, JyBizTaskCollectPackageEntity task, String searchCondition) {
        CollectBoxFlowDirectionConf conf = new CollectBoxFlowDirectionConf();
        conf.setStartSiteId(siteCode);
        conf.setBoxReceiveId(task.getEndSiteId().intValue());
        conf.setFlowType(FlowDirectionTypeEnum.OUT_SITE.getCode());
        // 箱号和集包规则运输类型枚举值不同，需要转换一下，默认公路类型
        Integer transportType = TRANSPORT_TYPE_HIGHWAY;
        if (BOX_TRANSPORT_TYPE_AIR.equals(task.getTransportType())) {
            transportType = TRANSPORT_TYPE_AIR;
        }
        conf.setTransportType(transportType);
        if (!StringUtils.isEmpty(searchCondition)) {
            // 目前只支持按目的地id和目的地名称查询
            if (NumberHelper.isNumber(searchCondition)) {
                conf.setEndSiteId(Integer.valueOf(searchCondition));
            }else {
                conf.setEndSiteName(searchCondition);
            }

        }
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


    public void execInterceptorChain(CollectPackageReq request) {
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

    public PdaOperateRequest assemblePdaOperateRequest(CollectPackageReq request) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setBoxCode(request.getBoxCode());
        pdaOperateRequest.setBusinessType(request.getBusinessType());
        pdaOperateRequest.setIsGather(0);
        //TODO
        //pdaOperateRequest.setOperateType(request.getOperateType());
        pdaOperateRequest.setPackageCode(request.getBarCode());
        pdaOperateRequest.setReceiveSiteCode(request.getEndSiteId().intValue());
        final CurrentOperate currentOperate = request.getCurrentOperate();
        pdaOperateRequest.setCreateSiteCode(currentOperate.getSiteCode());
        pdaOperateRequest.setCreateSiteName(currentOperate.getSiteName());
        pdaOperateRequest.setOperateTime(DateUtil.format(currentOperate.getOperateTime(), DateUtil.FORMAT_DATE_TIME));
        pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
        pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
        pdaOperateRequest.setInterceptChainBitCode(dmsConfigManager.getPropertyConfig().getJyCollectPackageInterceptBitCode());
        pdaOperateRequest.setOperateNode(OperateNodeConstants.SORTING);
        pdaOperateRequest.setJyCollectPackageFlag(true);
        final OperatorData operatorData = currentOperate.getOperatorData();
        if (operatorData != null) {
            pdaOperateRequest.setWorkGridKey(operatorData.getWorkGridKey());
            pdaOperateRequest.setWorkStationGridKey(operatorData.getWorkStationGridKey());
            pdaOperateRequest.setPositionCode(operatorData.getPositionCode());
        }
        return pdaOperateRequest;
    }

    public void boxCheck(CollectPackageReq request) {
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
        request.setBoxType(box.getType());
        request.setBoxSubType(box.getBoxSubType());
        request.setBusinessType(DmsConstants.BUSSINESS_TYPE_POSITIVE);
        if (ObjectHelper.isNotNull(request.getBoxReceiveId())){
            BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseService.getSiteBySiteID(request.getBoxReceiveId().intValue());
            log.info("查询箱号:{} 目的地站点信息:{}",request.getBoxCode(),JsonHelper.toJson(baseStaffSiteOrgDto));
            if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && isReverseSite(baseStaffSiteOrgDto.getSiteType())){
                request.setBusinessType(DmsConstants.BUSSINESS_TYPE_REVERSE);
            }
        }
    }

    public void collectPackageBaseCheck(CollectPackageReq request) {
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCollectPackageServiceImpl.listCollectPackageTask", mState = {JProEnum.TP, JProEnum.FunctionError})
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
        if (CollectionUtils.isNotEmpty(request.getSupportBoxTypes())){
            query.setSupportBoxTypes(request.getSupportBoxTypes());
        }
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
        HashMap<String,CollectPackageFlowDto> taskMap = getTaskMap(taskList);
        HashMap<String, List<CollectPackageFlowDto>> flowMap = getFlowMapByTask(bizIds, taskMap);

        // 批量获取统计信息
        HashMap<String, List<CollectScanDto>> aggMap = getScanAgg(bizIds);

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

    private HashMap<String, CollectPackageFlowDto> getTaskMap(List<JyBizTaskCollectPackageEntity> taskList) {
        HashMap<String, CollectPackageFlowDto> taskMap = new HashMap<>();
        taskList.forEach(item -> {
            CollectPackageFlowDto dto = new CollectPackageFlowDto();
            dto.setEndSiteId(item.getEndSiteId());
            dto.setEndSiteName(item.getEndSiteName());
            taskMap.put(item.getBizId(),dto);
        });
        return taskMap;
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
            listTaskStatisticDto = getCollectPackageManger().listTaskStatistic(queryDto);
        }catch (Exception e) {
            log.info("查询统计数据异常：{}", JsonHelper.toJson(queryDto));
            return aggMap;
        }
        if (listTaskStatisticDto == null || CollectionUtils.isEmpty(listTaskStatisticDto.getStatisticsUnderTaskDtoList())) {
            log.info("未获取到统计数据：{}", JsonHelper.toJson(queryDto));
            return aggMap;
        }

        listTaskStatisticDto.getStatisticsUnderTaskDtoList().forEach(item -> aggMap.put(item.getBizId(), item.getExcepScanDtoList()));
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
     * @param taskMap
     * @return
     */
    private HashMap<String, List<CollectPackageFlowDto>> getFlowMapByTask(List<String> bizIds, HashMap<String, CollectPackageFlowDto> taskMap) {
        HashMap<String, List<CollectPackageFlowDto>> flowMap = new HashMap<>();
        HashMap<String,Long> endSiteMap = new HashMap<>();
        taskMap.forEach((key, value) -> endSiteMap.put(key, value.getEndSiteId()));
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
                    // 添加建箱目的地流向
                    CollectPackageFlowDto flowDto = taskMap.get(entity.getCollectPackageBizId());
                    flowDtoList.add(flowDto);
                    flowMap.put(entity.getCollectPackageBizId(), flowDtoList);
                }
                Long endSiteId = endSiteMap.get(entity.getCollectPackageBizId());
                if (Objects.equals(endSiteId, entity.getEndSiteId())) {
                    continue;
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
        if (CollectionUtils.isNotEmpty(request.getSupportBoxTypes())){
            query.setSupportBoxTypes(request.getSupportBoxTypes());
        }
        return query;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCollectPackageServiceImpl.queryTaskDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
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

        if (BusinessUtil.isBoxcode(request.getBarCode()) && !BusinessUtil.isLLBoxcode(request.getBarCode())) {
            // 查询箱子是否已经被放入LL箱子中
            BoxRelation boxRelation = getBoxRelation(task);
            InvokeResult<List<BoxRelation>> boxRelationRes = boxRelationService.queryBoxRelation(boxRelation);
            taskDto.setHasBoundBoxFlag(boxRelationRes != null && !CollectionUtils.isEmpty(boxRelationRes.getData()));
        }

        // 查询集包袋号
        taskDto.setMaterialCode(cycleBoxService.getBoxMaterialRelation(task.getBoxCode()));
        if (ObjectHelper.isNotNull(taskDto.getMaterialCode())){
            if (BusinessUtil.isPalletBoxCollectionBag(taskDto.getMaterialCode())){
                taskDto.setMaterialType(MaterialTypeEnum.M_PALLET_BOX.getCode());
            }else if (BusinessUtil.isTrolleyCollectionBag(taskDto.getMaterialCode())){
                taskDto.setMaterialType(MaterialTypeEnum.M_CAGE_CAR.getCode());
            }else {
                taskDto.setMaterialType(MaterialTypeEnum.M_CYCLE_BAG.getCode());
            }
        }

        // 计算统计数据
        calculateCollectStatistic(taskDto);

        // 流向信息
        HashMap<String, CollectPackageFlowDto> taskMap = getTaskMap(Collections.singletonList(task));
        HashMap<String, List<CollectPackageFlowDto>> flowInfo = getFlowMapByTask(Collections.singletonList(taskDto.getBizId()), taskMap);
        taskDto.setCollectPackageFlowDtoList(flowInfo.get(task.getBizId()));
        resp.setCollectPackageTaskDto(taskDto);
        return result;
    }

    public void calculateCollectStatistic(CollectPackageTaskDto taskDto) {
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
    }

    private static BoxRelation getBoxRelation(JyBizTaskCollectPackageEntity task) {
        BoxRelation boxRelation = new BoxRelation();
        boxRelation.setRelationBoxCode(task.getBoxCode());
        boxRelation.setCreateSiteCode(task.getStartSiteId());
        return boxRelation;
    }

    public JyBizTaskCollectPackageEntity getTaskDetailByReq(TaskDetailReq request) {
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCollectPackageServiceImpl.sealingBox", mState = {JProEnum.TP, JProEnum.FunctionError})
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
        // LL类型和BC类型 绑定集包袋校验
        if (BusinessHelper.isLLBoxType(request.getSealingBoxDtoList().get(0).getBoxCode().substring(0, 2))
                || BusinessHelper.isBCBoxType(request.getSealingBoxDtoList().get(0).getBoxCode().substring(0, 2))) {

            if (ObjectHelper.isNotNull(request.getCurrentOperate()) && ObjectHelper.isNotNull(request.getCurrentOperate().getSiteCode())){
                boolean needBindMaterialBag = funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(), request.getCurrentOperate().getSiteCode());
                if (needBindMaterialBag){
                    String materialRelation = cycleBoxService.getBoxMaterialRelation(request.getSealingBoxDtoList().get(0).getBoxCode());
                    if (StringUtils.isEmpty(materialRelation)) {
                        result.setCode(RESULT_THIRD_ERROR_CODE);
                        result.setMessage("该箱号未绑定集包袋，不允许封箱！");
                        return false;
                    }
                }
            }else {
                String materialRelation = cycleBoxService.getBoxMaterialRelation(request.getSealingBoxDtoList().get(0).getBoxCode());
                if (StringUtils.isEmpty(materialRelation)) {
                    result.setCode(RESULT_THIRD_ERROR_CODE);
                    result.setMessage("该箱号未绑定集包袋，不允许封箱！");
                    return false;
                }
            }
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
    public InvokeResult<BindCollectBagResp> bindCollectBag(BindCollectBagReq request) {
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
        BindCollectBagResp bindCollectBagResp = assembleBindCollectBagResp(request);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, bindCollectBagResp);
    }

    public BindCollectBagResp assembleBindCollectBagResp(BindCollectBagReq bindCollectBagReq) {
        BindCollectBagResp bindCollectBagResp =new BindCollectBagResp();
        if (BusinessUtil.isPalletBoxCollectionBag(bindCollectBagReq.getMaterialCode())){
            bindCollectBagResp.setMaterialType(MaterialTypeEnum.M_PALLET_BOX.getCode());
        }else if (BusinessUtil.isTrolleyCollectionBag(bindCollectBagReq.getMaterialCode())){
            bindCollectBagResp.setMaterialType(MaterialTypeEnum.M_CAGE_CAR.getCode());
        }else {
            bindCollectBagResp.setMaterialType(MaterialTypeEnum.M_CYCLE_BAG.getCode());
        }
        return bindCollectBagResp;
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

    public void checkBindCollectBagReq(BindCollectBagReq request) {
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
        if (JyBizTaskCollectPackageStatusEnum.TO_COLLECT.getCode().equals(task.getTaskStatus())){
            throw new JyBizException("空箱不允许取消！");
        }
        //判断箱子是否已发货
        if (boxService.checkBoxIsSent(request.getBoxCode(), request.getCurrentOperate().getSiteCode())) {
            throw new JyBizException("箱号已经发货，不允许取消集包！");
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
        com.jd.bluedragon.distribution.api.domain.OperatorData operatorData
                = BeanConverter.convertToOperatorData(request.getCurrentOperate());
        cancelCollectPackageDto.setOperatorData(operatorData);
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
        while (pageNo < 500) {
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

        //查询内嵌箱号
        Box boxQuery =new Box();
        boxQuery.setCode(request.getBoxCode());
        List<Box> boxList =boxService.listSonBoxesByParentBox(boxQuery);
        if (CollectionUtils.isNotEmpty(boxList)){
            BatchCancelCollectPackageMqDto msg = assembleBatchCancelCollectBoxMqDto(boxList, request);
            batchCancelCollectPackageProduce.sendOnFailPersistent(request.getBoxCode(),JsonHelper.toJson(msg));
            log.info("===================={} 成功完成批量取消集箱消息--取消箱子{}============================", request.getBoxCode(),JsonHelper.toJson(msg));
        }
    }

    private BatchCancelCollectPackageMqDto assembleBatchCancelCollectBoxMqDto(List<Box> boxList, CancelCollectPackageReq request) {
        BatchCancelCollectPackageMqDto batchCancelCollectPackageMqDto = new BatchCancelCollectPackageMqDto();
        batchCancelCollectPackageMqDto.setBizId(request.getBizId());
        batchCancelCollectPackageMqDto.setBoxCode(request.getBoxCode());
        batchCancelCollectPackageMqDto.setSiteCode(request.getCurrentOperate().getSiteCode());
        batchCancelCollectPackageMqDto.setSiteName(request.getCurrentOperate().getSiteName());
        batchCancelCollectPackageMqDto.setUpdateUserErp(request.getUser().getUserErp());
        batchCancelCollectPackageMqDto.setUpdateUserName(request.getUser().getUserName());
        batchCancelCollectPackageMqDto.setUpdateUserCode(request.getUser().getUserCode());
        List<String> packageList = boxList.stream().map(box -> box.getCode()).collect(Collectors.toList());
        batchCancelCollectPackageMqDto.setPackageCodeList(packageList);
        return batchCancelCollectPackageMqDto;
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
        com.jd.bluedragon.distribution.api.domain.OperatorData operatorData
                = BeanConverter.convertToOperatorData(request.getCurrentOperate());
        batchCancelCollectPackageMqDto.setOperatorData(operatorData);
        return batchCancelCollectPackageMqDto;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCollectPackageServiceImpl.searchPackageTask", mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCollectPackageServiceImpl.queryStatisticsUnderTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<StatisticsUnderTaskQueryResp> queryStatisticsUnderTask(StatisticsUnderTaskQueryReq request) {
        checkStatisticsUnderTaskQueryReq(request);

        //查询任务的扫描类型统计数据
        StatisticsUnderTaskQueryDto taskQueryDto =assembleStatisticsUnderTaskQueryDto(request);
        StatisticsUnderTaskDto taskStatistics =getCollectPackageManger().queryTaskStatistic(taskQueryDto);

        if (ObjectHelper.isNotNull(taskStatistics)){
            //查询任务流向的聚合统计数据
            StatisticsUnderTaskQueryDto taskFlowQueryDto = assembleStatisticsUnderTaskQueryDto(request);
            StatisticsUnderTaskDto taskFlowStatistic =getCollectPackageManger().queryTaskFlowStatistic(taskFlowQueryDto);
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
        getAndSetEndSiteName(statisticsUnderTaskQueryResp);
        return statisticsUnderTaskQueryResp;
    }

    private void getAndSetEndSiteName(StatisticsUnderTaskQueryResp statisticsUnderTaskQueryResp) {
        try {
            if (CollectionUtils.isNotEmpty(statisticsUnderTaskQueryResp.getCollectPackageFlowDtoList()) && statisticsUnderTaskQueryResp.getCollectPackageFlowDtoList().size() <10){
                for (CollectPackageFlowDto collectPackageFlowDto: statisticsUnderTaskQueryResp.getCollectPackageFlowDtoList()){
                    if (ObjectHelper.isNotNull(collectPackageFlowDto.getEndSiteId())){
                        BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseMajorManager.getBaseSiteBySiteId(collectPackageFlowDto.getEndSiteId().intValue());
                        if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())){
                            collectPackageFlowDto.setEndSiteName(baseStaffSiteOrgDto.getSiteName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("queryStatisticsUnderTask.getAndSetEndSiteName err",e);
        }
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCollectPackageServiceImpl.queryStatisticsUnderFlow", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<StatisticsUnderFlowQueryResp> queryStatisticsUnderFlow(StatisticsUnderFlowQueryReq request) {
        checkStatisticsUnderFlowQueryReq(request);
        StatisticsUnderFlowQueryReq statisticsUnderFlowQueryReq =assembleStatisticsUnderFlowQueryReq(request);
        StatisticsUnderFlowQueryResp statisticsUnderFlowQueryResp =getCollectPackageManger().listPackageUnderFlow(statisticsUnderFlowQueryReq);
        if (ObjectHelper.isNotNull(statisticsUnderFlowQueryResp)){
            return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,statisticsUnderFlowQueryResp);
        }
        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCollectPackageServiceImpl.querySiteMixFlowList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<MixFlowListResp> querySiteMixFlowList(MixFlowListReq request) {
        InvokeResult<MixFlowListResp> result = new InvokeResult<>();
        if (!checkMixFlowListReq(request, result)) {
            return result;
        }
        MixFlowListResp resp = new MixFlowListResp();
        List<CollectPackageFlowDto> flowDtoList = new ArrayList<>();
        resp.setCollectPackageFlowDtoList(flowDtoList);
        result.setData(resp);
        JyBizTaskCollectPackageEntity task = jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (task == null) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("未获取到包裹任务信息！");
            return result;
        }

        CollectBoxFlowDirectionConf con = assembleCollectBoxFlowDirectionConf(request.getCurrentOperate().getSiteCode(), task,request.getSearchCondition());
        List<CollectBoxFlowDirectionConf> flowList=
                boxLimitConfigManager.listCollectBoxFlowDirection(con, Arrays.asList(COLLECT_CLAIM_MIX, COLLECT_CLAIM_SPECIFY_MIX));
        if (!CollectionUtils.isEmpty(flowList)) {
            flowList.forEach(item-> {
                CollectPackageFlowDto flowDto = new CollectPackageFlowDto();
                flowDto.setEndSiteId(item.getEndSiteId().longValue());
                flowDto.setEndSiteName(item.getEndSiteName());
                flowDtoList.add(flowDto);
            });
        }
        return result;
    }

    private boolean checkMixFlowListReq(MixFlowListReq request, InvokeResult<MixFlowListResp> result) {
        if (request == null || request.getCurrentOperate() == null || request.getCurrentOperate().getSiteCode() <= 0) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("未获取到场地信息！");
            return false;
        }

        return true;
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyCollectPackageServiceImpl.updateTaskFlowList", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InvokeResult<UpdateMixFlowListResp> updateTaskFlowList(UpdateMixFlowListReq request) {
        InvokeResult<UpdateMixFlowListResp> result = new InvokeResult<>();
        if (!checkUpdateMixFlowListReq(request, result)) {
            return result;
        }

        String boxLockKey = String.format(Constants.JY_COLLECT_BOX_LOCK_PREFIX, request.getBoxCode());
        if (!jimDbLock.lock(boxLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
            throw new JyBizException("当前系统繁忙,请稍后再试！");
        }

        try{
            JyBizTaskCollectPackageEntity task = jyBizTaskCollectPackageService.findByBizId(request.getBizId());
            if (task == null) {
                result.setCode(RESULT_THIRD_ERROR_CODE);
                result.setMessage("未获取到包裹任务信息！");
                return result;
            }
            if (!JyBizTaskCollectPackageStatusEnum.TO_COLLECT.getCode().equals(task.getTaskStatus())) {
                result.setCode(RESULT_THIRD_ERROR_CODE);
                result.setMessage("该任务已集包，不能集包修改流向信息！");
                return result;
            }
            // 删除原集包任务流向信息
            List<JyBizTaskCollectPackageFlowEntity> oldFlowList = jyBizTaskCollectPackageFlowService.queryListByBizIds(Collections.singletonList(task.getBizId()));
            List<Long> ids = oldFlowList.stream().map(JyBizTaskCollectPackageFlowEntity::getId).collect(Collectors.toList());
            jyBizTaskCollectPackageFlowService.deleteByIds(converUpdateData(ids, request));

            // 保存当前流向信息
            List<JyBizTaskCollectPackageFlowEntity> newFlowList = request.getCollectPackageFlowDtoList()
                    .stream().map(item -> converJyBizTaskCollectPackageFlowEntity(item,task, request)).collect(Collectors.toList());
            jyBizTaskCollectPackageFlowService.batchInsert(newFlowList);
        }finally {
            jimDbLock.releaseLock(boxLockKey, request.getRequestId());
        }
        return result;
    }

    protected void execCollectBox(CollectPackageReq request, CollectPackageResp response) {
        BoxRelation boxRelation =assmbleBoxRelation(request);
        InvokeResult<Boolean> rs =boxRelationService.saveBoxRelationWithoutCheck(boxRelation);
        if (ObjectHelper.isNotNull(rs) && RESULT_SUCCESS_CODE != rs.getCode()){
            throw new JyBizException("集箱失败！");
        }
        JyBizTaskCollectPackageEntity collectPackageTask = jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (ObjectHelper.isNotNull(collectPackageTask)){
            checkIfNeedUpdateStatus(request,collectPackageTask);
        }
        saveJyCollectPackageScanRecord(request);
    }

    protected BoxRelation assmbleBoxRelation(CollectPackageReq request) {
        BoxRelation relation =new BoxRelation();
        relation.setBoxCode(request.getBoxCode());
        relation.setRelationBoxCode(request.getBarCode());
        relation.setCreateSiteCode(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        relation.setCreateUserErp(request.getUser().getUserErp());
        relation.setCreateUserName(request.getUser().getUserName());
        relation.setUpdateUserErp(request.getUser().getUserErp());
        relation.setUpdateUserName(request.getUser().getUserName());
        relation.setYn(Constants.YN_YES);
        relation.setSource(OperatorTypeEnum.DMS_CLIENT.getCode());

        Date now = new Date();
        relation.setCreateTime(now);
        relation.setUpdateTime(now);
        return relation;
    }


    private void collectBoxBizCheck(CollectPackageReq request) {
        //重复集箱校验
        reCollectCheck(request);
        //外层箱的已发货校验
        sendCheck(request);
        //内外箱子的流向一致性校验
        boxflowCheck(request);
        //外层箱子最大可装入数量校验
        countLimitCheck(request);
        //内层箱是否为空箱校验
        emptyBoxCheck(request);
    }

    private void boxflowCheck(CollectPackageReq request) {
        Box outBox = boxService.findBoxByCode(request.getBoxCode());
        Box innerBox = boxService.findBoxByCode(request.getBarCode());

        request.setEndSiteId(Long.valueOf(innerBox.getReceiveSiteCode()));
        request.setEndSiteName(innerBox.getReceiveSiteName());
        request.setBoxReceiveId(Long.valueOf(outBox.getReceiveSiteCode()));
        request.setBoxReceiveName(outBox.getReceiveSiteName());

        if (!dmsConfigManager.getPropertyConfig().getNeedCollectLoadingBoxflowCheck()){
            return;
        }
        if (ObjectHelper.isNotNull(outBox)  && ObjectHelper.isNotNull(innerBox)
                && !ObjectUtils.equals(outBox.getReceiveSiteCode(), innerBox.getReceiveSiteCode())) {
            throw new JyBizException("箱号目的地不一致");
        }
    }

    private void emptyBoxCheck(CollectPackageReq request) {
        Integer count =sortingService.getSumByBoxCode(request.getBarCode());
        if (ObjectHelper.isEmpty(count) || count <= 0){
            throw new JyBizException("该箱子"+ request.getBarCode()+"为空箱子！");
        }
    }

    private void countLimitCheck(CollectPackageReq request) {
        BoxRelation relation = new BoxRelation();
        relation.setBoxCode(request.getBoxCode());
        relation.setCreateSiteCode(Long.valueOf(request.getCurrentOperate().getSiteCode()));

        relation.setRelationBoxCode(request.getBarCode());
        relation.setCreateUserErp(request.getUser().getUserErp());
        relation.setCreateUserName(request.getUser().getUserName());
        relation.setUpdateUserErp(request.getUser().getUserErp());
        relation.setUpdateUserName(request.getUser().getUserName());
        relation.setYn(Constants.YN_YES);
        Date now =new Date();
        relation.setCreateTime(now);
        relation.setUpdateTime(now);
        int existRelations = boxRelationService.countByBoxCode(relation);
        if (dmsConfigManager.getPropertyConfig().getLLContainBoxNumberLimit() > 0
                && existRelations >= dmsConfigManager.getPropertyConfig().getLLContainBoxNumberLimit()) {
            throw new JyBizException("最大允许装箱"+dmsConfigManager.getPropertyConfig().getLLContainBoxNumberLimit()+"个");
        }
    }

    private void sendCheck(CollectPackageReq request) {
        if (boxService.checkBoxIsSent(request.getBoxCode(), request.getCurrentOperate().getSiteCode())) {
            throw new JyBizException("该箱号"+ request.getBoxCode()+"已发货！");
        }
    }

    private void collectBoxBaseCheck(CollectPackageReq request) {
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
            throw new JyBizException("参数错误：缺失扫描单号！");
        }
        if (!BusinessUtil.isBoxcode(request.getBarCode())) {
            throw new JyBizException("参数错误：请扫描箱号！");
        }
        String outerBoxType = request.getBoxCode().substring(0,2);
        String innerBoxType =request.getBarCode().substring(0,2);

        BoxTypeV2Enum outer =BoxTypeV2Enum.getFromCode(outerBoxType);
        if (ObjectHelper.isEmpty(outer) || ObjectHelper.isEmpty(outer.getSupportEmbeddedTypes())){
            throw new JyBizException("参数错误:"+outerBoxType+"箱号不支持内嵌箱号");
        }
        if (!outer.getSupportEmbeddedTypes().contains(innerBoxType)){
            throw new JyBizException("参数错误:"+outerBoxType+"箱号不支持内嵌"+innerBoxType+"类型的箱");
        }
    }

    private JyBizTaskCollectPackageQuery converUpdateData(List<Long> ids, UpdateMixFlowListReq request) {
        JyBizTaskCollectPackageQuery query = new JyBizTaskCollectPackageQuery();
        query.setIds(ids);
        query.setUpdateUserErp(request.getUser().getUserErp());
        query.setUpdateUserName(request.getUser().getUserName());
        return query;
    }

    private JyBizTaskCollectPackageFlowEntity converJyBizTaskCollectPackageFlowEntity(CollectPackageFlowDto item, JyBizTaskCollectPackageEntity task, UpdateMixFlowListReq request) {
        JyBizTaskCollectPackageFlowEntity entity = new JyBizTaskCollectPackageFlowEntity();
        entity.setBoxCode(task.getBoxCode());
        entity.setCreateTime(new Date());
        entity.setCreateUserErp(request.getUser().getUserErp());
        entity.setStartSiteId(task.getStartSiteId());
        entity.setStartSiteName(task.getStartSiteName());
        entity.setCreateUserName(request.getUser().getUserName());
        entity.setCollectPackageBizId(task.getBizId());
        entity.setEndSiteId(item.getEndSiteId().longValue());
        entity.setEndSiteName(item.getEndSiteName());
        entity.setUpdateTime(new Date());
        entity.setUpdateUserErp(request.getUser().getUserErp());
        entity.setUpdateUserName(request.getUser().getUserName());
        entity.setYn(Boolean.TRUE);
        return entity;
    }

    private boolean checkUpdateMixFlowListReq(UpdateMixFlowListReq request, InvokeResult<UpdateMixFlowListResp> result) {
        if (StringUtils.isEmpty(request.getBizId())) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("未获取到集包任务BizId！");
            return false;
        }

        if (CollectionUtils.isEmpty(request.getCollectPackageFlowDtoList())) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("未获取到集包任务流向信息！");
            return false;
        }

        if (request.getUser() == null) {
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("未获取到操作人信息！");
            return false;
        }
        return true;
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

    public boolean checkSearchPackageTaskReq(SearchPackageTaskReq request, InvokeResult<CollectPackageTaskResp> result) {
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
        if (CollectionUtils.isNotEmpty(request.getSupportBoxTypes())){
            query.setSupportBoxTypes(request.getSupportBoxTypes());
        }

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

    @Override
    public InvokeResult<CancelCollectPackageResp> cancelCollectBox(CancelCollectPackageReq request) {
        checkCancelCollectBox(request);
        checkIfAllowCancelCollectBox(request);
        CancelCollectPackageResp response = new CancelCollectPackageResp();
        execCancelCollectBox(request, response);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, response);
    }

    public void execCancelCollectBox(CancelCollectPackageReq request, CancelCollectPackageResp response) {
        BoxRelation boxRelation =assmbleReleaseBoxRelation(request);
        InvokeResult<Boolean> invokeResult =boxRelationService.releaseBoxRelation(boxRelation);
        log.info("取消集装 outboxp:{},innerBox:{}",request.getBoxCode(),request.getBarCode());
        if (ObjectHelper.isNotNull(invokeResult) && !invokeResult.codeSuccess()){
            throw new JyBizException("取消装笼失败！");
        }
        CancelCollectPackageDto dto =assembleCancelCollectPackageDto(request);
        jyBizTaskCollectPackageService.deleteJyCollectPackageRecord(dto);
    }

    private BoxRelation assmbleReleaseBoxRelation(CancelCollectPackageReq request) {
        BoxRelation relation =new BoxRelation();
        relation.setRelationBoxCode(request.getBarCode());
        relation.setCreateSiteCode(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        relation.setCreateUserErp(request.getUser().getUserErp());
        relation.setCreateUserName(request.getUser().getUserName());
        relation.setUpdateUserErp(request.getUser().getUserErp());
        relation.setUpdateUserName(request.getUser().getUserName());
        relation.setYn(Constants.YN_NO);

        Date now = new Date();
        relation.setCreateTime(now);
        relation.setUpdateTime(now);
        return relation;
    }

    private void checkIfAllowCancelCollectBox(CancelCollectPackageReq request) {
        //校验一下箱号是否已经发货

    }

    private void checkCancelCollectBox(CancelCollectPackageReq request) {
        if (ObjectHelper.isEmpty(request.getBarCode())) {
            throw new JyBizException("参数错误：缺失取消箱号！");
        }
    }

    public static void main(String[] args) {
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = new BaseStaffSiteOrgDto();
        baseStaffSiteOrgDto.setSiteType(901);
        if (isReverseSite(baseStaffSiteOrgDto.getSiteType())){
            System.out.println(1);
        }
    }


}
