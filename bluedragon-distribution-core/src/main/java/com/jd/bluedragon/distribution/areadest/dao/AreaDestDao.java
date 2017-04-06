package com.jd.bluedragon.distribution.areadest.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;

import java.util.List;
import java.util.Map;

/**
 * Created by lixin39 on 2016/12/8.
 */
public class AreaDestDao extends BaseDao<AreaDest> {

    private final static String namespace = AreaDestDao.class.getName();

    /**
     * 新增
     *
     * @param areaDest
     * @return
     */
    public int add(AreaDest areaDest) {
        return this.getSqlSession().insert(AreaDestDao.namespace + ".add", areaDest);
    }

    /**
     * 批量新增
     *
     * @param areaDests
     * @return
     */
    public int addBatch(List<AreaDest> areaDests) {
        return this.getSqlSession().insert(AreaDestDao.namespace + ".addBatch", areaDests);
    }

    /**
     * 更新
     *
     * @param areaDest
     * @return
     */
    public int update(AreaDest areaDest) {
        return this.getSqlSession().update(AreaDestDao.namespace + ".update", areaDest);
    }

    /**
     * 根据主键id设置为无效
     *
     * @param params
     */
    public int disableByPlanId(Map<String, Object> params) {
        return this.getSqlSession().update(AreaDestDao.namespace + ".disableByPlanId", params);
    }

    /**
     * 根据参数设置为无效
     *
     * @param params
     */
    public int disableByParams(Map<String, Object> params) {
        return this.getSqlSession().update(AreaDestDao.namespace + ".disableByParams", params);
    }

    /**
     * 根据主键id设置为有效
     *
     * @param params
     */
    public int enableById(Map<String, Object> params) {
        return this.getSqlSession().update(AreaDestDao.namespace + ".enableById", params);
    }

    /**
     * 根据参数获取区域批次目的地列表
     *
     * @param params
     * @return
     */
    public AreaDest get(Map<String, Object> params) {
        return this.getSqlSession().selectOne(AreaDestDao.namespace + ".get", params);
    }

    /**
     * 根据参数获取区域批次目的地列表
     *
     * @param params
     * @return
     */
    public List<AreaDest> getList(Map<String, Object> params) {
        return this.getSqlSession().selectList(AreaDestDao.namespace + ".getList", params);
    }

    /**
     * 根据参数获取龙门架发货关系数量
     *
     * @param params
     * @return
     */
    public Integer getCount(Map<String, Object> params) {
        return this.getSqlSession().selectOne(AreaDestDao.namespace + ".getCount", params);
    }

}
