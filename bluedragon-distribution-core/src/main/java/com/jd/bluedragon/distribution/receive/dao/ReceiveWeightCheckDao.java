package com.jd.bluedragon.distribution.receive.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;

import java.util.List;

/**
 * @ClassName: ReceiveWeightCheckDao
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/2/28 18:03
 */
public class ReceiveWeightCheckDao extends BaseDao<ReceiveWeightCheckResult> {

    public static final String namespace = ReceiveWeightCheckDao.class.getName();

    /**
     * 新增
     * */
    public int insert(ReceiveWeightCheckResult receiveWeightCheckResult){
        return this.getSqlSession().insert(namespace + ".insert",receiveWeightCheckResult);
    }

    /**
     * 根据条件查询
     * */
    public List<ReceiveWeightCheckResult> queryByCondition(ReceiveWeightCheckCondition condition){
        return this.getSqlSession().selectList(namespace + ".queryByCondition",condition);
    }

    public List<WeightAndVolumeCheck> queryByCondition1(WeightAndVolumeCheckCondition condition){
        return this.getSqlSession().selectList(namespace + ".queryByCondition",condition);
    }

    /**
     * 根据条件查询数据条数
     * */
    public Integer queryNumByCondition(ReceiveWeightCheckCondition condition) {
        return this.getSqlSession().selectOne(namespace + ".pageNum_queryByPagerCondition",condition);
    }

    public Integer queryNumByCondition1(WeightAndVolumeCheckCondition condition) {
        return this.getSqlSession().selectOne(namespace + ".pageNum_queryByPagerCondition",condition);
    }

    /**
     * 根据包裹号查询数据
     * @param packageCode
     * @return
     */
    public ReceiveWeightCheckResult queryByPackageCode(String packageCode){
        return this.getSqlSession().selectOne(namespace + ".queryByPackageCode",packageCode);
    }

    /**
     * 更新数据
     * */
    public int updateByPackageCode(ReceiveWeightCheckResult receiveWeight){
        return this.getSqlSession().update(namespace + ".updateByPackageCode",receiveWeight);
    }
}
