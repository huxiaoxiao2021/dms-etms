package com.jd.bluedragon.distribution.sendprint.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.sendprint.domain.ExpressInfo;

/**
 * Created by wangtingwei on 2015/9/10.
 */
public interface ThirdExpressPrintService {

    /**
     * 获取订单对应三方面单数据
     * @param packageCode 包裹号
     * @return
     */
    InvokeResult<ExpressInfo> getThirdExpress(String packageCode);
}
