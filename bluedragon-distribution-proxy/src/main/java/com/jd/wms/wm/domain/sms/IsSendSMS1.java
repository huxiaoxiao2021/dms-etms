
package com.jd.wms.wm.domain.sms;

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
 *         &lt;element name="sms1" type="{http://360buy.com/}SMS1" minOccurs="0"/>
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
    "sms1"
})
@XmlRootElement(name = "IsSendSMS1")
public class IsSendSMS1 {

    protected SMS1 sms1;

    /**
     * Gets the value of the sms1 property.
     * 
     * @return
     *     possible object is
     *     {@link SMS1 }
     *     
     */
    public SMS1 getSms1() {
        return sms1;
    }

    /**
     * Sets the value of the sms1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link SMS1 }
     *     
     */
    public void setSms1(SMS1 value) {
        this.sms1 = value;
    }

}
