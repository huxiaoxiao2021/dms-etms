
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
 *         &lt;element name="GetStockResult" type="{http://360buy.com/}Stock" minOccurs="0"/>
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
    "getStockResult"
})
@XmlRootElement(name = "GetStockResponse")
public class GetStockResponse {

    @XmlElement(name = "GetStockResult")
    protected Stock getStockResult;

    /**
     * Gets the value of the getStockResult property.
     * 
     * @return
     *     possible object is
     *     {@link Stock }
     *     
     */
    public Stock getGetStockResult() {
        return getStockResult;
    }

    /**
     * Sets the value of the getStockResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Stock }
     *     
     */
    public void setGetStockResult(Stock value) {
        this.getStockResult = value;
    }

}
