package com.jd.bluedragon.distribution.rest.send;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ServiceMessage;
import com.jd.bluedragon.common.domain.ServiceResultEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.DeliveryBatchRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.DifferentialQueryRequest;
import com.jd.bluedragon.distribution.api.request.PackageCodeRequest;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.request.RecyclableBoxRequest;
import com.jd.bluedragon.distribution.api.response.CheckBeforeSendResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.api.response.ScannerFrameBatchSendResponse;
import com.jd.bluedragon.distribution.api.response.WhBcrsQueryResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.gantry.domain.SendGantryDeviceConfig;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.jsf.domain.WhemsWaybillResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
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
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.service.SendQueryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.LongHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

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
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DeliveryResource {

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

    /**
     * 快运发货标识
     */
    private static final Integer KY_DELIVERY = 1;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private SendMDao sendMDao;
    
    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private WaybillPackageBarcodeService waybillPackageBarcodeService;

    @Autowired
    private CycleBoxService cycleBoxService;

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;
    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

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
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1001)
    public InvokeResult<SendResult> newPackageSend(PackageSendRequest request) {
        if (log.isInfoEnabled()) {
            log.info(JsonHelper.toJson(request));
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.newPackageSend", Constants.UMP_APP_NAME_DMSWEB,false, true);
        SendM domain = this.toSendMDomain(request);
        InvokeResult<SendResult> result = new InvokeResult<SendResult>();
        try {
            if (BusinessUtil.isBoardCode(request.getBoxCode())) {
                // 一车一单下的组板发货
                domain.setBoardCode(request.getBoxCode());
                log.warn("组板发货newpackagesend：{}" , JsonHelper.toJson(request));
                result.setData(deliveryService.boardSend(domain, request.getIsForceSend()));
            } else {
                SendBizSourceEnum bizSource = SendBizSourceEnum.getEnum(request.getBizSource());
                // 一车一单发货
                domain.setBoxCode(request.getBoxCode());
                if (request.getIsCancelLastSend() == null) {
                    result.setData(deliveryService.packageSend(bizSource, domain, request.getIsForceSend()));
                } else {
                    result.setData(deliveryService.packageSend(bizSource, domain, request.getIsForceSend(), request.getIsCancelLastSend()));
                }
            }
        } catch (Exception ex) {
            Profiler.functionError(info);
            result.error(ex);
            log.error("一车一单发货{}",JsonHelper.toJson(request), ex);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        if (log.isInfoEnabled()) {
            log.info(JsonHelper.toJson(result));
        }
        return result;
    }

    /**
     * 请求拼装SendM发货对象
     *
     * @param request
     * @return
     */
    private SendM toSendMDomain(PackageSendRequest request) {
        SendM domain = new SendM();
        domain.setReceiveSiteCode(request.getReceiveSiteCode());
        domain.setSendCode(request.getSendCode());
        domain.setCreateSiteCode(request.getSiteCode());

        String turnoverBoxCode = request.getTurnoverBoxCode();
        if (StringUtils.isNotBlank(turnoverBoxCode) && turnoverBoxCode.length() > 30) {
            domain.setTurnoverBoxCode(turnoverBoxCode.substring(0, 30));
        } else {
            domain.setTurnoverBoxCode(turnoverBoxCode);
        }
        domain.setCreateUser(request.getUserName());
        domain.setCreateUserCode(request.getUserCode());
        domain.setSendType(request.getBusinessType());
        domain.setTransporttype(request.getTransporttype());

        domain.setYn(1);
        domain.setCreateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        domain.setOperateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        return domain;
    }

    @GET
    @Path("/delivery/checksendcodestatus/{sendCode}")
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
                log.error("发货校验批次号异常：{}",sendCode, ex);
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
            this.log.error("一车一单发货通过站点ID获取基础资料失败:{}",receiveSiteCode,e);
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
            this.log.warn("一车一单发获取站点信息为空：{}" , receiveSiteCode);
        }
        return false;
    }

    @POST
    @Path("/delivery/cancel")
    @BusinessLog(sourceSys = 1,bizType = 100,operateType = 1003)
    public ThreeDeliveryResponse cancelDeliveryInfo(DeliveryRequest request) {
        if(log.isDebugEnabled()){
            log.debug("取消发货JSON" + JsonHelper.toJson(request));
        }
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
            this.log.error("写入取消发货信息失败：{}",JsonHelper.toJson(request), e);
        }
        if (tDeliveryResponse != null) {
            return tDeliveryResponse;
        } else {
            return new ThreeDeliveryResponse(JdResponse.CODE_NOT_FOUND,
                    JdResponse.MESSAGE_SERVICE_ERROR, null);
        }
    }

    @POST
    @Path("/delivery/cancel/last")
    @BusinessLog(sourceSys = 1,bizType = 100,operateType = 1004)
    public ThreeDeliveryResponse cancelLastDeliveryInfo(DeliveryRequest request) {
        if(log.isDebugEnabled()){
            log.debug("取消最近的一次发货JSON" , JsonHelper.toJson(request));
        }

        //参数校验
        if (StringHelper.isEmpty(request.getBoxCode()) == null || request.getSiteCode() == null || StringHelper.isEmpty(request.getOperateTime())) {
            return new ThreeDeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR, null);
        }

        ThreeDeliveryResponse tDeliveryResponse = null;
        try {
            tDeliveryResponse = deliveryService.cancelLastSend(toSendM(request));
        } catch (Exception e) {
            this.log.error("写入取消最近的一次发货信息失败：{}",JsonHelper.toJson(request), e);
        }

        if (tDeliveryResponse != null) {
            return tDeliveryResponse;
        } else {
            return new ThreeDeliveryResponse(JdResponse.CODE_NOT_FOUND, JdResponse.MESSAGE_SERVICE_ERROR, null);
        }
    }

    @POST
    @Path("/delivery/recyclableboxsend")
    public InvokeResult recyclableBoxSend(RecyclableBoxRequest request) {
        InvokeResult result = new InvokeResult();
        try {
            if (log.isDebugEnabled()) {
                log.debug("循环箱MQ-JSON：" , JsonHelper.toJson(request));
            }
            cycleBoxService.recyclableBoxSend(request);
        }catch (Exception e){
            log.error("循环箱发送MQ异常:{}",JsonHelper.toJson(request),e);
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 取消发货 要判断是否已经装载， 未装载和拒绝的可以 取消 发货\分拣
     */
    private DeliveryResponse sendLoadBillCheck(DeliveryRequest request) {
        if (request.getBoxCode() == null || request.getSiteCode() == null) {
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        LoadBillReport loadBillReport = new LoadBillReport();
        if (BusinessHelper.isBoxcode(request.getBoxCode())) {
            loadBillReport.setBoxCode(request.getBoxCode());
        } else {
            loadBillReport.setWaybillCode(request.getBoxCode());
        }
        this.log.debug("开始获取 装载数据");
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
            this.log.error("开始获取 装载数据 失败 findWaybillInLoadBill:{}",JsonHelper.toJson(request), e);
        }

        return new DeliveryResponse(JdResponse.CODE_UNLOADBILL,
                JdResponse.MESSAGE_UNLOADBILL);
    }

    /**
     * 老发货接口
     *
     * @param request
     * @return
     */
    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.delivery.sendPack", mState = {JProEnum.TP, JProEnum.FunctionError})
    @POST
    @Path("/delivery/send")
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1002)
    public DeliveryResponse sendDeliveryInfo(List<DeliveryRequest> request) {
        if(log.isDebugEnabled()){
            this.log.debug("开始写入发货信息:{}" , JsonHelper.toJson(request));
        }
        if (check(request)) {
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        DeliveryResponse tDeliveryResponse = null;

        Integer opType = request.get(0).getOpType();
        if (KY_DELIVERY.equals(opType)) {
            tDeliveryResponse = this.sendDeliveryInfoForKY(request);
        } else {
            Integer businessType = request.get(0).getBusinessType();
            //获取批量参数中的批次号
            String sendCode = request.get(0).getSendCode();
            String redisKey = businessType + Constants.SEPARATOR_HYPHEN + sendCode;

            try {
                //查询redis中是否存在key
                if (jimdbCacheService.exists(redisKey)) {
                    log.warn("发货任务已提交，批次号：{}", sendCode);
                    tDeliveryResponse = new DeliveryResponse(DeliveryResponse.CODE_DELIVERY_SEND_CODE_IS_COMMITTED, DeliveryResponse.MESSAGE_DELIVERY_SEND_CODE_IS_COMMITTED);
                } else {
                    jimdbCacheService.setEx(redisKey, sendCode, 5 * DateHelper.ONE_MINUTES_MILLI);
                    if (businessType != null && Constants.BUSSINESS_TYPE_REVERSE == businessType) {
                        // 逆向发货
                        tDeliveryResponse = deliveryService.dellDeliveryMessage(SendBizSourceEnum.REVERSE_SEND, toSendDetailList(request));
                    } else {
                        // 正向老发货
                        tDeliveryResponse = deliveryService.dellDeliveryMessage(SendBizSourceEnum.OLD_PACKAGE_SEND, toSendDetailList(request));
                    }

                    if (! JdResponse.CODE_OK.equals(tDeliveryResponse.getCode())) {
                        //业务拦截，删除缓存中的key
                        jimdbCacheService.del(redisKey);
                    }
                }
            } catch (Exception e) {
                log.error("老发货执行失败，请求：{}", JsonHelper.toJson(request), e);
            }
        }
        this.log.debug("结束写入发货信息");
        if (tDeliveryResponse != null) {
            return tDeliveryResponse;
        } else {
            return new DeliveryResponse(JdResponse.CODE_NOT_FOUND,
                    JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    private DeliveryResponse sendDeliveryInfoForKY(List<DeliveryRequest> request){
        List<SendM> waybillCodeSendMList = this.assembleSendMForWaybillCode(request);
        List<SendM> otherSendMList = this.assembleSendMWithoutWaybillCode(request);
        if (waybillCodeSendMList.size() == 0) {
            return deliveryService.dellDeliveryMessage(SendBizSourceEnum.RAPID_TRANSPORT_SEND, otherSendMList);
        }
        /** 快运发货 */
        DeliveryResponse response = deliveryService.dellDeliveryMessageWithLock(SendBizSourceEnum.RAPID_TRANSPORT_SEND, waybillCodeSendMList);
        if (JdResponse.CODE_OK.equals(response.getCode()) && otherSendMList!=null && otherSendMList.size()>0) {
            return deliveryService.dellDeliveryMessage(SendBizSourceEnum.RAPID_TRANSPORT_SEND, otherSendMList);
        }
        return response;
    }

    /**
     * 快运发货差异查询
     *
     * @param request
     * @return
     */
    @POST
    @Path("/delivery/differentialQuery")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.differentialQuery", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public ThreeDeliveryResponse differentialQuery(DifferentialQueryRequest request) {
        if(log.isDebugEnabled()){
            this.log.debug("快运发货差异查询:{}" , JsonHelper.toJson(request));
        }
        try {
            if (check(request.getSendList())) {
                return new ThreeDeliveryResponse(
                        JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR, null);
            }
            Integer queryType = request.getQueryType();
            Integer opType = request.getSendList().get(0).getOpType();
            ThreeDeliveryResponse response = null;
            /** 快运发货 */
            if (KY_DELIVERY.equals(opType)) {
                response =
                        deliveryService.differentialQuery(toSendDetailList(request.getSendList()), queryType);
            }
            this.log.debug("结束快运发货差异查询");
            return response;
        } catch (Exception ex) {
            log.error("快运发货差异查询:{}",JsonHelper.toJson(request), ex);
            return new ThreeDeliveryResponse(JdResponse.CODE_INTERNAL_ERROR, ex.getMessage(), null);
        }
    }

    @POST
    @Path("/delivery/verification")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.verification", mState = {JProEnum.TP})
    public ThreeDeliveryResponse checkThreeDelivery(List<DeliveryRequest> request) {
        if(log.isDebugEnabled()){
            this.log.debug("开始三方发货不全验证:{}",JsonHelper.toJson(request));
        }
        try {
            if (check(request)) {
                return new ThreeDeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                        JdResponse.MESSAGE_PARAM_ERROR, null);
            }
            Integer opType = request.get(0).getOpType();
            ThreeDeliveryResponse response = null;
            if(KY_DELIVERY.equals(opType)){//快运发货
                response =  deliveryService.checkThreePackageForKY(toSendDetailList(request));
            }else{
                response =  deliveryService.checkThreePackage(toSendDetailList(request));
            }
            this.log.debug("结束三方发货不全验证");
            return response;
        } catch (Exception ex) {
            log.error("发货不全验证异常：{}",JsonHelper.toJson(request), ex);
            return new ThreeDeliveryResponse(JdResponse.CODE_INTERNAL_ERROR, ex.getMessage(), null);
        }
    }

    @POST
    @Path("/delivery/router/verification")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.router.verification", mState = {JProEnum.TP})
    public DeliveryResponse checkThreeDeliveryOld(DeliveryRequest request) {
        return checkThreeDelivery(request,Constants.DELIVERY_ROUTER_VERIFICATION_OLD);
    }

    @POST
    @Path("/delivery/router/verification/new")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.router.verification.new", mState = {JProEnum.TP}, jAppName=Constants.UMP_APP_NAME_DMSWEB)
    public DeliveryResponse checkThreeDeliveryNew(DeliveryRequest request) {
        return checkThreeDelivery(request,Constants.DELIVERY_ROUTER_VERIFICATION_NEW);
    }

    /**
     * @param flag 新老版本标识，0是老版本调用，1是新版本调用接口
     */
    private DeliveryResponse checkThreeDelivery(DeliveryRequest request,Integer flag) {
        try {
            if (request == null || StringUtils.isBlank(request.getBoxCode()) ||
                    request.getSiteCode() == null || request.getReceiveSiteCode() == null) {
                return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
            }

            //如果扫描的是运单号，判断是否是B冷链操作的快运发货
            if(isWaybillCode(request.getBoxCode())){
                DeliveryResponse response = isValidWaybillCode(request);
                if(!JdResponse.CODE_OK.equals(response.getCode())){
                    return response;
                }
            }
            //校验滑道号
            if(WaybillUtil.isPackageCode(request.getBoxCode()) && !checkPackageCrossCodeSucc(request.getBoxCode())){
                log.warn("滑道号不正确[{}]",request.getBoxCode());
                return new DeliveryResponse(DeliveryResponse.CODE_CROSS_CODE_ERROR, DeliveryResponse.MESSAGE_CROSS_CODE_ERROR);
            }

            Integer opType = request.getOpType();
            DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
            if(KY_DELIVERY.equals(opType)){//只有快运发货才做路由校验
                // 因为B冷链转运中心需要支持扫描运单号发货，
                // 如果扫的是运单号，则生成第一个包裹号，用于校验
                if (isWaybillCode(request.getBoxCode())) {
                    List<String> waybillCodeList = waybillPackageBarcodeService.getPackageCodeListByWaybillCode(request.getBoxCode());
                    if(waybillCodeList == null || waybillCodeList.size() < 1){
                        log.warn("快运发货扫运单号，根据运单号[{}]生成包裹号失败.没有运单/包裹信息",request.getBoxCode());
                        response.setCode(JdResponse.CODE_CAN_NOT_GENERATE_PACKAGECODE);
                        response.setMessage(MessageFormat.format(JdResponse.MESSAGE_CAN_NOT_GENERATE_PACKAGECODE,request.getBoxCode()));
                        return response;
                    }
                    request.setBoxCode(waybillCodeList.get(0));
                }
                response =  deliveryService.checkRouterForKY(deliveryRequest2SendM(request), flag);
            }
            return response;
        } catch (Exception ex) {
            log.error("快运发货路由验证出错：{}", JsonHelper.toJson(request),ex);
            return new DeliveryResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    @POST
    @Path("/delivery/performacne/verification")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.performacne.verification", mState = {JProEnum.TP})
    public DeliveryResponse checkJpWaybill(DeliveryRequest request) {
        try {
            if (request == null || StringUtils.isBlank(request.getBoxCode()) ||
                    request.getSiteCode() == null || request.getReceiveSiteCode() == null) {
                return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
            }

            //如果扫描的是运单号，判断是否是B冷链操作的快运发货
            if(isWaybillCode(request.getBoxCode())){
                DeliveryResponse response = isValidWaybillCode(request);
                if(!JdResponse.CODE_OK.equals(response.getCode())){
                    return response;
                }
            }

            DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
            Integer opType = request.getOpType();

            if(KY_DELIVERY.equals(opType)){
                //快运发货金鹏订单拦截提示
                if(JdResponse.CODE_OK.equals(response.getCode())){
                    String waybillCode = "";
                    if(WaybillUtil.isPackageCode(request.getBoxCode())){
                        waybillCode = WaybillUtil.getWaybillCode(request.getBoxCode());
                    }else if(!BusinessUtil.isBoxcode(request.getBoxCode()) && WaybillUtil.isWaybillCode(request.getBoxCode())){
                        waybillCode = request.getBoxCode();
                    }
                    response = deliveryService.dealJpWaybill(request.getSiteCode(),waybillCode);
                }
            }
            return response;
        } catch (Exception ex) {
            log.error("快运发货金鹏订单拦截出错：{}",JsonHelper.toJson(request), ex);
            return new DeliveryResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    /**
     * B冷链转运中心--快运发货支持扫描运单号发货
     * 如果扫描的是运单号，判断是否符是B冷链转运中心 && 入口是快运发货
     * @param request
     * @return
     */
    private DeliveryResponse isValidWaybillCode(DeliveryRequest request){
        Integer opType = request.getOpType();
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

        //判断是否是正确的箱号/包裹号--仅B冷链转运中心6460快运发货支持扫运单
        //登录人机构不是冷链分拣中心
        BaseStaffSiteOrgDto siteOrgDto = siteService.getSite(request.getSiteCode());
        if (siteOrgDto == null) {
            response.setCode(JdResponse.CODE_NO_SITE);
            response.setMessage(MessageFormat.format(JdResponse.MESSAGE_NO_SITE, request.getSiteCode()));
            return response;
        }
        if (!(Constants.B2B_CODE_SITE_TYPE.equals(siteOrgDto.getSubType())&& KY_DELIVERY.equals(opType)) ) {
            response.setCode(JdResponse.CODE_INVALID_PACKAGECODE_BOXCODE);
            response.setMessage(JdResponse.MESSAGE_INVALID_PACKAGECODE_BOXCODE);
            return response;
        }
        return response;
    }

    /**
     * 严格判断是否是运单号
     * 不是包裹号&&不是箱号&&是运单号
     * @param waybillCode
     * @return
     */
    private boolean isWaybillCode(String waybillCode){
        if(StringUtils.isBlank(waybillCode)){
            return false;
        }
        return !WaybillUtil.isPackageCode(waybillCode) && !BusinessHelper.isBoxcode(waybillCode)
                && WaybillUtil.isWaybillCode(waybillCode);
    }

    @POST
    @Path("/delivery/sortingdiff")
    public ThreeDeliveryResponse checkSortingDiff(DeliveryRequest request) {
        String boxCode = request.getBoxCode();
        Integer createSiteCode = request.getSiteCode();
        Integer receiveSiteCode = request.getReceiveSiteCode();
        this.log.debug("开始一单多件包裹不全验证,createSiteCode[{}],receiveSiteCode[{}],boxCode[{}]",createSiteCode,receiveSiteCode,boxCode);

        if (boxCode == null || boxCode.trim().equals("")
                || createSiteCode == null || receiveSiteCode == null) {
            return new ThreeDeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR, null);
        }

        List<SendThreeDetail> tDeliveryResponse = null;
        try {
            tDeliveryResponse = deliveryService.checkSortingDiff(boxCode, createSiteCode, receiveSiteCode);
            this.log.debug("结束一单多件包裹不全验证");
        } catch (Exception e) {
            this.log.error("一单多件包裹不全验证异常:{}",JsonHelper.toJson(request), e);
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
        this.log.debug("开始补全发货明细表包裹数量,,createSiteCode[{}],receiveSiteCode[{}],boxCode[{}]",createSiteCode,receiveSiteCode,boxCode);
        Integer updatedNum = 0;
        String msg;

        long startTime = System.currentTimeMillis();
        try {
            updatedNum = deliveryService.appendPackageNum(boxCode, createSiteCode, receiveSiteCode);
            this.log.debug("结束补全发货明细表包裹数量");
        } catch (Exception e) {
            msg = "补全发货明细表包裹数量失败";
            return new DeliveryResponse(JdResponse.CODE_OK, msg);
        }
        long endTime = System.currentTimeMillis();

        long cost = endTime - startTime;
        msg = JdResponse.MESSAGE_OK + ": 更新数量[" + updatedNum + "],花费时间[" + cost + "ms]";
        return new DeliveryResponse(JdResponse.CODE_OK, msg);
    }
    /**
     * 老发货、快运发货扫描箱号校验
     * @param boxCode
     * @param siteCode
     * @param receiveSiteCode
     * @param businessType
     * @return
     */
    @GET
    @Path("/delivery/check")
    public DeliveryResponse checkDeliveryInfo(@QueryParam("boxCode") String boxCode,
                                              @QueryParam("siteCode") String siteCode,
                                              @QueryParam("receiveSiteCode") String receiveSiteCode,
                                              @QueryParam("businessType") String businessType) {
        if (boxCode == null || siteCode == null || businessType == null || receiveSiteCode == null) {
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
        }
        return doCheckDeliveryInfo(boxCode,Integer.parseInt(siteCode),Integer.parseInt(receiveSiteCode),Integer.parseInt(businessType));
    }
    /**
     * 老发货、快运发货扫描箱号校验
     * @param boxCode
     * @param siteCode
     * @param receiveSiteCode
     * @param businessType
     * @return
     */
    private DeliveryResponse doCheckDeliveryInfo(String boxCode,
                Integer siteCode,
                Integer receiveSiteCode,
                Integer businessType) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DeliveryResource.checkDeliveryInfo", Constants.UMP_APP_NAME_DMSWEB,false, true);
        SendM tSendM = new SendM();
        tSendM.setBoxCode(boxCode);
        tSendM.setCreateSiteCode(siteCode);
        tSendM.setReceiveSiteCode(receiveSiteCode);
        tSendM.setSendType(businessType);

        try {
            boolean isTransferSend = deliveryService.isTransferSend(tSendM);
            DeliveryResponse tDeliveryResponse = deliveryService.findSendMByBoxCode(tSendM, isTransferSend);
            this.log.debug("结束验证箱号信息");
            if (tDeliveryResponse != null) {
                //设置运单类型
                String waybillCodeToJudgeType = null;
                if(WaybillUtil.isPackageCode(boxCode)){
                    waybillCodeToJudgeType = WaybillUtil.getWaybillCode(boxCode);
                }else if(BusinessUtil.isBoxcode(boxCode)){
                    //从箱子中取出一单
                    List<String> waybillCodeList = deliveryService.getWaybillCodesByBoxCodeAndFetchNum(boxCode,1);
                    if(waybillCodeList != null && waybillCodeList.size() > 0){
                        waybillCodeToJudgeType = waybillCodeList.get(0);
                    }
                }
                //获取运单类型
                Integer waybillType = waybillService.getWaybillTypeByWaybillSign(waybillCodeToJudgeType);
                tDeliveryResponse.setWaybillType(waybillType);

                if(JdResponse.CODE_OK.equals(tDeliveryResponse.getCode())){

                    //added by hanjiaxing3 2018.10.12 delivered is not allowed to reverse
                    if (WaybillUtil.isPackageCode(boxCode)) {
                        CallerInfo reverseCheckInfo = Profiler.registerInfo("DMSWEB.DeliveryResource.checkDeliveryInfo.reverseCheckInfo", Constants.UMP_APP_NAME_DMSWEB,false, true);
                        try {
                            BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
                            if (baseStaffSiteOrgDto != null) {
                                Integer siteType = baseStaffSiteOrgDto.getSiteType();
                                //售后
                                String asm_type = PropertiesHelper.newInstance().getValue("asm_type");
                                //仓储
                                String wms_type = PropertiesHelper.newInstance().getValue("wms_type");
                                //备件库退货
                                String spwms_type = PropertiesHelper.newInstance().getValue("spwms_type");
                                if (siteType == Integer.parseInt(asm_type) || siteType == Integer.parseInt(wms_type) || siteType == Integer.parseInt(spwms_type)) {
                                    String waybillCode = WaybillUtil.getWaybillCode(boxCode);
                                    InvokeResult<Boolean> result = waybillService.isReverseOperationAllowed(waybillCode, siteCode);
                                    if(result != null && InvokeResult.RESULT_SUCCESS_CODE != result.getCode()) {
                                        return new DeliveryResponse(result.getCode(), result.getMessage());
                                    }
                                }else{
                                    //校验滑道号
                                    if(!checkPackageCrossCodeSucc(boxCode)){
                                        this.log.warn("滑道号不正确[{}]",boxCode);
                                        return new DeliveryResponse(DeliveryResponse.CODE_CROSS_CODE_ERROR, DeliveryResponse.MESSAGE_CROSS_CODE_ERROR);
                                    }
                                }
                            } else{
                                this.log.warn("发货校验获取站点信息为空：{}" , receiveSiteCode);
                            }
                        } catch (Exception e) {
                            Profiler.functionError(reverseCheckInfo);
                            this.log.error("发货校验获取站点信息失败，站点编号:{}" , receiveSiteCode, e);
                        }finally {
                            Profiler.registerInfoEnd(reverseCheckInfo);
                        }
                    }

                    return tDeliveryResponse;
                    //adder end
                }else{
                    return tDeliveryResponse;
                }
            } else {
                return new DeliveryResponse(JdResponse.CODE_NOT_FOUND, JdResponse.MESSAGE_SERVICE_ERROR);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("发货校验异常:{}",JsonHelper.toJson(tSendM), e);
            return new DeliveryResponse(JdResponse.CODE_NOT_FOUND, JdResponse.MESSAGE_SERVICE_ERROR);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    @POST
    @Path("/delivery/whemsWaybill")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.getWhemsWaybill", mState = {JProEnum.TP})
    public WhemsWaybillResponse getWhemsWaybill(List<String> request, @Context HttpServletRequest servletRequest) {

        this.log.warn("servletRequest.getHeader()={}" , servletRequest.getHeader("X-Forwarded-For"));
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
        for (String waybillCode : request) {
            reverseDelivery.pushWhemsWaybill(waybillCode);
        }
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
        sendM.setSendCode(request.getSendCode());
        Date operateTime = DateHelper.parseDate(request.getOperateTime(), Constants.DATE_TIME_FORMAT);
        sendM.setOperateTime(operateTime);
        if (!BusinessHelper.isBoxcode(request.getBoxCode())) {
            sendM.setReceiveSiteCode(request.getReceiveSiteCode());
        }
        sendM.setUpdateTime(new Date());
        sendM.setYn(0);
        return sendM;
    }

    private List<SendM> toSendDetailList(List<DeliveryRequest> request) {
        List<SendM> sendMList = new ArrayList<SendM>();
        if (request != null && !request.isEmpty()) {
            for (DeliveryRequest deliveryRequest : request) {
                if (WaybillUtil.isPackageCode(deliveryRequest.getBoxCode()) || BusinessHelper.isBoxcode(deliveryRequest.getBoxCode())) {
                    sendMList.add(deliveryRequest2SendM(deliveryRequest));
                } else if (WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())) {
                    //B冷链快运发货支持扫运单号发货
                    DeliveryResponse response = isValidWaybillCode(deliveryRequest);
                    if (!JdResponse.CODE_OK.equals(response.getCode())) {
                        log.warn("DeliveryResource--toSendDatailList出现运单号，但非冷链快运发货,siteCode:{},单号:{}",
                                deliveryRequest.getSiteCode() , deliveryRequest.getBoxCode());
                    } else {
                        sendMList.addAll(deliveryRequest2SendMList(deliveryRequest));
                    }
                } else {
                    sendMList.add(deliveryRequest2SendM(deliveryRequest));
                }
            }
        }
        return sendMList;
    }

    protected <T extends DeliveryRequest> List<SendM> assembleSendMForWaybillCode(List<T> request) {
        List<SendM> sendMList = new ArrayList<>();
        if (request != null && !request.isEmpty()) {
            for (DeliveryRequest deliveryRequest : request) {
                if (WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())) {
                    //B冷链快运发货支持扫运单号发货
                    DeliveryResponse response = isValidWaybillCode(deliveryRequest);
                    if (!JdResponse.CODE_OK.equals(response.getCode())) {
                        log.warn("DeliveryResource--toSendDetailList出现运单号，但非冷链快运发货，siteCode:{},单号:{}",
                                deliveryRequest.getSiteCode() , deliveryRequest.getBoxCode());
                        continue;
                    }
                    sendMList.add(deliveryRequest2SendM(deliveryRequest));
                }
            }
        }
        return sendMList;
    }

    protected <T extends DeliveryRequest> List<SendM> assembleSendMWithoutWaybillCode(List<T> request) {
        List<SendM> sendMList = new ArrayList<>();
        if (request != null && !request.isEmpty()) {
            for (DeliveryRequest deliveryRequest : request) {
                if (!WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())) {
                    sendMList.add(deliveryRequest2SendM(deliveryRequest));
                }
            }
        }
        return sendMList;
    }

    /**
     * 只校验包裹的 校验滑道号
     * @return true 滑道号正确，或者非包裹号，false 不正确
     */
    private boolean checkPackageCrossCodeSucc(String packageCode){
        return jsfSortingResourceService.checkPackageCrossCode(WaybillUtil.getWaybillCode(packageCode),packageCode);
    }

    /**
     * 根据DeliveryRequest对象转成SendM列表
     * 注意：DeliveryRequest中的boxCode对应运单号
     * @param deliveryRequest
     * @return
     */
    private List<SendM> deliveryRequest2SendMList(DeliveryRequest deliveryRequest){
        List<SendM> sendMList = new ArrayList<SendM>();
        if(WaybillUtil.isPackageCode(deliveryRequest.getBoxCode()) || BusinessHelper.isBoxcode(deliveryRequest.getBoxCode())){
            sendMList.add(deliveryRequest2SendM(deliveryRequest));
        }else if(WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())){
            //生成包裹号
            List<String> packageCodes = waybillPackageBarcodeService.getPackageCodeListByWaybillCode(deliveryRequest.getBoxCode());
            for(String packageCode: packageCodes){
                SendM sendM = new SendM();
                sendM.setBoxCode(packageCode);
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

                sendMList.add(sendM);
            }
        }
        return sendMList;
    }

    /**
     * DeliveryRequest对象转sendM
     * @param deliveryRequest
     * @return
     */
    protected SendM deliveryRequest2SendM(DeliveryRequest deliveryRequest){
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
        if(!NumberUtils.isDigits(id)){
            return null;
        }
        try {
            List<Long> queueid = new ArrayList<>();
            queueid.add(LongHelper.strToLongOrNull(id));
            sendDetails = deliveryService.findWaybillStatus(queueid);
            if (sendDetails != null && !sendDetails.isEmpty()) {
                this.deliveryService.updateWaybillStatus(sendDetails);
                result = JsonHelper.toJsonUseGson(sendDetails);
            } else {
                log.warn("findWaybillStatus查询无符合条件:{}",id);
            }
        } catch (Exception e) {
            this.log.error("web补全包裹运单信息异常：{}",id, e);
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
                log.warn("queryBySendCodeAndSiteCode查询无符合条件的数据 sendCode[{}],createSiteCode[{}],receiveSiteCode[{}],senddStatus[{}]",
                        sendCode, createSiteCode, receiveSiteCode, senddStatus);
            }
        } catch (Exception e) {
            result.setCode(result.CODE_SERVICE_ERROR);
            result.setMessage("根据批次号补运单状态全程跟踪异常！");
            this.log.error("queryBySendCodeAndSiteCode查询无sendd补运单信息异常 sendCode[{}],createSiteCode[{}],receiveSiteCode[{}],senddStatus[{}]",
                    sendCode, createSiteCode, receiveSiteCode, senddStatus, e);
        }
        return result;
    }
    @POST
    @Path("/delivery/sendBatch")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.sendBatch", mState = {JProEnum.TP})
    public DeliveryResponse sendBatch(DeliveryRequest request) {
        this.log.debug("开始批量发货写入信息");
        if (check(request)) {
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        }
        DeliveryResponse tDeliveryResponse = deliveryService.dealWithSendBatch(toSendDatail(request));
        this.log.debug("结束批量发货写入信息");
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
            this.log.error("调用监控-发货运输差异仓查询异常：{}",sendCode, ex);
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
        this.log.debug("手动获取设备对应的批次号");
        ScannerFrameBatchSend scannerFrameBatchSend = scannerFrameBatchSendService.getOrGenerate(config.getOperateTime(), config.getReceiveSiteCode(), config.getConfig(),"");
        
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

    /**
     * 原包分拣发货
     * @author jinjingcheng
     * @param request
     * @return 发货结果 200成功
     */
    @POST
    @Path("/delivery/packageSortSend")
    public InvokeResult packageSortSend(PackageCodeRequest request){
        InvokeResult result = new InvokeResult();
        try {
            if(!packageSendCheckParam(request, result)){
                return result;
            }
            List<SendM> sendMListInit = initQueryCondition(request);
            for(SendM sendM : sendMListInit){
                /**根据条件获取SendM*/
                List<SendM> sendMList = deliveryService.queryCountByBox(sendM);
                if (sendMList.isEmpty()) {
                    deliveryService.packageSend(SendBizSourceEnum.OPEN_PLATFORM_SEND, sendM);
                }
            }

            result.success();
        }catch (Exception e){
            log.error("原包分拣发货异常:{}",JsonHelper.toJson(request), e);
            result.error("原包分拣发货异常");
        }

        return result;
    }

    /**
     * 原包分拣发货 参数校验
     * @param request
     * @param result
     * @return
     */
    private boolean packageSendCheckParam(PackageCodeRequest request, InvokeResult result){
        if(request.getDistributeId() == null){
            result.parameterError("发货分拣中心Id不能为空！");
            return false;
        }
        if(StringUtils.isBlank(request.getDistributeName())){
            result.parameterError("发货分拣中心名称不能为空！");
            return false;
        }
        if(StringUtils.isBlank(request.getOperatorName())){
            result.parameterError("操作人名称不能为空！");
            return false;
        }
        if(StringUtils.isBlank(request.getSendCode())){
            result.parameterError("批次号不能为空！");
            return false;
        }
        if(request.getOperatorId() == null){
            result.parameterError("操作人id不能为空！");
            return false;
        }
        if(request.getReceiveSiteCode() == null){
            result.parameterError("发货目的站点不能为空！");
            return false;
        }
        if(request.getOperateTime() == null){
            result.parameterError("操作时间不能为空！");
            return false;
        }
        if((request.getPackageList() == null || request.getPackageList().isEmpty()) && request.getBoxCode() == null){
            result.parameterError("包裹号和箱号不能同时为空！");
            return false;
        }
        /**
         * 验证发货分拣中心和目的站点是否存在
         */
        BaseStaffSiteOrgDto cbDto = null;
        BaseStaffSiteOrgDto rbDto = null;
        CallerInfo info = null;
        try {
            info = Profiler.registerInfo("DMSWEB.packageSortSend.checkSiteCode", Constants.UMP_APP_NAME_DMSWEB,
                    false,true);
            rbDto = this.baseMajorManager.getBaseSiteBySiteId(request.getReceiveSiteCode());
            cbDto = this.baseMajorManager.getBaseSiteBySiteId(request.getDistributeId());
            if(cbDto == null){
                cbDto = baseMajorManager.queryDmsBaseSiteByCodeDmsver(String.valueOf(request.getDistributeId()));
            }
            if (rbDto == null){
                rbDto = baseMajorManager.queryDmsBaseSiteByCodeDmsver(String.valueOf(request.getReceiveSiteCode()));
            }
            if(rbDto == null){
                result.parameterError(MessageFormat.format("发货目的站点编号[{0}]不合法，在基础资料未查到！",
                        request.getReceiveSiteCode()));
                return false;
            }
            if(cbDto == null){
                result.parameterError(MessageFormat.format("发货分拣中心id[{0}]不合法，在基础资料未查到！",
                        request.getDistributeId()));
                return false;
            }
        } catch (Exception e) {
            this.log.error("分拣开放平台调分拣发货接口校验参数，检查站点信息，调用站点信息异常:{}",JsonHelper.toJson(request), e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        /**检查批次号的合法 */
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(request.getSendCode());
        if(receiveSiteCode == null){
            result.parameterError(MessageFormat.format("发货批次号[{0}]不合法,正则校验未通过！",
                    request.getSendCode()));
            return false;
        }
        /**校验箱号是否符合规则*/
        if(request.getBoxCode()!=null && StringUtils.isBlank(request.getBoxCode())){
            if(!BusinessUtil.isBoxcode(request.getBoxCode())){
                result.parameterError(MessageFormat.format("发货箱号[{0}]不合法,正则校验未通过！",
                        request.getBoxCode()));
                return false;
            }

        }
        return true;
    }


    public List<SendM> initQueryCondition(PackageCodeRequest request) throws CloneNotSupportedException{

        SendM sendM = new SendM();
        sendM.setSendCode(request.getSendCode());
        sendM.setCreateSiteCode(request.getDistributeId());
        sendM.setCreateUser(request.getOperatorName());
        sendM.setReceiveSiteCode(request.getReceiveSiteCode());
        sendM.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        sendM.setYn(1);
        sendM.setCreateTime(new Date());
        sendM.setOperateTime(DateHelper.parseDate(request.getOperateTime(), DateHelper.DATE_TIME_FORMAT[0]));
        sendM.setCreateUserCode(request.getOperatorId());
        List<SendM> sendMList = new ArrayList<SendM>();
        boolean allNotEmpty = request.getPackageList() != null && !request.getPackageList().isEmpty() && request.getBoxCode() != null;
        boolean packageListNotEmpty = request.getPackageList() != null && !request.getPackageList().isEmpty() && request.getBoxCode() == null;
        boolean boxCodeNotEmpty = request.getBoxCode()!=null;
        //包裹号和箱号都不为空 按原包发货
        if(allNotEmpty || packageListNotEmpty){
            for(String packageCode : request.getPackageList()){
                SendM sendMClone = (SendM)sendM.clone();
                sendMClone.setBoxCode(packageCode);
                sendMList.add(sendMClone);
            }
        }else if(boxCodeNotEmpty){
            SendM sendMClone = (SendM)sendM.clone();
            sendMClone.setBoxCode(request.getBoxCode());
            sendMList.add(sendMClone);
        }

        return sendMList;
    }


    /**
     * 老发货校验
     * @param deliveryRequest
     * @return
     */
    @POST
    @Path("/delivery/packageSend/check")
    public DeliveryResponse packageSendCheck(DeliveryRequest deliveryRequest) {
        DeliveryResponse response = new DeliveryResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);

        try {
            JdResult result = jsfSortingResourceService.packageSendCheck(deliveryRequest);
            if(log.isDebugEnabled()){
                log.debug("调用verjsf进行老发货校验拦截,返回值:{}" , JSON.toJSONString(result));
            }
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());
            return response;
        }catch (Exception e){
            log.error("调用ver接口进行老发货验证异常:{}" , JSON.toJSONString(deliveryRequest),e);
            response.setCode(JdResponse.CODE_NOT_FOUND);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }

        return response;
    }
    /**
     * 2个接口合并，/delivery/packageSend/check 和 /delivery/check
     * 老发货扫描箱号或包裹号校验
     * @param deliveryRequest
     * @return
     */
    @POST
    @Path("/delivery/packageSend/checkBeforeSend")
    public JdResult<CheckBeforeSendResponse> checkBeforeSend(DeliveryRequest deliveryRequest) {
    	JdResult<CheckBeforeSendResponse> result = new JdResult<CheckBeforeSendResponse>();
    	CheckBeforeSendResponse checkResponse = new CheckBeforeSendResponse();
    	result.setData(checkResponse);
    	checkResponse.setTipMessages(new ArrayList<String>());
    	result.toSuccess();
        try {
        	//不是快运发货，调用箱号验证
        	if(!KY_DELIVERY.equals(deliveryRequest.getOpType())){
        		DeliveryResponse boxCheckResponse = this.doCheckDeliveryInfo(deliveryRequest.getBoxCode(), 
        				deliveryRequest.getSiteCode(), 
        				deliveryRequest.getReceiveSiteCode(), 
        				deliveryRequest.getBusinessType());
        		if(boxCheckResponse == null){
        			result.toFail("箱号验证失败！");
        			return result;
        		}
        		if(!JdResponse.CODE_OK.equals(boxCheckResponse.getCode())){
        			if(boxCheckResponse.getCode() != null 
        					&& boxCheckResponse.getCode()>=30000
        					&& boxCheckResponse.getCode()<=40000){
        				result.toWarn(boxCheckResponse.getCode(), boxCheckResponse.getMessage());
        				checkResponse.getTipMessages().add(boxCheckResponse.getMessage());
        			}else{
            			result.toFail(boxCheckResponse.getCode(), boxCheckResponse.getMessage());
            			return result;
        			}
        		}
        		checkResponse.setWaybillType(boxCheckResponse.getWaybillType());
        	}
        	//初始化批次已发货的数量
        	if(BusinessHelper.isSendCode(deliveryRequest.getSendCode())){
        		deliveryRequest.setHasSendPackageNum(sendDetailService.querySendDCountBySendCode(deliveryRequest.getSendCode()));
        	}
        	//调用ver校验链
        	JdResult<CheckBeforeSendResponse> verCheckResult = jsfSortingResourceService.checkBeforeSend(deliveryRequest);
            if(!verCheckResult.isSucceed()){
            	return verCheckResult;
            }else{
            	//前面校验
            	if(!result.isWarn()){
            		result.setCode(verCheckResult.getCode());
            		result.setMessageCode(verCheckResult.getMessageCode());
            		result.setMessage(verCheckResult.getMessage());
            	}
            	if(verCheckResult.isWarn() && verCheckResult.getData().getTipMessages() != null){
            		checkResponse.getTipMessages().addAll(verCheckResult.getData().getTipMessages());
            	}
            	checkResponse.setPackageNum(verCheckResult.getData().getPackageNum());
            }
            if(log.isDebugEnabled()){
                log.debug("调用verjsf进行老发货校验拦截,返回值:{}" , JSON.toJSONString(result));
            }
            return result;
        }catch (Exception e){
            log.error("调用ver接口进行老发货验证异常:{}" ,JSON.toJSONString(deliveryRequest),e);
            result.toError("调用ver接口进行老发货验证异常.");
        }
        return result;
    }
}
