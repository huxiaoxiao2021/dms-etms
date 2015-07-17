
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
 *         &lt;element name="s1" type="{http://360buy.com/}Stock" minOccurs="0"/>
 *         &lt;element name="s2" type="{http://360buy.com/}Stock" minOccurs="0"/>
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
    "s1",
    "s2"
})
@XmlRootElement(name = "InsertStockGiftCard")
public class InsertStockGiftCard {

    protected Stock s1;
    protected Stock s2;

    /**
     * Gets the value of the s1 property.
     * 
     * @return
     *     possible object is
     *     {@link Stock }
     *     
     */
    public Stock getS1() {
        return s1;
    }

    /**
     * Sets the value of the s1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Stock }
     *     
     */
    public void setS1(Stock value) {
        this.s1 = value;
    }

    /**
     * Gets the value of the s2 property.
     * 
     * @return
     *     possible object is
     *     {@link Stock }
     *     
     */
    public Stock getS2() {
        return s2;
    }

    /**
     * Sets the value of the s2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Stock }
     *     
     */
    public void setS2(Stock value) {
        this.s2 = value;
    }

}
