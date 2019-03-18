package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.List;

/***
 * 快运发货差异查询请求实体
 */
public class DifferentialQueryRequest extends JdRequest {
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
