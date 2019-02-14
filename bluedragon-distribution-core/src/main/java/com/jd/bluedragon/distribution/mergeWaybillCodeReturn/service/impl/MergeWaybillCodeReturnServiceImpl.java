package com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain.MergeWaybillCodeReturnRequest;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain.MergeWaybillMessage;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service.MergeWaybillCodeReturnService;
import com.jd.bluedragon.distribution.signAndReturn.domain.MergedWaybill;
import com.jd.bluedragon.distribution.signAndReturn.domain.SignReturnPrintM;
import com.jd.bluedragon.distribution.signAndReturn.service.MergedWaybillService;
import com.jd.bluedragon.distribution.signAndReturn.service.SignReturnService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.DeliverInfoDto;
import com.jd.ldop.basic.dto.BasicTraderReturnDTO;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureResult;
import com.jd.ldop.center.api.reverse.dto.WaybillReturnSignatureDTO;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @ClassName: MergeWaybillCodeReturnServiceImpl
 * @Description: 签单返回合单
 * @author: hujiping
 * @date: 2018/9/17 20:32
 */
@Service("MergeWaybillCodeReturnService")
public class MergeWaybillCodeReturnServiceImpl implements MergeWaybillCodeReturnService{

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private TaskService taskService;

    @Autowired
    private LDOPManager lDOPManager;

    @Autowired
    @Qualifier("mergeWaybillReturnMQ")
    private DefaultJMQProducer mergeWaybillReturnMQ;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private WaybillQueryApi waybillQueryApi;

    @Autowired
    private SignReturnService signReturnService;

    @Autowired
    private MergedWaybillService mergedWaybillService;

