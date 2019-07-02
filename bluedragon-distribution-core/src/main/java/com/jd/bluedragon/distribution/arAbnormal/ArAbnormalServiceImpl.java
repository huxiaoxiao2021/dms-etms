package com.jd.bluedragon.distribution.arAbnormal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:41分
 */
@Service("arAbnormalService")
public class ArAbnormalServiceImpl implements ArAbnormalService {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    @Qualifier("arAbnormalProducer")
    private DefaultJMQProducer arAbnormalProducer;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private SendDatailDao sendDatailDao;

    public ArAbnormalResponse pushArAbnormal(ArAbnormalRequest arAbnormalRequest) {
        ArAbnormalResponse response = validateRequest(arAbnormalRequest);
        //发mq
        if (ArAbnormalResponse.CODE_OK.equals(response.getCode())) {
            try {
                arAbnormalProducer.send(arAbnormalRequest.getPackageCode(), JsonHelper.toJson(arAbnormalRequest));
            } catch (JMQException e) {
                logger.error("ArAbnormalServiceImpl.pushArAbnormal推送消息错误" + JsonHelper.toJson(arAbnormalRequest), e);
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
        if (arAbnormalRequest.getTranspondReason() == null) {
            response.setCode(ArAbnormalResponse.CODE_TRANSPONDREASON);
            response.setMessage(ArAbnormalResponse.MESSAGE_TRANSPONDREASON);
            return response;
        }
        if (arAbnormalRequest.getTranspondType() == null) {
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
                logger.warn("ArAbnormalServiceImpl.pushArAbnormal时间格式错误" + JsonHelper.toJson(arAbnormalRequest), e);
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
    public void dealArAbnormal(ArAbnormalRequest arAbnormalRequest) {
        BdTraceDto bdTraceDto = new BdTraceDto();
        toWaybillStatus(arAbnormalRequest, bdTraceDto);
        if (BusinessHelper.isSendCode(arAbnormalRequest.getPackageCode())) {
            //按批次查出发货明细
            List<SendDetail> sendDetailList = sendDatailDao.queryWaybillsBySendCode(arAbnormalRequest.getPackageCode());
            if (null != sendDetailList && sendDetailList.size() > 0) {
                sendTraceForSendDetails(bdTraceDto, sendDetailList);
            } else {
                logger.error("ArAbnormalServiceImpl.dealArAbnormal批次没有发货明细" + JsonHelper.toJson(arAbnormalRequest));
                ArAbnormalResponse response = new ArAbnormalResponse();
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("批次没有发货明细");
                addBusinessLog(arAbnormalRequest, response);
            }
        } else if (BusinessUtil.isBoxcode(arAbnormalRequest.getPackageCode())) {
            //按箱号 和站点 查发货明细
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setBoxCode(arAbnormalRequest.getPackageCode());
            tSendDatail.setCreateSiteCode(arAbnormalRequest.getSiteCode());
            List<SendDetail> sendDetailList = sendDatailDao.querySendDatailsByBoxCode(tSendDatail);
            if (null != sendDetailList && sendDetailList.size() > 0) {
                sendTraceForSendDetails(bdTraceDto, sendDetailList);
            } else {
                logger.error("ArAbnormalServiceImpl.dealArAbnormal箱号没有发货明细" + JsonHelper.toJson(arAbnormalRequest));
                ArAbnormalResponse response = new ArAbnormalResponse();
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("箱号没有发货明细");
                addBusinessLog(arAbnormalRequest, response);
            }
        } else if (WaybillUtil.isWaybillCode(arAbnormalRequest.getPackageCode())) {
            WChoice choice = new WChoice();
            choice.setQueryWaybillC(true);
            choice.setQueryPackList(true);
            BaseEntity<BigWaybillDto> waybillDtoBaseEntity = waybillQueryManager.getDataByChoice(arAbnormalRequest.getPackageCode(), choice);
            if (waybillDtoBaseEntity == null || waybillDtoBaseEntity.getData() == null || waybillDtoBaseEntity.getData().getWaybill() == null) {
                logger.error("ArAbnormalServiceImpl.dealArAbnormal运单不存在" + JsonHelper.toJson(arAbnormalRequest));
                ArAbnormalResponse response = new ArAbnormalResponse();
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("运单不存在");
                addBusinessLog(arAbnormalRequest, response);
                return;
            }
            List<DeliveryPackageD>  packlist= waybillDtoBaseEntity.getData().getPackageList();
            if (packlist==null||packlist.size()==0){
                logger.error("ArAbnormalServiceImpl.dealArAbnormal没有包裹明细" + JsonHelper.toJson(arAbnormalRequest));
                ArAbnormalResponse response = new ArAbnormalResponse();
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("运单没有包裹明细");
                addBusinessLog(arAbnormalRequest, response);
                return;
            }
            for (DeliveryPackageD deliveryPackageD : packlist) {
                bdTraceDto.setWaybillCode(arAbnormalRequest.getPackageCode());
                bdTraceDto.setPackageBarCode(deliveryPackageD.getPackageBarcode());
                waybillQueryManager.sendBdTrace(bdTraceDto);
            }

        } else if (WaybillUtil.isPackageCode(arAbnormalRequest.getPackageCode())) {
            WChoice choice = new WChoice();
            choice.setQueryWaybillC(true);
            BaseEntity<BigWaybillDto> waybillDtoBaseEntity = waybillQueryManager.getDataByChoice(WaybillUtil.getWaybillCode(arAbnormalRequest.getPackageCode()), choice);
            if (waybillDtoBaseEntity == null || waybillDtoBaseEntity.getData() == null || waybillDtoBaseEntity.getData().getWaybill() == null) {
                logger.error("ArAbnormalServiceImpl.dealArAbnormal运单不存在" + JsonHelper.toJson(arAbnormalRequest));
                ArAbnormalResponse response = new ArAbnormalResponse();
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("运单不存在。");
                addBusinessLog(arAbnormalRequest, response);
                return;
            }
            bdTraceDto.setWaybillCode(WaybillUtil.getWaybillCode(arAbnormalRequest.getPackageCode()));
            bdTraceDto.setPackageBarCode(arAbnormalRequest.getPackageCode());
            waybillQueryManager.sendBdTrace(bdTraceDto);
        } else {
            logger.error("ArAbnormalServiceImpl.dealArAbnormal无可用扫描码" + JsonHelper.toJson(arAbnormalRequest));
            ArAbnormalResponse response = new ArAbnormalResponse();
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage("无可用扫描码");
            addBusinessLog(arAbnormalRequest, response);
        }
    }

    /**
     * 发全程跟踪
     *
     * @param bdTraceDto
     * @param sendDetailList
     */
    private void sendTraceForSendDetails(BdTraceDto bdTraceDto, List<SendDetail> sendDetailList) {
        for (SendDetail sendDetail : sendDetailList) {
            bdTraceDto.setWaybillCode(sendDetail.getWaybillCode());
            bdTraceDto.setPackageBarCode(sendDetail.getPackageBarcode());
            waybillQueryManager.sendBdTrace(bdTraceDto);
        }
    }

    /**
     * 封装全程跟踪对象
     *
     * @param arAbnormalRequest
     * @param bdTraceDto
     */
    private void toWaybillStatus(ArAbnormalRequest arAbnormalRequest, BdTraceDto bdTraceDto) {
        bdTraceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_ARQC);
        bdTraceDto.setOperatorSiteId(arAbnormalRequest.getSiteCode());
        bdTraceDto.setOperatorSiteName(arAbnormalRequest.getSiteName());
        bdTraceDto.setOperatorTime(DateHelper.parseAllFormatDateTime(arAbnormalRequest.getOperateTime()));
        bdTraceDto.setOperatorUserName(arAbnormalRequest.getUserName());
        bdTraceDto.setOperatorUserId(arAbnormalRequest.getUserCode());
        bdTraceDto.setOperatorDesp(converTranspondReason(arAbnormalRequest.getTranspondReason()) + ArAbnormalRequest.SEPARATOR_COMMA + converTranspondType(arAbnormalRequest.getTranspondType()));
    }

    /**
     * 原因  代码和描述互转
     *
     * @param transpondReason
     * @return
     */
    private String converTranspondReason(Integer transpondReason) {
        if (ArAbnormalRequest.TRANSPONDREASON10_CODE.equals(transpondReason)) {
            return ArAbnormalRequest.TRANSPONDREASON10_DESC;
        } else if (ArAbnormalRequest.TRANSPONDREASON20_CODE.equals(transpondReason)) {
            return ArAbnormalRequest.TRANSPONDREASON20_DESC;
        } else if (ArAbnormalRequest.TRANSPONDREASON30_CODE.equals(transpondReason)) {
            return ArAbnormalRequest.TRANSPONDREASON30_DESC;
        } else if (ArAbnormalRequest.TRANSPONDREASON40_CODE.equals(transpondReason)) {
            return ArAbnormalRequest.TRANSPONDREASON40_DESC;
        } else {
            return transpondReason.toString();
        }
    }

    /**
     * 类型  代码和描述互转
     *
     * @param transpondType
     * @return
     */
    private String converTranspondType(Integer transpondType) {
        if (ArAbnormalRequest.TRANSPONDTYPE10_CODE.equals(transpondType)) {
            return ArAbnormalRequest.TRANSPONDTYPE10_DESC;
        } else if (ArAbnormalRequest.TRANSPONDTYPE20_CODE.equals(transpondType)) {
            return ArAbnormalRequest.TRANSPONDTYPE20_DESC;
        } else if (ArAbnormalRequest.TRANSPONDTYPE30_CODE.equals(transpondType)) {
            return ArAbnormalRequest.TRANSPONDTYPE30_DESC;
        } else if (ArAbnormalRequest.TRANSPONDTYPE40_CODE.equals(transpondType)){
            return ArAbnormalRequest.TRANSPONDTYPE40_DESC;
        } else if (ArAbnormalRequest.TRANSPONDTYPE50_CODE.equals(transpondType)) {
            return ArAbnormalRequest.TRANSPONDTYPE50_DESC;
        } else {
            return transpondType.toString();
        }
    }

    /**
     * 插入pda操作日志表
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
}
