
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
 *         &lt;element name="GetKdanhaoByRfIdResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "getKdanhaoByRfIdResult"
})
@XmlRootElement(name = "GetKdanhaoByRfIdResponse")
public class GetKdanhaoByRfIdResponse {

    @XmlElement(name = "GetKdanhaoByRfIdResult")
    protected int getKdanhaoByRfIdResult;

    /**
     * Gets the value of the getKdanhaoByRfIdResult property.
     * 
     */
    public int getGetKdanhaoByRfIdResult() {
        return getKdanhaoByRfIdResult;
    }

    /**
     * Sets the value of the getKdanhaoByRfIdResult property.
     * 
     */
    public void setGetKdanhaoByRfIdResult(int value) {
        this.getKdanhaoByRfIdResult = value;
    }

}
