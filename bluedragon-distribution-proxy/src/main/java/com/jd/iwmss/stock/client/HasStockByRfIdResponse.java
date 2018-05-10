
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
 *         &lt;element name="HasStockByRfIdResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "hasStockByRfIdResult"
})
@XmlRootElement(name = "HasStockByRfIdResponse")
public class HasStockByRfIdResponse {

    @XmlElement(name = "HasStockByRfIdResult")
    protected boolean hasStockByRfIdResult;

    /**
     * Gets the value of the hasStockByRfIdResult property.
     * 
     */
    public boolean isHasStockByRfIdResult() {
        return hasStockByRfIdResult;
    }

    /**
     * Sets the value of the hasStockByRfIdResult property.
     * 
     */
    public void setHasStockByRfIdResult(boolean value) {
        this.hasStockByRfIdResult = value;
    }

}
