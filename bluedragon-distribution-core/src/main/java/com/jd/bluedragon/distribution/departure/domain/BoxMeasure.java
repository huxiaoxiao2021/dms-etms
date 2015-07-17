package com.jd.bluedragon.distribution.departure.domain;

import java.util.Date;

public class BoxMeasure {
	
	public static final Integer STATUS_RETRIEVED_YES = 1; // 已经抓取体积和重量
	public static final Integer STATUS_RETRIEVED_NO = 0; // 未抓取体积和重量
	
	/** 箱子体积重量记录ID */
	private long BoxMeasureId;

	/** 箱号 */
	private String boxCode;
	
	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 重量 */
	private Double weight;

	/** 体积 */
	private Double volume;
	
	/** 创建时间 */
	private Date createTime;
	
	/** 修改时间 */
	private Date updateTime;

	/** 是否已抓取运单数据 1已经抓取 2 未抓取 */
	private int status;
	
	/** 是否删除 '0' 删除 '1' 使用 */
	private int yn;

	public long getBoxMeasureId() {
		return BoxMeasureId;
	}

	public void setBoxMeasureId(long boxMeasureId) {
		BoxMeasureId = boxMeasureId;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getYn() {
		return yn;
	}

	public void setYn(int yn) {
		this.yn = yn;
	}
	
	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}

	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}
	
	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	@Override
	public String toString() {
		return "BoxMeasure [BoxMeasureId=" + BoxMeasureId + ", boxCode="
				+ boxCode + ", createSiteCode=" + createSiteCode + ", weight="
				+ weight + ", volume=" + volume + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", status=" + status + ", yn="
				+ yn + "]";
	}
}
