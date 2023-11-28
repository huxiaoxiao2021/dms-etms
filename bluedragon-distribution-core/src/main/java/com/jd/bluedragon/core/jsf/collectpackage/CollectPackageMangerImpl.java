package com.jd.bluedragon.core.jsf.collectpackage;

import com.jd.bluedragon.common.dto.collectpackage.request.StatisticsUnderFlowQueryReq;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageDto;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageFlowDto;
import com.jd.bluedragon.common.dto.collectpackage.response.StatisticsUnderFlowQueryResp;
import com.jd.bluedragon.core.jsf.collectpackage.dto.*;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CollectScanDto;
import com.jd.bluedragon.distribution.jy.enums.CollectPackageExcepScanEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.udata.query.api.dto.ApiDataQueryRequest;
import com.jd.udata.query.api.dto.ApiTopologyQueryResult;
import com.jd.udata.query.api.service.ApiQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;


@Service
@Slf4j
public class CollectPackageMangerImpl implements CollectPackageManger {

    @Autowired
    ApiQueryService apiQueryService;


    @Value("${jyCollectPackage.udata.appToken}")
    private String jyCollectPackageUdataAppToken;
    @Value("${jyCollectPackage.udata.apiGroupName}")
    private String jyCollectPackageUdataApiGroupName;


    /**
     * 查询任务统计信息
     *
     * @param dto 统计查询入参对象
     * @return 统计结果对象
     */
    @Override
    public StatisticsUnderTaskDto queryTaskStatistic(StatisticsUnderTaskQueryDto dto) {
        //校验统计查询入参
        checkStatisticsUnderTaskQueryDto(dto);

        //封装调用udata统计查询入参
        ListTaskStatisticQueryDto listTaskStatisticQueryDto = new ListTaskStatisticQueryDto();
        listTaskStatisticQueryDto.setStatisticsUnderTaskQueryDtoList(Collections.singletonList(dto));

        // 封装调用udata查询多任务统计数据的入参
        ApiDataQueryRequest queryParams = buildListTaskStatisticUDataQueryParams(listTaskStatisticQueryDto);

        // 调用udata接口
        ApiTopologyQueryResult response = callUdataQueryAPi(queryParams);

        // 处理响应数据
        ListTaskStatisticDto listTaskStatisticDto = processRespDataToListTaskStatisticDto(response);

        if (ObjectHelper.isNotEmpty(listTaskStatisticDto) && CollectionUtils.isNotEmpty(listTaskStatisticDto.getStatisticsUnderTaskDtoList())) {
            return listTaskStatisticDto.getStatisticsUnderTaskDtoList().get(0);
        }
        return null;
    }

    @Override
    public StatisticsUnderTaskDto queryTaskFlowStatistic(StatisticsUnderTaskQueryDto dto) {
        //校验统计查询入参
        checkStatisticsUnderTaskQueryDto(dto);

        // 封装调用udata查询多任务统计数据的入参
        ApiDataQueryRequest queryParams = buildQueryTaskFlowStatisticQueryParams(dto);

        // 调用udata接口
        ApiTopologyQueryResult response = callUdataQueryAPi(queryParams);

        // 处理响应数据
        StatisticsUnderTaskDto statisticsUnderTaskDto = processResDataToStatisticsUnderTaskDto(response,dto);

        return statisticsUnderTaskDto;
    }

    private StatisticsUnderTaskDto processResDataToStatisticsUnderTaskDto(ApiTopologyQueryResult response,StatisticsUnderTaskQueryDto req) {
        if (ObjectHelper.isNotNull(response) && RESULT_SUCCESS_CODE ==response.getCode() && CollectionUtils.isNotEmpty(response.getDataList())){
            try {
                List<UdataTaskFlowStatisticDto> udataTaskFlowStatisticDtoList = BeanUtils.listMapConvertToDtoList(response.getDataList(),UdataTaskFlowStatisticDto.class);
                if (CollectionUtils.isNotEmpty(udataTaskFlowStatisticDtoList)){
                    List<CollectPackageFlowDto> collectPackageFlowDtoList =udataTaskFlowStatisticDtoList.stream().map(udataTaskFlowStatisticDto -> {
                        CollectPackageFlowDto collectPackageFlowDto =assembleCollectPackageFlowDto(udataTaskFlowStatisticDto,req);
                        return collectPackageFlowDto;
                    }).collect(Collectors.toList());
                    StatisticsUnderTaskDto statisticsUnderTaskDto =new StatisticsUnderTaskDto();
                    statisticsUnderTaskDto.setCollectPackageFlowDtoList(collectPackageFlowDtoList);
                    statisticsUnderTaskDto.setBizId(req.getBizId());
                    statisticsUnderTaskDto.setBoxCode(req.getBoxCode());
                    return statisticsUnderTaskDto;
                }
            } catch (Exception e) {
                log.error("processResDataToStatisticsUnderTaskDto exception",e);
            }
        }
        return null;
    }

