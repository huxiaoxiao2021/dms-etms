package com.jd.bluedragon.distribution.goodsLoadScan.dao;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GoodsLoadScanRecordDao extends BaseDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String namespace = GoodsLoadScanRecordDao.class.getName();


    public int updateGoodsScanRecordById(GoodsLoadScanRecord record) {

        return this.getSqlSession().update(namespace + ".updateGoodsScanRecordById",record);
    }

    public List<GoodsLoadScanRecord> selectListByCondition(GoodsLoadScanRecord record) {
        record.setYn(GoodsLoadScanConstants.YN_Y);
        return this.getSqlSession().selectList(namespace + ".selectListByCondition",record);
    }

    public GoodsLoadScanRecord findLoadScanRecordByTaskIdAndWaybillCodeAndPackCode(GoodsLoadScanRecord record) {

        return this.getSqlSession().selectOne(namespace + ".selectListByCondition",record);
    }

    public GoodsLoadScanRecord findLoadScanRecordByTaskIdAndBoardCode(Long taskId, String boardCode) {
        GoodsLoadScanRecord record = new GoodsLoadScanRecord();
        record.setTaskId(taskId);
        record.setBoardCode(boardCode);
        record.setYn(Constants.YN_YES);
        return this.getSqlSession().selectOne(namespace + ".selectListByCondition",record);
    }

    public int insert(GoodsLoadScanRecord record) {

        return this.getSqlSession().update(namespace + ".add", record);
    }

    public boolean batchInsert(List<GoodsLoadScanRecord> records) {
        return super.getSqlSession().insert(namespace + ".batchInsert", records) > 0;
    }

    public int updatePackageForceStatus(GoodsLoadScanRecord record) {
        return this.getSqlSession().update(namespace + ".updatePackageForceStatus",record);
    }
}
