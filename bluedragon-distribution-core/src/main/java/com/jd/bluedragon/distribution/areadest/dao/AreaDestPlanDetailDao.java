package com.jd.bluedragon.distribution.areadest.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlanDetail;

import java.util.Map;

/**
 * 龙门架发货关系操作流水
 *
 * Created by lixin39 on 2017/3/16.
 */
public class AreaDestPlanDetailDao extends BaseDao<AreaDestPlanDetail> {

    private final static String namespace = AreaDestPlanDetailDao.class.getName();

    /**
     * 新增操作流水记录
     *
     * @param detail
     * @return
     */
    public int add(AreaDestPlanDetail detail){
        return this.getSqlSession().insert(AreaDestPlanDetailDao.namespace + ".add", detail);
    }

    /**
     * 获取操作流水记录
     *
     * @param parameters
     * @return
     */
    public AreaDestPlanDetail getByScannerTime(Map<String, Object> parameters){
        return this.getSqlSession().selectOne(AreaDestPlanDetailDao.namespace + ".getByScannerTime", parameters);
    }

}
