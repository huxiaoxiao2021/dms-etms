package com.jd.bluedragon.distribution.reprint.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.reprint.domain.ReprintRecord;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordQuery;

import java.util.List;

public class ReprintRecordDao extends BaseDao<ReprintRecord> {

    public static final String namespace = ReprintRecordDao.class.getName();

    /**
     *  插入
     *
     * @param rePrintRecord 补打记录实体
     * */
    public int add(ReprintRecord rePrintRecord) {
        return this.getSqlSession().insert(namespace + ".add", rePrintRecord);
    }

    /**
     * 查询补打记录次数
     *
     * @param barCode 条码号
     * */
    public int getCountByCondition(String barCode) {
        return this.getSqlSession().selectOne(namespace + ".getCountByCondition", barCode);
    }

    /**
     * 统计行数
     * @param query 请求参数
     * @return
     */
    public long queryCount(ReprintRecordQuery query) {
        return this.getSqlSession().selectOne(namespace + ".queryCount", query);
    }

    /**
     * 查询列表
     * @param query
     * @return
     */
    public List<ReprintRecord> queryList(ReprintRecordQuery query) {
        return this.getSqlSession().selectList(namespace + ".queryList", query);
    }

}