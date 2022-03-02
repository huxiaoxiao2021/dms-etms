package com.jd.bluedragon.distribution.position.query;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/2/26 8:42 PM
 */
public class PositionQuery extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 机构编码
     */
    private Integer orgCode;

    /**
     * 场地编码
     */
    private Integer siteCode;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 作业区编码
     */
    private String areaCode;

    /**
     * 网格号:01~99
     */
    private String gridNo;

    /**
     * 网格ID
     */
    private String gridCode;

    /**
     * 工序编码
     */
    private String workCode;

    /**
     * 分页-pageSize
     */
    private Integer pageSize;

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
