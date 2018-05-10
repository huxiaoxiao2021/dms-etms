
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
 *         &lt;element name="GetStockPanKuiSeResult" type="{http://360buy.com/}Stock" minOccurs="0"/>
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
    "getStockPanKuiSeResult"
})
@XmlRootElement(name = "GetStockPanKuiSeResponse")
public class GetStockPanKuiSeResponse {

    @XmlElement(name = "GetStockPanKuiSeResult")
    protected Stock getStockPanKuiSeResult;

    /**
     * Gets the value of the getStockPanKuiSeResult property.
     * 
     * @return
     *     possible object is
     *     {@link Stock }
     *     
     */
    public Stock getGetStockPanKuiSeResult() {
        return getStockPanKuiSeResult;
    }

    /**
     * Sets the value of the getStockPanKuiSeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Stock }
     *     
     */
    public void setGetStockPanKuiSeResult(Stock value) {
        this.getStockPanKuiSeResult = value;
    }

}
