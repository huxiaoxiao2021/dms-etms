package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.spotcheck.SpotCheckCheckReq;
import com.jd.bluedragon.common.dto.spotcheck.SpotCheckSubmitReq;

/**
 * @program: bluedragon-distribution
 * @description: PDA端抽检相关接口
 * @author: wuming
 * @create: 2020-12-28 14:40
 */
public interface SpotCheckGateWayService {

    /**
     * 校验超标
     *
     * @param req
     * @return
     */
    JdCResponse<Integer> checkIsExcess(SpotCheckCheckReq req);


    /**
     * 抽检提交接口
     *
     * @param req
     * @return
     */
    JdCResponse<Void> spotCheckSubmit(SpotCheckSubmitReq req);
}
