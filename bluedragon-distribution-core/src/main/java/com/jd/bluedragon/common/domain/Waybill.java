package com.jd.bluedragon.common.domain;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.product.domain.Product;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-7 上午10:19:54 运单
 */
public class Waybill implements Serializable {

	private static final long serialVersionUID = 5163001426091767255L;

	public static Integer TYPE_LUXURY = 1;
	public static Integer TYPE_SUBWAY = 2;
	public static Integer TYPE_CONTRACT = 3;
	public static Integer TYPE_ZITI = 4;

	public static final Integer TYPE_GENERAL = 0;
	public static final Integer TYPE_POP_FBP = 21;

	public static final int IS_PRINT_PACK = 1; // 已打印包裹

	public static final int IS_PRINT_INVOICE = 1;// 已打印发票

	/** 运单编号 */
	private String waybillCode;

	/** POP商家ID */
	private Integer popSupId;

	/** POP商家名称 */
	private String popSupName;

	/** 站点编号 */
	private Integer siteCode = 0;

	/** 站点名称 */
	private String siteName;

	/** 支付类型 */
	private Integer paymentType = 0;

	private Integer shipmentType = 0;

	/** 特殊属性 */
	private String sendPay;

	/** 重量 */
	private Double weight;

	/** 数量 */
	private Integer quantity;

	/** 地址 */
	private String address;

	/** 是否打印包裹 */
	private int isPrintPack;

	/** 是否打印发票 */
	private int isPrintInvoice;

	/** 机构ID */
	private Integer orgId;

	/** 库房ID */
	private Integer storeId;

	/** 运单类型(JYN) */
	private Integer type;

	/** 滑道号 */
	private String crossCode;

	/** 中转站编号 */
	private Integer transferStationId;

	/**	 * 中转站名称	 */
	private String transferStationName;

	/**	 * 库房ID	 */
	private Integer distributeStoreId;

	private String distributeStoreName;

	/**	 * 笼车号	 */
	private String trolleyCode;

	/**	 * 订单状态（分拣中心包装后的）	 */
	private Integer statusCode;

	/**	 * 订单状态描述	 */
	private String statusMessage;

	/**	 * 打印次数	 */
	private Integer printCount = 0;

	/**	 * 航空标示	 */
	private Integer airSigns;

	/**	 * 目标分拣中心ID	 */
	private Integer targetDmsCode;

	/**	 * 目标分拣中心名称	 */
	private String targetDmsName;
	
	/**	 * 目的滑道口	 */
	private String targetDmsDkh;
	
	/**	 * 目的地-笼车号	 */
	private String targetDmsLch;
	
	/**	 * B商家ID	 */
	private Integer busiId;
	
	/**	 * B商家名称	 */
	private String busiName;
	
	/**特殊标识*/
	private Integer specialLogo;
	
	/**标签样式的版本*/
	private String lablelVersion;
	
	/**标签样式的版本*/
	private String jsonData;
	
	/**cky2*/
	private Integer cky2;
	
	/**oldCode*/
	private Integer oldCode;
	/**
	 * 包裹数量
	 */
	private int packageNum;
	/**
	 * 包裹集合
	 */
	private List<Pack> packList;

	private List<Product> proList;


	private String waybillSign;

	/*路区号*/
	private String road;

	/*服务号*/
	private String serviceCode;

	/**
	 * 应收款（总金额+运费-优惠）
	 */
	private Double recMoney;

	/**
	 * 收件人手机
	 */
	private String receiverMobile;


	/*收件人座机*/
	private String receiverTel;

	/**
	 * 收件人姓名
	 */
	private String receiverName;

	/**
	 * 邮政编码
	 */
	private String ReceiverZipCode;

	private  Integer ProvinceNameId;

	/**
	 * 省
	 */
	private  String ProvinceName;


	private  Integer CityNameId;

	/**
	 * 市
	 */
	private  String CityName;


	private  Integer CountryNameId;

	/**
	 * 县
	 */
	private  String CountryName;

	/**
	 * 发票数量
	 */
	private String importantHint;
	/**
	 * 自营订单号
	 */
	private String orderId;

	private String busiOrderCode;

