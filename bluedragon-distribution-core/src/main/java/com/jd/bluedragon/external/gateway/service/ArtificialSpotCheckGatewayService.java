package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.spotcheck.ArtificialSpotCheckRequest;
import com.jd.bluedragon.common.dto.spotcheck.ArtificialSpotCheckResult;
import com.jd.bluedragon.common.dto.spotcheck.PicAutoDistinguishRequest;

/**
 * 人工抽检网关服务
 *
 * @author hujiping
 * @date 2021/8/19 8:06 下午
 */
public interface ArtificialSpotCheckGatewayService {

    /**
     * 获取基础数据
     *
     * @param artificialSpotCheckRequest
     * @return
     */
    JdCResponse<ArtificialSpotCheckResult> obtainBaseInfo(ArtificialSpotCheckRequest artificialSpotCheckRequest);

    /**
     * 校验是否超标
     *
     * @param artificialSpotCheckRequest
     * @return
     */
    JdCResponse<Integer> artificialCheckIsExcess(ArtificialSpotCheckRequest artificialSpotCheckRequest);

    /**
     * 校验超标结果
     *
     * @param artificialSpotCheckRequest
     * @return
     */
    JdCResponse<ArtificialSpotCheckResult> artificialCheckExcess(ArtificialSpotCheckRequest artificialSpotCheckRequest);

    /**
     * 超标图片AI识别
     * @param picAutoDistinguishRequest
     * @return
     */
    JdCResponse<Void> picAutoDistinguish(PicAutoDistinguishRequest picAutoDistinguishRequest);

    /**
     * 提交抽检数据
     *
     * @param artificialSpotCheckRequest
     * @return
     */
    JdCResponse<Void> artificialSubmitSpotCheckInfo(ArtificialSpotCheckRequest artificialSpotCheckRequest);
}
