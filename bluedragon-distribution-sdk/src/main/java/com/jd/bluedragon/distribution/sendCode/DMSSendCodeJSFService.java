package com.jd.bluedragon.distribution.sendCode;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sendCode.domain.HugeSendCodeEntity;

/**
 * <p>
 *     分拣批次号得相关得服务
 *
 * @author zoothon
 * @since 2020/2/18
 **/
public interface DMSSendCodeJSFService {

    /**
     * 查询批次号得相关信息，非明细信息
     * @param sendCode 批次号
     * @return
     */
    InvokeResult<HugeSendCodeEntity> queryBigInfoBySendCode(String sendCode);

}
