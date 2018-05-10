
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
 *         &lt;element name="GetStocksHisResult" type="{http://360buy.com/}ArrayOfStock" minOccurs="0"/>
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
    "getStocksHisResult"
})
@XmlRootElement(name = "GetStocksHisResponse")
public class GetStocksHisResponse {

    @XmlElement(name = "GetStocksHisResult")
    protected ArrayOfStock getStocksHisResult;

    /**
     * Gets the value of the getStocksHisResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStock }
     *     
     */
    public ArrayOfStock getGetStocksHisResult() {
        return getStocksHisResult;
    }

    /**
     * Sets the value of the getStocksHisResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStock }
     *     
     */
    public void setGetStocksHisResult(ArrayOfStock value) {
        this.getStocksHisResult = value;
    }

}
