package com.jd.bluedragon.distribution.box.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.box.domain.Box;

import java.util.List;

/**
 * @ClassName: GroupBoxDao
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2018/12/11 21:50
 */
public class GroupBoxDao extends BaseDao<Box> {

    public static final String namespace = GroupBoxDao.class.getName();


    public Integer batchAdd(List<Box> groupList) {
        return this.getSqlSession().insert(namespace + ".batchAdd", groupList);
    }

    public Box getBoxInfoByBoxCode(String boxCode) {
        return this.getSqlSession().selectOne(namespace + ".getBoxInfoByBoxCode",boxCode);
    }

    public List<Box> getAllBoxByGroupSendCode(String groupSendCode) {
        return this.getSqlSession().selectList(namespace + ".getAllBoxByGroupSendCode",groupSendCode);
    }
}
