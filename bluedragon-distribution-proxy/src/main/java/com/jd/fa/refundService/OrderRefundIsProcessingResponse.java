
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
 *         &lt;element name="OrderRefundIsProcessingResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "orderRefundIsProcessingResult"
})
@XmlRootElement(name = "OrderRefundIsProcessingResponse")
public class OrderRefundIsProcessingResponse {

    @XmlElement(name = "OrderRefundIsProcessingResult")
    protected boolean orderRefundIsProcessingResult;

    /**
     * Gets the value of the orderRefundIsProcessingResult property.
     * 
     */
    public boolean isOrderRefundIsProcessingResult() {
        return orderRefundIsProcessingResult;
    }

    /**
     * Sets the value of the orderRefundIsProcessingResult property.
     * 
     */
    public void setOrderRefundIsProcessingResult(boolean value) {
        this.orderRefundIsProcessingResult = value;
    }

}
