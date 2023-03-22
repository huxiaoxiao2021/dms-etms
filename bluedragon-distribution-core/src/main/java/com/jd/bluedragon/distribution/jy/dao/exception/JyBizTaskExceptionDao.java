package com.jd.bluedragon.distribution.jy.dao.exception;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTaskPageReq;
import com.jd.bluedragon.common.dto.jyexpection.request.StatisticsByGridReq;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByGridDto;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByStatusDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionAgg;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JyBizTaskExceptionDao  extends BaseDao<JyBizTaskExceptionEntity> {


    final static String NAMESPACE = JyBizTaskExceptionDao.class.getName();

    public int insertSelective(JyBizTaskExceptionEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
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
    public List<StatisticsByStatusDto> getCommonStatusStatistic(String gridRefId){
        return this.getSqlSession().selectList(NAMESPACE + ".getCommonStatusStatistic", gridRefId);
    }
    public List<StatisticsByStatusDto> getSpecialStatusStatistic(String gridRefId,String handleErp){
        Map<String,Object> params = new HashMap<>();
        params.put("gridRefId",gridRefId);
        params.put("handleErp",handleErp);
        return this.getSqlSession().selectList(NAMESPACE + ".getSpecialStatusStatistic", params);
    }

    public List<StatisticsByStatusDto> getCompleteStatusStatistic(String gridRefId,int limitDay){
        Map<String,Object> params = new HashMap<>();
        params.put("gridRefId",gridRefId);
        params.put("limitDay",limitDay);
        return this.getSqlSession().selectList(NAMESPACE + ".getCompleteStatusStatistic", params);
    }

    /**
     * 按网格统计带取件数量
     */
    public List<StatisticsByGridDto> getStatisticsByGrid(StatisticsByGridReq entity){
        return this.getSqlSession().selectList(NAMESPACE + ".getStatisticsByGrid", entity);
    }

    /**
     * 按网格查询标签
     */
    public List<JyBizTaskExceptionEntity> getTagsByGrid(StatisticsByGridReq entity){
        return this.getSqlSession().selectList(NAMESPACE + ".getTagsByGrid", entity);
    }


    public List<JyBizTaskExceptionEntity> queryExceptionTaskList(ExpTaskPageReq entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryExceptionTaskList", entity);
    }

    public List<JyExceptionAgg> queryUnCollectAndOverTimeAgg(Map<String, Object> params) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryUnCollectAndOverTimeAgg", params);
    }

    /**
     * 根据时间查询报废处理人erp
     * 
     * @param params
     * @return
     */
    public List<String> queryScrapHandlerErp(Map<String, Date> params) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryScrapHandlerErp", params);
    }

    /**
     * 根据条件查询报废详情
     * 
     * @param params
     * @return
     */
    public List<JyBizTaskExceptionEntity> queryScrapDetailByCondition(Map<String, Object> params) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryScrapDetailByCondition", params);
    }

    /**
     * 根据条件查询报废数量
     * 
     * @param entity
     * @return
     */
    public Integer queryScrapCountByCondition(JyBizTaskExceptionEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryScrapCountByCondition", entity);
    }
}
