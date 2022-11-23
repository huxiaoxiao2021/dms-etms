package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.comboard.response.BoardDto;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import java.util.List;

public class JyBizTaskComboardDao extends BaseDao<JyBizTaskComboardEntity> {
    private final static String NAMESPACE = JyBizTaskComboardDao.class.getName();

    public int deleteByPrimaryKey(Long id) {
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    public int insert(JyBizTaskComboardEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    public int insertSelective(JyBizTaskComboardEntity record) {
        return this.getSqlSession().insert(NAMESPACE + ".deleteByPrimaryKey", record);
    }
    public JyBizTaskComboardEntity selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NAMESPACE + ".deleteByPrimaryKey", id);
    }
    public int updateByPrimaryKeySelective(JyBizTaskComboardEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", record);
    }
    public int updateByPrimaryKey(JyBizTaskComboardEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", record);
    }

  public List<JyBizTaskComboardEntity> queryBoardTask(JyBizTaskComboardEntity record) {
      return this.getSqlSession().selectList(NAMESPACE + ".queryBoardTask", record);
  }
}
