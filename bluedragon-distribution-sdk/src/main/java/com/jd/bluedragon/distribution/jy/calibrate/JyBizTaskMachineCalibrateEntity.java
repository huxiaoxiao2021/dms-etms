package com.jd.bluedragon.distribution.jy.calibrate;


import java.io.Serializable;
import java.util.Date;

/**
 * 天官赐福 ◎ 百无禁忌
 * 拣运设备校准表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-12-07 15:35:53
 */
public class JyBizTaskMachineCalibrateEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 设备编码
	 */
	private String machineCode;
	/**
	 * 校准任务开始时间
	 */
	private Date calibrateTaskStartTime;
	/**
	 * 校准任务关闭时间
	 */
	private Date calibrateTaskCloseTime;
	/**
	 * 创建人ERP
	 */
	private String createUserErp;
	/**
	 * 修改人ERP
	 */
	private String updateUserErp;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 逻辑删除标志,0-删除,1-正常
	 */
	private Integer yn;
	/**
	 * 数据库时间
	 */
	private Date ts;

	public Long setId(Long id){
		return this.id = id;
	}

		public Long getId(){
		return this.id;
	}
	public String setMachineCode(String machineCode){
		return this.machineCode = machineCode;
	}

		public String getMachineCode(){
		return this.machineCode;
	}
	public Date setCalibrateTaskStartTime(Date calibrateTaskStartTime){
		return this.calibrateTaskStartTime = calibrateTaskStartTime;
	}

		public Date getCalibrateTaskStartTime(){
		return this.calibrateTaskStartTime;
	}
	public Date setCalibrateTaskCloseTime(Date calibrateTaskCloseTime){
		return this.calibrateTaskCloseTime = calibrateTaskCloseTime;
	}

		public Date getCalibrateTaskCloseTime(){
		return this.calibrateTaskCloseTime;
	}
	public String setCreateUserErp(String createUserErp){
		return this.createUserErp = createUserErp;
	}

	public String getUpdateUserErp() {
		return updateUserErp;
	}

	public void setUpdateUserErp(String updateUserErp) {
		this.updateUserErp = updateUserErp;
	}

	public String getCreateUserErp(){
		return this.createUserErp;
	}
	public Date setCreateTime(Date createTime){
		return this.createTime = createTime;
	}

		public Date getCreateTime(){
		return this.createTime;
	}
	public Date setUpdateTime(Date updateTime){
		return this.updateTime = updateTime;
	}

		public Date getUpdateTime(){
		return this.updateTime;
	}
	public Integer setYn(Integer yn){
		return this.yn = yn;
	}

		public Integer getYn(){
		return this.yn;
	}
	public Date setTs(Date ts){
		return this.ts = ts;
	}

		public Date getTs(){
		return this.ts;
	}

}
