package com.jd.bluedragon.distribution.jy.dao.strand;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.strand.JyBizStrandReportDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 拣运-滞留上报明细DAO
 *
 * @author hujiping
 * @date 2023/3/28 3:48 PM
 */
public class JyBizStrandReportDetailDao extends BaseDao<JyBizStrandReportDetailEntity> {

    private final static String NAMESPACE = JyBizStrandReportDetailDao.class.getName();

    public int insert(JyBizStrandReportDetailEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int cancel(JyBizStrandReportDetailEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".cancel", entity);
    }

    public int delete(JyBizStrandReportDetailEntity entity){
        return this.getSqlSession().delete(NAMESPACE + ".delete", entity);
    }

    public JyBizStrandReportDetailEntity queryOneByCondition(JyBizStrandReportDetailEntity condition) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryOneByCondition", condition);
    }
    
    public List<JyBizStrandReportDetailEntity> queryPageListByCondition(Map<String, Object> paramsMap) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryPageListByCondition", paramsMap);
    }
    
    public Integer queryTotalInnerScanNum(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryTotalInnerScanNum", bizId);
    }

    public Integer queryTotalScanNum(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryTotalScanNum", bizId);
    }
}
