package com.jd.bluedragon.distribution.jy.gateway.group.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.group.GroupMemberData;
import com.jd.bluedragon.common.dto.group.GroupMemberQueryRequest;
import com.jd.bluedragon.common.dto.group.GroupMemberRequest;
import com.jd.bluedragon.common.dto.group.JyGroupMemberResponse;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.distribution.jy.gateway.group.GroupMemberGatewayService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

/**
 * 小组成员网关服务实现
 * 
 * @author wuyoude
 * @date 2022年03月30日 14:30:43
 *
 */
@Service("groupMemberGatewayService")
public class GroupMemberGatewayServiceImpl implements GroupMemberGatewayService {

	@Autowired
	@Qualifier("jyGroupMemberService")
	private JyGroupMemberService jyGroupMemberService;

	@Autowired
	@Qualifier("jyGroupService")
	private JyGroupService jyGroupService;
	
	@Override
	@JProfiler(jKey = "dmsWeb.server.groupMemberGatewayService.addMember",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})	
	public JdCResponse<GroupMemberData> addMember(GroupMemberRequest addMemberRequest) {
		return jyGroupMemberService.addMember(addMemberRequest);
	}
	@Override
	@JProfiler(jKey = "dmsWeb.server.groupMemberGatewayService.removeMember",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})	
	public JdCResponse<GroupMemberData> removeMember(GroupMemberRequest removeMemberRequest) {
		return jyGroupMemberService.removeMember(removeMemberRequest);
	}
	@Override
	@JProfiler(jKey = "dmsWeb.server.groupMemberGatewayService.querySignListByGroup",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdCResponse<PageDto<UserSignRecordData>> querySignListByGroup(GroupMemberQueryRequest query) {
		return jyGroupMemberService.querySignListByGroup(query);
	}
	@Override
	public JdCResponse<GroupMemberData> queryGroupData(String groupCode) {
		return jyGroupService.queryGroupData(groupCode);
	}
	@Override
	@JProfiler(jKey = "dmsWeb.server.groupMemberGatewayService.queryMemberListByGroup",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})	
	public JdCResponse<JyGroupMemberResponse> queryMemberListByGroup(GroupMemberQueryRequest query) {
		return jyGroupMemberService.queryMemberListByGroup(query);
	}

}
