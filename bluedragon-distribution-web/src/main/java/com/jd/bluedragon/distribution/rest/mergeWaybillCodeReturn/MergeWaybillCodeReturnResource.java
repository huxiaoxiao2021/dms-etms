package com.jd.bluedragon.distribution.rest.mergeWaybillCodeReturn;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain.MergeWaybillCodeReturn;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain.MergeWaybillMessage;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service.MergeWaybillCodeReturnService;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.fastjson.JSON;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.reverse.WaybillReturnSignatureApi;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureResult;
import com.jd.ldop.center.api.reverse.dto.WaybillReturnSignatureDTO;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * @ClassName: MergeWaybillCodeReturnResource
 * @Description: 签单返回合单
 * @author: hujiping
 * @date: 2018/9/15 17:07
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class MergeWaybillCodeReturnResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private WaybillReturnSignatureApi waybillReturnSignatureApi;

    @Autowired
    private MergeWaybillCodeReturnService mergeWaybillCodeReturnService;

    @Autowired
    @Qualifier("mergeWaybillReturnMQ")
    private DefaultJMQProducer mergeWaybillReturnMQ;

    @GET
    @Path("/getNewReturnWaybillCode/{waybillCode}/{secondWaybillCode}")
    public JdResponse getNewReturnWaybillCode(@PathParam("waybillCode") String waybillCode,
                                                      @PathParam("secondWaybillCode") String secondWaybillCode){
        JdResponse result = new JdResponse();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        if(!SerialRuleUtil.isMatchAllWaybillCode(waybillCode) ||
                !SerialRuleUtil.isMatchAllWaybillCode(secondWaybillCode)){
            this.logger.error(InvokeResult.PARAM_ERROR);
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        try {
            ResponseDTO<ReturnSignatureMessageDTO>  responseDto = waybillReturnSignatureApi.queryReturnSignatureMessage(waybillCode);
            ResponseDTO<ReturnSignatureMessageDTO>  secondResponseDto = waybillReturnSignatureApi.queryReturnSignatureMessage(secondWaybillCode);
            if(responseDto!=null && responseDto.getData()!=null &&
                    secondResponseDto!=null && secondResponseDto.getData()!=null){
                Boolean flage = false;
                if(!waybillCode.equals(secondWaybillCode)){
                    flage = mergeWaybillCodeReturnService.compareWith(responseDto.getData(),secondResponseDto.getData());
                    if(!flage){
                        result.setCode(InvokeResult.RESULT_MULTI_ERROR);
                    }
                }
            }else{
                result.setCode(responseDto.getStatusCode());
                result.setMessage(responseDto.getStatusMessage());
            }
        } catch (Exception e) {
            this.logger.error("根据运单号"+waybillCode+"调用外单接口失败",e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }

    @POST
    @Path("/submitMergedWaybillCode")
    public JdResponse mergeWaybill(MergeWaybillCodeReturn mergeWaybillCodeReturn){

        JdResponse result = new JdResponse();
        String newWaybillCode = null;
        //提交合单
        ResponseDTO<ReturnSignatureResult> returnDto = null;
        WaybillReturnSignatureDTO dto = new WaybillReturnSignatureDTO();
        try {
            dto.setOperateTime(new Date(mergeWaybillCodeReturn.getOperateTime()));
            dto.setOperateUnitId(mergeWaybillCodeReturn.getOperateUnitId());
            dto.setOperateUnitType(mergeWaybillCodeReturn.getOperateUnitType());
            dto.setOperateUser(mergeWaybillCodeReturn.getOperatorName());
            dto.setOperateUserId(mergeWaybillCodeReturn.getOperateUserId());
            dto.setWaybillCodeList(mergeWaybillCodeReturn.getWaybillCodeList());
            returnDto = waybillReturnSignatureApi.waybillReturnSignature(dto);
            if(returnDto!=null){
                if(returnDto.getStatusCode()==0){
                    result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                    if(returnDto.getData()!=null&&returnDto.getData().getReturnWaybillCode()!=null){
                        newWaybillCode = returnDto.getData().getReturnWaybillCode();
                        //给运单发消息
                        MergeWaybillMessage message = new MergeWaybillMessage();
                        message.setNewWaybillCode(newWaybillCode);
                        message.setWaybillCodeList(mergeWaybillCodeReturn.getWaybillCodeList());
                        message.setOperateTime(mergeWaybillCodeReturn.getOperateTime());
                        message.setOperatorName(mergeWaybillCodeReturn.getOperatorName());
                        message.setOperatorNo(mergeWaybillCodeReturn.getOperatorNo());
                        message.setOperatorUserId(mergeWaybillCodeReturn.getOperateUserId());
                        message.setSiteCode(mergeWaybillCodeReturn.getOperateUnitId());
                        message.setSiteName(mergeWaybillCodeReturn.getSiteName());
                        this.logger.info("发送MQ[" + mergeWaybillReturnMQ.getTopic() + "],业务ID[" + newWaybillCode + "],消息主题: " + JSON.toJSONString(message));
                        mergeWaybillReturnMQ.sendOnFailPersistent(newWaybillCode,JSON.toJSONString(message));
                        //发全程跟踪
                        mergeWaybillCodeReturnService.sendTrace(message);
                    }
                }else{
                    this.logger.error(returnDto.getStatusMessage());
                    result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                    result.setMessage(returnDto.getStatusMessage());
                }
            }
        }catch (Exception e){
            this.logger.error("通过旧单号集合获取新单号失败"+ JSON.toJSONString(mergeWaybillCodeReturn.getWaybillCodeList()),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}

