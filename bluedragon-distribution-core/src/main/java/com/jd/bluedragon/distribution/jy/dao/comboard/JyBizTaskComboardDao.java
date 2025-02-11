package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountReq;
import com.jd.bluedragon.distribution.jy.dto.comboard.CountBoardDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyBizTaskComboardReq;
import com.jd.bluedragon.distribution.jy.dto.comboard.UpdateBoardStatusDto;

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

  public List<JyBizTaskComboardEntity> listBoardTaskBySendFlow(JyBizTaskComboardEntity condition) {
    return this.getSqlSession().selectList(NAMESPACE + ".listBoardTaskBySendFlow", condition);
  }

    /**
     * 查询其他内未封车的板
     * @param boardCountReq
     * @return
     */
    public List<BoardCountDto> boardCountTaskBySendFlowList(BoardCountReq boardCountReq) {
        return this.getSqlSession().selectList(NAMESPACE + ".boardCountTaskBySendFlowList", boardCountReq);
    }

  public List<JyBizTaskComboardEntity> listBoardTaskBySendCode(JyBizTaskComboardEntity entity) {
    return this.getSqlSession().selectList(NAMESPACE + ".listBoardTaskBySendCode", entity);
  }

    public int updateBoardStatus(UpdateBoardStatusDto boardStatusDto) {
        return this.getSqlSession().update(NAMESPACE + ".updateBoardStatus", boardStatusDto);
    }

    public List<JyBizTaskComboardEntity> queryTaskBySendCode(String sendCode) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryTaskBySendCode",sendCode);
    }

    public List<JyBizTaskComboardEntity> listSealOrUnSealedBoardTaskBySendFlow(JyBizTaskComboardEntity condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".listSealOrUnSealedBoardTaskBySendFlow", condition);
    }

    public List<JyBizTaskComboardEntity> listSealOrUnSealedBoardTaskBySendFlowUnionAll(JyBizTaskComboardEntity condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".listSealOrUnSealedBoardTaskBySendFlowUnionAll", condition);
    }

  public List<SendFlowDto> countBoardGroupBySendFlow(CountBoardDto countBoardDto) {
    return this.getSqlSession().selectList(NAMESPACE + ".countBoardGroupBySendFlow", countBoardDto);
  }
}
