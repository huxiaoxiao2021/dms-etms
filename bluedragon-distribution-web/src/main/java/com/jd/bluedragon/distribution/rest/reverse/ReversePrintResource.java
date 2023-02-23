package com.jd.bluedragon.distribution.rest.reverse;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.RepeatPrint;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ExchangePrintRequest;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.PopPrintResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionPrintDto;
import com.jd.bluedragon.distribution.message.OwnReverseTransferDomain;
import com.jd.bluedragon.distribution.popPrint.dto.PushPrintRecordDto;
import com.jd.bluedragon.distribution.print.domain.ChangeOrderPrintMq;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.record.entity.DmsHasnoPresiteWaybillMq;
import com.jd.bluedragon.distribution.record.enums.DmsHasnoPresiteWaybillMqOperateEnum;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.bluedragon.distribution.rest.pop.PopPrintResource;
import com.jd.bluedragon.distribution.rest.task.TaskResource;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.distribution.weight.domain.OpeEntity;
import com.jd.bluedragon.distribution.weight.domain.OpeObject;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Collections;
import java.util.Date;


/**
 * 外单逆向换单打印
 * Created by wangtingwei on 14-8-7.
 */
@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class ReversePrintResource {

    private static final Logger log = LoggerFactory.getLogger(ReversePrintResource.class);

    @Autowired
    private ReversePrintService reversePrintService;

    @Autowired
    private TaskResource taskResource;

    @Autowired
    private PopPrintResource popPrintResource;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillCancelService waybillCancelService;

    @Autowired
    @Qualifier("changeWaybillPrintSecondProducer")
    private DefaultJMQProducer changeWaybillPrintSecondProducer;
    
    /**
     * 外单逆向换单打印提交数据
     * @return JSON【{code: message: data:}】
     */
    @POST
    @Path("/reverse/exchange/print")
    @JProfiler(jKey = "DMS.WEB.ReversePrintResource.handlePrint", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> handlePrint(ReversePrintRequest request){
        if(log.isInfoEnabled()) {
            log.info("【逆向换单处理打印数据】:{}", request.toString());
        }
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();
        try{
            reversePrintService.handlePrint(request);
            result.setCode(JdResponse.CODE_OK);
            result.setMessage(JdResponse.MESSAGE_OK);
            result.setData(Boolean.TRUE);
        }
        catch (Throwable e){
            log.error("【逆向换单打印】",e);
            result.setCode(JdResponse.CODE_SERVICE_ERROR);
            result.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            result.setData(Boolean.FALSE);
        }
        return result;
    }

    /**
     * 根据原单号获取对应的新单号
     * 1.自营拒收：新运单规则：T+原运单号。调取运单来源：从运单处获取，调取运单新接口。
     * 2.外单拒收：新运单规则：生成新的V单。调取运单来源：1）从外单获得新外单单号。2）通过新外单单号从运单处调取新外单的信息。
     * 3.售后取件单：新运单规则：生成W单或VY单。调取运单来源：从运单处获取，调取运单新接口。
     * 4.配送异常类订单：新运单规则：T+原运单号,调取运单来源：从运单处获得，调取运单新接口。
     * 5.返单换单：1）新运单规则：F+原运单号  或  F+8位数字,调取运单来源：从运单处获得，调取运单新接口。2）分拣中心集中换单，暂时不做。
     * @param oldWaybillCode 原单号
     * @return
     */
    @GET
    @Path("/reverse/exchange/getNewWaybillCode/{oldWaybillCode}")
    @JProfiler(jKey = "DMS.WEB.ReversePrintResource.getNewWaybillCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<String> getNewWaybillCode(@PathParam("oldWaybillCode") String oldWaybillCode){
        InvokeResult<String> result=new InvokeResult<String>();
        try {
            boolean bool = waybillCancelService.checkWaybillCancelInterceptType99(oldWaybillCode);
            if (bool) {
                result.customMessage(-1, HintService.getHint(HintCodeConstants.WAYBILL_ERROR_RE_PRINT));
                return result;
            }
            result= reversePrintService.getNewWaybillCode(oldWaybillCode, true);
        }catch (Throwable e){
            log.error("[逆向换单获取新单号]",e);
            result.error(e);
        }
        return result;
    }


    /**
     *  根据原单号获取对应的新单号（新）
     * @param oldWaybillCode 原单号
     * @return
     */
    @GET
    @Path("/reverse/exchange/getNewWaybillCode1/{oldWaybillCode}")
    @JProfiler(jKey = "DMS.WEB.ReversePrintResource.getNewWaybillCode1", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<RepeatPrint> getNewWaybillCode1(@PathParam("oldWaybillCode") String oldWaybillCode){
        InvokeResult<RepeatPrint> result=new InvokeResult<RepeatPrint>();
        try {
            boolean bool = waybillCancelService.checkWaybillCancelInterceptType99(oldWaybillCode);
            if (bool) {
                result.customMessage(-1, HintService.getHint(HintCodeConstants.WAYBILL_ERROR_RE_PRINT));
                return result;
            }
           result= reversePrintService.getNewWaybillCode1(oldWaybillCode, true);
        }catch (Throwable e){
            log.error("[逆向换单获取新单号]",e);
            result.error(e);
        }
        return result;
    }

    /**
     *  根据原单号获取对应的新单号
     *  (此接口会更改QPL新单号包裹数量)
     * @param request 换单打印请求对象
     * @return
     */
    @POST
    @Path("/reverse/exchange/getNewWaybillCode")
    @JProfiler(jKey = "DMS.WEB.ReversePrintResource.getNewWaybillCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<RepeatPrint> getNewWaybillCode(ExchangePrintRequest request){
        JdResult<RepeatPrint> result=new JdResult<RepeatPrint>();
        if(StringUtils.isEmpty(request.getOldWaybillCode())){
            result.toFail("参数错误");
            return result;
        }
        String oldWaybillCode = request.getOldWaybillCode();
        Integer packNum = request.getPackageNumber();
        try {
            if (waybillTraceManager.isWaybillWaste(oldWaybillCode)){
                result.toFail("弃件禁换单，每月5、20日原运单返到货传站分拣中心，用箱号纸打印“返分拣弃件”贴面单同侧(禁手写/遮挡面单)");
                return result;
            }
            boolean bool = waybillCancelService.checkWaybillCancelInterceptType99(oldWaybillCode);
            if (bool) {
                result.toFail(HintService.getHint(HintCodeConstants.WAYBILL_ERROR_RE_PRINT));
                return result;
            }

            InvokeResult<RepeatPrint> invokeResult = reversePrintService.getNewWaybillCode1(oldWaybillCode, true);
            result.setData(invokeResult.getData());
            String newWaybillCode = null;
            if(invokeResult.getCode() == InvokeResult.RESULT_SUCCESS_CODE
                    && invokeResult.getData() != null && !StringHelper.isEmpty(invokeResult.getData().getNewWaybillCode())){
                result.toSuccess();
                //非QPL单号直接返回
                if(!WaybillUtil.isQPLWaybill(oldWaybillCode)){
                    return result;
                }
                newWaybillCode = invokeResult.getData().getNewWaybillCode();
                InvokeResult updateResult = waybillCommonService.batchUpdatePackageByWaybillCode(newWaybillCode, packNum);
                if(updateResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                    result.toFail(invokeResult.getMessage());
                }
            }else {
                //如果是理赔增加特殊提示
                if(invokeResult != null && invokeResult.getData() != null && invokeResult.getData().getIsLPFlag() != null && invokeResult.getData().getIsLPFlag()){
                    result.toFail("该理赔单正在审核中，无法换单。" +
                            "\n" +
                            "营业部操作请到站长工作台-运营管控-理赔单监控报表查看换单状态，当显示“可换单”时即可操作换单。(http://z-ql.jd.com/)" +
                            "\n" +
                            "集配和城配操作请到快运揽派工作台-运营支持-异常拦截报表中查看换单状态，当显示可换单时即可重试换单。（http://kyt.jd.com/）");
                }else{
                    result.toFail(invokeResult.getMessage());
                }
                if(StringUtils.isEmpty(invokeResult.getMessage())){
                    result.toFail("没有获取到新的取件单");
                }
            }
        }catch (Throwable e){
            log.error("[逆向换单获取新单号]",e);
            result.toFail("换单失败!");
        }
        return result;
    }

    /**
     * 自营逆向换单
     * @param domain
     * @return
     */
    @POST
    @Path("reverse/exchange/ownWaybill")
    @JProfiler(jKey = "DMS.WEB.ReversePrintResource.exchangeOwnWaybill", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> exchangeOwnWaybill(OwnReverseTransferDomain domain){
        InvokeResult<Boolean> result;
        try{
            result = reversePrintService.checkWayBillForExchange(domain.getWaybillCode(), domain.getSiteId());
            if(result.getData()){
                result= reversePrintService.exchangeOwnWaybill(domain);
            }else{
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            }
        }catch (Throwable e){
            result=new InvokeResult<Boolean>();
            log.error("自营逆向换单",e);
            result.error(e);
        }
        return result;
    }

    /**
     * 逆向换单限制校验
     * @param domain
     * @return
     */
    @POST
    @Path("reverse/exchange/check")
    @JProfiler(jKey = "DMS.WEB.ReversePrintResource.exchangeCheck", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> exchangeCheck(OwnReverseTransferDomain domain){
        InvokeResult<Boolean> result;
        try{
            result = reversePrintService.checkWayBillForExchange(domain.getWaybillCode(), domain.getSiteId());
        }catch (Throwable e){
            result=new InvokeResult<Boolean>();
            log.error("逆向换单检查异常",e);
            result.error(e);
        }
        return result;
    }

    /**
     * 换单打印之后，
     *  1.保存离线称重数据
     *  2.换单打印全程跟踪
     *  3.保存popPrint数据
     * @return
     */
    @POST
    @Path("/reverse/printAfter")
    @JProfiler(jKey = "DMS.WEB.ReversePrintResource.reversePrintAfter", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> reversePrintAfter(ReversePrintRequest request) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        /* 参数校验 */
        if (null == request || StringHelper.isEmpty(request.getOldCode())
                || !WaybillUtil.isWaybillCode(request.getNewCode()) || !WaybillUtil.isPackageCode(request.getNewPackageCode())
                || request.getSiteCode() <= 0 || StringHelper.isEmpty(request.getStaffErpCode()) || null == request.getWeightOperFlow()) {
            log.warn("ReversePrintResource.reversePrintAfter-->换单打印结果上传参数不正确{}", JsonHelper.toJson(request));
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        request.setOldCode(WaybillUtil.getWaybillCode(request.getOldCode()));/* 如果老单号是包裹号的话则截取运单号 */

        /* 1.保存离线称重信息 */
        try {
            if (Constants.WEIGHT_ENABLE.equals(request.getWeightVolumeOperEnable())
                || Constants.WEIGHT_VOLUME_ENABLE.equals(request.getWeightVolumeOperEnable())) {
                TaskResponse taskResponse = taskResource.add(convert2TaskRequest(request));
                if(log.isDebugEnabled()){
                    log.debug("ReversePrintResource.reversePrintAfter-->换单打印离线称重数据上传返回：{}",JsonHelper.toJson(taskResponse));
                }
                if (null == taskResponse || !TaskResponse.CODE_OK.equals(taskResponse.getCode())) {
                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                    result.setMessage(result.getMessage().replace(InvokeResult.RESULT_SUCCESS_MESSAGE,"") + "【称重数据保存失败】");
                }
            }
        } catch (Exception e) {
            log.error("ReversePrintResource.reversePrintAfter-->保存离线打印任务失败{}",JsonHelper.toJson(request),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(result.getMessage().replace(InvokeResult.RESULT_SUCCESS_MESSAGE,"") + "【称重数据保存异常】");
        }

        /* 2.发送换单打印全程跟踪 */
        try {
            Boolean bool = reversePrintService.handlePrint(request);
            if (!bool) {
                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                result.setMessage(result.getMessage().replace(InvokeResult.RESULT_SUCCESS_MESSAGE,"") + "【发送换单打印全程跟踪失败】");
            }
        } catch (Exception e) {
            log.error("ReversePrintResource.reversePrintAfter-->发送换单打印全程跟踪失败{}",JsonHelper.toJson(request),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(result.getMessage().replace(InvokeResult.RESULT_SUCCESS_MESSAGE,"") + "【发送换单打印全程跟踪异常】");
        }

        /* 3.保存popPrint数据 */
        try {
            PopPrintResponse popPrintResponse = popPrintResource.savePopPrint(convert2PopPrintRequest(request));
            if (null == popPrintResponse || !PopPrintResponse.CODE_OK.equals(popPrintResponse.getCode())) {
                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                result.setMessage(result.getMessage().replace(InvokeResult.RESULT_SUCCESS_MESSAGE,"") + "【保存打印日志失败】");
            }
        } catch (Exception e) {
            log.error("ReversePrintResource.reversePrintAfter-->保存popPrint数据失败{}",JsonHelper.toJson(request),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(result.getMessage().replace(InvokeResult.RESULT_SUCCESS_MESSAGE,"") + "【保存打印日志异常】");
        }

        //发送换单打印消息

        ChangeOrderPrintMq changeOrderPrintMq = convert2PushPrintRecordDto(request);
        try {
            log.info("ReversePrintResource.reversePrintAfter-->发送换单打印消息数据{}",JsonHelper.toJson(changeOrderPrintMq));
            changeWaybillPrintSecondProducer.send(request.getOldCode(),JsonHelper.toJson(changeOrderPrintMq));
        } catch (JMQException e) {
            log.error("ReversePrintResource.reversePrintAfter-->发送换单打印消息数据失败{}",JsonHelper.toJson(request),e);
        }

        return result;
    }

    /**
     * 转换打印记录dto
     * @param request
     * @return
     */
    private ChangeOrderPrintMq convert2PushPrintRecordDto(ReversePrintRequest request){
        ChangeOrderPrintMq dto = new ChangeOrderPrintMq();
        dto.setOperateType(WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType());
        dto.setWaybillCode(request.getOldCode());
        dto.setSiteCode(request.getSiteCode());
        return dto;
    }

    /**
     * 转换为离线称重task
     * @param request
     * @return
     */
    private TaskRequest convert2TaskRequest(ReversePrintRequest request) {
        OpeObject opeObject = new OpeObject();
        opeObject.setPackageCode(request.getNewPackageCode());
        opeObject.setpWeight((float) request.getWeightOperFlow().getWeight());
        opeObject.setpLength((float) request.getWeightOperFlow().getLength());
        opeObject.setpWidth((float) request.getWeightOperFlow().getWidth());
        opeObject.setpHigh((float) request.getWeightOperFlow().getHigh());
        opeObject.setOpeUserId(request.getStaffId());
        opeObject.setOpeUserName(request.getStaffRealName());
        opeObject.setOpeSiteId(request.getSiteCode());
        opeObject.setOpeSiteName(request.getSiteName());
        opeObject.setOpeTime(DateHelper.formatDateTime(new Date(request.getOperateUnixTime())));

        OpeEntity entity = new OpeEntity();
        entity.setWaybillCode(request.getNewCode());
        entity.setOpeType(1);
        entity.setOpeDetails(Collections.singletonList(opeObject));

        TaskRequest weightTask = new TaskRequest();
        weightTask.setType(1160);
        weightTask.setSiteCode(request.getSiteCode());
        weightTask.setKeyword1(request.getNewCode());
        weightTask.setKeyword2("离线称重");
        weightTask.setReceiveSiteCode(0);
        weightTask.setBody("[" + JsonHelper.toJson(entity) + "]");
        return weightTask;
    }

    /**
     * 转换为PopPrintRequest对象
     * @param request
     * @return
     */
    private PopPrintRequest convert2PopPrintRequest(ReversePrintRequest request) {
        PopPrintRequest popPrintRequest = new PopPrintRequest();
        popPrintRequest.setWaybillCode(request.getNewCode());
        popPrintRequest.setQuantity(WaybillUtil.getPackNumByPackCode(request.getNewPackageCode()));
        popPrintRequest.setPopSupName(request.getPopSupName());
        popPrintRequest.setPopSupId(request.getPopSupId());
        popPrintRequest.setWaybillType(request.getWaybillType());
        popPrintRequest.setOperatorCode(request.getStaffId());
        popPrintRequest.setOperatorName(request.getStaffRealName());
        popPrintRequest.setOperateType(1);
        popPrintRequest.setOperateSiteCode(request.getSiteCode());
        popPrintRequest.setOperateSiteName(request.getSiteName());
        popPrintRequest.setOperateTime(DateHelper.formatDateTime(new Date(request.getOperateUnixTime())));
        popPrintRequest.setPopReceiveType(1);
        popPrintRequest.setPackageBarcode(request.getNewPackageCode());
        popPrintRequest.setBusiId(request.getBusiId());
        popPrintRequest.setBusiName(request.getBusiName());
        popPrintRequest.setInterfaceType(WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT_TYPE);
        return popPrintRequest;
    }
}
