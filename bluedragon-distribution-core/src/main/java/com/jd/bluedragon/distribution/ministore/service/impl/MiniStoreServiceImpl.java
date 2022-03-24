package com.jd.bluedragon.distribution.ministore.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.ministore.dao.MiniStoreBindRelationDao;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.dto.MiniStoreEvent;
import com.jd.bluedragon.distribution.ministore.dto.QueryTaskDto;
import com.jd.bluedragon.distribution.ministore.dto.SealBoxDto;
import com.jd.bluedragon.distribution.ministore.enums.MSDeviceBindEventTypeEnum;
import com.jd.bluedragon.distribution.ministore.enums.MiniStoreProcessStatusEnum;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    //@Autowired
    //@Qualifier("miniStoreSealBoxProducer")
    private DefaultJMQProducer miniStoreSealBoxProducer;

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
        int rs = miniStoreBindRelationDao.updateByPrimaryKeySelective(miniStoreBindRelation);
        if (rs > 0) {
            MiniStoreEvent miniStoreEvent =BeanUtils.convert(sealBoxDto,MiniStoreEvent.class);
            miniStoreEvent.setEventType(MSDeviceBindEventTypeEnum.SEAL_BOX.getCode());
            miniStoreEvent.setCreateTime(TimeUtils.date2string(new Date(),TimeUtils.yyyy_MM_dd_HH_mm_ss));
            miniStoreSealBoxProducer.sendOnFailPersistent(sealBoxDto.getBoxCode(), JsonHelper.toJson(sealBoxDto));
            return true;
        }
        return false;
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
        return miniStoreBindRelationDao.updateByPrimaryKeySelective(miniStoreBindRelation)>0;
    }

    @Override
    public int incrSortCount(Long id,String updateUser,Long updateUserCode) {
        return miniStoreBindRelationDao.incrSortCount(id,updateUser,updateUserCode);
    }

    @Override
    public boolean validateSortRelation(String boxCode, String packageCode) {
        Sorting sorting =new Sorting();
        sorting.setBoxCode(boxCode);
        sorting.setPackageCode(packageCode);
        Long id =sortingDao.findByPackageCodeAndBoxCode(sorting);
        return null!=id;
    }
}
