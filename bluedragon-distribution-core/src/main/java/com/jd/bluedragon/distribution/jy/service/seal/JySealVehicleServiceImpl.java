package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.blockcar.enumeration.TransTypeEnum;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.base.JdiTransWorkWSManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendSealCodeEntity;
import com.jd.bluedragon.distribution.jy.service.send.JySendAggsService;
import com.jd.bluedragon.distribution.jy.service.send.JyVehicleSendRelationService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.TimeUtils;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.tms.jdi.dto.BigQueryOption;
import com.jd.tms.jdi.dto.BigTransWorkItemDto;
import com.jd.tms.jdi.dto.TransWorkItemDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.utils.TimeUtils.yyyy_MM_dd_HH_mm_ss;

@Service
@Slf4j
public class JySealVehicleServiceImpl implements JySealVehicleService {
    @Autowired
    JySendSealCodeService jySendSealCodeService;
    @Autowired
    JySendAggsService jySendAggsService;
    @Autowired
    JyTransportManager jyTransportManager;
    @Autowired
    JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    JdiTransWorkWSManager jdiTransWorkWSManager;
    @Autowired
    NewSealVehicleService newSealVehicleService;
    @Autowired
    JyVehicleSendRelationService jyVehicleSendRelationService;
    @Autowired
    JdiQueryWSManager jdiQueryWSManager;
    @Autowired
    private ColdChainSendService coldChainSendService;
    private static final Integer SCHEDULE_TYPE_KA_BAN = 1;


