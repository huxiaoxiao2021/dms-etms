package com.jd.bluedragon.distribution.api.response;

import java.util.Date;
import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

public class DeparturePrintResponse extends JdResponse{
	
	private static final long serialVersionUID = -3463897192420276033L;
    public static final int CODE_NULL_RESULT = 40000;
    public static final String MESSAGE_NULL_RESULT = "运输信息为空";
    public static final int CODE_UNEXCEPT_RESULT = 70000;
    public static final String MESSAGE_UNEXCEPT_RESULT = "非干线数据";
	/**
	 * 始发分拣中心
	 */
	private String createSite;
	/**
	 * 目的地，##分割
	 */
	private String receiveSites;
	
	private String carCode;
	/**
	 * 承运商
	 */
	private String sendUser;
	/**
	 * 班次
	 */
	private String runNumberName;
	/**
	 * 发车时间
	 */
	private String createTime;
	/**
	 * 封车号
	 */
    private String shieldsCarCode;

    private Long shieldsCarId;
    
  	/** 重量   */
  	private Double weight;
  	
  	/** 体积   */
  	private Double volume;
  	
  	private String m3;

    /** 承运商编码 */
    private String sendUserCode;

    /** 运单号 */
    private String waybillCode;

    /** 收货时间 */
    private String receiveTime;

    /** 运输方式 */
    private String transportType;

    /** 车次号 */
    private Long departureCarID;

    /** 运力编码 */
    private String capacityCode;

    /** 干支线运输 */
    private Integer routeType;

    /** 批次号 */
    private String sendCode;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Long getDepartureCarID() {
        return departureCarID;
    }

    public void setDepartureCarID(Long departureCarID) {
        this.departureCarID = departureCarID;
    }

    public String getCapacityCode() {
        return capacityCode;
    }

    public void setCapacityCode(String capacityCode) {
        this.capacityCode = capacityCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
  	 /** 打印时间 */
    private String printTime;
  	

	public String getCreateSite() {
		return createSite;
	}

	public void setCreateSite(String createSite) {
		this.createSite = createSite;
	}

    public String getSendUserCode() {
        return sendUserCode;
    }

    public void setSendUserCode(String sendUserCode) {
        this.sendUserCode = sendUserCode;
    }

	public String getReceiveSites() {
		return receiveSites;
	}

	public void setReceiveSites(String receiveSites) {
		this.receiveSites = receiveSites;
	}

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}


	public String getRunNumberName() {
		return runNumberName;
	}

	public void setRunNumberName(String runNumberName) {
		this.runNumberName = runNumberName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getShieldsCarCode() {
		return shieldsCarCode;
	}

	public void setShieldsCarCode(String shieldsCarCode) {
		this.shieldsCarCode = shieldsCarCode;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public String getM3() {
		return m3;
	}

	public void setM3(String m3) {
		this.m3 = m3;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	

	public Long getShieldsCarId() {
		return shieldsCarId;
	}

	public void setShieldsCarId(Long shieldsCarId) {
		this.shieldsCarId = shieldsCarId;
	}

	public String getPrintTime() {
		return printTime;
	}

	public void setPrintTime(String printTime) {
		this.printTime = printTime;
	}
  	
  	
  	
  	
  	
  	

}
