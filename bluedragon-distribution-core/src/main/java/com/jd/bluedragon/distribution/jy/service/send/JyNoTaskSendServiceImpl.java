package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.*;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.delivery.IDeliveryOperationService;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.enums.CancelSendTypeEnum;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailServiceImpl;
import com.jd.bluedragon.distribution.jy.service.transfer.JySendTransferService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.DMS_WEB_SYS;

@Service
@Slf4j
public class JyNoTaskSendServiceImpl implements JyNoTaskSendService{
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
    @Autowired
    JyVehicleSendRelationService jyVehicleSendRelationService;
    @Autowired
    SendCodeService sendCodeService;
    @Autowired
    JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    private IDeliveryOperationService deliveryOperationService;


    @Override
    public InvokeResult<List<VehicleSpecResp>> listVehicleType() {
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
            return new InvokeResult(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage(), vehicleSpecRespList);
        }
        return new InvokeResult(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
    }

    @Override
    public InvokeResult<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        return null;
    }

    @Override
    public InvokeResult deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        return null;
    }

    @Override
    public InvokeResult<List<VehicleTaskResp>> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        return null;
    }

    @Override
    public InvokeResult bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        return null;
    }

    @Override
    public InvokeResult transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        //查询要迁移的批次信息-sendCodes
        List<String> sendCodeList =jyVehicleSendRelationService.querySendCodesByVehicleDetailBizId(transferSendTaskReq.getFromSendVehicleDetailBizId());

        VehicleSendRelationDto dto =BeanUtils.copy(transferSendTaskReq,VehicleSendRelationDto.class);
        dto.setSendCodes(sendCodeList);
        dto.setUpdateUserErp(transferSendTaskReq.getUser().getUserErp());
        dto.setUpdateUserName(transferSendTaskReq.getUser().getUserName());

        if (transferSendTaskReq.getSameWayFlag()){
            //同流向--直接变更绑定关系
            jyVehicleSendRelationService.updateVehicleSendRelation(dto);
        }
        else {
            //删除原绑定关系
            jyVehicleSendRelationService.deleteVehicleSendRelation(dto);
            //增加新流向绑定关系
            JyBizTaskSendVehicleDetailEntity sendVehicleDetail =jyBizTaskSendVehicleDetailService.findByBizId(transferSendTaskReq.getToSendVehicleDetailBizId());
            String newSendCode =generateSendCode(sendVehicleDetail,transferSendTaskReq.getUser().getUserErp());
            JySendCodeEntity jySendCodeEntity = initJySendCodeEntity(transferSendTaskReq,newSendCode);
            jyVehicleSendRelationService.add(jySendCodeEntity);
            //生成迁移任务，异步执行迁移逻辑
            for (String sendCode:sendCodeList){
                List<SendM> sendMList =sendMService.selectBySiteAndSendCode(transferSendTaskReq.getCurrentOperate().getSiteCode(),sendCode);
                deliveryOperationService.asyncHandleTransfer(sendMList,newSendCode);
            }
        }
        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE);
    }

    private JySendCodeEntity initJySendCodeEntity(TransferSendTaskReq transferSendTaskReq, String sendCode) {
        JySendCodeEntity jySendCodeEntity =new JySendCodeEntity();
        jySendCodeEntity.setSendCode(sendCode);
        jySendCodeEntity.setSendVehicleBizId(transferSendTaskReq.getToSendVehicleBizId());
        jySendCodeEntity.setSendDetailBizId(transferSendTaskReq.getToSendVehicleDetailBizId());
        Date now =new Date();
        jySendCodeEntity.setCreateTime(now);
        jySendCodeEntity.setUpdateTime(now);
        jySendCodeEntity.setCreateUserErp(transferSendTaskReq.getUser().getUserErp());
        jySendCodeEntity.setCreateUserName(transferSendTaskReq.getUser().getUserName());
        return jySendCodeEntity;
    }

    private String generateSendCode(JyBizTaskSendVehicleDetailEntity sendVehicleDetail,String createUser) {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(sendVehicleDetail.getStartSiteId()));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, String.valueOf(sendVehicleDetail.getEndSiteId()));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh, "0");
        //TODO 这里要不要新增一个jy的来源
        return sendCodeService.createSendCode(attributeKeyEnumObjectMap, DMS_WEB_SYS, createUser);
    }

    @Override
    public InvokeResult<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq request) {
        SendM sendM = toSendM(request);
        if (WaybillUtil.isPackageCode(request.getCode()) || BusinessUtil.isBoxcode(request.getCode())) {
            String sendCode = sendMService.querySendCodeBySelective(sendM);
            if (!ObjectHelper.isNotNull(sendCode)) {
                return new InvokeResult(MSCodeMapping.NO_SENDCODE_BY_PACKAGECODE.getCode(), MSCodeMapping.NO_SENDCODE_BY_PACKAGECODE.getMessage());
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
                return new InvokeResult(MSCodeMapping.NO_BOARD_BY_PACKAGECODE.getCode(), MSCodeMapping.NO_BOARD_BY_PACKAGECODE.getMessage());
            }
            log.info("============按板取消发货，扫描的包裹号/箱号：{}，板号：{}", request.getCode(), boardResponse.getData().getCode());
            sendM.setBoxCode(boardResponse.getData().getCode());
        }

        ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
        if(ObjectHelper.isNotNull(tDResponse) && JdCResponse.CODE_SUCCESS.equals(tDResponse.getCode())){
            //TODO  根据包裹号掉印辉提供的服务查询列表，从那里面去流向名称
            CancelSendTaskResp cancelSendTaskResp =new CancelSendTaskResp();
            cancelSendTaskResp.setCanclePackageCount(sendM.getCancelPackageCount());
            cancelSendTaskResp.setEndSiteName("");
            return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage(),cancelSendTaskResp);
        }
        return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage());
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
