
package com.jd.bluedragon.distribution.send.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="orderId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="zdid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="operatorId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="operatorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cky2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ob" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "orderId",
    "zdid",
    "operatorId",
    "operatorName",
    "cky2",
    "ob"
})
@XmlRootElement(name = "PutMessage")
public class PutMessage {

    protected int orderId;
    protected int zdid;
    protected int operatorId;
    protected String operatorName;
    protected int cky2;
    protected Object ob;

    /**
     * Gets the value of the orderId property.
     * 
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Sets the value of the orderId property.
     * 
     */
    public void setOrderId(int value) {
        this.orderId = value;
    }

    /**
     * Gets the value of the zdid property.
     * 
     */
    public int getZdid() {
        return zdid;
    }

    /**
     * Sets the value of the zdid property.
     * 
     */
    public void setZdid(int value) {
        this.zdid = value;
    }

    /**
     * Gets the value of the operatorId property.
     * 
     */
    public int getOperatorId() {
        return operatorId;
    }

    /**
     * Sets the value of the operatorId property.
     * 
     */
    public void setOperatorId(int value) {
        this.operatorId = value;
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

    /**
     * Gets the value of the ob property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getOb() {
        return ob;
    }

    /**
     * Sets the value of the ob property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setOb(Object value) {
        this.ob = value;
    }

}
