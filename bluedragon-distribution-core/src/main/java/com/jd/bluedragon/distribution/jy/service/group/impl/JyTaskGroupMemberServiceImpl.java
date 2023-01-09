package com.jd.bluedragon.distribution.jy.service.group.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.group.GroupMemberQueryRequest;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.jy.dao.group.JyTaskGroupMemberDao;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberQuery;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberStatusEnum;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberQuery;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.jsf.gd.util.StringUtils;

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
	private static final Logger logger = LoggerFactory.getLogger(JyTaskGroupMemberServiceImpl.class);
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
		if(startData == null 
				|| StringUtils.isBlank(startData.getRefGroupCode())
				|| StringUtils.isBlank(startData.getRefTaskId())) {
			logger.warn("startTask-参数错误！groupCode和taskId不能为空！startData={}",JsonHelper.toJson(startData));
			result.toFail("startTask-参数错误！groupCode和taskId不能为空！");
			return  result;
		}
		//查询小组在岗人员
		JyGroupMemberQuery membersQuery = new JyGroupMemberQuery();
		membersQuery.setGroupCode(startData.getRefGroupCode());
		membersQuery.setStatus(JyGroupMemberStatusEnum.IN.getCode());
		List<JyGroupMemberEntity> members = jyGroupMemberService.queryMemberListByGroup(membersQuery);
		if(CollectionUtils.isEmpty(members)) {
			logger.info("startTask:taskId={}在岗人员列表为空，无需处理！",startData.getRefTaskId());
		}else {
			logger.info("startTask:taskId={}添加任务小组成员{}个！",startData.getRefTaskId(),members.size());
		}
		List<JyTaskGroupMemberEntity> taskMembers = new ArrayList<JyTaskGroupMemberEntity>();
		GroupMemberQueryRequest memberCodeListQuery = new GroupMemberQueryRequest();
		memberCodeListQuery.setTaskId(startData.getRefTaskId());
		//查询任务下已存在的MemberCode
		List<String> memberCodeList = jyTaskGroupMemberDao.queryMemberCodeListByTaskId(memberCodeListQuery);
		boolean checkExist = !CollectionUtils.isEmpty(memberCodeList);
		Date currentDate = new Date();
		for(JyGroupMemberEntity member: members) {
			//同一个任务一个MemberCode只能对应一条记录
			if(checkExist && memberCodeList.contains(member.getMemberCode())) {
				continue;
			}
			JyTaskGroupMemberEntity taskMember = new JyTaskGroupMemberEntity();
			taskMember.setMemberType(member.getMemberType());
			taskMember.setMachineCode(member.getMachineCode());
			taskMember.setDeviceTypeCode(member.getDeviceTypeCode());
			taskMember.setDeviceTypeName(member.getDeviceTypeName());
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
		if(taskMembers.size() > 0) {
			jyTaskGroupMemberDao.batchInsert(taskMembers);
		}
		return result;
	}

	@Override
	public Result<Boolean> endTask(JyTaskGroupMemberEntity endData) {
		Result<Boolean> result = new Result<Boolean>();
		result.toSuccess();
		Date currentDate = new Date();
		JyTaskGroupMemberEntity taskGroupMember = new JyTaskGroupMemberEntity();
		taskGroupMember.setRefTaskId(endData.getRefTaskId());
		if(endData.getUpdateTime() != null) {
			taskGroupMember.setEndTime(endData.getUpdateTime());
			taskGroupMember.setUpdateTime(endData.getUpdateTime());
		}else {
			taskGroupMember.setEndTime(currentDate);
			taskGroupMember.setUpdateTime(currentDate);
		}
		taskGroupMember.setUpdateUser(endData.getUpdateUser());
		taskGroupMember.setUpdateUserName(endData.getUpdateUserName());
		
		GroupMemberQueryRequest memberCodeListQuery = new GroupMemberQueryRequest();
		memberCodeListQuery.setTaskId(endData.getRefTaskId());
		//查询任务下MemberCode
		List<String> memberCodeList = jyTaskGroupMemberDao.queryMemberCodeListByTaskId(memberCodeListQuery);
		
		if(!CollectionUtils.isEmpty(memberCodeList)) {
			List<List<String>> memberCodeGroupList = CollectionHelper.splitList(memberCodeList, Integer.MAX_VALUE, Constants.DB_SQL_IN_LIMIT_NUM);
			for(List<String> memberCodes : memberCodeGroupList) {
				//查询在岗人员MemberCode
				List<String> unSignOutMemberCodeList = jyGroupMemberService.queryUnSignOutMemberCodeList(memberCodes);
				if(CollectionUtils.isEmpty(unSignOutMemberCodeList)) {
					logger.info("endTask:taskId={}在岗人员列表为空，无需处理！",endData.getRefTaskId());
					continue;
				}else {
					logger.info("endTask:taskId={}设置enTime,memberCodes={}！",endData.getRefTaskId(),JsonHelper.toJson(unSignOutMemberCodeList));
				}
				jyTaskGroupMemberDao.endWorkByMemberCodeListForEndTask(taskGroupMember,unSignOutMemberCodeList);
			}
		}else {
			logger.info("endTask:taskId={}人员列表为空，无需处理！",endData.getRefTaskId());
		}
		return result;
	}

	@Override
	public Result<List<JyTaskGroupMemberEntity>> queryMemberListByTaskId(JyTaskGroupMemberQuery query) {
		Result<List<JyTaskGroupMemberEntity>> result = new Result<>();
		result.toSuccess();
		result.setData(jyTaskGroupMemberDao.queryMemberListByTaskId(query));
		return result;
	}

	@Override
	public Result<Boolean> endWorkByMemberCodeListForAutoSignOut(JyTaskGroupMemberEntity endData, List<String> memberCodes) {
		Result<Boolean> result = new Result<Boolean>();
		result.toSuccess();
		jyTaskGroupMemberDao.endWorkByMemberCodeListForAutoSignOut(endData,memberCodes);
		return result;
	}

	@Override
	public Result<Boolean> deleteByMemberCode(JyTaskGroupMemberEntity taskGroupMember) {
		Result<Boolean> result = new Result<Boolean>();
		result.toSuccess();
		jyTaskGroupMemberDao.deleteByMemberCode(taskGroupMember);
		return result;
	}
}
