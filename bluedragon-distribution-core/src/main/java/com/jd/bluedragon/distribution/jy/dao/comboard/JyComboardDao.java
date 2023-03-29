package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardEntity;
import com.jd.bluedragon.distribution.jy.dto.comboard.BatchUpdateCancelReq;

import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class JyComboardDao extends BaseDao<JyComboardEntity> {

    private final static String NAMESPACE = JyComboardDao.class.getName();

    private static final String DB_TABLE_NAME = "jy_comboard";
    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    public int deleteByPrimaryKey(Long id) {
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    public int insert(JyComboardEntity entity) {
        entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    public int insertSelective(JyComboardEntity record) {
        record.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
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
    public List<User> queryUserByStartSiteCode(JyComboardEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryUserByStartSiteCode", entity);
    }

    public String queryWayBillCodeByBoardCode(JyComboardEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryWayBillCodeByBoardCode", entity);
    }

    public JyComboardEntity queryByBarCode(JyComboardEntity record) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByBarCode", record);
    }

    /**
     * 批量更新取消标识
     * @param req
     * @return
     */
    public int batchUpdateCancelFlag(BatchUpdateCancelReq req) {
        return this.getSqlSession().update(NAMESPACE + ".batchUpdateCancelFlag", req);
    }
}
