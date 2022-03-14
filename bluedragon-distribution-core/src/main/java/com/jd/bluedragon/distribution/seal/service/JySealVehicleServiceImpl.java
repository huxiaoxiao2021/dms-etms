package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.ToSealCarInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.UnSealCarData;
import com.jd.bluedragon.core.base.IJySealVehicleManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ValueNameEnumUtils;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.enums.seal.LineTypeEnum;
import com.jdl.jy.realtime.enums.seal.VehicleStatusEnum;
import com.jdl.jy.realtime.model.es.seal.SealCarMonitor;
import com.jdl.jy.realtime.model.query.seal.SealVehicleTaskQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * @ClassName JySealVehicleServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/3/11 14:30
 **/
@Service
public class JySealVehicleServiceImpl implements IJySealVehicleService {

    private static final Logger log = LoggerFactory.getLogger(JySealVehicleServiceImpl.class);

    /**
     * 查询几天内的带解任务（负数）
     * */
    @Value("${newSealVehicleResource.rollBackDay:-7}")
    private int rollBackDay;

    private static final int STATUS = 10;

    @Autowired
    @Qualifier("jySealVehicleManager")
    private IJySealVehicleManager jySealVehicleManager;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Override
    public InvokeResult<SealVehicleTaskResponse> fetchSealTask(SealVehicleTaskRequest request) {
        InvokeResult<SealVehicleTaskResponse> result = new InvokeResult<>();

        SealVehicleTaskQuery query = assembleCommandCondition(request);

        if (isSearch(request)) {
            if (BusinessUtil.isSealBoxNo(request.getBarCode())) {
                List<String> sealCarCodeList = getSealCarCodeFromVos(result, request);
                if (!result.codeSuccess()) {
                    return result;
                }

                query.setSealCarCode(sealCarCodeList);
            }
            else {
                query.setVehicleNumberLastFour(request.getBarCode());
            }
        }
        else if (isRefresh(request)) {
            query.setVehicleStatus(request.getVehicleStatus());
            query.setLineType(request.getLineType());
            // 查询最近6小时的待解封车任务
            query.setQueryTimeStart(DateHelper.newTimeRangeHoursAgo(new Date(), 6));
        }

        Pager<SealVehicleTaskQuery> pager = new Pager<>();
        pager.setPageNo(request.getPageNumber());
        pager.setPageSize(request.getPageSize());
        pager.setSearchVo(query);

        com.jdl.jy.realtime.model.vo.seal.SealVehicleTaskResponse serviceResult = jySealVehicleManager.querySealTask(pager);
        if (serviceResult == null) {
            result.error("服务器异常，请联系分拣小秘");
            return result;
        }

        SealVehicleTaskResponse taskResponse = new SealVehicleTaskResponse();
        try {
            StopWatch stopWatch = new StopWatch("CV-SealVehicleTaskResponse");
            stopWatch.start();
            BeanCopyUtil.copy(serviceResult, taskResponse);
            stopWatch.stop();
            log.info(stopWatch.prettyPrint());

            result.setData(taskResponse);
        }
        catch (Exception e) {
            log.error("copy SealVehicleTaskResponse error.", e);
            result.error("服务器异常，请联系分拣小秘");
        }

        return result;
    }

    private boolean isRefresh(SealVehicleTaskRequest request) {
        return SealVehicleTaskQuery.FETCH_TYPE_REFRESH.equals(request.getFetchType());
    }

    private boolean isSearch(SealVehicleTaskRequest request) {
        return SealVehicleTaskQuery.FETCH_TYPE_SEARCH.equals(request.getFetchType());
    }

    private SealVehicleTaskQuery assembleCommandCondition(SealVehicleTaskRequest request) {
        SealVehicleTaskQuery query = new SealVehicleTaskQuery();

        query.setEndSiteId(request.getEndSiteCode());
        query.setFetchType(request.getFetchType());

        return query;
    }

    @Override
    public InvokeResult<SealTaskInfo> taskInfo(SealTaskInfoRequest request) {

        InvokeResult<SealTaskInfo> response = new InvokeResult<>();

        SealCarMonitor sealCarMonitor = jySealVehicleManager.querySealTaskInfo(request);
        if (null == sealCarMonitor) {
            response.error("查询任务明细异常，请联系分拣小秘！");
            return response;
        }

        response.setData(makeTaskInfo(sealCarMonitor));
        return response;
    }

