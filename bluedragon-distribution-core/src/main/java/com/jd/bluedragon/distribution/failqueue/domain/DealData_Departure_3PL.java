package com.jd.bluedragon.distribution.failqueue.domain;


public class DealData_Departure_3PL {

	/** 发货交接单号-发货批次号 */
	private String sendCode;

	/**承运商编号*/
	private Integer carrierId;
	
	/**承运商名称*/
	private String carrierName;

	/** 运单号(第三方) 非数据库表中属性 */
	private String thirdWaybillCode;
	
	/** 全局唯一ID */
    private Long sendMId;

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public Integer getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(Integer carrierId) {
		this.carrierId = carrierId;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	public Long getSendMId() {
		return sendMId;
	}

	public void setSendMId(Long sendMId) {
		this.sendMId = sendMId;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        DealData_Departure_3PL other = (DealData_Departure_3PL) obj;

        return this.toString().equals(other.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public String toString(){
    	return ""+this.sendCode+this.thirdWaybillCode+this.carrierId;
    }

}
