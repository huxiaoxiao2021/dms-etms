
package com.jd.iwmss.stock.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for StockParamter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StockParamter">
 *   &lt;complexContent>
 *     &lt;extension base="{http://360buy.com/}Stock">
 *       &lt;sequence>
 *         &lt;element name="Pagination" type="{http://360buy.com/}Pagination" minOccurs="0"/>
 *         &lt;element name="MaxDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="MinDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="MinZjine" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="MaxZjine" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="WareId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="GoodsPutOrSale" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IsReturnStock" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IsNotPOP" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StockParamter", propOrder = {
    "pagination",
    "maxDate",
    "minDate",
    "minZjine",
    "maxZjine",
    "wareId",
    "goodsPutOrSale",
    "isReturnStock",
    "isNotPOP"
})
public class StockParamter
    extends Stock
{

    @XmlElement(name = "Pagination")
    protected Pagination pagination;
    @XmlElement(name = "MaxDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar maxDate;
    @XmlElement(name = "MinDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar minDate;
    @XmlElement(name = "MinZjine", required = true, type = Integer.class, nillable = true)
    protected Integer minZjine;
    @XmlElement(name = "MaxZjine", required = true, type = Integer.class, nillable = true)
    protected Integer maxZjine;
    @XmlElement(name = "WareId", required = true, type = Integer.class, nillable = true)
    protected Integer wareId;
    @XmlElement(name = "GoodsPutOrSale", required = true, type = Integer.class, nillable = true)
    protected Integer goodsPutOrSale;
    @XmlElement(name = "IsReturnStock", required = true, type = Integer.class, nillable = true)
    protected Integer isReturnStock;
    @XmlElement(name = "IsNotPOP")
    protected int isNotPOP;

    /**
     * Gets the value of the pagination property.
     * 
     * @return
     *     possible object is
     *     {@link Pagination }
     *     
     */
    public Pagination getPagination() {
        return pagination;
    }

    /**
     * Sets the value of the pagination property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pagination }
     *     
     */
    public void setPagination(Pagination value) {
        this.pagination = value;
    }

    /**
     * Gets the value of the maxDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMaxDate() {
        return maxDate;
    }

    /**
     * Sets the value of the maxDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMaxDate(XMLGregorianCalendar value) {
        this.maxDate = value;
    }

    /**
     * Gets the value of the minDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMinDate() {
        return minDate;
    }

    /**
     * Sets the value of the minDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMinDate(XMLGregorianCalendar value) {
        this.minDate = value;
    }

    /**
     * Gets the value of the minZjine property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinZjine() {
        return minZjine;
    }

    /**
     * Sets the value of the minZjine property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinZjine(Integer value) {
        this.minZjine = value;
    }

    /**
     * Gets the value of the maxZjine property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxZjine() {
        return maxZjine;
    }

    /**
     * Sets the value of the maxZjine property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxZjine(Integer value) {
        this.maxZjine = value;
    }

    /**
     * Gets the value of the wareId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWareId() {
        return wareId;
    }

    /**
     * Sets the value of the wareId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWareId(Integer value) {
        this.wareId = value;
    }

    /**
     * Gets the value of the goodsPutOrSale property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGoodsPutOrSale() {
        return goodsPutOrSale;
    }

    /**
     * Sets the value of the goodsPutOrSale property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGoodsPutOrSale(Integer value) {
        this.goodsPutOrSale = value;
    }

    /**
     * Gets the value of the isReturnStock property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsReturnStock() {
        return isReturnStock;
    }

    /**
     * Sets the value of the isReturnStock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsReturnStock(Integer value) {
        this.isReturnStock = value;
    }

    /**
     * Gets the value of the isNotPOP property.
     * 
     */
    public int getIsNotPOP() {
        return isNotPOP;
    }

    /**
     * Sets the value of the isNotPOP property.
     * 
     */
    public void setIsNotPOP(int value) {
        this.isNotPOP = value;
    }

}
