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

    public List<String> findPackageCodesBySealCarCode(String sealCarCode) {
        return this.getSqlSession().selectList(NAMESPACE + ".selectPackageCodesBySealCarCode", sealCarCode);
    }

    public UnloadScanRecord findRecordBySealAndPackCode(String sealCarCode, String packageCode) {
        UnloadScanRecord unloadScanRecord = new UnloadScanRecord();
        unloadScanRecord.setSealCarCode(sealCarCode);
        unloadScanRecord.setPackageCode(packageCode);
        return this.getSqlSession().selectOne(NAMESPACE + ".selectRecordBySealAndPackCode", unloadScanRecord);
    }

    public List<UnloadScanRecord> findRecordsBySealAndWaybillCode(String sealCarCode, String waybillCode) {
        UnloadScanRecord unloadScanRecord = new UnloadScanRecord();
        unloadScanRecord.setSealCarCode(sealCarCode);
        unloadScanRecord.setWaybillCode(waybillCode);
        return this.getSqlSession().selectList(NAMESPACE + ".selectRecordsBySealAndWaybillCode", unloadScanRecord);
    }

}
