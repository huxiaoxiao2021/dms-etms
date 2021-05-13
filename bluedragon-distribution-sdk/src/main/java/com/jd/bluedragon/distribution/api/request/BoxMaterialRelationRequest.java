package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.response.box.GroupBoxDto;

import java.util.List;


/**
 * 箱号与集包编号绑定、删除操作请求对象
 */
public class BoxMaterialRelationRequest extends JdRequest {
    private static final long serialVersionUID = 1L;

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
}
