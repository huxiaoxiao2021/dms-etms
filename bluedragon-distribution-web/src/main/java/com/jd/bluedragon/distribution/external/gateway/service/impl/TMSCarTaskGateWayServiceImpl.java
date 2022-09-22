package com.jd.bluedragon.distribution.external.gateway.service.impl;

import IceInternal.Ex;
import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardResultDto;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskQueryRequest;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskUpdateDto;
import com.jd.bluedragon.common.dto.carTask.request.FindEndNodeRequest;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.bluedragon.distribution.tms.TmsCarTaskService;
import com.jd.bluedragon.external.gateway.service.TMSCarTaskGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
public class TMSCarTaskGateWayServiceImpl implements TMSCarTaskGateWayService {

    @Autowired
    private TmsCarTaskService tmsCarTaskService;

    @Override
    public JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(String startNodeCode) {
        log.info("获取当前站点的目的站点列表 入参-{}", startNodeCode);
        JdCResponse<List<CarTaskEndNodeResponse>> jdCResponse = new JdCResponse<>();
        if (StringUtils.isBlank(startNodeCode)) {
            jdCResponse.toFail("站点ID不能为空！");
            return jdCResponse;
        }
        try {
            return tmsCarTaskService.getEndNodeList(startNodeCode);
        } catch (Exception e) {
            log.error("TMSCarTaskGateWayServiceImpl.getEndNodeList 获取目的分拣列表异常 入参：{}，message:{}", startNodeCode, e.getMessage());
            jdCResponse.toError("获取目的分拣列表异常!");
            return jdCResponse;
        }
    }

    @Override
    public JdCResponse<List<CarTaskResponse>> queryCarTaskList(CarTaskQueryRequest request) {
        log.info("TMSCarTaskGateWayServiceImpl.queryCarTaskList获取当前站点的目的站点列表 入参-{}", JSON.toJSONString(request));
        JdCResponse<List<CarTaskResponse>> jdCResponse = new JdCResponse<>();
        try {
            if (null == request || StringUtils.isBlank(request.getBeginNodeCode())) {
                jdCResponse.toFail("入参不能为空！");
                return jdCResponse;
            }
            return tmsCarTaskService.queryCarTaskList(request);
        } catch (Exception e) {
            log.error("TMSCarTaskGateWayServiceImpl.queryCarTaskList 获取车辆任务列表异常!-{}", e.getMessage(), e);
            jdCResponse.toError("获取车辆任务列表异常!");
            return jdCResponse;
        }
    }

    @Override
    public JdCResponse updateCarTaskInfo(CarTaskUpdateDto carTaskUpdateDto) {
        log.info("TMSCarTaskGateWayServiceImpl.updateCarTaskInfo 更新车辆任务信息-{}", JSON.toJSONString(carTaskUpdateDto));
        JdCResponse jdCResponse = new JdCResponse();
        try {
            String checkResult = this.checkParm(carTaskUpdateDto);
            log.info("更新车辆任务 checkResult-{}", checkResult);
            if (StringUtils.isNotBlank(checkResult)) {
                jdCResponse.toFail(checkResult);
                return jdCResponse;
            }
            return tmsCarTaskService.updateCarTaskInfo(carTaskUpdateDto);
        } catch (Exception e) {
            log.error("TMSCarTaskGateWayServiceImpl.updateCarTaskInfo 更新车辆任务信息异常 -{}", e.getMessage(), e);
            jdCResponse.toError("更新车辆任务信息异常!");
            return jdCResponse;
        }
    }

    /**
     * 获取目的地7位编码
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.TMSCarTaskGateWayServiceImpl.findEndNodeCode", mState = {JProEnum.TP,JProEnum.FunctionError})
    public JdCResponse<String> findEndNodeCode(FindEndNodeRequest request) {
        //校验输入条码必须为包裹号、运单号或者目的地ID中的一种
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();

        try {
            if(request != null && request.getCurrentOperate()!=null){
                String result = tmsCarTaskService.findEndNodeCode(request.getCurrentOperate().getSiteCode(),request.getBarCode());
                jdCResponse.setData(result);
            }
        } catch (Exception e) {
            log.error("TMSCarTaskGateWayServiceImpl.findEndNodeCode error! -{},{}", JsonHelper.toJson(request), e.getMessage(), e);
            jdCResponse.toError("获取目的地7位编码异常!");
            return jdCResponse;
        }

        return jdCResponse;
    }

    /**
     * 车辆任务更新参数校验
     *
     * @param carTaskUpdateDto
     * @return
     */
    private String checkParm(CarTaskUpdateDto carTaskUpdateDto) {
        if (null == carTaskUpdateDto) {
            return "入参不能为空!";
        }
        if (StringUtils.isBlank(carTaskUpdateDto.getBeginNodeCode())) {
            return "始发网点编码不能为空!";
        }
        if (StringUtils.isBlank(carTaskUpdateDto.getEndNodeCode())) {
            return "目的网点编码不能为空!";
        }
        if (StringUtils.isBlank(carTaskUpdateDto.getRouteLineCode())) {
            return "路由线路编码不能为空!";
        }
        if (null == carTaskUpdateDto.getPlanDepartTime()) {
            return "计划发车时间不能为空!";
        }
        if (null == carTaskUpdateDto.getVolume()) {
            return "调整方量不能为空!";
        }
        if (carTaskUpdateDto.getVolume() <= 0) {
            return "调整方量不能小于等于0!";
        }
        if (StringUtils.isBlank(carTaskUpdateDto.getAccountCode())) {
            return "账号编码不能为空!";
        }
        return "";
    }
}
