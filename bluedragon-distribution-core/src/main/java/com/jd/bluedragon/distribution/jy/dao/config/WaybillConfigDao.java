package com.jd.bluedragon.distribution.jy.dao.config;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.config.WaybillConfig;

import java.util.List;
import java.util.Map;


/**
 * 大宗扫描标准配置
 */
public class WaybillConfigDao extends BaseDao<WaybillConfig> {

    final static String NAMESPACE = WaybillConfigDao.class.getName();

    public int countByCondition(Map<String, Object> map) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countByCondition", map);
    }

    public List<WaybillConfig> findWaybillConfigList(Map<String, Object> map) {
        return this.getSqlSession().selectList(NAMESPACE + ".findWaybillConfigList", map);
    }

    public WaybillConfig findLatestWaybillConfig() {
        return this.getSqlSession().selectOne(NAMESPACE + ".findLatestWaybillConfig");
    }

    public int saveWaybillConfig(WaybillConfig record) {
        return this.getSqlSession().selectOne(NAMESPACE + ".saveWaybillConfig", record);
    }


}
