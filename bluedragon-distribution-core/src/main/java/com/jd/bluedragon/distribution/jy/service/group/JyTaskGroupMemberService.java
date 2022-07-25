package com.jd.bluedragon.distribution.jy.service.group;

import java.util.List;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberQuery;

/**
 * 任务-小组人员明细--Service接口
 * 
 * @author wuyoude
 * @date 2022年04月02日 14:30:43
 *
 */
public interface JyTaskGroupMemberService {
	/**
	 * 添加任务小组成员
	 * @param taskMember
	 */
	Result<Boolean> addTaskMember(JyTaskGroupMemberEntity taskMember);
	/**
	 * 根据小组成员编码结束
	 * @param updateData
	 * @return
	 */
	Result<Boolean> endWorkByMemberCode(JyTaskGroupMemberEntity updateData);
	/**
	 * 开始一个任务
	 * @param startData
	 * @return
	 */
	Result<Boolean> startTask(JyTaskGroupMemberEntity startData);
	/**
	 * 结束一个任务
	 * @param endData
	 * @return
	 */
	Result<Boolean> endTask(JyTaskGroupMemberEntity endData);
	/**
	 * 查询任务下小组成员信息
	 * @param endData
	 * @return
	 */
	Result<List<JyTaskGroupMemberEntity>> queryMemberListByTaskId(JyTaskGroupMemberQuery query);
	/**
	 * 结束多个任务
	 * @param endData
	 * @return
	 */
	Result<Boolean> endWorkByMemberCodeList(JyTaskGroupMemberEntity taskGroupMember, List<String> memberCodes);
}
