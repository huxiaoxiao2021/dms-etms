package com.jd.bluedragon.distribution.spare.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.spare.domain.SpareSortingRecord;

import java.util.HashMap;
import java.util.Map;

public class SpareSortingRecordDao extends BaseDao<SpareSortingRecord> {

    public static final String namespace = SpareSortingRecordDao.class.getName();

    /**
     * 新增
     * */
    public int insert(SpareSortingRecord spareSortingRecord) {
        return this.getSqlSession().insert(namespace + ".insert", spareSortingRecord);
    }

    /**
     * 查询最后一次记录
     *
     * @param createSiteCode 始发站点编号
     * @param waybillCode 运单号
     * */
    public SpareSortingRecord getLastRecord(Integer createSiteCode, String waybillCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("createSiteCode", createSiteCode);
        params.put("waybillCode", waybillCode);
        return this.getSqlSession().selectOne(namespace + ".getLastRecord", params);
    }

}