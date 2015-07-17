package com.jd.bluedragon.distribution.send.domain.reverse;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DsBatch")
public class DsBatch {
	
	private int id;
	
	private String batchId;
	
	/** 防重码 */
	private String rsn;
	
	private String operateTime;
	
	/** 备注 */
	private String remark;
	
	private List<DsBox> boxList = new ArrayList<DsBox>();
	
	public String getBatchId() {
		return this.batchId;
	}
	
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	public String getRsn() {
		return this.rsn;
	}
	
	public void setRsn(String rsn) {
		this.rsn = rsn;
	}
	
	public String getOperateTime() {
		return this.operateTime;
	}
	
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	
	public String getRemark() {
		return this.remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public List<DsBox> getBoxList() {
		return this.boxList;
	}
	
	public void setBoxList(List<DsBox> boxList) {
		this.boxList = boxList;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		
		DsBatch dsBatch = (DsBatch) obj;
		if (this.batchId == null) {
			return this.batchId == dsBatch.batchId;
		} else {
			return this.batchId.equals(dsBatch.batchId);
		}
	}
	
	@Override
	public int hashCode() {
		return this.batchId.hashCode();
	}
	
}
