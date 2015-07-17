package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.domain.FWaybillArgs;
import com.jd.bluedragon.distribution.waybill.domain.FWaybillResult;
import org.springframework.stereotype.Service;

/**
 * F返单换单服务
 * Created by wangtingwei on 2014/9/4.
 */
public interface FWaybillExchangeService {
    /**
     * 返单换单
     * @param fWaybillArgs
     * @return 新外单编号
     */
    InvokeResult<FWaybillResult> exchange(FWaybillArgs fWaybillArgs);
}
