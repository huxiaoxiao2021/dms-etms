package com.jd.bluedragon.distribution.rest.send;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.response.ScannerFrameBatchSendResponse;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.external.api.DmsDeliveryApi;
import com.jd.bluedragon.distribution.gantry.domain.SendGantryDeviceConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ServiceMessage;
import com.jd.bluedragon.common.domain.ServiceResultEnum;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.DeliveryBatchRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.api.response.WhBcrsQueryResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.bluedragon.distribution.jsf.domain.WhemsWaybillResponse;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendDifference;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.SendThreeDetail;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.distribution.send.service.SendQueryService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DeliveryResource implements DmsDeliveryApi {

    @Autowired
    DeliveryService deliveryService;

    @Autowired
    ReverseDeliveryService reverseDelivery;

    @Autowired
    DepartureService departureService;

    @Autowired
    private SiteService siteService;

    public static final String SEND_M = "sendm";

    @Autowired
    private LoadBillService loadBillService;

    @Autowired
    private SendQueryService sendQueryService;

    @Autowired
    private ScannerFrameBatchSendService scannerFrameBatchSendService;
    
    private static final Integer KY_DELIVERY = 1; //快运发货标识

    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private SendMDao sendMDao;
    
    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private BaseMajorManager baseMajorManager;


    /**
     * 原包发货【一车一件项目，发货专用】
     *
     * @param request 发货对象
     * @return
     */
    @POST
    @Path("/delivery/packagesend")
    public InvokeResult<AbstractMap.Entry<Integer, String>> packageSend(PackageSendRequest request) {
        InvokeResult<SendResult> res = this.newPackageSend(request);
        InvokeResult<AbstractMap.Entry<Integer, String>> result = new InvokeResult<AbstractMap.Entry<Integer, String>>();
        result.setCode(res.getCode());
        result.setMessage(res.getMessage());
        if (null != res.getData()) {
            AbstractMap.Entry<Integer, String> data = new AbstractMap.SimpleEntry<Integer, String>(res.getData().getKey(), res.getData().getValue());
            result.setData(data);
        }
        return result;
    }

    @POST
    @Path("/delivery/newpackagesend")
    @Override
    public InvokeResult<SendResult> newPackageSend(PackageSendRequest request) {
        if(logger.isInfoEnabled()){
            logger.info(JsonHelper.toJsonUseGson(request));
        }
        SendM domain = new SendM();
        domain.setReceiveSiteCode(request.getReceiveSiteCode());
        domain.setSendCode(request.getSendCode());
        domain.setCreateSiteCode(request.getSiteCode());

        String turnoverBoxCode = request.getTurnoverBoxCode();
        if(StringUtils.isNotBlank(turnoverBoxCode) && turnoverBoxCode.length() > 30){
            domain.setTurnoverBoxCode(turnoverBoxCode.substring(0, 30));
        }else{
            domain.setTurnoverBoxCode(turnoverBoxCode);
        }
        domain.setCreateUser(request.getUserName());
        domain.setCreateUserCode(request.getUserCode());
        domain.setSendType(request.getBusinessType());
        domain.setTransporttype(request.getTransporttype());
        domain.setYn(1);
        domain.setCreateTime(new Date(System.currentTimeMillis() + 30000));
        domain.setOperateTime(new Date(System.currentTimeMillis() + 30000));
        InvokeResult<SendResult> result = new InvokeResult<SendResult>();
        try {
            if(SerialRuleUtil.isBoardCode(request.getBoxCode())){//一车一单下的组板发货
                domain.setBoardCode(request.getBoxCode());
                logger.warn("组板发货newpackagesend：" + JsonHelper.toJson(request));
                result.setData(deliveryService.boardSend(domain));
            }else{//一车一单发货
                domain.setBoxCode(request.getBoxCode());
                result.setData(deliveryService.packageSend(domain, request.getIsForceSend()));
            }
        } catch (Exception ex) {
            result.error(ex);
            logger.error("一车一单发货", ex);
        }
        if(logger.isInfoEnabled()){
            logger.info(JsonHelper.toJsonUseGson(result));
        }
        return result;
    }

    @GET
    @Path("/delivery/checksendcodestatus/{sendCode}")
    @Override
    public InvokeResult<AbstractMap.Entry<Integer, String>> checkSendCodeStatus(@PathParam("sendCode") String sendCode) {
        InvokeResult<AbstractMap.Entry<Integer, String>> result = new InvokeResult<AbstractMap.Entry<Integer, String>>();
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode);
        if(receiveSiteCode == null){//批次号是否符合编码规范，不合规范直接返回参数错误
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("请输入正确的批次号！");
        }else{
            if (forbid(result, receiveSiteCode)) return result;
            try {
                ServiceMessage<Boolean> data = departureService.checkSendStatusFromVOS(sendCode);
                if (ServiceResultEnum.WRONG_STATUS.equals(data.getResult())) {//已被封车
                    result.setData(new AbstractMap.SimpleEntry<Integer, String>(2, "该发货批次号已操作封车，无法重复操作！"));
                } else if(ServiceResultEnum.SUCCESS.equals(data.getResult())){//未被封车
                    BaseStaffSiteOrgDto site = siteService.getSite(receiveSiteCode);
                    String siteName = null != site ? site.getSiteName() : "未获取到该站点名称";
                    result.setData(new AbstractMap.SimpleEntry<Integer, String>(1, siteName));
                }else{
                    result.error(data.getErrorMsg());
                }
            } catch (Exception ex) {
                result.error(ex);
                logger.error("发货校验批次号异常：", ex);
            }
        }
        return result;
    }

    /**
     * 一车一单操作增加提示，如果操作逆向则阻断
     * @param result 返回结果
     * @param receiveSiteCode 目的站点号
     * @return
     */
    private boolean forbid(InvokeResult<Map.Entry<Integer, String>> result, Integer receiveSiteCode) {
        BaseStaffSiteOrgDto bDto = null;
        try {
            bDto = this.baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
        } catch (Exception e) {
            this.logger.error("一车一单发货通过站点ID获取基础资料失败:"+receiveSiteCode,e);
            return false;
        }
        Integer siteType=0;
        if (null != bDto) {
            siteType = bDto.getSiteType();
            String asm_type = PropertiesHelper.newInstance().getValue("asm_type");//售后
            String wms_type = PropertiesHelper.newInstance().getValue("wms_type");//仓储
            String spwms_type = PropertiesHelper.newInstance().getValue("spwms_type");//备件库退货
            if(siteType==Integer.parseInt(asm_type)||siteType==Integer.parseInt(wms_type)||siteType==Integer.parseInt(spwms_type)){
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage("禁止逆向操作！");
                return true;
            }
        }else{
            this.logger.warn("一车一单发获取站点信息为空：" + receiveSiteCode);
        }
        return false;
    }

    @POST
    @Path("/delivery/cancel")
    public ThreeDeliveryResponse cancelDeliveryInfo(DeliveryRequest request) {
        logger.info("取消发货JSON" + JsonHelper.toJsonUseGson(request));
        this.logger.info("开始写入取消发货信息");
        if (request.getBoxCode() == null || request.getSiteCode() == null) {
            return new ThreeDeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR, null);
        }
        /**现在装在单逻辑***/
        DeliveryResponse deliveryResponse = sendLoadBillCheck(request);
        if (!deliveryResponse.getCode().equals(JdResponse.CODE_OK)) {
            return new ThreeDeliveryResponse(JdResponse.CODE_UNLOADBILL,
                    JdResponse.MESSAGE_UNLOADBILL, null);
        }
        /*****/

        ThreeDeliveryResponse tDeliveryResponse = null;
        try {
            tDeliveryResponse = deliveryService.dellCancelDeliveryMessage(toSendM(request), true);
        } catch (Exception e) {
            this.logger.error("写入取消发货信息失败", e);
        }
        this.logger.info("结束写入取消发货信息");
        if (tDeliveryResponse != null) {
            return tDeliveryResponse;
        } else {
            return new ThreeDeliveryResponse(JdResponse.CODE_NOT_FOUND,
                    JdResponse.MESSAGE_SERVICE_ERROR, null);
        }
    }

    /**
     * 取消发货 要判断是否已经装载， 未装载和拒绝的可以 取消 发货\分拣
     */
    private DeliveryResponse sendLoadBillCheck(DeliveryRequest request) {
        if (request.getBoxCode() == null || request.getSiteCode() == null) {
            this.logger.error("sendLoadBillCheck 参数错误");
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        LoadBillReport loadBillReport = new LoadBillReport();
        if (BusinessHelper.isBoxcode(request.getBoxCode())) {
            loadBillReport.setBoxCode(request.getBoxCode());
        } else {
            loadBillReport.setOrderId(request.getBoxCode());
        }
        this.logger.info("开始获取 装载数据");
        try {
            List<LoadBill> loadBillList = loadBillService.findWaybillInLoadBill(loadBillReport);

            /**  loadBillList 空时表示未装载 可以取消，
             *  10初始,20已申请,30已放行, 【40未放行】
             */
            if (loadBillList != null && !loadBillList.isEmpty()) {
                for (LoadBill bill : loadBillList) {
                    if (bill.getApprovalCode().equals(LoadBill.REDLIGHT) || !bill.getDmsCode().equals(request.getSiteCode())) {
                        return new DeliveryResponse(JdResponse.CODE_OK,
                                JdResponse.MESSAGE_OK);
                    }
                }
            } else {
                return new DeliveryResponse(JdResponse.CODE_OK,
                        JdResponse.MESSAGE_OK);
            }
        } catch (Exception e) {
            this.logger.error("开始获取 装载数据 失败 findWaybillInLoadBill" + e);
        }

        return new DeliveryResponse(JdResponse.CODE_UNLOADBILL,
                JdResponse.MESSAGE_UNLOADBILL);
    }

    /**
     * 老发货接口
     * @param request
     * @return
     */
    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.delivery.sendPack", mState = {JProEnum.TP, JProEnum.FunctionError})
    @POST
    @Path("/delivery/send")
    public DeliveryResponse sendDeliveryInfo(List<DeliveryRequest> request) {
        this.logger.info("开始写入发货信息"+JsonHelper.toJson(request));
        if (check(request)) {
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        DeliveryResponse tDeliveryResponse = deliveryService.dellDeliveryMessage(toSendDatailList(request));
        this.logger.info("结束写入发货信息");
        if (tDeliveryResponse != null) {
            return tDeliveryResponse;
        } else {
            return new DeliveryResponse(JdResponse.CODE_NOT_FOUND,
                    JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    @POST
    @Path("/delivery/verification")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.verification", mState = {JProEnum.TP})
    public ThreeDeliveryResponse checkThreeDelivery(List<DeliveryRequest> request) {
        this.logger.info("开始三方发货不全验证");
        try {
            if (check(request)) {
                return new ThreeDeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                        JdResponse.MESSAGE_PARAM_ERROR, null);
            }
            Integer opType = request.get(0).getOpType();
            ThreeDeliveryResponse response = null;
            if(KY_DELIVERY.equals(opType)){//快运发货
                response =  deliveryService.checkThreePackageForKY(toSendDatailList(request));
            }else{
                response =  deliveryService.checkThreePackage(toSendDatailList(request));
            }
            this.logger.info("结束三方发货不全验证");
            return response;
        } catch (Exception ex) {
            logger.error("发货不全验证", ex);
            return new ThreeDeliveryResponse(JdResponse.CODE_INTERNAL_ERROR, ex.getMessage(), null);
        }
    }

    @POST
    @Path("/delivery/router/verification")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.router.verification", mState = {JProEnum.TP})
    public DeliveryResponse checkThreeDelivery(DeliveryRequest request) {
        try {
            if (request == null || StringUtils.isBlank(request.getBoxCode()) ||
                    request.getSiteCode() == null || request.getReceiveSiteCode() == null) {
                return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
            }
            Integer opType = request.getOpType();
            DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
            if(KY_DELIVERY.equals(opType)){//只有快运发货才做路由校验
                response =  deliveryService.checkRouterForKY(deliveryRequest2SendM(request));
            }
            return response;
        } catch (Exception ex) {
            logger.error("快运发货路由验证出错：", ex);
            return new DeliveryResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    @POST
    @Path("/delivery/sortingdiff")
    public ThreeDeliveryResponse checkSortingDiff(DeliveryRequest request) {
        String boxCode = request.getBoxCode();
        Integer createSiteCode = request.getSiteCode();
        Integer receiveSiteCode = request.getReceiveSiteCode();
        this.logger.info("开始一单多件包裹不全验证,createSiteCode[" + createSiteCode
                + "],receiveSiteCode[" + receiveSiteCode + "],boxCode["
                + boxCode + "]");

        if (boxCode == null || boxCode.trim().equals("")
                || createSiteCode == null || receiveSiteCode == null) {
            return new ThreeDeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR, null);
        }

        List<SendThreeDetail> tDeliveryResponse = null;
        try {
            tDeliveryResponse = deliveryService.checkSortingDiff(boxCode, createSiteCode, receiveSiteCode);
            this.logger.info("结束一单多件包裹不全验证");
        } catch (Exception e) {
            this.logger.error("一单多件包裹不全验证异常", e);
        }
        if (tDeliveryResponse != null && !tDeliveryResponse.isEmpty()) {
            return new ThreeDeliveryResponse(
                    DeliveryResponse.CODE_Delivery_SORTING_DIFF,
                    DeliveryResponse.MESSAGE_Delivery_SORTING_DIFF,
                    tDeliveryResponse);
        } else {
            return new ThreeDeliveryResponse(JdResponse.CODE_OK,
                    JdResponse.MESSAGE_OK, null);
        }
    }

    @POST
    @Path("/delivery/appendpackagenum")
    public DeliveryResponse appendPackageNum(@QueryParam("createSiteCode") Integer createSiteCode,
                                             @QueryParam("receiveSiteCode") Integer receiveSiteCode, @QueryParam("boxCode") String boxCode) {
        this.logger.info("开始补全发货明细表包裹数量,createSiteCode[" + createSiteCode
                + "],receiveSiteCode[" + receiveSiteCode + "],boxCode["
                + boxCode + "]");
        Integer updatedNum = 0;
        String msg;

        long startTime = System.currentTimeMillis();
        try {
            updatedNum = deliveryService.appendPackageNum(boxCode, createSiteCode, receiveSiteCode);
            this.logger.info("结束补全发货明细表包裹数量");
        } catch (Exception e) {
            msg = "补全发货明细表包裹数量失败";
            return new DeliveryResponse(JdResponse.CODE_OK, msg);
        }
        long endTime = System.currentTimeMillis();

        long cost = endTime - startTime;
        msg = JdResponse.MESSAGE_OK + ": 更新数量[" + updatedNum + "],花费时间[" + cost + "ms]";
        return new DeliveryResponse(JdResponse.CODE_OK, msg);
    }

    @GET
    @Path("/delivery/check")
    public DeliveryResponse checkDeliveryInfo(@QueryParam("boxCode") String boxCode,
                                              @QueryParam("siteCode") String siteCode,
                                              @QueryParam("receiveSiteCode") String receiveSiteCode,
                                              @QueryParam("businessType") String businessType) {
        this.logger.info("开始验证箱号信息");
        this.logger.info("boxCode is " + boxCode);
        this.logger.info("siteCode is " + siteCode);
        this.logger.info("receiveSiteCode is " + receiveSiteCode);
        this.logger.info("businessType is " + businessType);
        if (boxCode == null || siteCode == null || businessType == null || receiveSiteCode == null) {
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        SendM tSendM = new SendM();
        tSendM.setBoxCode(boxCode);
        tSendM.setCreateSiteCode(Integer.parseInt(siteCode));
        tSendM.setReceiveSiteCode(Integer.parseInt(receiveSiteCode));
        tSendM.setSendType(Integer.parseInt(businessType));
        DeliveryResponse tDeliveryResponse = null;
        boolean flage = false;


        try {

            flage = deliveryService.isTransferSend(tSendM);
            tDeliveryResponse = deliveryService.findSendMByBoxCode(tSendM, flage);
            this.logger.info("结束验证箱号信息");
            if (tDeliveryResponse != null) {
                if (flage && tDeliveryResponse.getCode().equals(JdResponse.CODE_OK))
                    return new DeliveryResponse(DeliveryResponse.CODE_Delivery_TRANSIT,
                            DeliveryResponse.MESSAGE_Delivery_TRANSIT);

                return tDeliveryResponse;
            } else {
                return new DeliveryResponse(JdResponse.CODE_NOT_FOUND,
                        JdResponse.MESSAGE_SERVICE_ERROR);
            }
        } catch (Exception e) {
            logger.error("发货校验异常",e);
            return new DeliveryResponse(JdResponse.CODE_NOT_FOUND,
                    JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    @POST
    @Path("/delivery/whemsWaybill")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.getWhemsWaybill", mState = {JProEnum.TP})
    public WhemsWaybillResponse getWhemsWaybill(List<String> request, @Context HttpServletRequest servletRequest) {

        this.logger.error("servletRequest.getHeader()" + servletRequest.getHeader("X-Forwarded-For"));
        String realIP = servletRequest.getHeader("X-Forwarded-For");
        String emsIp = PropertiesHelper.newInstance().getValue("EMSIP");
        if (realIP != null && !realIP.contains(emsIp)) {
            return new WhemsWaybillResponse(JdResponse.CODE_PARAM_ERROR,
                    "非法IP调用");
        }
        if (request == null || request.isEmpty() || request.size() > 50) {
            return new WhemsWaybillResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        return reverseDelivery.getWhemsWaybill(request);
    }

    @POST
    @Path("/delivery/pushWhemsWaybill")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.pushWhemsWaybill", mState = {JProEnum.TP})
    public WhemsWaybillResponse pushWhemsWaybill(List<String> request) {
        if (request == null || request.isEmpty()) {
            return new WhemsWaybillResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        reverseDelivery.pushWhemsWaybill(request);
        return new WhemsWaybillResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }

    @POST
    @Path("/delivery/toEmsServer")
    public WhemsWaybillResponse toEmsServer(List<String> request) {
        if (request == null || request.isEmpty()) {
            return new WhemsWaybillResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        reverseDelivery.toEmsServer(request);
        return new WhemsWaybillResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }

    @POST
    @Path("/delivery/whBcrsQuery")
    public WhBcrsQueryResponse whBcrsQuery(DeliveryRequest request) {
        String sendCode = request.getSendCode();
        if (sendCode == null || sendCode.isEmpty()) {
            return new WhBcrsQueryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR, null);
        }
        List<SendDetail> sendds = deliveryService.queryBySendCodeAndSendType(sendCode, null);
        Set<String> packs = new HashSet<String>();
        for (SendDetail sendd : sendds) {
            packs.add(sendd.getWaybillCode());
        }
        return new WhBcrsQueryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK, JsonHelper.toJsonUseGson(packs));
    }

    private SendM toSendM(DeliveryRequest request) {
        SendM sendM = new SendM();
        sendM.setBoxCode(request.getBoxCode());
        sendM.setCreateSiteCode(request.getSiteCode());
        sendM.setUpdaterUser(request.getUserName());
        sendM.setSendType(request.getBusinessType());
        sendM.setUpdateUserCode(request.getUserCode());
        if (!BusinessHelper.isBoxcode(request.getBoxCode())) {
            sendM.setReceiveSiteCode(request.getReceiveSiteCode());
        }
        sendM.setUpdateTime(new Date());
        sendM.setYn(0);
        return sendM;
    }

    private List<SendM> toSendDatailList(List<DeliveryRequest> request) {

        List<SendM> sendMList = new ArrayList<SendM>();
        if (request != null && !request.isEmpty()) {
            for (DeliveryRequest deliveryRequest : request) {
                sendMList.add(deliveryRequest2SendM(deliveryRequest));
            }
        }
        return sendMList;
    }

    /**
     * DeliveryRequest对象转sendM
     * @param deliveryRequest
     * @return
     */
    private SendM deliveryRequest2SendM(DeliveryRequest deliveryRequest){
        SendM sendM = new SendM();
        sendM.setBoxCode(deliveryRequest.getBoxCode());
        sendM.setCreateSiteCode(deliveryRequest.getSiteCode());
        sendM.setReceiveSiteCode(deliveryRequest.getReceiveSiteCode());
        sendM.setCreateUserCode(deliveryRequest.getUserCode());
        sendM.setSendType(deliveryRequest.getBusinessType());
        sendM.setCreateUser(deliveryRequest.getUserName());
        sendM.setSendCode(deliveryRequest.getSendCode());
        sendM.setCreateTime(new Date());
        sendM.setOperateTime(new Date());
        sendM.setYn(1);
        sendM.setTurnoverBoxCode(deliveryRequest.getTurnoverBoxCode());
        sendM.setTransporttype(deliveryRequest.getTransporttype());
        return sendM;
    }


    private boolean check(List<DeliveryRequest> request) {
        if (request != null && !request.isEmpty()) {
            for (DeliveryRequest deliveryRequest : request) {
                if (deliveryRequest.getBoxCode() == null || deliveryRequest.getSiteCode() == null || deliveryRequest.getReceiveSiteCode() == null
                        || deliveryRequest.getBusinessType() == null || deliveryRequest.getSendCode() == null) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    @GET
    @Path("/delivery/findWaybillStatus/{id}")
    public String findWaybillStatus(@PathParam("id") String id) {
        String result = null;
        List<SendDetail> sendDetails = new ArrayList<SendDetail>();

        try {
            List<String> queueid = new ArrayList<String>();
            queueid.add(id);
            sendDetails = deliveryService.findWaybillStatus(queueid);
            if (sendDetails != null && !sendDetails.isEmpty()) {
                this.deliveryService.updateWaybillStatus(sendDetails);
                result = JsonHelper.toJsonUseGson(sendDetails);
            } else
                logger.error("findWaybillStatus查询无符合条件");
        } catch (Exception e) {
            this.logger.error("web补全包裹运单信息异常：", e);
        }

        return result;
    }

    @GET
    @Path("/delivery/updateWaybillStatus/{sendCode}/{createSiteCode}/{receiveSiteCode}/{senddStatus}")
    public JdResponse updateWaybillStatus(@PathParam("sendCode") String sendCode,
                                      @PathParam("createSiteCode") Integer createSiteCode,
                                      @PathParam("receiveSiteCode") Integer receiveSiteCode,
                                      @PathParam("senddStatus")  Integer senddStatus) {
        JdResponse result = new JdResponse();
        List<SendDetail> sendDetails ;
        try {
            sendDetails = deliveryService.queryBySendCodeAndSiteCode(sendCode, createSiteCode, receiveSiteCode, senddStatus);
            if (sendDetails != null && !sendDetails.isEmpty()) {
                if(deliveryService.updateWaybillStatus(sendDetails)){
                    result.setCode(result.CODE_OK);
                    result.setMessage(result.MESSAGE_OK);
                }else {
                    result.setCode(result.CODE_INTERNAL_ERROR);
                    result.setMessage("更新运单状态失败！");
                }

            } else{
                result.setCode(result.CODE_OK_NULL);
                result.setMessage("未查到符合条件的sendd数据");
                logger.error(MessageFormat.format("queryBySendCodeAndSiteCode查询无符合条件的数据" +
                                "sendCode[{0}],createSiteCode[{1}],receiveSiteCode[{2}],senddStatus[{3}]",
                        sendCode, createSiteCode, receiveSiteCode, senddStatus));
            }
        } catch (Exception e) {
            result.setCode(result.CODE_SERVICE_ERROR);
            result.setMessage("根据批次号补运单状态全程跟踪异常！");
            this.logger.error(MessageFormat.format("queryBySendCodeAndSiteCode查询无sendd补运单信息异常" +
                            "sendCode[{0}],createSiteCode[{1}],receiveSiteCode[{2}],senddStatus[{3}]",
                    sendCode, createSiteCode, receiveSiteCode, senddStatus), e);
        }
        return result;
    }
    @POST
    @Path("/delivery/sendBatch")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.sendBatch", mState = {JProEnum.TP})
    public DeliveryResponse sendBatch(DeliveryRequest request) {
        this.logger.info("开始批量发货写入信息");
        if (check(request)) {
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        DeliveryResponse tDeliveryResponse = deliveryService.dealWithSendBatch(toSendDatail(request));
        this.logger.info("结束批量发货写入信息");
        if (tDeliveryResponse != null) {
            return tDeliveryResponse;
        } else {
            return new DeliveryResponse(JdResponse.CODE_NOT_FOUND,
                    JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    private List<SendM> toAutoBatchSend(DeliveryBatchRequest request) {
        List<SendM> sendMList = new ArrayList<SendM>();
        if (request != null) {

            List<DeliveryRequest> deliverys = new ArrayList<DeliveryRequest>();
            deliverys = request.getDeliverys();
            for (DeliveryRequest delivery : deliverys) {
                SendM sendM = new SendM();
                sendM.setBoxCode(delivery.getBoxCode());
                sendM.setCreateSiteCode(request.getSiteCode());
                sendM.setReceiveSiteCode(delivery.getReceiveSiteCode());
                sendM.setCreateUserCode(request.getUserCode());
                sendM.setSendType(request.getBusinessType());
                sendM.setCreateUser(request.getUserName());
                sendM.setSendCode(delivery.getSendCode());
                sendM.setCreateTime(new Date());
                sendM.setOperateTime(new Date());
                sendM.setYn(1);
                sendM.setTurnoverBoxCode(delivery.getTurnoverBoxCode());
                sendM.setTransporttype(delivery.getTransporttype());
                sendMList.add(sendM);
            }
        }
        return sendMList;
    }

    private boolean checkAutoBatchSend(DeliveryBatchRequest request) {
        if (request == null || request.getDeliverys() == null || request.getSiteCode() == null) {
            return true;
        }
        return false;
    }

    /**
     * 自动化分拣发货处理，箱号字段为多个箱号
     *
     * @param request
     * @return
     */
    @POST
    @Path("/delivery/autoBatchSend")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.atuoBatchSend", mState = {JProEnum.TP})
    public DeliveryResponse autoBatchSend(DeliveryBatchRequest request) {
        this.logger.info("batchSend开始批量发货写入信息");
        if (checkAutoBatchSend(request)) {
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        DeliveryResponse tDeliveryResponse = deliveryService.autoBatchSend(toAutoBatchSend(request));
        this.logger.info("结束批量发货写入信息");
        if (tDeliveryResponse != null) {
            return tDeliveryResponse;
        } else {
            return new DeliveryResponse(JdResponse.CODE_NOT_FOUND,
                    JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    @GET
    @Path("/delivery/SendDifference/{sendCode}")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.SendDifference", mState = {JProEnum.TP})
    public SendDifference sendDifference(@PathParam("sendCode") String sendCode) {
        SendDifference sendDifference = new SendDifference();
        if (sendCode == null) {
            return new SendDifference(JdResponse.CODE_OK_NULL, JdResponse.MESSAGE_OK_NULL);
        }
        try {
            sendDifference = sendQueryService.querySendDifference(sendCode);
            return sendDifference;
        } catch (Exception ex) {
            this.logger.error("调用监控-发货运输差异仓查询异常：", ex);
            return new SendDifference(JdResponse.CODE_SERVICESEND_ERROR, JdResponse.MESSAGE_SERVICESEND_ERROR);
        }

    }
    
    /**
     * 手动获取设备对应的批次号
     *
     * @param config
     * @return
     */
    @POST
    @Path("/delivery/handAchieveSendCode")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.handAchieveSendCode", mState = {JProEnum.TP})
    public ScannerFrameBatchSendResponse handAchieveSendCode(SendGantryDeviceConfig config) {
        this.logger.info("手动获取设备对应的批次号");
        ScannerFrameBatchSend scannerFrameBatchSend = scannerFrameBatchSendService.getOrGenerate(config.getOperateTime(), config.getReceiveSiteCode(), config.getConfig());
        
        if (scannerFrameBatchSend != null) {
        	ScannerFrameBatchSendResponse response = new ScannerFrameBatchSendResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
        	response.setSendCode(scannerFrameBatchSend.getSendCode());
            return response;
        } else {
            return new ScannerFrameBatchSendResponse(JdResponse.CODE_INTERNAL_ERROR,
                    "未查询到对应的批次号sendCode，请确认！");
        }
    }

    private SendM toSendDatail(DeliveryRequest deliveryRequest) {
        SendM sendM = new SendM();
        if (deliveryRequest != null) {
            sendM.setBoxCode(deliveryRequest.getBoxCode());
            sendM.setCreateSiteCode(deliveryRequest.getSiteCode());
            sendM.setReceiveSiteCode(deliveryRequest.getReceiveSiteCode());
            sendM.setCreateUserCode(deliveryRequest.getUserCode());
            sendM.setSendType(deliveryRequest.getBusinessType());
            sendM.setCreateUser(deliveryRequest.getUserName());
            sendM.setSendCode(deliveryRequest.getSendCode());
            sendM.setCreateTime(new Date());
            sendM.setOperateTime(new Date());
            sendM.setYn(1);
            sendM.setTurnoverBoxCode(deliveryRequest.getTurnoverBoxCode());
            sendM.setTransporttype(deliveryRequest.getTransporttype());
        }
        return sendM;
    }

    private boolean check(DeliveryRequest deliveryRequest) {
        if (deliveryRequest == null) {
            return true;
        } else if (deliveryRequest.getSiteCode() == null
                || deliveryRequest.getBusinessType() == null
                || deliveryRequest.getSendCode() == null) {
            return true;
        }
        return false;
    }
    @POST
    @Path("/delivery/reSendBySendM")
    public InvokeResult<Map<String,Object>> reSendBySendM(SendM condition) {
    	InvokeResult<Map<String,Object>> res = new InvokeResult<Map<String,Object>>();
    	Map<String,Object> restData = new HashMap<String,Object>();
    	List<SendM> sendMList = sendMDao.queryListByCondition(condition);
    	if(sendMList!=null){
    		for(SendM sendM:sendMList){
    			deliveryService.pushStatusTask(sendM);
    		}
    	}
    	res.setData(restData);
    	return res;
    }
    @POST
    @Path("/delivery/pushStatusTask")
    public InvokeResult<Boolean> pushStatusTask(SendM sendM) {
    	InvokeResult<Boolean> res = new InvokeResult<Boolean>();
    	deliveryService.pushStatusTask(sendM);
    	return res;
    }
    @POST
    @Path("/delivery/querySendMListByCondition")
    public InvokeResult<List<SendM>> querySendMListByCondition(SendM condition) {
    	InvokeResult<List<SendM>> res = new InvokeResult<List<SendM>>();
    	res.setData(sendMDao.queryListByCondition(condition));
    	return res;
    }
    @POST
    @Path("/delivery/querySendDListByCondition")
    public InvokeResult<List<SendDetail>> querySendDListByCondition(SendDetail condition) {
    	InvokeResult<List<SendDetail>> res = new InvokeResult<List<SendDetail>>();
    	res.setData(sendDatailDao.queryListByCondition(condition));
    	return res;
    }
}
