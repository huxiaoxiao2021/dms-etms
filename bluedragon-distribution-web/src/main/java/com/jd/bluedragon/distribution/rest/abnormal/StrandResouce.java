package com.jd.bluedragon.distribution.rest.abnormal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.Date;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_PARAMETER_ERROR_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;

/**
 * 包裹滞留
 * @date 2020/3/10.
 * @author jinjingcheng
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class StrandResouce {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private StrandService strandService;

    /**
     * 包裹滞留上报
     * @param request
     * @return
     */
    @POST
    @Path("strand/report")
    public InvokeResult<Boolean> report(StrandReportRequest request){
        InvokeResult<Boolean> invokeResult = checkParam(request);
        if(RESULT_SUCCESS_CODE != invokeResult.getCode()){
            return invokeResult;
        }
        try {
            //发全程跟踪和 发滞留明细jmq
            invokeResult = strandService.reportStrandDetail(request);
            if(RESULT_SUCCESS_CODE != invokeResult.getCode()){
                log.warn("滞留上报发全程跟踪和jmq是失败:{}，请求参数{}", JsonHelper.toJson(invokeResult), JsonHelper.toJson(request));
                return invokeResult;
            }
            SendM sendM = initSendM(request);
            ThreeDeliveryResponse response = deliveryService.dellCancelDeliveryMessage(sendM, true);
            //取消发货时异常
            if(DeliveryResponse.CODE_Delivery_ERROR.equals(response.getCode())){
                log.error("包裹滞留上报取消发货时异常条码:{},createSiteCode:{}", request.getBarcode(), request.getSiteCode());
                invokeResult.error("取消发货时异常");
                return invokeResult;
            }
        }catch (Exception e){
            log.error("滞留上报异常,请求参数：{}", JsonHelper.toJson(request),e);
            invokeResult.error("滞留上报异常,请联系分拣小秘！");
        }
        return invokeResult;
    }

    private SendM initSendM(StrandReportRequest request){
        SendM sendM = new SendM();
        sendM.setCreateSiteCode(request.getSiteCode());
        sendM.setUpdaterUser(request.getUserName());
        sendM.setSendType(request.getBusinessType());
        sendM.setUpdateUserCode(request.getUserCode());
        String barcode = request.getBarcode();
        Integer reportType = request.getReportType();
        if(ReportTypeEnum.BATCH_NO.getCode().equals(reportType)){
            sendM.setSendCode(barcode);
        }else{
            sendM.setBoxCode(barcode);
        }
        Date operateTime = DateHelper.parseDate(request.getOperateTime(), Constants.DATE_TIME_FORMAT);
        sendM.setOperateTime(operateTime);
        //从批次号中获取目的站点
        if (ReportTypeEnum.BATCH_NO.getCode().equals(reportType)) {
            sendM.setReceiveSiteCode(BusinessUtil.getReceiveSiteCodeFromSendCode(barcode));
        }
        sendM.setUpdateTime(new Date());
        sendM.setYn(0);
        return sendM;
    }

    /**
     * 参数检查
     * @param request
     * @return
     */
    private InvokeResult<Boolean> checkParam(StrandReportRequest request){
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        invokeResult.setCode(RESULT_PARAMETER_ERROR_CODE);
        if(request == null){
            invokeResult.setMessage("异常上报请求参数null");
            log.warn("异常上报请求参数为空");
            return invokeResult;
        }
        String barcode = request.getBarcode();
        if(StringUtils.isBlank(barcode)){
            invokeResult.setMessage("异常上报请求参数错误，条码为空");
            log.warn("异常上报请求参数错误，条码为空");
            return invokeResult;
        }
        if(request.getReasonCode() == null){
            invokeResult.setMessage("异常上报原因为空，请选择");
            log.warn("异常上报原因为空");
            return invokeResult;
        }
        Integer reportType = request.getReportType();
        if(reportType == null){
            invokeResult.setMessage("上报的条码类型为空");
            log.warn("上报的条码类型为空");
            return invokeResult;
        }
        //按包裹操作，条码非包裹号
        if(ReportTypeEnum.PACKAGE_CODE.getCode().equals(reportType) && !WaybillUtil.isPackageCode(barcode)){
            invokeResult.setMessage("你选择的按包裹上报，请扫描包裹号");
            log.warn("按包裹上报，条码{}非包裹号", barcode);
            return invokeResult;
        }
        //按运单操作，条码非运单号
        if(ReportTypeEnum.WAYBILL_CODE.getCode().equals(reportType) && !WaybillUtil.isWaybillCode(barcode)){
            invokeResult.setMessage("你选择的按运单上报，请扫描运单号");
            log.warn("按包裹上报，条码{}非包裹号", barcode);
            return invokeResult;
        }
        //按箱号操作，条码非包裹号
        if(ReportTypeEnum.BOX_CODE.getCode().equals(reportType) && !BusinessUtil.isBoxcode(barcode)){
            invokeResult.setMessage("你选择的按箱上报，请扫描箱号");
            return invokeResult;
        }
        //按批次操作，条码非批次号
        if(ReportTypeEnum.BATCH_NO.getCode().equals(reportType) && !BusinessUtil.isSendCode(barcode)){
            invokeResult.setMessage("你选择的按批次上报，请扫描批次号");
            return invokeResult;
        }
        invokeResult.success();
        return invokeResult;
    }

}
