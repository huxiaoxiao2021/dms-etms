package com.jd.bluedragon.distribution.rest.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.TmsTfcWSManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.request.cancelSealRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.api.response.TransWorkItemResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.seal.service.CarLicenseChangeUtil;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import com.jd.tms.tfc.dto.TransWorkItemDto;
import com.jd.tms.tfc.dto.TransWorkItemWsDto;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private NewSealVehicleService newsealVehicleService;

    @Autowired
    private CarLicenseChangeUtil carLicenseChangeUtil;

    @Autowired
    private TmsTfcWSManager tmsTfcWSManager;

    @Autowired
    private ColdChainSendService coldChainSendService;

    private static final int ROLL_BACK_DAY = -7; //查询几天内的带解任务（负数）
    private static final int RANGE_HOUR = 2; //运力编码在两小时范围内

    /**
     * 校验并获取运力编码信息
     *
     * @param request
     * @return
     */
    @POST
    @Path("/new/vehicle/seal/transportCode")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101101)
    public RouteTypeResponse getTransportCode(NewSealVehicleRequest request) {
        logger.info("封车校验运力编码请求体：" + JsonHelper.toJson(request));
        RouteTypeResponse response = new RouteTypeResponse();
        if (StringUtils.isEmpty(request.getTransportCode()) || !NumberHelper.isPositiveNumber(request.getSiteCode())) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        try {
            com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> vtsDto = newsealVehicleService.getTransportResourceByTransCode(request.getTransportCode());
            if (vtsDto == null) {    //JSF接口返回空
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("查询运力信息结果为空:" + request.getTransportCode());
                return response;
            }
            if (Constants.RESULT_SUCCESS == vtsDto.getCode()) { //JSF接口调用成功
                VtsTransportResourceDto vtrd = vtsDto.getData();
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
                logger.error("查询运力信息出错,出错原因:" + vtsDto.getMessage());
                logger.error("查询运力信息出错,运力编码:" + request.getTransportCode());
            }
            return response;
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            logger.error("通过运力编码获取基础资料信息异常：" + JsonHelper.toJson(request), e);
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
    private RouteTypeResponse checkTransportCode(VtsTransportResourceDto data, Integer createSiteCode) {
        RouteTypeResponse response = new RouteTypeResponse();

        //设置运力基本信息
        response.setSiteCode(data.getEndNodeId());
        response.setDriverId(data.getCarrierId());
        response.setSendUserType(data.getTransType());
        response.setRouteType(data.getRouteType());
        response.setDriver(data.getCarrierName());
        response.setTransWay(data.getTransMode());
        response.setTransWayName(data.getTransModeName());
        response.setCarrierType(data.getTransType());

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
    public TransWorkItemResponse getVehicleNumBySimpleCode(@PathParam("simpleCode") String simpleCode) {
        TransWorkItemResponse sealVehicleResponse = new TransWorkItemResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            com.jd.tms.tfc.dto.CommonDto<TransWorkItemDto> returnCommonDto = newsealVehicleService.queryTransWorkItemBySimpleCode(simpleCode);
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage("根据任务简码获取任务信息!");
                    sealVehicleResponse.setVehicleNumber(returnCommonDto.getData().getVehicleNumber());
                    sealVehicleResponse.setTransType(returnCommonDto.getData().getTransWay());
                } else {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                }
            }
        } catch (Exception e) {
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            this.logger.error("根据任务简码获取任务信息：任务简码->" + simpleCode, e);
        }
        return sealVehicleResponse;
    }

    /**
     * 根据车牌号获取派车明细编码或根据派车明细编码获取车牌号
     */
    @POST
    @Path("/new/vehicle/seal/workitem/query")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101104)
    public TransWorkItemResponse getVehicleNumberOrItemCodeByParam(NewSealVehicleRequest request) {
        TransWorkItemResponse sealVehicleResponse = new TransWorkItemResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null || StringUtils.isEmpty(request.getUserErp()) || StringUtils.isEmpty(request.getDmsCode()) ||
                    (StringUtils.isEmpty(request.getTransWorkItemCode()) && StringUtils.isEmpty(request.getVehicleNumber()))) {
                this.logger.error("NewSealVehicleResource workitem query--> 传入参数非法" + JsonHelper.toJson(request));
                sealVehicleResponse.setCode(TransWorkItemResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(TransWorkItemResponse.MESSAGE_PARAM_ERROR);
                return sealVehicleResponse;
            }
            TransWorkItemWsDto transWorkItemWsDto = new TransWorkItemWsDto();
            transWorkItemWsDto.setTransWorkItemCode(request.getTransWorkItemCode());
            transWorkItemWsDto.setVehicleNumber(request.getVehicleNumber());
            transWorkItemWsDto.setOperateUserCode(request.getUserErp());
            transWorkItemWsDto.setOperateNodeCode(request.getDmsCode());
            com.jd.tms.tfc.dto.CommonDto<TransWorkItemWsDto> returnCommonDto = newsealVehicleService.getVehicleNumberOrItemCodeByParam(transWorkItemWsDto);
            logger.debug("根据车牌号获取派车明细编码或根据派车明细编码获取车牌号：" + JsonHelper.toJson(returnCommonDto));
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode() && returnCommonDto.getData() != null) {
                    sealVehicleResponse = getVehicleNumBySimpleCode(returnCommonDto.getData().getTransWorkItemCode());
                    this.buildTransWorkItemBySimpleCode(sealVehicleResponse, request.getTransWorkItemCode());
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_OK);
                    sealVehicleResponse.setTransWorkItemCode(returnCommonDto.getData().getTransWorkItemCode());
                    sealVehicleResponse.setVehicleNumber(returnCommonDto.getData().getVehicleNumber());
                } else {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
        } catch (Exception e) {
            this.logger.error("根据车牌号获取派车明细编码或根据派车明细编码获取车牌号-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 根据任务简码和运力资源编码校验运力资源编码并对运力资源编码进行更新
     */
    @POST
    @Path("/new/vehicle/seal/workitem/check")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101104)
    public TransWorkItemResponse checkTransportCode(NewSealVehicleRequest request) {
        TransWorkItemResponse sealVehicleResponse = new TransWorkItemResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null || StringUtils.isEmpty(request.getTransWorkItemCode()) || StringUtils.isEmpty(request.getTransportCode())) {
                this.logger.error("NewSealVehicleResource workitem check --> 传入参数非法" + JsonHelper.toJson(request));
                sealVehicleResponse.setCode(TransWorkItemResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(TransWorkItemResponse.MESSAGE_PARAM_ERROR);
                return sealVehicleResponse;
            }

            com.jd.tms.tfc.dto.CommonDto<String> returnCommonDto = newsealVehicleService.checkTransportCode(request.getTransWorkItemCode(), request.getTransportCode());
            logger.debug("根据任务简码和运力资源编码校验运力资源编码并对运力资源编码进行更新：" + JsonHelper.toJson(returnCommonDto));
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
            this.logger.error("根据任务简码和运力资源编码校验运力资源编码并对运力资源编码进行更新-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 检查运力编码和批次号目的地是否一致
     */
    @GET
    @Path("/new/vehicle/seal/check/{transportCode}/{batchCode}")
    public NewSealVehicleResponse checkTranCodeAndBatchCode(
            @PathParam("transportCode") String transportCode, @PathParam("batchCode") String batchCode) {
        return newCheckTranCodeAndBatchCode(transportCode, batchCode, Constants.SEAL_TYPE_TRANSPORT);
    }

    /**
     * 检查运力编码和批次号目的地是否一致
     */
    @GET
    @Path("/new/vehicle/seal/check/{transportCode}/{batchCode}/{sealCarType}")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101103)
    public NewSealVehicleResponse newCheckTranCodeAndBatchCode(
            @PathParam("transportCode") String transportCode, @PathParam("batchCode") String batchCode, @PathParam("sealCarType") Integer sealCarType) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            //1.检查批次号
            checkBatchCode(sealVehicleResponse, batchCode);
            //批次号校验通过,且是按运力编码封车，需要校验目的地是否一致
            if (Constants.SEAL_TYPE_TRANSPORT.equals(sealCarType) && JdResponse.CODE_OK.equals(sealVehicleResponse.getCode())) {
                //2.获取运力信息并检查目的站点
                com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> vtsDto = newsealVehicleService.getTransportResourceByTransCode(transportCode);
                if (vtsDto == null) {    //JSF接口返回空
                    sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                    sealVehicleResponse.setMessage("查询运力信息结果为空:" + transportCode);
                    return sealVehicleResponse;
                }
                if (Constants.RESULT_SUCCESS == vtsDto.getCode()) { //JSF接口调用成功
                    if (SerialRuleUtil.getReceiveSiteCodeFromSendCode(batchCode).equals(vtsDto.getData().getEndNodeId())) {  // 目标站点一致
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
                    logger.error("查询运力信息出错,出错原因:" + vtsDto.getMessage());
                    logger.error("查询运力信息出错,运力编码:" + transportCode);
                }
            }
        } catch (Exception e) {
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            this.logger.error("封车检查运力编码和批次号目的地是否一致出错：批次号->" + batchCode + "运力编码->" + transportCode, e);
        }
        return sealVehicleResponse;
    }


    /**
     * 校验车牌号能否封车创建车次任务
     */
    @GET
    @Path("/new/vehicle/seal/verifyVehicleJobByVehicleNumber/{transportCode}/{vehicleNumber}/{sealCarType}")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB,bizType = 1011,operateType = 101103)
    public NewSealVehicleResponse verifyVehicleJobByVehicleNumber(
            @PathParam("transportCode") String transportCode, @PathParam("vehicleNumber") String vehicleNumber, @PathParam("sealCarType") Integer sealCarType) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            //按运力封车，需要校验车牌号能否生成车次任务
            if (Constants.SEAL_TYPE_TRANSPORT.equals(sealCarType)) {
                CommonDto<String> dto = newsealVehicleService.verifyVehicleJobByVehicleNumber(transportCode,vehicleNumber);
                if (dto == null) {    //JSF接口返回空
                    logger.warn("校验车牌号能否封车创建车次任务返回值为空:" + transportCode + "-" + vehicleNumber + "-" + sealCarType);
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
            this.logger.error("校验车牌号能否封车创建车次任务异常.运力编码:" + transportCode + ",车牌号:" + vehicleNumber, e);
        }
        return sealVehicleResponse;
    }

    /**
     * 封车功能
     */
    @POST
    @Path("/new/vehicle/seal")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1011)
    public NewSealVehicleResponse seal(NewSealVehicleRequest request) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null || request.getData() == null) {
                logger.warn("NewSealVehicleResource seal --> 传入参数非法");
                sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return sealVehicleResponse;
            }

            CommonDto<String> returnCommonDto = newsealVehicleService.seal(request.getData());
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_SEAL_SUCCESS);
                    sealVehicleResponse.setData(returnCommonDto.getData());
                } else {
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
        } catch (Exception e) {
            this.logger.error("NewSealVehicleResource.seal-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * VOS封车业务同时生成车次任务
     */
    @POST
    @Path("/new/vehicle/doSealCarWithVehicleJob")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1011,operateType = 101102)
    public NewSealVehicleResponse doSealCarWithVehicleJob(NewSealVehicleRequest request) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null || request.getData() == null) {
                logger.warn("NewSealVehicleResource doSealCarWithVehicleJob --> 传入参数非法");
                sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return sealVehicleResponse;
            }

            sealVehicleResponse = newsealVehicleService.doSealCarWithVehicleJob(request.getData());
        } catch (Exception e) {
            this.logger.error("NewSealVehicleResource.doSealCarWithVehicleJob-error", e);
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
  public NewSealVehicleResponse cancelSeal(cancelSealRequest request) {
    NewSealVehicleResponse sealVehicleResponse =
        new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
    try
    {
      if (null == request || StringHelper.isEmpty(request.getBatchCode())) {
        logger.warn("NewSealVehicleResource cancelSeal --> 传入参数非法");
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
      this.logger.error("NewSealVehicleResource.cancelSeal-error", e);
    }
      return sealVehicleResponse;
    }

    /**
     * 获取待解封信息
     */
    @POST
    @Path("/new/vehicle/findSealInfo")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101202)
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
            //查询7天内的带解任务
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, ROLL_BACK_DAY);
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

            logger.info("解封车查询参数，sealCarDto：" + JsonHelper.toJson(sealCarDto));
            logger.info("解封车查询参数，pageDto：" + JsonHelper.toJson(pageDto));

            CommonDto<PageDto<SealCarDto>> returnCommonDto = newsealVehicleService.findSealInfo(sealCarDto, pageDto);

            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                    List<SealCarDto> sealCarDtos = returnCommonDto.getData().getResult();
                    if (sealCarDtos != null && sealCarDtos.size() > 0) {    //合并批次号并按创建时间倒序排序
                        sealCarDtos = mergeBatchCode(sealCarDtos);
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
            this.logger.error("NewSealVehicleResource.findSealInfo-error", e);
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
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 101201)
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
            this.logger.error("NewSealVehicleResource.unsealCheck-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 解封车功能
     */
    @POST
    @Path("/new/vehicle/unseal")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1012)
    public NewSealVehicleResponse unseal(NewSealVehicleRequest request) {
        NewSealVehicleResponse<String> sealVehicleResponse = new NewSealVehicleResponse<String>(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null || request.getData() == null) {
                logger.warn("NewSealVehicleResource unseal --> 传入参数非法");
                sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                sealVehicleResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                return sealVehicleResponse;
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
            this.logger.error("NewSealVehicleResource.unseal-error", e);
        }
        return sealVehicleResponse;
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
    private void checkBatchCode(NewSealVehicleResponse sealVehicleResponse, String batchCode) {
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(batchCode);//获取批次号目的地
        //1.批次号是否符合编码规范，不合规范直接返回参数错误
        if (receiveSiteCode == null) {
            sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_BATCHCODE_PARAM_ERROR);
            return;
        }
        //2.是否已经封车
        CommonDto<Boolean> isSealed = newsealVehicleService.isBatchCodeHasSealed(batchCode);
        if (isSealed == null) {
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage("服务异常，运输系统查询批次号状态结果为空！");
            logger.error("服务异常，运输系统查询批次号状态结果为空, 批次号:" + batchCode);
            return;
        }

        if (Constants.RESULT_SUCCESS == isSealed.getCode()) {//服务正常
            if (Boolean.TRUE.equals(isSealed.getData())) {//已被封车
                sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_BATCHCODE_SEALED_ERROR);
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
            logger.error("服务异常，运输系统查询批次号状态失败, 批次号:" + batchCode);
            logger.error("服务异常，运输系统查询批次号状态失败，失败原因:" + isSealed.getMessage());
        }
        //3.批次号是否存在（最后查询批次号是否存在，不存在时给前台提示）
        if (JdResponse.CODE_OK.equals(sealVehicleResponse.getCode()) && !newsealVehicleService.checkSendIsExist(batchCode)) {//批次号不存在
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
     * ScheduleType=1 是卡班调度模式
     */
    private static final Integer SCHEDULE_TYPE_KA_BAN = 1;

    /**
     * 根据派车任务明细简码获取派车任务明细
     *
     * @param transWorkItemCode
     * @return
     */
    private void buildTransWorkItemBySimpleCode(TransWorkItemResponse sealVehicleResponse, String transWorkItemCode) {
        try {
            TransWorkItemDto item = tmsTfcWSManager.queryTransWorkItemBySimpleCode(transWorkItemCode);
            if (item != null) {
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
            logger.error("[调用TMS-TFC-JSF接口]根据派车任务明细简码获取派车任务明细时发生异常", e);
        }
    }
}
