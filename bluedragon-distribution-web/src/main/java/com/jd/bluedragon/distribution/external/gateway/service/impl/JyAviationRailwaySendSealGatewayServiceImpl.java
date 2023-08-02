package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.AviationSendTaskListReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationSendTaskListRes;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.send.JyAviationRailwaySendSealServiceImpl;
import com.jd.bluedragon.external.gateway.service.JyAviationRailwaySendSealGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 14:14
 * @Description
 */

@Service
public class JyAviationRailwaySendSealGatewayServiceImpl implements JyAviationRailwaySendSealGatewayService {

    @Autowired
    private JyAviationRailwaySendSealServiceImpl jyAviationRailwaySendSealService;


    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }


    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.scanTypeOptions",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<SelectOption>> scanTypeOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (SendVehicleScanTypeEnum _enum : SendVehicleScanTypeEnum.values()) {
            SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
            optionList.add(option);
        }

        Collections.sort(optionList, new SelectOption.OrderComparator());

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    public JdCResponse<AviationSendTaskListRes> pageFetchAviationSendTaskList(AviationSendTaskListReq request) {
        return null;
    }
}
