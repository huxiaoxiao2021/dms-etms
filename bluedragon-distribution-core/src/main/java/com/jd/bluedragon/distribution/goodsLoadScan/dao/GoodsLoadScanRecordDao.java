package com.jd.bluedragon.distribution.goodsLoadScan.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoodsLoadScanRecordDao extends BaseDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String namespace = GoodsLoadScanRecordDao.class.getName();


    public int updateGoodsScanRecord(GoodsLoadScanRecord record) {

        return this.getSqlSession().update(namespace + ".goodsRemoveScanning",record);
    }



    public GoodsLoadScanRecord findExceptionGoodsScanRecord(GoodsLoadScanRecord record) {

        return null;
    }

    public int insert(GoodsLoadScanRecord record) {

        return this.getSqlSession().update(namespace + ".add", record);
    }
}
