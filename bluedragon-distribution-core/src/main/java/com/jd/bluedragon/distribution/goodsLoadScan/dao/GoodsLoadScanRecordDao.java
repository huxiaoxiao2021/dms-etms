package com.jd.bluedragon.distribution.goodsLoadScan.dao;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsLoadScanRecordDao extends BaseDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String namespace = GoodsLoadScanRecordDao.class.getName();


    public int updateGoodsScanRecordById(GoodsLoadScanRecord record) {

        return this.getSqlSession().update(namespace + ".updateGoodsScanRecordById", record);
    }

    public List<GoodsLoadScanRecord> selectListByCondition(GoodsLoadScanRecord record) {
        record.setYn(GoodsLoadScanConstants.YN_Y);
        return this.getSqlSession().selectList(namespace + ".selectListByCondition", record);
    }

    public GoodsLoadScanRecord findRecordByWaybillCodeAndPackCode(String waybillCode, String packageCode, Long createSiteCode) {
        GoodsLoadScanRecord record = new GoodsLoadScanRecord();
        record.setWayBillCode(waybillCode);
        record.setPackageCode(packageCode);
        record.setYn(Constants.YN_YES);
        record.setCreateSiteCode(createSiteCode);
        return this.getSqlSession().selectOne(namespace + ".selectRecordByWaybillCodeAndPackCode", record);
    }

    public List<String> selectPackageCodesByWaybillCode(Long taskId, String waybillCode) {
        GoodsLoadScanRecord record = new GoodsLoadScanRecord();
        record.setTaskId(taskId);
        record.setWayBillCode(waybillCode);
        record.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD);
        record.setYn(Constants.YN_YES);
        return this.getSqlSession().selectList(namespace + ".selectPackageCodesByWaybillCode", record);
    }

    public Map<String, GoodsLoadScanRecord> findRecordsByBoardCode(Long createSiteCode,List<String> packageCodeList) {
        GoodsLoadScanRecord record = new GoodsLoadScanRecord();
        record.setCreateSiteCode(createSiteCode);
        record.setYn(Constants.YN_YES);
        record.setPackageCodeList(packageCodeList);
        return this.getSqlSession().selectMap(namespace + ".selectRecordsByBoardCode", record, "packageCode");
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

    public int updateScanActionByPackageCodes(GoodsLoadScanRecord record) {
        return this.getSqlSession().update(namespace + ".updateScanActionByPackageCodes", record);
    }

    public int updateScanActionByBoardCode(GoodsLoadScanRecord record) {
        return this.getSqlSession().update(namespace + ".updateScanActionByBoardCode", record);
    }

    public int updateScanActionByTaskIds(GoodsLoadScanRecord record) {
        return this.getSqlSession().update(namespace + ".updateScanActionByTaskIds", record);
    }

    public boolean deleteLoadScanRecordByTaskId(Long taskId) {
        return super.getSqlSession().update(namespace + ".deleteLoadScanRecordByTaskId", taskId) > 0;
    }

    public List<GoodsLoadScanRecord> selectRecordList(GoodsLoadScanRecord record) {
        return this.getSqlSession().selectList(namespace + ".selectRecordList", record);
    }

    public List<GoodsLoadScanRecord> selectRecordByTaskId(Long taskId) {
        return this.getSqlSession().selectList(namespace + ".selectRecordByTaskId", taskId);
    }

    public List<GoodsLoadScanRecord> findGoodsLoadRecordPage(Long taskId, int start, int end) {
        Map<String, Object> map = new HashMap<>();
        map.put("taskId", taskId);
        map.put("start", start);
        map.put("end", end);
        return this.getSqlSession().selectList(namespace + ".findGoodsLoadRecordPage", map);
    }

    //根据任务id查询该任务下有效包裹数据（sql限制yn=1 and scan_action=1）
    public int getPackageCountByTaskId(Long taskId) {
        return this.getSqlSession().selectOne(namespace + ".getPackageCountByTaskId", taskId);
    }

    //删除包裹和任务关系时,先查有没有记录
    public List<GoodsLoadScanRecord> loadScanRecordIsExist(Long taskId) {
        return this.getSqlSession().selectList(namespace + ".loadScanRecordIsExist", taskId);
    }
}
