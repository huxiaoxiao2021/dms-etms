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

}
