package com.jd.bluedragon.distribution.jy.service.group.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.dao.group.JyTaskGroupMemberDao;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberQuery;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberStatusEnum;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;

import lombok.extern.slf4j.Slf4j;

/**
 * 任务-小组人员明细--Service接口实现
 * 
 * @author wuyoude
 * @date 2022年04月02日 14:30:43
 *
 */
@Slf4j
@Service("jyTaskGroupMemberService")
public class JyTaskGroupMemberServiceImpl implements JyTaskGroupMemberService {

	@Autowired
	@Qualifier("jyTaskGroupMemberDao")
	private JyTaskGroupMemberDao jyTaskGroupMemberDao;
	
	@Autowired
	@Qualifier("jyGroupMemberService")
	private JyGroupMemberService jyGroupMemberService;

	@Override
	public Result<Boolean> endWorkByMemberCode(JyTaskGroupMemberEntity updateData) {
		Result<Boolean> result = new Result<Boolean>();
		result.toSuccess();
		jyTaskGroupMemberDao.endWorkByMemberCode(updateData);
		return result;
	}

	@Override
	public Result<Boolean> addTaskMember(JyTaskGroupMemberEntity taskMember) {
		Result<Boolean> result = new Result<Boolean>();
		result.toSuccess();
		jyTaskGroupMemberDao.insert(taskMember);
		return result;
	}

	@Override
	public Result<Boolean> startTask(JyTaskGroupMemberEntity startData) {
		Result<Boolean> result = new Result<Boolean>();
		result.toSuccess();
		//查询小组在岗人员
		JyGroupMemberQuery membersQuery = new JyGroupMemberQuery();
		membersQuery.setRefGroupCode(startData.getRefGroupCode());
		membersQuery.setStatus(JyGroupMemberStatusEnum.IN.getCode());
		List<JyGroupMemberEntity> members = jyGroupMemberService.queryMemberListByGroup(membersQuery);
		List<JyTaskGroupMemberEntity> taskMembers = new ArrayList<JyTaskGroupMemberEntity>();
		Date currentDate = new Date();
		for(JyGroupMemberEntity member: members) {
			JyTaskGroupMemberEntity taskMember = new JyTaskGroupMemberEntity();
			taskMember.setRefGroupMemberCode(member.getMemberCode());
			taskMember.setRefGroupCode(member.getRefGroupCode());
			taskMember.setRefTaskId(startData.getRefTaskId());
			taskMember.setJobCode(member.getJobCode());
			taskMember.setUserCode(member.getUserCode());
			taskMember.setUserName(member.getUserName());
			taskMember.setOrgCode(startData.getOrgCode());
			taskMember.setSiteCode(startData.getSiteCode());
			taskMember.setStartTime(currentDate);
			taskMember.setCreateTime(currentDate);
			taskMember.setCreateUser(startData.getCreateUser());
			taskMember.setCreateUserName(startData.getCreateUserName());
			taskMembers.add(taskMember);
		}
		jyTaskGroupMemberDao.batchInsert(taskMembers);
		return result;
	}

	@Override
	public Result<Boolean> endTask(JyTaskGroupMemberEntity endData) {
		Result<Boolean> result = new Result<Boolean>();
		result.toSuccess();
		Date currentDate = new Date();
		JyTaskGroupMemberEntity taskGroupMember = new JyTaskGroupMemberEntity();
		taskGroupMember.setRefTaskId(endData.getRefTaskId());
		taskGroupMember.setEndTime(currentDate);
		taskGroupMember.setUpdateTime(currentDate);
		taskGroupMember.setUpdateUser(endData.getUpdateUser());
		taskGroupMember.setUpdateUserName(endData.getUpdateUserName());
		jyTaskGroupMemberDao.endWorkByTaskId(taskGroupMember);
		return result;
	}
}
