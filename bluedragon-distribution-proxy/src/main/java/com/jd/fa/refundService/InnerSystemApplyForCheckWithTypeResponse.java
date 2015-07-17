
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
 *         &lt;element name="InnerSystemApplyForCheckWithTypeResult" type="{http://tempuri.org/}ValidRequest" minOccurs="0"/>
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
    "innerSystemApplyForCheckWithTypeResult"
})
@XmlRootElement(name = "InnerSystemApplyForCheckWithTypeResponse")
public class InnerSystemApplyForCheckWithTypeResponse {

    @XmlElement(name = "InnerSystemApplyForCheckWithTypeResult")
    protected ValidRequest innerSystemApplyForCheckWithTypeResult;

    /**
     * Gets the value of the innerSystemApplyForCheckWithTypeResult property.
     * 
     * @return
     *     possible object is
     *     {@link ValidRequest }
     *     
     */
    public ValidRequest getInnerSystemApplyForCheckWithTypeResult() {
        return innerSystemApplyForCheckWithTypeResult;
    }

    /**
     * Sets the value of the innerSystemApplyForCheckWithTypeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidRequest }
     *     
     */
    public void setInnerSystemApplyForCheckWithTypeResult(ValidRequest value) {
        this.innerSystemApplyForCheckWithTypeResult = value;
    }

}
