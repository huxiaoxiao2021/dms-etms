
package com.jd.iwmss.stock.client;

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
 *         &lt;element name="stock" type="{http://360buy.com/}StockParamter" minOccurs="0"/>
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
    "stock"
})
@XmlRootElement(name = "GetStocks")
public class GetStocks {

    protected StockParamter stock;

    /**
     * Gets the value of the stock property.
     * 
     * @return
     *     possible object is
     *     {@link StockParamter }
     *     
     */
    public StockParamter getStock() {
        return stock;
    }

    /**
     * Sets the value of the stock property.
     * 
     * @param value
     *     allowed object is
     *     {@link StockParamter }
     *     
     */
    public void setStock(StockParamter value) {
        this.stock = value;
    }

}
