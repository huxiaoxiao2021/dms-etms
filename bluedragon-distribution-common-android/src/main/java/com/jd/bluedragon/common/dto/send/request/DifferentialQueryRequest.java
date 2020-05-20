package com.jd.bluedragon.common.dto.send.request;

import java.util.List;

public class DifferentialQueryRequest {
    /** 已扫描的包裹、箱号列表 */
    private List<DeliveryRequest> sendList;

    /** 查询类型 1：查询未扫描数据 2：查询已扫描数据 3：查询已扫、未扫数据 */
    private Integer queryType;

    public List<DeliveryRequest> getSendList() {
        return sendList;
    }

    public void setSendList(List<DeliveryRequest> sendList) {
        this.sendList = sendList;
    }

    public Integer getQueryType() {
        return queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }
}
