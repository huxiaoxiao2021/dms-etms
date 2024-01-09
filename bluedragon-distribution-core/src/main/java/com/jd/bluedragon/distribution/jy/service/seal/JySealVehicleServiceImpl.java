package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarSourceEnum;
import com.jd.bluedragon.common.dto.blockcar.enumeration.TransTypeEnum;
import com.jd.bluedragon.common.dto.comboard.request.QueryBelongBoardReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardDto;
import com.jd.bluedragon.common.dto.comboard.response.QueryBelongBoardResp;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendTaskTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.seal.SealCarSendCodeResp;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.*;
import com.jd.bluedragon.common.dto.send.request.GetTaskSimpleCodeReq;
import com.jd.bluedragon.common.dto.send.response.GetTaskSimpleCodeResp;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.base.JdiTransWorkWSManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.cancelSealRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.constants.TaskBindTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.JdiBoardLoadWSManager;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.send.JySendSealCodeEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.distribution.jy.service.send.*;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskBindService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.summary.BusinessKeyTypeEnum;
import com.jd.bluedragon.distribution.jy.summary.JyStatisticsSummaryEntity;
import com.jd.bluedragon.distribution.jy.summary.SummarySourceEnum;
import com.jd.bluedragon.distribution.jy.task.*;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.sendCode.DMSSendCodeJSFService;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.enums.SendStatusEnum;
import com.jd.bluedragon.utils.*;
import com.jd.dbs.util.CollectionUtils;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.SiteSignTool;
import com.jd.ql.dms.report.WeightVolSendCodeJSFService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolSendCodeSumVo;
import com.jd.tms.basic.dto.BasicDictDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.jdi.dto.*;
import com.jd.transboard.api.dto.BoardBoxInfoDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.utils.TimeUtils.yyyy_MM_dd_HH_mm_ss;

