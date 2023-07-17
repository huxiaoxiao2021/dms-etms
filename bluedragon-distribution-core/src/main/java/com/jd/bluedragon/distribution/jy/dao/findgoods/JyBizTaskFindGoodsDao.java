package com.jd.bluedragon.distribution.jy.dao.findgoods;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoods;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoodsQueryDto;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoodsStatisticsDto;
import com.jd.bluedragon.utils.DateHelper;

import java.util.Date;
import java.util.List;

public class JyBizTaskFindGoodsDao extends BaseDao<JyBizTaskFindGoods> {

    private final static String NAMESPACE = JyBizTaskFindGoodsDao.class.getName();

    int deleteByPrimaryKey(Long id){
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    int insert(JyBizTaskFindGoods record){
        return this.getSqlSession().insert(NAMESPACE + ".insert", record);
    }

    int insertSelective(JyBizTaskFindGoods record){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }

    JyBizTaskFindGoods selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    int updateByPrimaryKeySelective(JyBizTaskFindGoods record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }

    int updateByPrimaryKey(JyBizTaskFindGoods record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }

    public JyBizTaskFindGoods findOngoingTaskByWorkGrid(String workGridKey) {
        JyBizTaskFindGoodsQueryDto param = new JyBizTaskFindGoodsQueryDto();
        param.setCreateTimeBegin(DateHelper.addDate(new Date(), -3));
        param.setWorkGridKey(workGridKey);
        //岗位+createTime 索引
        return this.getSqlSession().selectOne(NAMESPACE + ".findOngoingTaskByWorkGrid", param);
    }

    public JyBizTaskFindGoods findByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", bizId);
    }

    public List<JyBizTaskFindGoods> pageFindTaskListByCreateTime(JyBizTaskFindGoodsQueryDto param) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageFindTaskListByCreateTime", param);
    }

    public JyBizTaskFindGoodsStatisticsDto taskStatistics(JyBizTaskFindGoodsQueryDto param) {
        return this.getSqlSession().selectOne(NAMESPACE + ".taskStatistics", param);
    }
}
