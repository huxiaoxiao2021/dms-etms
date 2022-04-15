package com.jd.bluedragon.distribution.ministore.service;


import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.dto.QueryTaskDto;
import com.jd.bluedragon.distribution.ministore.dto.SealBoxDto;

import java.util.List;

public interface MiniStoreService {
    /**
     * 检验设备可用状态
     * @return true 代表可用，false 不可用
     */
    Boolean validateDeviceCodeStatus(DeviceDto deviceDto);
    /**
     * 查询设备（三码）绑定状态
     * true 绑定状态 false 未绑定
     */
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
     * 绑定设备（三码：微仓 冰版 箱子）
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
    MiniStoreBindRelation selectBindRelation(DeviceDto deviceDto);


    Boolean updateProcessStatusAndInvaliSortRealtion(DeviceDto deviceDto);

    Boolean updateProcessStatusAndSyncMsg(SealBoxDto sealBoxDto);

    List<MiniStoreBindRelation> queryBindAndNoSortTaskList(QueryTaskDto queryTaskDto);

    Integer queryMiniStoreSortCount();

    boolean unBind(Long miniStoreBindRelationId,Long updateUserCode,String updateUser);

    int incrSortCount(Long id,String updateUser,Long updateUserCode);

    boolean validateSortRelation(String boxCode, String packageCode,Integer createSiteCode);

    boolean invaliSortRealtion(String boxCode,Long createSiteCode);

    MiniStoreBindRelation selectById(Long id);

    int updateById(MiniStoreBindRelation m);
}
