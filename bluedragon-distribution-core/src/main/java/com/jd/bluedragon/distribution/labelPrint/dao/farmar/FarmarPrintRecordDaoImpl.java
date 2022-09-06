package com.jd.bluedragon.distribution.labelPrint.dao.farmar;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.label.domain.farmar.FarmarEntity;

import java.util.List;

/**
 * 砝码查询DAO
 *
 * @author hujiping
 * @date 2022/2/25 5:48 PM
 */
public class FarmarPrintRecordDaoImpl extends BaseDao<FarmarEntity> implements FarmarPrintRecordDao {

    private final static String NAMESPACE = FarmarPrintRecordDao.class.getName();

    @Override
    public int insert(FarmarEntity record) {
        return this.getSqlSession().insert(NAMESPACE+".insert",record);
    }

    @Override
    public int batchInsert(List<FarmarEntity> list) {
        return this.getSqlSession().insert(NAMESPACE+".batchInsert",list);
    }

    @Override
    public FarmarEntity queryByFarmarCode(String farmarCode) {
        return this.getSqlSession().selectOne(NAMESPACE+".queryByFarmarCode",farmarCode);
    }
}
