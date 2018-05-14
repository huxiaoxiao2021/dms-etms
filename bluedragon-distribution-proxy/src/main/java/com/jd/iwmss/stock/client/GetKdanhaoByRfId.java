
package com.jd.iwmss.stock.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="RfType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="RfId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "rfType",
    "rfId"
})
@XmlRootElement(name = "GetKdanhaoByRfId")
public class GetKdanhaoByRfId {

    @XmlElement(name = "RfType")
    protected int rfType;
    @XmlElement(name = "RfId")
    protected String rfId;

    /**
     * Gets the value of the rfType property.
     * 
     */
    public int getRfType() {
        return rfType;
    }

    /**
     * Sets the value of the rfType property.
     * 
     */
    public void setRfType(int value) {
        this.rfType = value;
    }

    /**
     * Gets the value of the rfId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfId() {
        return rfId;
    }

    /**
     * Sets the value of the rfId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfId(String value) {
        this.rfId = value;
    }

}
