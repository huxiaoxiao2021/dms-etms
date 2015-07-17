
package com.jd.bluedragon.distribution.send.ws.client.dmc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for reinvest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reinvest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="orderId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reinvest" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reinvestDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="reinvestMsg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resultCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="resultMsg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reinvest", propOrder = {
    "orderId",
    "reinvest",
    "reinvestDate",
    "reinvestMsg",
    "resultCode",
    "resultMsg"
})
public class Reinvest {

    protected String orderId;
    protected String reinvest;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar reinvestDate;
    protected String reinvestMsg;
    protected int resultCode;
    protected String resultMsg;

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
     * Gets the value of the reinvest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReinvest() {
        return reinvest;
    }

    /**
     * Sets the value of the reinvest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReinvest(String value) {
        this.reinvest = value;
    }

    /**
     * Gets the value of the reinvestDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReinvestDate() {
        return reinvestDate;
    }

    /**
     * Sets the value of the reinvestDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReinvestDate(XMLGregorianCalendar value) {
        this.reinvestDate = value;
    }

    /**
     * Gets the value of the reinvestMsg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReinvestMsg() {
        return reinvestMsg;
    }

    /**
     * Sets the value of the reinvestMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReinvestMsg(String value) {
        this.reinvestMsg = value;
    }

    /**
     * Gets the value of the resultCode property.
     * 
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     */
    public void setResultCode(int value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the resultMsg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultMsg() {
        return resultMsg;
    }

    /**
     * Sets the value of the resultMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultMsg(String value) {
        this.resultMsg = value;
    }

}
