
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
 *         &lt;element name="Kdanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Cdanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Kuanx" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "kdanhao",
    "cdanhao",
    "kuanx"
})
@XmlRootElement(name = "UpdateStockByCdanhao")
public class UpdateStockByCdanhao {

    @XmlElement(name = "Kdanhao")
    protected int kdanhao;
    @XmlElement(name = "Cdanhao")
    protected int cdanhao;
    @XmlElement(name = "Kuanx")
    protected String kuanx;

    /**
     * Gets the value of the kdanhao property.
     * 
     */
    public int getKdanhao() {
        return kdanhao;
    }

    /**
     * Sets the value of the kdanhao property.
     * 
     */
    public void setKdanhao(int value) {
        this.kdanhao = value;
    }

    /**
     * Gets the value of the cdanhao property.
     * 
     */
    public int getCdanhao() {
        return cdanhao;
    }

    /**
     * Sets the value of the cdanhao property.
     * 
     */
    public void setCdanhao(int value) {
        this.cdanhao = value;
    }

    /**
     * Gets the value of the kuanx property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKuanx() {
        return kuanx;
    }

    /**
     * Sets the value of the kuanx property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKuanx(String value) {
        this.kuanx = value;
    }

}
