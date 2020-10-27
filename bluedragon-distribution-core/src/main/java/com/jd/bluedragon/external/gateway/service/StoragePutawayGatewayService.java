package com.jd.bluedragon.external.gateway.service;


import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.storageputaway.request.StoragePutawayRequest;
import com.jd.bluedragon.distribution.storage.domain.StorageCheckDto;

import java.util.List;

/**
 * 暂存上架服务
 *发布到物流网关 由安卓调用
 * @author jiaowenqiang
 * @date 2019/7/5
 */
public interface StoragePutawayGatewayService {

    JdCResponse<List<String>> getStorageInfo(Integer siteCode);

    JdCResponse<Boolean> checkStorage(Integer siteCode, String storageCode);

    JdCResponse<Boolean> checkIsNeedStorage(String barCode, Integer siteCode);

    JdCResponse<StorageCheckDto> storageTempCheck(String barCode, Integer siteCode);

    JdCResponse<Boolean> putaway(StoragePutawayRequest request);

    JdCResponse<String> getExistStorageCode(String barCode);

}
