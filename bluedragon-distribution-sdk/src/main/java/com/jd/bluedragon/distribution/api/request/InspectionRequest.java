package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
/**
 * for inspection
 * @author wangzichen
 *
 */
public class InspectionRequest extends JdRequest{

	private static final long serialVersionUID = 7304582203701562507L;

	/*箱号*/
    private String boxCode;

    /*包裹号或者运单号*/
    private String packageBarOrWaybillCode;

    /*包裹号*/
    private String packageBarcode;

    /*运单号*/
    private String waybillCode;

    /*异常类型(退货验货)*/
    private String exceptionType;

    /*验货类型*/
    private Integer inspectionType;

    /*收货单位*/
    private Integer receiveSiteCode;

    /*封箱号|封签号*/
    private String sealBoxCode;

    /*操作类型*/
    private Integer operateType;

	/*设备编码*/
	private String machineCode;

	private Float length;

	private Float width;

	private Float high;

	private Float volume;

	private Integer bizSource;

    private int pageNo;

    private int pageSize;

    private int totalPage;
    
    /**
     *@see com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum
     * 操作者类型编码
     */
	private Integer operatorTypeCode;
    /**
     * 操作者id
     */
	private String operatorId;
	/**
	 * 按单验货标识
	 */
	private Boolean waybillInspectionFlag;
    /**
     * 操作信息对象
     */
	private OperatorData operatorData;
	
	private String operatorDataJson;

	/**
	 * 操作流水表主键
	 */
	private Long operateFlowId;

	/**
	 * 车牌号
	 */
	private String vehicleNumber;
	
    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getPackageBarOrWaybillCode() {
		return packageBarOrWaybillCode;
	}

	public void setPackageBarOrWaybillCode(String packageBarOrWaybillCode) {
		this.packageBarOrWaybillCode = packageBarOrWaybillCode;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getSealBoxCode() {
		return sealBoxCode;
	}

	public void setSealBoxCode(String sealBoxCode) {
		this.sealBoxCode = sealBoxCode;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getInspectionType() {
		return inspectionType;
	}

	public void setInspectionType(Integer inspectionType) {
		this.inspectionType = inspectionType;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Float getLength() {
		return length;
	}

	public void setLength(Float length) {
		this.length = length;
	}

	public Float getWidth() {
		return width;
	}

	public void setWidth(Float width) {
		this.width = width;
	}

	public Float getHigh() {
		return high;
	}

	public void setHigh(Float high) {
		this.high = high;
	}

	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}

	public Integer getBizSource() {
		return bizSource;
	}

	public void setBizSource(Integer bizSource) {
		this.bizSource = bizSource;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	@Override
    public String toString() {
        return "InspectionRequest{" +
                "boxCode='" + boxCode + '\'' +
                ", packageBarOrWaybillCode='" + packageBarOrWaybillCode + '\'' +
                ", packageBarcode='" + packageBarcode + '\'' +
                ", waybillCode='" + waybillCode + '\'' +
                ", exceptionType='" + exceptionType + '\'' +
                ", inspectionType=" + inspectionType +
                ", receiveSiteCode=" + receiveSiteCode +
                ", sealBoxCode='" + sealBoxCode + '\'' +
                ", operateType=" + operateType +
                ", length=" + length +
                ", width=" + width +
                ", high=" + high +
                ", volume=" + volume +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", totalPage=" + totalPage +
				",waybillInspectionFlag=" + waybillInspectionFlag +
                '}';
    }

	public Integer getOperatorTypeCode() {
		return operatorTypeCode;
	}

	public void setOperatorTypeCode(Integer operatorTypeCode) {
		this.operatorTypeCode = operatorTypeCode;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public Boolean getWaybillInspectionFlag() {
		return waybillInspectionFlag;
	}

	public void setWaybillInspectionFlag(Boolean waybillInspectionFlag) {
		this.waybillInspectionFlag = waybillInspectionFlag;
	}

	public OperatorData getOperatorData() {
		return operatorData;
	}

	public void setOperatorData(OperatorData operatorData) {
		this.operatorData = operatorData;
	}

	public String getOperatorDataJson() {
		return operatorDataJson;
	}

	public void setOperatorDataJson(String operatorDataJson) {
		this.operatorDataJson = operatorDataJson;
	}

	public Long getOperateFlowId() {
		return operateFlowId;
	}

	public void setOperateFlowId(Long operateFlowId) {
		this.operateFlowId = operateFlowId;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
}