    /**
     * COD代收款数
     */
    private Double codMoney;


	/**
	 * 运单类型,Cachem发送的CWaybill类MQ,是waybillType, 当前系统的是type, 都指运单类型
	 */
	private Integer waybillType;


	/**
	 * 体积
	 */
	private Double volume;

	/**
	 * 配送方式
	 */
	private Integer distributeType;



	/*路区号*/
	private String roadCode;



	private boolean delivery;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 当前运单数据归属的表名
	 */
	private String tableName;


	/** 任务特性专用字段 start **/

	/**
	 * 任务执行状态
	 **/
	private Integer taskStatus;

	/**
	 * 任务执行次数
	 **/
	private Integer executeCount;

	/**
	 * 任务执行时间
	 **/
	private Date executeTime;

	/**
	 * 当前发货批次对应的目的分拣中心  这个字段用于本地ver分拣中心过滤属于自己的运单数据  added by zhanglei at 20161009
	 */
	private String dmsDisCode;

	/**
	 * 复重
	 */
	private Double againWeight;

	/**
	 * 复量方
	 */
	private String spareColumn2;

	/**
	 * 运费
	 */
	private String freight;

	public Integer getWaybillType() {
		return waybillType;
	}

	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Integer getDistributeType() {
		return distributeType;
	}

	public void setDistributeType(Integer distributeType) {
		this.distributeType = distributeType;
	}

	public String getRoadCode() {
		return roadCode;
	}

	public void setRoadCode(String roadCode) {
		this.roadCode = roadCode;
	}

	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Integer getExecuteCount() {
		return executeCount;
	}

