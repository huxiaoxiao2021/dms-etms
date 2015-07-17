
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
 *         &lt;element name="WriteOffByKdanhaoResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "writeOffByKdanhaoResult"
})
@XmlRootElement(name = "WriteOffByKdanhaoResponse")
public class WriteOffByKdanhaoResponse {

    @XmlElement(name = "WriteOffByKdanhaoResult")
    protected int writeOffByKdanhaoResult;

    /**
     * Gets the value of the writeOffByKdanhaoResult property.
     * 
     */
    public int getWriteOffByKdanhaoResult() {
        return writeOffByKdanhaoResult;
    }

    /**
     * Sets the value of the writeOffByKdanhaoResult property.
     * 
     */
    public void setWriteOffByKdanhaoResult(int value) {
        this.writeOffByKdanhaoResult = value;
    }

}
