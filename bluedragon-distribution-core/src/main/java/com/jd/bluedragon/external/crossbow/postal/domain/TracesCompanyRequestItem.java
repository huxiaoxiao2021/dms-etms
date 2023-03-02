package com.jd.bluedragon.external.crossbow.postal.domain;

import java.io.Serializable;

/**
 * 轨迹明细实体
 * @author wuyoude
 *
 */
public class TracesCompanyRequestItem implements Serializable{
	private static final long serialVersionUID = 1L;

	private String waybillNo;  //运单号
	private Long opTime;      //操作时间	10位时间戳
	private String opCode;    //操作码
	private String opName;     //操作名
	private String opDesc;     //操作描述
	private String opCountryName;    //操作国家名称	
	private String opOrgProvName;    //操作网点省名	
	private String opOrgCity;    //操作网点城市名称	
	private String opOrgName;    //操作网点名称	
	private String operatorNo;    //操作员工号	
	private String operatorName;     //操作员工名称	
	private String dataSource;    //快递公司代码	
	private String notes;    //备注	
	private String traceType;    //邮件类型	0：国内3：国际
	private String opGis;    //操作GIS坐标	经度，纬度（用逗号拼接在一起），地图展示用
	private String lastOrgName;    //上一站名称	
	private String lastProvName;    //上一站省份名称	
	private String nextOrgName;    //下一站名称	
	private String nextProvName;    //下一站省份名称	

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getOpDesc() {
		return opDesc;
	}

	public void setOpDesc(String opDesc) {
		this.opDesc = opDesc;
	}

	public String getOpCountryName() {
		return opCountryName;
	}

	public void setOpCountryName(String opCountryName) {
		this.opCountryName = opCountryName;
	}

	public String getOpOrgProvName() {
		return opOrgProvName;
	}

	public void setOpOrgProvName(String opOrgProvName) {
		this.opOrgProvName = opOrgProvName;
	}

	public String getOpOrgCity() {
		return opOrgCity;
	}

	public void setOpOrgCity(String opOrgCity) {
		this.opOrgCity = opOrgCity;
	}

	public String getOpOrgName() {
		return opOrgName;
	}

	public void setOpOrgName(String opOrgName) {
		this.opOrgName = opOrgName;
	}

	public String getOperatorNo() {
		return operatorNo;
	}

	public void setOperatorNo(String operatorNo) {
		this.operatorNo = operatorNo;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getTraceType() {
		return traceType;
	}

	public void setTraceType(String traceType) {
		this.traceType = traceType;
	}

	public String getOpGis() {
		return opGis;
	}

	public void setOpGis(String opGis) {
		this.opGis = opGis;
	}

	public String getLastOrgName() {
		return lastOrgName;
	}

	public void setLastOrgName(String lastOrgName) {
		this.lastOrgName = lastOrgName;
	}

	public String getLastProvName() {
		return lastProvName;
	}

	public void setLastProvName(String lastProvName) {
		this.lastProvName = lastProvName;
	}

	public String getNextOrgName() {
		return nextOrgName;
	}

	public void setNextOrgName(String nextOrgName) {
		this.nextOrgName = nextOrgName;
	}

	public String getNextProvName() {
		return nextProvName;
	}

	public void setNextProvName(String nextProvName) {
		this.nextProvName = nextProvName;
	}

	public String getWaybillNo() {
		return waybillNo;
	}

	public void setWaybillNo(String waybillNo) {
		this.waybillNo = waybillNo;
	}

	public Long getOpTime() {
		return opTime;
	}

	public void setOpTime(Long opTime) {
		this.opTime = opTime;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
	
}