    private CollectPackageFlowDto assembleCollectPackageFlowDto(UdataTaskFlowStatisticDto udataTaskFlowStatisticDto,StatisticsUnderTaskQueryDto req) {
        CollectPackageFlowDto collectPackageFlowDto =new CollectPackageFlowDto();
        collectPackageFlowDto.setEndSiteId(Long.valueOf(udataTaskFlowStatisticDto.getEndSiteId()));
        collectPackageFlowDto.setEndSiteName(udataTaskFlowStatisticDto.getEndSiteName());
        if (CollectPackageExcepScanEnum.INTERCEPTED.equals(req.getType())){
            collectPackageFlowDto.setCount(udataTaskFlowStatisticDto.getInterceptNum());
        }else if (CollectPackageExcepScanEnum.FORCE_SEND.getCode().equals(req.getType())){
            collectPackageFlowDto.setCount(udataTaskFlowStatisticDto.getForceNum());
        }else {
            collectPackageFlowDto.setCount(udataTaskFlowStatisticDto.getScannedNum());
        }
        collectPackageFlowDto.setCount(udataTaskFlowStatisticDto.getScannedNum());
        return collectPackageFlowDto;
    }

    private ApiDataQueryRequest buildQueryTaskFlowStatisticQueryParams(StatisticsUnderTaskQueryDto dto) {
        ApiDataQueryRequest apiDataQueryRequest = new ApiDataQueryRequest();
        apiDataQueryRequest.setApiName("CollectPackageAggFlow");
        apiDataQueryRequest.setApiGroupName(jyCollectPackageUdataApiGroupName);
        apiDataQueryRequest.setAppToken(jyCollectPackageUdataAppToken);
        Map<String, Object> params = new HashMap<>();
        params.put("bizId", dto.getBizId());
        apiDataQueryRequest.setParams(params);
        return apiDataQueryRequest;
    }


    private void checkStatisticsUnderTaskQueryDto(StatisticsUnderTaskQueryDto dto) {
        if (ObjectHelper.isEmpty(dto)) {
            throw new JyBizException("参数错误：缺失请求对象！");
        }
        if (!ObjectHelper.isNotNull(dto.getBizId())) {
            throw new JyBizException("参数错误：缺失任务bizId参数！");
        }
    }

    @Override
    public ListTaskStatisticDto listTaskStatistic(ListTaskStatisticQueryDto dto) {
        // 校验查询多任务统计数据的入参
        checkListTaskStatisticQueryDto(dto);

        // 封装调用udata查询多任务统计数据的入参
        ApiDataQueryRequest queryParams = buildListTaskStatisticUDataQueryParams(dto);

        // 调用udata接口
        ApiTopologyQueryResult response = callUdataQueryAPi(queryParams);

        // 处理响应数据
        ListTaskStatisticDto listTaskStatisticDto = processRespDataToListTaskStatisticDto(response);

        return listTaskStatisticDto;
    }

    private ApiTopologyQueryResult callUdataQueryAPi(ApiDataQueryRequest queryParams) {
        log.info("CollectPackageManger invoke udata api request :{}", JsonHelper.toJson(queryParams));
        ApiTopologyQueryResult apiTopologyQueryResult = null;
        try {
            apiTopologyQueryResult = apiQueryService.topologyDataQuery(queryParams);
        } catch (Exception e) {
            log.error("CollectPackageManger invoke udata api exception",e);
        }
        log.info("CollectPackageManger invoke udata api response :{}", JsonHelper.toJson(apiTopologyQueryResult));
        return apiTopologyQueryResult;
    }

    private ListTaskStatisticDto processRespDataToListTaskStatisticDto(ApiTopologyQueryResult response) {
        if (ObjectHelper.isNotNull(response) && RESULT_SUCCESS_CODE ==response.getCode() && CollectionUtils.isNotEmpty(response.getDataList())){
            try {
                List<UdataTaskStatisticDto> udataTaskStatisticDtoList = BeanUtils.listMapConvertToDtoList(response.getDataList(),UdataTaskStatisticDto.class);
                if (CollectionUtils.isNotEmpty(udataTaskStatisticDtoList)){
                    List<StatisticsUnderTaskDto> statisticsUnderTaskDtoList =udataTaskStatisticDtoList.stream().map(udataTaskStatisticDto -> {
                        StatisticsUnderTaskDto statisticsUnderTaskDto =assembleStatisticsUnderTaskDto(udataTaskStatisticDto);
                        return statisticsUnderTaskDto;
                    }).collect(Collectors.toList());
                    ListTaskStatisticDto listTaskStatisticDto =new ListTaskStatisticDto();
                    listTaskStatisticDto.setStatisticsUnderTaskDtoList(statisticsUnderTaskDtoList);
                    return listTaskStatisticDto;
                }
            } catch (Exception e) {
                log.error("processRespDataToListTaskStatisticDto exception",e);
            }
        }
        return null;
    }

