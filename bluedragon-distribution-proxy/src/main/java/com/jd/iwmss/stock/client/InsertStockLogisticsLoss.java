
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
 *         &lt;element name="inStock" type="{http://360buy.com/}Stock" minOccurs="0"/>
 *         &lt;element name="outStock" type="{http://360buy.com/}Stock" minOccurs="0"/>
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
    "inStock",
    "outStock"
})
@XmlRootElement(name = "InsertStockLogisticsLoss")
public class InsertStockLogisticsLoss {

    protected Stock inStock;
    protected Stock outStock;

    /**
     * Gets the value of the inStock property.
     * 
     * @return
     *     possible object is
     *     {@link Stock }
     *     
     */
    public Stock getInStock() {
        return inStock;
    }

    /**
     * Sets the value of the inStock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Stock }
     *     
     */
    public void setInStock(Stock value) {
        this.inStock = value;
    }

    /**
     * Gets the value of the outStock property.
     * 
     * @return
     *     possible object is
     *     {@link Stock }
     *     
     */
    public Stock getOutStock() {
        return outStock;
    }

    /**
     * Sets the value of the outStock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Stock }
     *     
     */
    public void setOutStock(Stock value) {
        this.outStock = value;
    }

}
