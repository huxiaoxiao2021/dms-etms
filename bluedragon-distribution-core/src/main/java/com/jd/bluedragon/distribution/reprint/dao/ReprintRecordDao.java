package com.jd.bluedragon.distribution.reprint.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.reprint.domain.ReprintRecord;

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


}