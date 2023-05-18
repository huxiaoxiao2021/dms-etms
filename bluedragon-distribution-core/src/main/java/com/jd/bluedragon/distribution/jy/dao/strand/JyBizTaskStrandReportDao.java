package com.jd.bluedragon.distribution.jy.dao.strand;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.strand.JyStrandTaskPageCondition;
import com.jd.bluedragon.distribution.jy.strand.JyBizTaskStrandReportEntity;

import java.util.List;

/**
 * 拣运-滞留上报任务DAO
 *
 * @author hujiping
 * @date 2023/3/28 3:48 PM
 */
public class JyBizTaskStrandReportDao extends BaseDao<JyBizTaskStrandReportEntity> {

    private final static String NAMESPACE = JyBizTaskStrandReportDao.class.getName();

    public int insert(JyBizTaskStrandReportEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int updateStatus(JyBizTaskStrandReportEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateStatus", entity);
    }

    public int overTimeBatchUpdate(List<String> bizIdList){
        return this.getSqlSession().update(NAMESPACE + ".overTimeBatchUpdate", bizIdList);
    }

    public int delete(JyBizTaskStrandReportEntity entity){
        return this.getSqlSession().delete(NAMESPACE + ".delete", entity);
    }

    public JyBizTaskStrandReportEntity queryOneByBiz(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryOneByBiz", bizId);
    }

    public List<JyBizTaskStrandReportEntity> queryPageListByCondition(JyStrandTaskPageCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryPageListByCondition", condition);
    }

    public Integer queryTotalCondition(JyStrandTaskPageCondition condition) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryTotalCondition", condition);
    }

    public JyBizTaskStrandReportEntity queryOneByTransportRejectBiz(String transPlanCode) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryOneByTransportRejectBiz", transPlanCode);
    }
}
