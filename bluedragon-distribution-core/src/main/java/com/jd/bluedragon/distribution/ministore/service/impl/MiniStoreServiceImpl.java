package com.jd.bluedragon.distribution.ministore.service.impl;

import com.jd.bluedragon.distribution.ministore.dao.MiniStoreBindRelationDao;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.enums.MiniStoreProcessStatusEnum;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.eclp.master.qualification.service.LicenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("miniStoreService")
public class MiniStoreServiceImpl implements MiniStoreService {
    private static final Logger logger = LoggerFactory.getLogger(MiniStoreServiceImpl.class);
    @Autowired
    MiniStoreBindRelationDao miniStoreBindRelationDao;
    @Autowired
    SortingDao sortingDao;

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

    @Override
    public MiniStoreBindRelation selectBindRelation(DeviceDto deviceDto) {
        List<MiniStoreBindRelation> miniStoreBindRelationList = miniStoreBindRelationDao.selectDeviceBindRelation(deviceDto);
        if (null != miniStoreBindRelationList && miniStoreBindRelationList.size() == 1) {
            return miniStoreBindRelationList.get(0);
        }
        return null;
    }

    @Override
    public Boolean updateProcessStatusAndInvaliSortRealtion(DeviceDto deviceDto) {
        int r1 =0;
        MiniStoreBindRelation pre = miniStoreBindRelationDao.selectByPrimaryKey(deviceDto.getMiniStoreBindRelationId());

        MiniStoreBindRelation miniStoreBindRelation = new MiniStoreBindRelation();
        miniStoreBindRelation.setId(deviceDto.getMiniStoreBindRelationId());
        miniStoreBindRelation.setState(Byte.valueOf(MiniStoreProcessStatusEnum.UN_SEAL_BOX.getCode()));
        miniStoreBindRelation.setUpdateTime(new Date());
        try {
            r1 = miniStoreBindRelationDao.updateByPrimaryKeySelective(miniStoreBindRelation);
            if (r1 > 0) {
                int rs = sortingDao.invaliSortRealtion(deviceDto.getBoxCode());
                if (rs <= 0) {
                    miniStoreBindRelationDao.updateByPrimaryKeySelective(pre);
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("解封箱异常",e);
            if (r1>0){
                miniStoreBindRelationDao.updateByPrimaryKeySelective(pre);
            }
        }
        return false;
    }

    @Override
    public Boolean updateProcessStatusAndSyncMsg(DeviceDto deviceDto) {
        return null;
    }
}
