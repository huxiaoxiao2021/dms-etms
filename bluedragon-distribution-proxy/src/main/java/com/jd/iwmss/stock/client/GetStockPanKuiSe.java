
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
 *         &lt;element name="pankuidanhao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orgid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sid" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "pankuidanhao",
    "orgid",
    "sid"
})
@XmlRootElement(name = "GetStockPanKuiSe")
public class GetStockPanKuiSe {

    protected String pankuidanhao;
    protected int orgid;
    protected int sid;

    /**
     * Gets the value of the pankuidanhao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPankuidanhao() {
        return pankuidanhao;
    }

    /**
     * Sets the value of the pankuidanhao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPankuidanhao(String value) {
        this.pankuidanhao = value;
    }

    /**
     * Gets the value of the orgid property.
     * 
     */
    public int getOrgid() {
        return orgid;
    }

    /**
     * Sets the value of the orgid property.
     * 
     */
    public void setOrgid(int value) {
        this.orgid = value;
    }

    /**
     * Gets the value of the sid property.
     * 
     */
    public int getSid() {
        return sid;
    }

    /**
     * Sets the value of the sid property.
     * 
     */
    public void setSid(int value) {
        this.sid = value;
    }

}
