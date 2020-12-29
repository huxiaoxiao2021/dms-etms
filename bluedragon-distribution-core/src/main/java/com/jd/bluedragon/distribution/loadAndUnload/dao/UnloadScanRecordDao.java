package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScanRecord;


import java.util.List;

public class UnloadScanRecordDao extends BaseDao<UnloadScanRecord> {


    private static final String NAMESPACE = UnloadScanRecordDao.class.getName();


    public boolean insert(UnloadScanRecord record) {
        return this.getSqlSession().update(NAMESPACE + ".add", record) > 0;
    }


    public boolean batchInsertByWaybill(UnloadScanRecord records) {
        return super.getSqlSession().insert(NAMESPACE + ".batchInsertByWaybill", records) > 0;
    }

    public List<UnloadScanRecord> findRecordBySealCarCode(String sealCarCode) {
        return this.getSqlSession().selectList(NAMESPACE + ".selectRecordBySealCarCode", sealCarCode);
    }


}
