
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
 *         &lt;element name="GetRevokeKdanhaoByOrderResult" type="{http://360buy.com/}ArrayOfInt" minOccurs="0"/>
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
    "getRevokeKdanhaoByOrderResult"
})
@XmlRootElement(name = "GetRevokeKdanhaoByOrderResponse")
public class GetRevokeKdanhaoByOrderResponse {

    @XmlElement(name = "GetRevokeKdanhaoByOrderResult")
    protected ArrayOfInt getRevokeKdanhaoByOrderResult;

    /**
     * Gets the value of the getRevokeKdanhaoByOrderResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getGetRevokeKdanhaoByOrderResult() {
        return getRevokeKdanhaoByOrderResult;
    }

    /**
     * Sets the value of the getRevokeKdanhaoByOrderResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setGetRevokeKdanhaoByOrderResult(ArrayOfInt value) {
        this.getRevokeKdanhaoByOrderResult = value;
    }

}
