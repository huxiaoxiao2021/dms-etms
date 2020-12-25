package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScan;
import java.util.List;


public class UnloadScanDao extends BaseDao<UnloadScan> {

    private static final String NAMESPACE = UnloadScanDao.class.getName();


    public List<UnloadScan> findUnloadScanByBySealCarCode(String sealCarCode) {
        return super.getSqlSession().selectList(NAMESPACE + ".selectUnloadScanBySealCarCode", sealCarCode);
    }

    public boolean insert(UnloadScan unloadScan) {
        return super.getSqlSession().insert(NAMESPACE + ".add", unloadScan) > 0;
    }

    public boolean updateByPrimaryKey(UnloadScan unloadScan) {
        return super.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", unloadScan) > 0;
    }

}


