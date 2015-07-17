
package com.jd.fa.orderrefund;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getBlackUsers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getBlackUsers">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="blackUserQuery" type="{http://www.360buy.com}blackUserQuery" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBlackUsers", propOrder = {
    "blackUserQuery"
})
public class GetBlackUsers {

    protected BlackUserQuery blackUserQuery;

    /**
     * Gets the value of the blackUserQuery property.
     * 
     * @return
     *     possible object is
     *     {@link BlackUserQuery }
     *     
     */
    public BlackUserQuery getBlackUserQuery() {
        return blackUserQuery;
    }

    /**
     * Sets the value of the blackUserQuery property.
     * 
     * @param value
     *     allowed object is
     *     {@link BlackUserQuery }
     *     
     */
    public void setBlackUserQuery(BlackUserQuery value) {
        this.blackUserQuery = value;
    }

}
