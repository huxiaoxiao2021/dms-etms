
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
 *         &lt;element name="UpdateStockByCgdanhaoResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "updateStockByCgdanhaoResult"
})
@XmlRootElement(name = "UpdateStockByCgdanhaoResponse")
public class UpdateStockByCgdanhaoResponse {

    @XmlElement(name = "UpdateStockByCgdanhaoResult")
    protected int updateStockByCgdanhaoResult;

    /**
     * Gets the value of the updateStockByCgdanhaoResult property.
     * 
     */
    public int getUpdateStockByCgdanhaoResult() {
        return updateStockByCgdanhaoResult;
    }

    /**
     * Sets the value of the updateStockByCgdanhaoResult property.
     * 
     */
    public void setUpdateStockByCgdanhaoResult(int value) {
        this.updateStockByCgdanhaoResult = value;
    }

}
