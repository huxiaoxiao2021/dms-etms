package com.jd.bluedragon.distribution.rest.farmar;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.label.enums.FarmarCheckTypeEnum;
import com.jd.bluedragon.distribution.labelPrint.domain.farmar.FarmarPrintEntity;
import com.jd.bluedragon.distribution.labelPrint.domain.farmar.FarmarPrintRequest;
import com.jd.bluedragon.distribution.labelPrint.service.LabelPrintService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.tp.common.utils.Objects;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 标签打印resource
 *
 * @author hujiping
 * @date 2022/8/22 4:16 PM
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class LabelPrintResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LabelPrintService labelPrintService;

    @POST
    @Path(value = "/farmar/getFarmarPrintInfo")
    @JProfiler(jKey = "dms.LabelPrintResource.getFarmarPrintInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<FarmarPrintEntity> getFarmarPrintInfo(@RequestBody FarmarPrintRequest request){
        InvokeResult<FarmarPrintEntity> result = checkParam(request);
        if(!result.codeSuccess()){
            return result;
        }
        return labelPrintService.farmarPrintDeal(request);
    }

    private InvokeResult<FarmarPrintEntity> checkParam(FarmarPrintRequest request) {
        InvokeResult<FarmarPrintEntity> result = new InvokeResult<FarmarPrintEntity>();
        if(request == null || !Lists.newArrayList(0, 1).contains(request.getPrintType())){
            result.parameterError();
            return result;
        }
        // 补打校验
        if(Objects.equals(request.getPrintType(), 1)){
            if(!WaybillUtil.isPackageCode(request.getFarmarCode())){
                result.parameterError("补打的砝码编码不符合规则！");
                return result;
            }
            return result;
        }
        // 打印校验
        if(request.getCreateSiteCode() == null || StringUtils.isEmpty(request.getCreateUserErp())
                || !FarmarCheckTypeEnum.LIST.contains(request.getFarmarCheckType())
                || request.getPrintCount() == null || request.getPrintCount() <= 0){
            result.parameterError();
            return result;
        }
        if(Objects.equals(request.getFarmarCheckType(), FarmarCheckTypeEnum.FARMAR_CHECK_TYPE_WEIGHT.getCode())
                && request.getWeight() == null){
            result.parameterError("重量标准，重量必须合法！");
            return result;
        }
        if(Objects.equals(request.getFarmarCheckType(), FarmarCheckTypeEnum.FARMAR_CHECK_TYPE_SIZE.getCode())
                && (
                request.getLength() == null
                || request.getWidth() == null
                || request.getHigh() == null
        )){
            result.parameterError("尺寸标准，长宽高必须合法！");
            return result;
        }
        if (Objects.equals(request.getFarmarCheckType(), FarmarCheckTypeEnum.FARMAR_CHECK_TYPE_BOTH.getCode())
                && (
                request.getWeight() == null
                || request.getLength() == null
                || request.getWidth()== null
                || request.getHigh() == null
        )) {
            result.parameterError("重量尺寸标准，重量和长宽高必须合法！");
            return result;
        }
        return result;
    }
}
