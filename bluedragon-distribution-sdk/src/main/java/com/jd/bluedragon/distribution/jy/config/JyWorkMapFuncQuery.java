package com.jd.bluedragon.distribution.jy.config;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 拣运功能配置查询条件
 *
 * @author hujiping
 * @date 2022/4/6 6:29 PM
 */
public class JyWorkMapFuncQuery extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 功能编码
     */
    private String funcCode;
    /**
     * 工序表业务主键
     */
    private String refWorkKey;
    /**
     * 作业区编码
     */
    private String areaCode;
    /**
     * 网格编码
     */
    private String workCode;

    /**
     * 分页-pageSize
     */
    private Integer pageSize;

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    public String getRefWorkKey() {
        return refWorkKey;
    }

    public void setRefWorkKey(String refWorkKey) {
        this.refWorkKey = refWorkKey;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
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
