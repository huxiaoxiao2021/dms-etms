package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendGoodsAggsEntity;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/11 16:23
 * @Description: 发货明细汇总
 */
public class JySendGoodsAggsDao extends BaseDao<JySendGoodsAggsEntity> {

    final static String NAMESPACE = JySendGoodsAggsDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insertOrUpdate(JySendGoodsAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertOrUpdate", entity);
    }
}
