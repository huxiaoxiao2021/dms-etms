package com.jd.bluedragon.distribution.external.api;

import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.SendResult;

import java.util.AbstractMap;

/**
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsDeliveryApi {

    /**
     * 校验并获取批次号信息
     * 用途:
     * 1. 扫描发货批次号后，校验批次号是否符合正则表达式
     * 2. 校验批次号是否逆向发货（根据批次号目的地类型校验），该入口不允许操作逆向发货。
     * 3. 校验批次号是否已经封车，已经封车不允许再发货。
     * 4. 获取该批次相关的信息，用于页面展示目的站点名称
     *
     * @param sendCode
     * @return
     */
    InvokeResult<AbstractMap.Entry<Integer, String>> checkSendCodeStatus(String sendCode);

    /**
     * 发货，根据批次号和所扫描的箱子或包裹或板号，进行发货
     *
     * @param request
     * @return
     */
    InvokeResult<SendResult> newPackageSend(PackageSendRequest request);

}
