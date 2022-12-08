package com.jd.bluedragon.utils.converter;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 结果转换工具类
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-11-23 18:39:01 周三
 */
public class ResultConverter {

    public static <T> JdCResponse<T> convertResultToJdcResponse(Result<T> result){
        JdCResponse<T> response = new JdCResponse<>();
        response.toFail();
        if(result == null){
            return response;
        }
        if(result.isSuccess()){
            response.toSucceed(result.getMessage());
            response.setData(result.getData());
            return response;
        }
        if(!result.isSuccess()){
            response.init(result.getCode(), result.getMessage());
            return response;
        }
        return response;
    }
}
