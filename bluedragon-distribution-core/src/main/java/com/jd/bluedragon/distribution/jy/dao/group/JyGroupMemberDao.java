package com.jd.bluedragon.distribution.jy.dao.group;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberEntity;

/**
 * 组成员表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:35
 */
public class JyGroupMemberDao extends BaseDao<JyGroupMemberEntity> {

    final static String NAMESPACE = JyGroupMemberDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyGroupMemberEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
}
