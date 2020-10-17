package com.jd.bluedragon.distribution.goodsLoadScan.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;

import java.util.List;


public class GoodsLoadScanDao extends BaseDao<GoodsLoadScan> {

    private static final String NAMESPACE = GoodsLoadScanDao.class.getName();


    public GoodsLoadScan findLoadScanByTaskIdAndWaybillCode(Long taskId, String waybillCode) {
        GoodsLoadScan goodsLoadScan = new GoodsLoadScan();
        goodsLoadScan.setTaskId(taskId);
        goodsLoadScan.setWayBillCode(waybillCode);
        return super.getSqlSession().selectOne(NAMESPACE + ".selectListByCondition", goodsLoadScan);
    }

    public List<String> findWaybillCodesByTaskId(Long taskId) {
        return super.getSqlSession().selectOne(NAMESPACE + ".selectWaybillCodesByTaskId", taskId);
    }

    public List<GoodsLoadScan> findLoadScanByTaskId(Long taskId) {
        GoodsLoadScan goodsLoadScan = new GoodsLoadScan();
        goodsLoadScan.setTaskId(taskId);
        return super.getSqlSession().selectList(NAMESPACE + ".selectListByCondition", goodsLoadScan);
    }

    public boolean batchInsert(List<GoodsLoadScan> loadScans) {
        return super.getSqlSession().insert(NAMESPACE + ".batchInsert", loadScans) > 0;
    }

    public boolean insert(GoodsLoadScan loadScan) {
        return super.getSqlSession().insert(NAMESPACE + ".insert", loadScan) > 0;
    }

    public boolean updateByPrimaryKey(GoodsLoadScan goodsLoadScan) {
        return super.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", goodsLoadScan) > 0;
    }

}


