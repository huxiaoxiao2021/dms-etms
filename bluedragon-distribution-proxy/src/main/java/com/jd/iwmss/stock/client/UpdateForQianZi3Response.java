
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
 *         &lt;element name="UpdateForQianZi3Result" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "updateForQianZi3Result"
})
@XmlRootElement(name = "UpdateForQianZi3Response")
public class UpdateForQianZi3Response {

    @XmlElement(name = "UpdateForQianZi3Result")
    protected int updateForQianZi3Result;

    /**
     * Gets the value of the updateForQianZi3Result property.
     * 
     */
    public int getUpdateForQianZi3Result() {
        return updateForQianZi3Result;
    }

    /**
     * Sets the value of the updateForQianZi3Result property.
     * 
     */
    public void setUpdateForQianZi3Result(int value) {
        this.updateForQianZi3Result = value;
    }

}
