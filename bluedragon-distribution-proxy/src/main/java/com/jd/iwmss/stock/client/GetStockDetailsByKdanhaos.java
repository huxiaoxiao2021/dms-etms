
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
 *         &lt;element name="strKdanhao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "strKdanhao"
})
@XmlRootElement(name = "GetStockDetailsByKdanhaos")
public class GetStockDetailsByKdanhaos {

    protected String strKdanhao;

    /**
     * Gets the value of the strKdanhao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrKdanhao() {
        return strKdanhao;
    }

    /**
     * Sets the value of the strKdanhao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrKdanhao(String value) {
        this.strKdanhao = value;
    }

}
