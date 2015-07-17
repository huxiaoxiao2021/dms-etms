
package com.jd.bluedragon.distribution.popAbnormal.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for popAbnormalOrderVo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="popAbnormalOrderVo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actualNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="currentNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="memo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="venderId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wayBill" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "popAbnormalOrderVo", propOrder = {
    "actualNumber",
    "currentNumber",
    "memo",
    "orderId",
    "serialNumber",
    "type",
    "venderId",
    "wayBill"
})
public class PopAbnormalOrderVo {

    protected String actualNumber;
    protected String currentNumber;
    protected String memo;
    protected String orderId;
    protected String serialNumber;
    protected String type;
    protected String venderId;
    protected String wayBill;

    /**
     * Gets the value of the actualNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActualNumber() {
        return actualNumber;
    }

    /**
     * Sets the value of the actualNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActualNumber(String value) {
        this.actualNumber = value;
    }

    /**
     * Gets the value of the currentNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentNumber() {
        return currentNumber;
    }

    /**
     * Sets the value of the currentNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentNumber(String value) {
        this.currentNumber = value;
    }

    /**
     * Gets the value of the memo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMemo() {
        return memo;
    }

    /**
     * Sets the value of the memo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMemo(String value) {
        this.memo = value;
    }

    /**
     * Gets the value of the orderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Sets the value of the orderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderId(String value) {
        this.orderId = value;
    }

    /**
     * Gets the value of the serialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNumber(String value) {
        this.serialNumber = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the venderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVenderId() {
        return venderId;
    }

    /**
     * Sets the value of the venderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVenderId(String value) {
        this.venderId = value;
    }

    /**
     * Gets the value of the wayBill property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWayBill() {
        return wayBill;
    }

    /**
     * Sets the value of the wayBill property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWayBill(String value) {
        this.wayBill = value;
    }

}
