
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
 *         &lt;element name="GetOrderMonitorStockNumResult" type="{http://360buy.com/}ArrayOfOrderMonitor" minOccurs="0"/>
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
    "getOrderMonitorStockNumResult"
})
@XmlRootElement(name = "GetOrderMonitorStockNumResponse")
public class GetOrderMonitorStockNumResponse {

    @XmlElement(name = "GetOrderMonitorStockNumResult")
    protected ArrayOfOrderMonitor getOrderMonitorStockNumResult;

    /**
     * Gets the value of the getOrderMonitorStockNumResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfOrderMonitor }
     *     
     */
    public ArrayOfOrderMonitor getGetOrderMonitorStockNumResult() {
        return getOrderMonitorStockNumResult;
    }

    /**
     * Sets the value of the getOrderMonitorStockNumResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfOrderMonitor }
     *     
     */
    public void setGetOrderMonitorStockNumResult(ArrayOfOrderMonitor value) {
        this.getOrderMonitorStockNumResult = value;
    }

}
