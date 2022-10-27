package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * 无任务验货请求
 *
 * @author fanggang7
 * @time 2022-10-09 14:31:03 周日
 */
public class InspectionNoTaskRequest extends BaseReq implements Serializable {

    private static final long serialVersionUID = -880642955427860129L;

    private String groupCode;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
