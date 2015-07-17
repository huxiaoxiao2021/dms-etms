
package com.jd.fa.orderrefund;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sendXmlMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sendXmlMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="xmlMessage" type="{http://www.360buy.com}xmlMessage" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendXmlMessage", propOrder = {
    "xmlMessage"
})
public class SendXmlMessage {

    protected XmlMessage xmlMessage;

    /**
     * Gets the value of the xmlMessage property.
     * 
     * @return
     *     possible object is
     *     {@link XmlMessage }
     *     
     */
    public XmlMessage getXmlMessage() {
        return xmlMessage;
    }

    /**
     * Sets the value of the xmlMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlMessage }
     *     
     */
    public void setXmlMessage(XmlMessage value) {
        this.xmlMessage = value;
    }

}
