package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.response.box.BoxDto;

/**
 * @author : xumigen
 * @date : 2020/7/2
 */
public interface DmsBoxQueryService {

    /**
     * 是否是经济网箱号
     * @param boxCode
     * @return
     */
    Response<Boolean> isEconomicNetBox(String boxCode);

    /**
     * 根据箱号查询箱信息
     * @param boxCode
     * @return
     */
    BoxDto getBoxByBoxCode(String boxCode);

    /**
     * 更新箱状态；状态有：可用，不可用
     * @param boxReq
     * @return
     */
    Boolean updateBoxStatus(BoxReq boxReq);
}
