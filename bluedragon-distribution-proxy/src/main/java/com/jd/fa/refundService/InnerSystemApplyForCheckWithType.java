
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
 *         &lt;element name="requestParam" type="{http://tempuri.org/}CustomerRequestNew" minOccurs="0"/>
 *         &lt;element name="businessType" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "requestParam",
    "businessType"
})
@XmlRootElement(name = "InnerSystemApplyForCheckWithType")
public class InnerSystemApplyForCheckWithType {

    protected CustomerRequestNew requestParam;
    protected int businessType;

    /**
     * Gets the value of the requestParam property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerRequestNew }
     *     
     */
    public CustomerRequestNew getRequestParam() {
        return requestParam;
    }

    /**
     * Sets the value of the requestParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerRequestNew }
     *     
     */
    public void setRequestParam(CustomerRequestNew value) {
        this.requestParam = value;
    }

    /**
     * Gets the value of the businessType property.
     * 
     */
    public int getBusinessType() {
        return businessType;
    }

    /**
     * Sets the value of the businessType property.
     * 
     */
    public void setBusinessType(int value) {
        this.businessType = value;
    }

}
