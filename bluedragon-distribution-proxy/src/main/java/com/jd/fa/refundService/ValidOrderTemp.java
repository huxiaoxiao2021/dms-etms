
package com.jd.fa.refundService;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValidOrderTemp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValidOrderTemp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="isValid" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AddFee" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Qishu" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="payWay" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RecommondType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="RecommondTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BankNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BankName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Money" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OnlinepayDays" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsDeleteOrderRefund" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidOrderTemp", propOrder = {
    "isValid",
    "addFee",
    "qishu",
    "payWay",
    "recommondType",
    "recommondTypeName",
    "bankNo",
    "bankName",
    "money",
    "userid",
    "onlinepayDays",
    "message",
    "isDeleteOrderRefund"
})
public class ValidOrderTemp {

    protected boolean isValid;
    @XmlElement(name = "AddFee", required = true)
    protected BigDecimal addFee;
    @XmlElement(name = "Qishu")
    protected int qishu;
    protected String payWay;
    @XmlElement(name = "RecommondType")
    protected int recommondType;
    @XmlElement(name = "RecommondTypeName")
    protected String recommondTypeName;
    @XmlElement(name = "BankNo")
    protected String bankNo;
    @XmlElement(name = "BankName")
    protected String bankName;
    @XmlElement(name = "Money", required = true)
    protected BigDecimal money;
    @XmlElement(name = "Userid")
    protected String userid;
    @XmlElement(name = "OnlinepayDays")
    protected int onlinepayDays;
    @XmlElement(name = "Message")
    protected String message;
    @XmlElement(name = "IsDeleteOrderRefund")
    protected int isDeleteOrderRefund;

    /**
     * Gets the value of the isValid property.
     * 
     */
    public boolean isIsValid() {
        return isValid;
    }

    /**
     * Sets the value of the isValid property.
     * 
     */
    public void setIsValid(boolean value) {
        this.isValid = value;
    }

    /**
     * Gets the value of the addFee property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAddFee() {
        return addFee;
    }

    /**
     * Sets the value of the addFee property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAddFee(BigDecimal value) {
        this.addFee = value;
    }

    /**
     * Gets the value of the qishu property.
     * 
     */
    public int getQishu() {
        return qishu;
    }

    /**
     * Sets the value of the qishu property.
     * 
     */
    public void setQishu(int value) {
        this.qishu = value;
    }

    /**
     * Gets the value of the payWay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPayWay() {
        return payWay;
    }

    /**
     * Sets the value of the payWay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPayWay(String value) {
        this.payWay = value;
    }

    /**
     * Gets the value of the recommondType property.
     * 
     */
    public int getRecommondType() {
        return recommondType;
    }

    /**
     * Sets the value of the recommondType property.
     * 
     */
    public void setRecommondType(int value) {
        this.recommondType = value;
    }

    /**
     * Gets the value of the recommondTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecommondTypeName() {
        return recommondTypeName;
    }

    /**
     * Sets the value of the recommondTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecommondTypeName(String value) {
        this.recommondTypeName = value;
    }

    /**
     * Gets the value of the bankNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankNo() {
        return bankNo;
    }

    /**
     * Sets the value of the bankNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankNo(String value) {
        this.bankNo = value;
    }

    /**
     * Gets the value of the bankName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * Sets the value of the bankName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankName(String value) {
        this.bankName = value;
    }

    /**
     * Gets the value of the money property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMoney() {
        return money;
    }

    /**
     * Sets the value of the money property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMoney(BigDecimal value) {
        this.money = value;
    }

    /**
     * Gets the value of the userid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Sets the value of the userid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserid(String value) {
        this.userid = value;
    }

    /**
     * Gets the value of the onlinepayDays property.
     * 
     */
    public int getOnlinepayDays() {
        return onlinepayDays;
    }

    /**
     * Sets the value of the onlinepayDays property.
     * 
     */
    public void setOnlinepayDays(int value) {
        this.onlinepayDays = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the isDeleteOrderRefund property.
     * 
     */
    public int getIsDeleteOrderRefund() {
        return isDeleteOrderRefund;
    }

    /**
     * Sets the value of the isDeleteOrderRefund property.
     * 
     */
    public void setIsDeleteOrderRefund(int value) {
        this.isDeleteOrderRefund = value;
    }

}
