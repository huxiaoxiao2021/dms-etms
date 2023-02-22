package com.jd.bluedragon.distribution.jy.dao.group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.group.GroupMemberQueryRequest;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberQuery;

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
     * 根据taskId查询已存在的memberCode列表
     * @param query
     * @return
     */
	public List<String> queryMemberCodeListByTaskId(GroupMemberQueryRequest query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryMemberCodeListByTaskId", query);
	}
    /**
     * 根据taskId查询已存在的member列表
     * @param query
     * @return
     */
	public List<JyTaskGroupMemberEntity> queryMemberListByTaskId(JyTaskGroupMemberQuery query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryMemberListByTaskId", query);
	}
	/**
	 * 自动签退-设置结束时间
	 * @param endData
	 * @param memberCodes
	 * @return
	 */
	public int endWorkByMemberCodeListForAutoSignOut(JyTaskGroupMemberEntity endData, List<String> memberCodes) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("endData", endData);
		params.put("memberCodes", memberCodes);
		return this.getSqlSession().update(NAMESPACE + ".endWorkByMemberCodeListForAutoSignOut", params);
	}
	/**
	 * 结束任务-设置结束时间
	 * @param endData
	 * @param memberCodes
	 * @return
	 */
	public int endWorkByMemberCodeListForEndTask(JyTaskGroupMemberEntity endData, List<String> memberCodes) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("endData", endData);
		params.put("memberCodes", memberCodes);
		return this.getSqlSession().update(NAMESPACE + ".endWorkByMemberCodeListForEndTask", params);
	}	
	/**
	 * 删除小组对应的任务成员信息
	 * @param taskGroupMember
	 * @return
	 */
	public int deleteByMemberCode(JyTaskGroupMemberEntity taskGroupMember) {
		return this.getSqlSession().update(NAMESPACE + ".deleteByMemberCode", taskGroupMember);
	}
}
