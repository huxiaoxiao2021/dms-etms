package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.comboard.response.BoardDto;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyBizTaskComboardReq;

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
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }
    public JyBizTaskComboardEntity selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }
    public int updateByPrimaryKeySelective(JyBizTaskComboardEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }
    public int updateByPrimaryKey(JyBizTaskComboardEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }

  public List<JyBizTaskComboardEntity> queryBoardTask(JyBizTaskComboardEntity record) {
      return this.getSqlSession().selectList(NAMESPACE + ".queryBoardTask", record);
  }

    /**
     * 根据始发和目的地批量获取当前正在进行中的板号信息
     * @param req
     * @return
     */
    public List<JyBizTaskComboardEntity> queryInProcessBoardListBySendFlowList(JyBizTaskComboardReq req) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryInProcessBoardListBySendFlowList", req);
    }

    /**
     * 完结组板
     * @param jyBizTaskComboardReq
     * @return
     */
    public int finishBoard(JyBizTaskComboardReq jyBizTaskComboardReq) {
        return this.getSqlSession().update(NAMESPACE + ".finishBoard", jyBizTaskComboardReq);
    }

    /**
     * 根据流向批量完结板
     * @param req
     * @return
     */
    public int batchFinishBoardBySendFLowList(JyBizTaskComboardReq req) {
        return this.getSqlSession().update(NAMESPACE + ".batchFinishBoardBySendFLowList", req);
    }
}
