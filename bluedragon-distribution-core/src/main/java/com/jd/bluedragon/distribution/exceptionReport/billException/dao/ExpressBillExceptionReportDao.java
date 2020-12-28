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
     * 查询总数
     * @param query
     * @return
     */
    public long queryCount(ExpressBillExceptionReportQuery query) {
        return this.getSqlSession().selectOne(ExpressBillExceptionReportDao.namespace + ".queryCount", query);
    }
}
    