    /**
     * 判断是否相同
     * @param data
     * @param secondData
     * @return
     */
    @Override
    public Boolean compareWith(ReturnSignatureMessageDTO data, ReturnSignatureMessageDTO secondData) {
        if (data.getRealName().equals(secondData.getRealName())) {
            if (data.getPhone().equals(secondData.getPhone())) {
                if (data.getAddress().getAddress().equals(secondData.getAddress().getAddress())) {
                    if (data.getTraderCode().equals(secondData.getTraderCode())) {
                        if (data.getTraderName().equals(secondData.getTraderName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 根据旧运单号合单生成新的运单号
     * @param dto
     */
    @Override
    @JProfiler(jKey = "DMS.MERGEWAYBILLCODERETURN.MergeWaybillCodeReturnServiceImpl.mergeWaybillCode",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public JdResponse mergeWaybillCode(WaybillReturnSignatureDTO dto,MergeWaybillCodeReturnRequest mergeWaybillCodeReturnRequest) {
        JdResponse result = new JdResponse();
        String newWaybillCode = null;
        ResponseDTO<ReturnSignatureResult> returnDto = lDOPManager.waybillReturnSignature(dto);
        if(returnDto!=null){
            if(returnDto.getStatusCode()==0){
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                if(returnDto.getData()!=null&&returnDto.getData().getReturnWaybillCode()!=null){
                    newWaybillCode = returnDto.getData().getReturnWaybillCode();
                    //给运单发消息
                    MergeWaybillMessage message = new MergeWaybillMessage();
                    message.setNewWaybillCode(newWaybillCode);
                    message.setWaybillCodeList(mergeWaybillCodeReturnRequest.getWaybillCodeList());
                    message.setOperateTime(mergeWaybillCodeReturnRequest.getOperateTime());
                    message.setOperatorName(mergeWaybillCodeReturnRequest.getOperatorName());
                    message.setOperatorNo(mergeWaybillCodeReturnRequest.getOperatorNo());
                    message.setOperatorUserId(mergeWaybillCodeReturnRequest.getOperateUserId());
                    message.setSiteCode(mergeWaybillCodeReturnRequest.getOperateUnitId());
                    message.setSiteName(mergeWaybillCodeReturnRequest.getSiteName());
                    //数据落库
                    insert(mergeWaybillCodeReturnRequest, newWaybillCode);
                    //给运单发mq
                    this.logger.info("发送MQ[" + mergeWaybillReturnMQ.getTopic() + "],业务ID[" + newWaybillCode + "],消息主题: " + com.jd.fastjson.JSON.toJSONString(message));
                    mergeWaybillReturnMQ.sendOnFailPersistent(newWaybillCode, com.jd.fastjson.JSON.toJSONString(message));
                    //发全程跟踪
                    sendTrace(message);
                }
            }else{
                this.logger.error(returnDto.getStatusMessage());
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setMessage(returnDto.getStatusMessage());
            }
        }
        return result;
    }

    /**
     * 数据落库
     * @param mergeWaybillCodeReturnRequest
     * @param newWaybillCode
     */
    private void insert(MergeWaybillCodeReturnRequest mergeWaybillCodeReturnRequest, String newWaybillCode) {
        SignReturnPrintM signReturnPrintM = new SignReturnPrintM();
        List<MergedWaybill> mergedWaybillList = new ArrayList<MergedWaybill>();
        for(String waybillCode : mergeWaybillCodeReturnRequest.getWaybillCodeList()){
            MergedWaybill mergedWaybill = new MergedWaybill();
            mergedWaybill.setWaybillCode(waybillCode);
            mergedWaybill.setIsDelete(1);
            //根据运单号获得妥投时间
            BaseEntity<DeliverInfoDto> entity = waybillQueryApi.getDeliverInfo(waybillCode);
            if(entity != null && entity.getResultCode() == 1 &&
                    entity.getData() != null && entity.getData().getCreateTime() != null){
                mergedWaybill.setDeliveredTime(entity.getData().getCreateTime());
            }
            mergedWaybillList.add(mergedWaybill);
        }
        signReturnPrintM.setMergedWaybillList(mergedWaybillList);
        signReturnPrintM.setMergeCount(mergeWaybillCodeReturnRequest.getWaybillCodeList().size());
        signReturnPrintM.setNewWaybillCode(newWaybillCode);
        signReturnPrintM.setOperateTime(new Date(mergeWaybillCodeReturnRequest.getOperateTime()));
        signReturnPrintM.setOperateUser(mergeWaybillCodeReturnRequest.getOperatorName());
        signReturnPrintM.setCreateSiteName(mergeWaybillCodeReturnRequest.getSiteName());
        signReturnPrintM.setIsDelete(1);
        BaseEntity<BigWaybillDto> entity = waybillQueryManager.getDataByChoice(mergedWaybillList.get(0).getWaybillCode(),
                true, false, false, false);
        if(entity != null && entity.getResultCode() > 0 && entity.getData() != null){
            Waybill waybill = entity.getData().getWaybill();
            if(waybill != null){
                signReturnPrintM.setBusiId(waybill.getBusiId()==null?"":waybill.getBusiId().toString());    //商家编码和商家名称
                signReturnPrintM.setBusiName(waybill.getBusiName());
                com.jd.ldop.basic.dto.ResponseDTO<List<BasicTraderReturnDTO>> responseDTO = baseMinorManager.getBaseTraderReturnListByTraderId(waybill.getBusiId());
                if(responseDTO != null && responseDTO.isSuccess()){
                    List<BasicTraderReturnDTO> returnList = responseDTO.getResult();
                    if(returnList != null && !returnList.isEmpty()){
                        signReturnPrintM.setReturnCycle(returnList.get(0).getReturnCycle()==null?"":returnList.get(0).getReturnCycle().toString()); //返单周期
                    }
                }
            }
        }
        //将数据落库
        signReturnService.add(signReturnPrintM);
        for(MergedWaybill mergedWaybill : mergedWaybillList){
            mergedWaybill.setSignReturnPrintMId(signReturnPrintM.getId());
        }
        mergedWaybillService.batchAdd(mergedWaybillList);
    }

    /**
     * 判断是否可以合单
     * @param waybillCode
     * @param secondWaybillCode
     * @return
     */
    @Override
    public JdResponse checkIsMerge(String waybillCode, String secondWaybillCode) {
        JdResponse result = new JdResponse();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        ResponseDTO<ReturnSignatureMessageDTO>  responseDto = null;
        ResponseDTO<ReturnSignatureMessageDTO>  secondResponseDto = null;
        if(waybillCode.equals(secondWaybillCode)){
            responseDto = lDOPManager.queryReturnSignatureMessage(waybillCode);
            if(responseDto.getStatusCode()==1||responseDto.getStatusCode()==-1){
                result.setCode(responseDto.getStatusCode());
                result.setMessage(responseDto.getStatusMessage());
            }
            return result;
        }
        responseDto = lDOPManager.queryReturnSignatureMessage(waybillCode);
        secondResponseDto = lDOPManager.queryReturnSignatureMessage(secondWaybillCode);
        if(responseDto!=null && responseDto.getData()!=null &&
                secondResponseDto!=null && secondResponseDto.getData()!=null){
            Boolean flage = false;
            flage = compareWith(responseDto.getData(),secondResponseDto.getData());
            if(!flage){
                result.setCode(InvokeResult.RESULT_MULTI_ERROR);
            }
        }else{
            if(secondResponseDto != null){
                result.setCode(secondResponseDto.getStatusCode());
                result.setMessage(secondResponseDto.getStatusMessage());
            }else{
                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            }
        }
        return result;
    }

    /**
     * 发旧单号和新单号全程跟踪
     * @param message
     */
    @Override
    public void sendTrace(MergeWaybillMessage message) {
        //新单号发全程跟踪
        toTask(message);
        //旧单号发全程跟踪
        List<String> WaybillCodeList = message.getWaybillCodeList();
        for(String waybillCode : WaybillCodeList){
            toTask(message, waybillCode,message.getNewWaybillCode());
        }
    }

    private void toTask(MergeWaybillMessage message) {
        Task tTask = new Task();
        tTask.setKeyword1(message.getNewWaybillCode());
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN_NEW));
        tTask.setCreateSiteCode(message.getSiteCode());
        tTask.setCreateTime(new Date(message.getOperateTime()));
//        tTask.setReceiveSiteCode(0);
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN);
        status.setWaybillCode(message.getNewWaybillCode());
        status.setOperateTime(new Date(message.getOperateTime()));
        status.setOperator(message.getOperatorName());
        status.setOperatorId(message.getOperatorUserId());
        status.setRemark(JSON.toJSONString(message.getWaybillCodeList()));
        status.setCreateSiteCode(message.getSiteCode());
        status.setCreateSiteName(message.getSiteName());
        status.setPackageCode(message.getNewWaybillCode());
        tTask.setBody(JsonHelper.toJson(status));
        taskService.add(tTask);
    }

    private void toTask(MergeWaybillMessage message, String waybillCode,String newWaybillCode) {
        Task tTask = new Task();
        tTask.setKeyword1(waybillCode);
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN_OLD));
        tTask.setCreateSiteCode(message.getSiteCode());
        tTask.setCreateTime(new Date(message.getOperateTime()));
//        tTask.setReceiveSiteCode(0);
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN);
        status.setWaybillCode(waybillCode);
        status.setSendCode(newWaybillCode);//将新单号存到sendCode字段中
        status.setOperateTime(new Date(message.getOperateTime()));
        status.setOperator(message.getOperatorName());
        status.setOperatorId(message.getOperatorUserId());
        status.setRemark("签单返回合单");
        status.setCreateSiteCode(message.getSiteCode());
        status.setCreateSiteName(message.getSiteName());
        status.setPackageCode(waybillCode);
        tTask.setBody(JsonHelper.toJson(status));
        taskService.add(tTask);
    }

}
