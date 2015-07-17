
package com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sendOemPacks complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sendOemPacks">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="oemOrderDto" type="{http://impl.rpc.waybill.etms.jd.com}oemOrderDto" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendOemPacks", propOrder = {
    "oemOrderDto"
})
public class SendOemPacks {

    protected OemOrderDto oemOrderDto;

    /**
     * Gets the value of the oemOrderDto property.
     * 
     * @return
     *     possible object is
     *     {@link OemOrderDto }
     *     
     */
    public OemOrderDto getOemOrderDto() {
        return oemOrderDto;
    }

    /**
     * Sets the value of the oemOrderDto property.
     * 
     * @param value
     *     allowed object is
     *     {@link OemOrderDto }
     *     
     */
    public void setOemOrderDto(OemOrderDto value) {
        this.oemOrderDto = value;
    }

}
