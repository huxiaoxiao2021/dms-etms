package com.jd.bluedragon.distribution.consumable.domain;

/**
 * Created by hanjiaxing1 on 2018/8/20.
 */
public class DmsConsumableRelationDetailInfo extends PackingConsumableInfo {

    /** 分拣中心编号 */
    private Integer dmsId;

    /** 分拣中心名称 */
    private String dmsName;

    /** 开启状态（0关闭，1开启） */
    private Integer status;

    public Integer getDmsId() {
        return dmsId;
    }

    public void setDmsId(Integer dmsId) {
        this.dmsId = dmsId;
    }

    public String getDmsName() {
        return dmsName;
    }

    public void setDmsName(String dmsName) {
        this.dmsName = dmsName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
