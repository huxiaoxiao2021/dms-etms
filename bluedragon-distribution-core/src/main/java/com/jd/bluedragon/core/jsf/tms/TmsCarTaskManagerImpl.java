package com.jd.bluedragon.core.jsf.tms;

import IceInternal.Ex;
import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.basic.ws.BasicSelectWS;
import com.jd.tms.tpc.dto.AccountDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TmsCarTaskManagerImpl implements TmsCarTaskManager{

    private static final Logger log = LoggerFactory.getLogger(TmsCarTaskManagerImpl.class);

    @Autowired
    private BasicSelectWS basicSelectWs;

    @Override
    public JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(PageDto<TransportResourceDto> page, TransportResourceDto transportResourceDto) {
        log.info("TmsCarTaskManagerImpl.getEndNodeList  获取目的站点列表 入参 page:{} ,transportResourceDto :{}",
                JSON.toJSONString(page),JSON.toJSONString(transportResourceDto));
        JdCResponse<List<CarTaskEndNodeResponse>> result = new JdCResponse<>();
        try{
            CommonDto<PageDto<TransportResourceDto>> rest = basicSelectWs.queryPageTransportResource(page,transportResourceDto);
            log.info("查询运输接口getTransportResourceByPage， 返回结果：{}",  JsonHelper.toJson(rest));
            if(null != rest && rest.getData() != null ){
                List<CarTaskEndNodeResponse> listData=new ArrayList<CarTaskEndNodeResponse>();
                List<TransportResourceDto> list = rest.getData().getResult();
                if(CollectionUtils.isNotEmpty(list)){
                    for (TransportResourceDto item:list){
                        CarTaskEndNodeResponse endNode = new CarTaskEndNodeResponse();
                        endNode.setEndNodeCode(item.getEndNodeCode());
                        endNode.setEndNodeName(item.getEndNodeName());
                        listData.add(endNode);
                    }
                }
                result.setData(listData);
                result.toSucceed();
                log.info("查询运输接口TmsCarTaskManagerImpl.getEndNodeList， 返回结果：{}",  JsonHelper.toJson(result));
            }
        }catch (Exception e){
            log.error("获取目的站点列表异常 入参 page:{} ,transportResourceDto :{} ,message :{}"
                    , JSON.toJSONString(page),JSON.toJSONString(transportResourceDto),e.getMessage());
            result.toFail("获取目的站点列表异常！");
        }
        return result;
    }



//    @Override
//    public JdCResponse<List<CarTaskResponse>> queryCarTaskList(SortingReferQueryDto dto, Page<SortingReferQueryDto> pageDto) {
//        return null;
//    }
//
//    @Override
//    public JdCResponse updateCarTaskInfo(AccountDto accountDto, SortingReferQueryDto sortingReferQueryDto) {
//        return null;
//    }
}
