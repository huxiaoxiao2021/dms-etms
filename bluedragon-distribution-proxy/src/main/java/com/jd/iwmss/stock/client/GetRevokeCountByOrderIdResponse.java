
package com.jd.iwmss.stock.client;

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
 *         &lt;element name="GetRevokeCountByOrderIdResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "getRevokeCountByOrderIdResult"
})
@XmlRootElement(name = "GetRevokeCountByOrderIdResponse")
public class GetRevokeCountByOrderIdResponse {

    @XmlElement(name = "GetRevokeCountByOrderIdResult")
    protected int getRevokeCountByOrderIdResult;

    /**
     * Gets the value of the getRevokeCountByOrderIdResult property.
     * 
     */
    public int getGetRevokeCountByOrderIdResult() {
        return getRevokeCountByOrderIdResult;
    }

    /**
     * Sets the value of the getRevokeCountByOrderIdResult property.
     * 
     */
    public void setGetRevokeCountByOrderIdResult(int value) {
        this.getRevokeCountByOrderIdResult = value;
    }

}
