
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
 *         &lt;element name="PutMessageResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "putMessageResult"
})
@XmlRootElement(name = "PutMessageResponse")
public class PutMessageResponse {

    @XmlElement(name = "PutMessageResult")
    protected boolean putMessageResult;

    /**
     * Gets the value of the putMessageResult property.
     * 
     */
    public boolean isPutMessageResult() {
        return putMessageResult;
    }

    /**
     * Sets the value of the putMessageResult property.
     * 
     */
    public void setPutMessageResult(boolean value) {
        this.putMessageResult = value;
    }

}
