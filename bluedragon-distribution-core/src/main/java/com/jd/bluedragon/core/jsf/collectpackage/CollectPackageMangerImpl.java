package com.jd.bluedragon.core.jsf.collectpackage;

import com.jd.bluedragon.common.dto.collectpackage.request.StatisticsUnderFlowQueryReq;
import com.jd.bluedragon.common.dto.collectpackage.response.StatisticsUnderFlowQueryResp;
import com.jd.bluedragon.core.jsf.collectpackage.dto.*;
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
        ListTaskStatisticDto listTaskStatisticDto = processResponseData(response);

        if (ObjectHelper.isNotEmpty(listTaskStatisticDto) && CollectionUtils.isNotEmpty(listTaskStatisticDto.getStatisticsUnderTaskDtoList())) {
            return listTaskStatisticDto.getStatisticsUnderTaskDtoList().get(0);
        }
        return null;
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
        ListTaskStatisticDto listTaskStatisticDto = processResponseData(response);

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

    private ListTaskStatisticDto processResponseData(ApiTopologyQueryResult response) {
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
                log.error("processResponseData udataTaskFlowDetailDtoList exception",e);
            }
        }
        return null;
    }

    private StatisticsUnderTaskDto assembleStatisticsUnderTaskDto(UdataTaskStatisticDto udataTaskStatisticDto) {
        StatisticsUnderTaskDto statisticsUnderTaskDto =new StatisticsUnderTaskDto();
        statisticsUnderTaskDto.setBizId(udataTaskStatisticDto.getBizId());
        statisticsUnderTaskDto.setBoxCode(udataTaskStatisticDto.getBoxCode());
        //TODO
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
        return null;
    }
}
