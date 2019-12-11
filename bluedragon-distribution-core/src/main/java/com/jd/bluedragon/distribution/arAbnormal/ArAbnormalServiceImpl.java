package com.jd.bluedragon.distribution.arAbnormal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.EclpItemManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.request.ArTransportModeChangeDto;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.transport.domain.ArAbnormalReasonEnum;
import com.jd.bluedragon.distribution.transport.domain.ArContrabandReasonEnum;
import com.jd.bluedragon.distribution.transport.domain.ArTransportChangeModeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.domain.AreaNode;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.*;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.kom.ext.service.domain.response.ItemInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:41分
 */
@Service("arAbnormalService")
public class ArAbnormalServiceImpl implements ArAbnormalService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("arAbnormalProducer")
    private DefaultJMQProducer arAbnormalProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private EclpItemManager eclpItemManager;

    @Autowired
    @Qualifier("arTransportModeChangeProducer")
    private DefaultJMQProducer arTransportModeChangeProducer;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public ArAbnormalResponse pushArAbnormal(ArAbnormalRequest arAbnormalRequest) {
        ArAbnormalResponse response = validateRequest(arAbnormalRequest);
        //发mq
        if (ArAbnormalResponse.CODE_OK.equals(response.getCode())) {
            try {
                arAbnormalProducer.send(arAbnormalRequest.getPackageCode(), JsonHelper.toJson(arAbnormalRequest));
            } catch (JMQException e) {
                log.error("ArAbnormalServiceImpl.pushArAbnormal推送消息错误：{}" , JsonHelper.toJson(arAbnormalRequest), e);
                response.setCode(ArAbnormalResponse.CODE_SERVICE_ERROR);
                response.setMessage(ArAbnormalResponse.MESSAGE_SERVICE_ERROR);
                addBusinessLog(arAbnormalRequest, response);
                return response;
            }
        }
        addBusinessLog(arAbnormalRequest, response);
        return response;
    }

    /**
     * 校验参数
     *
     * @param arAbnormalRequest
     * @return
     */
    private ArAbnormalResponse validateRequest(ArAbnormalRequest arAbnormalRequest) {
        ArAbnormalResponse response = new ArAbnormalResponse();
        if (StringUtils.isBlank(arAbnormalRequest.getPackageCode())) {
            response.setCode(ArAbnormalResponse.CODE_NODATA);
            response.setMessage(ArAbnormalResponse.MESSAGE_NODATA);
            return response;
        }
        arAbnormalRequest.setPackageCode(arAbnormalRequest.getPackageCode().trim());
        if (!WaybillUtil.isPackageCode(arAbnormalRequest.getPackageCode()) && !WaybillUtil.isWaybillCode(arAbnormalRequest.getPackageCode())
                && !BusinessUtil.isBoxcode(arAbnormalRequest.getPackageCode()) && !BusinessHelper.isSendCode(arAbnormalRequest.getPackageCode())) {
            response.setCode(ArAbnormalResponse.CODE_ERRORDATA);
            response.setMessage(ArAbnormalResponse.MESSAGE_ERRORDATA);
            return response;
        }
        if (ArAbnormalReasonEnum.getEnum(arAbnormalRequest.getTranspondReason()) == null) {
            response.setCode(ArAbnormalResponse.CODE_TRANSPONDREASON);
            response.setMessage(ArAbnormalResponse.MESSAGE_TRANSPONDREASON);
            return response;
        }
        if (ArTransportChangeModeEnum.getEnum(arAbnormalRequest.getTranspondType()) == null) {
            response.setCode(ArAbnormalResponse.CODE_TRANSPONDTYPE);
            response.setMessage(ArAbnormalResponse.MESSAGE_TRANSPONDTYPE);
            return response;
        }
        if (arAbnormalRequest.getSiteCode() == null) {
            response.setCode(ArAbnormalResponse.CODE_SITECODE);
            response.setMessage(ArAbnormalResponse.MESSAGE_SITECODE);
            return response;
        }
        if (StringUtils.isBlank(arAbnormalRequest.getSiteName())) {
            response.setCode(ArAbnormalResponse.CODE_SITENAME);
            response.setMessage(ArAbnormalResponse.MESSAGE_SITENAME);
            return response;
        }
        if (arAbnormalRequest.getUserCode() == null) {
            response.setCode(ArAbnormalResponse.CODE_USERCODE);
            response.setMessage(ArAbnormalResponse.MESSAGE_USERCODE);
            return response;
        }
        if (StringUtils.isBlank(arAbnormalRequest.getUserName())) {
            response.setCode(ArAbnormalResponse.CODE_USERNAME);
            response.setMessage(ArAbnormalResponse.MESSAGE_USERNAME);
            return response;
        }
        if (arAbnormalRequest.getOperateTime() != null) {
            try {
                DateHelper.parseAllFormatDateTime(arAbnormalRequest.getOperateTime());
            } catch (Exception e) {
                log.warn("ArAbnormalServiceImpl.pushArAbnormal时间格式错误:{}" , JsonHelper.toJson(arAbnormalRequest), e);
                response.setCode(ArAbnormalResponse.CODE_TIMEERROR);
                response.setMessage(ArAbnormalResponse.MESSAGE_TIMEERROR);
                return response;
            }
        } else {
            response.setCode(ArAbnormalResponse.CODE_TIME);
            response.setMessage(ArAbnormalResponse.MESSAGE_TIME);
            return response;
        }
        response.setCode(ArAbnormalResponse.CODE_OK);
        response.setMessage(ArAbnormalResponse.MESSAGE_OK);
        return response;
    }

    /**
     * 提报异常逻辑
     *
     * @param arAbnormalRequest
     */
    @JProfiler(jKey = "ArAbnormalServiceImpl.dealArAbnormal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void dealArAbnormal(ArAbnormalRequest arAbnormalRequest) throws JMQException {
        // 根据提报条码获取对应的运单和包裹信息
        Map<String, List<String>> waybillMap = getWaybillMapByBarCode(arAbnormalRequest);
        if (waybillMap != null) {
            // 发送全程跟踪
            this.doSendTrace(arAbnormalRequest, waybillMap);
            // 判断是否为新客户端请求
            if (isNewClientRequest(arAbnormalRequest)) {
                // 异常原因只有航空违禁品时，才发送MQ消息
                if (ArAbnormalReasonEnum.getEnum(arAbnormalRequest.getTranspondReason()) != ArAbnormalReasonEnum.CONTRABAND_GOODS) {
                    return;
                }
                // 第一期只做航空转陆运，其他运输类型不发送MQ消息
                if (ArTransportChangeModeEnum.getEnum(arAbnormalRequest.getTranspondType()) != ArTransportChangeModeEnum.AIR_TO_ROAD_CODE) {
                    return;
                }
                // 发MQ消息
                this.doSendMQ(arAbnormalRequest, waybillMap);
            }
        }
    }

    /**
     * 新老版本兼容，判断是否需要发送MQ消息
     * 新版空铁运输方式变更只处理航空转陆运，异常原因只有航空违禁品
     *
     * @param request
     * @return
     */
    private boolean isNewClientRequest(ArAbnormalRequest request) {
        // 新老版本客户端兼容，老客户端违禁品原因字段为空值，所以不需要发送MQ消息
        ArContrabandReasonEnum contrabandReason = ArContrabandReasonEnum.getEnum(request.getContrabandReason());
        if (contrabandReason == null) {
            return false;
        }
        return true;
    }

    /**
     * 根据条码类型 获取该条码下的运单-包裹关系对象
     *
     * @param arAbnormalRequest
     * @return
     */
    private Map<String, List<String>> getWaybillMapByBarCode(ArAbnormalRequest arAbnormalRequest) {
        String barCode = arAbnormalRequest.getPackageCode();
        if (BusinessHelper.isSendCode(barCode)) {
            // 批次号
            return this.getWaybillMapBySendCode(arAbnormalRequest);
        } else if (BusinessUtil.isBoxcode(barCode)) {
            // 箱号
            return this.getWaybillMapByBoxCode(arAbnormalRequest);
        } else if (WaybillUtil.isWaybillCode(barCode)) {
            // 运单号
            return this.getWaybillMapByWaybillCode(barCode);
        } else if (WaybillUtil.isPackageCode(barCode)) {
            // 包裹号
            return this.getWaybillMapByPackageCode(barCode);
        } else {
            log.warn("[空铁 - 运输方式变更]消费处理，无法识别条码，request:{}" , JsonHelper.toJson(arAbnormalRequest));
            return null;
        }
    }

    /**
     * 获取批次下的运单-包裹关系Map
     *
     * @param arAbnormalRequest
     * @return
     */
    private Map<String, List<String>> getWaybillMapBySendCode(ArAbnormalRequest arAbnormalRequest) {
        //按批次查出发货明细
        List<SendDetail> sendDetailList = sendDatailDao.queryWaybillsBySendCode(arAbnormalRequest.getPackageCode());
        if (sendDetailList.size() > 0) {
            return this.assembleBySendDetail(sendDetailList);
        } else {
            this.addErrorBusinessLog(arAbnormalRequest, "根据批次号没有发货明细");
        }
        return null;
    }

    /**
     * 获取箱号下的运单-包裹关系Map
     *
     * @param arAbnormalRequest
     * @return
     */
    private Map<String, List<String>> getWaybillMapByBoxCode(ArAbnormalRequest arAbnormalRequest) {
        //按箱号 和 站点查发货明细
        SendDetail queryParam = new SendDetail();
        queryParam.setBoxCode(arAbnormalRequest.getPackageCode());
        queryParam.setCreateSiteCode(arAbnormalRequest.getSiteCode());
        List<SendDetail> sendDetailList = sendDatailDao.querySendDatailsByBoxCode(queryParam);
        if (sendDetailList.size() > 0) {
            return this.assembleBySendDetail(sendDetailList);
        } else {
            this.addErrorBusinessLog(arAbnormalRequest, "箱号没有发货明细");
        }
        return null;
    }

    /**
     * 获取运单下的运单-包裹关系Map
     *
     * @param waybillCode
     * @return
     */
    private Map<String, List<String>> getWaybillMapByWaybillCode(String waybillCode) {
        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCode(waybillCode);
        if (baseEntity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode() && baseEntity.getData() != null) {
            List<DeliveryPackageD> packageDList = baseEntity.getData();
            if (packageDList.size() > 0) {
                Map<String, List<String>> resultMap = new HashMap<>(1);
                resultMap.put(waybillCode, this.getPackageCodeStringList(packageDList));
                return resultMap;
            }
        }
        return null;
    }

    private List<String> getPackageCodeStringList(List<DeliveryPackageD> packageDList) {
        List<String> resultList = new ArrayList<>(packageDList.size());
        for (DeliveryPackageD packageD : packageDList) {
            resultList.add(packageD.getPackageBarcode());
        }
        return resultList;
    }

    private Map<String, List<String>> getWaybillMapByPackageCode(String packageCode) {
        Map<String, List<String>> resultMap = new HashMap<>(1);
        List packageList = new ArrayList(1);
        packageList.add(packageCode);
        resultMap.put(WaybillUtil.getWaybillCodeByPackCode(packageCode), packageList);
        return resultMap;
    }

    /**
     * 根据发货明细SendDetail 组装运单包裹关系对象
     *
     * @param sendDetails
     * @return
     */
    private Map<String, List<String>> assembleBySendDetail(List<SendDetail> sendDetails) {
        Map<String, List<String>> resultMap = new HashMap<>();
        for (SendDetail sendDetail : sendDetails) {
            if (resultMap.containsKey(sendDetail.getWaybillCode())) {
                resultMap.get(sendDetail.getWaybillCode()).add(sendDetail.getPackageBarcode());
            } else {
                List packageList = new ArrayList();
                packageList.add(sendDetail.getPackageBarcode());
                resultMap.put(sendDetail.getWaybillCode(), packageList);
            }
        }
        return resultMap;
    }

    /**
     * 组装并发送全程跟踪
     *
     * @param arAbnormalRequest
     * @param waybillMap
     */
    private void doSendTrace(ArAbnormalRequest arAbnormalRequest, Map<String, List<String>> waybillMap) {
        // 组装全程跟踪对象
        BdTraceDto bdTraceDto = assembleBdTraceDto(arAbnormalRequest);
        for (Map.Entry<String, List<String>> entry : waybillMap.entrySet()) {
            // 发送全程跟踪
            this.sendTrace(bdTraceDto, entry.getKey(), entry.getValue());
        }
    }

    /**
     * 封装全程跟踪对象
     *
     * @param arAbnormalRequest
     */
    private BdTraceDto assembleBdTraceDto(ArAbnormalRequest arAbnormalRequest) {
        BdTraceDto bdTraceDto = new BdTraceDto();
        bdTraceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_ARQC);
        bdTraceDto.setOperatorSiteId(arAbnormalRequest.getSiteCode());
        bdTraceDto.setOperatorSiteName(arAbnormalRequest.getSiteName());
        bdTraceDto.setOperatorTime(DateHelper.parseAllFormatDateTime(arAbnormalRequest.getOperateTime()));
        bdTraceDto.setOperatorUserName(arAbnormalRequest.getUserName());
        bdTraceDto.setOperatorUserId(arAbnormalRequest.getUserCode());
        bdTraceDto.setOperatorDesp(this.getOperatorDesc(arAbnormalRequest));
        return bdTraceDto;
    }

    /**
     * 发全程跟踪
     *
     * @param bdTraceDto
     * @param waybillCode
     * @param packageCodeList
     */
    private void sendTrace(BdTraceDto bdTraceDto, String waybillCode, List<String> packageCodeList) {
        for (String packageCode : packageCodeList) {
            bdTraceDto.setWaybillCode(waybillCode);
            bdTraceDto.setPackageBarCode(packageCode);
            waybillQueryManager.sendBdTrace(bdTraceDto);
        }
    }

    private String getOperatorDesc(ArAbnormalRequest arAbnormalRequest) {
        StringBuilder desc = new StringBuilder();
        // 新旧版本客户端兼容问题
        if (isNewClientRequest(arAbnormalRequest)) {
            desc.append(getTransportChangeDesc(arAbnormalRequest.getTranspondType()));
            desc.append(Constants.SEPARATOR_BLANK_SPACE);
            desc.append(getAbnormalReasonDesc(arAbnormalRequest.getTranspondReason()));
            desc.append(Constants.PUNCTUATION_OPEN_BRACKET_SMALL);
            desc.append(getContrabandReasonDesc(arAbnormalRequest.getContrabandReason()));
            desc.append(Constants.PUNCTUATION_CLOSE_BRACKET_SMALL);
        } else {
            desc.append(getAbnormalReasonDesc(arAbnormalRequest.getTranspondReason()));
            desc.append(Constants.SEPARATOR_BLANK_SPACE);
            desc.append(getTransportChangeDesc(arAbnormalRequest.getTranspondType()));
        }
        return desc.toString();
    }

    /**
     * 原因  代码和描述互转
     *
     * @param transpondReason
     * @return
     */
    private String getAbnormalReasonDesc(Integer transpondReason) {
        ArAbnormalReasonEnum reason = ArAbnormalReasonEnum.getEnum(transpondReason);
        if (reason != null) {
            return reason.getDesc();
        }
        return String.valueOf(transpondReason);
    }

    /**
     * 类型  代码和描述互转
     *
     * @param transpondType
     * @return
     */
    private String getTransportChangeDesc(Integer transpondType) {
        ArTransportChangeModeEnum changeMode = ArTransportChangeModeEnum.getEnum(transpondType);
        if (changeMode != null) {
            return changeMode.getDesc();
        }
        return String.valueOf(transpondType);
    }

    /**
     * 违禁品原因描述
     *
     * @param code
     * @return
     */
    private String getContrabandReasonDesc(Integer code) {
        ArContrabandReasonEnum contrabandReason = ArContrabandReasonEnum.getEnum(code);
        if (contrabandReason != null) {
            return contrabandReason.getDesc();
        }
        return String.valueOf(code);
    }

    /**
     * 添加出现异常情况下的PDA业务日志
     *
     * @param arAbnormalRequest
     * @param msg
     */
    private void addErrorBusinessLog(ArAbnormalRequest arAbnormalRequest, String msg) {
        ArAbnormalResponse response = new ArAbnormalResponse();
        response.setCode(JdResponse.CODE_SERVICE_ERROR);
        response.setMessage(msg);
        addBusinessLog(arAbnormalRequest, response);
    }

    /**
     * 插入PDA业务日志表
     *
     * @param arAbnormalRequest
     */
    private void addBusinessLog(ArAbnormalRequest arAbnormalRequest, ArAbnormalResponse arAbnormalResponse) {
        //写入自定义日志
        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB);
        businessLogProfiler.setBizType(Constants.BUSINESS_LOG_BIZ_TYPE_ARABNORMAL);
        businessLogProfiler.setOperateType(Constants.BUSINESS_LOG_OPERATE_TYPE_ARABNORMAL);
        businessLogProfiler.setOperateRequest(JsonHelper.toJson(arAbnormalRequest));
        businessLogProfiler.setOperateResponse(JsonHelper.toJson(arAbnormalResponse));
        businessLogProfiler.setTimeStamp(System.currentTimeMillis());
        BusinessLogWriter.writeLog(businessLogProfiler);
    }

    /**
     * 发送MQ消息
     *
     * @param request
     * @param waybillMap
     */
    private void doSendMQ(ArAbnormalRequest request, Map<String, List<String>> waybillMap) throws JMQException {
        List<Message> messages = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : waybillMap.entrySet()) {
            BigWaybillDto bigWaybillDto = this.getBigWaybillDtoByWaybillCode(entry.getKey());
            if (bigWaybillDto != null && bigWaybillDto.getWaybill() != null) {
                if (isNeedSendMQ(bigWaybillDto.getWaybill().getWaybillSign())) {
                    messages.addAll(this.assembleMessageList(request, bigWaybillDto, entry.getValue()));
                }
            } else {
                this.addErrorBusinessLog(request, "根据运单号：" + entry.getKey() + "获取运单信息为空");
            }
        }
        arTransportModeChangeProducer.batchSend(messages);
    }

    /**
     * 根据waybillSign判断是否需要发消息
     * 【航】字标的运单， waybill_sign第31位等于1 才发消息
     *
     * @param waybillSign
     * @return
     */
    private boolean isNeedSendMQ(String waybillSign) {
        return BusinessUtil.isSignY(waybillSign, 31);
    }

    private BigWaybillDto getBigWaybillDtoByWaybillCode(String waybillCode) {
        WChoice choice = new WChoice();
        choice.setQueryWaybillC(true);
        choice.setQueryGoodList(true);
        choice.setQueryWaybillExtend(true);
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, choice);
        if (baseEntity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode() && baseEntity.getData() != null) {
            return baseEntity.getData();
        }
        return null;
    }

    /**
     * 组装MQ消息体List
     *
     * @param request
     * @param bigWaybillDto
     * @param packageCodeList
     * @return
     */
    private List<Message> assembleMessageList(ArAbnormalRequest request, BigWaybillDto bigWaybillDto, List<String> packageCodeList) {
        Waybill waybill = bigWaybillDto.getWaybill();
        ArTransportModeChangeDto dto = new ArTransportModeChangeDto();
        ArTransportChangeModeEnum transformChangeMode = ArTransportChangeModeEnum.getEnum(request.getTranspondType());
        ArAbnormalReasonEnum abnormalReason = ArAbnormalReasonEnum.getEnum(request.getTranspondReason());
        ArContrabandReasonEnum contrabandReason = ArContrabandReasonEnum.getEnum(request.getContrabandReason());

        // 转发方式 (航空转陆运 或 航空转高铁)
        dto.setTransformType(transformChangeMode.getFxmId());
        dto.setWaybillCode(waybill.getWaybillCode());
        // 异常类型 (航空违禁品)
        dto.setAbnormalType(abnormalReason.getFxmId());
        // 转发方式 (航空转陆运 或 航空转高铁)
        dto.setFirstLevelCode(transformChangeMode.getFxmId());
        dto.setFirstLevelName(transformChangeMode.getDesc());
        // 异常类型 (航空违禁品)
        dto.setSecondLevelCode(abnormalReason.getFxmId());
        dto.setSecondLevelName(abnormalReason.getDesc());
        // 违禁品原因
        dto.setThirdLevel(contrabandReason.getDesc());
        dto.setOperatorErp(request.getUserErp());
        dto.setSiteCode(request.getSiteCode());

        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(request.getSiteCode());
        if (siteOrgDto != null) {
            dto.setSiteName(siteOrgDto.getSiteName());
            dto.setAreaId(siteOrgDto.getOrgId());
            AreaNode areaNode = AreaHelper.getArea(siteOrgDto.getOrgId());
            dto.setAreaName(areaNode == null ? null : areaNode.getName());
        }
        dto.setOperateTime(request.getOperateTime());
        // 青龙业主编码
        dto.setCustomerCode(waybill.getCustomerCode());
        // 青龙寄件人pin码
        dto.setConsignerPin(waybill.getMemberId());
        dto.setConsigner(waybill.getConsigner());
        dto.setConsignerId(waybill.getConsignerId());
        dto.setConsignerMobile(waybill.getConsignerMobile());
        dto.setPickupSiteId(waybill.getPickupSiteId());
        dto.setPickupSiteName(waybill.getPickupSiteName());
        // 商家ID
        dto.setBusiId(waybill.getBusiId());
        // 商家名称
        dto.setBusiName(waybill.getBusiName());
        dto.setConsignmentName(this.getConsignmentNameByWaybillDto(bigWaybillDto));
        List<Message> messageList = new ArrayList<>(packageCodeList.size());
        for (String packageCode : packageCodeList) {
            dto.setPackageCode(packageCode);
            messageList.add(new Message(arTransportModeChangeProducer.getTopic(), JsonHelper.toJson(dto), packageCode));
        }
        return messageList;
    }

    /**
     * 根据运单信息获取托寄物名称
     *
     * @param bigWaybillDto
     * @return
     */
    private String getConsignmentNameByWaybillDto(BigWaybillDto bigWaybillDto) {
        // 1.查询运单商品信息
        String name = this.getConsignmentNameFromGoods(bigWaybillDto.getGoodsList());
        if (name != null) {
            return name;
        }
        // 2.查询ECLP全程跟踪
        name = this.getConsignmentNameFromECLP(bigWaybillDto.getWaybill().getBusiOrderCode());
        if (name != null) {
            return name;
        }
        // 3.查询运单托寄物信息
        name = this.getConsignmentNameFromWaybillExt(bigWaybillDto.getWaybill().getWaybillExt());
        if (name != null) {
            return name;
        }
        return null;
    }

    /**
     * 查询运单商品信息
     *
     * @param goods
     * @return
     */
    private String getConsignmentNameFromGoods(List<Goods> goods) {
        if (goods != null && goods.size() > 0) {
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < goods.size(); i++) {
                //明细内容： 商品编码SKU：商品名称*数量
                name.append(goods.get(i).getSku());
                name.append(":");
                name.append(goods.get(i).getGoodName());
                name.append(" * ");
                name.append(goods.get(i).getGoodCount());
                if (i != goods.size() - 1) {
                    //除了最后一个，其他拼完加个,
                    name.append(",");
                }
            }
            return name.toString();
        }
        return null;
    }

    /***
     * 调用ECLP获取商品信息
     *
     * @param busiOrderCode
     * @return
     */
    private String getConsignmentNameFromECLP(String busiOrderCode) {
        //第二步 查eclp
        //如果运单上没有明细 就判断是不是eclp订单 如果是，调用eclp接口
        if (WaybillUtil.isECLPByBusiOrderCode(busiOrderCode)) {
            StringBuilder name = new StringBuilder();
            List<ItemInfo> itemInfoList = eclpItemManager.getltemBySoNo(busiOrderCode);
            if (itemInfoList != null && itemInfoList.size() > 0) {
                for (int i = 0; i < itemInfoList.size(); i++) {
                    //明细内容： 商品名称*数量 优先取deptRealOutQty，如果该字段为空取realOutstoreQty  eclp负责人宫体雷
                    name.append(itemInfoList.get(i).getGoodsName());
                    name.append(" * ");
                    name.append(itemInfoList.get(i).getDeptRealOutQty() == null ? itemInfoList.get(i).getRealOutstoreQty() : itemInfoList.get(i).getDeptRealOutQty());
                    if (i != itemInfoList.size() - 1) {
                        //除了最后一个，其他拼完加个,
                        name.append(",");
                    }
                }
                return name.toString();
            }
        }
        return null;
    }

    /**
     * 查询运单托寄物信息
     *
     * @param waybillExt
     * @return
     */
    private String getConsignmentNameFromWaybillExt(WaybillExt waybillExt) {
        //第三步 查运单的托寄物
        if (waybillExt != null && StringUtils.isNotEmpty(waybillExt.getConsignWare())) {
            StringBuilder name = new StringBuilder();
            name.append(waybillExt.getConsignWare());
            name.append(waybillExt.getConsignCount() == null ? "" : " * " + waybillExt.getConsignCount());
            return name.toString();
        }
        return null;
    }
}
