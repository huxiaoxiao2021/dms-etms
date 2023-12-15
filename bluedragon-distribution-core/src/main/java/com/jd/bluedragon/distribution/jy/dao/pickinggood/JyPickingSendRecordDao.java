package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;

public class JyPickingSendRecordDao extends BaseDao<JyPickingSendRecordEntity> {
    private final static String NAMESPACE = JyPickingSendRecordDao.class.getName();

    public String fetchWaitPickingBizIdByBarCode(JyPickingSendRecordEntity recordEntity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".fetchWaitPickingBizIdByBarCode", recordEntity);
    }

    public JyPickingSendRecordEntity fetchRealPickingRecordByBarCodeAndBizId(JyPickingSendRecordEntity recordEntity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".fetchRealPickingRecordByBarCodeAndBizId", recordEntity);
    }

    public JyPickingSendRecordEntity latestPickingRecord(JyPickingSendRecordEntity recordEntity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".latestPickingRecord", recordEntity);
    }

    /**
     * 任务待扫、待发件数
     * @return
     */
    public Integer countTaskWaitScanItemNum(JyPickingSendRecordEntity recordEntity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countTaskWaitScanItemNum", recordEntity);
    }

    /**
     * 任务待扫数量
     * @param recordEntity
     * @return
     */
    public Integer countTaskRealScanItemNum(JyPickingSendRecordEntity recordEntity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countTaskRealScanItemNum", recordEntity);
    }
//
//    int deleteByPrimaryKey(Long id);
//
//    int insert(JyPickingSendRecord record);
//
//    int insertSelective(JyPickingSendRecord record);
//
//    JyPickingSendRecord selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(JyPickingSendRecord record);
//
//    int updateByPrimaryKey(JyPickingSendRecord record);
}