@Service("jyBaseSealVehicleService")
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
    DmsConfigManager dmsConfigManager;

    @Autowired
    GroupBoardManager groupBoardManager;
    @Autowired
    BasicQueryWSManager basicQueryWSManager;
    @Autowired
    SendMService sendMService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;
    @Autowired
    private JySendAggsService sendAggService;
    @Autowired
    @Qualifier("jySendVehicleService")
    private IJySendVehicleService jySendVehicleService;

    @Autowired
    @Qualifier("jySendVehicleServiceTys")
    private IJySendVehicleService jySendVehicleServiceTys;
    
    @Autowired
    private JdiBoardLoadWSManager jdiBoardLoadWSManager;
    
    @Autowired
    @Qualifier("dmsSendCodeJSFService")
    DMSSendCodeJSFService dmsSendCodeJSFService;
    
    public static final Integer LOADING_COMPLETEDC = 30;
    @Autowired
    private JySeaCarlCacheService jySeaCarlCacheService;
    @Autowired
    private JyBizTaskBindService jyBizTaskBindService;


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
        if (ObjectHelper.isNotNull(detailEntity.getTaskSimpleCode())){
            sealVehicleInfoResp.setTaskSimpleCode(detailEntity.getTaskSimpleCode());
            com.jd.tms.jdi.dto.CommonDto<TransWorkItemDto> transWorkItemResp = jdiQueryWSManager.queryTransWorkItemBySimpleCode(detailEntity.getTaskSimpleCode());
            if (ObjectHelper.isNotNull(transWorkItemResp) && Constants.RESULT_SUCCESS == transWorkItemResp.getCode()) {
                TransWorkItemDto transWorkItemDto = transWorkItemResp.getData();
                sealVehicleInfoResp.setRouteLineCode(transWorkItemDto.getRouteLineCode());
                sealVehicleInfoResp.setRouteLineName(transWorkItemDto.getRouteLineName());
            }
        }
        setSavedPageData(sealVehicleInfoReq, sealVehicleInfoResp);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sealVehicleInfoResp);
    }

    public void setSavedPageData(SealVehicleInfoReq sealVehicleInfoReq, SealVehicleInfoResp sealVehicleInfoResp) {
        sealVehicleInfoResp.setSavedPageData(jyAppDataSealService.loadSavedPageData(sealVehicleInfoReq.getSendVehicleDetailBizId()));
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.sealVehicle", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult sealVehicle(SealVehicleReq sealVehicleReq) {
        log.info("jy提交封车,sealVehicleReq:{}",JsonHelper.toJson(sealVehicleReq));
        //查询流向下所有发货批次
        List<String> sendCodes = jyVehicleSendRelationService.querySendCodesByVehicleDetailBizId(sealVehicleReq.getSendVehicleDetailBizId());
        if (ObjectHelper.isNotNull(sendCodes) && sendCodes.size() > 0) {
            //车上已经封了的封签号
            List<String> sealCodes = this.getSealCodes(sealVehicleReq);

            SealCarDto sealCarDto = convertSealCarDto(sealVehicleReq);
            if (sealCarDto.getBatchCodes() != null) {
                sealCarDto.getBatchCodes().addAll(sendCodes);
            } else {
                sealCarDto.setBatchCodes(sendCodes);
            }
            List<String> afterFilterDuplicate = ListUtil.processDuplicateByContains(sealCarDto.getBatchCodes());
            sealCarDto.setBatchCodes(afterFilterDuplicate);

            if (sealCarDto.getSealCodes() != null && sealCarDto.getSealCodes().size()>0
                    && sealCodes!=null && sealCodes.size()>0) {
                List mergeList =new ArrayList();
                mergeList.addAll(sealCarDto.getSealCodes());
                mergeList.addAll(sealCodes);
                sealCarDto.setSealCodes(mergeList);
            } else if (sealCodes!=null && sealCodes.size()>0){
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
                    if(JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode().equals(sealVehicleReq.getFuncType())) {
                        //空铁任务
                        updateTaskStatusSealAndSummary(sealVehicleReq, sendCodes, sealCarDto);
                    }else {
                        updateTaskStatus(sealVehicleReq, sealCarDto);
                    }
                    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
                }
                return new InvokeResult(sealResp.getCode(), sealResp.getMessage());
            } catch (Exception e) {
                log.error("newSealVehicleService.seal封车异常,request={},errMsg={}", JsonHelper.toJson(sealVehicleReq), e.getMessage(), e);
                return new InvokeResult(COMMIT_SEAL_CAR_EXCEPTION_CODE, COMMIT_SEAL_CAR_EXCEPTION_MESSAGE);
            }
        }
        return new InvokeResult(NO_SEND_CODE_DATA_UNDER_BIZTASK_CODE, NO_SEND_CODE_DATA_UNDER_BIZTASK_MESSAGE);
    }

    private List<String> getSealCodes(SealVehicleReq sealVehicleReq) {
        if(JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode().equals(sealVehicleReq.getFuncType())) {
            //空铁封车没有封签号
            return null;
        }
        else{
            return jySendSealCodeService.selectSealCodeByBizId(sealVehicleReq.getSendVehicleBizId());
        }
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.czSealVehicle", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Void> czSealVehicle(SealVehicleReq sealVehicleReq) {
        log.info("jy传站封车,sealVehicleReq:{}",JsonHelper.toJson(sealVehicleReq));
        String sealLockKey = String.format(Constants.JY_SEAL_LOCK_PREFIX, sealVehicleReq.getSendVehicleDetailBizId());
        if (!jimDbLock.lock(sealLockKey, sealVehicleReq.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
            throw new JyBizException("当前系统繁忙,请稍后再试！");
        }
        if (!ObjectHelper.isNotNull(sealVehicleReq.getBatchCodes())){
            throw new JyBizException("批次不能为空！");
        }
        try {
            JyBizTaskSendVehicleDetailEntity entity =jyBizTaskSendVehicleDetailService.findByBizId(sealVehicleReq.getSendVehicleDetailBizId());
            if (ObjectHelper.isNotNull(entity) && JyBizTaskSendStatusEnum.SEALED.getCode().equals(entity.getVehicleStatus())){
                throw new JyBizException("该流向已封车！");
            }
            //校验批次是否已经封车
            if (sealVehicleReq.getFuncType() == null || sealVehicleReq.getFuncType().equals(JyFuncCodeEnum.COMBOARD_SEND_POSITION.getCode())) {
                if (dmsConfigManager.getPropertyConfig().getNeedValidateBatchCodeHasSealed()  && sealVehicleReq.getBatchCodes().size() <= dmsConfigManager.getPropertyConfig().getJyComboardSealBoardListSelectLimit()){
                    for (String sendCode:sealVehicleReq.getBatchCodes()){
                        if (newsealVehicleService.newCheckSendCodeSealed(sendCode, new StringBuffer())) {
                            throw new JyBizException("该批次:"+sendCode+"已经封车,请勿重复勾选");
                        }
                    }
                }
            }

            SealCarDto sealCarDto = convertSealCarDto(sealVehicleReq);
            //车上已经封了的封签号
            List<String> sealCodes = jySendSealCodeService.selectSealCodeByBizId(sealVehicleReq.getSendVehicleBizId());
            if (sealCarDto.getSealCodes() != null && sealCarDto.getSealCodes().size()>0
                && sealCodes!=null && sealCodes.size()>0) {
                List mergeList =new ArrayList();
                mergeList.addAll(sealCarDto.getSealCodes());
                mergeList.addAll(sealCodes);
                sealCarDto.setSealCodes(mergeList);
            } else if (sealCodes!=null && sealCodes.size()>0){
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
                if (ObjectHelper.isNotNull(sealResp.getData())){
                    List<com.jd.etms.vos.dto.SealCarDto> successSealCarList =(List<com.jd.etms.vos.dto.SealCarDto>)sealResp.getData();
                    saveSealSendCode(successSealCarList.get(0).getBatchCodes(),sealVehicleReq);
                    if (sealVehicleReq.getFuncType() == null || sealVehicleReq.getFuncType().equals(JyFuncCodeEnum.COMBOARD_SEND_POSITION.getCode())) {
                        jyBizTaskComboardService.updateBoardStatusBySendCodeList(successSealCarList.get(0).getBatchCodes(),
                                sealVehicleReq.getUser().getUserErp(),sealVehicleReq.getUser().getUserName(),ComboardStatusEnum.SEALED);
                    }
                }
                return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
            }
            return new InvokeResult(sealResp.getCode(), sealResp.getMessage());
        } finally {
            jimDbLock.releaseLock(sealLockKey, sealVehicleReq.getRequestId());
        }
    }

    private void saveSealSendCode(List<String> batchCodes, SealVehicleReq sealVehicleReq) {
        this.saveSealSendCode(batchCodes,
                sealVehicleReq.getSendVehicleBizId(),
                sealVehicleReq.getSendVehicleDetailBizId(),
                sealVehicleReq.getUser().getUserErp(),
                sealVehicleReq.getUser().getUserName());
    }

    private void saveSealSendCode(List<String> batchCodes, String bizId, String detailBizId, String userErp, String userName) {
        try {
            Date now =new Date();
            List<JySendCodeEntity> jySendCodeEntityList =new ArrayList<>();
            for (String batchCode :batchCodes){
                JySendCodeEntity entity =new JySendCodeEntity();
                entity.setSendCode(batchCode);
                entity.setSendVehicleBizId(bizId);
                entity.setSendDetailBizId(detailBizId);
                entity.setCreateTime(now);
                entity.setUpdateTime(now);
                entity.setCreateUserErp(userErp);
                entity.setCreateUserName(userName);
                entity.setUpdateUserErp(userErp);
                entity.setUpdateUserName(userName);
                jySendCodeEntityList.add(entity);
            }
            jyVehicleSendRelationService.saveSealSendCode(jySendCodeEntityList);
        } catch (Exception e) {
            log.error("传站摆渡封车存储批次号异常",e);
        }

    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.saveSealVehicle", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> saveSealVehicle(SealVehicleReq sealVehicleReq) {
    	return jyAppDataSealService.savePageData(sealVehicleReq);
    }

    private void updateTaskStatus(SealVehicleReq sealVehicleReq, SealCarDto sealCarDto) {
        this.updateTaskStatus(sealVehicleReq.getSendVehicleBizId(),
                sealVehicleReq.getSendVehicleDetailBizId(),
                sealVehicleReq.getUser().getUserErp(),
                sealVehicleReq.getUser().getUserName(),
                sealCarDto.getSealCarTime());
    }

    private void updateTaskStatus(String bizId, String detailBizId, String userErp, String userName, String sealCarTime) {
        JyBizTaskSendVehicleDetailEntity taskSendDetail = jyBizTaskSendVehicleDetailService.findByBizId(detailBizId);
        JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(bizId);
        taskSend.setUpdateTime(new Date());
        taskSend.setUpdateUserErp(userErp);
        taskSend.setUpdateUserName(userName);

        taskSendDetail.setSealCarTime(DateHelper.parseDate(sealCarTime, Constants.DATE_TIME_FORMAT));
        taskSendDetail.setUpdateTime(taskSend.getUpdateTime());
        taskSendDetail.setUpdateUserErp(taskSend.getUpdateUserErp());
        taskSendDetail.setUpdateUserName(taskSend.getUpdateUserName());
        sendVehicleTransactionManager.updateTaskStatus(taskSend, taskSendDetail, JyBizTaskSendDetailStatusEnum.SEALED);
    }

    private void sealCarStatusUpdateAndSummary(String bizId, String detailBizId, String userErp, String userName, String sealCarTime,
                                  JyStatisticsSummaryEntity summaryEntity) {
        JyBizTaskSendVehicleDetailEntity taskSendDetail = jyBizTaskSendVehicleDetailService.findByBizId(detailBizId);
        JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(bizId);
        taskSend.setUpdateTime(new Date());
        taskSend.setUpdateUserErp(userErp);
        taskSend.setUpdateUserName(userName);

        taskSendDetail.setSealCarTime(DateHelper.parseDate(sealCarTime, Constants.DATE_TIME_FORMAT));
        taskSendDetail.setUpdateTime(taskSend.getUpdateTime());
        taskSendDetail.setUpdateUserErp(taskSend.getUpdateUserErp());
        taskSendDetail.setUpdateUserName(taskSend.getUpdateUserName());

        sendVehicleTransactionManager.sealCarStatusUpdateAndSummary(taskSend, taskSendDetail, summaryEntity);
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
        if (!StringUtils.isEmpty(sealVehicleReq.getPalletCount())) {
            String palletCount = sealVehicleReq.getPalletCount().trim();
            if (NumberUtils.isDigits(palletCount)) {
                sealCarDto.setPalletCount(Integer.valueOf(palletCount));
            }
        }
        return sealCarDto;
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

            com.jd.tms.basic.dto.CommonDto<TransportResourceDto> commonDto = newSealVehicleService.getTransportResourceByTransCode(validSendCodeReq.getTransportCode());
            if (commonDto == null) {
                invokeResult.setCode(SERVER_ERROR_CODE);
                invokeResult.setMessage("查询运力信息结果为空:" + validSendCodeReq.getTransportCode());
                return invokeResult;
            }

            final InvokeResult<Void> checkBatchCodeResult = newSealVehicleService.checkBatchCode(validSendCodeReq, commonDto.getData());
            if (RESULT_SUCCESS_CODE == checkBatchCodeResult.getCode()) {
//                com.jd.tms.basic.dto.CommonDto<TransportResourceDto> commonDto = newSealVehicleService.getTransportResourceByTransCode(validSendCodeReq.getTransportCode());
//                if (commonDto == null) {
//                    invokeResult.setCode(SERVER_ERROR_CODE);
//                    invokeResult.setMessage("查询运力信息结果为空:" + validSendCodeReq.getSendCode());
//                    return invokeResult;
//                }
                if (commonDto.getData() != null && Constants.RESULT_SUCCESS == commonDto.getCode()) {
                    Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(validSendCodeReq.getSendCode());
                    Integer endNodeId = commonDto.getData().getEndNodeId();
                    if (SealCarSourceEnum.FERRY_SEAL_CAR.getCode().equals(validSendCodeReq.getSealCarSource())
                            && newsealVehicleService.isAirTransport(commonDto.getData())
                            && validSendCodeReq.getTransportCode().startsWith("T")) {
                        Integer createSiteCodeInSendCode = BusinessUtil.getCreateSiteCodeFromSendCode(validSendCodeReq.getSendCode());
                        Integer receiveSiteCodeInSendCode = BusinessUtil.getReceiveSiteCodeFromSendCode(validSendCodeReq.getSendCode());
                        if (Objects.equals(validSendCodeReq.getCurrentOperate().getSiteCode(), createSiteCodeInSendCode)
                                || Objects.equals(validSendCodeReq.getCurrentOperate().getSiteCode(), receiveSiteCodeInSendCode)) {
                            invokeResult.setCode(JdResponse.CODE_OK);
                            invokeResult.setMessage(JdResponse.MESSAGE_OK);
                        } else {
                            invokeResult.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                            invokeResult.setMessage(NewSealVehicleResponse.MESSAGE_TRANSPORT_START_END_RANGE_ERROR);
                        }

                    } else if (receiveSiteCode.equals(endNodeId)) {
                        invokeResult.setCode(JdResponse.CODE_OK);
                        invokeResult.setMessage(JdResponse.MESSAGE_OK);
                    } else {
                        //不分传摆和运力都去校验目的地类型是中转场的时候
                        BaseStaffSiteOrgDto endNodeSite = baseMajorManager.getBaseSiteBySiteId(endNodeId);
                        if (endNodeSite != null && SiteSignTool.supportTemporaryTransfer(endNodeSite.getSiteSign())) {
                            //中转场地流向不一致时，弹窗确认是否继续封车
                            invokeResult.setCode(NewSealVehicleResponse.CODE_DESTINATION_DIFF_ERROR);
                            invokeResult.setMessage(MessageFormat.format(NewSealVehicleResponse.TIPS_TRANSPORT_BATCHCODE_DESTINATION_DIFF_ERROR,endNodeSite.getSiteName()));
                            return invokeResult;
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.checkTransCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<TransportResp> checkTransCode(CheckTransportReq reqcuest) {
        TransportResp transportResp = new TransportResp();
        InvokeResult<TransportResp> invokeResult = new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
        try {
            com.jd.tms.basic.dto.CommonDto<TransportResourceDto> commonDto = newSealVehicleService.getTransportResourceByTransCode(reqcuest.getTransportCode());
            if (commonDto == null) {
                invokeResult.setCode(SERVER_ERROR_CODE);
                invokeResult.setMessage("查询运力信息结果为空");
                return invokeResult;
            }
            if (commonDto.getData() != null && Constants.RESULT_SUCCESS == commonDto.getCode()) {
                TransportResourceDto data = commonDto.getData();
                if(Objects.isNull(data.getStartNodeId()) || reqcuest.getCurrentOperate().getSiteCode() != data.getStartNodeId()) {
                    invokeResult.error(String.format("运力编码始发地[%s]非当前场地", data.getStartNodeName()));
                    return invokeResult;
                }
                String hourStr = data.getSendCarMin() < 10 ? "0" + data.getSendCarHour() : String.valueOf(data.getSendCarHour());
                String minStr = data.getSendCarMin() < 10 ? "0" + data.getSendCarMin() : String.valueOf(data.getSendCarMin());
                transportResp.setSendCarHourMin(String.format("%s:%s", hourStr, minStr));

                Integer endNodeId = data.getEndNodeId();
                transportResp.setTransWay(data.getTransWay());
                transportResp.setTransWayName(data.getTransWayName());
                transportResp.setTransType(data.getTransType());
                transportResp.setTransTypeName(data.getTransTypeName());
                if (reqcuest.getEndSiteId().equals(endNodeId)) {
                    invokeResult.setCode(RESULT_SUCCESS_CODE);
                    invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
                    invokeResult.setData(transportResp);
                } else {
                    //不分传摆和运力都去校验目的地类型是中转场的时候 跳过目的地不一致逻辑
                    BaseStaffSiteOrgDto endNodeSite = baseMajorManager.getBaseSiteBySiteId(endNodeId);
                    if (endNodeSite != null && SiteSignTool.supportTemporaryTransfer(endNodeSite.getSiteSign())) {
                        invokeResult.setCode(RESULT_SUCCESS_CODE);
                        invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
                        invokeResult.setData(transportResp);
                    } else {
                        invokeResult.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                        invokeResult.setMessage(NewSealVehicleResponse.TIPS_RECEIVE_DIFF_ERROR);
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
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InvokeResult<Boolean> updateBoardStatusAndSealCode(com.jd.etms.vos.dto.SealCarDto sealCarCodeOfTms, String batchCode, String operateUserCode, String operateUserName) {

        // 更新批次状态
        InvokeResult<Boolean> invokeResult = new InvokeResult<>(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
        /*if (!jyBizTaskComboardService.updateBoardStatusBySendCode(batchCode, operateUserCode, operateUserName)) {
            invokeResult.setData(Boolean.FALSE);
            invokeResult.setMessage("更新板状态失败！");
            return invokeResult;
        }*/
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.deleteBySendVehicleBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.queryBelongBoardByBarCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request) {
        if (StringUtils.isEmpty(request.getBarCode()) || request.getEndSiteId() == null) {
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
            if ( !request.getEndSiteId().equals(comboardEntity.getEndSiteId().intValue())) {
                log.error("当前包裹流向与封车流向不一致：{}", JsonHelper.toJson(request));
                return new InvokeResult<>(NOT_FIND_BOARD_INFO_CODE, "当前包裹流向与封车流向不一致!");
            }
            boardDto.setSendCode(comboardEntity.getSendCode());
            boardDto.setComboardSource(JyBizTaskComboardSourceEnum.getNameByCode(comboardEntity.getComboardSource()));
            boardDto.setStatus(comboardEntity.getBoardStatus());
            boardDto.setStatusDesc(ComboardStatusEnum.getStatusDesc(comboardEntity.getBoardStatus()));
            boardDto.setBoardCreateTime(comboardEntity.getCreateTime());
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

    @Override
    public String transformLicensePrefixToChinese(String carLicense) {
        try {
            Map<String, String> dictMap = getDictMap("1066", 2, "1066");
            return CarLicenseTransformUtil.transformLicensePrefixToChinese(carLicense, dictMap);
        } catch (Exception e) {
            log.error("transformLicensePrefixToChinese error carLicense[" + carLicense + "]", e);
            return carLicense;
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.cancelSeal", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult cancelSeal(JyCancelSealRequest request) {
        barCodeCheck(request);
        cancelSealRequest cancelParams = assembleCancelParams(request);
        NewSealVehicleResponse response= newsealVehicleService.cancelSeal(cancelParams);
        if (Objects.equals(response.getCode(), JdResponse.CODE_OK)) {
            return new InvokeResult(RESULT_SUCCESS_CODE,response.getMessage());
        }
        return new InvokeResult(SERVER_ERROR_CODE,response.getMessage());
    }

    private void barCodeCheck(JyCancelSealRequest request) {
        if (!ObjectHelper.isNotNull(request.getBarCode())){
            throw new JyBizException("条形码不能为空！");
        }
        if (WaybillUtil.isPackageCode(request.getBarCode()) || BusinessUtil.isBoxcode(request.getBarCode())){
            SendM queryParams =new SendM();
            queryParams.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
            queryParams.setBoxCode(request.getBarCode());
            queryParams.setOperateTime(DateHelper.addHoursByDay(new Date(),-Constants.DOUBLE_ONE));
            List<SendM> sendMList =sendMService.findByParams(queryParams);
            if (CollectionUtils.isEmpty(sendMList)){
                throw new JyBizException("未找到该包裹/箱号的发货记录！");
            }
            request.setBatchCode(sendMList.get(0).getSendCode());

        }
        else if (BusinessUtil.isSendCode(request.getBarCode())){
            request.setBatchCode(request.getBarCode());
        }
        else {
            throw new JyBizException("暂不支持该类型条码，请扫描包裹号、箱号！");
        }
    }

    private cancelSealRequest assembleCancelParams(JyCancelSealRequest request) {
        cancelSealRequest cancelParams = new cancelSealRequest();
        cancelParams.setBatchCode(request.getBatchCode());
        cancelParams.setOperateTime(request.getOperateTime());
        cancelParams.setOperateType(request.getOperateType());
        cancelParams.setOperateUserCode(request.getOperateUserCode());
        return cancelParams;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.getCancelSealInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<JyCancelSealInfoResp> getCancelSealInfo(JyCancelSealRequest request) {
        if (!ObjectHelper.isNotNull(request.getBarCode())){
            throw new JyBizException("条形码不能为空！");
        }
        JyCancelSealInfoResp resp =new JyCancelSealInfoResp();
        if (WaybillUtil.isPackageCode(request.getBarCode()) || BusinessUtil.isBoxcode(request.getBarCode())){
            SendM queryParams =new SendM();
            queryParams.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
            queryParams.setBoxCode(request.getBarCode());
            queryParams.setOperateTime(DateHelper.addHoursByDay(new Date(),-Constants.DOUBLE_ONE));
            List<SendM> sendMList =sendMService.findByParams(queryParams);
            if (CollectionUtils.isEmpty(sendMList)){
                throw new JyBizException("未找到该包裹/箱号的发货记录！");
            }
            resp.setSendCode(sendMList.get(0).getSendCode());
            resp.setCreateTime(sendMList.get(0).getOperateTime());
        }
        else if (BusinessUtil.isBoardCode(request.getBarCode())){
            SendM sendM =sendMService.selectSendByBoardCode(request.getCurrentOperate().getSiteCode(),request.getBarCode(),SendStatusEnum.HAS_BEEN_SENDED.getCode());
            if (!ObjectHelper.isNotNull(sendM)){
                throw new JyBizException("未找到该板号的发货记录！");
            }
            resp.setSendCode(sendM.getSendCode());
            resp.setCreateTime(sendM.getOperateTime());
        }
        else if (BusinessUtil.isSendCode(request.getBarCode())){
            resp.setSendCode(request.getBarCode());
        }
        else {
            throw new JyBizException("暂不支持该类型条码，请扫描包裹号、箱号！");
        }
        if (ObjectHelper.isNotNull(resp.getSendCode())){
            Integer[] siteCodes = BusinessUtil.getSiteCodeBySendCode(resp.getSendCode());
            if (siteCodes[0] == -1 || siteCodes[1] == -1) {
                throw new JyBizException("根据批次号获取始发和目的分拣信息失败，批次号：" + "始发分拣code:" + siteCodes[0] + ",目的分拣Code:" + siteCodes[1]);
            }
            BaseStaffSiteOrgDto createSite = siteService.getSite(siteCodes[0]);
            BaseStaffSiteOrgDto receiveSite = siteService.getSite(siteCodes[1]);
            //始发站点信息的映射
            if(createSite != null){
                resp.setCreateSiteCode(createSite.getSiteCode());
                resp.setCreateSiteName(createSite.getSiteName());
                resp.setCreateSiteType(createSite.getSiteType());
                resp.setCreateSiteSubType(createSite.getSubType());
            }
            //目的站点信息的映射
            if(receiveSite != null){
                resp.setReceiveSiteCode(receiveSite.getSiteCode());
                resp.setReceiveSiteName(receiveSite.getSiteName());
                resp.setReceiveSiteType(receiveSite.getSiteType());
                resp.setReceiveSiteSubType(receiveSite.getSubType());
            }
        }
        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,resp);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.onlineGetTaskSimpleCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<GetTaskSimpleCodeResp> onlineGetTaskSimpleCode(GetTaskSimpleCodeReq request) {
        checkGetTaskSimpleCodeParams(request);
        JyBizTaskSendVehicleDetailEntity detailEntity =taskSendVehicleDetailService.findByBizId(request.getSendVehicleDetailBizId());
        if (ObjectHelper.isEmpty(detailEntity)){
            return new InvokeResult(TASK_NO_FOUND_BY_STATUS_CODE,TASK_NO_FOUND_BY_STATUS_MESSAGE);
        }
        if (ObjectHelper.isNotNull(detailEntity.getTaskSimpleCode())){
            GetTaskSimpleCodeResp resp = new GetTaskSimpleCodeResp();
            resp.setTaskSimpleCode(detailEntity.getTaskSimpleCode());
            com.jd.tms.jdi.dto.CommonDto<TransWorkItemDto> transWorkItemResp = jdiQueryWSManager.queryTransWorkItemBySimpleCode(detailEntity.getTaskSimpleCode());
            if (ObjectHelper.isNotNull(transWorkItemResp) && Constants.RESULT_SUCCESS == transWorkItemResp.getCode()) {
                TransWorkItemDto transWorkItemDto = transWorkItemResp.getData();
                resp.setRouteLineCode(transWorkItemDto.getRouteLineCode());
                resp.setRouteLineName(transWorkItemDto.getRouteLineName());
            }
            return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,resp);
        }
        else {
            checkOperateProgress(detailEntity.getSendVehicleBizId(),request.getSpotBizType());
            //调用运输接口获取，并持久化，返回给客户端
            JdiSealCarQueryDto dto =assembleJdiSealCarQueryDto(detailEntity,request);
            com.jd.tms.jdi.dto.CommonDto<JdiSealCarResponseDto> commonDto =jdiQueryWSManager.querySealCarSimpleCode(dto);
            if (ObjectHelper.isEmpty(commonDto)){
                return new InvokeResult(ONLINE_GET_TASK_SIMPLE_FAIL_CODE,ONLINE_GET_TASK_SIMPLE_FAIL_MESSAGE);
            }
            if (Constants.RESULT_SUCCESS == commonDto.getCode()){
                JyBizTaskSendVehicleDetailEntity detail =new JyBizTaskSendVehicleDetailEntity();
                detail.setBizId(detailEntity.getBizId());
                detail.setUpdateTime(new Date());
                detail.setUpdateUserErp(request.getUser().getUserErp());
                detail.setUpdateUserName(request.getUser().getUserName());
                detail.setTaskSimpleCode(commonDto.getData().getSimpleCode());
                taskSendVehicleDetailService.updateByBiz(detail);

                GetTaskSimpleCodeResp resp = new GetTaskSimpleCodeResp();
                resp.setTaskSimpleCode(commonDto.getData().getSimpleCode());
                com.jd.tms.jdi.dto.CommonDto<TransWorkItemDto> transWorkItemResp = jdiQueryWSManager.queryTransWorkItemBySimpleCode(detail.getTaskSimpleCode());
                if (ObjectHelper.isNotNull(transWorkItemResp) && Constants.RESULT_SUCCESS == transWorkItemResp.getCode()) {
                    TransWorkItemDto transWorkItemDto = transWorkItemResp.getData();
                    resp.setRouteLineCode(transWorkItemDto.getRouteLineCode());
                    resp.setRouteLineName(transWorkItemDto.getRouteLineName());
                }
                return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,resp);
            }
            return new InvokeResult(ONLINE_GET_TASK_SIMPLE_FAIL_CODE,commonDto.getMessage());
        }
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealVehicleServiceImpl.selectBoardByTmsAndInitWeightVolume", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyAppDataSealVo selectBoardByTmsAndInitWeightVolume(SealVehicleInfoReq sealVehicleInfoReq, JyAppDataSealVo jyAppDataSealVo) {
        try {
            if (jyAppDataSealVo == null) {
                jyAppDataSealVo = new JyAppDataSealVo();
            }
            // 获取暂存批次信息
            List<String> selectSendCode = new ArrayList<>();
            List<JyAppDataSealSendCodeVo> appDataSealSendCodeVos = new ArrayList<>();
            if (!CollectionUtils.isEmpty(jyAppDataSealVo.getSendCodeList())) {
                selectSendCode = jyAppDataSealVo.getSendCodeList().stream().map(JyAppDataSealSendCodeVo::getSendCode).collect(Collectors.toList());
                appDataSealSendCodeVos = jyAppDataSealVo.getSendCodeList();
            }
            BigDecimal weight = jyAppDataSealVo.getWeight() == null ? new BigDecimal(0) : jyAppDataSealVo.getWeight();
            BigDecimal volume = jyAppDataSealVo.getVolume() == null ? new BigDecimal(0) : jyAppDataSealVo.getVolume();
            
            // 校验当前任务是否存在暂存数据，如果不存在暂存数据，则自动选择板号
            List<BoardLoadDto> boardList = jdiBoardLoadWSManager.queryBoardLoad(assembleBoardLoadDto(sealVehicleInfoReq));
            if (CollectionUtils.isEmpty(boardList)) {
                return jyAppDataSealVo;
            }

            HashSet<String> sendCodeForQuery = new HashSet<>();
            for (BoardLoadDto boardLoadDto : boardList) {
                sendCodeForQuery.add(boardLoadDto.getBatchCode());
            }
            // 查询有效的批次
            JyBizTaskComboardEntity querySendCode = new JyBizTaskComboardEntity();
            querySendCode.setSendCodeList(new ArrayList<>(sendCodeForQuery));
            List<JyBizTaskComboardEntity> taskList = jyBizTaskComboardService.listBoardTaskBySendCode(querySendCode);
            List<String> sendCodeNotDel = taskList.stream().map(JyBizTaskComboardEntity::getSendCode).collect(Collectors.toList());
            
            //需要追加的批次
            HashSet<String> boardCodeAdd = new HashSet<>();
            HashMap<String, String> boardSendCodeMap = new HashMap<>();
            for (BoardLoadDto boardLoadDto : boardList) {
                // 只操作完成装车的板
                if (!LOADING_COMPLETEDC.equals(boardLoadDto.getBoardStatus())) {
                    continue;
                }
                // 操作了删除
                if (!sendCodeNotDel.contains(boardLoadDto.getBatchCode())) {
                    continue;
                }
                
                if (!selectSendCode.contains(boardLoadDto.getBatchCode())) {
                    boardCodeAdd.add(boardLoadDto.getBatchCode());
                    boardSendCodeMap.put(boardLoadDto.getBoardCode(),boardLoadDto.getBatchCode());
                }
            }

            if(CollectionUtils.isNotEmpty(boardCodeAdd)) {
                List<JyComboardAggsEntity> aggs = jyComboardAggsService.queryComboardAggs(new ArrayList<>(boardCodeAdd));
                for (JyComboardAggsEntity aggsEntity : aggs) {
                    JyAppDataSealSendCodeVo vo = new JyAppDataSealSendCodeVo();
                    vo.setSendCode(boardSendCodeMap.get(aggsEntity.getBoardCode()));
                    if (aggsEntity.getWeight() != null) {
                        vo.setWeight(aggsEntity.getWeight());
                        weight.add(aggsEntity.getWeight());
                    }
                    if (aggsEntity.getVolume() != null) {
                        vo.setVolume(aggsEntity.getVolume());
                        volume.add(aggsEntity.getVolume());
                    }
                    appDataSealSendCodeVos.add(vo);
                }
            }
            jyAppDataSealVo.setSendCodeList(appDataSealSendCodeVos);
            jyAppDataSealVo.setWeight(weight);
            jyAppDataSealVo.setVolume(volume);
            return jyAppDataSealVo;
        }catch (Exception e) {
            log.error("自动选择板号失败，request: {}", JsonHelper.toJson(sealVehicleInfoReq),e);
            return jyAppDataSealVo;
        }
    }
    
    
    private BoardLoadDto assembleBoardLoadDto(SealVehicleInfoReq sealVehicleInfoReq) {
        BoardLoadDto boardLoadDto = new BoardLoadDto();
        // 查询发货主任务信息
        JyBizTaskSendVehicleEntity sendVehicle = jyBizTaskSendVehicleService.findByBizId(sealVehicleInfoReq.getSendVehicleBizId());
        // 查询发货子任务信息
        JyBizTaskSendVehicleDetailEntity sendVehicleDetail = jyBizTaskSendVehicleDetailService.findByBizId(sealVehicleInfoReq.getSendVehicleDetailBizId());
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(sendVehicleDetail.getEndSiteId().intValue());
        if (siteInfo != null) {
            boardLoadDto.setEndNodeCode(siteInfo.getDmsSiteCode());
        }
        boardLoadDto.setTransWorkCode(sendVehicle.getTransWorkCode());
        return boardLoadDto;
    }

    private void checkGetTaskSimpleCodeParams(GetTaskSimpleCodeReq request) {
        if (ObjectHelper.isEmpty(request.getSpotBizType())){
            throw new JyBizException("参数错误：spotBizType为空！");
        }
        if (ObjectHelper.isEmpty(request.getSendVehicleDetailBizId())){
            throw new JyBizException("参数错误：任务明细bizId为空！");
        }
        if (CollectionUtils.isEmpty(request.getImgUrlList())){
            throw new JyBizException("参数错误：imgUrlList为空！");
        }
    }

    private void checkOperateProgress(String sendVehicleBizId,Integer spotBizType) {
        //加个降级开关的逻辑
        JySendAggsEntity sendAgg = sendAggService.getVehicleSendStatistics(sendVehicleBizId);
        BigDecimal operateProgress =
            SpotBizTypeEnum.SPOT_CHECK_TYPE_C.getCode().equals(spotBizType) ?
            jySendVehicleService.calculateOperateProgress(sendAgg,false)
            :jySendVehicleServiceTys.calculateOperateProgress(sendAgg,false);
        if (ObjectHelper.isNotNull(operateProgress)){
            log.info("拍照上传获取任务简码-计算装车进度：{}",operateProgress.doubleValue());
            if (operateProgress.compareTo(new BigDecimal(dmsConfigManager.getPropertyConfig().getOnlineGetTaskSimpleCodeThreshold()))<0){
                throw new JyBizException("装载率不足50%，无法拍照获取任务简码!");
            }
        }
    }

    private JdiSealCarQueryDto assembleJdiSealCarQueryDto(JyBizTaskSendVehicleDetailEntity detailEntity,GetTaskSimpleCodeReq req) {
        BaseStaffSiteOrgDto startSite =baseMajorManager.getBaseSiteBySiteId(detailEntity.getStartSiteId().intValue());
        BaseStaffSiteOrgDto endSite =baseMajorManager.getBaseSiteBySiteId(detailEntity.getEndSiteId().intValue());

        JdiSealCarQueryDto dto =new JdiSealCarQueryDto();
        dto.setTransWorkItemCode(detailEntity.getTransWorkItemCode());
        dto.setOperateSiteCode(startSite.getDmsSiteCode());
        dto.setBeginNodeCode(startSite.getDmsSiteCode());
        dto.setEndNodeCode(endSite.getDmsSiteCode());
        dto.setOperatorCode(req.getUser().getUserErp());
        dto.setLoadCarUrl(req.getImgUrlList());
        dto.setTransType(detailEntity.getLineType());
        return dto;
    }

    public Map<String,String> getDictMap(String parentCode, int dictLevel, String dictGroup) {
        if(StringUtils.isNotEmpty(parentCode) && StringUtils.isNotEmpty(dictGroup)){
            try {
                List<BasicDictDto>  list = basicQueryWSManager.getDictList(parentCode, dictLevel, dictGroup);
                if(CollectionUtils.isNotEmpty(list)){
                    Map<String,String> result = new HashMap<>();
                    for (BasicDictDto item : list) {
                        if(item == null || item.getDictCode() == null){
                            continue;
                        }
                        result.put(item.getDictCode(), item.getDictName());
                    }
                    if(!result.isEmpty()){
                        return result;
                    }
                }
            } catch (Exception e) {
                log.error("获取基础资料车牌归属区号-汉字映射关系异常", e);
            }
        }
        return CarLicenseTransformUtil.numToChinese;
    }



    //修改状态并落封车汇总数据
    private void updateTaskStatusSealAndSummary(SealVehicleReq sealVehicleReq, List<String> batchCodes, SealCarDto sealCarDto) {
        //航空计划
        Date time = new Date();
        JyBizTaskSendAviationPlanEntity aviationPlanEntity = new JyBizTaskSendAviationPlanEntity();
        aviationPlanEntity.setUpdateTime(time);
        aviationPlanEntity.setUpdateUserErp(sealVehicleReq.getUser().getUserErp());
        aviationPlanEntity.setUpdateUserName(sealVehicleReq.getUser().getUserName());
        aviationPlanEntity.setTaskStatus(JyBizTaskSendDetailStatusEnum.SEALED.getCode());
        aviationPlanEntity.setBizId(sealVehicleReq.getSendVehicleBizId());
        aviationPlanEntity.setStartSiteId(sealVehicleReq.getCurrentOperate().getSiteCode());
        //发货任务主表
        JyBizTaskSendVehicleDetailEntity taskSendDetail = jyBizTaskSendVehicleDetailService.findByBizId(sealVehicleReq.getSendVehicleDetailBizId());
        JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(sealVehicleReq.getSendVehicleBizId());
        taskSend.setUpdateTime(time);
        taskSend.setUpdateUserErp(sealVehicleReq.getUser().getUserErp());
        taskSend.setUpdateUserName(sealVehicleReq.getUser().getUserName());
        //发货任务子表
        taskSendDetail.setSealCarTime(DateHelper.parseDate(sealCarDto.getSealCarTime(), Constants.DATE_TIME_FORMAT));
        taskSendDetail.setUpdateTime(time);
        taskSendDetail.setUpdateUserErp(taskSend.getUpdateUserErp());
        taskSendDetail.setUpdateUserName(taskSend.getUpdateUserName());
        //航空任务封车汇总
        JyStatisticsSummaryEntity summaryEntity = new JyStatisticsSummaryEntity(
                sealVehicleReq.getSendVehicleBizId(), BusinessKeyTypeEnum.JY_SEND_TASK.getCode(),
                sealVehicleReq.getCurrentOperate().getSiteCode(), SummarySourceEnum.SEAL.getCode());
        summaryEntity.setWeight(sealVehicleReq.getWeight());
        summaryEntity.setVolume(sealVehicleReq.getVolume());
        summaryEntity.setItemNum(sealVehicleReq.getItemNum());
        summaryEntity.setSealBatchCodeNum(batchCodes.size());
        summaryEntity.setSubBusinessNum(Constants.NUMBER_ONE);
        summaryEntity.setTransportCode(sealVehicleReq.getTransportCode());
        summaryEntity.setCreateTime(time);
        summaryEntity.setUpdateTime(time);
        summaryEntity.setCreateUserErp(sealVehicleReq.getUser().getUserErp());
        summaryEntity.setCreateUserName(sealVehicleReq.getUser().getUserName());
        summaryEntity.setUpdateUserErp(sealVehicleReq.getUser().getUserErp());
        summaryEntity.setUpdateUserName(sealVehicleReq.getUser().getUserName());
        summaryEntity.setDepartTime(sealVehicleReq.getDepartureTimeStr());

        sendVehicleTransactionManager.updateAviationTaskStatus(aviationPlanEntity, taskSend, taskSendDetail, summaryEntity, JyBizTaskSendDetailStatusEnum.SEALED);
    }


    @Override
    public InvokeResult<Void> shuttleTaskSealCar(ShuttleTaskSealCarReq request) {
        final String methodDesc = "JySealVehicleServiceImpl:shuttleTaskSealCar:摆渡任务封车：";
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        if(!jySeaCarlCacheService.lockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.VEHICLE.getCode())) {
            res.error("多人操作中，请稍后重试");
            return res;
        }
        try{
            if(log.isInfoEnabled()) {
                log.info("{}request={}", methodDesc, JsonHelper.toJson(request));
            }
            //封车任务校验
            if(!this.sealCarShuttleTaskCheck(request, res)) {
                return res;
            }
            //封车批次汇总
            List<String> sealAllBatchCodes = this.getSealBatchCodes(request);
            if(CollectionUtils.isEmpty(sealAllBatchCodes)) {
                res.error("封车批次为空");
                return res;
            }
            //封签码汇总
            List<String> allSealCodes = this.getAllSealCodes(request);

            SealCarDto sealCarDto = this.convertSealCarDto(request, sealAllBatchCodes, allSealCodes);
            //封车执行
            List<SealCarDto> sealCarDtoList = Arrays.asList(sealCarDto);
            Map<String, String> emptyBatchCode = new HashMap<>();
            NewSealVehicleResponse sealResp = newSealVehicleService.doSealCarWithVehicleJob(sealCarDtoList,emptyBatchCode);

            if (!Objects.isNull(sealResp) && JdResponse.CODE_OK.equals(sealResp.getCode())) {
                //封签码暂存
                if(org.apache.commons.collections.CollectionUtils.isNotEmpty(request.getScanSealCodes())){
                    List<JySendSealCodeEntity> entityList = this.generateSendSealCodeList(request);
                    jySendSealCodeService.addBatch(entityList);
                }
                //发车任务状态修改
                JyStatisticsSummaryEntity summaryEntity = this.generateSealSummaryEntity(request, sealAllBatchCodes);
                this.sealCarStatusUpdateAndSummary(request.getBizId(), request.getDetailBizId(), request.getUser().getUserErp(), request.getUser().getUserName(), sealCarDto.getSealCarTime(), summaryEntity);
                //任务明细绑定批次号重置
                if (ObjectHelper.isNotNull(sealResp.getData())){
                    List<com.jd.etms.vos.dto.SealCarDto> successSealCarList =(List<com.jd.etms.vos.dto.SealCarDto>)sealResp.getData();

                    this.saveSealSendCode(successSealCarList.get(0).getBatchCodes(), request.getBizId(), request.getDetailBizId(), request.getUser().getUserErp(), request.getUser().getUserName());
                }
                return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
            }
            res.setCode(sealResp.getCode());
            res.setMessage(sealResp.getMessage());
            return res;
        }catch (Exception e) {
            log.error("{}摆渡封车服务异常，request={],errMsg={}", methodDesc, JsonHelper.toJson(request), e.getMessage(), e);
            res.error("摆渡封车服务异常");
            return res;
        }finally {
            //释放锁
            jySeaCarlCacheService.unlockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.VEHICLE.getCode());
        }
    }

    private JyStatisticsSummaryEntity generateSealSummaryEntity(ShuttleTaskSealCarReq request, List<String> batchCodes){

        String businessKey ;
        String businessKeyType ;
        if(JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode().equals(request.getPost())) {
            businessKey = request.getDetailBizId();
            businessKeyType = BusinessKeyTypeEnum.JY_SEND_TASK_DETAIL.getCode();
        }else {
            businessKey = request.getBizId();
            businessKeyType = BusinessKeyTypeEnum.JY_SEND_TASK.getCode();
        }
        Date time = new Date();
        JyStatisticsSummaryEntity summaryEntity = new JyStatisticsSummaryEntity(
                businessKey, businessKeyType,
                request.getCurrentOperate().getSiteCode(), SummarySourceEnum.SEAL.getCode());
        summaryEntity.setWeight(request.getWeight());
        summaryEntity.setVolume(request.getVolume());
        summaryEntity.setItemNum(request.getItemNum());
        summaryEntity.setSealBatchCodeNum(batchCodes.size());
        summaryEntity.setSubBusinessNum(Constants.NUMBER_ONE);
        summaryEntity.setTransportCode(request.getTransportCode());
        summaryEntity.setCreateTime(time);
        summaryEntity.setUpdateTime(time);
        summaryEntity.setCreateUserErp(request.getUser().getUserErp());
        summaryEntity.setCreateUserName(request.getUser().getUserName());
        summaryEntity.setUpdateUserErp(request.getUser().getUserErp());
        summaryEntity.setUpdateUserName(request.getUser().getUserName());
        summaryEntity.setDepartTime(request.getDepartureTimeStr());

        if(JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode().equals(request.getPost())) {
            summaryEntity.setSealBindAviationTaskNum(jyBizTaskBindService.countBind(request.getDetailBizId()));
        }
        return summaryEntity;
    }
    //摆渡封车-前置任务校验
    private boolean sealCarShuttleTaskCheck(ShuttleTaskSealCarReq request, InvokeResult<Void> res) {
        if(Objects.isNull(request) || Objects.isNull(res)) {
            return true;
        }
        res.success();
        //校验被绑摆渡任务是否封车，已封车禁绑
        JyBizTaskSendVehicleDetailEntity sendTaskDetailInfo = jyBizTaskSendVehicleDetailService.findByBizId(request.getDetailBizId());
        if(Objects.isNull(sendTaskDetailInfo)) {
            res.error("未找到派车任务.bizId:" + request.getBizId());
            return false;
        }
        if(JyBizTaskSendStatusEnum.SEALED.getCode().equals(sendTaskDetailInfo.getVehicleStatus())) {
            res.error("该流向已封车不支持绑定");
            return false;
        }
        if(JyBizTaskSendStatusEnum.CANCEL.getCode().equals(sendTaskDetailInfo.getVehicleStatus())) {
            res.error("已作废不支持绑定");
            return false;
        }
        if(com.jd.jsf.gd.util.StringUtils.isBlank(request.getVehicleNumber())) {
            JyBizTaskSendVehicleEntity sendTaskInfo = jyBizTaskSendVehicleService.findByBizId(request.getBizId());
            if(Objects.isNull(sendTaskInfo) || com.jd.jsf.gd.util.StringUtils.isEmpty(sendTaskInfo.getTransWorkCode())) {
                res.error("没有找到该任务派车信息");
                return false;
            }
            String vehicleNumber = this.getVehicleNumber(sendTaskInfo.getTransWorkCode());
            if(com.jd.jsf.gd.util.StringUtils.isNotBlank(vehicleNumber)) {
                request.setVehicleNumber(vehicleNumber);
            }
        }
        return true;
    }
    /**
     * TW号获取派车任务车牌号
     */
    private String getVehicleNumber(String transWorkCode){
        TransWorkBillDto transWorkBillDto = jdiQueryWSManager.queryTransWork(transWorkCode);
        if(!Objects.isNull(transWorkBillDto) && com.jd.jsf.gd.util.StringUtils.isNotBlank(transWorkBillDto.getVehicleNumber())) {
            return transWorkBillDto.getVehicleNumber();
        }
        if(log.isInfoEnabled()) {
            log.info("根据派车任务编码【{}】获取车牌号为空,运输返回={}", transWorkCode, JsonHelper.toJson(transWorkBillDto));
        }
        return StringUtils.EMPTY;
    }
    //摆渡封车-批次号获取
    private List<String> getSealBatchCodes(ShuttleTaskSealCarReq request) {
        List<String> res = new ArrayList<>();
        //PDA扫描的批次号
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(request.getScanBatchCodes())) {
            res.addAll(request.getScanBatchCodes());
        }
        if(JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode().equals(request.getPost())) {
            //空铁-任务绑定的批次号
            List<String> bindBatchCodes = this.queryBindTaskBatchCodes(request.getBizId(), request.getDetailBizId(), request.getCurrentOperate().getSiteCode());
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(bindBatchCodes)) {
                res.addAll(bindBatchCodes);
            }
        }
        //同批次可以封多次摆渡，不做批次封车校验

        return res;
    }
    //摆渡封车-绑定航空任务批次号获取
    private List<String> queryBindTaskBatchCodes(String bizId, String detailBizId, int siteCode) {
        JyBizTaskBindEntityQueryCondition condition = new JyBizTaskBindEntityQueryCondition();
        condition.setBizId(bizId);
        condition.setDetailBizId(detailBizId);
        condition.setOperateSiteCode(siteCode);
        condition.setType(TaskBindTypeEnum.BIND_TYPE_AVIATION.getCode());
        List<JyBizTaskBindEntity> bindEntityList = jyBizTaskBindService.queryBindTaskList(condition);
        if(org.apache.commons.collections.CollectionUtils.isEmpty(bindEntityList)) {
            return null;
        }
        List<String> bindDetailBizIdList = bindEntityList.stream().map(JyBizTaskBindEntity::getBindDetailBizId).collect(Collectors.toList());
        return jyVehicleSendRelationService.findSendCodesByDetailBizIds(bindDetailBizIdList);
    }
    //摆渡封车-封签码获取
    private List<String> getAllSealCodes(ShuttleTaskSealCarReq request) {
        HashSet<String> allSealCodes = new HashSet();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(request.getScanSealCodes())) {
            allSealCodes.addAll(request.getScanSealCodes());
        }
        //车上已经封了的封签号
        List<String> sealCodes = jySendSealCodeService.selectSealCodeByBizId(request.getBizId());
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(sealCodes)) {
            allSealCodes.addAll(sealCodes);
        }
        return allSealCodes.stream().collect(Collectors.toList());
    }
    //摆渡封车-封车实体转换
    private SealCarDto convertSealCarDto(ShuttleTaskSealCarReq request, List<String> batchCodes, List<String> sealCodes) {
        SealCarDto sealCarDto = BeanUtils.copy(request, SealCarDto.class);
        sealCarDto.setSealCarTime(TimeUtils.date2string(new Date(), yyyy_MM_dd_HH_mm_ss));
        sealCarDto.setSealSiteId(request.getCurrentOperate().getSiteCode());
        sealCarDto.setSealSiteName(request.getCurrentOperate().getSiteName());
        sealCarDto.setSealUserCode(request.getUser().getUserErp());
        sealCarDto.setSealUserName(request.getUser().getUserName());
        sealCarDto.setBatchCodes(batchCodes);
        sealCarDto.setSealCodes(sealCodes);
        return sealCarDto;
    }
    //摆渡封车-批次号处理
    private List<JySendSealCodeEntity> generateSendSealCodeList(ShuttleTaskSealCarReq sealCarReq) {
        List<JySendSealCodeEntity> entityList = new ArrayList<>();
        Date now = new Date();
        for (String sealCode : sealCarReq.getScanSealCodes()) {
            JySendSealCodeEntity jySendSealCodeEntity = new JySendSealCodeEntity();
            jySendSealCodeEntity.setSealCode(sealCode);
            jySendSealCodeEntity.setSendVehicleBizId(sealCarReq.getBizId());
            jySendSealCodeEntity.setOperateSiteId(Long.valueOf(sealCarReq.getCurrentOperate().getSiteCode()));
            jySendSealCodeEntity.setOperateTime(now);
            jySendSealCodeEntity.setCreateTime(now);
            jySendSealCodeEntity.setUpdateTime(now);
            jySendSealCodeEntity.setCreateUserErp(sealCarReq.getUser().getUserErp());
            jySendSealCodeEntity.setCreateUserName(sealCarReq.getUser().getUserName());
            entityList.add(jySendSealCodeEntity);
        }
        //航空摆渡发车关注流向维度数据  todo  批次号模型怎么兼容
        if(JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode().equals(sealCarReq.getPost())) {
            for (String sealCode : sealCarReq.getScanSealCodes()) {
                JySendSealCodeEntity jySendSealCodeEntity = new JySendSealCodeEntity();
                jySendSealCodeEntity.setSealCode(sealCode);
                jySendSealCodeEntity.setSendVehicleBizId(sealCarReq.getDetailBizId());
                jySendSealCodeEntity.setOperateSiteId(Long.valueOf(sealCarReq.getCurrentOperate().getSiteCode()));
                jySendSealCodeEntity.setOperateTime(now);
                jySendSealCodeEntity.setCreateTime(now);
                jySendSealCodeEntity.setUpdateTime(now);
                jySendSealCodeEntity.setCreateUserErp(sealCarReq.getUser().getUserErp());
                jySendSealCodeEntity.setCreateUserName(sealCarReq.getUser().getUserName());
                entityList.add(jySendSealCodeEntity);
            }
        }
        return entityList;
    }
}
