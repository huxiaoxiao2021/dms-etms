package com.jd.bluedragon.distribution.bagException.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.bagException.domain.CollectionBagExceptionReport;
import com.jd.bluedragon.distribution.bagException.request.CollectionBagExceptionReportQuery;

import java.util.List;

/**
 * 集包异常举报
 *
 * @author fanggang7
 * @time 2020-09-23 21:47:15 周三
 */
public class CollectionBagExceptionReportDao extends BaseDao<CollectionBagExceptionReport> {

    public static final String namespace = CollectionBagExceptionReportDao.class.getName();

    /**
     * 新增
     * @param record
     * @return
     */
    public int insertSelective(CollectionBagExceptionReport record) {
        return this.getSqlSession().insert(CollectionBagExceptionReportDao.namespace + ".insertSelective", record);
    }

    /**
     * 批量新增
     * @param recordList
     * @return
     */
    public int batchInsertSelective(List<CollectionBagExceptionReport> recordList) {
        return this.getSqlSession().insert(CollectionBagExceptionReportDao.namespace + ".batchInsertSelective", recordList);
    }

    /**
     * 统计行数
     * @param query 请求参数
     * @return
     */
    public long queryCount(CollectionBagExceptionReportQuery query) {
        return this.getSqlSession().selectOne(CollectionBagExceptionReportDao.namespace + ".queryCount", query);
    }

    /**
     * 按主键查询
     * @param id 请求参数
     * @return
     */
    public CollectionBagExceptionReport selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(CollectionBagExceptionReportDao.namespace + ".selectByPrimaryKey", id);
    }

    /**
     * 查询列表
     * @param query
     * @return
     */
    public List<CollectionBagExceptionReport> queryList(CollectionBagExceptionReportQuery query) {
        return this.getSqlSession().selectList(CollectionBagExceptionReportDao.namespace + ".queryList", query);
    }

    /**
     * 按主键查询
     * @param record 请求参数
     * @return
     */
    public int updateByPrimaryKey(CollectionBagExceptionReport record) {
        return this.getSqlSession().update(CollectionBagExceptionReportDao.namespace + ".updateByPrimaryKey", record);
    }
}