    @Override
    public InvokeResult<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq) {
        //根据运输任务查询 sealcode模型
        List<String> sendCodeList = jySendSealCodeService.selectSealCodeByBizId(sealCodeReq.getSendVehicleBizId());
        if (sendCodeList != null && sendCodeList.size() > 0) {
            SealCodeResp sealCodeResp = new SealCodeResp();
            sealCodeResp.setSealCodeList(sendCodeList);
            sealCodeResp.setVehicleNumber(sealCodeReq.getVehicleNumber());
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sealCodeResp);
        }
        return new InvokeResult(RESULT_NO_FOUND_DATA_CODE, RESULT_NO_FOUND_DATA_MESSAGE);
    }

    @Override
    public InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        JyBizTaskSendVehicleDetailEntity detailEntity = jyBizTaskSendVehicleDetailService.findByBizId(sealVehicleInfoReq.getSendVehicleDetailBizId());
        if (ObjectHelper.isEmpty(detailEntity)) {
            return new InvokeResult(RESULT_NO_FOUND_DATA_CODE, RESULT_NO_FOUND_DATA_MESSAGE);
        }
        //查询已经发货的批次信息sendcodeList TODO
        SealVehicleInfoResp sealVehicleInfoResp = new SealVehicleInfoResp();
        //查询已扫描货物的重量和体积
        JySendAggsEntity jySendAggsEntity = jySendAggsService.getVehicleSendStatistics(sealVehicleInfoReq.getSendVehicleBizId());
        if (ObjectHelper.isNotNull(jySendAggsEntity)) {
            sealVehicleInfoResp.setWeight(jySendAggsEntity.getTotalScannedWeight());
            sealVehicleInfoResp.setVolume(jySendAggsEntity.getTotalScannedVolume());
        }
        BigQueryOption queryOption = new BigQueryOption();
        queryOption.setQueryTransWorkItemDto(Boolean.TRUE);
        BigTransWorkItemDto bigTransWorkItemDto = jdiTransWorkWSManager.queryTransWorkItemByOptionWithRead(detailEntity.getTransWorkItemCode(), queryOption);
        if (ObjectHelper.isNotNull(bigTransWorkItemDto) && ObjectHelper.isNotNull(bigTransWorkItemDto.getTransWorkItemDto())) {
            TransWorkItemDto transWorkItemDto = bigTransWorkItemDto.getTransWorkItemDto();
            sealVehicleInfoResp.setTransportCode(transWorkItemDto.getTransportCode());
            sealVehicleInfoResp.setVehicleNumber(transWorkItemDto.getVehicleNumber());
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sealVehicleInfoResp);
        }
        return new InvokeResult(RESULT_NO_FOUND_BY_TRANS_WOEK_ITEM_CODE, RESULT_NO_FOUND_BY_TRANS_WOEK_ITEM_MESSAGE);
    }

    @Override
    public InvokeResult sealVehicle(SealVehicleReq sealVehicleReq) {
        //查询流向下所有发货批次
        List<String> sendCodes = jyVehicleSendRelationService.querySendCodesByVehicleDetailBizId(sealVehicleReq.getSendVehicleDetailBizId());
        if (ObjectHelper.isNotNull(sendCodes) && sendCodes.size() > 0) {
            //车上已经封了的封签号
            List<String> sealCodes = jySendSealCodeService.selectSealCodeByBizId(sealVehicleReq.getSendVehicleBizId());

            SealCarDto sealCarDto = convertSealCarDto(sealVehicleReq);
            if (sealCarDto.getBatchCodes() != null) {
                sealCarDto.getBatchCodes().addAll(sendCodes);
            } else {
                sealCarDto.setBatchCodes(sendCodes);
            }
            sealCarDto.getSealCodes().addAll(sealCodes);
            //封装提交封车请求的dto
            List<SealCarDto> sealCarDtoList = new ArrayList<>();
            sealCarDtoList.add(sealCarDto);

            //批次为空的列表信息
            Map<String, String> emptyBatchCode = new HashMap<>();
            try {
                CommonDto<String> sealResp = newSealVehicleService.seal(sealCarDtoList, emptyBatchCode);
                if (sealResp != null && Constants.RESULT_SUCCESS == sealResp.getCode()) {
                    List<JySendSealCodeEntity> entityList = generateSendSealCodeList(sealVehicleReq);
                    jySendSealCodeService.addBatch(entityList);
                    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
                }
                return new InvokeResult(sealResp.getCode(), sealResp.getMessage());
            } catch (Exception e) {
                log.error("newSealVehicleService.seal封车异常", e);
            }
        }
        return new InvokeResult(NO_SEND_CODE_DATA_UNDER_BIZTASK_CODE, NO_SEND_CODE_DATA_UNDER_BIZTASK_MESSAGE);
    }

    @Override
    public InvokeResult<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq) {
        //调用运输-根据任务简码查询任务详情信息
        com.jd.tms.jdi.dto.CommonDto<TransWorkItemDto> transWorkItemResp = jdiQueryWSManager.queryTransWorkItemBySimpleCode(getVehicleNumberReq.getTransWorkItemCode());
        if (ObjectHelper.isNotNull(transWorkItemResp) && Constants.RESULT_SUCCESS == transWorkItemResp.getCode()) {
            TransWorkItemDto transWorkItemDto = transWorkItemResp.getData();
            TransportResp transportResp = new TransportResp();
            transportResp.setTransType(transWorkItemDto.getTransType());
            if (ObjectHelper.isNotNull(transportResp.getTransType()) && ObjectHelper.isNotNull(TransTypeEnum.getEnum(transportResp.getTransType()))) {
                transportResp.setTransTypeName(TransTypeEnum.getEnum(transportResp.getTransType()).getName());
            }
            transportResp.setVehicleNumber(transWorkItemDto.getVehicleNumber());
            transportResp.setRouteLineCode(transWorkItemDto.getRouteLineCode());
            transportResp.setRouteLineName(transWorkItemDto.getRouteLineName());
            transportResp.setTransWorkItemCode(transWorkItemDto.getTransWorkItemCode());
            if (SCHEDULE_TYPE_KA_BAN.equals(transWorkItemDto.getScheduleType())) {
                if (StringUtils.isNotEmpty(transWorkItemDto.getTransPlanCode())) {
                    ColdChainSend coldChainSend = coldChainSendService.getByTransCode(transWorkItemDto.getTransPlanCode());
                    if (coldChainSend != null) {
                        transportResp.setSendCode(coldChainSend.getSendCode());
                    }
                }
            }
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, transportResp);
        }
        return new InvokeResult(transWorkItemResp.getCode(), transWorkItemResp.getMessage());
    }

    private List<JySendSealCodeEntity> generateSendSealCodeList(SealVehicleReq sealVehicleReq) {
        List<JySendSealCodeEntity> entityList = new ArrayList<>();
        Date now = new Date();
        for (String sealCode : sealVehicleReq.getSealCodes()) {
            JySendSealCodeEntity jySendSealCodeEntity = new JySendSealCodeEntity();
            jySendSealCodeEntity.setSealCode(sealCode);
            jySendSealCodeEntity.setSendVehicleBizId(sealVehicleReq.getSendVehicleBizId());
            jySendSealCodeEntity.setOperateSiteId(Long.valueOf(sealVehicleReq.getCurrentOperate().getSiteCode()));
            jySendSealCodeEntity.setOperateTime(now);
            jySendSealCodeEntity.setCreateTime(now);
            jySendSealCodeEntity.setUpdateTime(now);
            jySendSealCodeEntity.setCreateUserErp(sealVehicleReq.getUser().getUserErp());
            jySendSealCodeEntity.setCreateUserName(sealVehicleReq.getUser().getUserName());
            entityList.add(jySendSealCodeEntity);
        }
        return entityList;
    }

    private SealCarDto convertSealCarDto(SealVehicleReq sealVehicleReq) {
        SealCarDto sealCarDto = BeanUtils.copy(sealVehicleReq, SealCarDto.class);
        sealCarDto.setSealCarTime(TimeUtils.date2string(new Date(), yyyy_MM_dd_HH_mm_ss));
        sealCarDto.setSealSiteId(sealVehicleReq.getCurrentOperate().getSiteCode());
        sealCarDto.setSealSiteName(sealVehicleReq.getCurrentOperate().getSiteName());
        sealCarDto.setSealUserCode(sealVehicleReq.getUser().getUserErp());
        sealCarDto.setSealUserName(sealVehicleReq.getUser().getUserName());
        return sealCarDto;
    }
}
