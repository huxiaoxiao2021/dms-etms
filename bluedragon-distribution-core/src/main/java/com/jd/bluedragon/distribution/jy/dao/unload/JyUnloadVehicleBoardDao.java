package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadVehicleBoardEntity;

import java.util.List;

public class JyUnloadVehicleBoardDao extends BaseDao<JyUnloadVehicleBoardEntity> {

    final static String NAMESPACE = JyUnloadVehicleBoardDao.class.getName();

    public int insertSelective(JyUnloadVehicleBoardEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public JyUnloadVehicleBoardEntity selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    public int updateByPrimaryKeySelective(JyUnloadVehicleBoardEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", entity);
    }

    public JyUnloadVehicleBoardEntity selectByBoardCode(String boardCode){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByBoardCode", boardCode);
    }
    /**
     * 按任务查询流向统计数据
     * @param bizId
     * @return
     */
    public List<JyUnloadVehicleBoardEntity> getFlowStatisticsByBizId(String bizId){
        return this.getSqlSession().selectOne(NAMESPACE + ".getFlowStatisticsByBizId", bizId);
    }
    /**
     * 按流向查询板维度统计信息
     * @param jyUnloadVehicleBoardEntity
     * @return
     */
    public List<String> getBoardCodeList(JyUnloadVehicleBoardEntity jyUnloadVehicleBoardEntity){
        return this.getSqlSession().selectOne(NAMESPACE + ".getBoardCodeList", jyUnloadVehicleBoardEntity);
    }
    public List<JyUnloadVehicleBoardEntity> getTaskBoardInfoList(JyUnloadVehicleBoardEntity jyUnloadVehicleBoardEntity){
        return this.getSqlSession().selectOne(NAMESPACE + ".getTaskBoardInfoList", jyUnloadVehicleBoardEntity);
    }

}
