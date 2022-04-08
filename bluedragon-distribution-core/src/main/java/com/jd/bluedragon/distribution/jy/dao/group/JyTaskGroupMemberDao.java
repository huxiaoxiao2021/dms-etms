package com.jd.bluedragon.distribution.jy.dao.group;

import java.util.List;

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
    /**
     * 批量新增
     * @param taskMembers
     * @return
     */
	public int batchInsert(List<JyTaskGroupMemberEntity> taskMembers) {
		return this.getSqlSession().insert(NAMESPACE + ".batchInsert", taskMembers);
	}
    /**
     * 任务结束-按单个人员
     *
     * @param
     * @return
     */
	public int endWorkByMemberCode(JyTaskGroupMemberEntity entity) {
		return this.getSqlSession().update(NAMESPACE + ".endWorkByMemberCode", entity);
	}
	/**
	 * 任务结束-按任务id
	 * @param taskGroupMember
	 * @return
	 */
	public int endWorkByTaskId(JyTaskGroupMemberEntity taskGroupMember) {
		return this.getSqlSession().update(NAMESPACE + ".endWorkByTaskId", taskGroupMember);
	}
}
