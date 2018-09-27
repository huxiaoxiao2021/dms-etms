package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.modules.dmslocalserverinfo.entity.VipInfoJsfEntity;

import java.util.List;

/**
 * Created by lixin39 on 2018/7/16.
 */
public interface DmsLocalServerManager {

    /**
     * 根据分拣中心ID获取本地服务器VIP列表
     *
     * @param dmsId
     * @return
     */
    List<VipInfoJsfEntity> getVipListByDmsId(Integer dmsId);

    /**
     * 获取所有分拣中心本地服务器VIP列表
     *
     * @return
     */
    List<VipInfoJsfEntity> getAllVipList();

    /**
     * 根据 分拣中心 编号获取储位号
     * @return
     */
    List<String> getStorageCodeByDmsId(Integer dmsId);

    /**
     * 检查储位是否存在
     *
     * @param dmsId
     * @param storageCode
     * @return
     */
    boolean checkStorage(Integer dmsId,String storageCode);

}
