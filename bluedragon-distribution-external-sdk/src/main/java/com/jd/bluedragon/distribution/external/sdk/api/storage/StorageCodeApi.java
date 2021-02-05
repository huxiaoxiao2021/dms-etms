package com.jd.bluedragon.distribution.external.sdk.api.storage;

import com.jd.bluedragon.distribution.external.sdk.base.ServiceResult;

import java.util.List;

/**
 * 储位号相关jsf接口
 */
public interface StorageCodeApi {

    /**
     * 根据 分拣中心 编号获取储位号
     */
    ServiceResult<List<String>> getStorageCodeByDmsId(Integer dmsId);

    /**
     * 检查储位是否存在
     */
    ServiceResult<Boolean> checkStorage(Integer dmsId, String storageCode);


}