	public void setExecuteCount(Integer executeCount) {
		this.executeCount = executeCount;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	public String getDmsDisCode() {
		return dmsDisCode;
	}

	public void setDmsDisCode(String dmsDisCode) {
		this.dmsDisCode = dmsDisCode;
	}

	public Double getAgainWeight() {
		return againWeight;
	}

	public void setAgainWeight(Double againWeight) {
		this.againWeight = againWeight;
	}

	public String getSpareColumn2() {
		return spareColumn2;
	}

	public void setSpareColumn2(String spareColumn2) {
		this.spareColumn2 = spareColumn2;
	}

	public String getFreight() {
		return freight;
	}

	public void setFreight(String freight) {
		this.freight = freight;
	}

	public String getWaybillCode() {
		return this.waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getPopSupId() {
		return this.popSupId;
	}

	public void setPopSupId(Integer popSupId) {
		this.popSupId = popSupId;
	}

	public String getPopSupName() {
		return this.popSupName;
	}

	public void setPopSupName(String popSupName) {
		this.popSupName = popSupName;
	}

	public Integer getSiteCode() {
		return this.siteCode;
	}

	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	public String getSiteName() {
		return this.siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Integer getPaymentType() {
		return this.paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

	public String getSendPay() {
		return this.sendPay;
	}

	public void setSendPay(String sendPay) {
		this.sendPay = sendPay;
	}

	public Double getWeight() {
		return this.weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getIsPrintPack() {
		return this.isPrintPack;
	}

	public void setIsPrintPack(int isPrintPack) {
		this.isPrintPack = isPrintPack;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public int getIsPrintInvoice() {
		return this.isPrintInvoice;
	}

	public void setIsPrintInvoice(int isPrintInvoice) {
		this.isPrintInvoice = isPrintInvoice;
	}

	public Integer getOrgId() {
		return this.orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCrossCode() {
		return this.crossCode;
	}

	public void setCrossCode(String crossCode) {
		this.crossCode = crossCode;
	}

	public Integer getTransferStationId() {
		return this.transferStationId;
	}

	public void setTransferStationId(Integer transferStationId) {
		this.transferStationId = transferStationId;
	}

	public List<Product> getProList() {
		return proList;
	}

	public void setProList(List<Product> proList) {
		this.proList = proList;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getDistributeStoreName() {
		return distributeStoreName;
	}

	public void setDistributeStoreName(String distributeStoreName) {
		this.distributeStoreName = distributeStoreName;
	}

	public Integer getShipmentType() {
		return shipmentType;
	}

	public void setShipmentType(Integer shipmentType) {
		this.shipmentType = shipmentType;
	}

	public String getTransferStationName() {
		return transferStationName;
	}

	public void setTransferStationName(String transferStationName) {
		this.transferStationName = transferStationName;
	}

	public Integer getDistributeStoreId() {
		return distributeStoreId;
	}

	public void setDistributeStoreId(Integer distributeStoreId) {
		this.distributeStoreId = distributeStoreId;
	}

	public String getTrolleyCode() {
		return trolleyCode;
	}

	public void setTrolleyCode(String trolleyCode) {
		this.trolleyCode = trolleyCode;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Integer getPrintCount() {
		return printCount;
	}

	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}

	public Integer getAirSigns() {
		return airSigns;
	}

	public void setAirSigns(Integer airSigns) {
		this.airSigns = airSigns;
	}

	public Integer getTargetDmsCode() {
		return targetDmsCode;
	}

	public void setTargetDmsCode(Integer targetDmsCode) {
		this.targetDmsCode = targetDmsCode;
	}

	public String getTargetDmsName() {
		return targetDmsName;
	}

	public void setTargetDmsName(String targetDmsName) {
		this.targetDmsName = targetDmsName;
	}

	public String getTargetDmsDkh() {
		return targetDmsDkh;
	}

	public void setTargetDmsDkh(String targetDmsDkh) {
		this.targetDmsDkh = targetDmsDkh;
	}

	public String getTargetDmsLch() {
		return targetDmsLch;
	}

	public void setTargetDmsLch(String targetDmsLch) {
		this.targetDmsLch = targetDmsLch;
	}

	public Integer getBusiId() {
		return busiId;
	}

	public void setBusiId(Integer busiId) {
		this.busiId = busiId;
	}

	public String getBusiName() {
		return busiName;
	}

	public void setBusiName(String busiName) {
		this.busiName = busiName;
	}

	public List<Pack> getPackList() {
		return packList;
	}

	public void setPackList(List<Pack> packList) {
		this.packList = packList;
	}

	public void setStatusAndMessage(Integer statusCode, String statusMessage) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public Integer getSpecialLogo() {
		return specialLogo;
	}

	public void setSpecialLogo(Integer specialLogo) {
		this.specialLogo = specialLogo;
	}

	public String getLablelVersion() {
		return lablelVersion;
	}

	public void setLablelVersion(String lablelVersion) {
		this.lablelVersion = lablelVersion;
	}

	public Integer getCky2() {
		return cky2;
	}

	public void setCky2(Integer cky2) {
		this.cky2 = cky2;
	}

	public Integer getOldCode() {
		return oldCode;
	}

	public void setOldCode(Integer oldCode) {
		this.oldCode = oldCode;
	}

	public String getWaybillSign() {
		return waybillSign;
	}

	public void setWaybillSign(String waybillSign) {
		this.waybillSign = waybillSign;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public Double getRecMoney() {
		return recMoney;
	}

	public void setRecMoney(Double recMoney) {
		this.recMoney = recMoney;
	}

	public String getReceiverMobile() {
		return receiverMobile;
	}

	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}

	public String getReceiverTel() {
		return receiverTel;
	}

	public void setReceiverTel(String receiverTel) {
		this.receiverTel = receiverTel;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverZipCode() {
		return ReceiverZipCode;
	}

	public Integer getProvinceNameId() {
		return ProvinceNameId;
	}

	public void setProvinceNameId(Integer provinceNameId) {
		ProvinceNameId = provinceNameId;
	}

	public Integer getCityNameId() {
		return CityNameId;
	}

	public void setCityNameId(Integer cityNameId) {
		CityNameId = cityNameId;
	}

	public Integer getCountryNameId() {
		return CountryNameId;
	}

	public void setCountryNameId(Integer countryNameId) {
		CountryNameId = countryNameId;
	}

	public void setReceiverZipCode(String receiverZipCode) {
		ReceiverZipCode = receiverZipCode;
	}

	public String getProvinceName() {
		return ProvinceName;
	}

	public void setProvinceName(String provinceName) {
		ProvinceName = provinceName;
	}

	public String getCityName() {
		return CityName;
	}

	public void setCityName(String cityName) {
		CityName = cityName;
	}

	public String getCountryName() {
		return CountryName;
	}

	public void setCountryName(String countryName) {
		CountryName = countryName;
	}

	public String getImportantHint() {
		return importantHint;
	}

	public void setImportantHint(String importantHint) {
		this.importantHint = importantHint;
	}


	/**
	 * 是否出库 出库：TRUE 未出库：FALSE
	 * 
	 * @return
	 */
	public Boolean isDelivery() {
		if (this.quantity == null || this.quantity <= 0) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * 验证运单类型是否是双L（SOPL、LBP）类型订单，若是则返回true，不是返回false
	 * 
	 * @param type
	 * @return
	 */
	public static Boolean isPopWaybillType(Integer type) {
		if (type != null
				&& (type.equals(Constants.POP_LBP) || type
						.equals(Constants.POP_SOPL))) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * 验证运单类型是否是双L（SOPL、LBP、SOP）类型订单，若是则返回true，不是返回false
	 * 
	 * @param type
	 * @return
	 */
	public static Boolean isPopType(Integer type) {
		if (type != null
				&& (type.equals(Constants.POP_LBP) || type
						.equals(Constants.POP_SOPL)
						|| type.equals(Constants.POP_SOP))) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * 验证运单类型是否是B商家类型订单，若是则返回true，不是返回false
	 * 
	 * @param type
	 * @return
	 */
	public static Boolean isOrderTypeBWaybill(Integer type) {
		if (type != null
				&& type.equals(Constants.ORDER_TYPE_B)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * 检查运单的数据是否完整。如果有关键字段为空则返回true,正常情况返回false.
	 * 普通运单检查机构ID、站点编码、站点编号、支付类型、特殊属性、重量、地址;POP(23&&25)还检查数量、商家ID、商家名称.
	 * 
	 * @param waybill
	 * @return boolean
	 */
	public static Boolean isInvalidWaybill(Waybill waybill) {
		if (waybill == null) {
			return Boolean.TRUE;
		}

		Integer orgId = waybill.getOrgId();
//		Integer siteCode = waybill.getSiteCode();
		Integer paymentType = waybill.getPaymentType();
		String address = waybill.getAddress();
		String sendPay = waybill.getSendPay();
		// Double weight = waybill.getWeight();

		if (orgId == null 
				|| sendPay == null || sendPay.trim().length() == 0
				|| paymentType == null || address == null
				|| address.trim().length() == 0) {
			return Boolean.TRUE;
		}

		/*
		 * 与未出库冲突，不用判断
		 * if (isPopWaybillType(waybill)) {
			Integer quantity = waybill.getQuantity();
			Integer popSupId = waybill.getPopSupId();
			String popSupName = waybill.getPopSupName();
			if (quantity == null || quantity.equals(0) || popSupId == null 
					|| popSupName == null || popSupName.trim().length() == 0) {
				return Boolean.TRUE;
			}
		}*/

		return Boolean.FALSE;
	}



	@Override
	public String toString() {
		return "运单号【" + this.waybillCode + "】, 运单类型【" + this.type + "】, 机构号【"
				+ this.orgId + "】, 目的站点【" + this.siteCode + "】, 支付类型【"
				+ this.paymentType + "】, 地址【" + this.address + "】, 特殊属性【"
				+ this.sendPay + "】, 包裹数量【" + this.quantity + "】, POP商家ID【"
				+ this.popSupId + "】,POP商家名称【" + this.popSupName + "】";
	}

	/**
	 * @return the packageNum
	 */
	public int getPackageNum() {
		return packageNum;
	}

	/**
	 * @param packageNum the packageNum to set
	 */
	public void setPackageNum(int packageNum) {
		this.packageNum = packageNum;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getBusiOrderCode() {
		return busiOrderCode;
	}

	public void setBusiOrderCode(String busiOrderCode) {
		this.busiOrderCode = busiOrderCode;
	}

    public Double getCodMoney() {
        return codMoney;
    }

    public void setCodMoney(Double codMoney) {
        this.codMoney = codMoney;
    }
}
