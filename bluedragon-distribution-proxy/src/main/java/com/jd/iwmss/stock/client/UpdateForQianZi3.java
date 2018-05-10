
package com.jd.iwmss.stock.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="yuandanhao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "yuandanhao"
})
@XmlRootElement(name = "UpdateForQianZi3")
public class UpdateForQianZi3 {

    protected String yuandanhao;

    /**
     * Gets the value of the yuandanhao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYuandanhao() {
        return yuandanhao;
    }

    /**
     * Sets the value of the yuandanhao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYuandanhao(String value) {
        this.yuandanhao = value;
    }

}
