package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * 追加混扫任务列表查询
 */
public class AppendSendVehicleTaskQueryReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;

    /**
     * 搜索关键字
     */
    private String keyword;

    private Integer pageSize;
    private Integer pageNo;


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
