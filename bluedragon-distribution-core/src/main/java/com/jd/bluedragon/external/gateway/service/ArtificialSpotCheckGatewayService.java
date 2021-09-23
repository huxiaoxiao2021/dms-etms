package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.spotcheck.ArtificialSpotCheckRequest;

/**
 * 人工抽检网关服务
 *
 * @author hujiping
 * @date 2021/8/19 8:06 下午
 */
public interface ArtificialSpotCheckGatewayService {

    /**
     * 校验是否超标
     *
     * @param artificialSpotCheckRequest
     * @return
     */
    JdCResponse<Integer> artificialCheckIsExcess(ArtificialSpotCheckRequest artificialSpotCheckRequest);

    /**
     * 提交抽检数据
     *
     * @param artificialSpotCheckRequest
     * @return
     */
    JdCResponse<Void> artificialSubmitSpotCheckInfo(ArtificialSpotCheckRequest artificialSpotCheckRequest);
}
