
package com.jd.loss.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for returnStockInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="returnStockInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="backSortingCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="backSortingName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="backStockCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="backStockName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="backStockNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stockType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="updateDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "returnStockInfo", propOrder = {
    "backSortingCode",
    "backSortingName",
    "backStockCode",
    "backStockName",
    "backStockNo",
    "orderId",
    "stockType",
    "updateDate"
})
public class ReturnStockInfo {

    protected String backSortingCode;
    protected String backSortingName;
    protected String backStockCode;
    protected String backStockName;
    protected String backStockNo;
    protected String orderId;
    protected Integer stockType;
    protected String updateDate;

    /**
     * Gets the value of the backSortingCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackSortingCode() {
        return backSortingCode;
    }

    /**
     * Sets the value of the backSortingCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackSortingCode(String value) {
        this.backSortingCode = value;
    }

    /**
     * Gets the value of the backSortingName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackSortingName() {
        return backSortingName;
    }

    /**
     * Sets the value of the backSortingName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackSortingName(String value) {
        this.backSortingName = value;
    }

    /**
     * Gets the value of the backStockCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackStockCode() {
        return backStockCode;
    }

    /**
     * Sets the value of the backStockCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackStockCode(String value) {
        this.backStockCode = value;
    }

    /**
     * Gets the value of the backStockName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackStockName() {
        return backStockName;
    }

    /**
     * Sets the value of the backStockName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackStockName(String value) {
        this.backStockName = value;
    }

    /**
     * Gets the value of the backStockNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackStockNo() {
        return backStockNo;
    }

    /**
     * Sets the value of the backStockNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackStockNo(String value) {
        this.backStockNo = value;
    }

    /**
     * Gets the value of the orderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Sets the value of the orderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderId(String value) {
        this.orderId = value;
    }

    /**
     * Gets the value of the stockType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStockType() {
        return stockType;
    }

    /**
     * Sets the value of the stockType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStockType(Integer value) {
        this.stockType = value;
    }

    /**
     * Gets the value of the updateDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets the value of the updateDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdateDate(String value) {
        this.updateDate = value;
    }

}
