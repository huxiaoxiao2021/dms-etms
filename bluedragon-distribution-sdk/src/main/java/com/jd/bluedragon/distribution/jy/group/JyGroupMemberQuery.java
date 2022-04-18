package com.jd.bluedragon.distribution.jy.group;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 工作小组成员-查询条件实体类
 * 
 * @author wuyoude
 * @date 2022年03月30日 14:30:43
 *
 */
public class JyGroupMemberQuery extends BasePagerCondition {

	private static final long serialVersionUID = 1L;
	
    /**
     * 组编码
     */
    private String groupCode;
    /**
     * 状态
     */
    private Integer status;
	/**
	 * 分页-pageSize
	 */
	private Integer pageSize;
	
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}	
}
