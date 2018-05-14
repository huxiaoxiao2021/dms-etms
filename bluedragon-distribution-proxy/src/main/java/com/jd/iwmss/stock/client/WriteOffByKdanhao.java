
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
 *         &lt;element name="ErrorStock" type="{http://360buy.com/}Stock" minOccurs="0"/>
 *         &lt;element name="IsRevise" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "errorStock",
    "isRevise"
})
@XmlRootElement(name = "WriteOffByKdanhao")
public class WriteOffByKdanhao {

    @XmlElement(name = "ErrorStock")
    protected Stock errorStock;
    @XmlElement(name = "IsRevise")
    protected boolean isRevise;

    /**
     * Gets the value of the errorStock property.
     * 
     * @return
     *     possible object is
     *     {@link Stock }
     *     
     */
    public Stock getErrorStock() {
        return errorStock;
    }

    /**
     * Sets the value of the errorStock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Stock }
     *     
     */
    public void setErrorStock(Stock value) {
        this.errorStock = value;
    }

    /**
     * Gets the value of the isRevise property.
     * 
     */
    public boolean isIsRevise() {
        return isRevise;
    }

    /**
     * Sets the value of the isRevise property.
     * 
     */
    public void setIsRevise(boolean value) {
        this.isRevise = value;
    }

}
