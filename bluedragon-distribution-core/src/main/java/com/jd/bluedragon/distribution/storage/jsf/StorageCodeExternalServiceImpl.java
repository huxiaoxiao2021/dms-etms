package com.jd.bluedragon.distribution.storage.jsf;

import com.jd.bluedragon.core.base.DmsLocalServerManager;
import com.jd.bluedragon.distribution.external.sdk.api.storage.StorageCodeApi;
import com.jd.bluedragon.distribution.external.sdk.base.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("storageCodeExternalService")
public class StorageCodeExternalServiceImpl implements StorageCodeApi {

    private static final Logger log = LoggerFactory.getLogger(StorageCodeExternalServiceImpl.class);

    @Autowired
    @Qualifier("dmsLocalServerManager")
    private DmsLocalServerManager dmsLocalServerManager;


    @Override
    public ServiceResult<List<String>> getStorageCodeByDmsId(Integer dmsId) {
        ServiceResult<List<String>> result = new ServiceResult<>();
        try {
            result.setData(dmsLocalServerManager.getStorageCodeByDmsId(dmsId));
            result.setSuccess(true);
        } catch (Exception e) {
            log.error("获取储位信息失败,error=", e);
            result.toSystemError();
        }
        return result;
    }

    @Override
    public ServiceResult<Boolean> checkStorage(Integer dmsId, String storageCode) {
        ServiceResult<Boolean> result = new ServiceResult<>();
        try {
            result.setData(dmsLocalServerManager.checkStorage(dmsId, storageCode));
            result.setSuccess(true);
        } catch (Exception e) {
            log.error("校验储位信息失败,error=", e);
            result.toSystemError();
        }
        return result;
    }

}