    private StatisticsUnderTaskDto assembleStatisticsUnderTaskDto(UdataTaskStatisticDto udataTaskStatisticDto) {
        StatisticsUnderTaskDto statisticsUnderTaskDto =new StatisticsUnderTaskDto();
        statisticsUnderTaskDto.setBizId(udataTaskStatisticDto.getBizId());
        statisticsUnderTaskDto.setBoxCode(udataTaskStatisticDto.getBoxCode());

        List<CollectScanDto> collectScanDtoList =new ArrayList<>();
        CollectScanDto intercepter =new CollectScanDto();
        intercepter.setType(CollectPackageExcepScanEnum.INTERCEPTED.getCode());
        intercepter.setName(CollectPackageExcepScanEnum.INTERCEPTED.getName());
        intercepter.setCount(udataTaskStatisticDto.getInterceptNum());
        collectScanDtoList.add(intercepter);


        CollectScanDto haveScan =new CollectScanDto();
        haveScan.setType(CollectPackageExcepScanEnum.HAVE_SCAN.getCode());
        haveScan.setName(CollectPackageExcepScanEnum.HAVE_SCAN.getName());
        haveScan.setCount(udataTaskStatisticDto.getScannedNum());
        collectScanDtoList.add(haveScan);


        CollectScanDto force =new CollectScanDto();
        force.setType(CollectPackageExcepScanEnum.FORCE_SEND.getCode());
        force.setName(CollectPackageExcepScanEnum.FORCE_SEND.getName());
        force.setCount(udataTaskStatisticDto.getForceNum());
        collectScanDtoList.add(force);


        statisticsUnderTaskDto.setExcepScanDtoList(collectScanDtoList);
        return statisticsUnderTaskDto;
    }


    private ApiDataQueryRequest buildListTaskStatisticUDataQueryParams(ListTaskStatisticQueryDto dto) {
        ApiDataQueryRequest apiDataQueryRequest = new ApiDataQueryRequest();
        apiDataQueryRequest.setApiName("CollectPackageAgg");
        apiDataQueryRequest.setApiGroupName(jyCollectPackageUdataApiGroupName);
        apiDataQueryRequest.setAppToken(jyCollectPackageUdataAppToken);
        Map<String, Object> params = new HashMap<>();
        List<String> bizIds = dto.getStatisticsUnderTaskQueryDtoList().stream().map(s -> s.getBizId()).collect(Collectors.toList());
        params.put("bizIds", bizIds);
        apiDataQueryRequest.setParams(params);
        return apiDataQueryRequest;
    }


    private void checkListTaskStatisticQueryDto(ListTaskStatisticQueryDto dto) {
        if (ObjectHelper.isEmpty(dto)) {
            throw new JyBizException("参数错误：缺失请求对象！");
        }
        if (CollectionUtils.isEmpty(dto.getStatisticsUnderTaskQueryDtoList())) {
            throw new JyBizException("参数错误：缺失任务信息参数！");
        }
        for (StatisticsUnderTaskQueryDto s : dto.getStatisticsUnderTaskQueryDtoList()) {
            if (!ObjectHelper.isNotNull(s.getBizId())) {
                throw new JyBizException("参数错误：缺失任务bizId参数！");
            }
        }
    }

    @Override
    public StatisticsUnderFlowQueryResp listPackageUnderFlow(StatisticsUnderFlowQueryReq request) {
        checkStatisticsUnderFlowQueryReq(request);

        // 封装调用udata查询多任务统计数据的入参
        ApiDataQueryRequest queryParams = buildListPackageUnderFlowUDataQueryParams(request);

        // 调用udata接口
        ApiTopologyQueryResult response = callUdataQueryAPi(queryParams);

        // 处理响应数据
        StatisticsUnderFlowQueryResp statisticsUnderFlowQueryResp = processRespDataToStatisticsUnderFlowQueryResp(response);

        return statisticsUnderFlowQueryResp;
    }

    private ApiDataQueryRequest buildListPackageUnderFlowUDataQueryParams(StatisticsUnderFlowQueryReq request) {
        ApiDataQueryRequest apiDataQueryRequest = new ApiDataQueryRequest();
        apiDataQueryRequest.setApiName("CollectPackageDetail");
        apiDataQueryRequest.setApiGroupName(jyCollectPackageUdataApiGroupName);
        apiDataQueryRequest.setAppToken(jyCollectPackageUdataAppToken);
        Map<String, Object> params = new HashMap<>();
        params.put("bizId", request.getBizId());
        params.put("endSiteId",request.getEndSiteId());
        params.put("pageSize",request.getPageSize());
        params.put("pageNum",request.getPageNo());
        apiDataQueryRequest.setParams(params);
        return apiDataQueryRequest;
    }

