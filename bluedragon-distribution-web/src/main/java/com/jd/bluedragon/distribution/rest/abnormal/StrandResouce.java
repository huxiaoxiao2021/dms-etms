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
            //发送滞留上报消息
            strandService.sendStrandReportJmq(request);
        }catch (Exception e){
            log.error("滞留上报异常,请求参数：{}", JsonHelper.toJson(request),e);
            invokeResult.error("滞留上报异常,请联系分拣小秘！");
        }
        //按批次提交 单独提示
        if(ReportTypeEnum.BATCH_NO.getCode().equals(request.getReportType())){
            invokeResult.setMessage("提交成功，如需取消发货或封车，请手动操作！");
            return invokeResult;
        }
        boolean hasSend = strandService.hasSenddetail(request);
        String message = "滞留上报成功";
        if(hasSend){
            message += "，该" + ReportTypeEnum.getReportTypeName(request.getReportType()) + "发货已被取消";
        }
        invokeResult.setMessage(message);
        return invokeResult;
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
