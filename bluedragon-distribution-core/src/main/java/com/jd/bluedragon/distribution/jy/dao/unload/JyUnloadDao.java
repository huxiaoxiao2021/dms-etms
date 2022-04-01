package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;

/**
 * 卸车任务明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:35
 */
public class JyUnloadDao extends BaseDao<JyUnloadEntity> {

    final static String NAMESPACE = JyUnloadDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyUnloadEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
}
