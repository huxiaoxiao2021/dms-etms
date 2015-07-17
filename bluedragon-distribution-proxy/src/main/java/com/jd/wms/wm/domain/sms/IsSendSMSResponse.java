
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
 *         &lt;element name="IsSendSMSResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "isSendSMSResult"
})
@XmlRootElement(name = "IsSendSMSResponse")
public class IsSendSMSResponse {

    @XmlElement(name = "IsSendSMSResult")
    protected boolean isSendSMSResult;

    /**
     * Gets the value of the isSendSMSResult property.
     * 
     */
    public boolean isIsSendSMSResult() {
        return isSendSMSResult;
    }

    /**
     * Sets the value of the isSendSMSResult property.
     * 
     */
    public void setIsSendSMSResult(boolean value) {
        this.isSendSMSResult = value;
    }

}
