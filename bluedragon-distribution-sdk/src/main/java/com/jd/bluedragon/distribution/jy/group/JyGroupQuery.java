package com.jd.bluedragon.distribution.jy.group;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 工作小组-查询条件实体类
 * 
 * @author wuyoude
 * @date 2022年03月30日 14:30:43
 *
 */
public class JyGroupQuery extends BasePagerCondition {

	private static final long serialVersionUID = 1L;
	
    /**
     * 组编码
     */
    private String groupCode;
    /**
     * 岗位编码
     */
    private String positionCode;
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
	public String getPositionCode() {
		return positionCode;
	}
	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}	
}
