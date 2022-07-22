package com.jd.bluedragon.distribution.jy.service.group.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.group.GroupMemberData;
import com.jd.bluedragon.common.dto.group.GroupMemberQueryRequest;
import com.jd.bluedragon.common.dto.group.GroupMemberRequest;
import com.jd.bluedragon.common.dto.group.JyGroupMemberCountData;
import com.jd.bluedragon.common.dto.group.JyGroupMemberData;
import com.jd.bluedragon.common.dto.group.JyGroupMemberResponse;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.dao.group.JyGroupMemberDao;
import com.jd.bluedragon.distribution.jy.group.JyGroupEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberQuery;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberStatusEnum;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberTypeEnum;
import com.jd.bluedragon.distribution.jy.group.JyGroupQuery;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.position.domain.PositionDetailRecord;
import com.jd.bluedragon.distribution.position.service.PositionRecordService;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ql.dms.print.utils.StringHelper;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskDistributionTypeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 人员签到表--Service接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Slf4j
@Service("jyGroupMemberService")
public class JyGroupMemberServiceImpl implements JyGroupMemberService {

	
	@Autowired
	@Qualifier("jyGroupMemberDao")
	private JyGroupMemberDao jyGroupMemberDao;
	
	@Autowired
	@Qualifier("jyGroupService")
	JyGroupService jyGroupService;
	
	@Autowired
	@Qualifier("userSignRecordService")
	private UserSignRecordService userSignRecordService;
	
	@Autowired
	private PositionRecordService positionRecordService;
	
	@Autowired
	@Qualifier("jyTaskGroupMemberService")
	private JyTaskGroupMemberService jyTaskGroupMemberService;
	
	@Autowired
	@Qualifier("jyScheduleTaskManager")
	private JyScheduleTaskManager jyScheduleTaskManager;
	
