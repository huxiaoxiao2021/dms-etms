package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class DeliveryRequest extends JdRequest {
    
    private static final long serialVersionUID = 8464241462768408318L;
    
    /** 收货单位编号 */
    private Integer receiveSiteCode;
    
    /** 箱号 */
    private String boxCode;
    
    /** 批次号 */
    private String sendCode;
    
    /** 周转箱号 */
    private String turnoverBoxCode;
    
    /** 航标发货标示*/
    private Integer transporttype;

    /** 运输类型（默认老发货：0，快运发货：1）*/
    private Integer opType;
    
    public Integer getTransporttype() {
		return transporttype;
	}

	public void setTransporttype(Integer transporttype) {
		this.transporttype = transporttype;
	}

	public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }
    
    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }
    
    public String getBoxCode() {
        return this.boxCode;
    }
    
    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
    
    public String getSendCode() {
        return this.sendCode;
    }
    
    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }
    
    public String getTurnoverBoxCode() {
		return turnoverBoxCode;
	}

	public void setTurnoverBoxCode(String turnoverBoxCode) {
		this.turnoverBoxCode = turnoverBoxCode;
	}

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    @Override
    public String toString() {
        return "DeliveryRequest [receiveSiteCode=" + this.receiveSiteCode + ", boxCode="
                + this.boxCode + ", sendCode=" + this.sendCode + ", toString()=" + super.toString()
                + "]";
    }
    
}