    private InvokeResult<SealVehicleTaskResponse> sealTaskFallback(SealVehicleTaskRequest request) {
        InvokeResult<SealVehicleTaskResponse> invokeResult = new InvokeResult<>();
        try {
            List<SealCarDto> sealCarDtoList = getSealTaskFromVos(invokeResult, request);
            if (!invokeResult.codeSuccess()) {
                return invokeResult;
            }

            List<SealCarDto> filterList = new ArrayList<>();
            if (isSearch(request)) {
                for (SealCarDto sealCar : sealCarDtoList) {
                    if (filterBySealCode(request, sealCar) || filterByVehicleNumber(request, sealCar)) {
                        filterList.add(sealCar);
                    }
                }
            }
            else if (isRefresh(request)) {
                filterList = sealCarDtoList;
            }

            invokeResult.setData(sealCarDtoToResponse(filterList));
        }
        catch (Exception e) {
            log.error("从运输拉取解封车任务异常. {}", JsonHelper.toJson(request), e);
            invokeResult.error("服务器异常，请联系分拣小秘");
        }

        return invokeResult;
    }

    /**
     * 根据封签号获得封车编码
     * @param result
     * @param request
     * @return
     */
    private List<String> getSealCarCodeFromVos(InvokeResult<SealVehicleTaskResponse> result, SealVehicleTaskRequest request) {
        List<SealCarDto> sealCarDtoList = getSealTaskFromVos(result, request);
        if (!result.codeSuccess()) {
            return null;
        }
        List<SealCarDto> filterList = new ArrayList<>();
        for (SealCarDto sealCar : sealCarDtoList) {
            if (filterBySealCode(request, sealCar)) {
                filterList.add(sealCar);
            }
        }
        if (CollectionUtils.isEmpty(filterList)) {
            result.error("该封签号没有待解封车任务");
            return null;
        }

        Set<String> sealCarCodeSet = new HashSet<>();
        for (SealCarDto sealCarDto : filterList) {
            sealCarCodeSet.add(sealCarDto.getSealCarCode());
        }

        return new ArrayList<>(sealCarCodeSet);
    }

