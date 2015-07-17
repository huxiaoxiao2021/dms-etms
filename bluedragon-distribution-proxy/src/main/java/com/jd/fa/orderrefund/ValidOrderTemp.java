
package com.jd.fa.orderrefund;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for validOrderTemp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validOrderTemp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="addFee" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="bankName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bankNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="money" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="onlinepayDays" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="payWay" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qishu" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="recommondType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="recommondTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="valid" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validOrderTemp", propOrder = {
    "addFee",
    "bankName",
    "bankNo",
    "message",
    "money",
    "onlinepayDays",
    "payWay",
    "qishu",
    "recommondType",
    "recommondTypeName",
    "userid",
    "valid"
})
public class ValidOrderTemp {

    protected double addFee;
    protected String bankName;
    protected String bankNo;
    protected String message;
    protected double money;
    protected int onlinepayDays;
    protected String payWay;
    protected int qishu;
    protected int recommondType;
    protected String recommondTypeName;
    protected String userid;
    protected boolean valid;

    /**
     * Gets the value of the addFee property.
     * 
     */
    public double getAddFee() {
        return addFee;
    }

    /**
     * Sets the value of the addFee property.
     * 
     */
    public void setAddFee(double value) {
        this.addFee = value;
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
     * Gets the value of the money property.
     * 
     */
    public double getMoney() {
        return money;
    }

    /**
     * Sets the value of the money property.
     * 
     */
    public void setMoney(double value) {
        this.money = value;
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
     * Gets the value of the valid property.
     * 
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets the value of the valid property.
     * 
     */
    public void setValid(boolean value) {
        this.valid = value;
    }

}
