package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

/**
 * 包裹信息
 * Created by wangtingwei on 2015/12/23.
 */
public class PrintPackage implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * 包裹号
     */
    private String packageCode;

    /**
     * 包裹重量
     */
    private Double weight;

    /**
     * 包裹是否已打印
     */
    public Boolean isPrintPack;

    /**
     * 包裹序号（1/1）
     */
    public String packageIndex;

    /**
     * 包裹重量
     */
    public String packageWeight;

    /**
     * 包裹号后缀
     */
    public String packageSuffix;
    
    /**
     * 包裹序号数字
     */
    public Integer packageIndexNum;

    /**
     * 包裹增值服务信息
     */
    public String packageSpecialRequirement;

    public String getPackageSpecialRequirement() {
        return packageSpecialRequirement;
    }

    public void setPackageSpecialRequirement(String packageSpecialRequirement) {
        this.packageSpecialRequirement = packageSpecialRequirement;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Double getWeight() {
        return weight;
    }
    /**
     * 尽量使用setPackageWeightAndUnit，同时设置重量及单位信息
     * @param packageWeight
     */
    @Deprecated
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Boolean getIsPrintPack() {
        return isPrintPack;
    }

    public void setIsPrintPack(Boolean isPrintPack) {
        this.isPrintPack = isPrintPack;
    }

    public String getPackageWeight() {
        return packageWeight;
    }
    /**
     * 设置包裹的重量及单位
     * @param weight 称重数据
     * @param unit 称重显示单位
     */
    public void setWeightAndUnit(Double weight,String unit) {
    	this.weight = weight;
    	if(weight != null && weight > 0){
    		this.packageWeight = weight.toString();
        	if(unit != null){
        		this.packageWeight += unit;
        	}
    	}else{
    		this.packageWeight = "";
    	}
    }
    /**
     * 尽量使用setPackageWeightAndUnit，同时设置重量及单位信息
     * @param packageWeight
     */
    @Deprecated
    public void setPackageWeight(String packageWeight) {
        this.packageWeight = packageWeight;
    }

    public Boolean getPrintPack() {
        return isPrintPack;
    }

    public void setPrintPack(Boolean printPack) {
        isPrintPack = printPack;
    }

    public String getPackageIndex() {
        return packageIndex;
    }

    public void setPackageIndex(String packageIndex) {
        this.packageIndex = packageIndex;
    }

    public String getPackageSuffix() {
        return packageSuffix;
    }

    public void setPackageSuffix(String packageSuffix) {
        this.packageSuffix = packageSuffix;
    }

	/**
	 * @return the packageIndexNum
	 */
	public Integer getPackageIndexNum() {
		return packageIndexNum;
	}

	/**
	 * @param packageIndexNum the packageIndexNum to set
	 */
	public void setPackageIndexNum(Integer packageIndexNum) {
		this.packageIndexNum = packageIndexNum;
	}


}
