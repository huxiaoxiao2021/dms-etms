package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;
import java.util.Date;

public class DepartureSendDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2982174785634993993L;
	
	private String sendCode;
    private Integer receiveSiteCode;
    private String receiveSiteName;
    private Integer sendUserCode;
    private String sendUser;
    private String carCode;
    private String shieldsCarCode;
    private Date createTime;
    private Long departureCarId;
	public String getSendCode() {
		return sendCode;
	}
	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}
	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}
	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}
	public String getReceiveSiteName() {
		return receiveSiteName;
	}
	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}
	public Integer getSendUserCode() {
		return sendUserCode;
	}
	public void setSendUserCode(Integer sendUserCode) {
		this.sendUserCode = sendUserCode;
	}
	public String getSendUser() {
		return sendUser;
	}
	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}
	public String getCarCode() {
		return carCode;
	}
	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}
	public String getShieldsCarCode() {
		return shieldsCarCode;
	}
	public void setShieldsCarCode(String shieldsCarCode) {
		this.shieldsCarCode = shieldsCarCode;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getDepartureCarId() {
		return departureCarId;
	}
	public void setDepartureCarId(Long departureCarId) {
		this.departureCarId = departureCarId;
	}
    
    
	

}
