
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
 *         &lt;element name="SendXmlMessageWithObjectResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "sendXmlMessageWithObjectResult"
})
@XmlRootElement(name = "SendXmlMessageWithObjectResponse")
public class SendXmlMessageWithObjectResponse {

    @XmlElement(name = "SendXmlMessageWithObjectResult")
    protected String sendXmlMessageWithObjectResult;

    /**
     * Gets the value of the sendXmlMessageWithObjectResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendXmlMessageWithObjectResult() {
        return sendXmlMessageWithObjectResult;
    }

    /**
     * Sets the value of the sendXmlMessageWithObjectResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendXmlMessageWithObjectResult(String value) {
        this.sendXmlMessageWithObjectResult = value;
    }

}
