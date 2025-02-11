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

    public JyPickingSendRecordEntity latestPickingRecord(JyPickingSendRecordEntity recordEntity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".latestPickingRecord", recordEntity);
    }


    public int insertSelective(JyPickingSendRecordEntity recordEntity) {
        recordEntity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", recordEntity);
    }

    public int fillRealScanField(JyPickingSendRecordEntity updateEntity) {
        return this.getSqlSession().update(NAMESPACE + ".fillRealScanField", updateEntity);
    }

    public JyPickingSendRecordEntity fetchByPackageCodeAndCondition(JyPickingSendRecordEntity recordEntity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".fetchByPackageCodeAndCondition", recordEntity);
    }
    //初始化时如果已经存在做更新
    public int fillInitWaitScanField(JyPickingSendRecordEntity updateEntity) {
        return this.getSqlSession().update(NAMESPACE + ".fillInitWaitScanField", updateEntity);
    }

}