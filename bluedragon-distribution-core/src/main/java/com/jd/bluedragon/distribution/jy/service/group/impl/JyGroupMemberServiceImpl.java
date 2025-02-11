package com.jd.bluedragon.distribution.jy.service.group.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.device.response.DeviceInfoDto;
import com.jd.bluedragon.common.dto.group.*;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.device.service.DeviceInfoService;
import com.jd.bluedragon.distribution.jy.dao.group.JyGroupMemberDao;
import com.jd.bluedragon.distribution.jy.group.*;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ql.dms.print.utils.StringHelper;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskDistributionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	private PositionManager positionManager;
	
	@Autowired
	@Qualifier("jyTaskGroupMemberService")
	private JyTaskGroupMemberService jyTaskGroupMemberService;
	
	@Autowired
	@Qualifier("jyScheduleTaskManager")
	private JyScheduleTaskManager jyScheduleTaskManager;
	
	@Autowired
	private IGenerateObjectId genObjectId;

	@Autowired
	private DeviceInfoService deviceInfoService;

	@Autowired
	private BaseMajorManager baseMajorManager;
	
	/**
	 * 添加小组成员
	 */
	@Override
	public JdCResponse<GroupMemberData> addMember(GroupMemberRequest addMemberRequest) {
		JdCResponse<GroupMemberData> result = new JdCResponse<>();
		result.toSucceed();
		if(addMemberRequest == null) {
			result.toFail("请求参数不能为空！");
			return result;
		}
		if(StringHelper.isEmpty(addMemberRequest.getPositionCode())) {
			result.toFail("岗位码不能为空！");
			return result;
		}
		com.jdl.basic.common.utils.Result<com.jdl.basic.api.domain.position.PositionDetailRecord> positionData
				= positionManager.queryOneByPositionCode(addMemberRequest.getPositionCode());
		if(positionData == null
				|| positionData.getData() == null) {
			result.toFail("岗位码无效，联系【作业流程组】小哥维护岗位码");
			return result;
		}
		String gridKey = positionData.getData().getRefGridKey();
		
		// fill request org&province info
		fillRequestBasicInfo(addMemberRequest);
		
		if(addMemberRequest.getSignInTime() == null) {
			addMemberRequest.setSignInTime(new Date());
		}
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
			groupData.setProvinceAgencyCode(addMemberRequest.getProvinceAgencyCode());
			groupData.setAreaHubCode(addMemberRequest.getAreaHubCode());
			jyGroupService.addGroupData(groupData);
			isNewGroup = true;
		}
		String groupCode = groupData.getGroupCode();
		JyGroupMemberEntity memberData = new JyGroupMemberEntity();
		memberData.setMemberType(addMemberRequest.getMemberType());
		memberData.setDeviceTypeCode(addMemberRequest.getDeviceTypeCode());
		memberData.setDeviceTypeName(addMemberRequest.getDeviceTypeName());
		memberData.setMachineCode(addMemberRequest.getMachineCode());
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
		memberData.setProvinceAgencyCode(addMemberRequest.getProvinceAgencyCode());
		memberData.setAreaHubCode(addMemberRequest.getAreaHubCode());
		//校验组员是否存在
		Result<Boolean> checkResult = checkBeforeAddMember(memberData);
		if(checkResult != null
				&& !checkResult.isSuccess()) {
			result.toFail(checkResult.getMessage());
			return result;
		}
		generateAndSetMemberCode(memberData);
		//新小组，将已签未退人员加入到小组中，正常应该只有当前人员
		if(isNewGroup) {
			List<JyGroupMemberEntity> memberList = new ArrayList<JyGroupMemberEntity>();
			memberList.add(memberData);
			
			UserSignQueryRequest query = new UserSignQueryRequest();
			query.setRefGridKey(gridKey);
			List<UserSignRecord> signList = userSignRecordService.queryUnSignOutListWithPosition(query);
			if(!CollectionUtils.isEmpty(signList)) {
				for(UserSignRecord signData: signList) {
					//排除当前签到人员
					if(JyGroupMemberTypeEnum.PERSON.getCode().equals(addMemberRequest.getMemberType())&&
							signData.getId().equals(addMemberRequest.getSignRecordId())) {
						continue;
					}
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
					member.setProvinceAgencyCode(addMemberRequest.getProvinceAgencyCode());
					member.setAreaHubCode(addMemberRequest.getAreaHubCode());
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
			List<JyScheduleTaskResp> taskList = jyScheduleTaskManager.findStartedScheduleTasksForAddMember(taskQuery);
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
					taskMember.setProvinceAgencyCode(addMemberRequest.getProvinceAgencyCode());
					taskMember.setAreaHubCode(addMemberRequest.getAreaHubCode());
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
	 * 补充基础数据
	 * 
	 * @param addMemberRequest
	 */
	private void fillRequestBasicInfo(GroupMemberRequest addMemberRequest) {
		if(addMemberRequest.getSiteCode() != null){
			BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(addMemberRequest.getSiteCode());
			addMemberRequest.setOrgCode(baseSite == null ? null : baseSite.getOrgId());
			addMemberRequest.setProvinceAgencyCode((baseSite == null || baseSite.getProvinceAgencyCode() == null) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyCode());
			addMemberRequest.setAreaHubCode((baseSite == null || baseSite.getAreaCode() == null) ? Constants.EMPTY_FILL : baseSite.getAreaCode());
		}
	}

	/**
	 * 判断能否添加组员
	 * @param memberData
	 * @return
	 */
	private Result<Boolean> checkBeforeAddMember(JyGroupMemberEntity memberData) {
		Result<Boolean> result = new Result<>();
		result.toSuccess();
		//根据签到id查询小组成员
		JyGroupMemberEntity oldData = null;
		if(JyGroupMemberTypeEnum.PERSON.getCode().equals(memberData.getMemberType())) {
			oldData = jyGroupMemberDao.queryInDataBySignRecordId(memberData);
			if(oldData != null) {
				result.toFail("该人员已在岗！");
				return result;
			}
		}else {
			//根据设备编码查询小组成员
			oldData = jyGroupMemberDao.queryInDataByMachineCode(memberData);
			if(oldData != null) {
				result.toFail("该设备已在岗！");
				//不在当前岗位，查询在岗信息
				if(!oldData.getRefGroupCode().equals(memberData.getRefGroupCode())) {
					JyGroupEntity groupInfo = this.jyGroupService.queryGroupByGroupCode(oldData.getRefGroupCode());
					if(groupInfo != null) {
						result.toFail("该设备已在岗["+groupInfo.getPositionCode()+"]！");
					}
				}
				return result;
			}
			//校验并获取设备其他信息
			if(StringHelper.isEmpty(memberData.getDeviceTypeCode())) {
				JdResult<DeviceInfoDto> deviceInfo = this.deviceInfoService.queryDeviceConfigByMachineCode(memberData.getMachineCode(), memberData.getSiteCode());
				if(deviceInfo == null || deviceInfo.getData() == null) {
					result.toFail("场地中不存在设备编码！");
					return result;
				}
				memberData.setDeviceTypeCode(deviceInfo.getData().getDeviceTypeCode());
				memberData.setDeviceTypeName(deviceInfo.getData().getDeviceTypeName());
			}
		}
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
			//根据签到memberCode查询小组成员
			memberData = jyGroupMemberDao.queryByMemberCode(removeMemberRequest.getMemberCode());
		}
		if(memberData != null && !JyGroupMemberStatusEnum.OUT.getCode().equals(memberData.getStatus())) {
			JyGroupMemberEntity removeMemberData = new JyGroupMemberEntity();
			Date currentDate = new Date();
			//剔除小组成员
			removeMemberData.setId(memberData.getId());
			removeMemberData.setStatus(JyGroupMemberStatusEnum.OUT.getCode());
			removeMemberData.setUpdateTime(currentDate);
			removeMemberData.setUpdateUser(removeMemberRequest.getOperateUserCode());
			removeMemberData.setUpdateUserName(removeMemberRequest.getOperateUserName());
			removeMemberData.setSignOutTime(removeMemberRequest.getSignOutTime());
			if(removeMemberData.getSignOutTime() == null) {
				removeMemberData.setSignOutTime(new Date());
			}
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
		data.setMemberType(query.getMemberType());
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
	@Override
	public JdCResponse<Boolean> removeMembers(GroupMemberRequest removeMemberRequest) {
		JdCResponse<Boolean> result = new JdCResponse<>();
		result.toSucceed();
		//根据签到id查询小组成员
		List<String> memberCodes = jyGroupMemberDao.queryMemberCodesBySignRecordIds(removeMemberRequest.getSignRecordIdList());
		if(CollectionUtils.isNotEmpty(memberCodes)) {
			JyGroupMemberEntity removeMemberData = new JyGroupMemberEntity();
			Date currentDate = new Date();
			//剔除小组成员
			removeMemberData.setStatus(JyGroupMemberStatusEnum.OUT.getCode());
			removeMemberData.setUpdateTime(currentDate);
			removeMemberData.setUpdateUser(removeMemberRequest.getOperateUserCode());
			removeMemberData.setUpdateUserName(removeMemberRequest.getOperateUserName());
			removeMemberData.setSignOutTime(removeMemberRequest.getSignOutTime());
			if(removeMemberData.getSignOutTime() == null) {
				removeMemberData.setSignOutTime(new Date());
			}
			jyGroupMemberDao.removeMembers(removeMemberData,removeMemberRequest.getSignRecordIdList());
			JyTaskGroupMemberEntity taskGroupMember = new JyTaskGroupMemberEntity();
			taskGroupMember.setEndTime(currentDate);
			taskGroupMember.setUpdateTime(currentDate);
			taskGroupMember.setUpdateUser(removeMemberRequest.getOperateUserCode());
			taskGroupMember.setUpdateUserName(removeMemberRequest.getOperateUserName());
			//小组任务成员，设置结束时间
			jyTaskGroupMemberService.endWorkByMemberCodeListForAutoSignOut(taskGroupMember,memberCodes);
		}
		return result;
	}
	@Override
	public JdCResponse<GroupMemberData> deleteMember(GroupMemberRequest deleteMemberRequest) {
		JdCResponse<GroupMemberData> result = new JdCResponse<>();
		result.toSucceed();
		//根据签到id查询小组成员
		JyGroupMemberEntity memberData = null;
		if(JyGroupMemberTypeEnum.PERSON.getCode().equals(deleteMemberRequest.getMemberType())) {
			memberData = jyGroupMemberDao.queryBySignRecordId(deleteMemberRequest.getSignRecordId());
		}else {
			memberData = jyGroupMemberDao.queryByMemberCode(deleteMemberRequest.getMemberCode());
		}
		if(memberData == null) {
			result.toFail("小组数据无效|已删除！");
			return result;
		}
		JyGroupMemberEntity removeMemberData = new JyGroupMemberEntity();
		Date currentDate = new Date();
		//剔除小组成员
		removeMemberData.setId(memberData.getId());
		removeMemberData.setUpdateTime(currentDate);
		removeMemberData.setUpdateUser(deleteMemberRequest.getOperateUserCode());
		removeMemberData.setUpdateUserName(deleteMemberRequest.getOperateUserName());
		jyGroupMemberDao.deleteMember(removeMemberData);
		JyTaskGroupMemberEntity taskGroupMember = new JyTaskGroupMemberEntity();
		taskGroupMember.setRefGroupMemberCode(memberData.getMemberCode());
		taskGroupMember.setUpdateTime(currentDate);
		taskGroupMember.setUpdateUser(deleteMemberRequest.getOperateUserCode());
		taskGroupMember.setUpdateUserName(deleteMemberRequest.getOperateUserName());
		//小组任务成员，设置结束时间
		jyTaskGroupMemberService.deleteByMemberCode(taskGroupMember);
		GroupMemberData returnData = new GroupMemberData();
		returnData.setGroupCode(memberData.getRefGroupCode());
		returnData.setGroupMemberNum(jyGroupMemberDao.queryGroupMemberNum(memberData.getRefGroupCode()));
		result.setData(returnData);
		return result;
	}
	@Override
	public JdCResponse<GroupMemberData> queryGroupMemberDataByPositionCode(String positionCode) {
		JdCResponse<GroupMemberData> result = new JdCResponse<>();
		result.toSucceed();
		//查询岗位码对应的小组信息
		JyGroupQuery groupQuery = new JyGroupQuery();
		groupQuery.setPositionCode(positionCode);
		JyGroupEntity groupData = null;
		Result<JyGroupEntity> groupResult = jyGroupService.queryGroupByPosition(groupQuery);
		if(groupResult != null 
				&& groupResult.isSuccess()
				&& groupResult.getData() != null) {
			groupData = groupResult.getData();
			GroupMemberData returnData = new GroupMemberData();
			returnData.setGroupCode(groupData.getGroupCode());
			returnData.setGroupMemberNum(jyGroupMemberDao.queryGroupMemberNum(groupData.getGroupCode()));
			result.setData(returnData);
		}else {
			result.toFail("岗位码没有对应的小组信息");
		}
		return result;
	}
	@Override
	public List<String> queryUnSignOutMemberCodeList(List<String> memberCodeList) {
		if(CollectionUtils.isEmpty(memberCodeList)) {
			return new ArrayList<String>();
		}
		return jyGroupMemberDao.queryUnSignOutMemberCodeList(memberCodeList);
	}
	@Override
	public JyGroupMemberEntity queryBySignRecordId(Long signRecordId) {
		if(signRecordId == null || signRecordId <= 0) {
			return null;
		}
		return jyGroupMemberDao.queryBySignRecordId(signRecordId);
	}
	@Override
	public JdResult<Boolean> addMemberForFlow(GroupMemberRequest addMemberRequest) {
		JdResult<Boolean> result = new JdResult<>();
		result.toSuccess();
		if(addMemberRequest == null) {
			result.toFail("请求参数不能为空！");
			return result;
		}
		if(StringHelper.isEmpty(addMemberRequest.getPositionCode())) {
			result.toFail("岗位码不能为空！");
			return result;
		}
		com.jdl.basic.common.utils.Result<com.jdl.basic.api.domain.position.PositionDetailRecord> positionData
				= positionManager.queryOneByPositionCode(addMemberRequest.getPositionCode());
		if(positionData == null
				|| positionData.getData() == null) {
			result.toFail("岗位码无效，联系【作业流程组】小哥维护岗位码");
			return result;
		}
		// fill request org&province info
		fillRequestBasicInfo(addMemberRequest);
		
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
			groupData.setProvinceAgencyCode(addMemberRequest.getProvinceAgencyCode());
			groupData.setAreaHubCode(addMemberRequest.getAreaHubCode());
			jyGroupService.addGroupData(groupData);
			if(log.isDebugEnabled()) {
				log.debug("流程审批-添加组：{}", JsonHelper.toJson(groupData));
			}
			isNewGroup = true;
		}
		String groupCode = groupData.getGroupCode();
		JyGroupMemberEntity memberData = new JyGroupMemberEntity();
		memberData.setMemberType(addMemberRequest.getMemberType());
		memberData.setDeviceTypeCode(addMemberRequest.getDeviceTypeCode());
		memberData.setDeviceTypeName(addMemberRequest.getDeviceTypeName());
		memberData.setMachineCode(addMemberRequest.getMachineCode());
		memberData.setRefGroupCode(groupCode);
		memberData.setRefSignRecordId(addMemberRequest.getSignRecordId());
		memberData.setStatus(JyGroupMemberStatusEnum.OUT.getCode());
		memberData.setUserCode(addMemberRequest.getUserCode());
		memberData.setUserName(addMemberRequest.getUserName());
		memberData.setJobCode(addMemberRequest.getJobCode());
		memberData.setOrgCode(addMemberRequest.getOrgCode());
		memberData.setSiteCode(addMemberRequest.getSiteCode());
		memberData.setCreateTime(currentDate);
		memberData.setCreateUser(addMemberRequest.getOperateUserCode());
		memberData.setCreateUserName(addMemberRequest.getOperateUserName());
		memberData.setSignInTime(addMemberRequest.getSignInTime());
		memberData.setSignOutTime(addMemberRequest.getSignOutTime());
		memberData.setProvinceAgencyCode(addMemberRequest.getProvinceAgencyCode());
		memberData.setAreaHubCode(addMemberRequest.getAreaHubCode());
		//校验组员是否存在
		Result<Boolean> checkResult = checkBeforeAddMember(memberData);
		if(checkResult != null 
				&& !checkResult.isSuccess()) {
			result.toFail(checkResult.getMessage());
			return result;
		}
		generateAndSetMemberCode(memberData);
		//新小组，将已签未退人员加入到小组中，正常应该只有当前人员
		if(isNewGroup) {
			List<JyGroupMemberEntity> memberList = new ArrayList<JyGroupMemberEntity>();
			memberList.add(memberData);
			
			UserSignQueryRequest query = new UserSignQueryRequest();
			query.setRefGridKey(gridKey);
			List<UserSignRecord> signList = userSignRecordService.queryUnSignOutListWithPosition(query);
			if(!CollectionUtils.isEmpty(signList)) {
				for(UserSignRecord signData: signList) {
					//排除当前签到人员
					if(JyGroupMemberTypeEnum.PERSON.getCode().equals(addMemberRequest.getMemberType())&&
							signData.getId().equals(addMemberRequest.getSignRecordId())) {
						continue;
					}
					JyGroupMemberEntity member = new JyGroupMemberEntity();
					member.setMemberType(JyGroupMemberTypeEnum.PERSON.getCode());
					generateAndSetMemberCode(member);
					member.setRefGroupCode(groupCode);
					member.setRefSignRecordId(signData.getId());
                    member.setStatus(JyGroupMemberStatusEnum.OUT.getCode());
					member.setUserCode(signData.getUserCode());
					member.setUserName(signData.getUserName());
					member.setJobCode(signData.getJobCode());
					member.setOrgCode(signData.getOrgCode());
					member.setSiteCode(signData.getSiteCode());
					member.setCreateTime(currentDate);
					member.setCreateUser(signData.getCreateUser());
					member.setCreateUserName(signData.getCreateUserName());
					member.setSignInTime(signData.getSignInTime());
					member.setSignOutTime(signData.getSignOutTime());
					member.setProvinceAgencyCode(addMemberRequest.getProvinceAgencyCode());
					member.setAreaHubCode(addMemberRequest.getAreaHubCode());
					memberList.add(member);
				}
			}
			jyGroupMemberDao.batchInsert(memberList);
			if(log.isDebugEnabled()) {
				log.debug("流程审批-批量添加组员：{}", JsonHelper.toJson(memberList));
			}
		}else {
			//添加小组成员信息
			jyGroupMemberDao.insert(memberData);
			if(log.isDebugEnabled()) {
				log.debug("流程审批-添加组员：{}", JsonHelper.toJson(memberData));
			}
		}
		return result;
	}
	@Override
	public JdResult<Boolean> deleteMemberForFlow(GroupMemberRequest deleteMemberRequest) {
		JdResult<Boolean> result = new JdResult<>();
		result.toSuccess();
		//根据签到id查询小组成员
		JyGroupMemberEntity memberData = null;
		if(JyGroupMemberTypeEnum.PERSON.getCode().equals(deleteMemberRequest.getMemberType())) {
			memberData = jyGroupMemberDao.queryBySignRecordId(deleteMemberRequest.getSignRecordId());
		}else {
			memberData = jyGroupMemberDao.queryByMemberCode(deleteMemberRequest.getMemberCode());
		}
		if(memberData == null) {
			result.toFail("小组数据无效|已删除！");
			return result;
		}
		JyGroupMemberEntity removeMemberData = new JyGroupMemberEntity();
		Date currentDate = new Date();
		//剔除小组成员
		removeMemberData.setId(memberData.getId());
		removeMemberData.setUpdateTime(currentDate);
		removeMemberData.setUpdateUser(deleteMemberRequest.getOperateUserCode());
		removeMemberData.setUpdateUserName(deleteMemberRequest.getOperateUserName());
		jyGroupMemberDao.deleteMember(removeMemberData);
		JyTaskGroupMemberEntity taskGroupMember = new JyTaskGroupMemberEntity();
		taskGroupMember.setRefGroupMemberCode(memberData.getMemberCode());
		taskGroupMember.setUpdateTime(currentDate);
		taskGroupMember.setUpdateUser(deleteMemberRequest.getOperateUserCode());
		taskGroupMember.setUpdateUserName(deleteMemberRequest.getOperateUserName());
		//小组任务成员，设置结束时间
		jyTaskGroupMemberService.deleteByMemberCode(taskGroupMember);
		return result;
	}
}
