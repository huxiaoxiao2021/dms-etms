package com.jd.bluedragon.distribution.weight.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 交接称重实体
 *
 */
public class HandOverWeightDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String waybillCode;//运单号
    private String packageCode;	//包裹号
    private Integer operatorSiteCode;	//操作站点id
    private Double againWeight; //复重重量
    private String againVolumn; //复重体积	长*宽*高
    private Integer	againOperatorId; //操作人id
    private String againOperatorName;	//操作人名称
    private Date againOperatorTime;		//操作时间
    private String source = "DMS";	//来源

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getOperatorSiteCode() {
        return operatorSiteCode;
    }

    public void setOperatorSiteCode(Integer operatorSiteCode) {
        this.operatorSiteCode = operatorSiteCode;
    }

    public Double getAgainWeight() {
        return againWeight;
    }

    public void setAgainWeight(Double againWeight) {
        this.againWeight = againWeight;
    }

    public String getAgainVolumn() {
        return againVolumn;
    }

    public void setAgainVolumn(String againVolumn) {
        this.againVolumn = againVolumn;
    }

    public Integer getAgainOperatorId() {
        return againOperatorId;
    }

    public void setAgainOperatorId(Integer againOperatorId) {
        this.againOperatorId = againOperatorId;
    }

    public String getAgainOperatorName() {
        return againOperatorName;
    }

    public void setAgainOperatorName(String againOperatorName) {
        this.againOperatorName = againOperatorName;
    }

    public Date getAgainOperatorTime() {
        return againOperatorTime;
    }

    public void setAgainOperatorTime(Date againOperatorTime) {
        this.againOperatorTime = againOperatorTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
