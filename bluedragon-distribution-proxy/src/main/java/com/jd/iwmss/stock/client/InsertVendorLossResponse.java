
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
 *         &lt;element name="InsertVendorLossResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "insertVendorLossResult"
})
@XmlRootElement(name = "InsertVendorLossResponse")
public class InsertVendorLossResponse {

    @XmlElement(name = "InsertVendorLossResult")
    protected int insertVendorLossResult;

    /**
     * Gets the value of the insertVendorLossResult property.
     * 
     */
    public int getInsertVendorLossResult() {
        return insertVendorLossResult;
    }

    /**
     * Sets the value of the insertVendorLossResult property.
     * 
     */
    public void setInsertVendorLossResult(int value) {
        this.insertVendorLossResult = value;
    }

}
