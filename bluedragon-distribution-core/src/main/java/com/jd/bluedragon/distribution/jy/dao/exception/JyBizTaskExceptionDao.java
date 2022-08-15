package com.jd.bluedragon.distribution.jy.dao.exception;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByStatusDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;

import java.util.List;

public class JyBizTaskExceptionDao  extends BaseDao<JyBizTaskExceptionEntity> {


    final static String NAMESPACE = JyBizTaskExceptionDao.class.getName();
    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyBizTaskExceptionEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    /**
     * 根据bizID查询任务详情
     * @param bizId
     * @return
     */
    public JyBizTaskExceptionEntity findByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", bizId);
    }

    public int updateByBizId(JyBizTaskExceptionEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByBizId", entity);
    }
    public List<StatisticsByStatusDto> getStatusStatistic(String gridRefId){
        return this.getSqlSession().selectList(NAMESPACE + ".getStatusStatistic", gridRefId);
    }

}
