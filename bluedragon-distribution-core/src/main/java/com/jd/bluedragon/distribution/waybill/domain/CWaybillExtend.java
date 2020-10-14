package com.jd.bluedragon.distribution.waybill.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xumei3 on 2017/8/14.
 * 与总部实体保持一致
 */
public class CWaybillExtend implements Serializable {
    private static final long serialVersionUID = 7271382259158921275L;

    /**
     * 机构ID
     */
    private Integer orgId;

    /**
     * 运单编号
     */
    private String waybillCode;

    /**
     * 分拣中心编号
     */
    private Integer cky2;

    /**
     * 站点编号
     */
    private Integer siteCode;

    /**
     * 路区编号
     */
    private String roadCode;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 支付类型
     */
    private Integer paymentType;

    /**
     * 运单类型
     */
    private Integer waybillType;

    /**
     * 特殊属性
     */
    private String sendPay;

    /**
     * 重量
     */
    private Double weight;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 地址
     */
    private String address;

    /**
     * POP商家ID
     */
    private Integer popSupId;

    /**
     * POP商家名称
     */
    private String popSupName;

    /**
     * 调用类型
     */
    private Integer callType;

    /**
     * 所在表结构
     */
    private String tableName;

    /**
     * 数据源标识
     */
    private String db;

    /**
     * 订单状态
     **/
    private String waybillFlag;

    /**
     * 订单状态-操作时间
     **/
    private Date updateTime;

    /**
     * 中转站编号
     */
    private Integer transferStationId;
    /**
     * 滑道号
     */
    private String crossCode;

    /**
     * 配送库房Id
     */
    private Integer distributeStoreId;

    /**
     * 笼车号
     */
    private String trolleyCode;

    private Integer busiId;

    /**
     * 配送方式
     */
    private Integer distributeType;

    /**
     * 订单二期修改标示
     */
    private String waybillSign;

    /**
     * 配送库房名称
     */
    private String distributeStoreName;

    /**
     * B商家名称
     */
    private String busiName;

    /**
     * 目的分拣中心,用于本地ver分拣中心过滤属于自己的运单数据
     */
    private String dmsDisCode;

    /**
     * 原始消费的topic类型
     * */
    private String sourceTopic;


    /**
     * 数据库操作类型 insert/update
     * */
    private String opType;

    /**
     * 商品体积
     */
    private Double volume;

    /**
     * 省id
     */
    private Integer provinceId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 乡id
     */
    private Integer countryId;

    /**
     * 乡名称
     */
    private String countryName;

    /**
     * 镇id
     */
    private Integer townId;

    /**
     * 镇名称
     */
    private String townName;

    private String vendorId;

    /**
     * 收货人id
     */
    private String receiverId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机
     */
    private String receiverMobile;

    /**
     * 收货人电话
     */
    private String receiverTel;

    /**
     * 收货人邮编
     */
    private String receiverZipCode;

    /**
     * 重要备注
     */
    private String importantHint;

    /**
     * 应收金额
     */
    private Double codMoney;

    /**
     * 价格
     */
    private Double price;

    /**
     * 实收金额
     */
    private Double recMoney;

    /**
     * 取件单号
     */
    private String relWaybillCode;

    /**
     * 运单状态
     */
    private Integer waybillState;

    /**
     * 新收获地址
     */
    private String newRecAddr;

    /**
     * 商家订单号
     */
    private String busiOrderCode;


    private String sourceCode;

    /**
     * 用户级别
     */
    private Integer userLevel;

    /**
     * 备用字段1
     */
    private String spareColumn1;


    private String flagInfo;

    /**
     * 按合同计算出的运费
     */
    private String freight;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCky2() {
        return cky2;
    }

