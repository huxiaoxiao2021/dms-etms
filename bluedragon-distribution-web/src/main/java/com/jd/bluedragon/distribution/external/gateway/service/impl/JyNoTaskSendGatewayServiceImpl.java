package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.VehicleSpecResp;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.VehicleTypeDto;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.jy.enums.CancelSendTypeEnum;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.service.transfer.JySendTransferService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.JyNoTaskSendGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


@Slf4j
@UnifiedExceptionProcess
public class JyNoTaskSendGatewayServiceImpl implements JyNoTaskSendGatewayService {

    @Autowired
    JyTransportManager jyTransportManager;
    @Autowired
    JySendTransferService jySendTransferService;
    @Autowired
    DeliveryService deliveryService;
    @Autowired
    GroupBoardManager groupBoardManager;
    @Autowired
    SendMService sendMService;

    @Override
    public JdCResponse<List<VehicleSpecResp>> listVehicleType() {
        CommonDto<List<BasicVehicleTypeDto>> rs = jyTransportManager.getVehicleTypeList();
        if (null != rs && rs.getCode() == 1) {
            //按照车长做groupBy
            Map<String, List<VehicleTypeDto>> groupByVehicleLength = new HashMap<>();
            for (BasicVehicleTypeDto basicVehicleTypeDto : rs.getData()) {
                String vehicleLength = basicVehicleTypeDto.getVehicleLength();
                if (ObjectHelper.isNotNull(vehicleLength)) {
                    VehicleTypeDto vehicleTypeDto = BeanUtils.copy(basicVehicleTypeDto, VehicleTypeDto.class);
                    if (groupByVehicleLength.containsKey(vehicleLength)) {
                        groupByVehicleLength.get(vehicleLength).add(vehicleTypeDto);
                    } else {
                        List<VehicleTypeDto> vehicleTypeDtoList = new ArrayList<>();
                        vehicleTypeDtoList.add(vehicleTypeDto);
                        groupByVehicleLength.put(vehicleLength, vehicleTypeDtoList);
                    }
                }
            }
            //封装树形结构响应体
            List<VehicleSpecResp> vehicleSpecRespList = new ArrayList<>();
            for (Map.Entry<String, List<VehicleTypeDto>> entry : groupByVehicleLength.entrySet()) {
                String key = entry.getKey();
                List<VehicleTypeDto> value = entry.getValue();
                VehicleSpecResp vehicleSpecResp = new VehicleSpecResp();
                vehicleSpecResp.setVehicleLength(key);
                vehicleSpecResp.setVehicleTypeDtoList(value);
                vehicleSpecRespList.add(vehicleSpecResp);
            }
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage(), vehicleSpecRespList);
        }
        return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
    }

    @Override
    public JdCResponse<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        return null;
    }

    @Override
    public JdCResponse deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        return null;
    }

    @Override
    public JdCResponse<List<VehicleTaskResp>> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        return null;
    }

    @Override
    public JdCResponse bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        return null;
    }

    @Override
    public JdCResponse transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        return null;
    }

    @Override
    public JdCResponse cancelSendTask(CancelSendTaskReq request) {
        SendM sendM = toSendM(request);
        if (WaybillUtil.isPackageCode(request.getCode()) || BusinessUtil.isBoxcode(request.getCode())) {
            String sendCode = sendMService.querySendCodeBySelective(sendM);
            if (!ObjectHelper.isNotNull(sendCode)) {
                return new JdCResponse(MSCodeMapping.NO_SENDCODE_BY_PACKAGECODE.getCode(), MSCodeMapping.NO_SENDCODE_BY_PACKAGECODE.getMessage());
            }
            sendM.setSendCode(sendCode);
            sendM.setReceiveSiteCode(BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode));
        }

        if (WaybillUtil.isPackageCode(request.getCode()) && CancelSendTypeEnum.WAYBILL_CODE.getCode().equals(request.getType())) {
            sendM.setBoxCode(WaybillUtil.getWaybillCode(request.getCode()));
        }
        if (WaybillUtil.isPackageCode(request.getCode()) && CancelSendTypeEnum.BOARD_NO.getCode().equals(request.getType())) {
            Response<Board> boardResponse = groupBoardManager.getBoardByBoxCode(request.getCode(), request.getCurrentOperate().getSiteCode());
            if (!JdCResponse.CODE_SUCCESS.equals(boardResponse.getCode())) {
                log.info("按板取消发货-未根据箱号/包裹号找到匹配的板号！");
                return new JdCResponse(MSCodeMapping.NO_BOARD_BY_PACKAGECODE.getCode(), MSCodeMapping.NO_BOARD_BY_PACKAGECODE.getMessage());
            }
            log.info("============按板取消发货，扫描的包裹号/箱号：{}，板号：{}", request.getCode(), boardResponse.getData().getCode());
            sendM.setBoxCode(boardResponse.getData().getCode());
        }

        ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
        return new JdCResponse(tDResponse.getCode(), tDResponse.getMessage());
    }

    private SendM toSendM(CancelSendTaskReq request) {
        SendM sendM = new SendM();
        sendM.setBoxCode(request.getCode());
        sendM.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        sendM.setUpdaterUser(request.getUser().getUserName());
        sendM.setSendType(10);
        sendM.setUpdateUserCode(request.getUser().getUserCode());
        Date now = new Date();
        sendM.setOperateTime(now);
        sendM.setUpdateTime(now);
        sendM.setYn(0);
        return sendM;
    }

}
