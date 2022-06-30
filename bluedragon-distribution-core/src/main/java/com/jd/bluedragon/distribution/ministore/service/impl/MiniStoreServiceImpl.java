package com.jd.bluedragon.distribution.ministore.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.core.base.MiniStoreJsfManger;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ministore.dao.MiniStoreBindRelationDao;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.dto.QueryTaskDto;
import com.jd.bluedragon.distribution.ministore.dto.SealBoxDto;
import com.jd.bluedragon.common.dto.ministore.MiniStoreProcessStatusEnum;
import com.jd.bluedragon.distribution.ministore.exception.MiniStoreBizException;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.enums.SwDeviceStatusEnum;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.ObjectHelper;
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
    MiniStoreJsfManger miniStoreJsfManger;
    @Autowired
    MiniStoreBindRelationDao miniStoreBindRelationDao;
    @Autowired
    SortingService sortingService;

    @Override
    public Boolean validateDeviceCodeStatus(DeviceDto deviceDto) {
        if (deviceDto == null) {
            throw new MiniStoreBizException(MSCodeMapping.MINI_STORE_DEVICE_CODE_ISNULL);
        }
        if (ObjectHelper.isNotNull(deviceDto.getStoreCode())) {
            Integer availableStatus = miniStoreJsfManger.isDeviceUse(deviceDto.getStoreCode());
            if (!SwDeviceStatusEnum.AVAILABLE.getCode().equals(availableStatus)) {
                throw new MiniStoreBizException(MSCodeMapping.MINI_STORE_IS_NOT_AVAILABLE);
            }
            Integer hasBeenBind = miniStoreBindRelationDao.selectStoreBindStatus(deviceDto.getStoreCode());
            if (hasBeenBind != null) {
                throw new MiniStoreBizException(MSCodeMapping.MINI_STORE_HASBEEN_BIND);
            }
        }
        if (ObjectHelper.isNotNull(deviceDto.getIceBoardCode())) {
            Integer availableStatus = miniStoreJsfManger.isDeviceUse(deviceDto.getIceBoardCode());
            if (!SwDeviceStatusEnum.AVAILABLE.getCode().equals(availableStatus)) {
                throw new MiniStoreBizException(MSCodeMapping.INCE_BOARD_IS_NOT_AVAILABLE);
            }
            Integer hasBeenBind = miniStoreBindRelationDao.selectIceBoardStatus(deviceDto.getIceBoardCode());
            if (hasBeenBind != null) {
                throw new MiniStoreBizException(MSCodeMapping.INCE_BOARD_HASBEEN_BIND);
            }
        }
        if (ObjectHelper.isNotNull(deviceDto.getBoxCode())) {
            Integer hasBeenBind = miniStoreBindRelationDao.selectBoxBindStatus(deviceDto.getBoxCode());
            if (hasBeenBind != null) {
                throw new MiniStoreBizException(MSCodeMapping.BOX_HASBEEN_BIND);
            }
        }
        return true;
    }

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
        Integer hasBeenBind = miniStoreBindRelationDao.selectDeviceBindStatus(deviceDto);
        if (hasBeenBind!=null){
            throw new MiniStoreBizException(MSCodeMapping.DEVICE_HASBEEN_BIND);
        }
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

        Date time =new Date();
        MiniStoreBindRelation miniStoreBindRelation = new MiniStoreBindRelation();
        miniStoreBindRelation.setId(deviceDto.getMiniStoreBindRelationId());
        miniStoreBindRelation.setState(Byte.valueOf(MiniStoreProcessStatusEnum.UN_SEAL_BOX.getCode()));
        miniStoreBindRelation.setUpdateUser(deviceDto.getUpdateUser());
        miniStoreBindRelation.setUpdateUserCode(deviceDto.getUpdateUserCode());
        miniStoreBindRelation.setUpdateTime(time);
        miniStoreBindRelation.setOccupiedFlag(false);
        miniStoreBindRelation.setDes(deviceDto.getErrMsg());
        miniStoreBindRelation.setUnboxCount(deviceDto.getUnboxCount());
        try {
            r1 = miniStoreBindRelationDao.updateByPrimaryKeySelective(miniStoreBindRelation);
            if (r1 > 0) {
                Sorting sorting =new Sorting();
                sorting.setBoxCode(deviceDto.getBoxCode());
                sorting.setCreateSiteCode(deviceDto.getCreateSiteCode().intValue());
                sorting.setType(Constants.BUSSINESS_TYPE_POSITIVE);
                sorting.setOperateTime(time);
                sorting.setUpdateUserCode(deviceDto.getUpdateUserCode().intValue());
                sorting.setUpdateUser(deviceDto.getUpdateUser());

                SortingResponse sr = sortingService.doCancelSorting(sorting);
                if (!JdResponse.CODE_OK.equals(sr.getCode())) {
                    logger.error("移动微仓解封箱取消分拣失败:{}",sr.getMessage());
                    miniStoreBindRelationDao.updateByPrimaryKeySelective(pre);
                    if (sr.getCode().equals(22004)){
                        throw new MiniStoreBizException(sr.getCode(),"已经发货，不允许操作解封箱！");
                    }
                    throw new MiniStoreBizException(sr.getCode(),sr.getMessage());
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("解封箱异常", e);
            if (r1 > 0) {
                miniStoreBindRelationDao.updateByPrimaryKeySelective(pre);
            }
            if (e instanceof MiniStoreBizException){
                throw e;
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
        return miniStoreBindRelationDao.updateByPrimaryKeySelective(miniStoreBindRelation) > 0;
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
    public boolean unBind(Long miniStoreBindRelationId, Long updateUserCode, String updateUser) {
        MiniStoreBindRelation miniStoreBindRelation = new MiniStoreBindRelation();
        Date time =new Date();
        miniStoreBindRelation.setId(miniStoreBindRelationId);
        miniStoreBindRelation.setState(Byte.valueOf(MiniStoreProcessStatusEnum.UNBIND.getCode()));
        miniStoreBindRelation.setCreateTime(time);
        miniStoreBindRelation.setUpdateTime(time);
        miniStoreBindRelation.setUpdateUserCode(updateUserCode);
        miniStoreBindRelation.setUpdateUser(updateUser);
        miniStoreBindRelation.setOccupiedFlag(false);
        return miniStoreBindRelationDao.updateByPrimaryKeySelective(miniStoreBindRelation) > 0;
    }

    @Override
    public int incrSortCount(Long id, String updateUser, Long updateUserCode) {
        return miniStoreBindRelationDao.incrSortCount(id, updateUser, updateUserCode);
    }

    @Override
    public boolean validateSortRelation(String boxCode, String packageCode, Integer createSiteCode) {
        Sorting sorting = new Sorting();
        sorting.setBoxCode(boxCode);
        sorting.setPackageCode(packageCode);
        sorting.setCreateSiteCode(createSiteCode);
        Long id = sortingService.findByPackageCodeAndBoxCode(sorting);
        return null != id;
    }

    @Override
    public boolean invaliSortRealtion(String boxCode, Long createSiteCode) {
        Sorting sorting = new Sorting();
        sorting.setCreateSiteCode(createSiteCode.intValue());
        sorting.setBoxCode(boxCode);
        SortingResponse sr = sortingService.doCancelSorting(sorting);
        return JdResponse.CODE_OK.equals(sr.getCode());
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
