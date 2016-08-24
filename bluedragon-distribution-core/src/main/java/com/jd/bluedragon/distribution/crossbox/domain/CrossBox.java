package com.jd.bluedragon.distribution.crossbox.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 跨分拣箱号中转
 * 
 * @author xumei1
 */
public class CrossBox implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer originalDmsId;// 始发分拣中心编号
	private String originalDmsName;// 始发分拣中心名称
	private Integer destinationDmsId;// 目的分拣中心编号
	private String destinationDmsName;// 目的分拣中心名称
	private Integer transferOneId;// 中转站1编号
	private String transferOneName;// 中转站1名称
	private Integer transferTwoId;// 中转站2编号
	private String transferTwoName;// 中转站2名称
	private Integer transferThreeId;// 中转站3编号
	private String transferThreeName;// 中转站3名称
	private String fullLine;// 完整路线
	private Date createTime;// 创建时间
	private Date updateTime;// 更新时间
	private String createOperatorName;// 创建人姓名 或者erp账户
	private String updateOperatorName;// 操作人姓名 或者erp账户
	private String remark;// 备注
	private int yn;// 是否有效

	// 失效日期
	private Date expiryDate;

	// 生效日期
	private Date effectiveDate;

	// 原序号
	private int originId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOriginalDmsId() {
		return originalDmsId;
	}

	public void setOriginalDmsId(Integer originalDmsId) {
		this.originalDmsId = originalDmsId;
	}

	public String getOriginalDmsName() {
		return originalDmsName;
	}

	public void setOriginalDmsName(String originalDmsName) {
		this.originalDmsName = originalDmsName;
	}

	public Integer getDestinationDmsId() {
		return destinationDmsId;
	}

	public void setDestinationDmsId(Integer destinationDmsId) {
		this.destinationDmsId = destinationDmsId;
	}

	public String getDestinationDmsName() {
		return destinationDmsName;
	}

	public void setDestinationDmsName(String destinationDmsName) {
		this.destinationDmsName = destinationDmsName;
	}

	public Integer getTransferOneId() {
		return transferOneId;
	}

	public void setTransferOneId(Integer transferOneId) {
		this.transferOneId = transferOneId;
	}

	public String getTransferOneName() {
		return transferOneName;
	}

	public void setTransferOneName(String transferOneName) {
		this.transferOneName = transferOneName;
	}

	public Integer getTransferTwoId() {
		return transferTwoId;
	}

	public void setTransferTwoId(Integer transferTwoId) {
		this.transferTwoId = transferTwoId;
	}

	public String getTransferTwoName() {
		return transferTwoName;
	}

	public void setTransferTwoName(String transferTwoName) {
		this.transferTwoName = transferTwoName;
	}

	public Integer getTransferThreeId() {
		return transferThreeId;
	}

	public void setTransferThreeId(Integer transferThreeId) {
		this.transferThreeId = transferThreeId;
	}

	public String getTransferThreeName() {
		return transferThreeName;
	}

	public void setTransferThreeName(String transferThreeName) {
		this.transferThreeName = transferThreeName;
	}

	public String getFullLine() {
		return fullLine;
	}

	public void setFullLine(String fullLine) {
		this.fullLine = fullLine;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public int getOriginId() {
		return originId;
	}

	public void setOriginId(int originId) {
		this.originId = originId;
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

	public String getCreateOperatorName() {
		return createOperatorName;
	}

	public void setCreateOperatorName(String createOperatorName) {
		this.createOperatorName = createOperatorName;
	}

	public String getUpdateOperatorName() {
		return updateOperatorName;
	}

	public void setUpdateOperatorName(String updateOperatorName) {
		this.updateOperatorName = updateOperatorName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getYn() {
		return yn;
	}

	public void setYn(int yn) {
		this.yn = yn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
