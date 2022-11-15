package com.jd.bluedragon.common.dto.group;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
/**
 * @ClassName: JyGroupMemberCountData
 * @Description: 小组成员统计数据
 * @author wuyoude
 * @date 2022年03月30日 11:01:53
 *
 */
public class JyGroupMemberCountData implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 组员类型
	 */
	private Integer memberType;
	/**
	 * 组员数量
	 */
	private Integer count;
	
	public Integer getMemberType() {
		return memberType;
	}
	public void setMemberType(Integer memberType) {
		this.memberType = memberType;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
}
