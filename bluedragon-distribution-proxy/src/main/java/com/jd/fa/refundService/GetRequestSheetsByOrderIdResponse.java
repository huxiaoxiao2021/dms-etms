
package com.jd.fa.refundService;

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
 *         &lt;element name="GetRequestSheetsByOrderIdResult" type="{http://tempuri.org/}ArrayOfReqSheet" minOccurs="0"/>
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
    "getRequestSheetsByOrderIdResult"
})
@XmlRootElement(name = "GetRequestSheetsByOrderIdResponse")
public class GetRequestSheetsByOrderIdResponse {

    @XmlElement(name = "GetRequestSheetsByOrderIdResult")
    protected ArrayOfReqSheet getRequestSheetsByOrderIdResult;

    /**
     * Gets the value of the getRequestSheetsByOrderIdResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfReqSheet }
     *     
     */
    public ArrayOfReqSheet getGetRequestSheetsByOrderIdResult() {
        return getRequestSheetsByOrderIdResult;
    }

    /**
     * Sets the value of the getRequestSheetsByOrderIdResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfReqSheet }
     *     
     */
    public void setGetRequestSheetsByOrderIdResult(ArrayOfReqSheet value) {
        this.getRequestSheetsByOrderIdResult = value;
    }

}
