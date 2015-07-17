
package com.jd.iwmss.stock.client;

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
 *         &lt;element name="intOrderId" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
    "intOrderId"
})
@XmlRootElement(name = "GetRevokeDetailListByOrderId")
public class GetRevokeDetailListByOrderId {

    protected long intOrderId;

    /**
     * Gets the value of the intOrderId property.
     * 
     */
    public long getIntOrderId() {
        return intOrderId;
    }

    /**
     * Sets the value of the intOrderId property.
     * 
     */
    public void setIntOrderId(long value) {
        this.intOrderId = value;
    }

}
