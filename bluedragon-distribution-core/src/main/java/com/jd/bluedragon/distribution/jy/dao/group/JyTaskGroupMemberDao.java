package com.jd.bluedragon.distribution.jy.dao.group;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;

/**
 * 任务-小组人员明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:35
 */
public class JyTaskGroupMemberDao extends BaseDao<JyTaskGroupMemberEntity> {

    final static String NAMESPACE = JyTaskGroupMemberDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyTaskGroupMemberEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
}
