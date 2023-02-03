package com.jd.bluedragon.distribution.jy.send;


import java.io.Serializable;
import java.util.Date;

/**
 * 发货任务附属表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JySendAttachmentEntity implements Serializable {

	private static final long serialVersionUID = -5851491920135847337L;

    public JySendAttachmentEntity() {}

    public JySendAttachmentEntity(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * send_vehicle业务主键
	 */
	private String sendVehicleBizId;
	/**
	 * 操作场地ID
	 */
	private Long operateSiteId;
	/**
	 * 车辆是否已到 0-未到 1-已到
	 */
	private Integer vehicleArrived;
	/**
	 * 拍照图片，多个逗号分隔
	 */
	private String imgUrl;
	/**
	 * 封车前拍照图片，多个逗号分隔
	 */
	private String sealImgUrl;
	/**
	 * 无任务发货备注信息
	 */
	private String remark;
	/**
	 * 操作时间
	 */
	private Date operateTime;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSendVehicleBizId() {
		return sendVehicleBizId;
	}

	public void setSendVehicleBizId(String sendVehicleBizId) {
		this.sendVehicleBizId = sendVehicleBizId;
	}

	public Long getOperateSiteId() {
		return operateSiteId;
	}

	public void setOperateSiteId(Long operateSiteId) {
		this.operateSiteId = operateSiteId;
	}

	public Integer getVehicleArrived() {
		return vehicleArrived;
	}

	public void setVehicleArrived(Integer vehicleArrived) {
		this.vehicleArrived = vehicleArrived;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getSealImgUrl() {
		return sealImgUrl;
	}

	public void setSealImgUrl(String sealImgUrl) {
		this.sealImgUrl = sealImgUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getCreateUserErp() {
		return createUserErp;
	}

	public void setCreateUserErp(String createUserErp) {
		this.createUserErp = createUserErp;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getUpdateUserErp() {
		return updateUserErp;
	}

	public void setUpdateUserErp(String updateUserErp) {
		this.updateUserErp = updateUserErp;
	}

	public String getUpdateUserName() {
		return updateUserName;
	}

	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Date getTs() {
		return ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}
}
