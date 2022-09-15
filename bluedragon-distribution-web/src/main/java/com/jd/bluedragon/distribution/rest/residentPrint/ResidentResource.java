package com.jd.bluedragon.distribution.rest.residentPrint;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.TerminalManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintMessages;
import com.jd.bluedragon.distribution.resident.domain.ResidentCollectDto;
import com.jd.bluedragon.distribution.resident.service.ResidentCollectService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.erp.service.dto.SendInfoDto;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Objects;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ResidentResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private TerminalManager terminalManager;

    @Autowired
    private ResidentCollectService residentCollectService;

    /**
     * 驻厂揽收
     *
     * @param residentCollectDto
     * @return
     */
    @POST
    @Path("/zc/residentCollect")
    @JProfiler(jKey = "dms.web.ResidentResource.residentCollect", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> residentCollect(ResidentCollectDto residentCollectDto) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if (!basicParamsCheck(residentCollectDto, result)) {
            return result;
        }
        return residentCollectService.residentCollect(residentCollectDto);
    }

    /**
     * 驻厂揽收完成
     *
     * @param popPrintRequest
     * @return
     */
    @POST
    @Path("/zc/afterCollectFinish")
    @JProfiler(jKey = "dms.web.ResidentResource.afterCollectFinish", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> afterCollectFinish(PopPrintRequest popPrintRequest) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if (popPrintRequest == null
                || !WaybillUtil.isWaybillCode(popPrintRequest.getWaybillCode())
                || StringUtils.isEmpty(popPrintRequest.getPackageBarcode())
                || popPrintRequest.getOperateSiteCode() == null || Objects.equals(popPrintRequest.getOperateSiteCode(), Constants.NUMBER_ZERO)
                || popPrintRequest.getOperatorCode() == null
                || popPrintRequest.getOperateType() == null || Objects.equals(popPrintRequest.getOperateType(), Constants.NUMBER_ZERO)) {
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, InvokeResult.PARAM_ERROR);
            return result;
        }
        return residentCollectService.afterCollectFinish(popPrintRequest);
    }

    /**
     * 参数校验
     *
     * @param residentCollectDto
     * @param result
     * @return
     */
    private boolean basicParamsCheck(ResidentCollectDto residentCollectDto, InvokeResult<Boolean> result) {
        if(residentCollectDto == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, InvokeResult.PARAM_ERROR);
            return false;
        }
        if(residentCollectDto.getSiteCode() == null || residentCollectDto.getOperateUserId() == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "操作人所属信息不存在!");
            return false;
        }
        if(!WaybillUtil.isWaybillCode(residentCollectDto.getBarCode()) && !WaybillUtil.isPackageCode(residentCollectDto.getBarCode())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "操作单号不合法!");
            return false;
        }
        if(StringUtils.isEmpty(residentCollectDto.getConsignments())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "请选择托寄物!");
            return false;
        }
        if(residentCollectDto.getWeight() == null || residentCollectDto.getWeight() <= Constants.DOUBLE_ZERO) {
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "驻厂揽收必须录入重量!");
            return false;
        }
        if(residentCollectDto.getLength() == null || residentCollectDto.getLength() <= Constants.DOUBLE_ZERO
                || residentCollectDto.getWidth() == null || residentCollectDto.getWidth() <= Constants.DOUBLE_ZERO
                || residentCollectDto.getHeight() == null || residentCollectDto.getHeight() <= Constants.DOUBLE_ZERO){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "驻厂揽收必须录入重量、长、宽、高!");
            return false;
        }
        return true;
    }

    /**
     * 查询包裹是否操作站点发货
     * @param boxCode
     * @return
     */
    @GET
    @Path("/resident/isSendBySite/{boxCode}")
    public InvokeResult<String> isSendBySite(@PathParam("boxCode") String boxCode){
        InvokeResult<String> result = new InvokeResult<String>();
        String packageCode = null;
        try{
            //获取箱号中其中的一个包裹号（终端）
            List<SendInfoDto> sendDetailsFromZD = terminalManager.getSendDetailsFromZD(boxCode);
            if(CollectionUtils.isNotEmpty(sendDetailsFromZD)) {
                packageCode = sendDetailsFromZD.get(0).getPackageBarcode();
            }
            //根据包裹号判断是否操作发货
            if(packageCode != null){
                BaseEntity<List<PackageState>> entity = waybillTraceManager.getPkStateByPCode(packageCode);
                if(entity != null && entity.getData() != null &&
                        entity.getData().size() > 0){
                    List<PackageState> packageStateList = entity.getData();
                    for(PackageState packageState : packageStateList){
                        if(packageState.getState().equals(Constants.WAYBILL_TRACE_STATE_SEND_BY_SITE)){
                            result.setData(WaybillPrintMessages.MESSAGE_WAYBILL_STATE_SEND_BY_SITE);
                            break;
                        }
                    }
                }
            }
        }catch (Exception e){
            this.log.error("查询箱号{}是否操作站点发货服务异常!",boxCode);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            result.setData(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

}
