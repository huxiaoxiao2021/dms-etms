
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
 *         &lt;element name="GetInStockTimeByCgdanhaoResult" type="{http://360buy.com/}ArrayOfStock" minOccurs="0"/>
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
    "getInStockTimeByCgdanhaoResult"
})
@XmlRootElement(name = "GetInStockTimeByCgdanhaoResponse")
public class GetInStockTimeByCgdanhaoResponse {

    @XmlElement(name = "GetInStockTimeByCgdanhaoResult")
    protected ArrayOfStock getInStockTimeByCgdanhaoResult;

    /**
     * Gets the value of the getInStockTimeByCgdanhaoResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStock }
     *     
     */
    public ArrayOfStock getGetInStockTimeByCgdanhaoResult() {
        return getInStockTimeByCgdanhaoResult;
    }

    /**
     * Sets the value of the getInStockTimeByCgdanhaoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStock }
     *     
     */
    public void setGetInStockTimeByCgdanhaoResult(ArrayOfStock value) {
        this.getInStockTimeByCgdanhaoResult = value;
    }

}
