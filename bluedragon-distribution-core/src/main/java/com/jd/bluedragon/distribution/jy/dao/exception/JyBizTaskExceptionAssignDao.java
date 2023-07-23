package com.jd.bluedragon.distribution.jy.dao.exception;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTaskStatisticsReq;
import com.jd.bluedragon.common.dto.jyexpection.request.StatisticsByGridReq;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByGridDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionAssignEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;

import java.util.List;

public class JyBizTaskExceptionAssignDao extends BaseDao<JyBizTaskExceptionAssignEntity> {


    final static String NAMESPACE = JyBizTaskExceptionAssignDao.class.getName();

    public int insertSelective(JyBizTaskExceptionAssignEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }


    public List<JyBizTaskExceptionAssignEntity> getAssignExpTask(ExpTaskStatisticsReq req){
        return this.getSqlSession().selectList(NAMESPACE + ".getAssignExpTask", req);
    }


    public int updateByIds(List<Long> ids){
        return this.getSqlSession().update(NAMESPACE + ".updateByIds", ids);
    }

}