	@Autowired
	private IGenerateObjectId genObjectId;
	/**
	 * 添加小组成员
	 */
	@Override
	public JdCResponse<GroupMemberData> addMember(GroupMemberRequest addMemberRequest) {
		JdCResponse<GroupMemberData> result = new JdCResponse<>();
		result.toSucceed();
		if(addMemberRequest == null) {
			result.toFail("请求参数不能为空！");
		}
		if(StringHelper.isEmpty(addMemberRequest.getPositionCode())) {
			result.toFail("岗位码不能为空！");
		}
		Result<PositionDetailRecord> positionData = positionRecordService.queryOneByPositionCode(addMemberRequest.getPositionCode());
		if(positionData == null
				|| positionData.getData() == null) {
			result.toFail("岗位码无效！");
			return result;
		}
		if(addMemberRequest.getSignInTime() == null) {
			addMemberRequest.setSignInTime(new Date());
		}
		String gridKey = positionData.getData().getRefGridKey();
		//查询岗位码对应的小组信息
		JyGroupQuery groupQuery = new JyGroupQuery();
		groupQuery.setPositionCode(addMemberRequest.getPositionCode());
		JyGroupEntity groupData = null;
		Result<JyGroupEntity> groupResult = jyGroupService.queryGroupByPosition(groupQuery);
		boolean isNewGroup = false;
		Date currentDate = new Date();
		if(groupResult != null 
				&& groupResult.isSuccess()
				&& groupResult.getData() != null) {
			groupData = groupResult.getData();
		}else {
			groupData = new JyGroupEntity();
			groupData.setPositionCode(addMemberRequest.getPositionCode());
			groupData.setOrgCode(addMemberRequest.getOrgCode());
			groupData.setSiteCode(addMemberRequest.getSiteCode());
			groupData.setCreateTime(currentDate);
			groupData.setCreateUser(addMemberRequest.getOperateUserCode());
			groupData.setCreateUserName(addMemberRequest.getOperateUserName());
			jyGroupService.addGroupData(groupData);
			isNewGroup = true;
		}
		String groupCode = groupData.getGroupCode();
		JyGroupMemberEntity memberData = new JyGroupMemberEntity();
		memberData.setMemberType(addMemberRequest.getMemberType());
		memberData.setDeviceTypeCode(addMemberRequest.getDeviceTypeCode());
		memberData.setDeviceTypeName(addMemberRequest.getDeviceTypeName());
		memberData.setMachineCode(addMemberRequest.getMachineCode());
		generateAndSetMemberCode(memberData);
		memberData.setRefGroupCode(groupCode);
		memberData.setRefSignRecordId(addMemberRequest.getSignRecordId());
		memberData.setStatus(JyGroupMemberStatusEnum.IN.getCode());
		memberData.setUserCode(addMemberRequest.getUserCode());
		memberData.setUserName(addMemberRequest.getUserName());
		memberData.setJobCode(addMemberRequest.getJobCode());
		memberData.setOrgCode(addMemberRequest.getOrgCode());
		memberData.setSiteCode(addMemberRequest.getSiteCode());
		memberData.setCreateTime(currentDate);
		memberData.setCreateUser(addMemberRequest.getOperateUserCode());
		memberData.setCreateUserName(addMemberRequest.getOperateUserName());
		memberData.setSignInTime(addMemberRequest.getSignInTime());

		//新小组，将已签未退人员加入到小组中，正常应该只有当前人员
		if(isNewGroup) {
			List<JyGroupMemberEntity> memberList = new ArrayList<JyGroupMemberEntity>();
			memberList.add(memberData);
			
			UserSignQueryRequest query = new UserSignQueryRequest();
			query.setRefGridKey(gridKey);
			List<UserSignRecord> signList = userSignRecordService.queryUnSignOutListWithPosition(query);
			if(!CollectionUtils.isEmpty(signList)) {
				for(UserSignRecord signData: signList) {
					JyGroupMemberEntity member = new JyGroupMemberEntity();
					member.setMemberType(JyGroupMemberTypeEnum.PERSON.getCode());
					generateAndSetMemberCode(member);
					member.setRefGroupCode(groupCode);
					member.setRefSignRecordId(signData.getId());
                    member.setStatus(JyGroupMemberStatusEnum.IN.getCode());
					member.setUserCode(signData.getUserCode());
					member.setUserName(signData.getUserName());
					member.setJobCode(signData.getJobCode());
					member.setOrgCode(addMemberRequest.getOrgCode());
					member.setSiteCode(addMemberRequest.getSiteCode());
					member.setCreateTime(currentDate);
					member.setCreateUser(addMemberRequest.getOperateUserCode());
					member.setCreateUserName(addMemberRequest.getOperateUserName());
					member.setSignInTime(signData.getSignInTime());
					memberList.add(member);
				}
			}
			jyGroupMemberDao.batchInsert(memberList);
		}else {
			//添加小组成员信息
			jyGroupMemberDao.insert(memberData);
			//非新小组，将新加入组员加入到当前小组工作任务人员明细中
			//查询当前小组工作任务
			JyScheduleTaskReq taskQuery = new JyScheduleTaskReq();
			taskQuery.setDistributionType(JyScheduleTaskDistributionTypeEnum.GROUP.getCode());
			taskQuery.setDistributionTarget(groupCode);
			List<JyScheduleTaskResp> taskList = jyScheduleTaskManager.findStartedScheduleTasksByDistribute(taskQuery);
			if(!CollectionUtils.isEmpty(taskList)) {
				for(JyScheduleTaskResp task : taskList) {
					String taskId = task.getTaskId();
					JyTaskGroupMemberEntity taskMember = new JyTaskGroupMemberEntity();
					taskMember.setMemberType(memberData.getMemberType());
					taskMember.setDeviceTypeCode(memberData.getDeviceTypeCode());
					taskMember.setDeviceTypeName(memberData.getDeviceTypeName());
					taskMember.setMachineCode(memberData.getMachineCode());					
					taskMember.setRefGroupMemberCode(memberData.getMemberCode());
					taskMember.setRefGroupCode(groupCode);
					taskMember.setRefTaskId(taskId);
					taskMember.setJobCode(addMemberRequest.getJobCode());
					taskMember.setUserCode(addMemberRequest.getUserCode());
					taskMember.setUserName(addMemberRequest.getUserName());
					taskMember.setOrgCode(addMemberRequest.getOrgCode());
					taskMember.setSiteCode(addMemberRequest.getSiteCode());
					taskMember.setStartTime(currentDate);
					taskMember.setCreateTime(currentDate);
					taskMember.setCreateUser(addMemberRequest.getOperateUserCode());
					taskMember.setCreateUserName(addMemberRequest.getOperateUserName());	
					jyTaskGroupMemberService.addTaskMember(taskMember);
				}
			}
		}
		GroupMemberData returnData = new GroupMemberData();
		returnData.setGroupCode(groupCode);
		returnData.setGroupMemberNum(jyGroupMemberDao.queryGroupMemberNum(groupCode));
		result.setData(returnData);
		return result;
	}
	/**
	 * 移除小组成员，目前由签退触发
	 */
	@Override
	public JdCResponse<GroupMemberData> removeMember(GroupMemberRequest removeMemberRequest) {
		JdCResponse<GroupMemberData> result = new JdCResponse<>();
		result.toSucceed();
		//根据签到id查询小组成员
		JyGroupMemberEntity memberData = null;
		if(JyGroupMemberTypeEnum.PERSON.getCode().equals(removeMemberRequest.getMemberType())) {
			memberData = jyGroupMemberDao.queryBySignRecordId(removeMemberRequest.getSignRecordId());
		}else {
			memberData = jyGroupMemberDao.queryByMemberCode(removeMemberRequest.getMemberCode());
		}
		if(memberData != null) {
			JyGroupMemberEntity removeMemberData = new JyGroupMemberEntity();
			Date currentDate = new Date();
			//剔除小组成员
			removeMemberData.setId(memberData.getId());
			removeMemberData.setStatus(JyGroupMemberStatusEnum.OUT.getCode());
			removeMemberData.setUpdateTime(currentDate);
			removeMemberData.setUpdateUser(removeMemberRequest.getOperateUserCode());
			removeMemberData.setUpdateUserName(removeMemberRequest.getOperateUserName());
			removeMemberData.setSignOutTime(removeMemberRequest.getSignOutTime());
			jyGroupMemberDao.removeMember(removeMemberData);
			JyTaskGroupMemberEntity taskGroupMember = new JyTaskGroupMemberEntity();
			taskGroupMember.setRefGroupMemberCode(memberData.getMemberCode());
			taskGroupMember.setEndTime(currentDate);
			taskGroupMember.setUpdateTime(currentDate);
			taskGroupMember.setUpdateUser(removeMemberRequest.getOperateUserCode());
			taskGroupMember.setUpdateUserName(removeMemberRequest.getOperateUserName());
			//小组任务成员，设置结束时间
			jyTaskGroupMemberService.endWorkByMemberCode(taskGroupMember);
			GroupMemberData returnData = new GroupMemberData();
			returnData.setGroupCode(memberData.getRefGroupCode());
			returnData.setGroupMemberNum(jyGroupMemberDao.queryGroupMemberNum(memberData.getRefGroupCode()));
			result.setData(returnData);
		}
		return result;
	}
	@Override
	public JdCResponse<PageDto<UserSignRecordData>> querySignListByGroup(GroupMemberQueryRequest query) {
		if(query.getPageSize() == null
				|| query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		}
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		}
		JdCResponse<PageDto<UserSignRecordData>> result = new JdCResponse<>();
		result.toSucceed();
		PageDto<UserSignRecordData> PageDto = new PageDto<>(query.getPageNumber(), query.getPageSize());
		Long totalCount = jyGroupMemberDao.queryCountByGroup(query);
		if(totalCount != null && totalCount > 0){
			//先查询id列表
			List<Long> signIdList = jyGroupMemberDao.querySignIdListByGroup(query);
		    List<UserSignRecordData> dataList = userSignRecordService.queryUserSignRecordDataByIds(signIdList);
		    PageDto.setResult(dataList);
			PageDto.setTotalRow(totalCount.intValue());
		}else {
		    PageDto.setResult(new ArrayList<UserSignRecordData>());
			PageDto.setTotalRow(0);
		}
		result.setData(PageDto);
		return result;
	}
	@Override
	public List<JyGroupMemberEntity> queryMemberListByGroup(JyGroupMemberQuery query) {
		return jyGroupMemberDao.queryMemberListByGroup(query);
	}		
	/**
	 * 生成并设置memberCode
	 * @param data
	 */
	private void generateAndSetMemberCode(JyGroupMemberEntity data) {
		if(data != null) {
			data.setMemberCode(DmsConstants.CODE_PREFIX_JY_GROUP_MEMBER.concat(StringHelper.padZero(this.genObjectId.getObjectId(JyGroupMemberEntity.class.getName()),11)));
		}
	}
	@Override
	public Integer queryGroupMemberNum(String groupCode) {
		return jyGroupMemberDao.queryGroupMemberNum(groupCode);
	}
	@Override
	public JdCResponse<JyGroupMemberResponse> queryMemberListByGroup(GroupMemberQueryRequest query) {
		if(query.getPageSize() == null
				|| query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		}
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		}
		JdCResponse<JyGroupMemberResponse> result = new JdCResponse<>();
		result.toSucceed();
		JyGroupMemberResponse data = new JyGroupMemberResponse();
		result.setData(data);
		List<JyGroupMemberCountData> countDataList= jyGroupMemberDao.queryMemberDataCountByGroup(query);
		if(countDataList != null && countDataList.size() > 0){
		    List<JyGroupMemberData> dataList = jyGroupMemberDao.queryMemberDataListByGroup(query);
			if(!CollectionUtils.isEmpty(dataList)) {
				Date currentDate = new Date();
			    for(JyGroupMemberData item: dataList) {
			    	fillOtherInfo(item,currentDate);
			    }
			}
		    data.setDataList(dataList);
		    data.setCountDataList(countDataList);
		}else {
			data.setDataList(new ArrayList<JyGroupMemberData>());
			data.setCountDataList(new ArrayList<JyGroupMemberCountData>());
		}
		return result;
	}
	private void fillOtherInfo(JyGroupMemberData data, Date currentDate) {
		data.setUserName(BusinessUtil.encryptIdCard(data.getUserName()));
		if(data.getSignInTime() != null) {
			String workTimes = "--";
			double workHoursDouble = calculateWorkHours(data.getSignInTime(),data.getSignOutTime(),currentDate);
			if(workHoursDouble > 0) {
				workTimes = DateHelper.hoursToHHMM(workHoursDouble);
			}
			data.setWorkTimeStr(workTimes);
		}
	}
	private double calculateWorkHours(Date signInTime,Date signOutTime,Date currentDate) {
		Date workEndTime = signOutTime;
		if(workEndTime == null) {
			workEndTime = currentDate;
		}
		return DateHelper.betweenHours(signInTime,workEndTime);
	}
}
