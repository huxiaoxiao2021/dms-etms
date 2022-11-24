package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardEntity;
public class JyComboardDao extends BaseDao<JyComboardEntity> {

    private final static String NAMESPACE = JyComboardDao.class.getName();

    public int deleteByPrimaryKey(Long id) {
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    public int insert(JyComboardEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    public int insertSelective(JyComboardEntity record) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }
    public JyComboardEntity selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }
    public int updateByPrimaryKeySelective(JyComboardEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }
    public int updateByPrimaryKey(JyComboardEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }
    public int queryUserCountByStartSiteCode(Long startSiteId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryUserCountByStartSiteCode", startSiteId);
    }
}
