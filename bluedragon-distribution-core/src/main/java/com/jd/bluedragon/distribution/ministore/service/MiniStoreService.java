package com.jd.bluedragon.distribution.ministore.service;


import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfig;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfigRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedSite;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.PrintQueryRequest;

import java.util.List;

public interface MiniStoreService {

    /**
     * 查询设备的占用状态
     */
    Boolean validatDeviceBindStatus(DeviceDto deviceDto);

    Boolean validateStoreBindStatus(String storeCode);
    Boolean validateIceBoardBindStatus(String iceBoardCode);
    Boolean validateBoxBindStatus(String boxCode);
    Boolean bindMiniStoreDevice();



}
