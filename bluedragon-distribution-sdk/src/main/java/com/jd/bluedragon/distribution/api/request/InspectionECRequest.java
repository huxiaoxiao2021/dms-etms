package com.jd.bluedragon.distribution.api.request;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * 
 * @author wangzichen
 *
 */
public class InspectionECRequest extends JdRequest{

	private static final long serialVersionUID = -1748022841107243989L;
	/*异常类型:多验、少验*/
	private Integer inspectionECType;
	
	/*传入的三方id或者code*/
	private String partnerIdOrCode;
	
	/*传入箱号*/
	private String boxCode;
	
	/*接收站点*/
	private Integer receiveSiteCode;
	
	/*操作类型：查询、多验直接配送*/
	private Integer operationType;

	private List<InspectionRequest> packages;
	
	public Integer getInspectionECType() {
		return inspectionECType;
	}

	public void setInspectionECType(Integer inspectionECType) {
		this.inspectionECType = inspectionECType;
	}

	public List<InspectionRequest> getPackages() {
		return packages;
	}

	public void setPackages(List<InspectionRequest> packages) {
		this.packages = packages;
	}

	public Integer getOperationType() {
		return operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getPartnerIdOrCode() {
		return partnerIdOrCode;
	}

	public void setPartnerIdOrCode(String partnerIdOrCode) {
		this.partnerIdOrCode = partnerIdOrCode;
	}
	
	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	@Override
	public String toString() {
		return "InspectionECRequest [inspectionECType=" + inspectionECType
				+ ", partnerIdOrCode=" + partnerIdOrCode + ", receiveSiteCode="
				+ receiveSiteCode + ", operationType=" + operationType
				+ ", packages=" + packages + "]";
	}
	
}
