
package com.jd.fa.refundService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerRequestNew complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomerRequestNew">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrderId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="RequestType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BankName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserBankProvince" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserBankCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SubBank" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BankUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BankCardNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HandlingChargeSide" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="UserId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerRequestNew", propOrder = {
    "orderId",
    "requestType",
    "bankName",
    "userBankProvince",
    "userBankCity",
    "subBank",
    "bankUserName",
    "bankCardNo",
    "handlingChargeSide",
    "userId",
    "phoneNumber",
    "remark"
})
public class CustomerRequestNew {

    @XmlElement(name = "OrderId")
    protected long orderId;
    @XmlElement(name = "RequestType")
    protected int requestType;
    @XmlElement(name = "BankName")
    protected String bankName;
    @XmlElement(name = "UserBankProvince")
    protected String userBankProvince;
    @XmlElement(name = "UserBankCity")
    protected String userBankCity;
    @XmlElement(name = "SubBank")
    protected String subBank;
    @XmlElement(name = "BankUserName")
    protected String bankUserName;
    @XmlElement(name = "BankCardNo")
    protected String bankCardNo;
    @XmlElement(name = "HandlingChargeSide")
    protected int handlingChargeSide;
    @XmlElement(name = "UserId")
    protected String userId;
    @XmlElement(name = "PhoneNumber")
    protected String phoneNumber;
    @XmlElement(name = "Remark")
    protected String remark;

    /**
     * Gets the value of the orderId property.
     * 
     */
    public long getOrderId() {
        return orderId;
    }

    /**
     * Sets the value of the orderId property.
     * 
     */
    public void setOrderId(long value) {
        this.orderId = value;
    }

    /**
     * Gets the value of the requestType property.
     * 
     */
    public int getRequestType() {
        return requestType;
    }

    /**
     * Sets the value of the requestType property.
     * 
     */
    public void setRequestType(int value) {
        this.requestType = value;
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
     * Gets the value of the userBankProvince property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserBankProvince() {
        return userBankProvince;
    }

    /**
     * Sets the value of the userBankProvince property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserBankProvince(String value) {
        this.userBankProvince = value;
    }

    /**
     * Gets the value of the userBankCity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserBankCity() {
        return userBankCity;
    }

    /**
     * Sets the value of the userBankCity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserBankCity(String value) {
        this.userBankCity = value;
    }

    /**
     * Gets the value of the subBank property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubBank() {
        return subBank;
    }

    /**
     * Sets the value of the subBank property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubBank(String value) {
        this.subBank = value;
    }

    /**
     * Gets the value of the bankUserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankUserName() {
        return bankUserName;
    }

    /**
     * Sets the value of the bankUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankUserName(String value) {
        this.bankUserName = value;
    }

    /**
     * Gets the value of the bankCardNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankCardNo() {
        return bankCardNo;
    }

    /**
     * Sets the value of the bankCardNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankCardNo(String value) {
        this.bankCardNo = value;
    }

    /**
     * Gets the value of the handlingChargeSide property.
     * 
     */
    public int getHandlingChargeSide() {
        return handlingChargeSide;
    }

    /**
     * Sets the value of the handlingChargeSide property.
     * 
     */
    public void setHandlingChargeSide(int value) {
        this.handlingChargeSide = value;
    }

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserId(String value) {
        this.userId = value;
    }

    /**
     * Gets the value of the phoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    /**
     * Gets the value of the remark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemark() {
        return remark;
    }

    /**
     * Sets the value of the remark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemark(String value) {
        this.remark = value;
    }

}
