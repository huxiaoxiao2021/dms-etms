
package com.jd.fms.finance.client;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for groupModel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="groupModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bumeng" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="creatdate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="daoxie" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fangshi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fenlei" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ibumen" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ifangshi" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ifenlei" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ijiedai" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ijigou" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ischeck" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="iyinhang" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="jiedai" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="jigou" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="jingban" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="jingbancode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="laiyuan" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="laiyuancode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastdate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="luru" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lurucode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qianzi" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="qite" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tableName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="yeji" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="yinghang" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="yn" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="youhui" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="yun" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="zjine" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "groupModel", propOrder = {
    "bumeng",
    "city",
    "creatdate",
    "daoxie",
    "fangshi",
    "fenlei",
    "ibumen",
    "ifangshi",
    "ifenlei",
    "ijiedai",
    "ijigou",
    "ischeck",
    "iyinhang",
    "jiedai",
    "jigou",
    "jingban",
    "jingbancode",
    "laiyuan",
    "laiyuancode",
    "lastdate",
    "luru",
    "lurucode",
    "qianzi",
    "qite",
    "remark",
    "tableName",
    "yeji",
    "yinghang",
    "yn",
    "youhui",
    "yun",
    "zjine"
})
public class GroupModel {

    protected String bumeng;
    protected String city;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creatdate;
    protected String daoxie;
    protected String fangshi;
    protected String fenlei;
    protected Integer ibumen;
    protected Integer ifangshi;
    protected Integer ifenlei;
    protected Integer ijiedai;
    protected Integer ijigou;
    protected Integer ischeck;
    protected Integer iyinhang;
    protected String jiedai;
    protected String jigou;
    protected String jingban;
    protected String jingbancode;
    protected String laiyuan;
    protected String laiyuancode;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastdate;
    protected String luru;
    protected String lurucode;
    protected Integer qianzi;
    protected BigDecimal qite;
    protected String remark;
    protected String tableName;
    protected BigDecimal yeji;
    protected String yinghang;
    protected Integer yn;
    protected BigDecimal youhui;
    protected BigDecimal yun;
    protected BigDecimal zjine;

    /**
     * Gets the value of the bumeng property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBumeng() {
        return bumeng;
    }

    /**
     * Sets the value of the bumeng property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBumeng(String value) {
        this.bumeng = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the creatdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreatdate() {
        return creatdate;
    }

    /**
     * Sets the value of the creatdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreatdate(XMLGregorianCalendar value) {
        this.creatdate = value;
    }

    /**
     * Gets the value of the daoxie property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDaoxie() {
        return daoxie;
    }

    /**
     * Sets the value of the daoxie property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDaoxie(String value) {
        this.daoxie = value;
    }

    /**
     * Gets the value of the fangshi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFangshi() {
        return fangshi;
    }

    /**
     * Sets the value of the fangshi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFangshi(String value) {
        this.fangshi = value;
    }

    /**
     * Gets the value of the fenlei property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFenlei() {
        return fenlei;
    }

    /**
     * Sets the value of the fenlei property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFenlei(String value) {
        this.fenlei = value;
    }

    /**
     * Gets the value of the ibumen property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIbumen() {
        return ibumen;
    }

    /**
     * Sets the value of the ibumen property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIbumen(Integer value) {
        this.ibumen = value;
    }

    /**
     * Gets the value of the ifangshi property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIfangshi() {
        return ifangshi;
    }

    /**
     * Sets the value of the ifangshi property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIfangshi(Integer value) {
        this.ifangshi = value;
    }

    /**
     * Gets the value of the ifenlei property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIfenlei() {
        return ifenlei;
    }

    /**
     * Sets the value of the ifenlei property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIfenlei(Integer value) {
        this.ifenlei = value;
    }

    /**
     * Gets the value of the ijiedai property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIjiedai() {
        return ijiedai;
    }

    /**
     * Sets the value of the ijiedai property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIjiedai(Integer value) {
        this.ijiedai = value;
    }

    /**
     * Gets the value of the ijigou property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIjigou() {
        return ijigou;
    }

    /**
     * Sets the value of the ijigou property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIjigou(Integer value) {
        this.ijigou = value;
    }

    /**
     * Gets the value of the ischeck property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIscheck() {
        return ischeck;
    }

    /**
     * Sets the value of the ischeck property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIscheck(Integer value) {
        this.ischeck = value;
    }

    /**
     * Gets the value of the iyinhang property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIyinhang() {
        return iyinhang;
    }

    /**
     * Sets the value of the iyinhang property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIyinhang(Integer value) {
        this.iyinhang = value;
    }

    /**
     * Gets the value of the jiedai property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJiedai() {
        return jiedai;
    }

    /**
     * Sets the value of the jiedai property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJiedai(String value) {
        this.jiedai = value;
    }

    /**
     * Gets the value of the jigou property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJigou() {
        return jigou;
    }

    /**
     * Sets the value of the jigou property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJigou(String value) {
        this.jigou = value;
    }

    /**
     * Gets the value of the jingban property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJingban() {
        return jingban;
    }

    /**
     * Sets the value of the jingban property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJingban(String value) {
        this.jingban = value;
    }

    /**
     * Gets the value of the jingbancode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJingbancode() {
        return jingbancode;
    }

    /**
     * Sets the value of the jingbancode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJingbancode(String value) {
        this.jingbancode = value;
    }

    /**
     * Gets the value of the laiyuan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLaiyuan() {
        return laiyuan;
    }

    /**
     * Sets the value of the laiyuan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLaiyuan(String value) {
        this.laiyuan = value;
    }

    /**
     * Gets the value of the laiyuancode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLaiyuancode() {
        return laiyuancode;
    }

    /**
     * Sets the value of the laiyuancode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLaiyuancode(String value) {
        this.laiyuancode = value;
    }

    /**
     * Gets the value of the lastdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastdate() {
        return lastdate;
    }

    /**
     * Sets the value of the lastdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastdate(XMLGregorianCalendar value) {
        this.lastdate = value;
    }

    /**
     * Gets the value of the luru property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLuru() {
        return luru;
    }

    /**
     * Sets the value of the luru property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLuru(String value) {
        this.luru = value;
    }

    /**
     * Gets the value of the lurucode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLurucode() {
        return lurucode;
    }

    /**
     * Sets the value of the lurucode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLurucode(String value) {
        this.lurucode = value;
    }

    /**
     * Gets the value of the qianzi property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getQianzi() {
        return qianzi;
    }

    /**
     * Sets the value of the qianzi property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setQianzi(Integer value) {
        this.qianzi = value;
    }

    /**
     * Gets the value of the qite property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getQite() {
        return qite;
    }

    /**
     * Sets the value of the qite property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setQite(BigDecimal value) {
        this.qite = value;
    }

    /**
     * Gets the value of the remark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemark() {
        return remark;
    }

    /**
     * Sets the value of the remark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemark(String value) {
        this.remark = value;
    }

    /**
     * Gets the value of the tableName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the value of the tableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTableName(String value) {
        this.tableName = value;
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
     * Gets the value of the yinghang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYinghang() {
        return yinghang;
    }

    /**
     * Sets the value of the yinghang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYinghang(String value) {
        this.yinghang = value;
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
     * Gets the value of the youhui property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getYouhui() {
        return youhui;
    }

    /**
     * Sets the value of the youhui property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setYouhui(BigDecimal value) {
        this.youhui = value;
    }

    /**
     * Gets the value of the yun property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getYun() {
        return yun;
    }

    /**
     * Sets the value of the yun property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setYun(BigDecimal value) {
        this.yun = value;
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

}
