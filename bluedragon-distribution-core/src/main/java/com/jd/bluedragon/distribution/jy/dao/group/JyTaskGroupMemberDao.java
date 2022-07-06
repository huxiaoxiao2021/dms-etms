package com.jd.bluedragon.distribution.jy.dao.group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.group.GroupMemberQueryRequest;
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
    /**
     * 根据taskId查询已存在的memberCode列表
     * @param query
     * @return
     */
	public List<String> queryMemberCodeListByTaskId(GroupMemberQueryRequest query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryMemberCodeListByTaskId", query);
	}
	public int endWorkByMemberCodeList(JyTaskGroupMemberEntity endData, List<String> memberCodes) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("endData", endData);
		params.put("memberCodes", memberCodes);
		return this.getSqlSession().update(NAMESPACE + ".endWorkByMemberCodeList", params);
	}
}
