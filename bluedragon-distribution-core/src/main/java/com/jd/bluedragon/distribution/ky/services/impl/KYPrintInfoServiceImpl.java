package com.jd.bluedragon.distribution.ky.services.impl;

import com.jd.bluedragon.core.base.EcpAirWSManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.ky.domain.KYPrintInfo;
import com.jd.bluedragon.distribution.ky.services.KYPrintService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.tms.ecp.dto.AirTransportBillSignDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("kyPrintService")
public class KYPrintInfoServiceImpl implements KYPrintService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KYPrintInfoServiceImpl.class);

    @Autowired
    private EcpAirWSManager ecpAirWSManager;

    @Override
    public InvokeResult<KYPrintInfo> getKYPrintInfoBySendCode(String sendCode) {
        InvokeResult<KYPrintInfo> invokeResult = new InvokeResult<>();
        invokeResult.success();

        if (StringHelper.isEmpty(sendCode)) {
            invokeResult.parameterError("批次号无效");
            return invokeResult;
        }

        try {
            InvokeResult<AirTransportBillSignDto> result = ecpAirWSManager.getAirTransportBillSign(sendCode);
            if (result == null  || InvokeResult.RESULT_SUCCESS_CODE != result.getCode() || result.getData() == null) {
                invokeResult.error(result != null? result.getMessage() : "服务异常");
                return invokeResult;
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("获取航空签信息，参数{},返回值：{}", sendCode, JsonHelper.toJson(result));
            }
            AirTransportBillSignDto airTransportBillSignDto = result.getData();
            KYPrintInfo kyPrintInfo = new KYPrintInfo();
            kyPrintInfo.setKyCode(airTransportBillSignDto.getShipperOrderCode());
            kyPrintInfo.setSendCode(airTransportBillSignDto.getBatchCode());
            kyPrintInfo.setReceiveAirPortName(airTransportBillSignDto.getEndNodeName());
            invokeResult.setData(kyPrintInfo);

        } catch (RuntimeException e) {
            LOGGER.error("获取航空签信息发生异常：{}", sendCode,e);
            invokeResult.error(e);
        }

        return invokeResult;
    }
}
