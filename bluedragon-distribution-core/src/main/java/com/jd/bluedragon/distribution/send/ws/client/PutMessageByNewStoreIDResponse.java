
package com.jd.bluedragon.distribution.send.ws.client;

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
 *         &lt;element name="PutMessageByNewStoreIDResult" type="{http://tempuri.org/}ReturnMessage" minOccurs="0"/>
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
    "putMessageByNewStoreIDResult"
})
@XmlRootElement(name = "PutMessageByNewStoreIDResponse")
public class PutMessageByNewStoreIDResponse {

    @XmlElement(name = "PutMessageByNewStoreIDResult")
    protected ReturnMessage putMessageByNewStoreIDResult;

    /**
     * Gets the value of the putMessageByNewStoreIDResult property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnMessage }
     *     
     */
    public ReturnMessage getPutMessageByNewStoreIDResult() {
        return putMessageByNewStoreIDResult;
    }

    /**
     * Sets the value of the putMessageByNewStoreIDResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnMessage }
     *     
     */
    public void setPutMessageByNewStoreIDResult(ReturnMessage value) {
        this.putMessageByNewStoreIDResult = value;
    }

}
