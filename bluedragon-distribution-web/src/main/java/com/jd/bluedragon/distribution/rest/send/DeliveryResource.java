package com.jd.bluedragon.distribution.rest.send;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ServiceMessage;
import com.jd.bluedragon.common.domain.ServiceResultEnum;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameBatchSendService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.service.BoxRelationService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.gantry.domain.SendGantryDeviceConfig;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.jsf.domain.WhemsWaybillResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.*;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.distribution.send.service.SendQueryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.transboard.api.service.IVirtualBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.*;

import static com.jd.bluedragon.Constants.KY_DELIVERY;

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DeliveryResource {

    @Value("${send.checkSendCodeDate:false}")
    private boolean checkSendCodeDate;

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
    @Qualifier("sendVehicleTransactionManager")
    private SendVehicleTransactionManager sendVehicleTransactionManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private BoxRelationService boxRelationService;

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private LogEngine logEngine;

    @Autowired
    private SendCodeService sendCodeService;

    /**
     * 原包发货【一车一件项目，发货专用】
     *
     * @param request 发货对象
     * @return
     */
    @Deprecated
    @POST
    @Path("/delivery/packagesend")
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.packageSend", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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

    // 新发货
    @POST
    @Path("/delivery/newpackagesend")
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.newPackageSend", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @BusinessLog(sourceSys = 1, bizType = 100, operateType = 1001)
    public InvokeResult<SendResult> newPackageSend(PackageSendRequest request) {
        if (log.isInfoEnabled()) {
            log.info(JsonHelper.toJson(request));
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.DeliveryServiceImpl.newPackageSend", Constants.UMP_APP_NAME_DMSWEB,false, true);
        SendM domain = this.toSendMDomain(request);
        InvokeResult<SendResult> result = new InvokeResult<SendResult>();
        try {

            // 校验批次号
            InvokeResult<Boolean> chkResult = sendCodeService.validateSendCodeEffective(request.getSendCode());
            if (!chkResult.codeSuccess()) {
                result.customMessage(chkResult.getCode(), chkResult.getMessage());
                return result;
            }

            if (SendBizSourceEnum.WAYBILL_SEND.getCode().equals(request.getBizSource())) {
                // 按运单发货
                domain.setBoxCode(request.getBoxCode());
                result.setData(deliveryService.packageSendByWaybill(domain, request.getIsForceSend(), request.getIsCancelLastSend()));
            }else if (BusinessUtil.isBoardCode(request.getBoxCode())) {
                // 一车一单下的组板发货
                domain.setBoardCode(request.getBoxCode());
                //组板发货时间以PDA上传时间为准
                Date opeTime = DateHelper.parseDateTime(request.getOperateTime());
                if(opeTime != null) {
                    domain.setOperateTime(opeTime);
                }
                if(log.isInfoEnabled()){
                    log.info("组板发货newpackagesend：req:{} domain: {}" , JsonHelper.toJson(request),JsonHelper.toJson(domain));
                }
                result.setData(deliveryService.boardSend(domain, request.getIsForceSend()));
            } else {
                SendBizSourceEnum bizSource = SendBizSourceEnum.getEnum(request.getBizSource());
                // 一车一单发货
                domain.setBoxCode(request.getBoxCode());
                // 如果是按包裹或者箱号找到整板进行发货
                if((BusinessUtil.isBoxcode(request.getBoxCode()) || WaybillUtil.isPackageCode(request.getBoxCode())) && Objects.equals(Constants.YN_YES, request.getSendForWholeBoard())){
                    return this.handleSendByPackageOrBoxCodeForWholeBoard(request, domain);
                } else {
                    if (request.getIsCancelLastSend() == null) {
                        result.setData(deliveryService.packageSend(bizSource, domain, request.getIsForceSend()));
                    } else {
                        result.setData(deliveryService.packageSend(bizSource, domain, request.getIsForceSend(), request.getIsCancelLastSend()));
                    }
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

    @Autowired
    private IVirtualBoardService virtualBoardService;

    /**
     * 按包裹号、箱号找到整板进行整板发货
     * @author fanggang7
     * @time 2021-08-24 18:31:56 周二
     */
    private InvokeResult<SendResult> handleSendByPackageOrBoxCodeForWholeBoard(PackageSendRequest request, SendM domain) {
        InvokeResult<SendResult> result = new InvokeResult<>();
        // 根据箱号找到板号
        final Response<Board> boardResult = virtualBoardService.getBoardByBarCode(request.getBoxCode(), request.getSiteCode());
        if(!Objects.equals(boardResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
            log.error("handleSendByPackageOrBoxCodeForWholeBoard fail {}", JsonHelper.toJson(boardResult));
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, "根据包裹或箱号查找板号数据异常");
            return result;
        }
        final Board board = boardResult.getData();
        if(board == null){
            SendResult sendResult = new SendResult(SendResult.CODE_SENDED, "根据包裹或箱号未找到对应板数据");
            result.setData(sendResult);
            return result;
        }
        domain.setBoardCode(board.getCode());
        final SendResult sendResult = deliveryService.boardSend(domain, request.getIsForceSend());
        result.setData(sendResult);
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

        domain.setBizSource(request.getBizSource());

        domain.setYn(1);
        domain.setCreateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        domain.setOperateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        domain.setOperatorTypeCode(request.getOperatorTypeCode());
        domain.setOperatorId(request.getOperatorId());
        return domain;
    }

    @GET
    @Path("/delivery/checksendcodestatus/{sendCode}")
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.checkSendCodeStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<AbstractMap.Entry<Integer, String>> checkSendCodeStatus(@PathParam("sendCode") String sendCode) {
        InvokeResult<AbstractMap.Entry<Integer, String>> result = new InvokeResult<AbstractMap.Entry<Integer, String>>();
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode);
        if(receiveSiteCode == null){//批次号是否符合编码规范，不合规范直接返回参数错误
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(HintService.getHint(HintCodeConstants.SEND_CODE_ILLEGAL));
        }else{
            if (forbid(result, receiveSiteCode)) return result;
            try {
                ServiceMessage<Boolean> data = departureService.checkSendStatusFromVOS(sendCode);
                if (ServiceResultEnum.WRONG_STATUS.equals(data.getResult())) {//已被封车
                    result.setData(new AbstractMap.SimpleEntry<Integer, String>(2, HintService.getHint(HintCodeConstants.SEND_CODE_SEALED)));
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
     * 校验批次是否已封车
     *  公用校验方法，目前用于以下场景
     * <p>
     *     1、批量一车一单
     * </p>
     * @param sendCode 批次号
     * @return Code非200PDA给出提示信息
     */
    @GET
    @Path("/delivery/commonCheckSendCode/{sendCode}")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.commonCheckSendCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> commonCheckSendCode(@PathParam("sendCode") String sendCode) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(!BusinessHelper.isSendCode(sendCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"批次号不符合规则!");
            return result;
        }
        // 校验批次号
        result = sendCodeService.validateSendCodeEffective(sendCode);
        if (!result.codeSuccess()) {
            return result;
        }
        if(deliveryService.checkSendCodeIsOld(sendCode) && checkSendCodeDate){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"批次号创建时间过早，请更换批次!");
            return result;
        }
        if(deliveryService.checkSendCodeIsSealed(sendCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"批次号已封车，请更换批次!");
        }
        //干支批次拦截禁止使用
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(BusinessUtil.getCreateSiteCodeFromSendCode(sendCode));
        InvokeResult<Boolean> interceptResult = sendVehicleTransactionManager.needInterceptOfGZ(sendCode,Constants.MENU_CODE_BATCH_SEND_CODE,currentOperate,null);
        if(interceptResult.codeSuccess() && interceptResult.getData()){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,interceptResult.getMessage());
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
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.cancelDeliveryInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
                    HintService.getHint(HintCodeConstants.PRE_LOAD_CANNOT_CANCEL), null);
        }
        /*****/

        // 如果是按包裹找整板进行发货
        final InvokeResult<Void> handleCancelSendByPackageOrBoxCodeForWholeBoardResult = this.handleCancelSendByPackageOrBoxCodeForWholeBoard(request);
        if(!Objects.equals(handleCancelSendByPackageOrBoxCodeForWholeBoardResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
            return new ThreeDeliveryResponse(handleCancelSendByPackageOrBoxCodeForWholeBoardResult.getCode(), handleCancelSendByPackageOrBoxCodeForWholeBoardResult.getMessage(), null);
        }

        /**
         * 取消发货校验封车业务
         */
        DeliveryResponse checkResponse = deliveryService.dellCancelDeliveryCheckSealCar(toSendM(request));
        if (checkResponse!=null && !JdResponse.CODE_OK.equals(checkResponse.getCode())) {
            return new ThreeDeliveryResponse(checkResponse.getCode(),checkResponse.getMessage(), null);
        }

        ThreeDeliveryResponse tDeliveryResponse = null;
        try {
            SendM sendMDomain = toSendM(request);
            tDeliveryResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendMDomain, true);

            // BC箱号取消成功后，同步取消WJ箱号的发货
            if (ObjectUtils.equals(JdResponse.CODE_OK, tDeliveryResponse.getCode())) {
                List<SendM> relationSendList = new DeliveryCancelSendMGen().createBoxRelationSendM(Collections.singletonList(request));
                for (SendM sendM : relationSendList) {
                    long startTime = System.currentTimeMillis();
                    tDeliveryResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
                    long endTime = System.currentTimeMillis();
                    this.addFileSendingBizLog(sendMDomain, sendM, JsonHelper.toJson(tDeliveryResponse), startTime, endTime);
                }
            }

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

    /**
     * 按包裹号、箱号找到整板进行整板发货
     * @author fanggang7
     * @time 2021-08-24 18:31:56 周二
     */
    private InvokeResult<Void> handleCancelSendByPackageOrBoxCodeForWholeBoard(DeliveryRequest request) {
        InvokeResult<Void> result = new InvokeResult<>();
        final boolean isCancelPackageForWholeBoard = (WaybillUtil.isPackageCode(request.getBoxCode()) || BusinessUtil.isBoxcode(request.getBoxCode())) && Objects.equals(request.getCancelWholeBoard(), Constants.YN_YES);
        if(!isCancelPackageForWholeBoard){
            return result;
        }
        // 根据箱号找到板号
        final Response<Board> boardResult = virtualBoardService.getBoardByBarCode(request.getBoxCode(), request.getSiteCode());
        if(!Objects.equals(boardResult.getCode(), ResponseEnum.SUCCESS.getIndex())){
            log.error("handleSendByPackageOrBoxCodeForWholeBoard fail {}", JsonHelper.toJson(boardResult));
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage("根据包裹或箱号查找板号数据异常");
            return result;
        }
        final Board board = boardResult.getData();
        if(board == null){
            result.setCode(InvokeResult.RESULT_NULL_CODE);
            result.setMessage("根据包裹或箱号未找到对应板数据");
            return result;
        }
        request.setBoxCode(board.getCode());
        return result;
    }

    /**
     * 文件发货添加Business Log
     * @param BCSendM
     * @param fileSendM
     * @param result
     * @param startTime
     * @param endTime
     */
    private void addFileSendingBizLog(SendM BCSendM, SendM fileSendM, String result, Long startTime, Long endTime) {
        JSONObject operateRequest = new JSONObject();
        operateRequest.put("bcSendM", JsonHelper.toJson(BCSendM));
        operateRequest.put("wjSendM", JsonHelper.toJson(fileSendM));

        BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEND_FILE_BOX_CANCEL)
                .operateRequest(operateRequest)
                .operateResponse(result)
                .processTime(endTime, startTime)
                .methodName("DeliveryResource#cancelDeliveryInfo")
                .build();

        logEngine.addLog(businessLogProfiler);
    }

    @POST
    @Path("/delivery/cancel/last")
    @BusinessLog(sourceSys = 1,bizType = 100,operateType = 1004)
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.cancelLastDeliveryInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ThreeDeliveryResponse cancelLastDeliveryInfo(DeliveryRequest request) {
        if(log.isDebugEnabled()){
            log.debug("取消最近的一次发货JSON. {}" , JsonHelper.toJson(request));
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
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.recyclableBoxSend", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
                HintService.getHint(HintCodeConstants.PRE_LOAD_CANNOT_CANCEL));
    }

    /**
     * 老发货，批量发货，快运发货，逆向发货、三方发货入口
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
        try{
            if (check(request)) {
                return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR,
                        JdResponse.MESSAGE_PARAM_ERROR);
            }

            // 校验批次号
            DeliveryResponse chkResponse = validateSendCode(request);
            if (!JdResponse.CODE_OK.equals(chkResponse.getCode())) {
                return chkResponse;
            }

            DeliveryResponse tDeliveryResponse = null;

            DeliveryRequest deliveryRequest = request.get(0);
            Integer opType = deliveryRequest.getOpType();
            if (KY_DELIVERY.equals(opType)) {
                tDeliveryResponse = this.sendDeliveryInfoForKY(request);
            } else {

                Integer businessType = deliveryRequest.getBusinessType();
                //获取批量参数中的批次号
                String sendCode = deliveryRequest.getSendCode();
                String redisKey = Constants.BUSINESS_TYPE_PREFIX + Constants.SEPARATOR_HYPHEN + businessType + Constants.SEPARATOR_HYPHEN + sendCode;

                try {
                    //查询redis中是否存在key
                    if (jimdbCacheService.exists(redisKey)) {
                        log.warn("发货任务已提交，批次号：{}", sendCode);
                        tDeliveryResponse = new DeliveryResponse(DeliveryResponse.CODE_DELIVERY_SEND_CODE_IS_COMMITTED, HintService.getHint(HintCodeConstants.BATCH_SEND_PROCESSING));
                    } else {
                        jimdbCacheService.setEx(redisKey, sendCode, 5 * DateHelper.ONE_MINUTES_MILLI);
                        if (businessType != null && Constants.BUSSINESS_TYPE_REVERSE == businessType) {
                            // 逆向发货
                            tDeliveryResponse = deliveryService.dellDeliveryMessage(SendBizSourceEnum.REVERSE_SEND, toSendDetailList(request));
                        } else {
                            // 正向老发货
                            tDeliveryResponse = deliveryService.dellDeliveryMessage(SendBizSourceEnum.OLD_PACKAGE_SEND, toSendDetailList(request));

                            if (ObjectUtils.equals(JdResponse.CODE_OK, tDeliveryResponse.getCode())) {
                                List<SendM> relationSendList = new DeliverySendMGen().createBoxRelationSendM(request);
                                if (CollectionUtils.isNotEmpty(relationSendList)) {
                                    tDeliveryResponse = deliveryService.dealFileBoxBatchSending(SendBizSourceEnum.OLD_PACKAGE_SEND, toSendDetailList(request), relationSendList);
                                }
                            }
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
        }catch (Exception e){
            log.error("老发货执行异常，请求：{}", JsonHelper.toJson(request), e);
            return new DeliveryResponse(JdResponse.CODE_NOT_FOUND,
                    JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    private DeliveryResponse sendDeliveryInfoForKY(List<DeliveryRequest> request){
        return deliveryService.sendDeliveryInfoForKY(request,SendBizSourceEnum.RAPID_TRANSPORT_SEND);
    }

    /**
     * 验证批次号是否合法
     * @param request
     * @return
     */
    private DeliveryResponse validateSendCode(List<DeliveryRequest> request) {
        if (CollectionUtils.isNotEmpty(request)) {
            for (DeliveryRequest deliveryRequest : request) {
                InvokeResult<Boolean> sendChkResult = sendCodeService.validateSendCodeEffective(deliveryRequest.getSendCode());
                if (!sendChkResult.codeSuccess()) {
                    return new DeliveryResponse(sendChkResult.getCode(), sendChkResult.getMessage());
                }
            }
        }

        return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
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
                response =  deliveryService.checkThreePackageForKY(toSendDetailListInFirstIndex(request));
            }else{
                response =  deliveryService.checkThreePackage(toSendDetailListInFirstIndex(request));
            }
            this.log.debug("结束三方发货不全验证");
            return response;
        } catch (Exception ex) {
            log.error("发货不全验证异常：{}",JsonHelper.toJson(request), ex);
            return new ThreeDeliveryResponse(JdResponse.CODE_INTERNAL_ERROR, ex.getMessage(), null);
        }
    }

    @Deprecated
    @POST
    @Path("/delivery/router/verification")
    @JProfiler(jKey = "DMSWEB.DeliveryResource.router.verification", mState = {JProEnum.TP})
    public DeliveryResponse checkThreeDeliveryOld(DeliveryRequest request) {
        return checkThreeDelivery(request,Constants.DELIVERY_ROUTER_VERIFICATION_OLD);
    }

    /**
     * 快运发货、冷链发货调用
     * @param request
     * @return
     */
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
        return deliveryService.checkThreeDelivery(request,flag);
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
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.checkSortingDiff", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.appendPackageNum", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
     * <ul>
     *     <li>批量发货</li>
     *     <li>三方发货</li>
     *     <li>离线发货</li>
     * </ul>
     * @param boxCode
     * @param siteCode
     * @param receiveSiteCode
     * @param businessType
     * @return
     */
    @GET
    @Path("/delivery/check")
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.checkDeliveryInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public DeliveryResponse checkDeliveryInfo(@QueryParam("boxCode") String boxCode,
                                              @QueryParam("siteCode") String siteCode,
                                              @QueryParam("receiveSiteCode") String receiveSiteCode,
                                              @QueryParam("businessType") String businessType) {
        if (boxCode == null || siteCode == null || businessType == null || receiveSiteCode == null) {
            return new DeliveryResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
        }
        return deliveryService.doCheckDeliveryInfo(boxCode,Integer.parseInt(siteCode),Integer.parseInt(receiveSiteCode),Integer.parseInt(businessType), null);
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
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.toEmsServer", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.whBcrsQuery", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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

    public abstract class AbstractSendMGen {

        protected abstract SendM makeSendMFromRequest(BoxRelation relation, DeliveryRequest request);

        List<SendM> createBoxRelationSendM(List<DeliveryRequest> requests) {
            List<SendM> sendMList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(requests)) {

                for (DeliveryRequest request : requests) {
                    if (!BusinessUtil.isBoxcode(request.getBoxCode()) || null == request.getSiteCode()) {
                        continue;
                    }
                    InvokeResult<List<BoxRelation>> sr = boxRelationService.getRelationsByBoxCode(request.getBoxCode());
                    if (!sr.codeSuccess() || CollectionUtils.isEmpty(sr.getData())) {
                        continue;
                    }

                    for (BoxRelation relation : sr.getData()) {
                        if (StringUtils.isBlank(relation.getRelationBoxCode())) {
                            continue;
                        }
                        sendMList.add(makeSendMFromRequest(relation, request));
                    }
                }
            }

            return sendMList;
        }
    }

    protected class DeliverySendMGen extends AbstractSendMGen {

        @Override
        protected SendM makeSendMFromRequest(BoxRelation relation, DeliveryRequest request) {
            SendM sendM = deliveryRequest2SendM(request);
            sendM.setBoxCode(relation.getRelationBoxCode());
            return sendM;
        }
    }

    protected class DeliveryCancelSendMGen extends AbstractSendMGen {

        @Override
        protected SendM makeSendMFromRequest(BoxRelation relation, DeliveryRequest request) {
            SendM sendM = toSendM(request);
            sendM.setBoxCode(relation.getRelationBoxCode());
            return sendM;
        }
    }


    /**
     * toSendDetailList(java.util.List) 的优化方法，主要优化isValidWaybillCode()的循环调用问题
     * @see DeliveryResource#toSendDetailList(java.util.List)
     * @param request 发货列表
     * @return
     */
    private List<SendM> toSendDetailListInFirstIndex(List<DeliveryRequest> request) {
        List<SendM> sendMList = new ArrayList<SendM>();
        boolean ifBColdChain = CollectionUtils.isNotEmpty(request) && request.size() > 0
                 && JdResponse.CODE_OK.equals(isValidWaybillCode(request.get(0)).getCode());//是否B冷链快运发货
        if (request != null && !request.isEmpty()) {
            for (DeliveryRequest deliveryRequest : request) {
                if (WaybillUtil.isPackageCode(deliveryRequest.getBoxCode()) || BusinessHelper.isBoxcode(deliveryRequest.getBoxCode())) {
                    sendMList.add(deliveryRequest2SendM(deliveryRequest));
                } else if (WaybillUtil.isWaybillCode(deliveryRequest.getBoxCode())) {
                    //B冷链快运发货支持扫运单号发货
                    if (!ifBColdChain) {
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
        if(!uccPropertyConfiguration.isControlCheckPackage()){
            return true;
        }
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
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.findWaybillStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.updateWaybillStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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

    @Deprecated
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
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.reSendBySendM", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.pushStatusTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> pushStatusTask(SendM sendM) {
    	InvokeResult<Boolean> res = new InvokeResult<Boolean>();
    	deliveryService.pushStatusTask(sendM);
    	return res;
    }
    @POST
    @Path("/delivery/querySendMListByCondition")
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.querySendMListByCondition", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<SendM>> querySendMListByCondition(SendM condition) {
    	InvokeResult<List<SendM>> res = new InvokeResult<List<SendM>>();
    	res.setData(sendMDao.queryListByCondition(condition));
    	return res;
    }
    @POST
    @Path("/delivery/querySendDListByCondition")
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.querySendDListByCondition", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<SendDetail>> querySendDListByCondition(SendDetail condition) {
    	InvokeResult<List<SendDetail>> res = new InvokeResult<List<SendDetail>>();
    	res.setData(sendDatailDao.queryListByCondition(condition));
    	return res;
    }

    /**
     * 原包分拣发货，云分拣调用
     * @author jinjingcheng
     * @param request
     * @return 发货结果 200成功
     */
    @POST
    @Path("/delivery/packageSortSend")
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.packageSortSend", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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

        // 云分拣调用。校验批次号
        InvokeResult<Boolean> invokeResult = sendCodeService.validateSendCodeEffective(request.getSendCode());
        if (!invokeResult.codeSuccess()) {
            result.parameterError(invokeResult.getMessage());
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
     * 老发货校验，PDA无调用
     * @param deliveryRequest
     * @return
     */
    @Deprecated
    @POST
    @Path("/delivery/packageSend/check")
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.packageSendCheck", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
     * 老发货校验接口
     * 2个接口合并，/delivery/packageSend/check 和 /delivery/check
     * 老发货扫描箱号或包裹号校验
     *
     * <ul>
     *     <li>老发货</li>
     *     <li>快运发货</li>
     * </ul>
     * @param deliveryRequest
     * @return
     */
    @POST
    @Path("/delivery/packageSend/checkBeforeSend")
    @JProfiler(jKey = "DMS.WEB.DeliveryResource.checkBeforeSend", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<CheckBeforeSendResponse> checkBeforeSend(DeliveryRequest deliveryRequest) {

        return deliveryService.checkBeforeSend(deliveryRequest);
    }




}
