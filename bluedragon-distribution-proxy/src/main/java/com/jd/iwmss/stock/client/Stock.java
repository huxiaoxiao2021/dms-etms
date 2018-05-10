
package com.jd.iwmss.stock.client;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Stock complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Stock">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Kdanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Cdanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="RfType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="RfId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Orderid" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ChuruId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FeileiId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Churu" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Feilei" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Kuanx" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Jingban" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Moneyn" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Luru" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CityId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrgId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Jigou" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Laiyuan" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Zjine" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Creatdate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Lastdate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Qianzi" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Yun" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Youhui" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Qite" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Yuandanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Laiyuancode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Phdanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Cgdanhao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Rmaid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Qtfs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Actor" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Shanghai" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ToOrgId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ToSId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="StockDetails" type="{http://360buy.com/}ArrayOfStockDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Stock", propOrder = {
    "kdanhao",
    "cdanhao",
    "rfType",
    "rfId",
    "orderid",
    "churuId",
    "feileiId",
    "churu",
    "feilei",
    "kuanx",
    "jingban",
    "moneyn",
    "luru",
    "cityId",
    "city",
    "orgId",
    "jigou",
    "laiyuan",
    "zjine",
    "remark",
    "creatdate",
    "lastdate",
    "qianzi",
    "yun",
    "youhui",
    "qite",
    "yuandanhao",
    "laiyuancode",
    "phdanhao",
    "cgdanhao",
    "rmaid",
    "qtfs",
    "actor",
    "shanghai",
    "sId",
    "toOrgId",
    "toSId",
    "stockDetails"
})
@XmlSeeAlso({
    StockParamter.class
})
public class Stock {

