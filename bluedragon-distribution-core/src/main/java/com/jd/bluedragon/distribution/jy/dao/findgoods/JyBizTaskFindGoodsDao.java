package com.jd.bluedragon.distribution.jy.dao.findgoods;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.inventory.InventoryTaskDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.FindGoodsTaskDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.FindGoodsTaskQueryDto;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoods;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoodsDetail;
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

    public int insertSelective(JyBizTaskFindGoods record){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }

    public JyBizTaskFindGoods selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    public int updateByPrimaryKeySelective(JyBizTaskFindGoods record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }

    public int updateByPrimaryKey(JyBizTaskFindGoods record) {
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

    public int updatePhotoStatus(JyBizTaskFindGoods dbUpdate) {
        return this.getSqlSession().update(NAMESPACE + ".updatePhotoStatus", dbUpdate);
    }

  public JyBizTaskFindGoods findWaveTask(FindGoodsTaskQueryDto dto) {
      return this.getSqlSession().selectOne(NAMESPACE + ".findWaveTask", dto);

  }

    public int batchInsert(List<JyBizTaskFindGoodsDetail> findGoodsDetailList) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", findGoodsDetailList);
    }
}
