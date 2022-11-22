package com.jd.bluedragon.distribution.jy.group;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 任务-小组人员明细表-查询条件实体类
 * 
 * @author wuyoude
 * @date 2022年03月30日 14:30:43
 *
 */
public class JyTaskGroupMemberQuery extends BasePagerCondition {

	private static final long serialVersionUID = 1L;
	
    /**
     * 组编码
     */
    private String groupCode;
	/**
	 * 分页-pageSize
	 */
	private Integer pageSize;
    /**
     * 关联任务表-id
     */
    private String refTaskId;
    /**
     * 组员类型
     */
    private Integer memberType;
	
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public String getRefTaskId() {
		return refTaskId;
	}
	public void setRefTaskId(String refTaskId) {
		this.refTaskId = refTaskId;
	}
	public Integer getMemberType() {
		return memberType;
	}
	public void setMemberType(Integer memberType) {
		this.memberType = memberType;
	}	
}
