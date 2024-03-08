package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.request.AutoBoardCompleteRequest;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardResultDto;
import com.jd.bluedragon.common.dto.cage.request.AutoCageRequest;

import java.util.List;

public interface DeviceCageGatewayService {

    /**
     * 更加场地 查询分拣机编码 支持装笼的分拣机
     * @param siteCode
     * @return
     */
    JdCResponse<List<String>> getSortMachineBySiteCode(Integer siteCode);

    /**
     * 查询设备是否回传分拣信息
     * @param request
     * @return
     */
    JdCResponse<Boolean> querySortingInfo(AutoCageRequest request);

    /**
     * 装笼
     * @param request
     * @return
     */
    JdCResponse<Boolean> cage(AutoCageRequest request);
}
