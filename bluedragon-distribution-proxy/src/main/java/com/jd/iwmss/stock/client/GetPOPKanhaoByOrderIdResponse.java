
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
 *         &lt;element name="GetPOPKanhaoByOrderIdResult" type="{http://360buy.com/}ArrayOfInt" minOccurs="0"/>
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
    "getPOPKanhaoByOrderIdResult"
})
@XmlRootElement(name = "GetPOPKanhaoByOrderIdResponse")
public class GetPOPKanhaoByOrderIdResponse {

    @XmlElement(name = "GetPOPKanhaoByOrderIdResult")
    protected ArrayOfInt getPOPKanhaoByOrderIdResult;

    /**
     * Gets the value of the getPOPKanhaoByOrderIdResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getGetPOPKanhaoByOrderIdResult() {
        return getPOPKanhaoByOrderIdResult;
    }

    /**
     * Sets the value of the getPOPKanhaoByOrderIdResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setGetPOPKanhaoByOrderIdResult(ArrayOfInt value) {
        this.getPOPKanhaoByOrderIdResult = value;
    }

}
