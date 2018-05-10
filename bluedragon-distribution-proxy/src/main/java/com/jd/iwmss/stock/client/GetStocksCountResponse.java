
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
 *         &lt;element name="GetStocksCountResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "getStocksCountResult"
})
@XmlRootElement(name = "GetStocksCountResponse")
public class GetStocksCountResponse {

    @XmlElement(name = "GetStocksCountResult")
    protected int getStocksCountResult;

    /**
     * Gets the value of the getStocksCountResult property.
     * 
     */
    public int getGetStocksCountResult() {
        return getStocksCountResult;
    }

    /**
     * Sets the value of the getStocksCountResult property.
     * 
     */
    public void setGetStocksCountResult(int value) {
        this.getStocksCountResult = value;
    }

}
