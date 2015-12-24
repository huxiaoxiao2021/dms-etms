package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class SealVehicleResponse extends JdResponse {

	private static final long serialVersionUID = -9096768337471427500L;

	/** 全局唯一ID */
	private Long id;

	/** 封签(箱子|车辆)编号 */
	private String sealCode;

	/** 箱号 */
	private String boxCode;

	/** 车辆编号 */
	private String vehicleCode;

	/** 司机编号 */
	private Integer driverCode;

	/** 司机 */
	private String driver;

	/** 封签号 , 可以传多个, 逗号隔开 */
	private String sealCodes;
	
	public static final Integer CODE_2006_ERROR = 2006;
    public static final String MESSAGE_2006_ERROR = "取消封签异常，请核实信息是否正确";
    
    public static final Integer CODE_2007_ERROR = 2007;
    public static final String MESSAGE_2007_ERROR = "	已完成发车的封车签号不允许撤销封车";

	public SealVehicleResponse() {
		super();
	}

	public SealVehicleResponse(Integer code, String message) {
		super(code, message);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSealCode() {
		return this.sealCode;
	}

	public void setSealCode(String sealCode) {
		this.sealCode = sealCode;
	}

	public String getBoxCode() {
		return this.boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getVehicleCode() {
		return this.vehicleCode;
	}

	public void setVehicleCode(String vehicleCode) {
		this.vehicleCode = vehicleCode;
	}

	public Integer getDriverCode() {
		return this.driverCode;
	}

	public void setDriverCode(Integer driverCode) {
		this.driverCode = driverCode;
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getSealCodes() {
		return sealCodes;
	}

	public void setSealCodes(String sealCodes) {
		this.sealCodes = sealCodes;
	}

}
