package com.jd.bluedragon.distribution.ministore.service.impl;

import com.jd.bluedragon.distribution.ministore.dao.MiniStoreBindRelationDao;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("miniStoreService")
public class MiniStoreServiceImpl implements MiniStoreService {
    private static final Logger logger = LoggerFactory.getLogger(MiniStoreServiceImpl.class);
    @Autowired
    MiniStoreBindRelationDao miniStoreBindRelationDao;

    @Override
    public Boolean validatDeviceBindStatus(DeviceDto deviceDto) {
        return null != miniStoreBindRelationDao.selectDeviceBindStatus(deviceDto);
    }

    @Override
    public Boolean validateStoreBindStatus(String storeCode) {
        return null != miniStoreBindRelationDao.selectStoreBindStatus(storeCode);
    }

    @Override
    public Boolean validateIceBoardBindStatus(String iceBoardCode) {
        return null != miniStoreBindRelationDao.selectIceBoardStatus(iceBoardCode);
    }

    @Override
    public Boolean validateBoxBindStatus(String boxCode) {
        return null != miniStoreBindRelationDao.selectBoxBindStatus(boxCode);
    }

    @Override
    public Boolean bindMiniStoreDevice(DeviceDto deviceDto) {
        MiniStoreBindRelation miniStoreBindRelation = BeanUtils.copy(deviceDto, MiniStoreBindRelation.class);
        Date date = new Date();
        miniStoreBindRelation.setCreateTime(date);
        miniStoreBindRelation.setUpdateTime(date);
        return miniStoreBindRelationDao.insertSelective(miniStoreBindRelation) > 0;
    }
}
