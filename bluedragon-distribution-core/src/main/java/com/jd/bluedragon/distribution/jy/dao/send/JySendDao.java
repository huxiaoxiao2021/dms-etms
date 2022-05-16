package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendEntity;

/**
 * 发货明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JySendDao extends BaseDao<JySendEntity> {

    private final static String NAMESPACE = JySendDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JySendEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
}
