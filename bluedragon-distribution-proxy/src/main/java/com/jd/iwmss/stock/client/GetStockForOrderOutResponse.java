
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
 *         &lt;element name="GetStockForOrderOutResult" type="{http://360buy.com/}ArrayOfStock" minOccurs="0"/>
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
    "getStockForOrderOutResult"
})
@XmlRootElement(name = "GetStockForOrderOutResponse")
public class GetStockForOrderOutResponse {

    @XmlElement(name = "GetStockForOrderOutResult")
    protected ArrayOfStock getStockForOrderOutResult;

    /**
     * Gets the value of the getStockForOrderOutResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStock }
     *     
     */
    public ArrayOfStock getGetStockForOrderOutResult() {
        return getStockForOrderOutResult;
    }

    /**
     * Sets the value of the getStockForOrderOutResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStock }
     *     
     */
    public void setGetStockForOrderOutResult(ArrayOfStock value) {
        this.getStockForOrderOutResult = value;
    }

}
