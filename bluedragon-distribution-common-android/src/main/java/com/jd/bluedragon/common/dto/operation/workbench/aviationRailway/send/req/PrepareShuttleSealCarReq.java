package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/10/12 20:30
 * @Description
 */
public class PrepareShuttleSealCarReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;
    /**
     * 航空任务BizId
     */
    private List<String> bizIdList;

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }
}
