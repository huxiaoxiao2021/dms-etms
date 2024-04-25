package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.request.OperatorData;
import com.jd.bluedragon.common.dto.collectpackage.request.BindCollectBagReq;
import com.jd.bluedragon.common.dto.collectpackage.request.CollectPackageReq;
import com.jd.bluedragon.common.dto.collectpackage.request.SearchPackageTaskReq;
import com.jd.bluedragon.common.dto.collectpackage.request.TaskDetailReq;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageTaskDto;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageTaskResp;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectStatisticDto;
import com.jd.bluedragon.core.jsf.collectpackage.CollectPackageManger;
import com.jd.bluedragon.core.jsf.collectpackage.dto.StatisticsUnderTaskDto;
import com.jd.bluedragon.core.jsf.collectpackage.dto.StatisticsUnderTaskQueryDto;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageResp;
import com.jd.bluedragon.common.dto.sorting.request.PackSortTaskBody;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.constants.BoxTypeV2Enum;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskCollectPackageStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.sorting.domain.SortingBizSourceEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_THIRD_ERROR_CODE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;
import static com.jd.bluedragon.distribution.task.domain.Task.TASK_TYPE_SORTING;

@Service("jyCollectLoadingService")
@Slf4j
public class JyCollectLoadingServiceImpl extends JyCollectPackageServiceImpl{

    @Autowired
    private JyBizTaskCollectPackageService jyBizTaskCollectPackageService;
    @Autowired
    private BaseService baseService;

    @Autowired
    @Qualifier("collectLoadingManger")
    private CollectPackageManger collectPackageManger;

    @Override
    public CollectPackageManger getCollectPackageManger() {
        return this.collectPackageManger;
    }

    @Override
    public void collectPackageBizCheck(CollectPackageReq request) {
        //重复集包校验
        reCollectCheck(request);
        //校验箱号：是否存在 +是否已打印+状态合法性+是否已经发货
        boxCheck(request);
        //流向校验
        flowCheck(request);
        //sorting拦截器链
        execInterceptorChain(request);
    }

    @Override
    public void flowCheck(CollectPackageReq request) {
        JyBizTaskCollectPackageEntity collectPackageTask = jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (ObjectHelper.isEmpty(collectPackageTask)) {
            throw new JyBizException("集包任务不存在或者已经过期，请刷新界面！");
        }
        if (JyBizTaskCollectPackageStatusEnum.CANCEL.getCode().equals(collectPackageTask.getTaskStatus())) {
            throw new JyBizException("集包任务已作废-操作了批量取消集包！");
        }
        request.setTaskStatus(collectPackageTask.getTaskStatus());
        request.setEndSiteId(collectPackageTask.getEndSiteId());
        if (ObjectHelper.isNotNull(request.getEndSiteId())) {
            BaseStaffSiteOrgDto staffSiteOrgDto = baseService.getSiteBySiteID(request.getEndSiteId().intValue());
            if (ObjectHelper.isNotNull(staffSiteOrgDto) && ObjectHelper.isNotNull(staffSiteOrgDto.getSiteName())) {
                request.setEndSiteName(staffSiteOrgDto.getSiteName());
            }
        }
    }

