package com.jd.bluedragon.distribution.newseal.domain;

import java.util.*;

import com.jd.bluedragon.Constants;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @ClassName: PreSealVehicle
 * @Description: 预封车数据表-实体类
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
public class PreSealVehicle extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 预封车UUID：运力+“-”+时间long */
	private String preSealUuid;

	 /** 始发站点Code */
	private Integer createSiteCode;

	 /** 始发站点名称 */
	private String createSiteName;

	 /** 目的站点Code */
	private Integer receiveSiteCode;

	 /** 目的站点名称 */
	private String receiveSiteName;

	 /** 运力编码 */
	private String transportCode;

	 /** 车牌号 */
	private String vehicleNumber;

	 /** 封签号多个逗号分割 */
	private String sealCodes;

	 /** 运力最晚发车时间 */
	private String sendCarTime;

	 /** 封车状态：0-预封车，1-封车 */
	private Integer status;

	 /** 创建人ERP */
	private String createUserErp;

	 /** 创建人name */
	private String createUserName;

	 /** 更新人ERP */
	private String updateUserErp;

	 /** 更新人name */
	private String updateUserName;

	 /** 系统标识 */
	private String source;

	 /** 预封车实操时间 */
	private Date operateTime;

    /** 批次信息 */
	private List<SealVehicles> sendCodes;

    /** 车牌号List */
    private List<String> vehicleNumbers;

	/** 车牌号:封签号; */
	private String sealCodeStr;

    /** 车牌号:封签号Map */
    private Map<String, String> vehicleSealCodeMap;

	/** 封车来源 */
	private Integer preSealSource;

	/** 运输类型 */
	private Integer transWay;

	/** 运输类型名称 */
	private String transWayName;

	/** 重量 */
	private Double weight;

	/** 体积 */
	private Double volume;

	/** 车牌号:封签号Map */
	private Map<String, VehicleMeasureInfo> vehicleMeasureMap;
    /** 站点对应的预封车uuids */
	private List<String> preSealUuids;
    /** 已选中的批次信息 */
	private List<String> selectedSendCodes;

	/**
	 * The set method for preSealUuid.
	 * @param preSealUuid
	 */
	public void setPreSealUuid(String preSealUuid) {
		this.preSealUuid = preSealUuid;
	}

	/**
	 * The get method for preSealUuid.
	 * @return this.preSealUuid
	 */
	public String getPreSealUuid() {
		return this.preSealUuid;
	}

	/**
	 * The set method for createSiteCode.
	 * @param createSiteCode
	 */
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	/**
	 * The get method for createSiteCode.
	 * @return this.createSiteCode
	 */
	public Integer getCreateSiteCode() {
		return this.createSiteCode;
	}

	/**
	 * The set method for createSiteName.
	 * @param createSiteName
	 */
	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	/**
	 * The get method for createSiteName.
	 * @return this.createSiteName
	 */
	public String getCreateSiteName() {
		return this.createSiteName;
	}

	/**
	 * The set method for receiveSiteCode.
	 * @param receiveSiteCode
	 */
	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	/**
	 * The get method for receiveSiteCode.
	 * @return this.receiveSiteCode
	 */
	public Integer getReceiveSiteCode() {
		return this.receiveSiteCode;
	}

	/**
	 * The set method for receiveSiteName.
	 * @param receiveSiteName
	 */
	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	/**
	 * The get method for receiveSiteName.
	 * @return this.receiveSiteName
	 */
	public String getReceiveSiteName() {
		return this.receiveSiteName;
	}

	/**
	 * The set method for transportCode.
	 * @param transportCode
	 */
	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	/**
	 * The get method for transportCode.
	 * @return this.transportCode
	 */
	public String getTransportCode() {
		return this.transportCode;
	}

	/**
	 * The set method for vehicleNumber.
	 * @param vehicleNumber
	 */
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	/**
	 * The get method for vehicleNumber.
	 * @return this.vehicleNumber
	 */
	public String getVehicleNumber() {
		return this.vehicleNumber;
	}

	/**
	 * The set method for sealCodes.
	 * @param sealCodes
	 */
	public void setSealCodes(String sealCodes) {
		this.sealCodes = sealCodes;
	}

	/**
	 * The get method for sealCodes.
	 * @return this.sealCodes
	 */
	public String getSealCodes() {
		return this.sealCodes;
	}

	/**
	 * The set method for sendCarTime.
	 * @param sendCarTime
	 */
	public void setSendCarTime(String sendCarTime) {
		this.sendCarTime = sendCarTime;
	}

	/**
	 * The get method for sendCarTime.
	 * @return this.sendCarTime
	 */
	public String getSendCarTime() {
		return this.sendCarTime;
	}

	/**
	 * The set method for status.
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * The get method for status.
	 * @return this.status
	 */
	public Integer getStatus() {
		return this.status;
	}

	/**
	 * The set method for createUserErp.
	 * @param createUserErp
	 */
	public void setCreateUserErp(String createUserErp) {
		this.createUserErp = createUserErp;
	}

	/**
	 * The get method for createUserErp.
	 * @return this.createUserErp
	 */
	public String getCreateUserErp() {
		return this.createUserErp;
	}

	/**
	 * The set method for createUserName.
	 * @param createUserName
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 * The get method for createUserName.
	 * @return this.createUserName
	 */
	public String getCreateUserName() {
		return this.createUserName;
	}

	/**
	 * The set method for updateUserErp.
	 * @param updateUserErp
	 */
	public void setUpdateUserErp(String updateUserErp) {
		this.updateUserErp = updateUserErp;
	}

	/**
	 * The get method for updateUserErp.
	 * @return this.updateUserErp
	 */
	public String getUpdateUserErp() {
		return this.updateUserErp;
	}

	/**
	 * The set method for updateUserName.
	 * @param updateUserName
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	/**
	 * The get method for updateUserName.
	 * @return this.updateUserName
	 */
	public String getUpdateUserName() {
		return this.updateUserName;
	}

	/**
	 * The set method for source.
	 * @param source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * The get method for source.
	 * @return this.source
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * The set method for operateTime.
	 * @param operateTime
	 */
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	/**
	 * The get method for operateTime.
	 * @return this.operateTime
	 */
	public Date getOperateTime() {
		return this.operateTime;
	}

    public List<SealVehicles> getSendCodes() {
	    if(sendCodes == null){
	        sendCodes = new ArrayList<>();
        }
        return sendCodes;
    }

    public void setSendCodes(List<SealVehicles> sendCodes) {
        this.sendCodes = sendCodes;
    }

    public List<String> getVehicleNumbers() {
        if(vehicleNumbers == null){
            vehicleNumbers = new ArrayList<>();
        }
        return vehicleNumbers;
    }

    public void setVehicleNumbers(List<String> vehicleNumbers) {
        this.vehicleNumbers = vehicleNumbers;
    }

    public String getSealCodeStr() {
        return sealCodeStr;
    }

    public void setSealCodeStr(String sealCodeStr) {
        this.sealCodeStr = sealCodeStr;
    }

    public void appendSealCodeStr(String sealCodeStr) {
        if(StringUtils.isEmpty(getSealCodeStr())){
            setSealCodeStr(sealCodeStr);
        }else {
            setSealCodeStr(getSealCodeStr() + Constants.SEPARATOR_SEMICOLON + sealCodeStr);
        }
    }

    public Map<String, String> getVehicleSealCodeMap() {
        return vehicleSealCodeMap;
    }

    public void setVehicleSealCodeMap(Map<String, String> vehicleSealCodeMap) {
        this.vehicleSealCodeMap = vehicleSealCodeMap;
    }

    public void putVehicleSealCode(String vehicleNumber, String sealCodes) {
        if(this.vehicleSealCodeMap == null){
            this.vehicleSealCodeMap = new HashMap<>();
        }
        this.vehicleSealCodeMap.put(vehicleNumber, sealCodes);
    }

	public Integer getPreSealSource() {
		return preSealSource;
	}

	public void setPreSealSource(Integer preSealSource) {
		this.preSealSource = preSealSource;
	}

	public Integer getTransWay() {
		return transWay;
	}

	public void setTransWay(Integer transWay) {
		this.transWay = transWay;
	}

	public String getTransWayName() {
		return transWayName;
	}

	public void setTransWayName(String transWayName) {
		this.transWayName = transWayName;
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

	public Map<String, VehicleMeasureInfo> getVehicleMeasureMap() {
		if(this.vehicleMeasureMap == null){
			this.vehicleMeasureMap = new HashMap<>();
		}
		return vehicleMeasureMap;
	}

	public void setVehicleMeasureMap(Map<String, VehicleMeasureInfo> vehicleMeasureMap) {
		this.vehicleMeasureMap = vehicleMeasureMap;
	}

	public List<String> getPreSealUuids() {
		return preSealUuids;
	}

	public void setPreSealUuids(List<String> preSealUuids) {
		this.preSealUuids = preSealUuids;
	}

	public List<String> getSelectedSendCodes() {
		return selectedSendCodes;
	}

	public void setSelectedSendCodes(List<String> selectedSendCodes) {
		this.selectedSendCodes = selectedSendCodes;
	}
}
