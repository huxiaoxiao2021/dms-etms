
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
 *         &lt;element name="CheckTransferStatusIsRejectedBySparePartsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "checkTransferStatusIsRejectedBySparePartsResult"
})
@XmlRootElement(name = "CheckTransferStatusIsRejectedBySparePartsResponse")
public class CheckTransferStatusIsRejectedBySparePartsResponse {

    @XmlElement(name = "CheckTransferStatusIsRejectedBySparePartsResult")
    protected boolean checkTransferStatusIsRejectedBySparePartsResult;

    /**
     * Gets the value of the checkTransferStatusIsRejectedBySparePartsResult property.
     * 
     */
    public boolean isCheckTransferStatusIsRejectedBySparePartsResult() {
        return checkTransferStatusIsRejectedBySparePartsResult;
    }

    /**
     * Sets the value of the checkTransferStatusIsRejectedBySparePartsResult property.
     * 
     */
    public void setCheckTransferStatusIsRejectedBySparePartsResult(boolean value) {
        this.checkTransferStatusIsRejectedBySparePartsResult = value;
    }

}
