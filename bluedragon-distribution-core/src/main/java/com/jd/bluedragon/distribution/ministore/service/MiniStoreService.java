package com.jd.bluedragon.distribution.ministore.service;


import com.jd.bluedragon.Pager;
import com.jd.bluedragon.dbrouter.DataSources;
import com.jd.bluedragon.dbrouter.DynamicDataSourceType;
import com.jd.bluedragon.distribution.ministore.dao.MiniStoreBindRelationDao;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.dto.QueryTaskDto;
import com.jd.bluedragon.distribution.ministore.dto.SealBoxDto;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfig;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfigRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedSite;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.PrintQueryRequest;

import java.util.List;

public interface MiniStoreService {

    /**
     * 查询设备（三码）绑定状态
     * true 绑定状态 false 未绑定
     */
    @DataSources(DynamicDataSourceType.DMS_UNDIV_MAIN)
    Boolean validatDeviceBindStatus(DeviceDto deviceDto);

    /**
     * 查询storeCode的占用状态
     * true 被绑定 false 未绑定
     */
    Boolean validateStoreBindStatus(String storeCode);

    /**
     * 查询iceBoardCode占用状态
     * true 被绑定 false 未绑定
     */
    Boolean validateIceBoardBindStatus(String iceBoardCode);

    /**
     * 查询boxCode的占用状态
     * true 被绑定 false 未绑定
     */
    Boolean validateBoxBindStatus(String boxCode);

    /**
     * 绑定设备（三码）
     *
     * @param deviceDto
     * @return true绑定成功 false绑定失败
     */
    Boolean bindMiniStoreDevice(DeviceDto deviceDto);

    /**
     * 查询绑定关系模型
     * @param deviceDto
     * @return
     */
    @DataSources(DynamicDataSourceType.DMS_UNDIV_MAIN)
    MiniStoreBindRelation selectBindRelation(DeviceDto deviceDto);


    Boolean updateProcessStatusAndInvaliSortRealtion(DeviceDto deviceDto);

    Boolean updateProcessStatusAndSyncMsg(SealBoxDto sealBoxDto);

    @DataSources(DynamicDataSourceType.DMS_UNDIV_SLAVE)
    List<MiniStoreBindRelation> queryBindAndNoSortTaskList(QueryTaskDto queryTaskDto);

    Integer queryMiniStoreSortCount();

    boolean unBind(Long miniStoreBindRelationId,Long updateUserCode,String updateUser);

    int incrSortCount(Long id,String updateUser,Long updateUserCode);

    boolean validateSortRelation(String boxCode, String packageCode,Integer createSiteCode);

    int invaliSortRealtion(String boxCode,Long createSiteCode);

    MiniStoreBindRelation selectById(Long id);

    int updateById(MiniStoreBindRelation m);
}
