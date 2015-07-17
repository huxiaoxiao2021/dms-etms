
package com.jd.fa.refundService;

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
 *         &lt;element name="rrd" type="{http://tempuri.org/}RefundReceiptDetailNew" minOccurs="0"/>
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
    "rrd"
})
@XmlRootElement(name = "SendXmlMessageWithObject")
public class SendXmlMessageWithObject {

    protected RefundReceiptDetailNew rrd;

    /**
     * Gets the value of the rrd property.
     * 
     * @return
     *     possible object is
     *     {@link RefundReceiptDetailNew }
     *     
     */
    public RefundReceiptDetailNew getRrd() {
        return rrd;
    }

    /**
     * Sets the value of the rrd property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefundReceiptDetailNew }
     *     
     */
    public void setRrd(RefundReceiptDetailNew value) {
        this.rrd = value;
    }

}