    public void setCky2(Integer cky2) {
        this.cky2 = cky2;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getRoadCode() {
        return roadCode;
    }

    public void setRoadCode(String roadCode) {
        this.roadCode = roadCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public void setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
    }

    public String getSendPay() {
        return sendPay;
    }

    public void setSendPay(String sendPay) {
        this.sendPay = sendPay;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPopSupId() {
        return popSupId;
    }

    public void setPopSupId(Integer popSupId) {
        this.popSupId = popSupId;
    }

    public String getPopSupName() {
        return popSupName;
    }

    public void setPopSupName(String popSupName) {
        this.popSupName = popSupName;
    }

    public Integer getCallType() {
        return callType;
    }

    public void setCallType(Integer callType) {
        this.callType = callType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getWaybillFlag() {
        return waybillFlag;
    }

    public void setWaybillFlag(String waybillFlag) {
        this.waybillFlag = waybillFlag;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getTransferStationId() {
        return transferStationId;
    }

    public void setTransferStationId(Integer transferStationId) {
        this.transferStationId = transferStationId;
    }

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
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

    public Integer getBusiId() {
        return busiId;
    }

    public void setBusiId(Integer busiId) {
        this.busiId = busiId;
    }

    public Integer getDistributeType() {
        return distributeType;
    }

    public void setDistributeType(Integer distributeType) {
        this.distributeType = distributeType;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public String getDistributeStoreName() {
        return distributeStoreName;
    }

    public void setDistributeStoreName(String distributeStoreName) {
        this.distributeStoreName = distributeStoreName;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public String getDmsDisCode() {
        return dmsDisCode;
    }

    public void setDmsDisCode(String dmsDisCode) {
        this.dmsDisCode = dmsDisCode;
    }

    public String getSourceTopic() {
        return sourceTopic;
    }

    public void setSourceTopic(String sourceTopic) {
        this.sourceTopic = sourceTopic;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Integer getTownId() {
        return townId;
    }

    public void setTownId(Integer townId) {
        this.townId = townId;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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

    public String getReceiverZipCode() {
        return receiverZipCode;
    }

    public void setReceiverZipCode(String receiverZipCode) {
        this.receiverZipCode = receiverZipCode;
    }

    public String getImportantHint() {
        return importantHint;
    }

    public void setImportantHint(String importantHint) {
        this.importantHint = importantHint;
    }

    public Double getCodMoney() {
        return codMoney;
    }

    public void setCodMoney(Double codMoney) {
        this.codMoney = codMoney;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getRecMoney() {
        return recMoney;
    }

    public void setRecMoney(Double recMoney) {
        this.recMoney = recMoney;
    }

    public String getRelWaybillCode() {
        return relWaybillCode;
    }

    public void setRelWaybillCode(String relWaybillCode) {
        this.relWaybillCode = relWaybillCode;
    }

    public Integer getWaybillState() {
        return waybillState;
    }

    public void setWaybillState(Integer waybillState) {
        this.waybillState = waybillState;
    }

    public String getNewRecAddr() {
        return newRecAddr;
    }

    public void setNewRecAddr(String newRecAddr) {
        this.newRecAddr = newRecAddr;
    }

    public String getBusiOrderCode() {
        return busiOrderCode;
    }

    public void setBusiOrderCode(String busiOrderCode) {
        this.busiOrderCode = busiOrderCode;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public String getSpareColumn1() {
        return spareColumn1;
    }

    public void setSpareColumn1(String spareColumn1) {
        this.spareColumn1 = spareColumn1;
    }

    public String getFlagInfo() {
        return flagInfo;
    }

    public void setFlagInfo(String flagInfo) {
        this.flagInfo = flagInfo;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    @Override
    public String toString() {
        return "CWaybillExtend{" +
                "orgId=" + orgId +
                ", waybillCode='" + waybillCode + '\'' +
                ", cky2=" + cky2 +
                ", siteCode=" + siteCode +
                ", roadCode='" + roadCode + '\'' +
                ", siteName='" + siteName + '\'' +
                ", paymentType=" + paymentType +
                ", waybillType=" + waybillType +
                ", sendPay='" + sendPay + '\'' +
                ", weight=" + weight +
                ", quantity=" + quantity +
                ", address='" + address + '\'' +
                ", popSupId=" + popSupId +
                ", popSupName='" + popSupName + '\'' +
                ", callType=" + callType +
                ", tableName='" + tableName + '\'' +
                ", db='" + db + '\'' +
                ", waybillFlag='" + waybillFlag + '\'' +
                ", updateTime=" + updateTime +
                ", transferStationId=" + transferStationId +
                ", crossCode='" + crossCode + '\'' +
                ", distributeStoreId=" + distributeStoreId +
                ", trolleyCode='" + trolleyCode + '\'' +
                ", busiId=" + busiId +
                ", distributeType=" + distributeType +
                ", waybillSign='" + waybillSign + '\'' +
                ", distributeStoreName='" + distributeStoreName + '\'' +
                ", busiName='" + busiName + '\'' +
                ", dmsDisCode='" + dmsDisCode + '\'' +
                ", sourceTopic='" + sourceTopic + '\'' +
                ", opType='" + opType + '\'' +
                ", volume=" + volume +
                ", provinceId=" + provinceId +
                ", provinceName='" + provinceName + '\'' +
                ", cityId=" + cityId +
                ", cityName='" + cityName + '\'' +
                ", countryId=" + countryId +
                ", countryName='" + countryName + '\'' +
                ", townId=" + townId +
                ", townName='" + townName + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", receiverMobile='" + receiverMobile + '\'' +
                ", receiverTel='" + receiverTel + '\'' +
                ", receiverZipCode='" + receiverZipCode + '\'' +
                ", importantHint='" + importantHint + '\'' +
                ", codMoney=" + codMoney +
                ", price=" + price +
                ", recMoney=" + recMoney +
                ", relWaybillCode='" + relWaybillCode + '\'' +
                ", waybillState=" + waybillState +
                ", newRecAddr='" + newRecAddr + '\'' +
                ", busiOrderCode='" + busiOrderCode + '\'' +
                ", sourceCode='" + sourceCode + '\'' +
                ", userLevel=" + userLevel +
                ", spareColumn1='" + spareColumn1 + '\'' +
                ", flagInfo='" + flagInfo + '\'' +
                ", freight='" + freight + '\'' +
                '}';
    }
}
