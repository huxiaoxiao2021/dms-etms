
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
 *         &lt;element name="GetFullRevokeKdanhaoByOrderIdResult" type="{http://360buy.com/}ArrayOfInt" minOccurs="0"/>
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
    "getFullRevokeKdanhaoByOrderIdResult"
})
@XmlRootElement(name = "GetFullRevokeKdanhaoByOrderIdResponse")
public class GetFullRevokeKdanhaoByOrderIdResponse {

    @XmlElement(name = "GetFullRevokeKdanhaoByOrderIdResult")
    protected ArrayOfInt getFullRevokeKdanhaoByOrderIdResult;

    /**
     * Gets the value of the getFullRevokeKdanhaoByOrderIdResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getGetFullRevokeKdanhaoByOrderIdResult() {
        return getFullRevokeKdanhaoByOrderIdResult;
    }

    /**
     * Sets the value of the getFullRevokeKdanhaoByOrderIdResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setGetFullRevokeKdanhaoByOrderIdResult(ArrayOfInt value) {
        this.getFullRevokeKdanhaoByOrderIdResult = value;
    }

}
