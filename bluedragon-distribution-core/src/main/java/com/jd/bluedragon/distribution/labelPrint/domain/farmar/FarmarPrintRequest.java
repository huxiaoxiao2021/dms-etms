package com.jd.bluedragon.distribution.labelPrint.domain.farmar;

import software.amazon.ion.Decimal;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/8/22 4:21 PM
 */
public class FarmarPrintRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // 砝码编码
    private String farmarCode;
    // 场地ID
    private Integer createSiteCode;
    // 场地名称
    private String createSiteName;
    // 创建人ERP
    private String createUserErp;
    // 创建人名称
    private String createUserName;
    // 砝码重量
    private BigDecimal weight;
    // 砝码长
    private BigDecimal length;
    // 砝码宽
    private BigDecimal width;
    // 砝码高
    private BigDecimal high;
    // 砝码校验类型 0、重量标准 1、尺寸标准
    private Integer farmarCheckType;
    // 打印类型 0、打印 1、补打
    private Integer printType;
    // 打印数量
    private Integer printCount;

    public String getFarmarCode() {
        return farmarCode;
    }

    public void setFarmarCode(String farmarCode) {
        this.farmarCode = farmarCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public Integer getFarmarCheckType() {
        return farmarCheckType;
    }

    public void setFarmarCheckType(Integer farmarCheckType) {
        this.farmarCheckType = farmarCheckType;
    }

    public Integer getPrintType() {
        return printType;
    }

    public void setPrintType(Integer printType) {
        this.printType = printType;
    }

    public Integer getPrintCount() {
        return printCount;
    }

    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }
}