    /**
     * 根据查询条件从运输获得封车任务
     * @param invokeResult
     * @param request
     * @return
     */
    private List<SealCarDto> getSealTaskFromVos(InvokeResult<SealVehicleTaskResponse> invokeResult, SealVehicleTaskRequest request) {
        List<SealCarDto> sealCarDtoList = new ArrayList<>();

        SealCarDto sealCarDto = getSealCarDto(request);
        PageDto<SealCarDto> pageDto = getSealCarDtoPageDto(request);

        try {
            CommonDto<PageDto<SealCarDto>> returnCommonDto = newSealVehicleService.findSealInfo(sealCarDto, pageDto);
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    if (returnCommonDto.getData() != null && CollectionUtils.isNotEmpty(returnCommonDto.getData().getResult())) {
                        return returnCommonDto.getData().getResult();
                    }
                    else {
                        invokeResult.setCode(JdResponse.CODE_OK_NULL);
                        invokeResult.setMessage(JdResponse.MESSAGE_OK_NULL);
                    }
                }
                else {
                    invokeResult.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    invokeResult.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                }
            }
        }
        catch (Exception e) {
            log.error("从运输拉取解封车任务异常. {}", JsonHelper.toJson(request), e);
            invokeResult.error("服务器异常，请联系分拣小秘！");
        }

        return sealCarDtoList;
    }

    /**
     * 按搜索条件过滤封车任务
     * @param request
     * @param sealCar
     * @return
     */
    private boolean filterBySealCode(SealVehicleTaskRequest request, SealCarDto sealCar) {
        if (CollectionUtils.isNotEmpty(sealCar.getSealCodes()) && sealCar.getSealCodes().contains(request.getBarCode())) {
            return true;
        }

        return false;
    }

    private boolean filterByVehicleNumber(SealVehicleTaskRequest request, SealCarDto sealCar) {
        if (!BusinessUtil.isSealBoxNo(request.getBarCode())) {
            if (StringUtils.isNotBlank(sealCar.getVehicleNumber())
                    && sealCar.getVehicleNumber().length() > 4 && request.getBarCode().equals(StringUtils.substring(sealCar.getVehicleNumber(), - 4))) {
                return true;
            }
        }

        return false;
    }

    private SealVehicleTaskResponse sealCarDtoToResponse(List<SealCarDto> sealCarDtos) {
        if (CollectionUtils.isEmpty(sealCarDtos)) {
            return null;
        }

        SealVehicleTaskResponse response = new SealVehicleTaskResponse();

        List<ToSealCarInfo> sealCarInfList = new ArrayList<>();
        for (SealCarDto sealCarDto : sealCarDtos) {
            ToSealCarInfo sealCarInfo = new ToSealCarInfo();
            sealCarInfo.setActualArriveTime(null);
            sealCarInfo.setSealCarCode(sealCarDto.getSealCarCode());
            sealCarInfo.setVehicleNumber(sealCarDto.getVehicleNumber());
            sealCarInfo.setLineType(null);
            sealCarInfo.setLineTypeName(null);
            sealCarInfo.setSpotCheck(null);
            sealCarInfList.add(sealCarInfo);
        }

        UnSealCarData<ToSealCarInfo> toSealCarData = new UnSealCarData<>();
        toSealCarData.setData(sealCarInfList);
        response.setToSealCarData(toSealCarData);

        return response;
    }

    private PageDto<SealCarDto> getSealCarDtoPageDto(SealVehicleTaskRequest request) {
        PageDto<SealCarDto> pageDto = new PageDto<>();
        pageDto.setCurrentPage(request.getPageNumber());
        pageDto.setPageSize(request.getPageSize());
        return pageDto;
    }

    private SealCarDto getSealCarDto(SealVehicleTaskRequest request) {
        SealCarDto sealCarDto = new SealCarDto();
        // 查询封车任务
        sealCarDto.setStatus(STATUS);
        // 查询15天内的待解任务
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, rollBackDay);
        sealCarDto.setSealCarTimeBegin(c.getTime());
        sealCarDto.setEndSiteId(request.getEndSiteCode());
        return sealCarDto;
    }

    private SealTaskInfo makeTaskInfo(SealCarMonitor sealCarMonitor) {
        SealTaskInfo taskInfo = new SealTaskInfo();
        taskInfo.setVehicleNumber(sealCarMonitor.getVehicleNumber());
        taskInfo.setCarModel(sealCarMonitor.getCarModel());
        taskInfo.setVehicleStatus(sealCarMonitor.getVehicleStatus());
        taskInfo.setVehicleStatusName(ValueNameEnumUtils.getNameByValue(VehicleStatusEnum.class, taskInfo.getVehicleStatus()));
        taskInfo.setStartSiteId(sealCarMonitor.getStartSiteId());
        taskInfo.setStartSiteName(sealCarMonitor.getStartSiteName());
        taskInfo.setLineType(sealCarMonitor.getLineType());
        taskInfo.setLineTypeName(ValueNameEnumUtils.getNameByValue(LineTypeEnum.class, taskInfo.getLineType()));
        taskInfo.setTransportCode(sealCarMonitor.getTransportCode());
        taskInfo.setTransBookCode(sealCarMonitor.getTransBookCode());
        if (StringUtils.isNotBlank(sealCarMonitor.getSendCodeList())) {
            try {
                taskInfo.setBatchCodeList(Arrays.asList(sealCarMonitor.getSendCodeList().split(Constants.SEPARATOR_COMMA)));
            }
            catch (Exception e) {
                log.error("transfer sendCodeList from es error.", e);
            }
        }
        taskInfo.setTotalCount(sealCarMonitor.getTotalCount());
        taskInfo.setUnloadCount(sealCarMonitor.getUnloadCount());
        taskInfo.setSealCarTime(sealCarMonitor.getSealCarTime());
        taskInfo.setPredictionArriveTime(sealCarMonitor.getPredictionArriveTime());
        taskInfo.setActualArriveTime(sealCarMonitor.getActualArriveTime());

        return taskInfo;
    }
}
