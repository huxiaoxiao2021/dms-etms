package com.jd.bluedragon.distribution.rest.seal;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.blockcar.enumeration.FerrySealCarSceneEnum;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarSourceEnum;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarTypeEnum;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarPreRequest;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.MenuUsageProcessDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.ReportExternalManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.base.JdiSelectWSManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.TransAbnormalTypeDto;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.api.response.spot.SpotCheckResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jy.enums.SpotCheckTypeEnum;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.seal.domain.CreateTransAbnormalAndUnsealJmqMsg;
import com.jd.bluedragon.distribution.seal.service.CarLicenseChangeUtil;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.transport.service.TransportRelatedService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.SiteSignTool;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ql.dms.report.domain.WaitSpotCheckQueryCondition;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.jdi.dto.TransWorkItemDto;
import com.jd.tms.jdi.dto.TransWorkItemWsDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.*;

import static com.jd.bluedragon.distribution.seal.domain.CreateTransAbnormalAndUnsealJmqMsg.TYPE_CREATE_TRANS_ABNORMAL_AND_UNSEAL;

/**
 * create by zhanglei 2017-05-10
 * <p>
 * 新版封车解封车
 * <p>
 * 主要功能点
 * 1、封车：回传TMS发车信息（通过jsf接口）
 * 2、解封车：回传TMS解封车信息（通过jsf接口）
 * 3、获取待解封列表(通过jsf接口)
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class NewSealVehicleResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     *  运力网点逆向退货组
     *      类型2、子类型215
     * */
    private static final Integer RETURNGROUP_NODE_TYPE = 2;
    private static final Integer RETURNGROUP_NODE_SUBTYPE = 215;
    /** 飞机场网点类型 */
    private static final Integer AIRPORT_NODE_TYPE = 7;
    /** 火车站网点类型 */
    private static final Integer TRAINSTATION_NODE_TYPE = 9;
    /** 仓库网点类型 */
    private static final Integer WMS_NODE_TYPE = 3;

    /** 封车体积确认CODE */
    private static final Integer SEAL_VOLUME_CONFIRM = 100;

    private static final int RANGE_HOUR = 2; //运力编码在两小时范围内

    /**
     * ScheduleType=1 是卡班调度模式
     */
    private static final Integer SCHEDULE_TYPE_KA_BAN = 1;

    /**
     * 查询几天内的带解任务（负数）
     * */
    @Value("${newSealVehicleResource.rollBackDay:-7}")
    private int rollBackDay;

    @Autowired
    private NewSealVehicleService newsealVehicleService;

    @Autowired
    private CarLicenseChangeUtil carLicenseChangeUtil;

    @Autowired
    private ColdChainSendService coldChainSendService;

    @Autowired
    @Qualifier("basicPrimaryWS")
    private BasicPrimaryWS basicPrimaryWS;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private JdiQueryWSManager jdiQueryWSManager;

    @Autowired
    private JdiSelectWSManager jdiSelectWSManager;

    @Autowired
    private ReportExternalManager reportExternalManager;

    @Autowired
    private BaseService baseService;

    @Autowired
    private TransportRelatedService transportRelatedService;

    @Autowired
    @Qualifier("sendVehicleTransactionManager")
    private SendVehicleTransactionManager sendVehicleTransactionManager;


    @Autowired
    @Qualifier("createTransAbnormalAndUnsealProducer")
    private DefaultJMQProducer createTransAbnormalAndUnsealProducer;

    /**
     * 校验并获取运力编码信息
     *
     * @param request
     * @return
     */
    @POST
    @Path("/new/vehicle/seal/transportCode")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101101)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.getTransportCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public RouteTypeResponse getTransportCode(NewSealVehicleRequest request) {
        RouteTypeResponse response = new RouteTypeResponse();
        if (StringUtils.isEmpty(request.getTransportCode()) || !NumberHelper.isPositiveNumber(request.getSiteCode())) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        try {
            com.jd.tms.basic.dto.CommonDto<TransportResourceDto> vtsDto = newsealVehicleService.getTransportResourceByTransCode(request.getTransportCode());
            if (vtsDto == null) {    //JSF接口返回空
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("查询运力信息结果为空:" + request.getTransportCode());
                return response;
            }
            if (Constants.RESULT_SUCCESS == vtsDto.getCode()) { //JSF接口调用成功
                TransportResourceDto vtrd = vtsDto.getData();
                if (vtrd != null) {
                    response = checkTransportCode(vtrd, request.getSiteCode());
                } else {
                    response.setCode(JdResponse.CODE_SERVICE_ERROR);
                    response.setMessage("查询运力信息结果为空:" + request.getTransportCode());
                }
            } else if (Constants.RESULT_WARN == vtsDto.getCode()) {    //查询运力信息接口返回警告，给出前台提示
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage(vtsDto.getMessage());
            } else { //服务出错或者出异常，打日志
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("查询运力信息出错！");
                log.warn("查询运力信息出错,出错原因:{}", vtsDto.getMessage());
                log.warn("查询运力信息出错,运力编码:{}",request.getTransportCode());
            }
            return response;
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            log.error("通过运力编码获取基础资料信息异常：{}", JsonHelper.toJson(request), e);
        }

        return response;
    }

    /**
     * 校验并返回运力信息
     *
     * @param data
     * @param createSiteCode
     * @return
     */
    private RouteTypeResponse checkTransportCode(TransportResourceDto data, Integer createSiteCode) {
        RouteTypeResponse response = new RouteTypeResponse();

        //设置运力基本信息
        response.setSiteCode(data.getEndNodeId());
        response.setSendUserType(data.getTransType());
        response.setRouteType(data.getTransType());
        response.setDriver(data.getCarrierName());
        response.setTransWay(data.getTransWay());
        response.setTransWayName(data.getTransWayName());
        response.setCarrierType(data.getCarrierType());

        //仅限于传摆封车
        if(data.getStartNodeId() != null
                && data.getStartNodeId().equals(data.getEndNodeId())){
            response.setFerrySealCarSceneCode(FerrySealCarSceneEnum.PARK_SEAL_CAR.getCode());
            response.setFerrySealCarSceneName(FerrySealCarSceneEnum.PARK_SEAL_CAR.getName());
        }
        if(AIRPORT_NODE_TYPE.equals(data.getStartNodeType())
                || TRAINSTATION_NODE_TYPE.equals(data.getStartNodeType())
                || AIRPORT_NODE_TYPE.equals(data.getEndNodeType())
                || TRAINSTATION_NODE_TYPE.equals(data.getEndNodeType())){
            response.setFerrySealCarSceneCode(FerrySealCarSceneEnum.AIRLINE_SEAL_CAR.getCode());
            response.setFerrySealCarSceneName(FerrySealCarSceneEnum.AIRLINE_SEAL_CAR.getName());
        }
        if(WMS_NODE_TYPE.equals(data.getEndNodeType())
                && RETURNGROUP_NODE_TYPE.equals(data.getStartNodeType())
                && RETURNGROUP_NODE_SUBTYPE.equals(data.getStartNodeSubType())){
            response.setFerrySealCarSceneCode(FerrySealCarSceneEnum.WMS_SEAL_CAR.getCode());
            response.setFerrySealCarSceneName(FerrySealCarSceneEnum.WMS_SEAL_CAR.getName());
        }

        //运力校验
        if (createSiteCode.equals(data.getStartNodeId())) {
            int hour = data.getSendCarHour();
            int min = data.getSendCarMin();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            if (DateHelper.currentTimeIsRangeHours(calendar.getTime(), RANGE_HOUR)) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage(JdResponse.MESSAGE_OK);
            } else {
                String hourStr = hour < 10 ? "0" + String.valueOf(hour) : String.valueOf(hour);
                String minStr = min < 10 ? "0" + String.valueOf(min) : String.valueOf(min);
                response.setCode(NewSealVehicleResponse.CODE_TRANSPORT_RANGE_CHECK);
                response.setMessage(MessageFormat.format(NewSealVehicleResponse.MESSAGE_TRANSPORT_RANGE_OUT_CHECK, hourStr, minStr));
            }
        } else {
            response.setCode(NewSealVehicleResponse.CODE_TRANSPORT_RANGE_ERROR);
            response.setMessage(NewSealVehicleResponse.MESSAGE_TRANSPORT_RANGE_ERROR);
        }

        return response;
    }

    /**
     * 根据任务简码获取任务信息
     */
    @GET
    @Path("/new/vehicle/seal/workitem/{simpleCode}")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101102)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.getVehicleNumBySimpleCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public TransWorkItemResponse getVehicleNumBySimpleCode(@PathParam("simpleCode") String simpleCode) {
        TransWorkItemResponse sealVehicleResponse = new TransWorkItemResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            com.jd.tms.jdi.dto.CommonDto<TransWorkItemDto> returnCommonDto = jdiQueryWSManager.queryTransWorkItemBySimpleCode(simpleCode);
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage("根据任务简码获取任务信息!");
                    sealVehicleResponse.setVehicleNumber(returnCommonDto.getData() == null ? null : returnCommonDto.getData().getVehicleNumber());
                    sealVehicleResponse.setTransType(returnCommonDto.getData() == null ? null : returnCommonDto.getData().getTransWay());
                } else {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                }
            }
        } catch (Exception e) {
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            this.log.error("根据任务简码获取任务信息：任务简码->{}", simpleCode, e);
        }
        return sealVehicleResponse;
    }

    /**
     * 根据车牌号获取派车明细编码或根据派车明细编码获取车牌号
     */
    @POST
    @Path("/new/vehicle/seal/workitem/query")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101104)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.getVehicleNumberOrItemCodeByParam", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public TransWorkItemResponse getVehicleNumberOrItemCodeByParam(NewSealVehicleRequest request) {
        TransWorkItemResponse sealVehicleResponse = new TransWorkItemResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null || StringUtils.isEmpty(request.getUserErp()) || StringUtils.isEmpty(request.getDmsCode()) ||
                    (StringUtils.isEmpty(request.getTransWorkItemCode()) && StringUtils.isEmpty(request.getVehicleNumber()))) {
                this.log.warn("NewSealVehicleResource workitem query--> 传入参数非法:{}", JsonHelper.toJson(request));
                sealVehicleResponse.setCode(TransWorkItemResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(TransWorkItemResponse.MESSAGE_PARAM_ERROR);
                return sealVehicleResponse;
            }
            TransWorkItemWsDto transWorkItemWsDto = new TransWorkItemWsDto();
            transWorkItemWsDto.setTransWorkItemCode(request.getTransWorkItemCode());//任务简码
            transWorkItemWsDto.setVehicleNumber(request.getVehicleNumber());
            transWorkItemWsDto.setOperateUserCode(request.getUserErp());
            transWorkItemWsDto.setOperateNodeCode(request.getDmsCode());
            com.jd.tms.jdi.dto.CommonDto<com.jd.tms.jdi.dto.TransWorkItemWsDto> returnCommonDto
                    = jdiSelectWSManager.getVehicleNumberOrItemCodeByParam(transWorkItemWsDto);
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode() && returnCommonDto.getData() != null) {
                    sealVehicleResponse = getVehicleNumBySimpleCode(returnCommonDto.getData().getTransWorkItemCode());
                    this.buildTransWorkItemBySimpleCode(sealVehicleResponse, request.getTransWorkItemCode());
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_OK);
                    sealVehicleResponse.setTransWorkItemCode(returnCommonDto.getData().getTransWorkItemCode());
                    sealVehicleResponse.setVehicleNumber(returnCommonDto.getData().getVehicleNumber());
                    // 校验运输任务（返回结果只做提示展示）
                    String transWorkItemCode = StringUtils.isEmpty(request.getTransWorkItemCode()) ? returnCommonDto.getData().getTransWorkItemCode() : request.getTransWorkItemCode();
                    ImmutablePair<Integer, String> checkResult = transportRelatedService.checkTransportTask(request.getDmsSiteId(), null, null, transWorkItemCode, null);
                    sealVehicleResponse.setExtraBusinessCode(checkResult.left);
                    sealVehicleResponse.setExtraBusinessMessage(checkResult.right);
                } else {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
        } catch (Exception e) {
            this.log.error("根据车牌号获取派车明细编码或根据派车明细编码获取车牌号-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 根据任务简码和运力资源编码校验运力资源编码并对运力资源编码进行更新
     */
    @POST
    @Path("/new/vehicle/seal/workitem/check")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101104)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.checkTransportCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public TransWorkItemResponse checkTransportCode(NewSealVehicleRequest request) {
        TransWorkItemResponse sealVehicleResponse = new TransWorkItemResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null || StringUtils.isEmpty(request.getTransWorkItemCode()) || StringUtils.isEmpty(request.getTransportCode())) {
                this.log.warn("NewSealVehicleResource workitem check --> 传入参数非法:{}", JsonHelper.toJson(request));
                sealVehicleResponse.setCode(TransWorkItemResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(TransWorkItemResponse.MESSAGE_PARAM_ERROR);
                return sealVehicleResponse;
            }

            com.jd.tms.jdi.dto.CommonDto<String> returnCommonDto = jdiSelectWSManager.checkTransportCode(request.getTransWorkItemCode(), request.getTransportCode());
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_OK);
                    sealVehicleResponse.setData(returnCommonDto.getData());
                } else {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
        } catch (Exception e) {
            this.log.error("根据任务简码和运力资源编码校验运力资源编码并对运力资源编码进行更新-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 检查运力编码和批次号目的地是否一致
     */
    @Deprecated
    @GET
    @Path("/new/vehicle/seal/check/{transportCode}/{batchCode}")
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.checkTranCodeAndBatchCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse checkTranCodeAndBatchCode(
            @PathParam("transportCode") String transportCode, @PathParam("batchCode") String batchCode) {
        return newCheckTranCodeAndBatchCode(transportCode, batchCode, Constants.SEAL_TYPE_TRANSPORT);
    }

    /**
     * 检查运力编码和批次号目的地是否一致
     */
    @Deprecated
    @GET
    @Path("/new/vehicle/seal/check/{transportCode}/{batchCode}/{sealCarType}")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101103)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.check", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse newCheckTranCodeAndBatchCode(
            @PathParam("transportCode") String transportCode, @PathParam("batchCode") String batchCode, @PathParam("sealCarType") Integer sealCarType) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            //2.获取运力信息并检查目的站点
            com.jd.tms.basic.dto.CommonDto<TransportResourceDto> vtsDto = newsealVehicleService.getTransportResourceByTransCode(transportCode);
            if (vtsDto == null) {    //JSF接口返回空
                sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                sealVehicleResponse.setMessage("查询运力信息结果为空:" + transportCode);
                return sealVehicleResponse;
            }
            //1.检查批次号
            checkBatchCode(sealVehicleResponse, vtsDto.getData(), batchCode, null);
            //批次号校验通过,且是按运力编码封车，需要校验目的地是否一致
            if (Constants.SEAL_TYPE_TRANSPORT.equals(sealCarType) && JdResponse.CODE_OK.equals(sealVehicleResponse.getCode())) {

                if (Constants.RESULT_SUCCESS == vtsDto.getCode()) { //JSF接口调用成功
                    if (Objects.equals(SealCarSourceEnum.FERRY_SEAL_CAR.getCode(), null)
                            && newsealVehicleService.isAirTransport(vtsDto.getData())
                            && transportCode.startsWith("T")) {

                    } else if (SerialRuleUtil.getReceiveSiteCodeFromSendCode(batchCode).equals(vtsDto.getData().getEndNodeId())) {  // 目标站点一致
                        sealVehicleResponse.setCode(JdResponse.CODE_OK);
                        sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                    } else {// 目标站点不一致
                        sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                        sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_RECEIVESITE_DIFF_ERROR);
                    }
                } else if (Constants.RESULT_WARN == vtsDto.getCode()) {    //查询运力信息接口返回警告，给出前台提示
                    sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                    sealVehicleResponse.setMessage(vtsDto.getMessage());
                } else { //服务出错或者出异常，打日志
                    sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                    sealVehicleResponse.setMessage("查询运力信息出错！");
                    log.warn("查询运力信息出错,出错原因:{}", vtsDto.getMessage());
                    log.warn("查询运力信息出错,运力编码:{}", transportCode);
                }
            }
        } catch (Exception e) {
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            this.log.error("封车检查运力编码和批次号目的地是否一致出错：批次号->{}运力编码->{}",batchCode, transportCode, e);
        }
        return sealVehicleResponse;
    }

    /**
     * 检查运力编码和批次号目的地是否一致(新)
     * 返回code!=200，PDA显示message
     */
    @POST
    @Path("/new/vehicle/seal/check")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101103)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.newCheckTranCodeAndBatchCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse newCheckTranCodeAndBatchCode(SealCarPreRequest sealCarPreRequest) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        String sendCode = sealCarPreRequest.getSendCode();
        Integer sealCarType = sealCarPreRequest.getSealCarType();
        String transportCode = sealCarPreRequest.getTransportCode();
        try {
            com.jd.tms.basic.dto.CommonDto<TransportResourceDto> vtsDto
                    = newsealVehicleService.getTransportResourceByTransCode(transportCode);
            if (vtsDto == null) {
                sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                sealVehicleResponse.setMessage("查询运力信息结果为空:" + transportCode);
                return sealVehicleResponse;
            }

            //1.检查批次号
            checkBatchCode(sealVehicleResponse, vtsDto.getData(), sendCode, sealCarPreRequest.getSealCarSource());
            if ((Constants.SEAL_TYPE_TRANSPORT.equals(sealCarType) || Constants.SEAL_TYPE_TASK.equals(sealCarType))
                    && JdResponse.CODE_OK.equals(sealVehicleResponse.getCode())) {
                //按任务封车 干支封车拦截校验
                if(Constants.SEAL_TYPE_TASK.equals(sealCarType)){
                    CurrentOperate currentOperate = new CurrentOperate();
                    currentOperate.setSiteCode(BusinessUtil.getCreateSiteCodeFromSendCode(sendCode));
                    InvokeResult<Boolean> interceptResult = sendVehicleTransactionManager.needInterceptOfGZ(sendCode,Constants.MENU_CODE_SEAL_GZ,currentOperate,null);
                    if(interceptResult.codeSuccess() && interceptResult.getData()){
                        sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                        sealVehicleResponse.setMessage(interceptResult.getMessage());
                        return sealVehicleResponse;
                    }
                }

                if (Constants.RESULT_SUCCESS == vtsDto.getCode() && vtsDto.getData() != null) {
                    /**
                     * 校验规则
                     *  1、普通封车：校验运力编码目的地和批次目的地一致
                     *  2、传摆封车：满足1或者目的地站点类型是中转场
                     *  3、用户使用T开头空铁运力编码时:校验【封车操作人场地】必须为批次号【始发地】或【目的地】其中之一；满足其一可操作空铁摆渡封车，否则无法封车成功。规避批次流向封错的问题【空铁新需求】
                     *  4. 用户使用T开头空铁运力编码时:不校验批次号始发地/目的地  与 T空铁运力始发地/目的地 是否相同——1月11日新增
                     * */
                    Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode);
                    Integer endNodeId = vtsDto.getData().getEndNodeId();
                    if (sealCarPreRequest.getCreateSiteCode() != null
                            && SealCarSourceEnum.FERRY_SEAL_CAR.getCode().equals(sealCarPreRequest.getSealCarSource())
                            && newsealVehicleService.isAirTransport(vtsDto.getData())
                            && transportCode.startsWith("T")) {
                        Integer createSiteCodeInSendCode = BusinessUtil.getCreateSiteCodeFromSendCode(sendCode);
                        Integer receiveSiteCodeInSendCode = BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode);
                        if (Objects.equals(sealCarPreRequest.getCreateSiteCode(), createSiteCodeInSendCode)
                                || Objects.equals(sealCarPreRequest.getCreateSiteCode(), receiveSiteCodeInSendCode)) {
                            sealVehicleResponse.setCode(JdResponse.CODE_OK);
                            sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                        } else {
                            sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                            sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_TRANSPORT_START_END_RANGE_ERROR);
                        }

                    } else if (receiveSiteCode.equals(endNodeId)) {
                        sealVehicleResponse.setCode(JdResponse.CODE_OK);
                        sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                    } else {
                        //if(SealCarSourceEnum.FERRY_SEAL_CAR.getCode().equals(sealCarSource)){
                            //不分传摆和运力都去校验目的地类型是中转场的时候 跳过目的地不一致逻辑
                            BaseStaffSiteOrgDto endNodeSite = basicPrimaryWS.getBaseSiteBySiteId(endNodeId);
                            if(endNodeSite != null
                                    && SiteSignTool.supportTemporaryTransfer(endNodeSite.getSiteSign())){
                                sealVehicleResponse.setCode(JdResponse.CODE_OK);
                                sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                                String siteName = endNodeSite.getSiteName();
                                sealVehicleResponse.setExtraBusinessCode(NewSealVehicleResponse.CODE_DESTINATION_DIFF_ERROR);
                                sealVehicleResponse.setExtraBusinessMessage(MessageFormat.format(NewSealVehicleResponse.TIPS_TRANSPORT_BATCHCODE_DESTINATION_DIFF_ERROR,siteName));
                                return sealVehicleResponse;
                            }
                        //}
                        sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                        sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_RECEIVESITE_DIFF_ERROR);
                    }
                } else if (Constants.RESULT_WARN == vtsDto.getCode()) {
                    sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                    sealVehicleResponse.setMessage(vtsDto.getMessage());
                } else {
                    sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                    sealVehicleResponse.setMessage("查询运力信息出错！");
                    log.warn("根据运力编码：【{}】查询运力信息出错,出错原因:{}", transportCode,vtsDto.getMessage());
                }
            }
        } catch (Exception e) {
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            this.log.error("封车检查运力编码和批次号目的地是否一致出错：批次号->{}运力编码->{}", sendCode, transportCode, e);
        }
        return sealVehicleResponse;
    }


    /**
     * 校验车牌号能否封车创建车次任务
     */
    @Deprecated
    @GET
    @Path("/new/vehicle/seal/verifyVehicleJobByVehicleNumber/{transportCode}/{vehicleNumber}/{sealCarType}")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB,bizType = 1011,operateType = 101103)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.verifyVehicleJobByVehicleNumber", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse verifyVehicleJobByVehicleNumber(
            @PathParam("transportCode") String transportCode, @PathParam("vehicleNumber") String vehicleNumber, @PathParam("sealCarType") Integer sealCarType) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            //按运力封车，需要校验车牌号能否生成车次任务
            if (Constants.SEAL_TYPE_TRANSPORT.equals(sealCarType)) {
                CommonDto<String> dto = newsealVehicleService.verifyVehicleJobByVehicleNumber(transportCode,vehicleNumber);
                if (dto == null) {    //JSF接口返回空
                    log.warn("校验车牌号能否封车创建车次任务返回值为空:{}-{}-{}",transportCode, vehicleNumber, sealCarType);
                    sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                    sealVehicleResponse.setMessage("校验车牌号能否封车创建车次任务返回值为空:" + transportCode + "-" + vehicleNumber + "-" + sealCarType);
                    return sealVehicleResponse;
                }
                if (Constants.RESULT_SUCCESS == dto.getCode()) { //JSF接口调用成功
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                }else {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage(dto.getMessage());
                }
            }
        } catch (Exception e) {
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            this.log.error("校验车牌号能否封车创建车次任务异常.运力编码:{},车牌号:{}",transportCode, vehicleNumber, e);
        }
        return sealVehicleResponse;
    }

    /**
     * 校验车牌号能否封车创建车次任务（新）
     */
    @POST
    @Path("/new/vehicle/seal/newVerifyVehicleJobByVehicleNumber")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB,bizType = 1011,operateType = 101103)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.newVerifyVehicleJobByVehicleNumber", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse newVerifyVehicleJobByVehicleNumber(SealCarPreRequest sealCarPreRequest) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        try {
            if(!Objects.equals(sealCarPreRequest.getSealCarType(), SealCarTypeEnum.SEAL_BY_TRANSPORT_CAPABILITY.getType())){
                return sealVehicleResponse;
            }
            // 校验封车任务标识
            boolean transportTaskCheck;
            if(Objects.equals(sealCarPreRequest.getSealCarSource(), SealCarSourceEnum.COMMON_SEAL_CAR.getCode())){
                // 普通封车：只需校验运输任务
                transportTaskCheck = true;
            }else {
                // 传摆封车：需要校验车牌号能否生成车次任务
                CommonDto<String> dto = newsealVehicleService.newVerifyVehicleJobByVehicleNumber(sealCarPreRequest);
                boolean sucFlag = dto != null && Constants.RESULT_SUCCESS == dto.getCode();
                sealVehicleResponse.setCode(sucFlag ? JdResponse.CODE_OK : NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                sealVehicleResponse.setMessage(sucFlag ? JdResponse.MESSAGE_OK : dto == null ? "校验车牌号能否封车创建车次任务失败" : dto.getMessage());
                transportTaskCheck = sucFlag;
            }
            if(transportTaskCheck){
                // 校验运输任务（返回结果只做提示展示）
                ImmutablePair<Integer, String> checkResult = transportRelatedService.checkTransportTask(sealCarPreRequest.getCreateSiteCode(),
                        null, null, null, sealCarPreRequest.getVehicleNumber());
                sealVehicleResponse.setExtraBusinessCode(checkResult.left);
                sealVehicleResponse.setExtraBusinessMessage(checkResult.right);
            }
        } catch (Exception e) {
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            this.log.error("校验车牌号能否封车创建车次任务失败,请求参数：【{}】",JsonHelper.toJson(sealCarPreRequest),e);
        }
        return sealVehicleResponse;
    }

    /**
     * 校验批次中的体积是否超标
     */
    @POST
    @Path("/new/vehicle/seal/verifySealVehicleVolume")
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.verifySendVolume", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public SealVehicleVolumeVerifyResponse verifySendVolume(SealVehicleVolumeVerifyRequest request){

        SealVehicleVolumeVerifyResponse response = new SealVehicleVolumeVerifyResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        if (request == null) {
            log.warn("NewSealVehicleResource verifySendVolume --> 传入参数非法");
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        if(!isNeedCheck(request.getSealSiteId())){
            response.setCode(JdCResponse.CODE_SUCCESS);
            response.setMessage(JdCResponse.MESSAGE_SUCCESS);
            return response;
        }
        try{
            SealCarDto sealCarDto = new SealCarDto();
            sealCarDto.setSource(request.getSource());
            sealCarDto.setVehicleNumber(request.getVehicleNumber());
            sealCarDto.setTransportCode(request.getTransportCode());
            sealCarDto.setBatchCodes(request.getBatchCodes());
            sealCarDto.setSealCodes(request.getSealCodes());
            sealCarDto.setSealCarTime(DateHelper.parseDate(request.getSealCarTime(),Constants.DATE_TIME_FORMAT));
            sealCarDto.setSealSiteId(request.getSealSiteId());
            sealCarDto.setSealSiteCode(request.getSealSiteCode());
            sealCarDto.setSealSiteName(request.getSealSiteName());
            sealCarDto.setSealUserCode(sealCarDto.getDesealUserCode());
            sealCarDto.setSealUserName(request.getSealUserName());
            sealCarDto.setSealCarType(request.getSealCarType());
            sealCarDto.setItemSimpleCode(request.getItemSimpleCode());
            sealCarDto.setVolume(request.getVolume());
            sealCarDto.setWeight(request.getWeight());

            CommonDto<String> dto = newsealVehicleService.verifySealVehicleVolume(sealCarDto);
            if(dto == null){
                response.setCode(JdCResponse.CODE_ERROR);
                response.setMessage(JdCResponse.MESSAGE_ERROR);
                return response;
            }
            if(dto.getCode() == CommonDto.CODE_SUCCESS){
                response.setCode(JdCResponse.CODE_SUCCESS);
                response.setMessage(JdCResponse.MESSAGE_SUCCESS);
            }else if(dto.getCode() == SEAL_VOLUME_CONFIRM){
                response.setCode(JdCResponse.CODE_CONFIRM);
                response.setMessage(dto.getMessage());
            }else {
                response.setCode(JdCResponse.CODE_FAIL);
                response.setMessage(dto.getMessage());
            }
        }catch(Exception e){
            response.setCode(JdCResponse.CODE_ERROR);
            response.setMessage(JdCResponse.MESSAGE_ERROR);
            this.log.error("校验批次的体积异常，批次号:{}",request.getBatchCodes().toString(), e);
        }
        return response;
    }

    /**
     * 是否需要校验封车批次体积
     *
     * @param siteCode
     * @return
     */
    private boolean isNeedCheck(Integer siteCode) {
        String sealVolumeCheckSites = uccPropertyConfiguration.getSealVolumeCheckSites();
        if(StringUtils.isEmpty(sealVolumeCheckSites)){
            return true;
        }
        List<String> siteCodes = Arrays.asList(sealVolumeCheckSites.split(Constants.SEPARATOR_COMMA));
        if(siteCodes.contains(String.valueOf(siteCode))){
            return true;
        }
        return false;
    }


    /**
     * 封车功能
     * <p>
     *     按任务封车或按运力封车使用此接口（传摆封车默认按运力封车）
     * </p>
     */
    @POST
    @Path("/new/vehicle/seal")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1011)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.seal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse seal(NewSealVehicleRequest request) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null || request.getData() == null) {
                log.warn("NewSealVehicleResource seal --> 传入参数非法");
                sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return sealVehicleResponse;
            }

            //批次为空的列表信息
            Map<String, String> emptyBatchCode =new HashMap<String,String>();

            CommonDto<String> returnCommonDto = newsealVehicleService.seal(request.getData(),emptyBatchCode);
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setData(returnCommonDto.getData());
                    if(emptyBatchCode==null || emptyBatchCode.isEmpty()){
                        sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_SEAL_SUCCESS);
                    }else {
                        sealVehicleResponse.setMessage(getMsgByList(emptyBatchCode)); //NewSealVehicleResponse.CODE_SEAL_SUCCEED_BUT_WARN
                    }

                } else {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
        } catch (Exception e) {
            this.log.error("NewSealVehicleResource.seal-error", e);
        }
        return sealVehicleResponse;
    }

    private String getMsgByList(Map<String, String> emptyBatchCode){
        StringBuilder msg= new StringBuilder("封车成功。已剔除无发货数据批次：\r\n");
        for(Map.Entry<String, String> entry : emptyBatchCode.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            msg.append(mapValue).append(":").append(mapKey).append("\r\n");

        }

        return msg.toString();
    }

    /**
     * VOS封车业务同时生成车次任务
     * <p>
     *     传摆封车使用
     * </p>
     */
    @POST
    @Path("/new/vehicle/doSealCarWithVehicleJob")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1011,operateType = 101102)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.doSealCarWithVehicleJob", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse doSealCarWithVehicleJob(NewSealVehicleRequest request) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null || request.getData() == null) {
                log.warn("NewSealVehicleResource doSealCarWithVehicleJob --> 传入参数非法");
                sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return sealVehicleResponse;
            }

            //批次为空的列表信息
            Map<String, String> emptyBatchCode = new HashMap<String,String>();

            sealVehicleResponse = newsealVehicleService.doSealCarWithVehicleJob(request.getData(),emptyBatchCode);
            if (sealVehicleResponse != null) {
                if (JdResponse.CODE_OK.equals(sealVehicleResponse.getCode())) {
                    if(emptyBatchCode!=null && !emptyBatchCode.isEmpty()){
                        //sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_SEAL_SUCCEED_BUT_WARN);
                        sealVehicleResponse.setMessage(getMsgByList(emptyBatchCode));
                    }
                }
            }
        } catch (Exception e) {
            this.log.error("NewSealVehicleResource.doSealCarWithVehicleJob-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 取消封车
     * @param request
     * @return
     */
  @POST
  @Path("/new/vehicle/cancelSeal")
  @BusinessLog(
    sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB,
    bizType = 11011,
    operateType = 1101101
  )
  @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.cancelSeal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
  public NewSealVehicleResponse cancelSeal(cancelSealRequest request) {
    NewSealVehicleResponse sealVehicleResponse =
        new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
    try
    {
      if (null == request || StringHelper.isEmpty(request.getBatchCode())) {
        log.warn("NewSealVehicleResource cancelSeal --> 传入参数非法");
        sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
        sealVehicleResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
        return sealVehicleResponse;
      }

        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(request.getBatchCode());//获取批次号目的地
        //1.批次号是否符合编码规范，不合规范直接返回参数错误
        if (receiveSiteCode == null) {
            sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_BATCHCODE_PARAM_ERROR);
            return sealVehicleResponse;
        }

        sealVehicleResponse = newsealVehicleService.cancelSeal(request);
    }
    catch (Exception e) {
      this.log.error("NewSealVehicleResource.cancelSeal-error", e);
    }
      return sealVehicleResponse;
    }

    /**
     * 获取待解封信息
     */
    @POST
    @Path("/new/vehicle/findSealInfo")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1012 ,operateType = 101202)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.findSealInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse findSealInfo(NewSealVehicleRequest request) {

        NewSealVehicleResponse<List<SealCarDto>> sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {

            if (request == null || StringHelper.isEmpty(request.getEndSiteId()) ||
                    (StringHelper.isEmpty(request.getStartSiteId()) && StringHelper.isEmpty(request.getVehicleNumber()) && StringHelper.isEmpty(request.getBatchCode()))) {
                //目的站点为空，或者始发站点和车牌号同时为空
                sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_SITECODE_PARAM_NULL_ERROR);
                return sealVehicleResponse;
            }

            SealCarDto sealCarDto = new SealCarDto();
            sealCarDto.setStatus(request.getStatus());
            sealCarDto.setSealCode(request.getSealCode());
            sealCarDto.setTransportCode(request.getTransportCode());
            sealCarDto.setBatchCode(request.getBatchCode());
            //查询15天内的待解任务
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, rollBackDay);
            sealCarDto.setSealCarTimeBegin(c.getTime());

            if (StringHelper.isNotEmpty(request.getVehicleNumber())) {
                String ChineseVehicleNumber = carLicenseChangeUtil.formateLicense2Chinese(request.getVehicleNumber());

                //增加车牌号的条件
                sealCarDto.setVehicleNumber(StringUtils.isEmpty(ChineseVehicleNumber)?request.getVehicleNumber():ChineseVehicleNumber);
            }


            Integer intStartSiteId = NumberHelper.isNumber(request.getStartSiteId()) ? Integer.parseInt(request.getStartSiteId()) : null;
            Integer intEndSiteId = NumberHelper.isNumber(request.getEndSiteId()) ? Integer.parseInt(request.getEndSiteId()) : null;


            sealCarDto.setStartSiteId(intStartSiteId);
            sealCarDto.setEndSiteId(intEndSiteId);

            PageDto<SealCarDto> pageDto = new PageDto<SealCarDto>();
            pageDto.setPageSize(request.getPageNums());

            if(log.isInfoEnabled()){
                log.info("解封车查询参数，sealCarDto：{}", JsonHelper.toJson(sealCarDto));
                log.info("解封车查询参数，pageDto：{}", JsonHelper.toJson(pageDto));
            }

            CommonDto<PageDto<SealCarDto>> returnCommonDto = newsealVehicleService.findSealInfo(sealCarDto, pageDto);

            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                    List<SealCarDto> sealCarDtos = returnCommonDto.getData().getResult();
                    if (sealCarDtos != null && sealCarDtos.size() > 0) {    //合并批次号并按创建时间倒序排序
                        sealCarDtos = mergeBatchCode(sealCarDtos);
                        filterDeSealCode(sealCarDtos);
                        sortSealCarDtos(sealCarDtos);
                        sealVehicleResponse.setData(sealCarDtos);
                    } else {
                        sealVehicleResponse.setCode(JdResponse.CODE_OK_NULL);
                        sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK_NULL);
                        sealVehicleResponse.setData(null);
                    }
                } else {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                    sealVehicleResponse.setData(null);
                }
            }
        } catch (Exception e) {
            this.log.error("NewSealVehicleResource.findSealInfo-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 解封车校验
     *
     * @param request
     * @return
     */
    @POST
    @Path("/new/vehicle/unseal/check")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1012,operateType = 101201)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.unsealCheck", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse unsealCheck(NewSealVehicleRequest request) {
        NewSealVehicleResponse<String> sealVehicleResponse = new NewSealVehicleResponse<String>(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        try {
            if (request.getData().size() > 20) {
                sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage("单次解封车最大选择20条数据！");
            } else {
                List<String> unsealCarOutArea = newsealVehicleService.isSealCarInArea(request.getData());
                if (unsealCarOutArea != null && !unsealCarOutArea.isEmpty()) {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_UNSEAL_CAR_OUT_CHECK);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_UNSEAL_CAR_OUT_CHECK + unsealCarOutArea.toString());
                }
            }
        } catch (Exception e) {
            this.log.error("NewSealVehicleResource.unsealCheck-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 解封车功能
     */
    @POST
    @Path("/new/vehicle/unseal")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1012)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.unseal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse unseal(NewSealVehicleRequest request) {
        return unsealWithCheckUsage(request, true);
    }

    public NewSealVehicleResponse unsealWithCheckUsage(NewSealVehicleRequest request, boolean checkUsage) {
        NewSealVehicleResponse<String> sealVehicleResponse = new NewSealVehicleResponse<String>(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null || request.getData() == null) {
                log.warn("NewSealVehicleResource unseal --> 传入参数非法");
                sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return sealVehicleResponse;
            }

            if(checkUsage) {
                final List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> data = request.getData();
                final com.jd.bluedragon.distribution.wss.dto.SealCarDto sealCarDto = data.get(0);
                final MenuUsageConfigRequestDto menuUsageConfigRequestDto = new MenuUsageConfigRequestDto();
                menuUsageConfigRequestDto.setMenuCode("0101014");
                final CurrentOperate currentOperate = new CurrentOperate();
                currentOperate.setSiteCode(sealCarDto.getDesealSiteId());
                menuUsageConfigRequestDto.setCurrentOperate(currentOperate);
                final User user = new User();
                user.setUserErp(sealCarDto.getDesealUserCode());
                user.setUserName(sealCarDto.getDesealUserName());
                menuUsageConfigRequestDto.setUser(user);
                final MenuUsageProcessDto clientMenuUsageConfig = baseService.getClientMenuUsageConfig(menuUsageConfigRequestDto);

                if(clientMenuUsageConfig != null && Objects.equals(Constants.YN_NO, clientMenuUsageConfig.getCanUse())){
                    sealVehicleResponse.setCode(JdResponse.CODE_SEE_OTHER);
                    sealVehicleResponse.setMessage(clientMenuUsageConfig.getMsg());
                    return sealVehicleResponse;
                }
            }

            CommonDto<String> returnCommonDto = newsealVehicleService.unseal(request.getData());
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_UNSEAL_SUCCESS);
                    sealVehicleResponse.setData(returnCommonDto.getData());
                } else {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
        } catch (Exception e) {
            this.log.error("NewSealVehicleResource.unseal-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 新解封车功能
     */
    @POST
    @Path("/new/vehicle/newUnseal")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1012)
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.newUnseal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewUnsealVehicleResponse<Boolean> newUnseal(NewSealVehicleRequest request) {
        return newUnsealWithCheckUsage(request, true);
    }

    public NewUnsealVehicleResponse<Boolean> newUnsealWithCheckUsage(NewSealVehicleRequest request, boolean checkUsage) {
        NewUnsealVehicleResponse<Boolean> unSealVehicleResponse = new NewUnsealVehicleResponse<Boolean>();
        try {
            if (request == null || request.getData() == null) {
                log.warn("NewSealVehicleResource newUnseal --> 传入参数非法");
                unSealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                unSealVehicleResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return unSealVehicleResponse;
            }

            if(checkUsage) {
                final List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> data = request.getData();
                final com.jd.bluedragon.distribution.wss.dto.SealCarDto sealCarDto = data.get(0);
                final MenuUsageConfigRequestDto menuUsageConfigRequestDto = new MenuUsageConfigRequestDto();
                menuUsageConfigRequestDto.setMenuCode("0101014");
                final CurrentOperate currentOperate = new CurrentOperate();
                currentOperate.setSiteCode(sealCarDto.getDesealSiteId());
                menuUsageConfigRequestDto.setCurrentOperate(currentOperate);
                final User user = new User();
                user.setUserErp(sealCarDto.getDesealUserCode());
                user.setUserName(sealCarDto.getDesealUserName());
                menuUsageConfigRequestDto.setUser(user);
                final MenuUsageProcessDto clientMenuUsageConfig = baseService.getClientMenuUsageConfig(menuUsageConfigRequestDto);

                if(clientMenuUsageConfig != null && Objects.equals(Constants.YN_NO, clientMenuUsageConfig.getCanUse())){
                    unSealVehicleResponse.setCode(JdResponse.CODE_SEE_OTHER);
                    unSealVehicleResponse.setMessage(clientMenuUsageConfig.getMsg());
                    return unSealVehicleResponse;
                }
            }

            CommonDto<String> returnCommonDto = newsealVehicleService.unseal(request.getData());
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    unSealVehicleResponse.setCode(JdResponse.CODE_OK);
                    unSealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_UNSEAL_SUCCESS);
                    // 抽检校验
                    checkIsNeedSpotCheck(request, unSealVehicleResponse);
                } else {
                    unSealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    unSealVehicleResponse.setMessage(returnCommonDto.getMessage());
                }
            }
        } catch (Exception e) {
            this.log.error("NewSealVehicleResource.newUnseal-error", e);
        }
        return unSealVehicleResponse;
    }

    /**
     * 查询待解封签号（无到货解封签专用）
     * @param param
     * @return
     */
    @POST
    @Path("/new/vehicle/querySealCodes")
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.querySealCodes", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse<SealCodesResponse> querySealCodes (SealCodeRequest param) {
        return newsealVehicleService.querySealCodes(param);
    }

    /**
     * 无到货解封签（无到货解封签专用）
     * @param param
     * @return
     */
    @POST
    @Path("/new/vehicle/doDeSealCodes")
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.doDeSealCodes", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse<String> doDeSealCodes (DeSealCodeRequest param) {
        return newsealVehicleService.doDeSealCodes(param);
    }

    /**
     * 无货解封签异常上报
     * @param param
     * @return
     */
    @POST
    @Path("/new/vehicle/createTransAbnormalStandard")
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.createTransAbnormalStandard", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse<String> createTransAbnormalStandard (TransAbnormalDto param) {
        return newsealVehicleService.createTransAbnormalStandard(param);
    }

    @GET
    @Path("/new/vehicle/getTransAbnormalTypeCode")
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.getTransAbnormalTypeCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse<List<TransAbnormalTypeDto>> getTransAbnormalTypeCode () {
        return newsealVehicleService.getTransAbnormalTypeCode();
    }

    @POST
    @Path("/new/vehicle/createTransAbnormalAndDeSealCode")
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.createTransAbnormalAndDeSealCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse<String> createTransAbnormalAndDeSealCode(TransAbnormalAndDeSealRequest request){
        return newsealVehicleService.createTransAbnormalAndDeSealCode(request);
    }

    @POST
    @Path("/new/vehicle/createTransAbnormalAndUnseal")
    @JProfiler(jKey = "DMS.WEB.NewSealVehicleResource.createTransAbnormalAndUnseal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewUnsealVehicleResponse<Boolean> createTransAbnormalAndUnseal(TransAbnormalAndUnsealRequest request){
        return createTransAbnormalAndUnsealWithCheckUsage(request, true);
    }

    public NewUnsealVehicleResponse<Boolean> createTransAbnormalAndUnsealWithCheckUsage(TransAbnormalAndUnsealRequest request, boolean checkUsage){
        NewUnsealVehicleResponse<Boolean> unSealVehicleResponse = new NewUnsealVehicleResponse<>();

        NewSealVehicleResponse<String> response = this.createTransAbnormalStandard(request.getTransAbnormalDto());
        if (response == null) {
            unSealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            unSealVehicleResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            return unSealVehicleResponse;
        }
        if (!JdResponse.CODE_OK.equals(response.getCode())) {
            unSealVehicleResponse.setCode(response.getCode());
            unSealVehicleResponse.setMessage(response.getMessage());
            return unSealVehicleResponse;
        }

        try {
            createTransAbnormalAndUnseal2jmq(request);
        } catch (JMQException e) {
            this.log.error("提报异常并解封车异常 NewSealVehicleResource.createTransAbnormalAndUnsealWithCheckUsage-error", e);
        }


        try {
            NewSealVehicleRequest request1 = new NewSealVehicleRequest();
            request1.setData(Collections.singletonList(request.getSealCarDto()));
            unSealVehicleResponse = this.newUnsealWithCheckUsage(request1, checkUsage);
            return unSealVehicleResponse;

        } catch (Exception e) {
            unSealVehicleResponse = new NewUnsealVehicleResponse<>();
            unSealVehicleResponse.setCode(NewSealVehicleResponse.CODE_INTERNAL_ERROR);
            unSealVehicleResponse.setMessage("提报异常成功，解封签异常");
        }
        return unSealVehicleResponse;
    }

    @JProfiler(jKey = "NewSealVehicleResource.createTransAbnormalAndUnseal2jmq", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP}) public void createTransAbnormalAndUnseal2jmq(TransAbnormalAndUnsealRequest request) throws JMQException {
        TransAbnormalDto transAbnormalDto = request.getTransAbnormalDto();
        com.jd.bluedragon.distribution.wss.dto.SealCarDto sealCarDto = request.getSealCarDto();
        CreateTransAbnormalAndUnsealJmqMsg msg = new CreateTransAbnormalAndUnsealJmqMsg();
        msg.setDesealCarTime(sealCarDto.getDesealCarTime());
        msg.setDesealCodes(sealCarDto.getDesealCodes());
        msg.setDesealSiteId(sealCarDto.getDesealSiteId());
        msg.setDesealSiteName(sealCarDto.getDesealSiteName());
        msg.setDesealUserCode(sealCarDto.getDesealUserCode());
        msg.setDesealUserName(sealCarDto.getDesealUserName());
        msg.setSealCarCode(sealCarDto.getSealCarCode());
        msg.setVehicleNumber(sealCarDto.getVehicleNumber());
        msg.setAbnormalDesc(transAbnormalDto.getAbnormalDesc());
        msg.setAbnormalTypeCode(transAbnormalDto.getAbnormalTypeCode());
        msg.setAbnormalTypeName(transAbnormalDto.getAbnormalTypeName());
        msg.setPhotoUrlList(transAbnormalDto.getPhotoUrlList());
        msg.setReferBillCode(transAbnormalDto.getReferBillCode());
        msg.setReferBillType(transAbnormalDto.getReferBillType());

        msg.setSource(TYPE_CREATE_TRANS_ABNORMAL_AND_UNSEAL);
        msg.setCreateTransAbnormalTime(new Date());

        String msgkey = "";
        if (StringUtils.isNotEmpty(sealCarDto.getSealUserCode())) {
            msgkey = sealCarDto.getSealUserCode();
        }
        String body = JSONObject.toJSONString(msg);
        createTransAbnormalAndUnsealProducer.send(msgkey, body);
        log.info("提报异常并解封车消息发送成功，消息内容：{}", body);

    }


    /**
     * 校验是否需要抽检
     *
     * @param request
     * @param unSealVehicleResponse
     */
    private void checkIsNeedSpotCheck(NewSealVehicleRequest request, NewUnsealVehicleResponse<Boolean> unSealVehicleResponse) {
        List<WaitSpotCheckQueryCondition> queryConditions = new ArrayList<WaitSpotCheckQueryCondition>();
        List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> unSealCarList = request.getData();
        if(CollectionUtils.isEmpty(unSealCarList)){
            return;
        }
        Map<String, Set<String>> unSealCarMap = new HashMap<>();
        // 按车牌过滤
        for (com.jd.bluedragon.distribution.wss.dto.SealCarDto sealCarDto : unSealCarList) {
            String vehicleNumber = carLicenseChangeUtil.formateLicense2Chinese(sealCarDto.getVehicleNumber());
            List<String> batchCodes = sealCarDto.getBatchCodes();
            if(unSealCarMap.containsKey(vehicleNumber)){
                unSealCarMap.get(vehicleNumber).addAll(batchCodes);
            }else {
                Set<String> batchCodeSet = new HashSet<>();
                if(CollectionUtils.isNotEmpty(batchCodes)){
                    batchCodeSet.addAll(batchCodes);
                }
                unSealCarMap.put(vehicleNumber, batchCodeSet);
            }
        }
        // 组装查询条件
        for (Map.Entry<String, Set<String>> entry : unSealCarMap.entrySet()) {
            if(CollectionUtils.isEmpty(entry.getValue())){
                continue;
            }
            WaitSpotCheckQueryCondition condition = new WaitSpotCheckQueryCondition();
            condition.setVehicleNumber(entry.getKey());
            condition.setBatchCodeSet(entry.getValue());
            condition.setUnSealTime(new Date());
            queryConditions.add(condition);
        }
        if(CollectionUtils.isEmpty(queryConditions)){
            return;
        }
        JdResult<SpotCheckResponse> checkResult = reportExternalManager.checkIsNeedSpotCheck(queryConditions);
        if(checkResult.isSucceed()
        		&& checkResult.getData() != null
        		&& Boolean.TRUE.equals(checkResult.getData().getNeedCheck())){
            unSealVehicleResponse.setBusinessCode(NewUnsealVehicleResponse.SPOT_CHECK_UNSEAL_HINT_CODE);
            unSealVehicleResponse.setBusinessMessage(
            		String.format(NewUnsealVehicleResponse.SPOT_CHECK_UNSEAL_HINT_MESSAGE, 
            		SpotCheckTypeEnum.getNameByCode(checkResult.getData().getSpotCheckType())));
        }
    }

    /**
     * 1.检查批次号是否有且符合编码规则
     * 否则提示“请输入正确的批次号!”
     * 2.检查批次号是否已经封车
     * 已封车则提示“该发货批次号已操作封车，无法重复操作！”
     *
     * @param batchCode
     * @return
     */
    private void checkBatchCode(NewSealVehicleResponse sealVehicleResponse, TransportResourceDto transportResourceDto, String batchCode, Integer sealCarSource) {
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(batchCode);//获取批次号目的地
        //1.批次号是否符合编码规范，不合规范直接返回参数错误
        if (receiveSiteCode == null) {
            sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_BATCHCODE_PARAM_ERROR);
            return;
        }

        // 1.1 校验批次号是否可用
        InvokeResult<Boolean> sendChkResult = sendCodeService.validateSendCodeEffective(batchCode);
        if (!sendChkResult.codeSuccess()) {
            sealVehicleResponse.setCode(sendChkResult.getCode());
            sealVehicleResponse.setMessage(sendChkResult.getMessage());
            return;
        }

        //2.是否已经封车: 同一个批次号，T开头空铁运力封几次都行 ， 非T空铁运力的只能封一次，不能重复封车 【空铁新增逻辑】
        CommonDto<Boolean> isSealed = null;
        boolean flag = Objects.equals(SealCarSourceEnum.FERRY_SEAL_CAR.getCode(), sealCarSource)
                && newsealVehicleService.isAirTransport(transportResourceDto)
                && transportResourceDto.getTransCode().startsWith("T");
        if (!flag) {
            //空铁摆渡T运力不校验批次号是否封车
            isSealed = newsealVehicleService.isBatchCodeHasSealedExcludeAirFerry(batchCode);

            if (isSealed == null) {
                sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                sealVehicleResponse.setMessage("服务异常，运输系统查询批次号状态结果为空！");
                log.warn("服务异常，运输系统查询批次号状态结果为空, 批次号:{}", batchCode);
                return;
            }

            if (Constants.RESULT_SUCCESS == isSealed.getCode()) {//服务正常
                if (Boolean.TRUE.equals(isSealed.getData())) {//已被封车
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_BATCH_CODE_SEALED);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_BATCH_CODE_SEALED);
                } else {//未被封车
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                }
            } else if (Constants.RESULT_WARN == isSealed.getCode()) { //接口返回警告信息，给前台提示
                sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                sealVehicleResponse.setMessage(isSealed.getMessage());
            } else {//服务出错或者出异常，打日志
                sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                sealVehicleResponse.setMessage("服务异常，运输系统查询批次号状态失败！");
                log.warn("服务异常，运输系统查询批次号状态失败, 批次号:{}", batchCode);
                log.warn("服务异常，运输系统查询批次号状态失败，失败原因:{}", isSealed.getMessage());
            }

        } else {
            sealVehicleResponse.setCode(JdResponse.CODE_OK);
            sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
        }

        //3.批次号是否存在（最后查询批次号是否存在，不存在时给前台提示）
        // 批次号没有运单发货记录，也没有物资发货记录，判定为不存在
        if (JdResponse.CODE_OK.equals(sealVehicleResponse.getCode()) && !newsealVehicleService.checkBatchCodeIsNewSealVehicle(batchCode)) {
                log.info("批次号不包含运单发货记录，也不包含物资发货记录!, batchCode:[{}]", batchCode);
                sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_BATCHCODE_PARAM_NOTEXSITE_ERROR);
        }
    }

    /**
     * 合并同一运力编码、同意操作时间的批次号
     *
     * @param SealCarDtos
     * @return
     */
    private List<SealCarDto> mergeBatchCode(List<SealCarDto> SealCarDtos) {
        Map<String, SealCarDto> mergeMap = new HashedMap();
        for (SealCarDto dto : SealCarDtos) {
            String key = dto.getTransportCode() + dto.getSealCarTime().getTime() + dto.getSealCarCode();
            if (dto.getBatchCodes() == null) {
                dto.setBatchCodes(new ArrayList<String>());
            }
            if (mergeMap.containsKey(key)) {
                mergeMap.get(key).getBatchCodes().addAll(dto.getBatchCodes());
            } else {
                mergeMap.put(key, dto);
            }
        }
        List<SealCarDto> mergeResult = new ArrayList<SealCarDto>(mergeMap.size());
        for (SealCarDto dto : mergeMap.values()) {
            String batchCodes = dto.getBatchCodes().toString();
            dto.getBatchCodes().clear();
            dto.getBatchCodes().add(StringUtils.strip(batchCodes, "[]"));    //去掉中括号
            mergeResult.add(dto);
        }

        return mergeResult;
    }

    /**
     * 过滤待解封签号
     * 返回sealCodes中，将存在于deSealCodes中的数据去掉
     * @param sealCarDtos
     * @return
     */
    private List<SealCarDto> filterDeSealCode(List<SealCarDto> sealCarDtos) {
        for (SealCarDto item : sealCarDtos) {
            if (CollectionUtils.isEmpty(item.getSealCodes()) || CollectionUtils.isEmpty(item.getDesealCodes())) {
                continue;
            }
            for (String deSealCode : item.getDesealCodes()) {
                if (!item.getSealCodes().contains(deSealCode)) {
                    continue;
                }
                item.getSealCodes().remove(deSealCode);
            }
        }
        return sealCarDtos;
    }

    /**
     * 按创建时间倒序排序
     *
     * @param SealCarDtos
     */
    private void sortSealCarDtos(List<SealCarDto> SealCarDtos) {
        Collections.sort(SealCarDtos, new Comparator<SealCarDto>() {
            @Override
            public int compare(SealCarDto dto1, SealCarDto dto2) {
                return dto2.getCreateTime().compareTo(dto1.getCreateTime());
            }
        });
    }

    /**
     * 根据派车任务明细简码获取派车任务明细
     *
     * @param transWorkItemCode
     * @return
     */
    private void buildTransWorkItemBySimpleCode(TransWorkItemResponse sealVehicleResponse, String transWorkItemCode) {
        try {
            com.jd.tms.jdi.dto.CommonDto<TransWorkItemDto> returnCommonDto = jdiQueryWSManager.queryTransWorkItemBySimpleCode(transWorkItemCode);
            if (returnCommonDto != null && returnCommonDto.getData() != null) {
                TransWorkItemDto item = returnCommonDto.getData();
                sealVehicleResponse.setTransPlanCode(item.getTransPlanCode());
                sealVehicleResponse.setRouteLineCode(item.getRouteLineCode());
                sealVehicleResponse.setRouteLineName(item.getRouteLineName());
                sealVehicleResponse.setScheduleType(item.getScheduleType());
                // ScheduleType=1 是卡班调度模式
                if (SCHEDULE_TYPE_KA_BAN.equals(item.getScheduleType())) {
                    if (StringUtils.isNotEmpty(item.getTransPlanCode())) {
                        ColdChainSend coldChainSend = coldChainSendService.getByTransCode(item.getTransPlanCode());
                        if (coldChainSend != null) {
                            sealVehicleResponse.setSendCode(coldChainSend.getSendCode());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("[调用TMS-TFC-JSF接口]根据派车任务明细简码获取派车任务明细时发生异常", e);
        }
    }
}
