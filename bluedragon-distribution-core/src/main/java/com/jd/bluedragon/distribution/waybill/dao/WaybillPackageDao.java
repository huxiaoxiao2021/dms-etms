package com.jd.bluedragon.distribution.waybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.waybill.domain.FreshWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;

/**
 * Created by zhanglei51 on 2016/12/15.
 */
public class WaybillPackageDao extends BaseDao<FreshWaybill> {
    private static final String namespace = WaybillPackageDao.class.getName();

    public WaybillPackageDTO get(String packageCode){
        return this.getSqlSession().selectOne(namespace + ".get", packageCode);
    }

}
