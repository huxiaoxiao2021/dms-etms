package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.sorting.request.SortingCancelRequest;
import com.jd.bluedragon.common.dto.sorting.request.SortingCheckRequest;

import java.util.List;

/**
 * 分拣相关物流网关调用
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/6/12
 */
public interface SortingGatewayService {

    JdCResponse sortingCancel(SortingCancelRequest request);

    JdVerifyResponse sortingPostCheck(SortingCheckRequest checkRequest);

    /**
     * 根据箱号获取该箱号下的运单列表
     */
    JdCResponse<List<String>> getWaybillCodes(String boxCode);

}
