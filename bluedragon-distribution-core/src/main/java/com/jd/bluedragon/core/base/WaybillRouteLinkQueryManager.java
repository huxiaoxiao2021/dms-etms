package com.jd.bluedragon.core.base;

import com.jd.etms.api.common.dto.CommonDto;
import com.jd.etms.api.waybillroutelink.resp.WaybillRouteLinkResp;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/8 17:17
 * @Description: 查询运单的最新路由环节信息
 */
public interface WaybillRouteLinkQueryManager {

    //根据运单查询运单路由环节信息
    List<WaybillRouteLinkResp> queryCustomWaybillRouteLink(String waybillCode);
}
