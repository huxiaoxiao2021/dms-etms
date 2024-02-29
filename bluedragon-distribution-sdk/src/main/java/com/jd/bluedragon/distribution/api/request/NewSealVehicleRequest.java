package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;

import java.util.List;

/**
 * create by zhanglei 2017-05-10
 * 增加运力编码、批次、重量、体积及封车号的多层级关系
 */
public class NewSealVehicleRequest extends JdRequest {

	private static final long serialVersionUID = -4900034488418821323L;

	/**
	 * 代码
	 * */
	private Integer code;

	/**
	 * 信息
	 * */
	private String message;

	/**
	 * 10:封车
	 * 20:解封车
	 * */
	private Integer status;

	/**
	 * 显示条数
	 * */
	private Integer pageNums;

	/**
	 * 封车号
	 * */
	private String sealCode;

	/**
	 * 车牌号
	 * */
	private String vehicleNumber;

	/**
	 *运力编码
	 * */
	private String transportCode;

	/**
	 * 始发站点
	 * */
	private String startSiteId;

	/**
	 * 目的站点s
	 * */
	private String endSiteId;


	/**批次基本信息*/
	private List<SealCarDto> data;

	/**批次号查询条件*/
	private String batchCode;

	/**
	 * 派车明细简码
	 */
	private String transWorkItemCode;


    /** 操作人ERP */
    private String userErp;

    /** 当前站点7为编码	 */
    private String dmsCode;

	/** 当前站点ID	 */
    private Integer dmsSiteId;

	/**
	 * 解封车业务类型
	 */
	private String bizType;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPageNums() {
		return pageNums;
	}

	public void setPageNums(Integer pageNums) {
		this.pageNums = pageNums;
	}

	public String getSealCode() {
		return sealCode;
	}

	public void setSealCode(String sealCode) {
		this.sealCode = sealCode;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getTransportCode() {
		return transportCode;
	}

	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	public String getStartSiteId() {
		return startSiteId;
	}

	public void setStartSiteId(String startSiteId) {
		this.startSiteId = startSiteId;
	}

	public String getEndSiteId() {
		return endSiteId;
	}

	public void setEndSiteId(String endSiteId) {
		this.endSiteId = endSiteId;
	}

	public List<SealCarDto> getData() {
		return data;
	}

	public void setData(List<SealCarDto> data) {
		this.data = data;
	}

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
    }

	public Integer getDmsSiteId() {
		return dmsSiteId;
	}

	public void setDmsSiteId(Integer dmsSiteId) {
		this.dmsSiteId = dmsSiteId;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
}
