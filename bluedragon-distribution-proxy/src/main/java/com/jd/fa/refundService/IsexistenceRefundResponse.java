
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
 *         &lt;element name="IsexistenceRefundResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "isexistenceRefundResult"
})
@XmlRootElement(name = "IsexistenceRefundResponse")
public class IsexistenceRefundResponse {

    @XmlElement(name = "IsexistenceRefundResult")
    protected boolean isexistenceRefundResult;

    /**
     * Gets the value of the isexistenceRefundResult property.
     * 
     */
    public boolean isIsexistenceRefundResult() {
        return isexistenceRefundResult;
    }

    /**
     * Sets the value of the isexistenceRefundResult property.
     * 
     */
    public void setIsexistenceRefundResult(boolean value) {
        this.isexistenceRefundResult = value;
    }

}
