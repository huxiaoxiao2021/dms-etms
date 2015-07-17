
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
 *         &lt;element name="Cgdanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Cdanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "cgdanhao",
    "cdanhao"
})
@XmlRootElement(name = "UpdateStockByCgdanhao")
public class UpdateStockByCgdanhao {

    @XmlElement(name = "Cgdanhao")
    protected int cgdanhao;
    @XmlElement(name = "Cdanhao")
    protected int cdanhao;

    /**
     * Gets the value of the cgdanhao property.
     * 
     */
    public int getCgdanhao() {
        return cgdanhao;
    }

    /**
     * Sets the value of the cgdanhao property.
     * 
     */
    public void setCgdanhao(int value) {
        this.cgdanhao = value;
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

}
