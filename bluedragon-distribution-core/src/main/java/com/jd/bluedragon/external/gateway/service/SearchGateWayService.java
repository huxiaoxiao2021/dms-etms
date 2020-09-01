package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.search.request.PackBoxRequest;
import com.jd.bluedragon.common.dto.search.request.PackWeightVORequest;
import com.jd.bluedragon.common.dto.search.response.BoxInfoResponse;
import com.jd.bluedragon.common.dto.search.response.ExpressReceivePackDiffResponse;
import com.jd.bluedragon.common.dto.search.response.PackBoxResponse;
import com.jd.bluedragon.common.dto.search.response.PackWeightVOResponse;
import com.jd.bluedragon.common.dto.search.response.PackageDifferentialResponse;
import com.jd.bluedragon.common.dto.send.request.DeliveryRequest;

import java.util.List;

/**
 * 安卓查询接口服务类
 */
public interface SearchGateWayService {

    /**
     * 根据运单号或包裹号查询箱号包裹信息
     * @param request
     * @return
     */
    JdCResponse<List<PackBoxResponse>> getBoxPackList(PackBoxRequest request);

    /**
     * 根据箱号，获取箱内包裹数
     * @param request
     * @return
     */
    JdCResponse<BoxInfoResponse> getPackSortByBoxCode(PackBoxRequest request);

    /**
     *包裹不全查询
     * @param request
     * @return
     */
    JdCResponse<List<PackageDifferentialResponse>> getPackageDifferential(DeliveryRequest request);

    /**
     * 获取快运包裹明细信息
     * @param request
     * @return
     */
    JdCResponse<ExpressReceivePackDiffResponse> queryPackageDetails(PackBoxRequest request);

    /**
     * 获取快运箱明细信息
     * @param request
     * @return
     */
    JdCResponse<List<PackBoxResponse>> queryBoxDetails(PackBoxRequest request);

    /**
     * 根据包裹号获取重量信息
     * @param request
     * @return
     */
    JdCResponse<PackWeightVOResponse> getPackageWeight(String request);

    /**
     * 上传包裹称重信息
     * @param request
     * @return
     */
    JdCResponse<Boolean> savePackageWeight(PackWeightVORequest request);
}
