
package com.jd.wms.wm.domain.sms;

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
 *         &lt;element name="IsSendSMS1Result" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "isSendSMS1Result"
})
@XmlRootElement(name = "IsSendSMS1Response")
public class IsSendSMS1Response {

    @XmlElement(name = "IsSendSMS1Result")
    protected boolean isSendSMS1Result;

    /**
     * Gets the value of the isSendSMS1Result property.
     * 
     */
    public boolean isIsSendSMS1Result() {
        return isSendSMS1Result;
    }

    /**
     * Sets the value of the isSendSMS1Result property.
     * 
     */
    public void setIsSendSMS1Result(boolean value) {
        this.isSendSMS1Result = value;
    }

}
