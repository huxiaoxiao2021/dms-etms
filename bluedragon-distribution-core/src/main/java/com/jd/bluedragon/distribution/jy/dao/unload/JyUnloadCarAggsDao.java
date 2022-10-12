package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadCarAggsEntity;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/11 16:23
 * @Description: 卸车进度汇总
 */
public class JyUnloadCarAggsDao extends BaseDao<JyUnloadCarAggsEntity> {

    final static String NAMESPACE = JyUnloadAggsDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insertOrUpdate(JyUnloadCarAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertOrUpdate", entity);
    }
}
