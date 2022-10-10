package com.jd.bluedragon.common.dto.seal.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class SealVehicleReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 6675404873010444219L;

    //主任务编号
    private String sendVehicleBizId;
    //子任务编号
    private String sendVehicleDetailBizId;

    /**
     * 封车类型
     * SealCarTypeEnum
     *  SEAL_BY_TRANSPORT_CAPABILITY(10, "按运力"),
     *  SEAL_BY_TASK(20, "按派车任务明细");
     */
    private Integer sealCarType;

    /**
     * 任务简码
     */
    private String itemSimpleCode;

    /**
     * 运力编码
     */
    private String transportCode;

    /**
     * 批次信息
     *  这块暂定先按老逻辑走：根据任务简码查询任务详情时会返回sendCode 批次信息，封车时传一下，没返回就不传
     */
    private List<String> batchCodes;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 封签号
     */
    private List<String> sealCodes;

    /**
     * 重量
     */
    private Double weight;

    /**
     * 体积
     */
    private Double volume;

    /**
     * 线路编码
     *  根据任务简码查询任务详情时返回的
     */
    private String routeLineCode;

    /**
     * 封车时间
     */
    private String sealCarTime;

    /**
     * 分拣中心id
     */
    private Integer sealSiteId;

    /**
     * 分拣中心名称
     */
    private String sealSiteName;

    /**
     * 用户erp账号
     */
    private String sealUserCode;

    /**
     * 用户姓名
     */
    private String sealUserName;

    /**
     * 托盘数
     *
     */
    private String palletCount;

    /**
     * TransTypeEnum
     * 运输方式
     *  1、公路零担
     *  2、公路整车
     *  3、航空
     *  4、铁路整车
     *  5、快递
     *  6、冷链整车
     *  7、冷链零担
     *  8、公路整车平板
     *  9、公路零担平板
     *  10、铁路零担
     *
     *  根据任务简码查询任务详情时返回的
     * */
    private Integer transWay;

    /**
     * 运输方式名称
     */
    private String transWayName;

    /**
     * 预封车选择的批次号列表
     */
    private List<String> selectedSendCodes;
    
    /**
     * 扫描的封签号
     */
    private List<String> scannedSealCodes;
    /**
     * 扫描的批次
     */
    private List<String> scannedBatchCodes;    

    private String billCode;


    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getSendVehicleDetailBizId() {
        return sendVehicleDetailBizId;
    }

    public void setSendVehicleDetailBizId(String sendVehicleDetailBizId) {
        this.sendVehicleDetailBizId = sendVehicleDetailBizId;
    }

    public Integer getSealCarType() {
        return sealCarType;
    }

    public void setSealCarType(Integer sealCarType) {
        this.sealCarType = sealCarType;
    }

    public String getItemSimpleCode() {
        return itemSimpleCode;
    }

    public void setItemSimpleCode(String itemSimpleCode) {
        this.itemSimpleCode = itemSimpleCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public List<String> getBatchCodes() {
        return batchCodes;
    }

    public void setBatchCodes(List<String> batchCodes) {
        this.batchCodes = batchCodes;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public List<String> getSealCodes() {
        return sealCodes;
    }

    public void setSealCodes(List<String> sealCodes) {
        this.sealCodes = sealCodes;
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

    public String getRouteLineCode() {
        return routeLineCode;
    }

    public void setRouteLineCode(String routeLineCode) {
        this.routeLineCode = routeLineCode;
    }

    public String getSealCarTime() {
        return sealCarTime;
    }

    public void setSealCarTime(String sealCarTime) {
        this.sealCarTime = sealCarTime;
    }

    public Integer getSealSiteId() {
        return sealSiteId;
    }

    public void setSealSiteId(Integer sealSiteId) {
        this.sealSiteId = sealSiteId;
    }

    public String getSealSiteName() {
        return sealSiteName;
    }

    public void setSealSiteName(String sealSiteName) {
        this.sealSiteName = sealSiteName;
    }

    public String getSealUserCode() {
        return sealUserCode;
    }

    public void setSealUserCode(String sealUserCode) {
        this.sealUserCode = sealUserCode;
    }

    public String getSealUserName() {
        return sealUserName;
    }

    public void setSealUserName(String sealUserName) {
        this.sealUserName = sealUserName;
    }

    public String getPalletCount() {
        return palletCount;
    }

    public void setPalletCount(String palletCount) {
        this.palletCount = palletCount;
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

    public List<String> getSelectedSendCodes() {
        return selectedSendCodes;
    }

    public void setSelectedSendCodes(List<String> selectedSendCodes) {
        this.selectedSendCodes = selectedSendCodes;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

	public List<String> getScannedSealCodes() {
		return scannedSealCodes;
	}

	public void setScannedSealCodes(List<String> scannedSealCodes) {
		this.scannedSealCodes = scannedSealCodes;
	}

	public List<String> getScannedBatchCodes() {
		return scannedBatchCodes;
	}

	public void setScannedBatchCodes(List<String> scannedBatchCodes) {
		this.scannedBatchCodes = scannedBatchCodes;
	}
}
