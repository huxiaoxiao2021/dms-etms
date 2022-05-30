package com.jd.bluedragon.distribution.jy.dao.send;

import io.renren.modules.generator.entity.JySendAggsEntity;
import com.jd.bluedragon.common.dao.BaseDao;

/**
 * 发货数据统计表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
public class JySendAggsDao extends BaseDao<JySendAggsEntity> {

    private final static String NAMESPACE = JySendAggsDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JySendAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
}
