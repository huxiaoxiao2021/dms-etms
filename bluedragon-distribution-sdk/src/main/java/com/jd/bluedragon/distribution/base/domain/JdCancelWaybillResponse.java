package com.jd.bluedragon.distribution.base.domain;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName JdCancelWaybillResponse
 * @date 2019/9/6
 */
public class JdCancelWaybillResponse extends JdResponse {

    private Integer featureType;

    /**
     * 拦截类型  1:取消订单拦截,2:拒收订单拦截,3:恶意订单拦截,4:分拣中心拦截,5:仓储异常拦截,6:白条强制拦截
     */
    private Integer interceptType;

    public JdCancelWaybillResponse() {}

    public JdCancelWaybillResponse(Integer code, String message) {
        super(code, message);
    }

    public Integer getFeatureType() {
        return featureType;
    }

    public void setFeatureType(Integer featureType) {
        this.featureType = featureType;
    }

    public Integer getInterceptType() {
        return interceptType;
    }

    public void setInterceptType(Integer interceptType) {
        this.interceptType = interceptType;
    }

}
