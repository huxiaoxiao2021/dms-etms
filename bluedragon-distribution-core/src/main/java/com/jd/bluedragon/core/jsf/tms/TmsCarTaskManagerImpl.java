package com.jd.bluedragon.core.jsf.tms;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.basic.ws.BasicSelectWS;
import com.jd.tms.tpc.api.TpcRouteLineCargoApi;
import com.jd.tms.tpc.dto.RouteLineCargoDto;
import com.jd.tms.tpc.dto.RouteLineCargoQueryDto;
import com.jd.tms.tpc.dto.RouteLineCargoUpdateDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TmsCarTaskManagerImpl implements TmsCarTaskManager {

    private static final Logger log = LoggerFactory.getLogger(TmsCarTaskManagerImpl.class);

    @Autowired
    private BasicSelectWS basicSelectWs;
    @Autowired
    private TpcRouteLineCargoApi tpcLineCargoVolumeApi;

    @Override
    public JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(PageDto<TransportResourceDto> page, TransportResourceDto transportResourceDto) {
        log.info("TmsCarTaskManagerImpl.getEndNodeList  获取目的站点列表 入参 page:{} ,transportResourceDto :{}",
                JSON.toJSONString(page), JSON.toJSONString(transportResourceDto));
        JdCResponse<List<CarTaskEndNodeResponse>> result = new JdCResponse<>();
        try {
            CommonDto<PageDto<TransportResourceDto>> rest = basicSelectWs.queryPageTransportResource(page, transportResourceDto);
            log.info("获取目的站点列表接口getTransportResourceByPage， 返回结果：{}", JsonHelper.toJson(rest));
            if (null == rest || CommonDto.CODE_SUCCESS !=rest.getCode() ||  rest.getData() == null) {
                result.toFail("获取目的站点列表失败!");
                return result;
            }
            List<CarTaskEndNodeResponse> listData = new ArrayList<>();
            List<TransportResourceDto> list = rest.getData().getResult();
            if (CollectionUtils.isNotEmpty(list)) {
                for (TransportResourceDto item : list) {
                    CarTaskEndNodeResponse endNode = new CarTaskEndNodeResponse();
                    endNode.setEndNodeCode(item.getEndNodeCode());
                    endNode.setEndNodeName(item.getEndNodeName());
                    //对目的分拣进行去重
                    if(!listData.contains(endNode)){
                        listData.add(endNode);
                    }

                }
            }

            result.setData(listData);
            result.toSucceed("获取目的站点列表成功!");
            log.info("获取目的站点列表接口TmsCarTaskManagerImpl.getEndNodeList， 返回结果：{}", JsonHelper.toJson(result));

        } catch (Exception e) {
            log.error("获取目的站点列表异常 入参 page:{} ,transportResourceDto :{} ,message :{}"
                    , JSON.toJSONString(page), JSON.toJSONString(transportResourceDto), e.getMessage(),e);
            result.toError("获取目的站点列表异常！");
        }
        return result;
    }


    @Override
    public JdCResponse<List<CarTaskResponse>> queryCarTaskList(RouteLineCargoQueryDto queryDto
            , com.jd.tms.tpc.dto.PageDto<RouteLineCargoDto> pageDto) {
        log.info("TmsCarTaskManagerImpl.queryCarTaskList 调用运输获取车辆任务入参 queryDto:{},PageDao:{}", JSON.toJSONString(queryDto)
                , JSON.toJSONString(pageDto));
        JdCResponse<List<CarTaskResponse>> response = new JdCResponse<>();
        try {
            com.jd.tms.tpc.dto.CommonDto<com.jd.tms.tpc.dto.PageDto<RouteLineCargoDto>> result = tpcLineCargoVolumeApi.selectPageByConditionForDms(queryDto, pageDto);
            log.info("调用运输获取车任务 result:{}",JSON.toJSONString(result));
            if (result == null || CommonDto.CODE_SUCCESS !=result.getCode() ||  result.getData() == null) {
                response.toFail("调用运输获取车辆任务失败！");
                return response;
            }
            List<RouteLineCargoDto> cargoVolumeList = result.getData().getResult();
            List<CarTaskResponse> carTaskList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(cargoVolumeList)) {
                for (RouteLineCargoDto detailDto : cargoVolumeList) {
                    CarTaskResponse carTask = new CarTaskResponse();
                    BeanUtils.copyProperties(detailDto,carTask);
                    carTaskList.add(carTask);
                }
            }
            response.setData(carTaskList);
            response.toSucceed("调用运输获取车辆任务成功！");
        } catch (Exception e) {
            log.error("调用运输获取车辆任务异常 入参- queryDto:{},PageDao:{},message-{}", JSON.toJSONString(queryDto)
                    , JSON.toJSONString(pageDto),e.getMessage(),e);
            response.toError("调用运输获取车辆任务异常!");
        }
        return response;
    }

    @Override
    public JdCResponse updateCarTaskInfo(RouteLineCargoUpdateDto updateDto) {
        log.info("调用运输更新车辆任务信息入参 updateDto:{}",JSON.toJSONString(updateDto));
        JdCResponse response = new JdCResponse();
        response.toSucceed("更新车辆任务信息成功!");
        try{
            com.jd.tms.tpc.dto.CommonDto<String> result = tpcLineCargoVolumeApi.updateLineCargoVolumeByCondition(updateDto);
            log.info("调用运输更新车辆任务信息结果 result:{}",JSON.toJSONString(result));
            if(CommonDto.CODE_SUCCESS != result.getCode()){
                response.toFail(result.getMessage());
            }
            return response;
        }catch (Exception e){
            log.error("调用运输更新车辆任务信息异常! 入参-{},message:{}",JSON.toJSONString(updateDto),e.getMessage(),e);
            response.toError("调用运输更新车辆任务信息异常!");
        }
        return response;
    }
}
