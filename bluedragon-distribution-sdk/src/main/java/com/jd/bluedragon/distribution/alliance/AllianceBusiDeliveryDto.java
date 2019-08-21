package com.jd.bluedragon.distribution.alliance;

import java.io.Serializable;
import java.util.List;

public class AllianceBusiDeliveryDto implements Serializable{

    private static final long serialVersionUID = 1L;

    /* 操作类型 */
    private int opeType;

    /*操作人 id 同基础资料id*/
    private Integer operatorId;

    /*操作人姓名*/
    private String operatorName;

    /*操作人所在单位SiteCode*/
    private Integer operatorSiteCode;

    /*操作人所在单位名称*/
    private String operatorSiteName;

    /*强制交接 不校验余额*/
    private boolean force = Boolean.FALSE;

    List<AllianceBusiDeliveryDetailDto> datas;

    public int getOpeType() {
        return opeType;
    }

    public void setOpeType(int opeType) {
        this.opeType = opeType;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getOperatorSiteCode() {
        return operatorSiteCode;
    }

    public void setOperatorSiteCode(Integer operatorSiteCode) {
        this.operatorSiteCode = operatorSiteCode;
    }

    public String getOperatorSiteName() {
        return operatorSiteName;
    }

    public void setOperatorSiteName(String operatorSiteName) {
        this.operatorSiteName = operatorSiteName;
    }


    public List<AllianceBusiDeliveryDetailDto> getDatas() {
        return datas;
    }

    public void setDatas(List<AllianceBusiDeliveryDetailDto> datas) {
        this.datas = datas;
    }

    public boolean getForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
