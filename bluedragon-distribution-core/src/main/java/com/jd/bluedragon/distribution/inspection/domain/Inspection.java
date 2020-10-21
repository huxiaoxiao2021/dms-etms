package com.jd.bluedragon.distribution.inspection.domain;

import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.apache.commons.lang.StringUtils;

import java.util.Date;


/**
 * 验货表 db: db_dms
 * @author wangzichen
 *
 */
public class Inspection implements java.io.Serializable,Comparable<Inspection>{

	private static final long serialVersionUID = 4279345928379065280L;

	private Long inspectionId;

    /*运单号*/
    private String waybillCode;

    /*箱号*/
    private String boxCode;

    /*包裹号*/
    private String packageBarcode;

    /*状态*/
    private Integer status;

    /*异常类型(退货验货)*/
    private String exceptionType;

    /*验货类型*/
    private Integer inspectionType;
    
    /*操作类型*/
    private Integer operateType;

    /*操作人*/
    private String createUser;

    /*操作人code*/
    private Integer createUserCode;

    /*创建时间*/
    private Date createTime;

    /*操作时间*/
    private Date operateTime;

    /*操作单位Code*/
    private Integer createSiteCode;
    
    /*收货单位Code*/
    private Integer receiveSiteCode;
    
    /*更新人name*/
    private String updateUser;
    
    /*更新人code*/
    private Integer updateUserCode;

    /*更新时间*/
    private Date updateTime;

    private Integer yn;

    protected Integer startNo;
    protected Integer limitNo;
    /**
	 * POP第三方运单号
	 */
	private String thirdWaybillCode;
	/**
	 * 操作标识,1为系统补全，0或空其他，默认为空
	 */
	private Integer popFlag;
	
	/**
     * POP商家ID
     */
    private Integer popSupId;
    
    /**
     * POP商家名称
     */
    private String popSupName;
    
    /**
     * 包裹数量
     */
    private Integer quantity;
    
    /**
     * 滑道号
     */
    private String crossCode;

    /** 运单类型(JYN) */
    private Integer waybillType;
    
    /**
     * POP收货排队号
     */
    private String queueNo;
    
    /**
     * POP收货类型：
     * 	商家直送：1
     * 	托寄送货：2
     *  司机收货：3
     */
    private Integer popReceiveType;
    
    /**
     * 司机Code
     */
    private String driverCode;
    
    /**
     * 司机名称
     */
    private String driverName;
    
    /**
	 * B商家ID
	 */
	private Integer busiId;
	
	/**
	 * B商家名称
	 */
	private String busiName;

	//包裹长
	private Float length;
	//包裹宽
	private Float width;
	//包裹高
	private Float high;
	//包裹体积
	private Float volume;

