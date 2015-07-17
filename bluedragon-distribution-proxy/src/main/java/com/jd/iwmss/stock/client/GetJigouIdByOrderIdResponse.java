
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
 *         &lt;element name="GetJigouIdByOrderIdResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "getJigouIdByOrderIdResult"
})
@XmlRootElement(name = "GetJigouIdByOrderIdResponse")
public class GetJigouIdByOrderIdResponse {

    @XmlElement(name = "GetJigouIdByOrderIdResult")
    protected int getJigouIdByOrderIdResult;

    /**
     * Gets the value of the getJigouIdByOrderIdResult property.
     * 
     */
    public int getGetJigouIdByOrderIdResult() {
        return getJigouIdByOrderIdResult;
    }

    /**
     * Sets the value of the getJigouIdByOrderIdResult property.
     * 
     */
    public void setGetJigouIdByOrderIdResult(int value) {
        this.getJigouIdByOrderIdResult = value;
    }

}
