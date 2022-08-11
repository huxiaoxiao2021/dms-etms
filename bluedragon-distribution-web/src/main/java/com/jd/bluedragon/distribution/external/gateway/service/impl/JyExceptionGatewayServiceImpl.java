package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpBaseReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByStatusDto;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpStatusEnum;
import com.jd.bluedragon.external.gateway.service.JyExceptionGatewayService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JyExceptionGatewayServiceImpl implements JyExceptionGatewayService {
    @Override
    public JdCResponse<Object> uploadScan(ExpUploadScanReq req) {

        return JdCResponse.ok();
    }

    @Override
    public JdCResponse<List<StatisticsByStatusDto>> statisticsByStatus(ExpBaseReq req) {

        List<StatisticsByStatusDto> list = new ArrayList<>();
        for (JyExpStatusEnum value : JyExpStatusEnum.values()) {
            StatisticsByStatusDto dto = new StatisticsByStatusDto();
            dto.setStatus(value.getCode());
            dto.setCount(new Random().nextInt(100));
            list.add(dto);
        }

        return JdCResponse.ok(list);
    }
}
