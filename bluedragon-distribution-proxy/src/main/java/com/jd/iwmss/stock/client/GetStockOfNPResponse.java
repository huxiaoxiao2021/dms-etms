
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
 *         &lt;element name="GetStockOfNPResult" type="{http://360buy.com/}ArrayOfSkuStockNP" minOccurs="0"/>
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
    "getStockOfNPResult"
})
@XmlRootElement(name = "GetStockOfNPResponse")
public class GetStockOfNPResponse {

    @XmlElement(name = "GetStockOfNPResult")
    protected ArrayOfSkuStockNP getStockOfNPResult;

    /**
     * Gets the value of the getStockOfNPResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSkuStockNP }
     *     
     */
    public ArrayOfSkuStockNP getGetStockOfNPResult() {
        return getStockOfNPResult;
    }

    /**
     * Sets the value of the getStockOfNPResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSkuStockNP }
     *     
     */
    public void setGetStockOfNPResult(ArrayOfSkuStockNP value) {
        this.getStockOfNPResult = value;
    }

}
