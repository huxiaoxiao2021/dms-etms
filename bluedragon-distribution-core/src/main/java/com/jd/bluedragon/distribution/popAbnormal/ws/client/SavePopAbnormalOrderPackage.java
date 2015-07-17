
package com.jd.bluedragon.distribution.popAbnormal.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for savePopAbnormalOrderPackage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="savePopAbnormalOrderPackage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://service.orderpackage.pop.jd.com/}popAbnormalOrderVo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "savePopAbnormalOrderPackage", propOrder = {
    "arg0"
})
public class SavePopAbnormalOrderPackage {

    protected PopAbnormalOrderVo arg0;

    /**
     * Gets the value of the arg0 property.
     * 
     * @return
     *     possible object is
     *     {@link PopAbnormalOrderVo }
     *     
     */
    public PopAbnormalOrderVo getArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link PopAbnormalOrderVo }
     *     
     */
    public void setArg0(PopAbnormalOrderVo value) {
        this.arg0 = value;
    }

}
