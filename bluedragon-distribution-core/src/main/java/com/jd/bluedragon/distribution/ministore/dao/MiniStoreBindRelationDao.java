package com.jd.bluedragon.distribution.ministore.dao;

import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;

import java.util.List;

public interface MiniStoreBindRelationDao {
    int deleteByPrimaryKey(Long id);

    int insert(MiniStoreBindRelation record);

    int insertSelective(MiniStoreBindRelation record);

    MiniStoreBindRelation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiniStoreBindRelation record);

    int updateByPrimaryKey(MiniStoreBindRelation record);

    MiniStoreBindRelation selectByDeviceInfo(DeviceDto deviceDto);

    Integer selectStoreBindStatus(String storeCode);

    Integer selectBoxBindStatus(String boxCode);

    Integer selectIceBoardStatus(String iceBoardCode);

    List<MiniStoreBindRelation> list(DeviceDto deviceDto);

    List<MiniStoreBindRelation> selectBindingList(DeviceDto deviceDto);


}