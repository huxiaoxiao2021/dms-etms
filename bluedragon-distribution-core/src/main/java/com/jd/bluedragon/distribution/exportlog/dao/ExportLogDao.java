package com.jd.bluedragon.distribution.exportlog.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLog;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLogCondition;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;


import java.util.List;

public class ExportLogDao extends BaseDao<ExportLog> {

    public static final String namespace = ExportLogDao.class.getName();
    /**
     * 新增
     * @param detail
     * @return*/
    public int add(ExportLog detail){
        return this.getSqlSession().insert(namespace + ".add",detail);
    }
    /**
     * 更新
     * @param detail
     * @return*/
    public int update(ExportLog detail){
        return this.getSqlSession().update(namespace + ".update",detail);
    }
    /**
     * 删除
     * @param id
     * @return*/
    public int delete(Long id){
        return this.getSqlSession().delete(namespace + ".deleteById",id);
    }

    /**
     * 查询
     * @param condition
     * @return*/
    public List<ExportLog> queryByCondition(ExportLogCondition condition){
        return this.getSqlSession().selectList(namespace + ".queryByPagerCondition",condition);
    }

    /**
     * 查询总数
     * @param condition
     * @return*/
    public Integer queryCountByCondition(ExportLogCondition condition){
        return this.getSqlSession().selectOne(namespace + ".queryCountByCondition",condition);
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public ExportLog getById(Long id) {
        return this.get(namespace, id);
    }

}
