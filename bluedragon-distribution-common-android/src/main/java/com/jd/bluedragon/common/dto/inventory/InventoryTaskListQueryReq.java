package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/7/13 16:56
 * @Description
 */
public class InventoryTaskListQueryReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -6051001368608203945L;

    /**
     * 网格key
     */
    private String workGridKey;
    /**
     * 1: 查当日
     * 2: 查15天
     */
    private String operateType;
    /**
     * 页码
     */
    private Integer pageNo;
    private Integer pageSize;


    public String getWorkGridKey() {
        return workGridKey;
    }

    public void setWorkGridKey(String workGridKey) {
        this.workGridKey = workGridKey;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
