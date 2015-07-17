
package com.jd.fa.refundService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="requestParam" type="{http://tempuri.org/}PopRequest" minOccurs="0"/>
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
    "requestParam"
})
@XmlRootElement(name = "PopRefund")
public class PopRefund {

    protected PopRequest requestParam;

    /**
     * Gets the value of the requestParam property.
     * 
     * @return
     *     possible object is
     *     {@link PopRequest }
     *     
     */
    public PopRequest getRequestParam() {
        return requestParam;
    }

    /**
     * Sets the value of the requestParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link PopRequest }
     *     
     */
    public void setRequestParam(PopRequest value) {
        this.requestParam = value;
    }

}
