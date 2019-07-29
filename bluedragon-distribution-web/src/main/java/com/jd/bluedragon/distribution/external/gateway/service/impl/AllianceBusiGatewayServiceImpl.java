package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.distribution.rest.allianceBusi.AllianceBusiResouse;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.external.gateway.service.AllianceBusiGatewayService;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;

/**
 * 加盟商
 * @author : xumigen
 * @date : 2019/7/27
 */
public class AllianceBusiGatewayServiceImpl implements AllianceBusiGatewayService {

    @Resource
    private AllianceBusiResouse allianceBusiResouse;

    @Override
    public JdVerifyResponse checkAllianceMoney(String waybillCode) {
        JdVerifyResponse jdVerifyResponse = new JdVerifyResponse();
        if(StringUtils.isEmpty(waybillCode)){
            jdVerifyResponse.toError("参数不能为空！");
            return jdVerifyResponse;
        }
        BaseEntity<Boolean> result = allianceBusiResouse.checkMoney(waybillCode);
        if(result.getCode() != BaseEntity.CODE_SUCCESS){
            jdVerifyResponse.toError(result.getMessage());
            return jdVerifyResponse;
        }
        //不充足就是需要拦截
        if(!result.getData()){
            jdVerifyResponse.toSuccess();
            jdVerifyResponse.addInterceptBox(0,result.getMessage());
            return jdVerifyResponse;
        }
        jdVerifyResponse.toSuccess(result.getMessage());
        return jdVerifyResponse;
    }
}
