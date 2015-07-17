
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
 *         &lt;element name="SkuId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DepId" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "skuId",
    "depId"
})
@XmlRootElement(name = "GetOrderMonitorStockNum")
public class GetOrderMonitorStockNum {

    @XmlElement(name = "SkuId")
    protected int skuId;
    @XmlElement(name = "DepId")
    protected int depId;

    /**
     * Gets the value of the skuId property.
     * 
     */
    public int getSkuId() {
        return skuId;
    }

    /**
     * Sets the value of the skuId property.
     * 
     */
    public void setSkuId(int value) {
        this.skuId = value;
    }

    /**
     * Gets the value of the depId property.
     * 
     */
    public int getDepId() {
        return depId;
    }

    /**
     * Sets the value of the depId property.
     * 
     */
    public void setDepId(int value) {
        this.depId = value;
    }

}
