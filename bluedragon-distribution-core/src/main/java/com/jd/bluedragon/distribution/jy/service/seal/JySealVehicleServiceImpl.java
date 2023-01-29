package com.jd.bluedragon.distribution.jy.service.seal;

import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.blockcar.enumeration.TransTypeEnum;
import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.request.QueryBelongBoardReq;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.seal.SealCarSendCodeResp;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.base.JdiTransWorkWSManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountReq;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendSealCodeEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.jy.service.send.JySendAggsService;
import com.jd.bluedragon.distribution.jy.service.send.JyVehicleSendRelationService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.dbs.util.CollectionUtils;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.SiteSignTool;
import com.jd.ql.dms.report.WeightVolSendCodeJSFService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolSendCodeSumVo;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.jdi.dto.BigQueryOption;
import com.jd.tms.jdi.dto.BigTransWorkItemDto;
import com.jd.tms.jdi.dto.TransWorkItemDto;
import com.jd.transboard.api.dto.BoardBoxInfoDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
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
    JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    private ColdChainSendService coldChainSendService;
    private static final Integer SCHEDULE_TYPE_KA_BAN = 1;

    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;

    @Autowired
    private JyAppDataSealService jyAppDataSealService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WeightVolSendCodeJSFService weightVolSendCodeJSFService;
    @Autowired
    JimDbLock jimDbLock;
    @Autowired
    private NewSealVehicleService newsealVehicleService;

    @Autowired
    private JyBizTaskComboardService jyBizTaskComboardService;

    @Autowired
    private JyComboardAggsService jyComboardAggsService;
    
    @Autowired
    UccPropertyConfiguration ucc;

    @Autowired
    GroupBoardManager groupBoardManager;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.listSealCodeByBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq) {
        //根据运输任务查询 sealcode模型
        List<String> sealCodeList = jySendSealCodeService.selectSealCodeByBizId(sealCodeReq.getSendVehicleBizId());
        if (sealCodeList != null && sealCodeList.size() > 0) {
            SealCodeResp sealCodeResp = new SealCodeResp();
            sealCodeResp.setSealCodeList(sealCodeList);
            sealCodeResp.setVehicleNumber(sealCodeReq.getVehicleNumber());
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sealCodeResp);
        }
        return new InvokeResult(RESULT_NO_FOUND_DATA_CODE, RESULT_NO_FOUND_DATA_MESSAGE);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.getSealVehicleInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        JyBizTaskSendVehicleDetailEntity detailEntity = jyBizTaskSendVehicleDetailService.findByBizId(sealVehicleInfoReq.getSendVehicleDetailBizId());
        if (ObjectHelper.isEmpty(detailEntity)) {
            return new InvokeResult(RESULT_NO_FOUND_DATA_CODE, RESULT_NO_FOUND_DATA_MESSAGE);
        }
        SealVehicleInfoResp sealVehicleInfoResp = new SealVehicleInfoResp();
        //查询已经发货的批次信息sendcodeList
        List<String> sendCodeList =jyVehicleSendRelationService.querySendCodesByVehicleDetailBizId(sealVehicleInfoReq.getSendVehicleDetailBizId());
        if (ObjectHelper.isNotNull(sendCodeList)){
            sealVehicleInfoResp.setSendCodeList(sendCodeList);
        }
        //查询封签号
        List<String> sealCodeList = jySendSealCodeService.selectSealCodeByBizId(sealVehicleInfoReq.getSendVehicleBizId());
        if (ObjectHelper.isNotNull(sealCodeList)){
            sealVehicleInfoResp.setSealCodeList(sealCodeList);
        }
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
            if (ObjectHelper.isNotNull(transWorkItemDto.getTransportCode()) &&
                    !transWorkItemDto.getTransportCode().toUpperCase().startsWith("T")){
                sealVehicleInfoResp.setTransportCode(transWorkItemDto.getTransportCode());
            }
            sealVehicleInfoResp.setVehicleNumber(transWorkItemDto.getVehicleNumber());
        }
        sealVehicleInfoResp.setSavedPageData(jyAppDataSealService.loadSavedPageData(sealVehicleInfoReq.getSendVehicleDetailBizId()));
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sealVehicleInfoResp);
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.sealVehicle", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult sealVehicle(SealVehicleReq sealVehicleReq) {
        log.info("jy提交封车,sealVehicleReq:{}",JsonHelper.toJson(sealVehicleReq));
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
            List<String> afterFilterDuplicate = ListUtil.processDuplicateByContains(sealCarDto.getBatchCodes());
            sealCarDto.setBatchCodes(afterFilterDuplicate);

            if (sealCarDto.getSealCodes() != null) {
                sealCarDto.getSealCodes().addAll(sealCodes);
            } else {
                sealCarDto.setSealCodes(sealCodes);
            }
            if (ObjectHelper.isNotNull(sealVehicleReq.getTransWay())
                    && TransTypeEnum.ROAD_ZHENGCHE.getType()==sealVehicleReq.getTransWay()){
                if (ObjectHelper.isEmpty(sealCarDto.getSealCodes()) || sealCarDto.getSealCodes().size()<=0){
                    return new InvokeResult(COMMIT_SEAL_CAR_EXCEPTION_CODE, COMMIT_SEAL_CAR_NO_SEAL_CODES_MESSAGE);
                }
            }

            //封装提交封车请求的dto
            List<SealCarDto> sealCarDtoList = new ArrayList<>();
            sealCarDtoList.add(sealCarDto);

            //批次为空的列表信息
            Map<String, String> emptyBatchCode = new HashMap<>();
            try {
                CommonDto<String> sealResp = newSealVehicleService.seal(sealCarDtoList, emptyBatchCode);
                if (sealResp != null && Constants.RESULT_SUCCESS == sealResp.getCode()) {
                    if(ObjectHelper.isNotNull(sealVehicleReq.getSealCodes()) && sealVehicleReq.getSealCodes().size()>0){
                        List<JySendSealCodeEntity> entityList = generateSendSealCodeList(sealVehicleReq);
                        jySendSealCodeService.addBatch(entityList);
                    }
                    updateTaskStatus(sealVehicleReq, sealCarDto);
                    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
                }
                return new InvokeResult(sealResp.getCode(), sealResp.getMessage());
            } catch (Exception e) {
                log.error("newSealVehicleService.seal封车异常", e);
                return new InvokeResult(COMMIT_SEAL_CAR_EXCEPTION_CODE, COMMIT_SEAL_CAR_EXCEPTION_MESSAGE);
            }
        }
        return new InvokeResult(NO_SEND_CODE_DATA_UNDER_BIZTASK_CODE, NO_SEND_CODE_DATA_UNDER_BIZTASK_MESSAGE);
    }

    @Override
    public InvokeResult czSealVehicle(SealVehicleReq sealVehicleReq) {
        log.info("jy传站封车,sealVehicleReq:{}",JsonHelper.toJson(sealVehicleReq));
        String sealLockKey = String.format(Constants.JY_SEAL_LOCK_PREFIX, sealVehicleReq.getSendVehicleDetailBizId());
        if (!jimDbLock.lock(sealLockKey, sealVehicleReq.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
            throw new JyBizException("当前系统繁忙,请稍后再试！");
        }
        try {
            JyBizTaskSendVehicleDetailEntity entity =jyBizTaskSendVehicleDetailService.findByBizId(sealVehicleReq.getSendVehicleDetailBizId());
            if (ObjectHelper.isNotNull(entity) && JyBizTaskSendStatusEnum.SEALED.getCode().equals(entity.getVehicleStatus())){
                throw new JyBizException("该流向已封车！");
            }
            //校验批次是否已经封车
            for (String sendCode:sealVehicleReq.getBatchCodes()){
                if (newsealVehicleService.newCheckSendCodeSealed(sendCode, new StringBuffer())) {
                    throw new JyBizException("该批次:"+sendCode+"已经封车");
                }
            }

            SealCarDto sealCarDto = convertSealCarDto(sealVehicleReq);
            //车上已经封了的封签号
            List<String> sealCodes = jySendSealCodeService.selectSealCodeByBizId(sealVehicleReq.getSendVehicleBizId());
            if (sealCarDto.getSealCodes() != null) {
                sealCarDto.getSealCodes().addAll(sealCodes);
            } else {
                sealCarDto.setSealCodes(sealCodes);
            }
            //封装提交封车请求的dto
            List<SealCarDto> sealCarDtoList = new ArrayList<>();
            sealCarDtoList.add(sealCarDto);
            //批次为空的列表信息
            Map<String, String> emptyBatchCode = new HashMap<String,String>();
            NewSealVehicleResponse sealResp = newSealVehicleService.doSealCarWithVehicleJob(sealCarDtoList,emptyBatchCode);
            if (sealResp != null && JdResponse.CODE_OK.equals(sealResp.getCode())) {
                if(ObjectHelper.isNotNull(sealVehicleReq.getSealCodes()) && sealVehicleReq.getSealCodes().size()>0){
                    List<JySendSealCodeEntity> entityList = generateSendSealCodeList(sealVehicleReq);
                    jySendSealCodeService.addBatch(entityList);
                }
                updateTaskStatus(sealVehicleReq, sealCarDto);
                return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
            }
            return new InvokeResult(sealResp.getCode(), sealResp.getMessage());
        } finally {
            jimDbLock.releaseLock(sealLockKey, sealVehicleReq.getRequestId());
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.saveSealVehicle", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> saveSealVehicle(SealVehicleReq sealVehicleReq) {
    	return jyAppDataSealService.savePageData(sealVehicleReq);
    }
    private void updateTaskStatus(SealVehicleReq sealVehicleReq, SealCarDto sealCarDto) {
        JyBizTaskSendVehicleDetailEntity taskSendDetail = jyBizTaskSendVehicleDetailService.findByBizId(sealVehicleReq.getSendVehicleDetailBizId());
        JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(sealVehicleReq.getSendVehicleBizId());
        taskSend.setUpdateTime(new Date());
        taskSend.setUpdateUserErp(sealVehicleReq.getUser().getUserErp());
        taskSend.setUpdateUserName(sealVehicleReq.getUser().getUserName());

        taskSendDetail.setSealCarTime(DateHelper.parseDate(sealCarDto.getSealCarTime(), Constants.DATE_TIME_FORMAT));
        taskSendDetail.setUpdateTime(taskSend.getUpdateTime());
        taskSendDetail.setUpdateUserErp(taskSend.getUpdateUserErp());
        taskSendDetail.setUpdateUserName(taskSend.getUpdateUserName());
        sendVehicleTransactionManager.updateTaskStatus(taskSend, taskSendDetail, JyBizTaskSendDetailStatusEnum.SEALED);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.getTransWorkItemByWorkItemCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq) {
        //调用运输-根据任务简码查询任务详情信息
        com.jd.tms.jdi.dto.CommonDto<TransWorkItemDto> transWorkItemResp = jdiQueryWSManager.queryTransWorkItemBySimpleCode(getVehicleNumberReq.getTransWorkItemCode());
        if (ObjectHelper.isNotNull(transWorkItemResp) && Constants.RESULT_SUCCESS == transWorkItemResp.getCode()) {
            TransWorkItemDto transWorkItemDto = transWorkItemResp.getData();
            TransportResp transportResp = new TransportResp();
            transportResp.setTransType(transWorkItemDto.getTransWay());
            transportResp.setTransWay(transWorkItemDto.getTransWay());
            if (ObjectHelper.isNotNull(transportResp.getTransWay()) && ObjectHelper.isNotNull(TransTypeEnum.getEnum(transportResp.getTransWay()))) {
                transportResp.setTransWayName(TransTypeEnum.getEnum(transportResp.getTransWay()).getName());
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
        //转换体积单位 立方厘米转换为立方米
        sealCarDto.setVolume(NumberHelper.cm3ToM3(sealVehicleReq.getVolume()));
        return sealCarDto;
    }

    public static void main(String[] args) {
        SealCarDto sealCarDto =new SealCarDto();
        List<String> sealCodes =new ArrayList<>();
        sealCodes.add("1");
        sealCodes.add("2");
        sealCarDto.setSealCodes(null);



        //System.out.println(sealCarDto.getSealCodes());
    }

    /**
     * 校验运力编码和发货批次的目的地是否一致
     * @param validSendCodeReq 请求参数
     * @author fanggang7
     * @time 2022-09-27 15:15:03 周二
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.validateTranCodeAndSendCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SealCarSendCodeResp> validateTranCodeAndSendCode(ValidSendCodeReq validSendCodeReq) {
        InvokeResult<SealCarSendCodeResp> invokeResult = new InvokeResult<>(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
        try {
            final Result<Void> checkResult = checkValidSendCodeReq(validSendCodeReq);
            if (checkResult.isFail()) {
                invokeResult.setCode(checkResult.getCode());
                invokeResult.setMessage(checkResult.getMessage());
                return invokeResult;
            }
            invokeResult = new InvokeResult<>(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
            final InvokeResult<Void> checkBatchCodeResult = newSealVehicleService.checkBatchCode(validSendCodeReq.getSendCode());
            if (RESULT_SUCCESS_CODE == checkBatchCodeResult.getCode()) {
                com.jd.tms.basic.dto.CommonDto<TransportResourceDto> commonDto = newSealVehicleService.getTransportResourceByTransCode(validSendCodeReq.getTransportCode());
                if (commonDto == null) {
                    invokeResult.setCode(SERVER_ERROR_CODE);
                    invokeResult.setMessage("查询运力信息结果为空:" + validSendCodeReq.getSendCode());
                    return invokeResult;
                }
                if (commonDto.getData() != null && Constants.RESULT_SUCCESS == commonDto.getCode()) {
                    Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(validSendCodeReq.getSendCode());
                    Integer endNodeId = commonDto.getData().getEndNodeId();
                    if (receiveSiteCode.equals(endNodeId)) {
                        invokeResult.setCode(JdResponse.CODE_OK);
                        invokeResult.setMessage(JdResponse.MESSAGE_OK);
                    } else {
                        //不分传摆和运力都去校验目的地类型是中转场的时候 跳过目的地不一致逻辑
                        BaseStaffSiteOrgDto endNodeSite = baseMajorManager.getBaseSiteBySiteId(endNodeId);
                        if (endNodeSite != null && SiteSignTool.supportTemporaryTransfer(endNodeSite.getSiteSign())) {
                            invokeResult.setCode(RESULT_SUCCESS_CODE);
                            invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
                        } else {
                            invokeResult.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                            invokeResult.setMessage(NewSealVehicleResponse.TIPS_RECEIVESITE_DIFF_ERROR);
                            return invokeResult;
                        }
                    }
                } else if (Constants.RESULT_WARN == commonDto.getCode()) {
                    invokeResult.setCode(SERVER_ERROR_CODE);
                    invokeResult.setMessage(commonDto.getMessage());
                    return invokeResult;
                } else {
                    invokeResult.setCode(SERVER_ERROR_CODE);
                    invokeResult.setMessage("查询运力信息出错！");
                    log.warn("根据运力编码：【{}】查询运力信息出错,出错原因:{}", validSendCodeReq.getTransportCode(), commonDto.getMessage());
                    return invokeResult;
                }
                // 查询批次对应的包裹重量体积数据
                try {
                    final SealCarSendCodeResp sealCarSendCodeResp = new SealCarSendCodeResp();
                    sealCarSendCodeResp.setBatchCode(validSendCodeReq.getSendCode());
                    sealCarSendCodeResp.setPackageVolumeTotal(new BigDecimal(BigDecimal.ZERO.toString()));
                    sealCarSendCodeResp.setPackageWeightTotal(new BigDecimal(BigDecimal.ZERO.toString()));
                    invokeResult.setData(sealCarSendCodeResp);
                    final BaseEntity<WeightVolSendCodeSumVo> weightVolSendCodeSumVoBaseEntity = weightVolSendCodeJSFService.sumWeightAndVolumeBySendCode(validSendCodeReq.getSendCode());
                    if (weightVolSendCodeSumVoBaseEntity != null && weightVolSendCodeSumVoBaseEntity.isSuccess()
                            && weightVolSendCodeSumVoBaseEntity.getData() != null) {
                        sealCarSendCodeResp.setPackageVolumeTotal(BigDecimal.valueOf(weightVolSendCodeSumVoBaseEntity.getData().getPackageVolumeSum()).setScale(2, RoundingMode.CEILING));
                        sealCarSendCodeResp.setPackageWeightTotal(BigDecimal.valueOf(weightVolSendCodeSumVoBaseEntity.getData().getPackageWeightSum()).setScale(2, RoundingMode.CEILING));
                    } else {
                        log.error("根据批次号查询批次下重量体积失败：{}", validSendCodeReq.getSendCode());
                    }
                } catch (Exception e) {
                    log.error("call WeightVolSendCodeJSFService.sumWeightAndVolumeBySendCode error ", e);
                }
            }else {
                invokeResult.setCode(checkBatchCodeResult.getCode());
                invokeResult.setMessage(checkBatchCodeResult.getMessage());
            }
        } catch (Exception e) {
            log.error("JySealVehicleServiceImpl.validateTranCodeAndSendCode e ", e);
        }
        return invokeResult;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.checkTransCodeScan", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult checkTransCodeScan(CheckTransportReq reqcuest) {
        InvokeResult invokeResult = new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
        try {
            com.jd.tms.basic.dto.CommonDto<TransportResourceDto> commonDto = newSealVehicleService.getTransportResourceByTransCode(reqcuest.getTransportCode());
            if (commonDto == null) {
                invokeResult.setCode(SERVER_ERROR_CODE);
                invokeResult.setMessage("查询运力信息结果为空:");
                return invokeResult;
            }
            if (commonDto.getData() != null && Constants.RESULT_SUCCESS == commonDto.getCode()) {
                Integer endNodeId = commonDto.getData().getEndNodeId();
                if (reqcuest.getEndSiteId().equals(endNodeId)) {
                    invokeResult.setCode(JdResponse.CODE_OK);
                    invokeResult.setMessage(JdResponse.MESSAGE_OK);
                } else {
                    //不分传摆和运力都去校验目的地类型是中转场的时候 跳过目的地不一致逻辑
                    BaseStaffSiteOrgDto endNodeSite = baseMajorManager.getBaseSiteBySiteId(endNodeId);
                    if (endNodeSite != null && SiteSignTool.supportTemporaryTransfer(endNodeSite.getSiteSign())) {
                        invokeResult.setCode(RESULT_SUCCESS_CODE);
                        invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
                    } else {
                        invokeResult.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                        invokeResult.setMessage(NewSealVehicleResponse.TIPS_RECEIVESITE_DIFF_ERROR);
                        return invokeResult;
                    }
                }
            } else if (Constants.RESULT_WARN == commonDto.getCode()) {
                invokeResult.setCode(SERVER_ERROR_CODE);
                invokeResult.setMessage(commonDto.getMessage());
                return invokeResult;
            } else {
                invokeResult.setCode(SERVER_ERROR_CODE);
                invokeResult.setMessage("查询运力信息出错！");
                log.warn("根据运力编码：【{}】查询运力信息出错,出错原因:{}", reqcuest.getTransportCode(), commonDto.getMessage());
                return invokeResult;
            }
        } catch (Exception e) {
            log.error("JySealVehicleServiceImpl.checkTransCode e ", e);
        }
        return invokeResult;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.listComboardBySendFlow", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<BoardQueryResp> listComboardBySendFlow(BoardQueryReq request) {
        InvokeResult<BoardQueryResp> invokeResult = new InvokeResult<>();
        if (request == null || request.getEndSiteId() < 0 || request.getCurrentOperate() == null) {
            invokeResult.setCode(NO_SEND_FLOW_CODE);
            invokeResult.setMessage(NO_SEND_FLOW_MESSAGE);
            return invokeResult;
        }
        BoardQueryResp boardQueryResp = new BoardQueryResp();
        List<BoardDto> boardDtos = new ArrayList<>();
        boardQueryResp.setBoardDtoList(boardDtos);
        invokeResult.setData(boardQueryResp);

        // 获取当前场地未封车的板号
        SendFlowDto sendFlow = new SendFlowDto();
        sendFlow.setEndSiteId(request.getEndSiteId());
        sendFlow.setStartSiteId(request.getCurrentOperate().getSiteCode());
        Date time = DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -ucc.getJyComboardTaskCreateTimeBeginDay());
        sendFlow.setQueryTimeBegin(time);
        List<Integer> comboardSourceList = new ArrayList<>();
        comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
        comboardSourceList.add(JyBizTaskComboardSourceEnum.AUTOMATION.getCode());
        sendFlow.setComboardSourceList(comboardSourceList);
        PageHelper.startPage(request.getPageNo(),request.getPageSize());
        List<JyBizTaskComboardEntity> boardList = jyBizTaskComboardService.listBoardTaskBySendFlow(sendFlow);

        if (CollectionUtils.isEmpty(boardList)) {
            invokeResult.setCode(RESULT_SUCCESS_CODE);
            invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
            return invokeResult;
        }
        
        //查询流向下7天内未封车的板总数
        BoardCountReq boardCountReq = new BoardCountReq();
        boardCountReq.setCreateTime(time);
        List<Integer> endSiteIdList = new ArrayList<>();
        endSiteIdList.add(request.getEndSiteId());
        boardCountReq.setEndSiteIdList(endSiteIdList);
        boardCountReq.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
        List<Integer> sourceList = new ArrayList<>();
        sourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
        sourceList.add(JyBizTaskComboardSourceEnum.AUTOMATION.getCode());        
        boardCountReq.setComboardSourceList(sourceList);
        List<BoardCountDto> entityList = jyBizTaskComboardService.boardCountTaskBySendFlowList(boardCountReq);
        
        if (!CollectionUtils.isEmpty(entityList)) {
            boardQueryResp.setBoardTotal(entityList.get(0).getBoardCount().longValue());
        }

        // 获取板号扫描数量统计数据
        List<String> boardCodeList = getboardCodeList(boardList);
        List<JyComboardAggsEntity> boardScanCountList = new ArrayList<>();
        try {
            boardScanCountList = jyComboardAggsService.queryComboardAggs(boardCodeList);
        } catch (Exception e) {
            log.error("获取板号统计信息失败：{}", JsonHelper.toJson(boardCodeList), e);
        }
        HashMap<String, JyComboardAggsEntity> boardScanCountMap = getBoardScanCountMap(boardScanCountList);


        for (JyBizTaskComboardEntity board : boardList) {
            BoardDto boardDto = new BoardDto();
            boardDto.setSendCode(board.getSendCode());
            boardDto.setBoardCode(board.getBoardCode());
            boardDto.setComboardSource(JyBizTaskComboardSourceEnum.getNameByCode(board.getComboardSource()));
            boardDto.setStatus(board.getBoardStatus());
            boardDto.setStatusDesc(ComboardStatusEnum.getStatusDesc(board.getBoardStatus()));

            if (boardScanCountMap.containsKey(board.getBoardCode())) {
                JyComboardAggsEntity aggsEntity = boardScanCountMap.get(board.getBoardCode());
                boardDto.setBoxHaveScanCount(aggsEntity.getBoxScannedCount());
                boardDto.setPackageHaveScanCount(aggsEntity.getPackageScannedCount());
                if (aggsEntity.getWeight() != null) {
                    boardDto.setWeight(aggsEntity.getWeight().toString());
                }
                if (aggsEntity.getVolume() != null) {
                    boardDto.setVolume(aggsEntity.getVolume().toString());
                }
            }

            boardDtos.add(boardDto);
        }
        invokeResult.setCode(RESULT_SUCCESS_CODE);
        invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
        return invokeResult;
    }

    @Override
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InvokeResult<Boolean> updateBoardStatusAndSealCode(com.jd.etms.vos.dto.SealCarDto sealCarCodeOfTms, String batchCode, String operateUserCode, String operateUserName) {

        // 更新批次状态
        InvokeResult<Boolean> invokeResult = new InvokeResult<>(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
        if (!jyBizTaskComboardService.updateBoardStatusBySendCodeList(batchCode, operateUserCode, operateUserName)) {
            invokeResult.setData(Boolean.FALSE);
            invokeResult.setMessage("更新板状态失败！");
            return invokeResult;
        }
        InvokeResult<Boolean> result = deleteBySendVehicleBizId(sealCarCodeOfTms.getTransWorkItemCode(), operateUserCode, operateUserName);
        if (result != null && result.getData() == Boolean.FALSE) {
            throw new JyBizException("删除封签列表失败！");
        }
        invokeResult.setCode(RESULT_SUCCESS_CODE);
        invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
        invokeResult.setData(Boolean.TRUE);
        return invokeResult;
    }

    @Override
    public InvokeResult<Boolean> deleteBySendVehicleBizId(String transWorkItemCode, String operateUserCode, String operateUserName) {
        
        InvokeResult<Boolean> invokeResult = new InvokeResult<>(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);

        // 根据派车单号查询发车任务
        if (StringUtils.isEmpty(transWorkItemCode)) {
            invokeResult.setData(Boolean.FALSE);
            invokeResult.setMessage("派车单号为空");
            return invokeResult;
        }
        JyBizTaskSendVehicleDetailEntity query = new JyBizTaskSendVehicleDetailEntity();
        query.setTransWorkItemCode(transWorkItemCode);
        JyBizTaskSendVehicleDetailEntity jyBizTaskSendVehicleDetail = jyBizTaskSendVehicleDetailService.findByTransWorkItemCode(query);
        if (jyBizTaskSendVehicleDetail == null || StringUtils.isEmpty(jyBizTaskSendVehicleDetail.getSendVehicleBizId())) {
            invokeResult.setData(Boolean.FALSE);
            invokeResult.setMessage("未查询到该派车单号的发车明细");
            return invokeResult;
        }

        // 根据业务主键逻辑删除封签号
        List<String> sendSealCodeList = jySendSealCodeService.selectSealCodeByBizId(jyBizTaskSendVehicleDetail.getSendVehicleBizId());
        if (CollectionUtils.isEmpty(sendSealCodeList)) {
            invokeResult.setCode(RESULT_SUCCESS_CODE);
            invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
            invokeResult.setData(Boolean.TRUE);
            return invokeResult;
        }

        JySendSealCodeEntity updateData = new JySendSealCodeEntity();
        updateData.setUpdateUserErp(operateUserCode);
        updateData.setUpdateUserName(operateUserName);
        updateData.setSendVehicleBizId(jyBizTaskSendVehicleDetail.getSendVehicleBizId());
        if (!jySendSealCodeService.deleteBySendVehicleBizId(updateData)) {
            invokeResult.setData(Boolean.FALSE);
            invokeResult.setMessage("删除封签号失败");
            return invokeResult;
        }
        invokeResult.setCode(RESULT_SUCCESS_CODE);
        invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
        invokeResult.setData(Boolean.TRUE);
        return invokeResult;
    }

    @Override
    public InvokeResult<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request) {
        if (StringUtils.isEmpty(request.getBarCode())) {
            return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
        }
        if (!BusinessHelper.isBoxcode(request.getBarCode()) && !WaybillUtil.isPackageCode(request.getBarCode())) {
            return new InvokeResult<>(CHECK_BARCODE_CODE,"请扫描包裹号或者箱号！");
        }
        QueryBelongBoardResp belongBoardResp = new QueryBelongBoardResp();
        BoardDto boardDto = new BoardDto();
        belongBoardResp.setBoardDto(boardDto);
        // 查询板号信息
        BoardBoxInfoDto boardBoxInfoDto = groupBoardManager.getBoardBoxInfo(request.getBarCode(), request.getCurrentOperate().getSiteCode());
        if (boardBoxInfoDto != null && boardBoxInfoDto.getCode() != null) {
            boardDto.setBoardCode(boardBoxInfoDto.getCode());
        } else {
            log.error("未找到对应的板信息：{}", JsonHelper.toJson(request.getBarCode()));
            return new InvokeResult<>(NOT_FIND_BOARD_INFO_CODE, NOT_FIND_BOARD_INFO_MESSAGE);
        }

        // 根据板号查询任务信息
        JyBizTaskComboardEntity query = new JyBizTaskComboardEntity();
        query.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
        query.setBoardCode(boardBoxInfoDto.getCode());
        JyBizTaskComboardEntity comboardEntity = jyBizTaskComboardService.queryBizTaskByBoardCode(query);

        if (comboardEntity == null) {
            log.error("未找到板的批次信息：{}", JsonHelper.toJson(request.getBarCode()));
            return new InvokeResult<>(NOT_FIND_BOARD_INFO_CODE, NOT_FIND_BOARD_INFO_MESSAGE);
        } else {
            boardDto.setSendCode(comboardEntity.getSendCode());
            boardDto.setComboardSource(JyBizTaskComboardSourceEnum.getNameByCode(comboardEntity.getComboardSource()));
            boardDto.setStatus(comboardEntity.getBoardStatus());
            boardDto.setStatusDesc(ComboardStatusEnum.getStatusDesc(comboardEntity.getBoardStatus()));
        }

        JyComboardAggsEntity aggsEntity = null;
        try {
            aggsEntity = jyComboardAggsService.queryComboardAggs(boardBoxInfoDto.getCode());
        } catch (Exception e) {
            log.error("获取板号统计信息失败：{}", JsonHelper.toJson(boardBoxInfoDto.getCode()), e);
        }

        if (aggsEntity != null) {
            boardDto.setBoxHaveScanCount(aggsEntity.getBoxScannedCount());
            boardDto.setPackageHaveScanCount(aggsEntity.getPackageScannedCount());
            if (aggsEntity.getWeight() != null) {
                boardDto.setWeight(aggsEntity.getWeight().toString());
            }
            if (aggsEntity.getVolume() != null) {
                boardDto.setVolume(aggsEntity.getVolume().toString());
            }
        }
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, belongBoardResp);
    }

    private HashMap<String, JyComboardAggsEntity> getBoardScanCountMap(List<JyComboardAggsEntity> boardScanCountList) {
        HashMap<String, JyComboardAggsEntity> boardScanCountMap = new HashMap<>();
        for (JyComboardAggsEntity aggsEntity : boardScanCountList) {
            boardScanCountMap.put(aggsEntity.getBoardCode(),aggsEntity);
        }
        return boardScanCountMap;
    }


    private List<String> getboardCodeList(List<JyBizTaskComboardEntity> boardList) {
        List<String> boardCodeList = new ArrayList<>();
        for (JyBizTaskComboardEntity boardInfo : boardList) {
            boardCodeList.add(boardInfo.getBoardCode());
        }
        return boardCodeList;
    }

    private Result<Void> checkValidSendCodeReq(ValidSendCodeReq validSendCodeReq) {
        Result<Void> result = Result.success();
        if (!(Constants.SEAL_TYPE_TRANSPORT.equals(validSendCodeReq.getSealCarType()) || Constants.SEAL_TYPE_TASK.equals(validSendCodeReq.getSealCarType()))) {
            return result.toFail("不支持该封车类型！", RESULT_THIRD_ERROR_CODE);
        }
        if (!ObjectHelper.isNotNull(validSendCodeReq.getSendCode())) {
            return result.toFail("参数错误：批次号为空！", RESULT_THIRD_ERROR_CODE);
        }
        if (!ObjectHelper.isNotNull(validSendCodeReq.getTransportCode())) {
            return result.toFail("参数错误：运力编码为空！", RESULT_THIRD_ERROR_CODE);
        }
        return result;
    }
}
