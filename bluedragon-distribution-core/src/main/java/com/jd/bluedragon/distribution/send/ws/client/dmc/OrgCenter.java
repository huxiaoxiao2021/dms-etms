
package com.jd.bluedragon.distribution.send.ws.client.dmc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for orgCenter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="orgCenter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dmsName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dmsNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "orgCenter", propOrder = {
    "dmsName",
    "dmsNo",
    "locName",
    "locNo",
    "type"
})
public class OrgCenter {

    protected String dmsName;
    protected String dmsNo;
    protected String locName;
    protected String locNo;
    protected String type;

    /**
     * Gets the value of the dmsName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDmsName() {
        return dmsName;
    }

    /**
     * Sets the value of the dmsName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDmsName(String value) {
        this.dmsName = value;
    }

    /**
     * Gets the value of the dmsNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDmsNo() {
        return dmsNo;
    }

    /**
     * Sets the value of the dmsNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDmsNo(String value) {
        this.dmsNo = value;
    }

    /**
     * Gets the value of the locName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocName() {
        return locName;
    }

    /**
     * Sets the value of the locName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocName(String value) {
        this.locName = value;
    }

    /**
     * Gets the value of the locNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocNo() {
        return locNo;
    }

    /**
     * Sets the value of the locNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocNo(String value) {
        this.locNo = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
