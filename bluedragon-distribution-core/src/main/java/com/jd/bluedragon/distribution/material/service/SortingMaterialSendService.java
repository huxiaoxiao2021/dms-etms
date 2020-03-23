package com.jd.bluedragon.distribution.material.service;

import com.jd.bluedragon.distribution.api.request.material.batch.MaterialBatchSendRequest;
import com.jd.bluedragon.distribution.api.response.material.batch.MaterialTypeResponse;
import com.jd.bluedragon.distribution.command.JdResult;

import java.util.List;

/**
 * @ClassName SortingMaterialSendService
 * @Description
 * @Author wyh
 * @Date 2020/3/16 13:52
 **/
public interface SortingMaterialSendService {

    JdResult<Boolean> cancelMaterialSendBySendCode(MaterialBatchSendRequest request);

    JdResult<List<MaterialTypeResponse>> listSortingMaterialType(MaterialBatchSendRequest request);
}
