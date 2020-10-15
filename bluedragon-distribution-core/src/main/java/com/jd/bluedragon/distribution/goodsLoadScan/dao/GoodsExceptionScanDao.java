package com.jd.bluedragon.distribution.goodsLoadScan.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoodsExceptionScanDao extends BaseDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String namespace = GoodsExceptionScanDao.class.getName();

    public int update(ExportLog detail){
        return this.getSqlSession().update(namespace + ".update",detail);
    }
}
