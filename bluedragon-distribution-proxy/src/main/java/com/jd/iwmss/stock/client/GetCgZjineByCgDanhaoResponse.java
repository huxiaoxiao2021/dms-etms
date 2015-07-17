
package com.jd.iwmss.stock.client;

import java.math.BigDecimal;
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
 *         &lt;element name="GetCgZjineByCgDanhaoResult" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
    "getCgZjineByCgDanhaoResult"
})
@XmlRootElement(name = "GetCgZjineByCgDanhaoResponse")
public class GetCgZjineByCgDanhaoResponse {

    @XmlElement(name = "GetCgZjineByCgDanhaoResult", required = true)
    protected BigDecimal getCgZjineByCgDanhaoResult;

    /**
     * Gets the value of the getCgZjineByCgDanhaoResult property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getGetCgZjineByCgDanhaoResult() {
        return getCgZjineByCgDanhaoResult;
    }

    /**
     * Sets the value of the getCgZjineByCgDanhaoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setGetCgZjineByCgDanhaoResult(BigDecimal value) {
        this.getCgZjineByCgDanhaoResult = value;
    }

}
