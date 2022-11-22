package com.jd.bluedragon.common.dto.group;

import java.io.Serializable;
import java.util.List;
/**
 * @ClassName: JyGroupMemberData
 * @Description: 小组成员数据
 * @author wuyoude
 * @date 2022年03月30日 11:01:53
 *
 */
public class JyGroupMemberResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
    /**
     * 组员类型
     */
    private Integer memberType;
	
	private List<JyGroupMemberCountData> countDataList;
	
	private List<JyGroupMemberData> dataList;

	public List<JyGroupMemberCountData> getCountDataList() {
		return countDataList;
	}

	public void setCountDataList(List<JyGroupMemberCountData> countDataList) {
		this.countDataList = countDataList;
	}

	public List<JyGroupMemberData> getDataList() {
		return dataList;
	}

	public void setDataList(List<JyGroupMemberData> dataList) {
		this.dataList = dataList;
	}

	public Integer getMemberType() {
		return memberType;
	}

	public void setMemberType(Integer memberType) {
		this.memberType = memberType;
	}
	
}
