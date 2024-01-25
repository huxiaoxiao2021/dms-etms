package com.jd.bluedragon.external.gateway.store;

import com.jd.bluedragon.distribution.box.domain.GenerateBoxReq;
import com.jd.bluedragon.distribution.box.domain.GenerateBoxResp;
import com.jd.bluedragon.distribution.box.domain.UpdateBoxReq;
import com.jd.bluedragon.distribution.box.domain.UpdateBoxResp;
import com.jd.ql.dms.common.domain.JdResponse;
public interface TpCollectPackageGatewayService {

    /**
     * 三方生成箱号逻辑-针对于:不明确始发和目的的场景
     * @param request
     * @return
     */
    JdResponse<GenerateBoxResp> generateBoxCode(GenerateBoxReq request);


    /**
     * 箱号信息 变更
     * @param request
     * @return
     */
    JdResponse<UpdateBoxResp> updateBox(UpdateBoxReq request);
}
