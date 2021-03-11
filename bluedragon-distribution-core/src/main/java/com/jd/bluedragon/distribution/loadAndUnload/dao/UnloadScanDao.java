package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScan;

import java.util.List;


public class UnloadScanDao extends BaseDao<UnloadScan> {

    private static final String NAMESPACE = UnloadScanDao.class.getName();


    public List<UnloadScan> findUnloadScanBySealCarCode(String sealCarCode) {
        return super.getSqlSession().selectList(NAMESPACE + ".selectUnloadScanBySealCarCode", sealCarCode);
    }

    public UnloadScan findUnloadBySealAndWaybillCode(String sealCarCode, String waybillCode) {
        UnloadScan unloadScan = new UnloadScan();
        unloadScan.setWaybillCode(waybillCode);
        unloadScan.setSealCarCode(sealCarCode);
        return super.getSqlSession().selectOne(NAMESPACE + ".selectUnloadByBySealAndWaybillCode", unloadScan);
    }

    public boolean insert(UnloadScan unloadScan) {
        return super.getSqlSession().insert(NAMESPACE + ".add", unloadScan) > 0;
    }

    public boolean batchInsert(UnloadScan records) {
        return super.getSqlSession().insert(NAMESPACE + ".batchInsert", records) > 0;
    }

    public boolean updateByPrimaryKey(UnloadScan unloadScan) {
        return super.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", unloadScan) > 0;
    }

}


