package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.waybillnocollection.request.DifferentialQueryRequest;
import com.jd.bluedragon.common.dto.waybillnocollection.response.DifferentialQueryResultDto;

/**
 * DifferentialQueryGatewayService
 * 一车一单发货、组板、建箱差异查询服务
 *
 * @author jiaowenqiang
 * @date 2019/7/5
 */
public interface DifferentialQueryGatewayService {

    JdCResponse<DifferentialQueryResultDto> getDifferentialQuery(DifferentialQueryRequest request);

}
