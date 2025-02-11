package com.jd.bluedragon.distribution.rest.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ColdChainDeliveryRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.TransPlanScheduleRequest;
import com.jd.bluedragon.distribution.api.response.ColdChainSendResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.coldchain.domain.TransPlanDetailResult;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
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

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private ColdChainSendService coldChainSendService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Autowired
    private WaybillPackageBarcodeService waybillPackageBarcodeService;

    @Autowired
    private SortingCheckService sortingCheckService;

    /**
     * 冷链发货校验接口
     *
     * @param pdaOperateRequest
     * @return
     */
    @JProfiler(jKey = "Bluedragon_dms_center.dms.delivery.checkColdChainSendDelivery", mState = {JProEnum.TP, JProEnum.FunctionError})
    @POST
    @Path("/delivery/coldChain/checkColdChainSendDelivery")
    public SortingJsfResponse checkColdChainSendDelivery(PdaOperateRequest pdaOperateRequest) {
        if(log.isInfoEnabled()){
            this.log.info("ColdChainDeliveryResource.checkColdChainSendDelivery param :{}", JsonHelper.toJson(pdaOperateRequest));
        }
        try {
            com.jd.bluedragon.common.dto.send.request.DeliveryRequest request = new com.jd.bluedragon.common.dto.send.request.DeliveryRequest();
            request.setBoxCode(pdaOperateRequest.getPackageCode());
            request.setBusinessType(pdaOperateRequest.getBusinessType());
            request.setOpType(pdaOperateRequest.getOperateType());
            request.setReceiveSiteCode(pdaOperateRequest.getReceiveSiteCode());
            CurrentOperate currentOperate = new CurrentOperate();
            currentOperate.setSiteCode(pdaOperateRequest.getCreateSiteCode());
            currentOperate.setSiteName(pdaOperateRequest.getCreateSiteName());
            request.setCurrentOperate(currentOperate);
            User user = new User();
            user.setUserCode(pdaOperateRequest.getOperateUserCode());
            user.setUserName(pdaOperateRequest.getOperateUserName());
            request.setUser(user);
            SortingJsfResponse sortingJsfResponse = sortingCheckService.coldChainSendCheckAndReportIntercept(request);
            log.info("ColdChainDeliveryResource.checkColdChainSendDelivery result {}", JsonHelper.toJson(sortingJsfResponse));
            return sortingJsfResponse;
        } catch (Exception e) {
            log.error("ColdChainDeliveryResource.checkColdChainSendDelivery exception ", e);
            return new SortingJsfResponse(DeliveryResponse.CODE_SERVICE_ERROR, DeliveryResponse.MESSAGE_SERVICE_ERROR_C);
        }
    }

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
        if(log.isInfoEnabled()){
            this.log.info("冷链发货 - 开始:{}", JsonHelper.toJson(request));
        }
        try{
            return deliveryService.coldChainSendDelivery(request,SendBizSourceEnum.COLD_CHAIN_SEND,Boolean.TRUE);
        } catch (Exception e) {
            log.error("B网冷链发货时发生异常", e);
            return new DeliveryResponse(DeliveryResponse.CODE_Delivery_ERROR, DeliveryResponse.MESSAGE_Delivery_ERROR);
        }
    }



    /**
     * 根据始发和目的站点获取当日的运输计划信息
     *
     * @param request
     * @return
     */
    @POST
    @Path("/delivery/coldChain/getTransPlan")
    @JProfiler(jKey = "DMS.WEB.ColdChainDeliveryResource.getTransPlan", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ColdChainSendResponse<List<TransPlanDetailResult>> getTransPlan(TransPlanScheduleRequest request) {
        Integer createSiteCode = request.getCreateSiteCode();
        Integer receiveSiteCode = request.getReceiveSiteCode();
        if (createSiteCode != null && receiveSiteCode != null) {
            try {
                List<TransPlanDetailResult> resultList = coldChainSendService.getTransPlanDetail(createSiteCode, receiveSiteCode);
                if (resultList != null) {
                    return new ColdChainSendResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, resultList);
                } else {
                    return new ColdChainSendResponse(JdResponse.CODE_GET_TRANSPLAN_ERROR, JdResponse.MESSAGE_GET_TRANSPLAN_ERROR);
                }
            } catch (Exception e) {
                log.error("[冷链发货]根据始发分拣中心和目的分拣中心编号获取当日的运输计划明细信息时发生异常", e);
                return new ColdChainSendResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
            }
        }
        return new ColdChainSendResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
    }

}
