package com.jd.bluedragon.distribution.receive.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.receive.domain.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.WeightAndVolumeCheck;

import java.util.List;

/**
 * @ClassName: ReceiveWeightCheckDao
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/2/28 18:03
 */
public class ReceiveWeightCheckDao extends BaseDao<WeightAndVolumeCheck> {

    public static final String namespace = ReceiveWeightCheckDao.class.getName();

    /**
     * 新增
     * */
    public int insert(WeightAndVolumeCheck weightAndVolumeCheck){
        return this.getSqlSession().insert(namespace + ".insert", weightAndVolumeCheck);
    }

    /**
     * 根据条件查询
     * */
    public List<WeightAndVolumeCheck> queryByCondition(WeightAndVolumeCheckCondition condition){
        return this.getSqlSession().selectList(namespace + ".queryByCondition",condition);
    }

    /**
     * 根据条件查询数据条数
     * */
    public Integer queryNumByCondition(WeightAndVolumeCheckCondition condition) {
        return this.getSqlSession().selectOne(namespace + ".pageNum_queryByPagerCondition",condition);
    }

    /**
     * 根据包裹号查询数据
     * @param packageCode
     * @return
     */
    public WeightAndVolumeCheck queryByPackageCode(String packageCode){
        return this.getSqlSession().selectOne(namespace + ".queryByPackageCode",packageCode);
    }

    /**
     * 更新数据
     * */
    public int updateByPackageCode(WeightAndVolumeCheck receiveWeight){
        return this.getSqlSession().update(namespace + ".updateByPackageCode",receiveWeight);
    }
}
