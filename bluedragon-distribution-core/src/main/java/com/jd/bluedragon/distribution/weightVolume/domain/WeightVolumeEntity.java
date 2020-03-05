package com.jd.bluedragon.distribution.weightVolume.domain;

import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;

import java.util.Date;

/**
 * <p>
 *     分拣称重量方处理对象
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
public class WeightVolumeEntity {

    /**
     * 当前操作的业务原始条码
     */
    private String barCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 重量 单位KG
     */
    private Double weight;

    /**
     * 长度 单位CM
     */
    private Double length;

    /**
     * 宽 单位CM
     */
    private Double width;

    /**
     * 高 单位CM
     */
    private Double height;

    /**
     * 体积 单位cm³
     */
    private Double volume;

    /**
     * 业务类型枚举，要求：枚举值 大写 值来源于客户单定义 具有可识别性可读性
     * 含义：该流水的业务来源
     */
    private WeightVolumeBusinessTypeEnum businessType;

    /**
     * 系统识别编号， 要求：枚举值大写，值来自于调用方系统定义，约定俗成
     */
    private FromSourceEnum sourceCode;

    /**
     * 操作站点编号
     */
    private Integer operateSiteCode;

    /**
     * 操作站点名称
     */
    private String operateSiteName;

    /**
     * 操作人编号
     */
    private String operatorCode;

    /**
     * 操作人ID
     */
    private Integer operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 长包裹 0:普通包裹 1:长包裹
     * */
    private Integer longPackage;

    public WeightVolumeEntity longPackage(Integer longPackage){
        this.longPackage = longPackage;
        return this;
    }

    public WeightVolumeEntity barCode(String barCode){
        this.barCode = barCode;
        return this;
    }

    public WeightVolumeEntity waybillCode(String waybillCode){
        this.waybillCode = waybillCode;
        return this;
    }

    public WeightVolumeEntity packageCode(String packageCode){
        this.packageCode = packageCode;
        return this;
    }

    public WeightVolumeEntity boxCode(String boxCode){
        this.boxCode = boxCode;
        return this;
    }

    public WeightVolumeEntity businessType(WeightVolumeBusinessTypeEnum businessType){
        this.businessType = businessType;
        return this;
    }

    public WeightVolumeEntity sourceCode(FromSourceEnum sourceCode){
        this.sourceCode = sourceCode;
        return this;
    }

    public WeightVolumeEntity operateSiteCode(Integer operateSiteCode){
        this.operateSiteCode = operateSiteCode;
        return this;
    }

    public WeightVolumeEntity operateSiteName(String operateSiteName){
        this.operateSiteName = operateSiteName;
        return this;
    }

    public WeightVolumeEntity operatorCode(String operatorCode){
        this.operatorCode = operatorCode;
        return this;
    }

    public WeightVolumeEntity operatorId(Integer operatorId){
        this.operatorId = operatorId;
        return this;
    }

    public WeightVolumeEntity operatorName(String operatorName){
        this.operatorName = operatorName;
        return this;
    }

    public WeightVolumeEntity weight(Double weight){
        this.weight = weight;
        return this;
    }

    public WeightVolumeEntity length(Double length){
        this.length = length;
        return this;
    }

    public WeightVolumeEntity width(Double width){
        this.width = width;
        return this;
    }

    public WeightVolumeEntity height(Double height){
        this.height = height;
        return this;
    }

    public WeightVolumeEntity volume(Double volume){
        this.volume = volume;
        return this;
    }

    public WeightVolumeEntity operateTime(Date operateTime){
        this.operateTime = operateTime;
        return this;
    }

    public Integer getLongPackage() {
        return longPackage;
    }

    public void setLongPackage(Integer longPackage) {
        this.longPackage = longPackage;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public WeightVolumeBusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public void setBusinessType(WeightVolumeBusinessTypeEnum businessType) {
        this.businessType = businessType;
    }

    public FromSourceEnum getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(FromSourceEnum sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
