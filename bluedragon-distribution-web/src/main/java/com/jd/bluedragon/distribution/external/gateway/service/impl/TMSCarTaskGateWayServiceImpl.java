package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardResultDto;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.bluedragon.external.gateway.service.TMSCarTaskGateWayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
public class TMSCarTaskGateWayServiceImpl implements TMSCarTaskGateWayService {
    @Override
    public JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(Integer beginNodeCode) {
        log.info("获取当前站点的目的站点列表 入参-{}",beginNodeCode);
        JdCResponse<List<CarTaskEndNodeResponse>> jdCResponse = new JdCResponse<List<CarTaskEndNodeResponse>>();
        jdCResponse.toSucceed();
//        if(beginNodeCode == null){
//            jdCResponse.toFail("站点ID不能为空！");
//        }
        List<CarTaskEndNodeResponse> list = new ArrayList<>();
        CarTaskEndNodeResponse endNode = new CarTaskEndNodeResponse();
        endNode.setEndNodeCode(1000);
        endNode.setEndNodeName("目的站点1");

        CarTaskEndNodeResponse endNode1 = new CarTaskEndNodeResponse();
        endNode1.setEndNodeCode(2000);
        endNode1.setEndNodeName("目的站点2");

        CarTaskEndNodeResponse endNode2 = new CarTaskEndNodeResponse();
        endNode2.setEndNodeCode(3000);
        endNode2.setEndNodeName("目的站点3");

        list.add(endNode);
        list.add(endNode1);
        list.add(endNode2);

        jdCResponse.setData(list);
        return jdCResponse;
    }

    @Override
    public JdCResponse<List<CarTaskResponse>>  queryCarTaskList(Integer beginNodeCode, Integer endNodeCode) {

        log.info("获取当前站点的目的站点列表 入参-{}",beginNodeCode);
        JdCResponse<List<CarTaskResponse>> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();
//        if(beginNodeCode == null ){
//            jdCResponse.toFail("站点ID不能为空！");
//        }
        List<CarTaskResponse> list = new ArrayList<>();

        CarTaskResponse task1 = new CarTaskResponse();
        task1.setId(1);
        task1.setRouteLineCode("A00001");
        task1.setEndNodeCode("38");
        task1.setEndNodeName("通州分拣目的分拣");
        task1.setSumVolume(111D);
        task1.setVolume(1111D);
        task1.setPredictedDeviation(11111D);
        task1.setAdjustVolume(111111D);
        task1.setVehicleTypeCount("1000");
        task1.setVehicleChangeFlag(1);
        task1.setPlanDepartTime(new Date());
        task1.setPackageCount(10000);
        list.add(task1);

        CarTaskResponse task2 = new CarTaskResponse();
        task2.setId(2);
        task2.setRouteLineCode("BB00001");
        task2.setEndNodeCode("10061");
        task2.setEndNodeName("北京大兴分拣目的分拣");
        task2.setSumVolume(2222D);
        task2.setVolume(22222D);
        task2.setPredictedDeviation(2222D);
        task2.setAdjustVolume(222222D);
        task2.setVehicleTypeCount("2000");
        task2.setVehicleChangeFlag(0);
        task2.setPlanDepartTime(new Date());
        task2.setPackageCount(20000);
        list.add(task2);

        jdCResponse.setData(list);

        return jdCResponse;
    }

    @Override
    public void updateCarTaskInfo() {

    }
}
