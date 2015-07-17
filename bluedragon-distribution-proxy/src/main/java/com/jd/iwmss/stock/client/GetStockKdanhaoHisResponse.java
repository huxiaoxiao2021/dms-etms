
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
 *         &lt;element name="GetStockKdanhaoHisResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "getStockKdanhaoHisResult"
})
@XmlRootElement(name = "GetStockKdanhaoHisResponse")
public class GetStockKdanhaoHisResponse {

    @XmlElement(name = "GetStockKdanhaoHisResult")
    protected int getStockKdanhaoHisResult;

    /**
     * Gets the value of the getStockKdanhaoHisResult property.
     * 
     */
    public int getGetStockKdanhaoHisResult() {
        return getStockKdanhaoHisResult;
    }

    /**
     * Sets the value of the getStockKdanhaoHisResult property.
     * 
     */
    public void setGetStockKdanhaoHisResult(int value) {
        this.getStockKdanhaoHisResult = value;
    }

}
