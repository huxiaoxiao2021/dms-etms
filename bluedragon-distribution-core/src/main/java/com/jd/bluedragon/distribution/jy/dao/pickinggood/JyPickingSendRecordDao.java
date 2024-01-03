package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

public class JyPickingSendRecordDao extends BaseDao<JyPickingSendRecordEntity> {
    private final static String NAMESPACE = JyPickingSendRecordDao.class.getName();

    private static final String DB_TABLE_NAME = "jy_picking_send_record";

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

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

    public int insertSelective(JyPickingSendRecordEntity recordEntity) {
        recordEntity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", recordEntity);
    }

    public int updatePickingGoodRecordByWaitScanCode(JyPickingSendRecordEntity updateEntity) {
        return this.getSqlSession().update(NAMESPACE + ".updatePickingGoodRecordByWaitScanCode", updateEntity);
    }

    public JyPickingSendRecordEntity fetchByPackageCodeAndBizId(JyPickingSendRecordEntity recordEntity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".latestPickingRecord", recordEntity);
    }
    //初始化时如果已经存在做更新
    public int initUpdateIfExist(JyPickingSendRecordEntity updateEntity) {
        return this.getSqlSession().update(NAMESPACE + ".initUpdateIfExist", updateEntity);
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