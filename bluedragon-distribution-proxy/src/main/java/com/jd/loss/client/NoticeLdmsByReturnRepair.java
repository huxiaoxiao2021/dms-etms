
package com.jd.loss.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for noticeLdmsByReturnRepair complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="noticeLdmsByReturnRepair">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="returnStockInfo" type="{http://ws.ldms.pis.bk.jd.com/}returnStockInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "noticeLdmsByReturnRepair", propOrder = {
    "returnStockInfo"
})
public class NoticeLdmsByReturnRepair {

    protected ReturnStockInfo returnStockInfo;

    /**
     * Gets the value of the returnStockInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnStockInfo }
     *     
     */
    public ReturnStockInfo getReturnStockInfo() {
        return returnStockInfo;
    }

    /**
     * Sets the value of the returnStockInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnStockInfo }
     *     
     */
    public void setReturnStockInfo(ReturnStockInfo value) {
        this.returnStockInfo = value;
    }

}
