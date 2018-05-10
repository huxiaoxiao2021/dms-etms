
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
 *         &lt;element name="stockParamter" type="{http://360buy.com/}StockParamter" minOccurs="0"/>
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
    "stockParamter"
})
@XmlRootElement(name = "GetKdanhao")
public class GetKdanhao {

    protected StockParamter stockParamter;

    /**
     * Gets the value of the stockParamter property.
     * 
     * @return
     *     possible object is
     *     {@link StockParamter }
     *     
     */
    public StockParamter getStockParamter() {
        return stockParamter;
    }

    /**
     * Sets the value of the stockParamter property.
     * 
     * @param value
     *     allowed object is
     *     {@link StockParamter }
     *     
     */
    public void setStockParamter(StockParamter value) {
        this.stockParamter = value;
    }

}
