package com.jd.bluedragon.distribution.api.request;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.domain.OperatorData;

public class SortingRequest extends JdRequest {

    private static final long serialVersionUID = 556555029682781842L;

    /** 箱号 */
    private String boxCode;
    
    /** 批量处理批次号 */
    private String bsendCode;

    /** 封签号 */
    private String sealCode;

    /** 包裹号 */
    private String packageCode;

    /** 运单号 */
    private String waybillCode;

    /** 是否取消分拣 '0' 正常 '1' 取消 */
    private Integer isCancel;

    /** 接收站点编号 */
    private Integer receiveSiteCode;

    /** 接收站点名称 */
    private String receiveSiteName;
    
    /** 逆向物流接入报损系统 0正常分拣 1  报丢分拣*/
    private Integer isLoss;
    
    /**逆向标识字段 1：报损 2：三方七折退备件库，以后取代isLoss*/
    private Integer featureType;
    
    /**亚一返仓标识 b1 c1*/
    private Integer whReverse;
    
    private List<String> packages;

    private Integer bizSource;
    
    /**
     *@see com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum
     * 操作者类型编码
     */
	private Integer operatorTypeCode;
    /**
     * 操作者id
     */
	private String operatorId;
    /**
     * 操作信息对象
     */
	private OperatorData operatorData;
    /**
     * 当前正在操作的场地code
     */
    private Integer currentSiteCode;
    /**
     * 是否跳过取消集包之前的检查条件 false-不跳过  true-跳过
     */
    private Boolean conditionCheck;

    public String getBoxCode() {
        return this.boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageCode() {
        return this.packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getIsCancel() {
        return this.isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return this.receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getSealCode() {
        return this.sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }
    
    public Integer getIsLoss() {
		return isLoss;
	}

	public void setIsLoss(Integer isLoss) {
		this.isLoss = isLoss;
	}

    public Integer getFeatureType() {
		return featureType;
	}

	public void setFeatureType(Integer featureType) {
		this.featureType = featureType;
	}
	
	public Integer getWhReverse() {
		return whReverse;
	}

	public void setWhReverse(Integer whReverse) {
		this.whReverse = whReverse;
	}

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	
	public String getBsendCode() {
		return bsendCode;
	}

	public void setBsendCode(String bsendCode) {
		this.bsendCode = bsendCode;
	}

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        SortingRequest other = (SortingRequest) obj;

        if (this.boxCode == null || this.packageCode == null || this.receiveSiteCode == null
                || super.getBusinessType() == null) {
            return this.boxCode == other.boxCode || this.packageCode == other.packageCode
                    || this.receiveSiteCode == other.receiveSiteCode
                    || super.getBusinessType() == other.getBusinessType();
        }

        return this.boxCode.equals(other.boxCode) && this.packageCode.equals(other.packageCode)
                && this.receiveSiteCode.equals(other.receiveSiteCode)
                && super.getBusinessType().equals(other.getBusinessType());
    }

    @Override
    public int hashCode() {
        return 31 + this.boxCode.hashCode() + this.packageCode.hashCode()
                + this.receiveSiteCode.hashCode() + super.getBusinessType().hashCode();
    }

	public Integer getOperatorTypeCode() {
		return operatorTypeCode;
	}

	public void setOperatorTypeCode(Integer operatorTypeCode) {
		this.operatorTypeCode = operatorTypeCode;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public OperatorData getOperatorData() {
		return operatorData;
	}

	public void setOperatorData(OperatorData operatorData) {
		this.operatorData = operatorData;
	}

    public Integer getCurrentSiteCode() {
        return currentSiteCode;
    }

    public void setCurrentSiteCode(Integer currentSiteCode) {
        this.currentSiteCode = currentSiteCode;
    }

    public Boolean getConditionCheck() {
        return conditionCheck;
    }

    public void setConditionCheck(Boolean conditionCheck) {
        this.conditionCheck = conditionCheck;
    }
}
