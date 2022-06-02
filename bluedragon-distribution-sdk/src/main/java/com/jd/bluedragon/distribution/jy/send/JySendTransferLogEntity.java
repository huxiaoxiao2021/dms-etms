package com.jd.bluedragon.distribution.jy.send;


import java.io.Serializable;
import java.util.Date;

/**
 * 发货任务迁移记录表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
public class JySendTransferLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 被迁移的发货任务批次号
	 */
	private String sendCode;
	/**
	 * 迁移前所属的主任务编号
	 */
	private String fromSendVehicleBizId;
	/**
	 * 迁移前所属的子任务编号
	 */
	private String fromSendVehicleDetailBizId;
	/**
	 * 迁移前所属的主任务编号
	 */
	private String toSendVehicleBizId;
	/**
	 * 迁移前所属的子任务编号
	 */
	private String toSendVehicleDetailBizId;
	/**
	 * 创建人ERP
	 */
	private String createUserErp;
	/**
	 * 创建人姓名
	 */
	private String createUserName;
	/**
	 * 更新人ERP
	 */
	private String updateUserErp;
	/**
	 * 更新人姓名
	 */
	private String updateUserName;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 是否删除：1-有效，0-删除
	 */
	private Integer yn;
	/**
	 * 数据库时间
	 */
	private Date ts;
	/**
	 * 1：同流向迁移（自建任务绑定）2：同流向迁移（迁移功能）3：不同流向迁移-原批次解除 4：不同流向迁移-新批次新增
	 */
	private Integer type;

	public Long setId(Long id){
		return this.id = id;
	}

		public Long getId(){
		return this.id;
	}
	public String setSendCode(String sendCode){
		return this.sendCode = sendCode;
	}

		public String getSendCode(){
		return this.sendCode;
	}
	public String setFromSendVehicleBizId(String fromSendVehicleBizId){
		return this.fromSendVehicleBizId = fromSendVehicleBizId;
	}

		public String getFromSendVehicleBizId(){
		return this.fromSendVehicleBizId;
	}
	public String setFromSendVehicleDetailBizId(String fromSendVehicleDetailBizId){
		return this.fromSendVehicleDetailBizId = fromSendVehicleDetailBizId;
	}

		public String getFromSendVehicleDetailBizId(){
		return this.fromSendVehicleDetailBizId;
	}
	public String setToSendVehicleBizId(String toSendVehicleBizId){
		return this.toSendVehicleBizId = toSendVehicleBizId;
	}

		public String getToSendVehicleBizId(){
		return this.toSendVehicleBizId;
	}
	public String setToSendVehicleDetailBizId(String toSendVehicleDetailBizId){
		return this.toSendVehicleDetailBizId = toSendVehicleDetailBizId;
	}

		public String getToSendVehicleDetailBizId(){
		return this.toSendVehicleDetailBizId;
	}
	public String setCreateUserErp(String createUserErp){
		return this.createUserErp = createUserErp;
	}

		public String getCreateUserErp(){
		return this.createUserErp;
	}
	public String setCreateUserName(String createUserName){
		return this.createUserName = createUserName;
	}

		public String getCreateUserName(){
		return this.createUserName;
	}
	public String setUpdateUserErp(String updateUserErp){
		return this.updateUserErp = updateUserErp;
	}

		public String getUpdateUserErp(){
		return this.updateUserErp;
	}
	public String setUpdateUserName(String updateUserName){
		return this.updateUserName = updateUserName;
	}

		public String getUpdateUserName(){
		return this.updateUserName;
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
	public Integer setType(Integer type){
		return this.type = type;
	}

		public Integer getType(){
		return this.type;
	}

}
