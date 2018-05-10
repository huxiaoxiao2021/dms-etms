
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
 *         &lt;element name="GetStockDetailsByKdanhaosResult" type="{http://360buy.com/}ArrayOfStockDetail" minOccurs="0"/>
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
    "getStockDetailsByKdanhaosResult"
})
@XmlRootElement(name = "GetStockDetailsByKdanhaosResponse")
public class GetStockDetailsByKdanhaosResponse {

    @XmlElement(name = "GetStockDetailsByKdanhaosResult")
    protected ArrayOfStockDetail getStockDetailsByKdanhaosResult;

    /**
     * Gets the value of the getStockDetailsByKdanhaosResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStockDetail }
     *     
     */
    public ArrayOfStockDetail getGetStockDetailsByKdanhaosResult() {
        return getStockDetailsByKdanhaosResult;
    }

    /**
     * Sets the value of the getStockDetailsByKdanhaosResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStockDetail }
     *     
     */
    public void setGetStockDetailsByKdanhaosResult(ArrayOfStockDetail value) {
        this.getStockDetailsByKdanhaosResult = value;
    }

}
