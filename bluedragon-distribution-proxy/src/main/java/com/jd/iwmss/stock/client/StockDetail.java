
package com.jd.iwmss.stock.client;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StockDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StockDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Cdanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Kdanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Wareid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Ware" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Num" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Daima" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Succeed" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Yn" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Jiage" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Zjine" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Yeji" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Caiguo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Bilv" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Tkcb" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StockDetail", propOrder = {
    "id",
    "cdanhao",
    "kdanhao",
    "wareid",
    "ware",
    "num",
    "daima",
    "succeed",
    "yn",
    "jiage",
    "zjine",
    "yeji",
    "caiguo",
    "bilv",
    "tkcb"
})
public class StockDetail {

    @XmlElement(name = "Id")
    protected int id;
    @XmlElement(name = "Cdanhao", required = true, type = Integer.class, nillable = true)
    protected Integer cdanhao;
    @XmlElement(name = "Kdanhao")
    protected int kdanhao;
    @XmlElement(name = "Wareid")
    protected int wareid;
    @XmlElement(name = "Ware")
    protected String ware;
    @XmlElement(name = "Num")
    protected int num;
    @XmlElement(name = "Daima")
    protected String daima;
    @XmlElement(name = "Succeed", required = true, type = Integer.class, nillable = true)
    protected Integer succeed;
    @XmlElement(name = "Yn", required = true, type = Integer.class, nillable = true)
    protected Integer yn;
    @XmlElement(name = "Jiage", required = true)
    protected BigDecimal jiage;
    @XmlElement(name = "Zjine", required = true)
    protected BigDecimal zjine;
    @XmlElement(name = "Yeji", required = true)
    protected BigDecimal yeji;
    @XmlElement(name = "Caiguo", required = true, type = Integer.class, nillable = true)
    protected Integer caiguo;
    @XmlElement(name = "Bilv")
    protected int bilv;
    @XmlElement(name = "Tkcb", required = true)
    protected BigDecimal tkcb;

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the cdanhao property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCdanhao() {
        return cdanhao;
    }

    /**
     * Sets the value of the cdanhao property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCdanhao(Integer value) {
        this.cdanhao = value;
    }

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
     * Gets the value of the wareid property.
     * 
     */
    public int getWareid() {
        return wareid;
    }

    /**
     * Sets the value of the wareid property.
     * 
     */
    public void setWareid(int value) {
        this.wareid = value;
    }

    /**
     * Gets the value of the ware property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWare() {
        return ware;
    }

    /**
     * Sets the value of the ware property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWare(String value) {
        this.ware = value;
    }

    /**
     * Gets the value of the num property.
     * 
     */
    public int getNum() {
        return num;
    }

    /**
     * Sets the value of the num property.
     * 
     */
    public void setNum(int value) {
        this.num = value;
    }

    /**
     * Gets the value of the daima property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDaima() {
        return daima;
    }

    /**
     * Sets the value of the daima property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDaima(String value) {
        this.daima = value;
    }

    /**
     * Gets the value of the succeed property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSucceed() {
        return succeed;
    }

    /**
     * Sets the value of the succeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSucceed(Integer value) {
        this.succeed = value;
    }

    /**
     * Gets the value of the yn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getYn() {
        return yn;
    }

    /**
     * Sets the value of the yn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setYn(Integer value) {
        this.yn = value;
    }

    /**
     * Gets the value of the jiage property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getJiage() {
        return jiage;
    }

    /**
     * Sets the value of the jiage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setJiage(BigDecimal value) {
        this.jiage = value;
    }

    /**
     * Gets the value of the zjine property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getZjine() {
        return zjine;
    }

    /**
     * Sets the value of the zjine property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setZjine(BigDecimal value) {
        this.zjine = value;
    }

    /**
     * Gets the value of the yeji property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getYeji() {
        return yeji;
    }

    /**
     * Sets the value of the yeji property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setYeji(BigDecimal value) {
        this.yeji = value;
    }

    /**
     * Gets the value of the caiguo property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCaiguo() {
        return caiguo;
    }

    /**
     * Sets the value of the caiguo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCaiguo(Integer value) {
        this.caiguo = value;
    }

    /**
     * Gets the value of the bilv property.
     * 
     */
    public int getBilv() {
        return bilv;
    }

    /**
     * Sets the value of the bilv property.
     * 
     */
    public void setBilv(int value) {
        this.bilv = value;
    }

    /**
     * Gets the value of the tkcb property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTkcb() {
        return tkcb;
    }

    /**
     * Sets the value of the tkcb property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTkcb(BigDecimal value) {
        this.tkcb = value;
    }

}
