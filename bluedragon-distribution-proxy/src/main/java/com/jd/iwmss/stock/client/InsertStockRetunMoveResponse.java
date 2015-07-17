
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
 *         &lt;element name="InsertStockRetunMoveResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "insertStockRetunMoveResult"
})
@XmlRootElement(name = "InsertStockRetunMoveResponse")
public class InsertStockRetunMoveResponse {

    @XmlElement(name = "InsertStockRetunMoveResult")
    protected int insertStockRetunMoveResult;

    /**
     * Gets the value of the insertStockRetunMoveResult property.
     * 
     */
    public int getInsertStockRetunMoveResult() {
        return insertStockRetunMoveResult;
    }

    /**
     * Sets the value of the insertStockRetunMoveResult property.
     * 
     */
    public void setInsertStockRetunMoveResult(int value) {
        this.insertStockRetunMoveResult = value;
    }

}
