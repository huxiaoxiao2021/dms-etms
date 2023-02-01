package com.jd.bluedragon.distribution.jy.service.group;

import java.util.List;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.group.GroupMemberData;
import com.jd.bluedragon.common.dto.group.GroupMemberQueryRequest;
import com.jd.bluedragon.common.dto.group.GroupMemberRequest;
import com.jd.bluedragon.common.dto.group.JyGroupMemberResponse;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 小组成员--Service接口
 * 
 * @author wuyoude
 * @date 2022年03月30日 14:30:43
 *
 */
public interface JyGroupMemberService {

	/**
	 * 添加小组成员
	 * @param addMemberRequest
	 * @return
	 */
	JdCResponse<GroupMemberData> addMember(GroupMemberRequest addMemberRequest);
	/**
	 * 移除小组成员
	 * @param addMemberRequest
	 * @return
	 */
	JdCResponse<GroupMemberData> removeMember(GroupMemberRequest removeMemberRequest);
	/**
	 * 删除小组成员
	 * @param deleteMemberRequest
	 * @return
	 */
	JdCResponse<GroupMemberData> deleteMember(GroupMemberRequest deleteMemberRequest);	
	/**
	 * 批量删除小组成员
	 * @param removeMemberRequest
	 * @return
	 */
	JdCResponse<Boolean> removeMembers(GroupMemberRequest removeMemberRequest);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	JdCResponse<PageDto<UserSignRecordData>> querySignListByGroup(GroupMemberQueryRequest query);
	/**
	 * 
	 * @param membersQuery
	 * @return
	 */
	List<JyGroupMemberEntity> queryMemberListByGroup(JyGroupMemberQuery membersQuery);
	/**
	 * 查询小组成员数量
	 * @param groupCode
	 * @return
	 */
	Integer queryGroupMemberNum(String groupCode);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	JdCResponse<JyGroupMemberResponse> queryMemberListByGroup(GroupMemberQueryRequest query);
	/**
	 * 查询小组信息
	 * @param positionCode
	 * @return
	 */
	JdCResponse<GroupMemberData> queryGroupMemberDataByPositionCode(String positionCode);
	
	/**
	 * 查询未签退人员-memberCode列表
	 * @param memberCodeList
	 * @return
	 */
	List<String> queryUnSignOutMemberCodeList(List<String> memberCodeList);
}
