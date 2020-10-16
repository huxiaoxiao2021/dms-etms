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

public class GoodsExceptionScanDao extends BaseDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String namespace = GoodsExceptionScanDao.class.getName();


    public int updateGoodsScanRecord(GoodsLoadScanRecord record) {

        return this.getSqlSession().update(namespace + ".goodsRemoveScanning",record);
    }

    public int updateGoodsScan(GoodsExceptionScanningReq req) {
        return 0;
    }

    public GoodsLoadScanRecord findExceptionGoodsScanRecord(GoodsLoadScanRecord record) {
        record.setScanAction(1); //扫描动作：1是装车扫描，0是取消扫描
        return null;
    }

    public GoodsLoadScan findGoodLoadScan(GoodsLoadScan loadScan) {
        return null;
    }
}
