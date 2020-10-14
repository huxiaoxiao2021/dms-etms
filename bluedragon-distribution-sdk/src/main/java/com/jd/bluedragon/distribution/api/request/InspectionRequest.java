package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
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

	private Float length;

	private Float width;

	private Float high;

	private Float volume;

    private int pageNo;

    private int pageSize;

    private int totalPage;

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
                '}';
    }
}
