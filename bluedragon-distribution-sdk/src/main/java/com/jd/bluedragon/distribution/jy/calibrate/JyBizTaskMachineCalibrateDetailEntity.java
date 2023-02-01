package com.jd.bluedragon.distribution.jy.calibrate;


import java.io.Serializable;
import java.util.Date;

/**
 * 天官赐福 ◎ 百无禁忌
 * 拣运设备校准任务明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-12-07 15:35:53
 */
public class JyBizTaskMachineCalibrateDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 设备主表id
	 */
	private Long refMachineKey;
	/**
	 * 设备编码
	 */
	private String machineCode;
	/**
	 * 任务创建时间
	 */
	private Date taskCreateTime;
	/**
	 * 任务截止时间
	 */
	private Date taskEndTime;
	/**
	 * 任务状态:0-待处理,1-已完成,2-超时,3-关闭
	 */
	private Integer taskStatus;
	/**
	 * 重量校准时间
	 */
	private Date weightCalibrateTime;
	/**
	 * 体积校准时间
	 */
	private Date volumeCalibrateTime;
	/**
	 * 校准完成时间
	 */
	private Date calibrateFinishTime;
	/**
	 * 重量校准状态:0-未校准,1-合格,2-不合格
	 */
	private Integer weightCalibrateStatus;
	/**
	 * 体积校准状态:0-未校准,1-合格,2-不合格
	 */
	private Integer volumeCalibrateStatus;
	/**
	 * 设备状态:0-未校准,1-合格,2-不合格
	 */
	private Integer machineStatus;
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

	public Long getRefMachineKey() {
		return refMachineKey;
	}

	public void setRefMachineKey(Long refMachineKey) {
		this.refMachineKey = refMachineKey;
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
	public Date setTaskCreateTime(Date taskCreateTime){
		return this.taskCreateTime = taskCreateTime;
	}

		public Date getTaskCreateTime(){
		return this.taskCreateTime;
	}
	public Date setTaskEndTime(Date taskEndTime){
		return this.taskEndTime = taskEndTime;
	}

		public Date getTaskEndTime(){
		return this.taskEndTime;
	}
	public Integer setTaskStatus(Integer taskStatus){
		return this.taskStatus = taskStatus;
	}

		public Integer getTaskStatus(){
		return this.taskStatus;
	}
	public Date setWeightCalibrateTime(Date weightCalibrateTime){
		return this.weightCalibrateTime = weightCalibrateTime;
	}

		public Date getWeightCalibrateTime(){
		return this.weightCalibrateTime;
	}
	public Date setVolumeCalibrateTime(Date volumeCalibrateTime){
		return this.volumeCalibrateTime = volumeCalibrateTime;
	}

		public Date getVolumeCalibrateTime(){
		return this.volumeCalibrateTime;
	}
	public Date setCalibrateFinishTime(Date calibrateFinishTime){
		return this.calibrateFinishTime = calibrateFinishTime;
	}

		public Date getCalibrateFinishTime(){
		return this.calibrateFinishTime;
	}
	public Integer setWeightCalibrateStatus(Integer weightCalibrateStatus){
		return this.weightCalibrateStatus = weightCalibrateStatus;
	}

		public Integer getWeightCalibrateStatus(){
		return this.weightCalibrateStatus;
	}
	public Integer setVolumeCalibrateStatus(Integer volumeCalibrateStatus){
		return this.volumeCalibrateStatus = volumeCalibrateStatus;
	}

		public Integer getVolumeCalibrateStatus(){
		return this.volumeCalibrateStatus;
	}
	public Integer setMachineStatus(Integer machineStatus){
		return this.machineStatus = machineStatus;
	}

		public Integer getMachineStatus(){
		return this.machineStatus;
	}
	public String setCreateUserErp(String createUserErp){
		return this.createUserErp = createUserErp;
	}

		public String getCreateUserErp(){
		return this.createUserErp;
	}
	public String setUpdateUserErp(String updateUserErp){
		return this.updateUserErp = updateUserErp;
	}

		public String getUpdateUserErp(){
		return this.updateUserErp;
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
