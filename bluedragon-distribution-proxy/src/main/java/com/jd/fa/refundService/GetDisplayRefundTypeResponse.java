
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
 *         &lt;element name="GetDisplayRefundTypeResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getDisplayRefundTypeResult"
})
@XmlRootElement(name = "GetDisplayRefundTypeResponse")
public class GetDisplayRefundTypeResponse {

    @XmlElement(name = "GetDisplayRefundTypeResult")
    protected String getDisplayRefundTypeResult;

    /**
     * Gets the value of the getDisplayRefundTypeResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetDisplayRefundTypeResult() {
        return getDisplayRefundTypeResult;
    }

    /**
     * Sets the value of the getDisplayRefundTypeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetDisplayRefundTypeResult(String value) {
        this.getDisplayRefundTypeResult = value;
    }

}
