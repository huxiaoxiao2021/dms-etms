package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.waybill.request.WaybillTrackReq;
import com.jd.etms.waybill.domain.PackageState;

import java.util.List;

/**
 * 运单相关服务
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/7/16
 */
public interface WaybillGateWayService {

    JdCResponse<List<Integer>> getPerAndSfSiteByWaybill(String waybillCode);

    JdCResponse<List<String>> queryPackageCodes(String waybillCode);

    JdCResponse<List<String>> queryWaybillTrackHistory(String erp);

    JdCResponse<List<PackageState>> queryWaybillTrack(WaybillTrackReq waybillTrackReq);
}
