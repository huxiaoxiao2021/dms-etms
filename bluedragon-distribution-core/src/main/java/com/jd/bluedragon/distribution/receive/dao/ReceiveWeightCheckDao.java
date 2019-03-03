package com.jd.bluedragon.distribution.receive.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckResult;

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

}
