package com.jd.bluedragon.distribution.exceptionReport.billException.dao;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.exceptionReport.billException.domain.ExpressBillExceptionReport;
import com.jd.bluedragon.distribution.exceptionReport.billException.request.ExpressBillExceptionReportQuery;

import java.util.List;

/**
 * @Author: liming522
 * @Description: 面单异常举报 dao
 * @Date: create in 2020/12/21 14:42
 */
public class ExpressBillExceptionReportDao extends BaseDao<ExpressBillExceptionReport> {


    public static final String namespace = ExpressBillExceptionReportDao.class.getName();

    /**
     * 查询包裹举报记录数
     * @param packageCode
     * @return
     */
    public Integer selectPackageIsReport(String packageCode) {
        return this.getSqlSession().selectOne(ExpressBillExceptionReportDao.namespace + ".selectPackageIsReport", packageCode);
    }

    /**
     * 查询最近一条记录
     *
     * @param packageCode
     * @return
     */
    public ExpressBillExceptionReport selectOneRecent(String packageCode) {
        return this.getSqlSession().selectOne(ExpressBillExceptionReportDao.namespace + ".selectOneRecent", packageCode);
    }

    /**
     * 新增举报数据
     * @param record
     * @return
     */
    public Integer insertReport(ExpressBillExceptionReport record) {
        return this.getSqlSession().insert(ExpressBillExceptionReportDao.namespace + ".insertReport", record);
    }

    /**
     * 查询列表
     * @param query
     * @return
     */
    public List<ExpressBillExceptionReport> queryList(ExpressBillExceptionReportQuery query) {
        return this.getSqlSession().selectList(ExpressBillExceptionReportDao.namespace + ".queryList", query);
    }

    /**
     * 分页查询（解决深分页）
     * @param query
     * @return
     */
    public List<ExpressBillExceptionReport> newQueryList(ExpressBillExceptionReportQuery query) {
        return this.getSqlSession().selectList(ExpressBillExceptionReportDao.namespace + ".newQueryList", query);
    }

    /**
     * 查询总数
     * @param query
     * @return
     */
    public long queryCount(ExpressBillExceptionReportQuery query) {
        return this.getSqlSession().selectOne(ExpressBillExceptionReportDao.namespace + ".queryCount", query);
    }

    public int updateByBusiCode(ExpressBillExceptionReport query) {
        return this.getSqlSession().update(ExpressBillExceptionReportDao.namespace + ".updateByBusiCode", query);
    }
}
    
