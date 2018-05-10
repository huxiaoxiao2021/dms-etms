
package com.jd.wms.wm.domain.sms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SMS1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SMS1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SjNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UnickName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SmsContent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OrderIdString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SMS1", propOrder = {
    "id",
    "sjNo",
    "unickName",
    "smsContent",
    "type",
    "orderIdString"
})
public class SMS1 {

    @XmlElement(name = "Id")
    protected int id;
    @XmlElement(name = "SjNo")
    protected String sjNo;
    @XmlElement(name = "UnickName")
    protected String unickName;
    @XmlElement(name = "SmsContent")
    protected String smsContent;
    @XmlElement(name = "Type")
    protected int type;
    @XmlElement(name = "OrderIdString")
    protected String orderIdString;

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the sjNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSjNo() {
        return sjNo;
    }

    /**
     * Sets the value of the sjNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSjNo(String value) {
        this.sjNo = value;
    }

    /**
     * Gets the value of the unickName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnickName() {
        return unickName;
    }

    /**
     * Sets the value of the unickName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnickName(String value) {
        this.unickName = value;
    }

    /**
     * Gets the value of the smsContent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmsContent() {
        return smsContent;
    }

    /**
     * Sets the value of the smsContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmsContent(String value) {
        this.smsContent = value;
    }

    /**
     * Gets the value of the type property.
     * 
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     */
    public void setType(int value) {
        this.type = value;
    }

    /**
     * Gets the value of the orderIdString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderIdString() {
        return orderIdString;
    }

    /**
     * Sets the value of the orderIdString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderIdString(String value) {
        this.orderIdString = value;
    }

}
