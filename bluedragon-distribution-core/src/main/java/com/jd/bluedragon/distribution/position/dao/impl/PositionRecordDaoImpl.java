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
public class PositionRecordDaoImpl extends BaseDao<PositionDetailRecord> implements PositionRecordDao {

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
    public Long queryCount(PositionQuery query) {
        return this.getSqlSession().selectOne(NAMESPACE+".queryCount",query);
    }

    @Override
    public List<PositionDetailRecord> queryList(PositionQuery query) {
        return this.getSqlSession().selectList(NAMESPACE+".queryList",query);
    }

    @Override
    public int updateByPositionCode(String positionCode) {
        return this.getSqlSession().update(NAMESPACE+".updateByPositionCode",positionCode);
    }

    @Override
    public int deleteByBusinessKey(String businessKey) {
        return this.getSqlSession().update(NAMESPACE+".deleteByBusinessKey",businessKey);
    }
}
