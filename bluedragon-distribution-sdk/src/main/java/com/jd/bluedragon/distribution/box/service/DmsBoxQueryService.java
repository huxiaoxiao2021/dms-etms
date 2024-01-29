package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.request.box.BoxTypeReq;
import com.jd.bluedragon.distribution.api.response.box.BoxDto;
import com.jd.bluedragon.distribution.api.response.box.BoxTypeDto;
import com.jd.dms.java.utils.sdk.base.Result;

import java.util.List;

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

    /**
     * 查询箱类型
     *
     * @param boxTypeReq 查询箱类型入参
     * @return 箱号类型列表
     * @author fanggang7
     * @time 2023-10-24 14:14:24 周二
     */
    Result<List<BoxTypeDto>> getBoxTypeList(BoxTypeReq boxTypeReq);

}
