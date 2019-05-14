package com.jd.bluedragon.distribution.sorting.domain;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;

public class Sorting implements Cloneable,java.io.Serializable,Comparable<Sorting> {
    
	private static final long serialVersionUID = 197799100239086759L;
	
	public static final Integer STATUS_DEFALUT = 0;
    public static final Integer STATUS_DONE = 1;
    public static final Integer STATUS_ERROR = 4;
    
    public static final Integer IS_CANCEL = 1;
    public static final Integer IS_NOT_CANCEL = 0;
    
    public static final Integer TYPE_REVERSE = 20;
    
    /** 全局唯一ID */
    private Long id;
    
    /** 箱号 */
    private String boxCode;
    
    /** 批量处理批次号 */
    private String bsendCode;
    
    /** 箱号集合 */
    private String boxCodes;
    
    /** 运单号 */
    private String waybillCode;
    
    /** 包裹号 */
    private String packageCode;
    
    /** 取件单号 */
    private String pickupCode;
    
    /** 创建站点编号 */
    private Integer createSiteCode;
    
    /** 创建站点名称 */
    private String createSiteName;
    
    /** 接收站点编号 */
    private Integer receiveSiteCode;
    
    /** 接收站点名称 */
    private String receiveSiteName;
    
    /** 创建人编号 */
    private Integer createUserCode;
    
    /** 创建人 */
    private String createUser;
    
    /** 创建时间 */
    private Date createTime;
    
    /** pda操作时间 */
    private Date operateTime;
    
    /** 最后操作人编号 */
    private Integer updateUserCode;
    
    /** 最后操作人 */
    private String updateUser;
    
    /** 最后修改时间 */
    private Date updateTime;
    
    /** 是否处理 '0' 未处理 '1' 回传运单完毕 */
    private Integer status;
    
    /** 分拣类型 '10' 自营 '20' 退货 '30'三方 */
    private Integer type;
    
    /** 分拣类型 '0' 自营 '1' 退货 */
    private Integer isCancel;
    
    /** 执行次数 */
    private Integer excuteCount;
    
    /** 执行时间 */
    private Date excuteTime;
    
    /** 是否删除 '0' 删除 '1' 使用 */
    private Integer yn;
    
    /** 备件库退货原因 */
    private String spareReason;
    
    /** 逆向物流接入报损系统 0正常分拣 1  报丢分拣*/
    private Integer isLoss;
    
    /**逆向标识字段 1：报损 2：三方七折退备件库，以后取代isLoss*/
    private Integer featureType;
    
    /**亚一返仓标识 b1 c1*/
    private Integer whReverse;

    private List<String> boxCodeList;
    
