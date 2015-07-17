package com.jd.bluedragon.distribution.wss.dto;

import java.io.Serializable;
import java.util.Date;

public class SealVehicleSummaryDto  implements Serializable {

	private static final long serialVersionUID = 8837698044251669890L;

	/** 全局唯一ID */
    private Long id;

    /** 封车编号 */
    private String code;

    /** 车辆编号 */
    private String vehicleCode;

    /** 司机编号 */
    private Integer driverCode;

    /** 司机姓名 */
    private String driver;
    
    /** 创建人编号 */
    private Integer createUserCode;

    /** 创建人 */
    private String createUser;

    /** 创建时间 */
    private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVehicleCode() {
		return vehicleCode;
	}

	public void setVehicleCode(String vehicleCode) {
		this.vehicleCode = vehicleCode;
	}

	public Integer getDriverCode() {
		return driverCode;
	}

	public void setDriverCode(Integer driverCode) {
		this.driverCode = driverCode;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Integer getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public String getCreateUser() {
		return createUser;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
}
