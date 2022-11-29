package com.jd.bluedragon.distribution.jy.gateway.group;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.group.GroupMemberData;
import com.jd.bluedragon.common.dto.group.GroupMemberQueryRequest;
import com.jd.bluedragon.common.dto.group.GroupMemberRequest;
import com.jd.bluedragon.common.dto.group.JyGroupMemberData;
import com.jd.bluedragon.common.dto.group.JyGroupMemberResponse;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 小组成员网关服务接口
 * 
 * @author wuyoude
 * @date 2022年03月30日 14:30:43
 *
 */
public interface GroupMemberGatewayService {
	/**
	 * 添加小组成员
	 * @param addMemberRequest
	 * @return
	 */
	JdCResponse<GroupMemberData> addMember(GroupMemberRequest addMemberRequest);
	/**
	 * 删除小组成员
	 * @param removeMemberRequest
	 * @return
	 */
	JdCResponse<GroupMemberData> removeMember(GroupMemberRequest removeMemberRequest);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	JdCResponse<PageDto<UserSignRecordData>> querySignListByGroup(GroupMemberQueryRequest query);
	/**
	 * 查询小组信息
	 * @param groupCode
	 * @return
	 */
	JdCResponse<GroupMemberData> queryGroupData(String groupCode);
	/**
	 * 按条件分页查询小组成员信息
	 * @param query
	 * @return
	 */
	JdCResponse<JyGroupMemberResponse> queryMemberListByGroup(GroupMemberQueryRequest query);
}