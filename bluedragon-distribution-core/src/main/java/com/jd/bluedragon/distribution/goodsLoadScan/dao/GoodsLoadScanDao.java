package com.jd.bluedragon.distribution.goodsLoadScan.dao;

import com.google.common.collect.Maps;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GoodsLoadScanDao extends BaseDao<GoodsLoadScan> {

    private static final String NAMESPACE = GoodsLoadScanDao.class.getName();


    public GoodsLoadScan findLoadScanByTaskIdAndWaybillCode(Long taskId, String waybillCode) {
        GoodsLoadScan goodsLoadScan = new GoodsLoadScan();
        goodsLoadScan.setTaskId(taskId);
        goodsLoadScan.setWayBillCode(waybillCode);
        goodsLoadScan.setYn(GoodsLoadScanConstants.YN_Y);
        return super.getSqlSession().selectOne(NAMESPACE + ".selectListByCondition", goodsLoadScan);
    }

    public List<String> findWaybillCodesByTaskId(Long taskId) {
        return super.getSqlSession().selectList(NAMESPACE + ".selectWaybillCodesByTaskId", taskId);
    }

    public List<GoodsLoadScan> findLoadScanByTaskId(Long taskId) {
        return super.getSqlSession().selectList(NAMESPACE + ".selectLoadScanByTaskId", taskId);
    }

    public Integer findWaybillCountByTaskId(Long taskId) {
        return super.getSqlSession().selectOne(NAMESPACE + ".selectWaybillCountByTaskId", taskId);
    }

    public boolean batchInsert(List<GoodsLoadScan> loadScans) {
        return super.getSqlSession().insert(NAMESPACE + ".batchInsert", loadScans) > 0;
    }

    public boolean insert(GoodsLoadScan loadScan) {
        return super.getSqlSession().insert(NAMESPACE + ".add", loadScan) > 0;
    }

    public boolean updateByPrimaryKey(GoodsLoadScan goodsLoadScan) {
        return super.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", goodsLoadScan) > 0;
    }

    public List<GoodsLoadScan> findException(Long taskId, ArrayList<Integer> list) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("taskId", taskId);
        map.put("statusList", list);
        return super.getSqlSession().selectList(NAMESPACE + ".findException", map);

    }

    public boolean deleteLoadScanByTaskId(Long taskId) {
        return super.getSqlSession().update(NAMESPACE + ".deleteLoadScanByTaskId", taskId) > 0;
    }

    // 根据id查询所有运单信息 20201025
    public List<GoodsLoadScan> findAllLoadScanByTaskId(Long taskId) {
        return super.getSqlSession().selectList(NAMESPACE + ".findAllLoadScanByTaskId", taskId);
    }

    //根据任务号 运单号查询装车扫描运单信息  20201025
    public GoodsLoadScan findWaybillInfoByTaskIdAndWaybillCode(Long taskId, String waybillCode) {
        GoodsLoadScan goodsLoadScan = new GoodsLoadScan();
        goodsLoadScan.setTaskId(taskId);
        goodsLoadScan.setWayBillCode(waybillCode);
        goodsLoadScan.setYn(GoodsLoadScanConstants.YN_Y);
        return super.getSqlSession().selectOne(NAMESPACE + ".findWaybillInfoByTaskIdAndWaybillCode", goodsLoadScan);
    }

}


