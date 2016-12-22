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
     * 根据主键id逻辑删除
     *
     * @param id
     */
    public void disableById(Integer id) {
        this.getSqlSession().update(AreaDestDao.namespace + ".disableById", id);
    }

    /**
     * 根据参数逻辑删除
     *
     * @param params
     */
    public void disableByParams(Map<String, Object> params) {
        this.getSqlSession().update(AreaDestDao.namespace + ".disableByParams", params);
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

}
