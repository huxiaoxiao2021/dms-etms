package com.jd.bluedragon.distribution.ky.services;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.ky.domain.KYPrintInfo;

public interface KYPrintService {

    /**
     * 根据批次号，获取KY航签单打印信息
     * @param sendCode
     * @return
     */
    InvokeResult<KYPrintInfo> getKYPrintInfoBySendCode(String sendCode);
}