    private StatisticsUnderFlowQueryResp processRespDataToStatisticsUnderFlowQueryResp(ApiTopologyQueryResult response) {
        if (ObjectHelper.isNotNull(response) && RESULT_SUCCESS_CODE ==response.getCode() && CollectionUtils.isNotEmpty(response.getDataList())){
            try {
                List<UdataTaskFlowDetailDto> udataTaskFlowDetailDtoList = BeanUtils.listMapConvertToDtoList(response.getDataList(),UdataTaskFlowDetailDto.class);
                if (CollectionUtils.isNotEmpty(udataTaskFlowDetailDtoList)){
                    List<CollectPackageDto> collectPackageDtoList =udataTaskFlowDetailDtoList.stream().map(udataTaskFlowDetailDto -> {
                        CollectPackageDto collectPackageDto =assembleCollectPackageDto(udataTaskFlowDetailDto);
                        return collectPackageDto;
                    }).collect(Collectors.toList());
                    StatisticsUnderFlowQueryResp statisticsUnderFlowQueryResp =new StatisticsUnderFlowQueryResp();
                    statisticsUnderFlowQueryResp.setCollectPackageDtoList(collectPackageDtoList);
                    return statisticsUnderFlowQueryResp;
                }
            } catch (Exception e) {
                log.error("processRespDataToStatisticsUnderFlowQueryResp exception",e);
            }
        }
        return null;
    }

    private CollectPackageDto assembleCollectPackageDto(UdataTaskFlowDetailDto udataTaskFlowDetailDto) {
        CollectPackageDto collectPackageDto =new CollectPackageDto();
        collectPackageDto.setPackageCode(udataTaskFlowDetailDto.getPackageCode());
        return collectPackageDto;
    }

    private void checkStatisticsUnderFlowQueryReq(StatisticsUnderFlowQueryReq request) {
        if (ObjectHelper.isEmpty(request)) {
            throw new JyBizException("参数错误：缺失请求对象！");
        }
        if (!ObjectHelper.isNotNull(request.getBizId())) {
            throw new JyBizException("参数错误：缺失任务bizId参数！");
        }
        if (!ObjectHelper.isNotNull(request.getEndSiteId())) {
            throw new JyBizException("参数错误：缺失流向ID参数！");
        }
    }


    public static void main(String[] args) {
        ListTaskStatisticDto result = createMockListTaskStatisticDto();
        System.out.println(JsonHelper.toJson(result));

        ListTaskStatisticDto result2 = BeanUtils.mockClassObj(ListTaskStatisticDto.class);
        System.out.println(JsonHelper.toJson(result2));

    }


    // 根据具体需求创建一个Mock的ListTaskStatisticDto对象
    private static ListTaskStatisticDto createMockListTaskStatisticDto() {
        ListTaskStatisticDto mockDto = new ListTaskStatisticDto();
        // 创建一个StatisticsUnderTaskDto对象，并设置其字段值
        StatisticsUnderTaskDto statisticsUnderTaskDto = new StatisticsUnderTaskDto();
        statisticsUnderTaskDto.setBizId("123");
        statisticsUnderTaskDto.setBoxCode("ABC");
        statisticsUnderTaskDto.setMaterialCode("XYZ");

        // 创建一个CollectScanDto对象，并设置其字段值
        CollectScanDto collectScanDto = new CollectScanDto();
        collectScanDto.setType(1);
        collectScanDto.setName("Scan 1");
        collectScanDto.setCount(10);

        // 将CollectScanDto对象添加到excepScanDtoList中
        statisticsUnderTaskDto.setExcepScanDtoList(Collections.singletonList(collectScanDto));

        // 创建一个CollectPackageFlowDto对象，并设置其字段值
        CollectPackageFlowDto collectPackageFlowDto = new CollectPackageFlowDto();
        collectPackageFlowDto.setEndSiteId(1L);
        collectPackageFlowDto.setEndSiteName("Site 1");
        collectPackageFlowDto.setCount(5);

        // 将CollectPackageFlowDto对象添加到collectPackageFlowDtoList中
        statisticsUnderTaskDto.setCollectPackageFlowDtoList(Collections.singletonList(collectPackageFlowDto));

        // 将StatisticsUnderTaskDto对象添加到List中
        mockDto.setStatisticsUnderTaskDtoList(Collections.singletonList(statisticsUnderTaskDto));

        // 返回mockDto对象
        return mockDto;
    }



    }
