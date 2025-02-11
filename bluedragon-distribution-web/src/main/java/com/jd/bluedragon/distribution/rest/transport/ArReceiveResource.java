package com.jd.bluedragon.distribution.rest.transport;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.receive.service.ArReceiveService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.transport.domain.*;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.domain.ListResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xumei3 on 2017/12/29.
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ArReceiveResource {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ArSendRegisterService arSendRegisterService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private ArReceiveService arReceiveService;

    @Autowired
    private ArSendCodeService arSendCodeService;

    @Autowired
    private SendDatailDao sendDetailDao;

    /**
     * 空铁项目查找24小时内的待提货信息
     *
     * @param request
     * @return
     */
    @POST
    @Path("/arreceive/getARWaitReceive")
    @JProfiler(jKey = "DMS.WEB.ArReceiveResource.getARWaitReceive", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ListResponse<ArWaitReceive> getARWaitReceive(ArWaitReceiveRequest request) {
        //参数校验：始发城市id、操作人所属站点id必须
        //航空单号、运力名称非必须
        ListResponse<ArWaitReceive> result = new ListResponse<ArWaitReceive>();
        if (request == null) {
            result.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_ERROR);
            result.setMessage(com.jd.ql.dms.common.domain.JdResponse.MESSAGE_ERROR + ":参数为空！");
        } else if (request.getSiteCode() == null || request.getSiteCode() == 0) {
            result.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_ERROR);
            result.setMessage(com.jd.ql.dms.common.domain.JdResponse.MESSAGE_ERROR + ":参数-siteCode为0！");
        } else if (request.getStartCityId() == null) {
            result.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_ERROR);
            result.setMessage(com.jd.ql.dms.common.domain.JdResponse.MESSAGE_ERROR + ":参数-startCityId为0！");
        } else {
            //根据操作人所属站点获取目的城市
            int endCityId = getCityIdBySiteCod(request.getSiteCode());

            //组装查询参数
            ArSendRegister arSendRegister = new ArSendRegister();
            //如果始发城市的id为-1,表示需要匹配所有的始发城市
            if (request.getStartCityId() != -1) {
                arSendRegister.setStartCityId(request.getStartCityId());
            }
            //如果目的城市的id为-1,表示调用基础资料接口获取的分拣中心所在城市出错,则匹配所有目的城市
            if (endCityId != -1) {
                arSendRegister.setEndCityId(endCityId);
            }
            if (StringHelper.isNotEmpty(request.getOrderNo())) {
                arSendRegister.setOrderCode(request.getOrderNo());
            }
            if (StringHelper.isNotEmpty(request.getTransportName())) {
                arSendRegister.setTransportName(request.getTransportName());
            }
            log.info("空铁项目待提货查询参数,始发城市id:{},目的城市id:{},航空单号:{},运力名称:{}"
                    ,request.getStartCityId(),endCityId,request.getOrderNo(), request.getTransportName());

            try {
                //从发货登记表中查找待提货信息
                List<ArSendRegister> arSendRegisters = arSendRegisterService.queryWaitReceive(arSendRegister);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (ArSendRegister arSendRegister1 : arSendRegisters) {
                    result.add(new ArWaitReceive(arSendRegister1.getOrderCode(), arSendRegister1.getTransportName(),
                            arSendRegister1.getStartStationName(), arSendRegister1.getEndStationName(),
                            formatter.format(arSendRegister1.getPlanStartTime()).toString(),
                            formatter.format(arSendRegister1.getPlanEndTime()).toString()));
                }
            } catch (Exception e) {
                log.error("空铁项目待提货查询失败",e);
                result.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_ERROR);
                result.setMessage(com.jd.ql.dms.common.domain.JdResponse.MESSAGE_ERROR + ":请联系研发人员");
            }

        }
        return result;
    }

    public JdResponse<List<ArSendRegister>> getArSendRegisterByTransInfo(Integer transType, String transName, String siteOrder, Date sendDate) {
        JdResponse<List<ArSendRegister>> response = new JdResponse<List<ArSendRegister>>();
        if (StringUtils.isEmpty(transName)) {
            response.toFail("运力名称不能为null或空字符串");
        }

        if (sendDate == null) {
            response.toFail("发货日期不能为空");
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.ArReceiveResource.getArSendRegisterByTransInfo", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            //只支持航空单查询
            if (transType.equals(ArTransportTypeEnum.AIR_TRANSPORT.getCode()) && siteOrder == null) {
                List<ArSendRegister> sendRegisterListToRouter = new ArrayList<ArSendRegister>();
                //根据入参查询发货登记记录列表
                List<ArSendRegister> sendRegisterList = arSendRegisterService.getAirListByTransParam(transName, sendDate);
                if (sendRegisterList != null && !sendRegisterList.isEmpty()) {
                    for (ArSendRegister sendRegister : sendRegisterList) {
                        //根据每一个发货登记表查询批次号列表---一对多的关系
                        List<ArSendCode> sendCodes = arSendCodeService.getBySendRegisterId(sendRegister.getId());
                        if (sendCodes != null && !sendCodes.isEmpty()) {
                            for (ArSendCode arSendCode : sendCodes) {
                                //根据每一个批次查询运单
                                List<SendDetail> sendDetailList = sendDetailDao.queryWaybillsBySendCode(arSendCode.getSendCode());
                                if (null != sendDetailList && sendDetailList.size() > 0) {
                                    for (SendDetail sendDetail : sendDetailList) {
                                        /* 运单号 */
                                        sendRegister.setWaybillCode(sendDetail.getWaybillCode());
                                        /* 包裹号 */
                                        sendRegister.setPackageCode(sendDetail.getPackageBarcode());
                                        /* 批次号 */
                                        sendRegister.setSendCode(arSendCode.getSendCode());

                                        sendRegisterListToRouter.add(sendRegister);
                                    }
                                } else {
                                    log.warn("空铁JSF接口---根据批次号获取发货明细为空，批次号：{}", arSendCode);
                                }
                            }
                        } else {
                            log.warn("空铁JSF接口---根据发货登记id获取批次号列表为空，发货登记id：{}", sendRegister.getId());
                        }
                    }
                } else {
                    log.warn("空铁JSF接口---根据入参获取发货登记列表明细为空");
                }
                response.toSucceed();
                response.setData(sendRegisterListToRouter);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("获取发货登记信息和批次信息时发生异常", e);
            response.toError("获取发货登记信息和批次信息时发生异常");
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return response;
    }

    public JdResponse<List<ArSendRegister>> getArSendRegisterListByParam(Integer transType, String transName, String creTransbillCode, Date sendDate) {
        JdResponse<List<ArSendRegister>> response = new JdResponse<List<ArSendRegister>>();
        if (transType == null){
            response.toFail("运输类型不能为空");
            return response;
        }

        try {
            List<ArSendRegister> sendRegisterList;
            if (ArTransportTypeEnum.AIR_TRANSPORT.getCode() == transType) {
                if (StringUtils.isEmpty(transName)) {
                    response.toFail("查询航空类型时，运力名称不能为null或空字符串");
                }
                if (sendDate == null) {
                    response.toFail("查询航空类型时，发货日期不能为空");
                }
                sendRegisterList = arSendRegisterService.getAirListByTransParam(transName, sendDate);
            } else {
                if (StringUtils.isEmpty(creTransbillCode)) {
                    response.toFail("查询铁路类型时，中铁运单号不能为null或空字符串");
                }
               sendRegisterList = arSendRegisterService.getRailwayListByTransParam(creTransbillCode);
            }
            if (sendRegisterList != null && !sendRegisterList.isEmpty()) {
                for (ArSendRegister sendRegister : sendRegisterList) {
                    List<ArSendCode> sendCodes = arSendCodeService.getBySendRegisterId(sendRegister.getId());
                    if (sendCodes != null && !sendCodes.isEmpty()) {
                        List<String> sendCodeStrList = new ArrayList<String>(sendCodes.size());
                        for (ArSendCode arSendCode : sendCodes) {
                            sendCodeStrList.add(arSendCode.getSendCode());
                        }
                        sendRegister.setSendCodes(sendCodeStrList);
                    }
                }
            }
            response.toSucceed();
            response.setData(sendRegisterList);
        } catch (Exception e) {
            log.error("获取发货登记信息和批次信息时发生异常", e);
            response.toError("获取发货登记信息和批次信息时发生异常");
        }
        return response;
    }


    /**
     * 根据分拣中心id调用基础资料接口获取所在的城市
     * 如果是直辖市（北京、上海、天津、重庆）则将其cityId赋值为provinceId
     * （这么做是为了与运输保持一致）
     *
     * @param siteCode
     * @return
     */
    private int getCityIdBySiteCod(int siteCode) {
        int cityId = -1;
        try {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService.queryDmsBaseSiteByCode(siteCode + "");
            //判断是否是北京(1)、上海(2)、天津(3)、重庆(4)这4个直辖市
            if (baseStaffSiteOrgDto != null){
                if (baseStaffSiteOrgDto.getProvinceId() == 1 || baseStaffSiteOrgDto.getProvinceId() == 2
                                || baseStaffSiteOrgDto.getProvinceId() == 3 || baseStaffSiteOrgDto.getProvinceId() == 4) {
                    cityId = baseStaffSiteOrgDto.getProvinceId();
                } else {
                    cityId = baseStaffSiteOrgDto.getCityId();
                }
            }
        } catch (Exception e) {
            log.error("中心服务调用基础资料getDmsBaseSiteByCode出错 siteCode={}", siteCode, e);
        }
        return cityId;
    }

    /**
     * 空铁提货-根据扫描包裹号/箱号获取空铁登记信息
     *
     * @param barcode
     * @return
     */
    @POST
    @GET
    @Path("/arReceive/getArSendRegisterByBarcode/{barcode}")
    @JProfiler(jKey = "DMS.WEB.ArReceiveResource.getArSendRegisterByBarcode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse<ArSendRegister> getArSendRegisterByBarcode(@PathParam("barcode") String barcode) {
        JdResponse<ArSendRegister> rest = new JdResponse<ArSendRegister>();
        if (StringHelper.isEmpty(barcode)) {
            rest.toFail("包裹号/箱号不能为空！");
            return rest;
        }

        if (BusinessHelper.isBoxcode(barcode) && WaybillUtil.isPackageCode(barcode)) {
            rest.toFail("无法识别的包裹号/箱号！");
            return rest;
        }

        ArSendCode arSendCode = arReceiveService.getLastArSendCodeByBarcode(barcode);
        if (arSendCode != null) {
            rest.setData(arSendRegisterService.findById(arSendCode.getSendRegisterId()));
        } else {
            //不再作为强制校验
            log.warn("根据包裹号/箱号获取空铁登记信息，不存在或未操作空铁发货登记.{}", barcode);
        }

        return rest;
    }

    public JdResponse<Integer> queryArReceiveCountForWorking(ArReceiveCondition request) {
        JdResponse<Integer> rest = new JdResponse<>();
        log.info("计提查询空铁提货count参数：{}", JsonHelper.toJsonMs(request));
        rest.setData(arReceiveService.queryArReceiveCountForWorking(request));
        log.info("计提查询空铁提货count返回值：{}", JsonHelper.toJsonMs(rest));
        return rest;
    }

    public JdResponse<List<ArReceiveVo>> queryArReceiveDetailForWorking(ArReceiveCondition request) {
        JdResponse<List<ArReceiveVo>> rest = new JdResponse<>();
        log.info("计提查询空铁提货detail参数：{}", JsonHelper.toJsonMs(request));
        rest.setData(arReceiveService.queryArReceiveDetailForWorking(request));
        log.info("计提查询空铁提货detail返回数据量：{}", rest.getData() == null ? 0 : rest.getData().size());
        return rest;
    }

}
