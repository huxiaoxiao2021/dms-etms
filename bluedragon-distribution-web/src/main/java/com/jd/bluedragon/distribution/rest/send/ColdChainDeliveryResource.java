package com.jd.bluedragon.distribution.rest.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ColdChainDeliveryRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.TransPlanScheduleRequest;
import com.jd.bluedragon.distribution.api.response.ColdChainSendResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.coldchain.domain.TransPlanDetailResult;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainDeliveryResource
 * @date 2019/4/6
 */

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ColdChainDeliveryResource extends DeliveryResource{

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private ColdChainSendService coldChainSendService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Autowired
    private WaybillPackageBarcodeService waybillPackageBarcodeService;

    /**
     * 冷链发货接口
     *
     * @param request
     * @return
     */
    @JProfiler(jKey = "Bluedragon_dms_center.dms.delivery.coldChainSendDelivery", mState = {JProEnum.TP, JProEnum.FunctionError})
    @POST
    @Path("/delivery/coldChain/send")
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1003)
    public DeliveryResponse coldChainSendDelivery(List<ColdChainDeliveryRequest> request) {
        this.logger.info("冷链发发货 - 开始:" + JsonHelper.toJson(request));
        DeliveryResponse response = this.coldChainSendCheckAndFixSendCode(request);
        if (response != null) {
            return response;
        }
        List<SendM> sendMList = toSendMList(request);
        response = deliveryService.dellDeliveryMessage(SendBizSourceEnum.COLD_CHAIN_SEND, sendMList);
        if (JdResponse.CODE_OK.equals(response.getCode())) {
            coldChainSendService.batchAdd(sendMList, request.get(0).getTransPlanCode());
        }
        if (response != null) {
            return response;
        } else {
            return new DeliveryResponse(JdResponse.CODE_NOT_FOUND, JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    private DeliveryResponse coldChainSendCheckAndFixSendCode(List<ColdChainDeliveryRequest> request) {
        if (request != null && !request.isEmpty()) {
            ColdChainDeliveryRequest request0 = request.get(0);
            if (StringUtils.isEmpty(request0.getTransPlanCode()) || request0.getBoxCode() == null || request0.getSiteCode() == null || request0.getReceiveSiteCode() == null || request0.getBusinessType() == null) {
                return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
            }
            String sendCode = coldChainSendService.getOrGenerateSendCode(request0.getTransPlanCode(), request0.getSiteCode(), request0.getReceiveSiteCode());
            // 批次号封车校验，已封车不能发货
            if (newSealVehicleService.checkSendCodeIsSealed(sendCode)) {
                return new DeliveryResponse(DeliveryResponse.CODE_SEND_CODE_ERROR, "该运输计划编码对应批次已经封车，请更换其他运输计划编码");
            }
            request0.setSendCode(sendCode);

            for (int i = 1; i < request.size(); i++) {
                DeliveryRequest deliveryRequest = request.get(i);
                if (StringUtils.isEmpty(request0.getTransPlanCode()) || deliveryRequest.getBoxCode() == null || deliveryRequest.getSiteCode() == null || deliveryRequest.getReceiveSiteCode() == null || deliveryRequest.getBusinessType() == null) {
                    return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
                }
                deliveryRequest.setSendCode(sendCode);
            }
            return null;
        }
        return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
    }

    private List<SendM> toSendMList(List<ColdChainDeliveryRequest> request) {
        if (request != null && !request.isEmpty()) {
            List<SendM> sendMList = new ArrayList<>();
            for (DeliveryRequest deliveryRequest : request) {
                if (WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())) {
                    sendMList.addAll(this.request2SendMByWaybillCode(deliveryRequest));
                } else {
                    sendMList.add(this.deliveryRequest2SendM(deliveryRequest));
                }
            }
            return sendMList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 根据DeliveryRequest对象转成SendM列表
     * 注意：DeliveryRequest中的boxCode对应运单号
     *
     * @param deliveryRequest
     * @return
     */
    private List<SendM> request2SendMByWaybillCode(DeliveryRequest deliveryRequest) {
        List<SendM> sendMList = new ArrayList<SendM>();
        //生成包裹号
        List<String> packageCodes = waybillPackageBarcodeService.getPackageCodeListByWaybillCode(deliveryRequest.getBoxCode());
        for (String packageCode : packageCodes) {
            deliveryRequest.setBoxCode(packageCode);
            sendMList.add(this.deliveryRequest2SendM(deliveryRequest));
        }
        return sendMList;
    }

    /**
     * 根据始发和目的站点获取当日的运输计划信息
     *
     * @param request
     * @return
     */
    @POST
    @Path("/delivery/coldChain/getTransPlan")
    public ColdChainSendResponse<List<TransPlanDetailResult>> getTransPlan(TransPlanScheduleRequest request) {
        Integer createSiteCode = request.getCreateSiteCode();
        Integer receiveSiteCode = request.getReceiveSiteCode();
        if (createSiteCode != null && receiveSiteCode != null) {
            try {
                List<TransPlanDetailResult> resultList = coldChainSendService.getTransPlanDetail(createSiteCode, receiveSiteCode);
                if (resultList != null) {
                    return new ColdChainSendResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, resultList);
                } else {
                    return new ColdChainSendResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
                }
            } catch (Exception e) {
                logger.error("[冷链发货]根据始发分拣中心和目的分拣中心编号获取当日的运输计划明细信息时发生异常", e);
                return new ColdChainSendResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
            }
        }
        return new ColdChainSendResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
    }

}
