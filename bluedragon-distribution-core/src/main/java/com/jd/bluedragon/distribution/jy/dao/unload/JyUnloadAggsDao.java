package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;

/**
 * 卸车进度汇总表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:35
 */
public class JyUnloadAggsDao extends BaseDao<JyUnloadAggsEntity> {

    final static String NAMESPACE = JyUnloadAggsDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyUnloadAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
}
