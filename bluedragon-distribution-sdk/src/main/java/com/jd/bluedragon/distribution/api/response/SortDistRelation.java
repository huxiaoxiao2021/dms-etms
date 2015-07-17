package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

/**
 * @author dudong
 * @date 2015/5/10
 */
public class SortDistRelation implements Serializable{
    private static final long serialVersionUID = 4852524427503186865L;
    /**
     * 数据库ID
     * */
    private Integer Id;
    /**
     * 分拣中心ID
     */
    private Integer sortCenterId;
    /**
     * 分拣中心名称
     */
    private String sortCenterName;
    /**
     * 配送中心ID
     */
    private Integer deliveryCenterId;
    /**
     * 配送中心名称
     */
    private String deliveryCenterName;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getSortCenterId() {
        return sortCenterId;
    }

    public void setSortCenterId(Integer sortCenterId) {
        this.sortCenterId = sortCenterId;
    }

    public String getSortCenterName() {
        return sortCenterName;
    }

    public void setSortCenterName(String sortCenterName) {
        this.sortCenterName = sortCenterName;
    }

    public Integer getDeliveryCenterId() {
        return deliveryCenterId;
    }

    public void setDeliveryCenterId(Integer deliveryCenterId) {
        this.deliveryCenterId = deliveryCenterId;
    }

    public String getDeliveryCenterName() {
        return deliveryCenterName;
    }

    public void setDeliveryCenterName(String deliveryCenterName) {
        this.deliveryCenterName = deliveryCenterName;
    }
}