	/**
	 * @see com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum
	 */
	private Integer bizSource;

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getQueueNo() {
		return queueNo;
	}

	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}

	public Integer getPopReceiveType() {
		return popReceiveType;
	}

	public void setPopReceiveType(Integer popReceiveType) {
		this.popReceiveType = popReceiveType;
	}

	public Integer getPopSupId() {
		return popSupId;
	}

	public void setPopSupId(Integer popSupId) {
		this.popSupId = popSupId;
	}

	public String getPopSupName() {
		return popSupName;
	}

	public void setPopSupName(String popSupName) {
		this.popSupName = popSupName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCrossCode() {
		return crossCode;
	}

	public void setCrossCode(String crossCode) {
		this.crossCode = crossCode;
	}

	public Integer getWaybillType() {
		return waybillType;
	}

	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}

	public Integer getPopFlag() {
		return popFlag;
	}

	public void setPopFlag(Integer popFlag) {
		this.popFlag = popFlag;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Integer getBusiId() {
		return busiId;
	}

	public void setBusiId(Integer busiId) {
		this.busiId = busiId;
	}

	public String getBusiName() {
		return busiName;
	}

	public void setBusiName(String busiName) {
		this.busiName = busiName;
	}

	public Float getLength() {
		return length;
	}

	public void setLength(Float length) {
		this.length = length;
	}

	public Float getWidth() {
		return width;
	}

	public void setWidth(Float width) {
		this.width = width;
	}

	public Float getHigh() {
		return high;
	}

	public void setHigh(Float high) {
		this.high = high;
	}

	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}

	// for json deserialization start
    /*封箱号|封签号*/
    private String sealBoxCode;

    public static final int BUSSINESS_TYPE_THIRD_PARTY=30; 
    public static final int BUSSINESS_TYPE_TRANSFER=50;
    
    // //for json deserialization end

    public Inspection() {
        super();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((boxCode == null) ? 0 : boxCode.hashCode());
		result = prime * result
				+ ((createSiteCode == null) ? 0 : createSiteCode.hashCode());
		result = prime * result
				+ ((inspectionType == null) ? 0 : inspectionType.hashCode());
		result = prime * result
				+ ((packageBarcode == null) ? 0 : packageBarcode.hashCode());
		result = prime * result
				+ ((sealBoxCode == null) ? 0 : sealBoxCode.hashCode());
		result = prime * result
				+ ((waybillCode == null) ? 0 : waybillCode.hashCode());
		result = prime * result + ((yn == null) ? 0 : yn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Inspection other = (Inspection) obj;
		if (boxCode == null) {
			if (other.boxCode != null)
				return false;
		} else if (!boxCode.equals(other.boxCode))
			return false;
		if (createSiteCode == null) {
			if (other.createSiteCode != null)
				return false;
		} else if (!createSiteCode.equals(other.createSiteCode))
			return false;
		if (inspectionType == null) {
			if (other.inspectionType != null)
				return false;
		} else if (!inspectionType.equals(other.inspectionType))
			return false;
		if (packageBarcode == null) {
			if (other.packageBarcode != null)
				return false;
		} else if (!packageBarcode.equals(other.packageBarcode))
			return false;
		if (sealBoxCode == null) {
			if (other.sealBoxCode != null)
				return false;
		} else if (!sealBoxCode.equals(other.sealBoxCode))
			return false;
		if (waybillCode == null) {
			if (other.waybillCode != null)
				return false;
		} else if (!waybillCode.equals(other.waybillCode))
			return false;
		if (yn == null) {
			if (other.yn != null)
				return false;
		} else if (!yn.equals(other.yn))
			return false;
		return true;
	}

	public Long getInspectionId() {
		return inspectionId;
	}

	public void setInspectionId(Long inspectionId) {
		this.inspectionId = inspectionId;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public Integer getInspectionType() {
		return inspectionType;
	}

	public void setInspectionType(Integer inspectionType) {
		this.inspectionType = inspectionType;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Integer getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getUpdateUser() {
		return updateUser;
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
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Integer getStartNo() {
		return startNo;
	}

	public void setStartNo(Integer startNo) {
		this.startNo = startNo;
	}

	public Integer getLimitNo() {
		return limitNo;
	}

	public void setLimitNo(Integer limitNo) {
		this.limitNo = limitNo;
	}

	public String getSealBoxCode() {
		return sealBoxCode;
	}

	public void setSealBoxCode(String sealBoxCode) {
		this.sealBoxCode = sealBoxCode;
	}

	public Integer getUpdateUserCode() {
		return updateUserCode;
	}

	public void setUpdateUserCode(Integer updateUserCode) {
		this.updateUserCode = updateUserCode;
	}

	public String getDriverCode() {
		return driverCode;
	}

	public void setDriverCode(String driverCode) {
		this.driverCode = driverCode;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public Integer getBizSource() {
		return bizSource;
	}

	public void setBizSource(Integer bizSource) {
		this.bizSource = bizSource;
	}

	public static Inspection toInspection(InspectionRequest requestBean, BigWaybillDto bigWaybillDto) {
		Inspection inspection = new Inspection();
		inspection.setWaybillCode(requestBean.getWaybillCode());
		inspection.setBoxCode(requestBean.getBoxCode());
		inspection.setPackageBarcode(requestBean.getPackageBarcode());
		inspection.setExceptionType(requestBean.getExceptionType());
		inspection.setInspectionType(requestBean.getBusinessType());
		inspection.setOperateType(requestBean.getOperateType());
		inspection.setCreateUser(requestBean.getUserName());
		inspection.setCreateUserCode(requestBean.getUserCode());
		inspection.setCreateSiteCode(requestBean.getSiteCode());
		inspection.setReceiveSiteCode(requestBean.getReceiveSiteCode());
		inspection.setUpdateUser(requestBean.getUserName());
		inspection.setUpdateUserCode(requestBean.getUserCode());
		inspection.setSealBoxCode(requestBean.getSealBoxCode());
		inspection.setLength(requestBean.getLength());
		inspection.setWidth(requestBean.getWidth());
		inspection.setHigh(requestBean.getHigh());
		inspection.setVolume(requestBean.getVolume());
		if(bigWaybillDto!=null && bigWaybillDto.getWaybill()!=null && bigWaybillDto.getWaybill().getGoodNumber()!=null){
			inspection.setQuantity(bigWaybillDto.getWaybill().getGoodNumber());
		}else if(StringUtils.isNotBlank(requestBean.getPackageBarcode()) && WaybillUtil.isPackageCode(requestBean.getPackageBarcode())){
			inspection.setQuantity(WaybillUtil.getPackNumByPackCode(requestBean.getPackageBarcode()));
		}
		Date operateTime = StringUtils.isBlank(requestBean.getOperateTime())?new Date():DateHelper.getSeverTime(requestBean.getOperateTime());
        Date createTime = new Date();
        inspection.setOperateTime(operateTime);
		inspection.setCreateTime(createTime);
		inspection.setUpdateTime(operateTime);
		inspection.setBizSource(requestBean.getBizSource());
		return inspection;
	}

	public static Inspection toInspectionByEC(InspectionEC inspectionEC) {
		Inspection inspection = new Inspection();
		inspection.setBoxCode(inspectionEC.getBoxCode());
		inspection.setCreateSiteCode(inspectionEC.getCreateSiteCode());
		inspection.setInspectionType(inspectionEC.getInspectionType());
		inspection.setPackageBarcode(inspectionEC.getPackageBarcode());
		inspection.setReceiveSiteCode(inspectionEC.getReceiveSiteCode());
		inspection.setWaybillCode(inspectionEC.getWaybillCode());
		return inspection;
	}

	public static class Builder{

		/*Required parameters*/
	    /*包裹号*/
	    private String packageBarcode;
	    
	    /*操作单位Code*/
	    private Integer createSiteCode;
	    
	    /*Optional parameters*/
	    /*收货单位Code*/
	    private Integer receiveSiteCode;
	    
	    /*箱号*/
	    private String boxCode;
	    
	    /*验货类型*/
	    private Integer inspectionType;
	    
	    /*更新时间*/
	    private Date updateTime;
	    
	    /*更新人name*/
	    private String updateUser;
	    
	    /*更新人code*/
	    private Integer updateUserCode;

	    private Integer bizSource;

		public Builder(String packageBarcode, Integer createSiteCode) {
			super();
			this.packageBarcode = packageBarcode;
			this.createSiteCode = createSiteCode;
		}
	    
	    public Builder receiveSiteCode(Integer val){
	    	receiveSiteCode = val;
	    	return this;
	    }
	    
	    public Builder boxCode(String val){
	    	boxCode = val;
	    	return this;
	    }
	   
	    public Builder inspectionType( Integer val ){
	    	inspectionType = val;
	    	return this;
	    }
	    
	    public Builder updateTime( Date val ){
	    	updateTime = val!=null?(Date)val.clone():null;
	    	return this;
	    }
	    
	    public Builder updateUser( String val ){
	    	updateUser = val;
	    	return this;
	    }
	    
	    public Builder updateUserCode( Integer val ){
	    	updateUserCode = val;
	    	return this;
	    }

	    public Builder bizSource(Integer val) {
			bizSource = val;
			return this;
		}
	    
	    public Inspection build(){
	    	return new Inspection(this);
	    }
	}
	
	public Inspection(Builder builder) {
		this.packageBarcode = builder.packageBarcode;
		this.createSiteCode = builder.createSiteCode;
		this.boxCode = builder.boxCode;
		this.receiveSiteCode = builder.receiveSiteCode;
		this.inspectionType = builder.inspectionType;
		this.updateTime = builder.updateTime;
		this.updateUser = builder.updateUser;
		this.updateUserCode = builder.updateUserCode;
		this.bizSource = builder.bizSource;
	}

	@Override
	public int compareTo(Inspection inspection) {
		if( null == inspection || StringUtils.isBlank(inspection.getPackageBarcode()) || StringUtils.isBlank(this.getPackageBarcode()) ){
    		return 0;
    	}else {
    		return this.getPackageBarcode().compareTo(inspection.getPackageBarcode());
    	}
	}
}