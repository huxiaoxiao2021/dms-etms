
package com.jd.bluedragon.distribution.popAbnormal.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for savePopAbnormalOrderPackageResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="savePopAbnormalOrderPackageResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://service.orderpackage.pop.jd.com/}abnormalResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "savePopAbnormalOrderPackageResponse", propOrder = {
    "_return"
})
public class SavePopAbnormalOrderPackageResponse {

    @XmlElement(name = "return")
    protected AbnormalResult _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link AbnormalResult }
     *     
     */
    public AbnormalResult getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbnormalResult }
     *     
     */
    public void setReturn(AbnormalResult value) {
        this._return = value;
    }

}