    @XmlElement(name = "Kdanhao")
    protected int kdanhao;
    @XmlElement(name = "Cdanhao")
    protected int cdanhao;
    @XmlElement(name = "RfType")
    protected int rfType;
    @XmlElement(name = "RfId")
    protected String rfId;
    @XmlElement(name = "Orderid")
    protected long orderid;
    @XmlElement(name = "ChuruId")
    protected int churuId;
    @XmlElement(name = "FeileiId")
    protected int feileiId;
    @XmlElement(name = "Churu")
    protected String churu;
    @XmlElement(name = "Feilei")
    protected String feilei;
    @XmlElement(name = "Kuanx")
    protected String kuanx;
    @XmlElement(name = "Jingban")
    protected String jingban;
    @XmlElement(name = "Moneyn")
    protected int moneyn;
    @XmlElement(name = "Luru")
    protected String luru;
    @XmlElement(name = "CityId")
    protected int cityId;
    @XmlElement(name = "City")
    protected String city;
    @XmlElement(name = "OrgId")
    protected int orgId;
    @XmlElement(name = "Jigou")
    protected String jigou;
    @XmlElement(name = "Laiyuan")
    protected String laiyuan;
    @XmlElement(name = "Zjine", required = true)
    protected BigDecimal zjine;
    @XmlElement(name = "Remark")
    protected String remark;
    @XmlElement(name = "Creatdate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creatdate;
    @XmlElement(name = "Lastdate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastdate;
    @XmlElement(name = "Qianzi", required = true, type = Integer.class, nillable = true)
    protected Integer qianzi;
    @XmlElement(name = "Yun", required = true)
    protected BigDecimal yun;
    @XmlElement(name = "Youhui", required = true)
    protected BigDecimal youhui;
    @XmlElement(name = "Qite", required = true)
    protected BigDecimal qite;
    @XmlElement(name = "Yuandanhao", required = true, type = Integer.class, nillable = true)
    protected Integer yuandanhao;
    @XmlElement(name = "Laiyuancode")
    protected String laiyuancode;
    @XmlElement(name = "Phdanhao")
    protected int phdanhao;
    @XmlElement(name = "Cgdanhao")
    protected int cgdanhao;
    @XmlElement(name = "Rmaid")
    protected int rmaid;
    @XmlElement(name = "Qtfs")
    protected String qtfs;
    @XmlElement(name = "Actor", required = true)
    protected BigDecimal actor;
    @XmlElement(name = "Shanghai")
    protected int shanghai;
    @XmlElement(name = "SId")
    protected int sId;
    @XmlElement(name = "ToOrgId")
    protected int toOrgId;
    @XmlElement(name = "ToSId")
    protected int toSId;
    @XmlElement(name = "StockDetails")
    protected ArrayOfStockDetail stockDetails;

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
     * Gets the value of the rfType property.
     * 
     */
    public int getRfType() {
        return rfType;
    }

    /**
     * Sets the value of the rfType property.
     * 
     */
    public void setRfType(int value) {
        this.rfType = value;
    }

    /**
     * Gets the value of the rfId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfId() {
        return rfId;
    }

    /**
     * Sets the value of the rfId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfId(String value) {
        this.rfId = value;
    }

    /**
     * Gets the value of the orderid property.
     * 
     */
    public long getOrderid() {
        return orderid;
    }

    /**
     * Sets the value of the orderid property.
     * 
     */
    public void setOrderid(long value) {
        this.orderid = value;
    }

    /**
     * Gets the value of the churuId property.
     * 
     */
    public int getChuruId() {
        return churuId;
    }

    /**
     * Sets the value of the churuId property.
     * 
     */
    public void setChuruId(int value) {
        this.churuId = value;
    }

    /**
     * Gets the value of the feileiId property.
     * 
     */
    public int getFeileiId() {
        return feileiId;
    }

    /**
     * Sets the value of the feileiId property.
     * 
     */
    public void setFeileiId(int value) {
        this.feileiId = value;
    }

    /**
     * Gets the value of the churu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChuru() {
        return churu;
    }

    /**
     * Sets the value of the churu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChuru(String value) {
        this.churu = value;
    }

    /**
     * Gets the value of the feilei property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFeilei() {
        return feilei;
    }

    /**
     * Sets the value of the feilei property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeilei(String value) {
        this.feilei = value;
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
     * Gets the value of the moneyn property.
     * 
     */
    public int getMoneyn() {
        return moneyn;
    }

    /**
     * Sets the value of the moneyn property.
     * 
     */
    public void setMoneyn(int value) {
        this.moneyn = value;
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
     * Gets the value of the cityId property.
     * 
     */
    public int getCityId() {
        return cityId;
    }

    /**
     * Sets the value of the cityId property.
     * 
     */
    public void setCityId(int value) {
        this.cityId = value;
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
     * Gets the value of the orgId property.
     * 
     */
    public int getOrgId() {
        return orgId;
    }

    /**
     * Sets the value of the orgId property.
     * 
     */
    public void setOrgId(int value) {
        this.orgId = value;
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
     * Gets the value of the yuandanhao property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getYuandanhao() {
        return yuandanhao;
    }

    /**
     * Sets the value of the yuandanhao property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setYuandanhao(Integer value) {
        this.yuandanhao = value;
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
     * Gets the value of the phdanhao property.
     * 
     */
    public int getPhdanhao() {
        return phdanhao;
    }

    /**
     * Sets the value of the phdanhao property.
     * 
     */
    public void setPhdanhao(int value) {
        this.phdanhao = value;
    }

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
     * Gets the value of the rmaid property.
     * 
     */
    public int getRmaid() {
        return rmaid;
    }

    /**
     * Sets the value of the rmaid property.
     * 
     */
    public void setRmaid(int value) {
        this.rmaid = value;
    }

    /**
     * Gets the value of the qtfs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQtfs() {
        return qtfs;
    }

    /**
     * Sets the value of the qtfs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQtfs(String value) {
        this.qtfs = value;
    }

    /**
     * Gets the value of the actor property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getActor() {
        return actor;
    }

    /**
     * Sets the value of the actor property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setActor(BigDecimal value) {
        this.actor = value;
    }

    /**
     * Gets the value of the shanghai property.
     * 
     */
    public int getShanghai() {
        return shanghai;
    }

    /**
     * Sets the value of the shanghai property.
     * 
     */
    public void setShanghai(int value) {
        this.shanghai = value;
    }

    /**
     * Gets the value of the sId property.
     * 
     */
    public int getSId() {
        return sId;
    }

    /**
     * Sets the value of the sId property.
     * 
     */
    public void setSId(int value) {
        this.sId = value;
    }

    /**
     * Gets the value of the toOrgId property.
     * 
     */
    public int getToOrgId() {
        return toOrgId;
    }

    /**
     * Sets the value of the toOrgId property.
     * 
     */
    public void setToOrgId(int value) {
        this.toOrgId = value;
    }

    /**
     * Gets the value of the toSId property.
     * 
     */
    public int getToSId() {
        return toSId;
    }

    /**
     * Sets the value of the toSId property.
     * 
     */
    public void setToSId(int value) {
        this.toSId = value;
    }

    /**
     * Gets the value of the stockDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfStockDetail }
     *     
     */
    public ArrayOfStockDetail getStockDetails() {
        return stockDetails;
    }

    /**
     * Sets the value of the stockDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfStockDetail }
     *     
     */
    public void setStockDetails(ArrayOfStockDetail value) {
        this.stockDetails = value;
    }

}
