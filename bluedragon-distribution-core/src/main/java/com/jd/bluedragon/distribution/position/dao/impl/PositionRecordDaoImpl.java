package com.jd.bluedragon.distribution.position.dao.impl;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.position.dao.PositionRecordDao;
import com.jd.bluedragon.distribution.position.domain.PositionDetailRecord;
import com.jd.bluedragon.distribution.position.domain.PositionRecord;
import com.jd.bluedragon.distribution.position.query.PositionQuery;

import java.util.List;

/**
 * 岗位查询DAO
 *
 * @author hujiping
 * @date 2022/2/25 5:48 PM
 */
public class PositionRecordDaoImpl extends BaseDao<PositionRecord> implements PositionRecordDao {

    private final static String NAMESPACE = PositionRecordDao.class.getName();

    @Override
    public int insert(PositionRecord record) {
        return this.getSqlSession().insert(NAMESPACE+".insert",record);
    }

    @Override
    public int batchInsert(List<PositionRecord> list) {
        return this.getSqlSession().insert(NAMESPACE+".batchInsert",list);
    }

    @Override
    public int updateByPositionCode(PositionRecord positionRecord) {
        return this.getSqlSession().update(NAMESPACE+".updateByPositionCode",positionRecord);
    }

    @Override
    public int deleteByBusinessKey(PositionRecord positionRecord) {
        return this.getSqlSession().update(NAMESPACE+".deleteByBusinessKey",positionRecord);
    }

    @Override
    public PositionRecord queryByPositionCode(String positionCode) {
        return this.getSqlSession().selectOne(NAMESPACE+".queryByPositionCode",positionCode);
    }

    @Override
    public PositionRecord queryByBusinessKey(String businessKey) {
        return this.getSqlSession().selectOne(NAMESPACE+".queryByBusinessKey",businessKey);
    }

    @Override
    public List<PositionDetailRecord> queryList(PositionQuery query) {
        return this.getSqlSession().selectList(NAMESPACE+".queryList",query);
    }

    @Override
    public Long queryCount(PositionQuery query) {
        return this.getSqlSession().selectOne(NAMESPACE+".queryCount",query);
    }
}