    @Override
    public PdaOperateRequest assemblePdaOperateRequest(CollectPackageReq request) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setBoxCode(request.getBoxCode());
        pdaOperateRequest.setBusinessType(request.getBusinessType());
        pdaOperateRequest.setIsGather(0);
        pdaOperateRequest.setOperateType(1);
        pdaOperateRequest.setPackageCode(request.getBarCode());
        pdaOperateRequest.setReceiveSiteCode(request.getBoxReceiveId().intValue());
        pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        pdaOperateRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
        pdaOperateRequest.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(), DateUtil.FORMAT_DATE_TIME));
        pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
        pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
        pdaOperateRequest.setJyCollectPackageFlag(false);
        if (ObjectHelper.isNotNull(request.getCurrentOperate()) && ObjectHelper.isNotNull(request.getCurrentOperate().getOperatorData())) {
            OperatorData operatorData = request.getCurrentOperate().getOperatorData();
            pdaOperateRequest.setWorkGridKey(operatorData.getWorkGridKey());
            pdaOperateRequest.setWorkStationGridKey(operatorData.getWorkStationGridKey());
            pdaOperateRequest.setPositionCode(operatorData.getPositionCode());
        }
        return pdaOperateRequest;
    }

    @Override
    public InvokeResult<CollectPackageResp> collectBoxForMachine(CollectPackageReq request) {
        //基础校验
        collectBoxMachineCheck(request);
        //执行集装
        CollectPackageResp response = new CollectPackageResp();
        execCollectBoxForMachine(request, response);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, response);
    }
    protected void execCollectBoxForMachine(CollectPackageReq request, CollectPackageResp response) {
        BoxRelation boxRelation =assmbleBoxRelation(request);
        boxRelation.setSource(OperatorTypeEnum.AUTO_MACHINE.getCode());
        boxRelationService.saveBoxRelationWithoutCheck(boxRelation);
    }
    private void collectBoxMachineCheck(CollectPackageReq request) {

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

    private void collectPackageMachineBaseCheck(CollectPackageReq request) {

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
    public InvokeResult<CollectPackageResp> collectPackageForMachine(CollectPackageReq request) {
        //基础校验
        collectPackageMachineBaseCheck(request);
        //执行集包
        CollectPackageResp response = new CollectPackageResp();
        execCollectPackageForMachine(request, response);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, response);
    }

    protected void execCollectPackageForMachine(CollectPackageReq request, CollectPackageResp response) {
        //执行集包
        TaskRequest taskRequest = assembleTaskRequestForMachine(request);
        taskService.add(taskRequest);
    }

    protected TaskRequest assembleTaskRequestForMachine(CollectPackageReq request) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBoxCode(request.getBoxCode());
        taskRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskRequest.setReceiveSiteCode(request.getBoxReceiveId().intValue());
        taskRequest.setType(TASK_TYPE_SORTING);
        taskRequest.setKeyword1(String.valueOf(request.getCurrentOperate().getSiteCode()));
        taskRequest.setKeyword2(request.getBoxCode());
        taskRequest.setBusinessType(10);
        String body = assembleCollectDataBodyForMachine(request);
        taskRequest.setBody(body);
        return taskRequest;
    }

    private String assembleCollectDataBodyForMachine(CollectPackageReq request) {
        PackSortTaskBody taskBody = new PackSortTaskBody();
        taskBody.setBoxCode(request.getBoxCode());
        taskBody.setBusinessType(10);
        taskBody.setFeatureType(0);
        taskBody.setIsCancel(0);
        taskBody.setIsLoss(0);
        taskBody.setSiteCode(request.getCurrentOperate().getSiteCode());
        taskBody.setSiteName(request.getCurrentOperate().getSiteName());
        taskBody.setPackageCode(request.getBarCode());
        taskBody.setOperateTime(DateHelper.formatDateTime(request.getCurrentOperate().getOperateTime()));
        taskBody.setReceiveSiteCode(request.getBoxReceiveId().intValue());
        taskBody.setReceiveSiteName(request.getBoxReceiveName());
        taskBody.setUserCode(request.getUser().getUserCode());
        taskBody.setUserName(request.getUser().getUserName());
        taskBody.setBizSource(SortingBizSourceEnum.AUTOMATIC_SORTING_MACHINE_SORTING.getCode());
        List<PackSortTaskBody> bodyList = new ArrayList<>();
        bodyList.add(taskBody);
        return JSON.toJSONString(bodyList);
    }

    @Override
    public boolean checkSearchPackageTaskReq(SearchPackageTaskReq request, InvokeResult<CollectPackageTaskResp> result) {
        if (BusinessHelper.isBoxcode(request.getBarCode()) && !BusinessUtil.isLLBoxcode(request.getBarCode())){
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("箱号只支持扫描LL箱号！");
            return false;
        }
        return super.checkSearchPackageTaskReq(request, result);
    }

    @Override
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
        if (!BusinessUtil.isLLBoxBindingCollectionBag(request.getMaterialCode())) {
            throw new JyBizException("参数错误：非法的集包袋号！");
        }
    }

    @Override
    public void collectPackageBaseCheck(CollectPackageReq request) {
        if (!ObjectHelper.isNotNull(request.getBizId())) {
            throw new JyBizException("参数错误：缺失任务bizId！");
        }
        if (!ObjectHelper.isNotNull(request.getBoxCode())) {
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isLLBoxcode(request.getBoxCode())){
            throw new JyBizException("参数错误：集装岗只支持操作LL类型箱号！");
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
    public JyBizTaskCollectPackageEntity getTaskDetailByReq(TaskDetailReq request) {
        if (BusinessUtil.isBoxcode(request.getBarCode()) && BusinessUtil.isLLBoxcode(request.getBarCode())) {
            // 如果是LL箱号
            return jyBizTaskCollectPackageService.findByBoxCode(request.getBarCode());
        }else if (WaybillUtil.isPackageCode(request.getBarCode()) ||
                (BusinessUtil.isBoxcode(request.getBarCode()) && !BusinessUtil.isLLBoxcode(request.getBarCode()))) {
            JyCollectPackageEntity query = new JyCollectPackageEntity();
            query.setPackageCode(request.getBarCode());
            query.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
            JyCollectPackageEntity collectPackage = jyCollectPackageScanRecordService.queryJyCollectPackageRecord(query);
            if (collectPackage == null || StringUtils.isEmpty(collectPackage.getBizId())) {
                return null;
            }
            return jyBizTaskCollectPackageService.findByBizId(collectPackage.getBizId());
        }else if (ObjectHelper.isNotNull(request.getBizId())){
            return jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        }else {
            throw new JyBizException("暂不支持改类型的任务检索方式!");
        }
    }


    @Override
    public void calculateCollectStatistic(CollectPackageTaskDto taskDto) {
        StatisticsUnderTaskQueryDto queryDto =new StatisticsUnderTaskQueryDto();
        queryDto.setBizId(taskDto.getBizId());
        StatisticsUnderTaskDto statisticsUnderTaskDto = getCollectPackageManger().queryTaskStatistic(queryDto);
        if (ObjectHelper.isNotNull(statisticsUnderTaskDto) && ObjectHelper.isNotNull(statisticsUnderTaskDto.getCollectStatisticDto())){
            CollectStatisticDto collectStatisticDto = statisticsUnderTaskDto.getCollectStatisticDto();
            taskDto.setScanCount(collectStatisticDto.getTotalScanCount());
            taskDto.setPackageScanCount(collectStatisticDto.getPackageScanCount());
            taskDto.setBoxScanCount(collectStatisticDto.getBoxScanCount());
            taskDto.setInterceptCount(collectStatisticDto.getTotalInterceptCount());
            taskDto.setPackageInterceptCount(collectStatisticDto.getPackageInterceptCount());
            taskDto.setBoxInterceptCount(collectStatisticDto.getBoxInterceptCount());
        }
    }
}
