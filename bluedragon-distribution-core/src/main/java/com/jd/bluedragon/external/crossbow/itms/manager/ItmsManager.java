package com.jd.bluedragon.external.crossbow.itms.manager;

import com.jd.bluedragon.external.crossbow.AbstractCrossbowManager;
import com.jd.bluedragon.external.crossbow.itms.domain.ItmsResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ItmsManager
 * @Description
 * @Author wyh
 * @Date 2021/6/4 14:54
 **/
public class ItmsManager extends AbstractCrossbowManager<Object, ItmsResponse> {

    /**
     * 构建url参数
     *
     * @param condition 相应的条件
     * @return 返回三方接口的url请求参数
     */
    @Override
    protected Map<String, String> getMyUrlParams(Object condition) {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("timeStamp", System.currentTimeMillis() + "");
        return urlParams;
    }

    /**
     * 构建三方接口的请求体request对象
     *
     * @param condition 相应的条件
     * @return 返回三方接口的请求参数request对象
     */
    @Override
    protected Object getMyRequestBody(Object condition) {
        // ITMS网关层要求body为数组结构
        return Collections.singletonList(condition);
    }
}
