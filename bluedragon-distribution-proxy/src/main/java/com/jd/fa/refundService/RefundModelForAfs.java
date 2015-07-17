
package com.jd.fa.refundService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RefundModelForAfs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RefundModelForAfs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CanDisallow" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RequestType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RequestPerson" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CashAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CheckPerson" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RefundModelForAfs", propOrder = {
    "canDisallow",
    "requestId",
    "userId",
    "requestType",
    "requestPerson",
    "cashAmount",
    "checkPerson",
    "state"
})
public class RefundModelForAfs {

    @XmlElement(name = "CanDisallow")
    protected boolean canDisallow;
    @XmlElement(name = "RequestId")
    protected String requestId;
    @XmlElement(name = "UserId")
    protected String userId;
    @XmlElement(name = "RequestType")
    protected String requestType;
    @XmlElement(name = "RequestPerson")
    protected String requestPerson;
    @XmlElement(name = "CashAmount")
    protected String cashAmount;
    @XmlElement(name = "CheckPerson")
    protected String checkPerson;
    @XmlElement(name = "State")
    protected String state;

    /**
     * Gets the value of the canDisallow property.
     * 
     */
    public boolean isCanDisallow() {
        return canDisallow;
    }

    /**
     * Sets the value of the canDisallow property.
     * 
     */
    public void setCanDisallow(boolean value) {
        this.canDisallow = value;
    }

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
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
     * Gets the value of the requestType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Sets the value of the requestType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestType(String value) {
        this.requestType = value;
    }

    /**
     * Gets the value of the requestPerson property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestPerson() {
        return requestPerson;
    }

    /**
     * Sets the value of the requestPerson property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestPerson(String value) {
        this.requestPerson = value;
    }

    /**
     * Gets the value of the cashAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCashAmount() {
        return cashAmount;
    }

    /**
     * Sets the value of the cashAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCashAmount(String value) {
        this.cashAmount = value;
    }

    /**
     * Gets the value of the checkPerson property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckPerson() {
        return checkPerson;
    }

    /**
     * Sets the value of the checkPerson property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckPerson(String value) {
        this.checkPerson = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

}
