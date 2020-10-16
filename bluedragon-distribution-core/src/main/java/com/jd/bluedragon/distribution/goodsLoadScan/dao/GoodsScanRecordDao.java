package com.jd.bluedragon.distribution.goodsLoadScan.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLog;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GoodsScanRecordDao extends BaseDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String namespace = GoodsScanRecordDao.class.getName();


    public int updateGoodsScanRecord(GoodsLoadScanRecord record) {

        return this.getSqlSession().update(namespace + ".goodsRemoveScanning",record);
    }

    public int updateGoodsScan(GoodsLoadScan req) {
        return 0;
    }

    public GoodsLoadScanRecord findExceptionGoodsScanRecord(GoodsLoadScanRecord record) {

        return null;
    }

    public GoodsLoadScan findGoodLoadScan(GoodsLoadScan loadScan) {
        return null;
    }

    public int insert(GoodsLoadScanRecord record) {

        return this.getSqlSession().update(namespace + ".add", record);
    }
}
