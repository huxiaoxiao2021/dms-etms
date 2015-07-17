
package com.jd.fa.refundService;

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
 *         &lt;element name="requestid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CurrentUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "requestid",
    "currentUserName"
})
@XmlRootElement(name = "DisallowRefund")
public class DisallowRefund {

    protected int requestid;
    @XmlElement(name = "CurrentUserName")
    protected String currentUserName;

    /**
     * Gets the value of the requestid property.
     * 
     */
    public int getRequestid() {
        return requestid;
    }

    /**
     * Sets the value of the requestid property.
     * 
     */
    public void setRequestid(int value) {
        this.requestid = value;
    }

    /**
     * Gets the value of the currentUserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentUserName() {
        return currentUserName;
    }

    /**
     * Sets the value of the currentUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentUserName(String value) {
        this.currentUserName = value;
    }

}
