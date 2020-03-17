package com.jd.bluedragon.distribution.material.service;

import com.jd.bluedragon.distribution.api.request.material.warmbox.MaterialBatchSendRequest;
import com.jd.bluedragon.distribution.command.JdResult;

/**
 * @ClassName SortingMaterialSendService
 * @Description
 * @Author wyh
 * @Date 2020/3/16 13:52
 **/
public interface SortingMaterialSendService {

    JdResult<Boolean> cancelMaterialSendByBatchCode(MaterialBatchSendRequest request);
}
