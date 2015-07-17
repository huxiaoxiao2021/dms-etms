
package com.jd.bluedragon.distribution.send.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Parameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Parameter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrderID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ZDID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OperatorID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OperatorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StoreID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Cky2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Parameter", propOrder = {
    "orderID",
    "zdid",
    "operatorID",
    "operatorName",
    "storeID",
    "cky2"
})
public class Parameter {

    @XmlElement(name = "OrderID")
    protected int orderID;
    @XmlElement(name = "ZDID")
    protected int zdid;
    @XmlElement(name = "OperatorID")
    protected int operatorID;
    @XmlElement(name = "OperatorName")
    protected String operatorName;
    @XmlElement(name = "StoreID")
    protected int storeID;
    @XmlElement(name = "Cky2")
    protected int cky2;

    /**
     * Gets the value of the orderID property.
     * 
     */
    public int getOrderID() {
        return orderID;
    }

    /**
     * Sets the value of the orderID property.
     * 
     */
    public void setOrderID(int value) {
        this.orderID = value;
    }

    /**
     * Gets the value of the zdid property.
     * 
     */
    public int getZDID() {
        return zdid;
    }

    /**
     * Sets the value of the zdid property.
     * 
     */
    public void setZDID(int value) {
        this.zdid = value;
    }

    /**
     * Gets the value of the operatorID property.
     * 
     */
    public int getOperatorID() {
        return operatorID;
    }

    /**
     * Sets the value of the operatorID property.
     * 
     */
    public void setOperatorID(int value) {
        this.operatorID = value;
    }

    /**
     * Gets the value of the operatorName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * Sets the value of the operatorName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatorName(String value) {
        this.operatorName = value;
    }

    /**
     * Gets the value of the storeID property.
     * 
     */
    public int getStoreID() {
        return storeID;
    }

    /**
     * Sets the value of the storeID property.
     * 
     */
    public void setStoreID(int value) {
        this.storeID = value;
    }

    /**
     * Gets the value of the cky2 property.
     * 
     */
    public int getCky2() {
        return cky2;
    }

    /**
     * Sets the value of the cky2 property.
     * 
     */
    public void setCky2(int value) {
        this.cky2 = value;
    }

}
