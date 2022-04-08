package com.jd.bluedragon.common.dto.group;

import java.io.Serializable;
/**
 * @ClassName: GroupMemberData
 * @Description: 小组成员数据
 * @author wuyoude
 * @date 2022年03月30日 11:01:53
 *
 */
public class GroupMemberData implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 小组编码
	 */
	private String groupCode;
	/**
	 * 成员数量
	 */
	private Integer groupMemberNum;
	
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public Integer getGroupMemberNum() {
		return groupMemberNum;
	}
	public void setGroupMemberNum(Integer groupMemberNum) {
		this.groupMemberNum = groupMemberNum;
	}
}
