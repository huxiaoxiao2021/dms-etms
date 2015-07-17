
package com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for oemPackDto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="oemPackDto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="againWeight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goodVolume" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="goodWeight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="packCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="sendUserId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "oemPackDto", propOrder = {
    "againWeight",
    "goodVolume",
    "goodWeight",
    "orderId",
    "packCode",
    "remark",
    "sendTime",
    "sendUserId",
    "sendUserName"
})
public class OemPackDto {

    protected String againWeight;
    protected String goodVolume;
    protected String goodWeight;
    protected String orderId;
    protected String packCode;
    protected String remark;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sendTime;
    protected String sendUserId;
    protected String sendUserName;

    /**
     * Gets the value of the againWeight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgainWeight() {
        return againWeight;
    }

    /**
     * Sets the value of the againWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgainWeight(String value) {
        this.againWeight = value;
    }

    /**
     * Gets the value of the goodVolume property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGoodVolume() {
        return goodVolume;
    }

    /**
     * Sets the value of the goodVolume property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGoodVolume(String value) {
        this.goodVolume = value;
    }

    /**
     * Gets the value of the goodWeight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGoodWeight() {
        return goodWeight;
    }

    /**
     * Sets the value of the goodWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGoodWeight(String value) {
        this.goodWeight = value;
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
     * Gets the value of the packCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackCode() {
        return packCode;
    }

    /**
     * Sets the value of the packCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackCode(String value) {
        this.packCode = value;
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

    /**
     * Gets the value of the sendTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSendTime() {
        return sendTime;
    }

    /**
     * Sets the value of the sendTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSendTime(XMLGregorianCalendar value) {
        this.sendTime = value;
    }

    /**
     * Gets the value of the sendUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendUserId() {
        return sendUserId;
    }

    /**
     * Sets the value of the sendUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendUserId(String value) {
        this.sendUserId = value;
    }

    /**
     * Gets the value of the sendUserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendUserName() {
        return sendUserName;
    }

    /**
     * Sets the value of the sendUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendUserName(String value) {
        this.sendUserName = value;
    }

}
