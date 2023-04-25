package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.ql.erp.dto.ResponseDTO;
import com.jd.ql.erp.dto.delivery.DeliveredReqDTO;
import com.jd.ql.erp.jsf.DeliveryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/4/24 11:19
 * @Description:
 */
@Service
public class DeliveryWSManagerImpl implements DeliveryWSManager{

    private final static Logger log = LoggerFactory.getLogger(DeliveryWSManagerImpl.class);


    @Autowired
    @Qualifier("deliveryWSService")
    private DeliveryWS deliveryWSService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DeliveryWSManagerImpl.delivered", mState = {JProEnum.TP, JProEnum.FunctionError})
    public ResponseDTO delivered(DeliveredReqDTO deliveredReqDTO) {
        ResponseDTO responseDTO = null;
        try{
            log.info("DeliveryWSManagerImpl-妥投处理接口 入参-{}", JSON.toJSONString(deliveredReqDTO));
            responseDTO = deliveryWSService.delivered(deliveredReqDTO);
            log.info("DeliveryWSManagerImpl-妥投处理接口 出参-{}", JSON.toJSONString(deliveredReqDTO));
            return responseDTO;
        }catch (Exception e){
            log.error("妥投处理接口异常-入参-{}-异常信息-{}",JSON.toJSONString(deliveredReqDTO),e.getMessage(),e);
        }
        return responseDTO;
    }
}
