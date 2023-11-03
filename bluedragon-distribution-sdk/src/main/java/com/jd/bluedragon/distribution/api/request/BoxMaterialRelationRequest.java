package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.response.box.GroupBoxDto;

import java.util.List;


/**
 * 箱号与集包编号绑定、删除操作请求对象
 */
public class BoxMaterialRelationRequest extends JdRequest {
    private static final long serialVersionUID = 1L;

    public static String BIZ_SORTING_MACHINE = "SORTING_MACHINE";

    /**
     *  箱号
     */
    private String boxCode;

    /**
     * 集包袋编号
     */
    private String materialCode;

    /**
     * 操作类型 1 绑定 2 解绑
     */
    private int bindFlag;

    /**
     * 操作人ERP
     */
    private String operatorERP;

    /**
     * 是否查询一组箱号绑定情况
     * true 查询  false 不查询
     */
    private boolean groupSearch;


    private List<GroupBoxDto> groupList;

    /**
     * 业务来源
     */
    private String bizSource;

    /**
     * 强制提交
     */
    private Boolean forceFlag;

    /**
     * 本次绑定时需要发货的目的地
     */
    private Integer receiveSiteCode;

    private Integer pageNumber;

    private Integer pageSize;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<GroupBoxDto> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupBoxDto> groupList) {
        this.groupList = groupList;
    }

    public boolean getGroupSearch() {
        return groupSearch;
    }

    public void setGroupSearch(boolean groupSearch) {
        this.groupSearch = groupSearch;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public int getBindFlag() {
        return bindFlag;
    }

    public void setBindFlag(int bindFlag) {
        this.bindFlag = bindFlag;
    }

    public String getOperatorERP() {
        return operatorERP;
    }

    public void setOperatorERP(String operatorERP) {
        this.operatorERP = operatorERP;
    }

    public String getBizSource() {
        return bizSource;
    }

    public void setBizSource(String bizSource) {
        this.bizSource = bizSource;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Boolean getForceFlag() {
        return forceFlag;
    }

    public void setForceFlag(Boolean forceFlag) {
        this.forceFlag = forceFlag;
    }
}
