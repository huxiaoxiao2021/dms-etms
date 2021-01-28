package com.jd.bluedragon.distribution.external.sdk.api.box;

import com.jd.bluedragon.distribution.external.sdk.base.ServiceResult;
import com.jd.bluedragon.distribution.external.sdk.dto.box.BoxDto;
import com.jd.bluedragon.distribution.external.sdk.dto.box.BoxReq;

/**
 * 对外暴露的箱服务
 */
public interface IDMSBoxApi {

    /**
     * 根据箱号查询箱信息
     * @param boxCode
     * @return
     */
    ServiceResult<BoxDto> getBoxByBoxCode(String boxCode);

    /**
     * 更新箱状态；状态有：可用，不可用
     * @param boxReq
     * @return
     */
    ServiceResult<String> updateBoxStatus(BoxReq boxReq);

}
