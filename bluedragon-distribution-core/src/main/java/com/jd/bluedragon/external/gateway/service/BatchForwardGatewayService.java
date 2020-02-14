package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.send.request.WholeBatchRetransRequest;
import com.jd.bluedragon.common.dto.send.response.SendCodeSiteDto;

/**
 * 批次号相关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/7/11
 */
public interface BatchForwardGatewayService {

    /**
     *  校验批次号下的数据，然后获取站点信息
     * @param batchcode 批次号
     * @param batchcodeFlag 1：旧批次 2：新批次
     * @return
     */
    JdCResponse<SendCodeSiteDto> checkSendCodeAndGetSite(String batchcode, Integer batchcodeFlag);

    JdCResponse<String> batchForwardSend(WholeBatchRetransRequest retransRequest);
}
