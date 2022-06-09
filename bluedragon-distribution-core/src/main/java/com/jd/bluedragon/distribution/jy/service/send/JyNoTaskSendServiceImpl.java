package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.*;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.delivery.IDeliveryOperationService;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.dto.send.JySendCodeDto;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.enums.CancelSendTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.service.transfer.JySendTransferService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jim.cli.Cluster;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.DMS_WEB_SYS;
import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.JY_APP;
import static com.jd.bluedragon.utils.TimeUtils.yyyyMMdd;

@Service
@Slf4j
public class JyNoTaskSendServiceImpl implements JyNoTaskSendService {
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
    @Autowired
    JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    @Qualifier("redisClientCache")
    protected Cluster redisClientCache;
    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;
    @Autowired
    IJySendVehicleService iJySendVehicleService;
    @Autowired
    private SendDetailService sendDetailService;
    @Autowired
    SortingService sortingService;


    @Override
    public InvokeResult<List<VehicleSpecResp>> listVehicleType() {
        CommonDto<List<BasicVehicleTypeDto>> rs = jyTransportManager.getVehicleTypeList();
        if (null != rs && rs.getCode() == Constants.RESULT_SUCCESS) {
            //按照车长做groupBy
            Map<String, List<VehicleTypeDto>> groupByVehicleLength = new HashMap<>();
            for (BasicVehicleTypeDto basicVehicleTypeDto : rs.getData()) {
                String vehicleLength = basicVehicleTypeDto.getVehicleLength();
                if (ObjectHelper.isNotNull(vehicleLength)) {
                    VehicleTypeDto vehicleTypeDto = BeanUtils.copy(basicVehicleTypeDto, VehicleTypeDto.class);
                    vehicleLength = vehicleLength.length() == 4 ? vehicleLength.substring(0, 3) : vehicleLength;
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
            DecimalFormat df = new DecimalFormat("###.0");
            for (Map.Entry<String, List<VehicleTypeDto>> entry : groupByVehicleLength.entrySet()) {
                String key = entry.getKey();
                List<VehicleTypeDto> value = entry.getValue();
                VehicleSpecResp vehicleSpecResp = new VehicleSpecResp();
                vehicleSpecResp.setVehicleLength(Integer.valueOf(key));
                vehicleSpecResp.setName(df.format((double) vehicleSpecResp.getVehicleLength() / 10) + "米");
                vehicleSpecResp.setVehicleTypeDtoList(value);
                vehicleSpecRespList.add(vehicleSpecResp);
            }
            Collections.sort(vehicleSpecRespList, new VehicleTypeComparator());
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, vehicleSpecRespList);
        }
        return new InvokeResult(RESULT_NODATA_GETCARTYPE_CODE, RESULT_NODATA_GETCARTYPE_MESSAGE);
    }

    class VehicleTypeComparator implements Comparator<VehicleSpecResp> {
        @Override
        public int compare(VehicleSpecResp o1, VehicleSpecResp o2) {
            return o1.getVehicleLength() - o2.getVehicleLength();
        }
    }

    @Override
    public InvokeResult<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        JyBizTaskSendVehicleEntity jyBizTaskSendVehicleEntity = initJyBizTaskSendVehicle(createVehicleTaskReq);
        jyBizTaskSendVehicleService.saveSendVehicleTask(jyBizTaskSendVehicleEntity);
        CreateVehicleTaskResp createVehicleTaskResp = new CreateVehicleTaskResp();
        createVehicleTaskResp.setBizId(jyBizTaskSendVehicleEntity.getBizId());
        createVehicleTaskResp.setBizNo(jyBizTaskSendVehicleEntity.getBizNo());
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, createVehicleTaskResp);
    }

    private JyBizTaskSendVehicleEntity initJyBizTaskSendVehicle(CreateVehicleTaskReq createVehicleTaskReq) {
        JyBizTaskSendVehicleEntity entity = new JyBizTaskSendVehicleEntity();
        entity.setBizId(genMainTaskBizId());
        entity.setBizNo(genSendVehicleTaskBizNo(createVehicleTaskReq));
        entity.setStartSiteId(Long.valueOf(createVehicleTaskReq.getCurrentOperate().getSiteCode()));
        entity.setManualCreatedFlag(1);
        entity.setVehicleType(createVehicleTaskReq.getVehicleType());
        entity.setVehicleTypeName(createVehicleTaskReq.getVehicleTypeName());
        entity.setCreateUserErp(createVehicleTaskReq.getUser().getUserErp());
        entity.setCreateUserName(createVehicleTaskReq.getUser().getUserName());
        entity.setYn(0);
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        return entity;
    }

    private String genSendVehicleTaskBizNo(CreateVehicleTaskReq createVehicleTaskReq) {
        String bizNoKey = "bizNo:" + createVehicleTaskReq.getCurrentOperate().getSiteCode() + ":" + TimeUtils.date2string(new Date(), yyyyMMdd + ":");
        long bizNo = 0;
        try {
            bizNo = redisClientCache.incr(bizNoKey);
        } catch (Exception e) {
            return "";
        }
        return String.valueOf(bizNo);
    }

    private String genMainTaskBizId() {
        String ownerKey = String.format(JyBizTaskSendVehicleEntity.BIZ_PREFIX_NOTASK, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }

    @Override
    @Transactional
    public InvokeResult deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        //删除主任务
        JyBizTaskSendVehicleEntity entity = new JyBizTaskSendVehicleEntity();
        entity.setBizId(deleteVehicleTaskReq.getBizId());
        entity.setYn(0);
        Date now = new Date();
        entity.setUpdateTime(now);
        entity.setUpdateUserErp(deleteVehicleTaskReq.getUser().getUserErp());
        entity.setUpdateUserName(deleteVehicleTaskReq.getUser().getUserName());
        jyBizTaskSendVehicleService.updateSendVehicleTask(entity);
        //删除子任务
        JyBizTaskSendVehicleDetailEntity detailEntity = new JyBizTaskSendVehicleDetailEntity();
        detailEntity.setSendVehicleBizId(deleteVehicleTaskReq.getBizId());
        detailEntity.setYn(0);
        detailEntity.setUpdateTime(now);
        detailEntity.setUpdateUserErp(deleteVehicleTaskReq.getUser().getUserErp());
        detailEntity.setUpdateUserName(deleteVehicleTaskReq.getUser().getUserName());
        jyBizTaskSendVehicleDetailService.updateDateilTaskByVehicleBizId(detailEntity);
        //删除任务-发货绑定关系+取消发货
        List<String> sendCodeList = jyVehicleSendRelationService.querySendCodesByVehicleBizId(deleteVehicleTaskReq.getBizId());
        if (ObjectHelper.isNotNull(sendCodeList) && sendCodeList.size() > 0) {
            JySendCodeDto dto = new JySendCodeDto();
            dto.setSendVehicleBizId(deleteVehicleTaskReq.getBizId());
            dto.setUpdateUserErp(deleteVehicleTaskReq.getUser().getUserErp());
            dto.setUpdateUserName(deleteVehicleTaskReq.getUser().getUserName());
            jyVehicleSendRelationService.deleteVehicleSendRelationByVehicleBizId(dto);

            for (String sendCode : sendCodeList) {
                SendM sendM = new SendM();
                sendM.setSendCode(sendCode);
                sendM.setCreateSiteCode(deleteVehicleTaskReq.getCurrentOperate().getSiteCode());
                ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
                if (!tDResponse.getCode().equals(RESULT_SUCCESS_CODE)) {
                    return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage());
                }
            }
        }
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    }

    @Override
    public InvokeResult<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        SendVehicleTaskRequest sendVehicleTaskRequest = toSendVehicleTaskRequest(vehicleTaskReq);
        //TODO
        return new InvokeResult<>();
    }

    private SendVehicleTaskRequest toSendVehicleTaskRequest(VehicleTaskReq vehicleTaskReq) {
        SendVehicleTaskRequest sendVehicleTaskRequest = new SendVehicleTaskRequest();
        sendVehicleTaskRequest.setVehicleStatus(vehicleTaskReq.getVehicleStatus());
        sendVehicleTaskRequest.setEndSiteId(vehicleTaskReq.getEndSiteId());
        sendVehicleTaskRequest.setPageNumber(vehicleTaskReq.getPageNumber());
        sendVehicleTaskRequest.setPageSize(vehicleTaskReq.getPageSize());
        sendVehicleTaskRequest.setKeyword(vehicleTaskReq.getPackageCode());
        sendVehicleTaskRequest.setCurrentOperate(vehicleTaskReq.getCurrentOperate());
        sendVehicleTaskRequest.setUser(vehicleTaskReq.getUser());
        return sendVehicleTaskRequest;
    }

    @Override
    public InvokeResult bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        //更新任务与发货批次的关联关系
        List<String> sendCodeList = jyVehicleSendRelationService.querySendCodesByVehicleDetailBizId(bindVehicleDetailTaskReq.getFromSendVehicleDetailBizId());
        VehicleSendRelationDto dto = BeanUtils.copy(bindVehicleDetailTaskReq, VehicleSendRelationDto.class);
        dto.setSendCodes(sendCodeList);
        dto.setUpdateUserErp(bindVehicleDetailTaskReq.getUser().getUserErp());
        dto.setUpdateUserName(bindVehicleDetailTaskReq.getUser().getUserName());
        jyVehicleSendRelationService.updateVehicleSendRelation(dto);
        //删除主任务
        JyBizTaskSendVehicleEntity entity = new JyBizTaskSendVehicleEntity();
        entity.setBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
        entity.setYn(0);
        Date now = new Date();
        entity.setUpdateTime(now);
        entity.setUpdateUserErp(bindVehicleDetailTaskReq.getUser().getUserErp());
        entity.setUpdateUserName(bindVehicleDetailTaskReq.getUser().getUserName());
        jyBizTaskSendVehicleService.updateSendVehicleTask(entity);
        //删除子任务
        JyBizTaskSendVehicleDetailEntity detailEntity = new JyBizTaskSendVehicleDetailEntity();
        detailEntity.setSendVehicleBizId(bindVehicleDetailTaskReq.getFromSendVehicleDetailBizId());
        detailEntity.setYn(0);
        detailEntity.setUpdateTime(now);
        detailEntity.setUpdateUserErp(bindVehicleDetailTaskReq.getUser().getUserErp());
        detailEntity.setUpdateUserName(bindVehicleDetailTaskReq.getUser().getUserName());
        jyBizTaskSendVehicleDetailService.updateDateilTaskByVehicleBizId(detailEntity);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    }

    @Override
    public InvokeResult transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        //查询要迁移的批次信息-sendCodes
        List<String> sendCodeList = jyVehicleSendRelationService.querySendCodesByVehicleDetailBizId(transferSendTaskReq.getFromSendVehicleDetailBizId());

        VehicleSendRelationDto dto = BeanUtils.copy(transferSendTaskReq, VehicleSendRelationDto.class);
        dto.setSendCodes(sendCodeList);
        dto.setUpdateUserErp(transferSendTaskReq.getUser().getUserErp());
        dto.setUpdateUserName(transferSendTaskReq.getUser().getUserName());

        if (transferSendTaskReq.getSameWayFlag()) {
            //同流向--直接变更绑定关系
            jyVehicleSendRelationService.updateVehicleSendRelation(dto);
        } else {
            //删除原绑定关系
            jyVehicleSendRelationService.deleteVehicleSendRelation(dto);
            //增加新流向绑定关系
            JyBizTaskSendVehicleDetailEntity sendVehicleDetail = jyBizTaskSendVehicleDetailService.findByBizId(transferSendTaskReq.getToSendVehicleDetailBizId());
            String newSendCode = generateSendCode(sendVehicleDetail, transferSendTaskReq.getUser().getUserErp());
            JySendCodeEntity jySendCodeEntity = initJySendCodeEntity(transferSendTaskReq, newSendCode);
            jyVehicleSendRelationService.add(jySendCodeEntity);
            //生成迁移任务，异步执行迁移逻辑
            for (String sendCode : sendCodeList) {
                List<SendM> sendMList = sendMService.selectBySiteAndSendCode(transferSendTaskReq.getCurrentOperate().getSiteCode(), sendCode);
                deliveryOperationService.asyncHandleTransfer(sendMList, newSendCode);
            }
        }
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    }

    private JySendCodeEntity initJySendCodeEntity(TransferSendTaskReq transferSendTaskReq, String sendCode) {
        JySendCodeEntity jySendCodeEntity = new JySendCodeEntity();
        jySendCodeEntity.setSendCode(sendCode);
        jySendCodeEntity.setSendVehicleBizId(transferSendTaskReq.getToSendVehicleBizId());
        jySendCodeEntity.setSendDetailBizId(transferSendTaskReq.getToSendVehicleDetailBizId());
        Date now = new Date();
        jySendCodeEntity.setCreateTime(now);
        jySendCodeEntity.setUpdateTime(now);
        jySendCodeEntity.setCreateUserErp(transferSendTaskReq.getUser().getUserErp());
        jySendCodeEntity.setCreateUserName(transferSendTaskReq.getUser().getUserName());
        return jySendCodeEntity;
    }

    private String generateSendCode(JyBizTaskSendVehicleDetailEntity sendVehicleDetail, String createUser) {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(sendVehicleDetail.getStartSiteId()));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, String.valueOf(sendVehicleDetail.getEndSiteId()));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh, "0");//TODO 这个从哪获取
        return sendCodeService.createSendCode(attributeKeyEnumObjectMap, JY_APP, createUser);
    }

    @Override
    public InvokeResult<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq request) {
        log.info("jy取消发货，按{}进行取消,扫描号码：{}", CancelSendTypeEnum.getReportTypeName(request.getType()), request.getCode());
        validateCancelReq(request);
        SendM sendM = toSendM(request);
        CancelSendTaskResp cancelSendTaskResp = new CancelSendTaskResp();
        cancelSendTaskResp.setCancelCode(request.getCode());

        //按运单
        if (CancelSendTypeEnum.WAYBILL_CODE.getCode().equals(request.getType())) {
            String wayBillCode = WaybillUtil.isPackageCode(request.getCode()) ? WaybillUtil.getWaybillCode(request.getCode()) : request.getCode();
            sendM.setBoxCode(wayBillCode);
            cancelSendTaskResp.setCancelCode(wayBillCode);
        }
        //按箱
        if (CancelSendTypeEnum.BOX_CODE.getCode().equals(request.getType())) {
            String boxCode = request.getCode();
            if (WaybillUtil.isPackageCode(request.getCode())) {
                //寻找箱号
                List<Sorting> sortingList = sortingService.findByPackageCode(request.getCurrentOperate().getSiteCode(), request.getCode());
                if (!ObjectHelper.isNotNull(sortingList) || sortingList.size() != 1) {
                    log.info("按箱号取消，根据包裹号{}查询箱号数据异常", request.getCode());
                    return new InvokeResult(BOX_NO_FOUND_BY_PACKAGE_CODE, BOX_NO_FOUND_BY_PACKAGE_MESSAGE);
                }
                boxCode = sortingList.get(0).getBoxCode();
            }
            sendM.setBoxCode(boxCode);
            cancelSendTaskResp.setCancelCode(boxCode);
        }
        //按板
        if (CancelSendTypeEnum.BOARD_NO.getCode().equals(request.getType())) {
            String boardCode = request.getCode();
            if (WaybillUtil.isPackageCode(request.getCode()) || BusinessUtil.isBoxcode(request.getCode())) {
                Response<Board> boardResponse = groupBoardManager.getBoardByBoxCode(request.getCode(), request.getCurrentOperate().getSiteCode());
                if (!JdCResponse.CODE_SUCCESS.equals(boardResponse.getCode())) {
                    log.info("按板取消发货-未根据箱号/包裹号找到匹配的板号！");
                    return new InvokeResult(MSCodeMapping.NO_BOARD_BY_PACKAGECODE.getCode(), MSCodeMapping.NO_BOARD_BY_PACKAGECODE.getMessage());
                }
                log.info("============按板取消发货，扫描的包裹号/箱号：{}，板号：{}", request.getCode(), boardResponse.getData().getCode());
                boardCode = boardResponse.getData().getCode();
            }
            sendM.setBoxCode(boardCode);
            cancelSendTaskResp.setCancelCode(boardCode);
        }

        if (CancelSendTypeEnum.BOARD_NO.getCode().equals(request.getType()) && BusinessUtil.isBoardCode(sendM.getBoxCode())) {
            //查询组板信息
            Response<List<String>> response = groupBoardManager.getBoxesByBoardCode(sendM.getBoxCode());
            if (!(JdCResponse.CODE_SUCCESS.equals(response.getCode()) && null != response.getData() && response.getData().size() > 0)) {
                log.info("根据板号：{}未查到包裹/箱号信息", sendM.getBoxCode());
                return new InvokeResult(RESULT_NO_FOUND_BY_BOARD_CODE, RESULT_NO_FOUND_BY_BOARD_MESSAGE);
            }
            List<String> packOrBoxCodes = response.getData();
            List<String> packageCodes = getPackageCodesFromPackOrBoxCodes(packOrBoxCodes, request.getCurrentOperate().getSiteCode());
            cancelSendTaskResp.setCanclePackageCount(packageCodes.size());
        }

        ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
        if (ObjectHelper.isNotNull(tDResponse) && JdCResponse.CODE_SUCCESS.equals(tDResponse.getCode())) {
            //TODO  根据包裹号掉印辉提供的服务查询列表，从那里面去流向名称
            if (cancelSendTaskResp.getCanclePackageCount() == null) {
                cancelSendTaskResp.setCanclePackageCount(sendM.getCancelPackageCount());
            }
            cancelSendTaskResp.setEndSiteName("");
            return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage(), cancelSendTaskResp);
        }
        return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage());
    }

    private void validateCancelReq(CancelSendTaskReq request) {
        //按运单-支持运单和包裹
        if (CancelSendTypeEnum.WAYBILL_CODE.getCode().equals(request.getType())) {
            if (!(WaybillUtil.isPackageCode(request.getCode()) || WaybillUtil.isWaybillCode(request.getCode()))) {
                throw new JyBizException("无效条码，请扫描运单号或者包裹号！");
            }
        }
        //按箱-支持箱和包裹
        else if (CancelSendTypeEnum.BOX_CODE.getCode().equals(request.getType())) {
            if (!(BusinessUtil.isBoxcode(request.getCode()) || WaybillUtil.isPackageCode(request.getCode()))) {
                throw new JyBizException("无效条码，请扫描箱号或者包裹号！");
            }
        }
        //按板-支持板 箱 包裹
        if (CancelSendTypeEnum.BOARD_NO.getCode().equals(request.getType())) {
            if (!(BusinessUtil.isBoardCode(request.getCode()) || BusinessUtil.isBoxcode(request.getCode())
                    || WaybillUtil.isPackageCode(request.getCode()))) {
                throw new JyBizException("无效条码，请扫描板号、箱号或者包裹号！");
            }
        } else {
            if (!WaybillUtil.isPackageCode(request.getCode())) {
                throw new JyBizException("无效条码，请扫描包裹号！");
            }
        }
    }

    private List<String> getPackageCodesFromPackOrBoxCodes(List<String> packOrBoxCodes, Integer siteCode) {
        List<String> packageCodes = new ArrayList<>();
        for (String code : packOrBoxCodes) {
            if (BusinessUtil.isBoxcode(code)) {
                log.info("=====getPackageCodesFromPackOrBoxCodes=======根据箱号获取集包包裹 {}", code);
                List<String> pCodes = getPackageCodesByBoxCodeOrSendCode(code, siteCode);
                if (pCodes != null && pCodes.size() > 0) {
                    log.info("======getPackageCodesFromPackOrBoxCodes======根据sendD找到包裹信息{}", pCodes.toString());
                    packageCodes.addAll(pCodes);
                }
            } else {
                packageCodes.add(code);
            }
        }
        return packageCodes;
    }

    private SendDetailDto initSendDetail(String barcode, int createSiteCode) {
        SendDetailDto sendDetail = new SendDetailDto();
        sendDetail.setIsCancel(0);
        sendDetail.setCreateSiteCode(createSiteCode);
        if (BusinessUtil.isBoxcode(barcode)) {
            sendDetail.setBoxCode(barcode);
        }
        if (BusinessUtil.isSendCode(barcode)) {
            sendDetail.setSendCode(barcode);
        }
        return sendDetail;
    }

    private List<String> getPackageCodesByBoxCodeOrSendCode(String boxOrSendCode, Integer siteCode) {
        //构建查询sendDetail的查询参数
        SendDetailDto sendDetail = initSendDetail(boxOrSendCode, siteCode);
        if (BusinessUtil.isBoxcode(boxOrSendCode)) {
            return sendDetailService.queryPackageCodeByboxCode(sendDetail);
        }
        return sendDetailService.queryPackageCodeBySendCode(sendDetail);
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
