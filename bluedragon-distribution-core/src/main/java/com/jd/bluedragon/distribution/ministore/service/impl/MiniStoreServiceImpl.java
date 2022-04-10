package com.jd.bluedragon.distribution.ministore.service.impl;

import com.jd.bluedragon.distribution.ministore.dao.MiniStoreBindRelationDao;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.dto.QueryTaskDto;
import com.jd.bluedragon.distribution.ministore.dto.SealBoxDto;
import com.jd.bluedragon.common.dto.ministore.MiniStoreProcessStatusEnum;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

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
        int r1 = 0;
        //保存镜像-回滚使用
        MiniStoreBindRelation pre = miniStoreBindRelationDao.selectByPrimaryKey(deviceDto.getMiniStoreBindRelationId());

        MiniStoreBindRelation miniStoreBindRelation = new MiniStoreBindRelation();
        miniStoreBindRelation.setId(deviceDto.getMiniStoreBindRelationId());
        miniStoreBindRelation.setState(Byte.valueOf(MiniStoreProcessStatusEnum.UN_SEAL_BOX.getCode()));
        miniStoreBindRelation.setUpdateUser(deviceDto.getUpdateUser());
        miniStoreBindRelation.setUpdateUserCode(deviceDto.getUpdateUserCode());
        miniStoreBindRelation.setUpdateTime(new Date());
        miniStoreBindRelation.setOccupiedFlag(false);
        try {
            r1 = miniStoreBindRelationDao.updateByPrimaryKeySelective(miniStoreBindRelation);
            if (r1 > 0) {
                int rs = invaliSortRealtion(deviceDto.getBoxCode(),deviceDto.getCreateSiteCode());
                if (rs <= 0) {
                    miniStoreBindRelationDao.updateByPrimaryKeySelective(pre);
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("解封箱异常", e);
            if (r1 > 0) {
                miniStoreBindRelationDao.updateByPrimaryKeySelective(pre);
            }
        }
        return false;
    }

    @Override
    public Boolean updateProcessStatusAndSyncMsg(SealBoxDto sealBoxDto) {
        MiniStoreBindRelation miniStoreBindRelation = new MiniStoreBindRelation();
        miniStoreBindRelation.setId(sealBoxDto.getMiniStoreBindRelationId());
        miniStoreBindRelation.setState(Byte.valueOf(MiniStoreProcessStatusEnum.SEAL_BOX.getCode()));
        miniStoreBindRelation.setUpdateTime(new Date());
        return miniStoreBindRelationDao.updateByPrimaryKeySelective(miniStoreBindRelation)>0;
    }

    @Override
    public List<MiniStoreBindRelation> queryBindAndNoSortTaskList(QueryTaskDto queryTaskDto) {
        return miniStoreBindRelationDao.listBindDate(queryTaskDto);
    }

    @Override
    public Integer queryMiniStoreSortCount() {
        return null;
    }

    @Override
    public boolean unBind(Long miniStoreBindRelationId,Long updateUserCode,String updateUser) {
        MiniStoreBindRelation miniStoreBindRelation =new MiniStoreBindRelation();
        miniStoreBindRelation.setId(miniStoreBindRelationId);
        miniStoreBindRelation.setState(Byte.valueOf(MiniStoreProcessStatusEnum.UNBIND.getCode()));
        miniStoreBindRelation.setUpdateTime(new Date());
        miniStoreBindRelation.setUpdateUserCode(updateUserCode);
        miniStoreBindRelation.setUpdateUser(updateUser);
        miniStoreBindRelation.setOccupiedFlag(false);
        return miniStoreBindRelationDao.updateByPrimaryKeySelective(miniStoreBindRelation)>0;
    }

    @Override
    public int incrSortCount(Long id,String updateUser,Long updateUserCode) {
        return miniStoreBindRelationDao.incrSortCount(id,updateUser,updateUserCode);
    }

    @Override
    public boolean validateSortRelation(String boxCode, String packageCode,Integer createSiteCode) {
        Sorting sorting =new Sorting();
        sorting.setBoxCode(boxCode);
        sorting.setPackageCode(packageCode);
        sorting.setCreateSiteCode(createSiteCode);
        Long id =sortingDao.findByPackageCodeAndBoxCode(sorting);
        return null!=id;
    }

    @Override
    public int invaliSortRealtion(String boxCode, Long createSiteCode) {
        Sorting sorting =new Sorting();
        sorting.setCreateSiteCode(createSiteCode.intValue());
        sorting.setBoxCode(boxCode);
        return sortingDao.invaliSortRealtion(sorting);
    }

    @Override
    public MiniStoreBindRelation selectById(Long id) {
        return miniStoreBindRelationDao.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(MiniStoreBindRelation m) {
        return miniStoreBindRelationDao.updateByPrimaryKeySelective(m);
    }
}
