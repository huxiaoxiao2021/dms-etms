
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
 *         &lt;element name="DisallowRefundResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="outMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "disallowRefundResult",
    "outMessage"
})
@XmlRootElement(name = "DisallowRefundResponse")
public class DisallowRefundResponse {

    @XmlElement(name = "DisallowRefundResult")
    protected boolean disallowRefundResult;
    protected String outMessage;

    /**
     * Gets the value of the disallowRefundResult property.
     * 
     */
    public boolean isDisallowRefundResult() {
        return disallowRefundResult;
    }

    /**
     * Sets the value of the disallowRefundResult property.
     * 
     */
    public void setDisallowRefundResult(boolean value) {
        this.disallowRefundResult = value;
    }

    /**
     * Gets the value of the outMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutMessage() {
        return outMessage;
    }

    /**
     * Sets the value of the outMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutMessage(String value) {
        this.outMessage = value;
    }

}