    public Sorting() {
        super();
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBoxCode() {
        return this.boxCode;
    }
    
    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
    
    public String getBoxCodes() {
        return this.boxCodes;
    }
    
    public void setBoxCodes(String boxCodes) {
        this.boxCodes = boxCodes;
    }
    
    public String getWaybillCode() {
        return this.waybillCode;
    }
    
    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
    
    public String getPackageCode() {
        return this.packageCode;
    }
    
    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
    
    public String getPickupCode() {
        return this.pickupCode;
    }
    
    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }
    
    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }
    
    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }
    
    public String getCreateSiteName() {
        return this.createSiteName;
    }
    
    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
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
    
    public Integer getCreateUserCode() {
        return this.createUserCode;
    }
    
    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }
    
    public String getCreateUser() {
        return this.createUser;
    }
    
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    
    public Date getCreateTime() {
    	return createTime!=null?(Date)createTime.clone():null;
    }
    
    public void setCreateTime(Date createTime) {
    	this.createTime = createTime!=null?(Date)createTime.clone():null;
    }
    
    public Date getOperateTime() {
    	return operateTime!=null?(Date)operateTime.clone():null;
    }
    
    public void setOperateTime(Date operateTime) {
    	this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
    }
    
    public Integer getUpdateUserCode() {
        return this.updateUserCode;
    }
    
    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }
    
    public String getUpdateUser() {
        return this.updateUser;
    }
    
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
    
    public Date getUpdateTime() {
    	return updateTime!=null?(Date)updateTime.clone():null;
    }
    
    public void setUpdateTime(Date updateTime) {
    	this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
    }
    
    public Integer getYn() {
        return this.yn;
    }
    
    public void setYn(Integer yn) {
        this.yn = yn;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public Integer getIsCancel() {
        return this.isCancel;
    }
    
    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }
    
    public Integer getExcuteCount() {
        return this.excuteCount;
    }
    
    public void setExcuteCount(Integer excuteCount) {
        this.excuteCount = excuteCount;
    }
    
    public Date getExcuteTime() {
    	return excuteTime!=null?(Date)excuteTime.clone():null;
    }
    
    public void setExcuteTime(Date excuteTime) {
    	this.excuteTime = excuteTime!=null?(Date)excuteTime.clone():null;
    }
    
    public String getSpareReason() {
        return this.spareReason;
    }
    
    public void setSpareReason(String spareReason) {
        this.spareReason = spareReason;
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

    public List<String> getBoxCodeList() {
        return boxCodeList;
    }

    public void setBoxCodeList(List<String> boxCodeList) {
        this.boxCodeList = boxCodeList;
    }

    public Boolean isCancel() {
        if (this.isCancel == null) {
            return Boolean.FALSE;
        }
        
        if (Sorting.IS_CANCEL.equals(this.isCancel)) {
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
    
    public Boolean isReverse() {
        if (this.type == null) {
            return Boolean.FALSE;
        }
        
        if (Constants.BUSSINESS_TYPE_REVERSE == this.type.intValue()) {
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
    
    
    public String getBsendCode() {
		return bsendCode;
	}

	public void setBsendCode(String bsendCode) {
		this.bsendCode = bsendCode;
	}

	public Boolean isForward() {
        if (this.type == null) {
            return Boolean.FALSE;
        }
        
        if (Constants.BUSSINESS_TYPE_POSITIVE == this.type.intValue()
                || Constants.BUSSINESS_TYPE_THIRD_PARTY == this.type.intValue()) {
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
    
    public static Sorting toSorting(SortingRequest request) {
        Sorting sorting = new Sorting();
        
        String aBoxCode = StringHelper.removeRN(request.getBoxCode());
        String aPackageCode = StringHelper.removeRN(request.getPackageCode());
        
        sorting.setCreateSiteCode(request.getSiteCode());
        sorting.setCreateSiteName(request.getSiteName());
        sorting.setReceiveSiteCode(request.getReceiveSiteCode());
        sorting.setReceiveSiteName(request.getReceiveSiteName());
        sorting.setCreateUser(request.getUserName());
        sorting.setCreateUserCode(request.getUserCode());
        sorting.setUpdateUser(request.getUserName());
        sorting.setUpdateUserCode(request.getUserCode());
        sorting.setIsCancel(request.getIsCancel());
        sorting.setType(request.getBusinessType());
        sorting.setOperateTime(DateHelper.getSeverTime(request.getOperateTime()));
        sorting.setCreateTime(DateHelper.getSeverTime(request.getOperateTime()));
        sorting.setBoxCode(StringHelper.isNotEmpty(aBoxCode) ? aBoxCode : aPackageCode);
        sorting.setIsLoss(request.getIsLoss());
        sorting.setFeatureType(request.getFeatureType());
        sorting.setWhReverse(request.getWhReverse());
        sorting.setBsendCode(request.getBsendCode());
        
        //扫W及WA的包裹号时  下面的包裹号判断 会覆盖运单号的赋值
        if (WaybillUtil.isSurfaceCode(aPackageCode)) {
//            sorting.setWaybillCode(BusinessHelper.getWaybillCode(aPackageCode));
        	sorting.setWaybillCode(aPackageCode);
        }
        if (WaybillUtil.isPackageCode(aPackageCode)) {
            sorting.setPackageCode(aPackageCode);
            sorting.setWaybillCode(WaybillUtil.getWaybillCode(aPackageCode));
        }
        if (WaybillUtil.isWaybillCode(aPackageCode)) {
            sorting.setWaybillCode(WaybillUtil.getWaybillCode(aPackageCode));
        }
        
        return sorting;
    }
    
    public static Sorting toSorting2(SortingRequest request) {
        Sorting sorting = new Sorting();
        String aPackageCode = request.getPackageCode();
        
        sorting.setBoxCode(request.getBoxCode());
        sorting.setOperateTime(DateHelper.getSeverTime(request.getOperateTime()));//增加操作时间
        
        if (WaybillUtil.isPackageCode(aPackageCode)) {
            sorting.setPackageCode(aPackageCode);
        } else if (WaybillUtil.isWaybillCode(aPackageCode)) {
            sorting.setWaybillCode(WaybillUtil.getWaybillCode(aPackageCode));
        } else if (BusinessHelper.isBoxcode(aPackageCode)) {
            sorting.setBoxCode(aPackageCode);
        }
        
        sorting.setCreateSiteCode(request.getSiteCode());
        sorting.setUpdateUser(request.getUserName());
        sorting.setUpdateUserCode(request.getUserCode());
        sorting.setType(request.getBusinessType());
        return sorting;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.boxCode == null ? 0 : this.boxCode.hashCode());
        result = prime * result
                + (this.createSiteCode == null ? 0 : this.createSiteCode.hashCode());
        result = prime * result + (this.isCancel == null ? 0 : this.isCancel.hashCode());
        result = prime * result + (this.packageCode == null ? 0 : this.packageCode.hashCode());
        result = prime * result + (this.pickupCode == null ? 0 : this.pickupCode.hashCode());
        result = prime * result
                + (this.receiveSiteCode == null ? 0 : this.receiveSiteCode.hashCode());
        result = prime * result + (this.type == null ? 0 : this.type.hashCode());
        result = prime * result + (this.waybillCode == null ? 0 : this.waybillCode.hashCode());
        result = prime * result + (this.yn == null ? 0 : this.yn.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Sorting other = (Sorting) obj;
        if (this.boxCode == null) {
            if (other.boxCode != null) {
                return false;
            }
        } else if (!this.boxCode.equals(other.boxCode)) {
            return false;
        }
        if (this.createSiteCode == null) {
            if (other.createSiteCode != null) {
                return false;
            }
        } else if (!this.createSiteCode.equals(other.createSiteCode)) {
            return false;
        }
        if (this.isCancel == null) {
            if (other.isCancel != null) {
                return false;
            }
        } else if (!this.isCancel.equals(other.isCancel)) {
            return false;
        }
        if (this.packageCode == null) {
            if (other.packageCode != null) {
                return false;
            }
        } else if (!this.packageCode.equals(other.packageCode)) {
            return false;
        }
        if (this.pickupCode == null) {
            if (other.pickupCode != null) {
                return false;
            }
        } else if (!this.pickupCode.equals(other.pickupCode)) {
            return false;
        }
        if (this.receiveSiteCode == null) {
            if (other.receiveSiteCode != null) {
                return false;
            }
        } else if (!this.receiveSiteCode.equals(other.receiveSiteCode)) {
            return false;
        }
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!this.type.equals(other.type)) {
            return false;
        }
        if (this.waybillCode == null) {
            if (other.waybillCode != null) {
                return false;
            }
        } else if (!this.waybillCode.equals(other.waybillCode)) {
            return false;
        }
        if (this.yn == null) {
            if (other.yn != null) {
                return false;
            }
        } else if (!this.yn.equals(other.yn)) {
            return false;
        }
        return true;
    }
    
    public static class Builder {
        /** 箱号 */
        private String boxCode;
        
        /** 运单号 */
        private String waybillCode;
        
        /** 包裹号 */
        private String packageCode;
        
        /** 分拣类型 '10' 自营 '20' 退货 '30'三方 */
        private Integer type;
        
        /** 创建站点编号 */
        private Integer createSiteCode;
        
        /** 创建站点名称 */
        private String createSiteName;
        
        /** 接收站点编号 */
        private Integer receiveSiteCode;
        
        /** 接收站点名称 */
        private String receiveSiteName;
        
        /** 最后操作人编号 */
        private Integer updateUserCode;
        
        /** 最后操作人 */
        private String updateUser;
        
        /** 最后修改时间 */
        private Date updateTime;
        
        /** 是否删除 '0' 删除 '1' 使用 */
        private Integer yn;
        
        public Builder(Integer createSiteCode) {
            super();
            this.createSiteCode = createSiteCode;
        }
        
        public Builder boxCode(String val) {
            this.boxCode = val;
            return this;
        }
        
        public Builder waybillCode(String val) {
            this.waybillCode = val;
            return this;
        }
        
        public Builder packageCode(String val) {
            this.packageCode = val;
            return this;
        }
        
        public Builder type(Integer val) {
            this.type = val;
            return this;
        }
        
        public Builder createSiteCode(Integer val) {
            this.createSiteCode = val;
            return this;
        }
        
        public Builder createSiteName(String val) {
            this.createSiteName = val;
            return this;
        }
        
        public Builder receiveSiteCode(Integer val) {
            this.receiveSiteCode = val;
            return this;
        }
        
        public Builder receiveSiteName(String val) {
            this.receiveSiteName = val;
            return this;
        }
        
        public Builder updateUserCode(Integer val) {
            this.updateUserCode = val;
            return this;
        }
        
        public Builder updateUser(String val) {
            this.updateUser = val;
            return this;
        }
        
        public Builder updateTime(Date val) {
            this.updateTime = val!=null?(Date)val.clone():null;
            return this;
        }
        
        public Builder yn(Integer val) {
            this.yn = val;
            return this;
        }
        
        public Sorting build() {
            return new Sorting(this);
        }
    }
    
    public Sorting(Builder builder) {
        this.boxCode = builder.boxCode;
        this.waybillCode = builder.waybillCode;
        this.packageCode = builder.packageCode;
        this.type = builder.type;
        this.createSiteCode = builder.createSiteCode;
        this.createSiteName = builder.createSiteName;
        this.receiveSiteCode = builder.receiveSiteCode;
        this.receiveSiteName = builder.receiveSiteName;
        this.updateUserCode = builder.updateUserCode;
        this.updateUser = builder.updateUser;
        this.updateTime = builder.updateTime;
        this.yn = builder.yn;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	@Override
	public int compareTo(Sorting sorting) {
		if( null == sorting || StringUtils.isBlank(sorting.getPackageCode()) || StringUtils.isBlank(this.getPackageCode()) ){
    		return 0;
    	}else {
    		return this.getPackageCode().compareTo(sorting.getPackageCode());
    	}
	}

}