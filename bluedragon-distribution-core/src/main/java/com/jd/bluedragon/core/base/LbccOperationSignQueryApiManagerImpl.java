package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.lbcc.delivery.api.dto.ResponseDTO;
import com.jd.lbcc.rule.api.OperationSignQueryApi;
import com.jd.lbcc.rule.api.dto.request.PackageTypeQueryDTO;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * 运营打标中心件型接口试算服务实现
 *
 * @author: 刘铎（liuduo8）
 * @date: 2024/3/20
 * @description: 运营打标中心件型接口试算服务
 *
 */
@Service("lbccOperationSignQueryApiManager")
@Slf4j
public class LbccOperationSignQueryApiManagerImpl implements LbccOperationSignQueryApiManager{


    /**
     * 中台试算服务
     */
    @Autowired
    @Qualifier("operationSignQueryApiJsfService")
    private OperationSignQueryApi operationSignQueryApi;


    /**
     * 件型计算服务
     * https://joyspace.jd.com/pages/oyVAwkDjB4KFo2AA9UID
     * @param packageTypeQueryDTO
     * @return
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "LbccOperationSignQueryApiManager.shapeCalculate",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Map<String, String> shapeCalculate(PackageTypeQueryDTO packageTypeQueryDTO) {
        ResponseDTO<Map<String, String>> responseDTO = null;
        try {
            responseDTO =  operationSignQueryApi.queryPackageType(packageTypeQueryDTO);
            if(Constants.NUMBER_ZERO.equals(responseDTO.getStatusCode()) && responseDTO.getData() != null){
                return responseDTO.getData();
            }else{
                log.error("operationSignQueryApi.queryPackageType fail! req:{},resp:{}", JSON.toJSONString(packageTypeQueryDTO),JSON.toJSONString(responseDTO));
            }
            return new HashMap<>();
        }finally {
            if(log.isInfoEnabled()){
                log.info("operationSignQueryApi.queryPackageType req:{},resp{}",JSON.toJSONString(packageTypeQueryDTO),JSON.toJSONString(responseDTO));
            }
        }
    }
}
