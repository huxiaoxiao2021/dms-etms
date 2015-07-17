
package com.jd.fa.refundService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RefundReceiptDetailNew complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RefundReceiptDetailNew">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReceiptSource" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrderId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="CanRefund" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="StorageCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Cky2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="StorageName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReStockRequestNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="UserId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CheckTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RefundReceiptDetailNew", propOrder = {
    "receiptSource",
    "orderId",
    "canRefund",
    "storageCode",
    "cky2",
    "storageName",
    "reStockRequestNo",
    "userId",
    "checkTime",
    "remark"
})
public class RefundReceiptDetailNew {

    @XmlElement(name = "ReceiptSource")
    protected String receiptSource;
    @XmlElement(name = "OrderId", required = true, type = Long.class, nillable = true)
    protected Long orderId;
    @XmlElement(name = "CanRefund", required = true, type = Integer.class, nillable = true)
    protected Integer canRefund;
    @XmlElement(name = "StorageCode", required = true, type = Integer.class, nillable = true)
    protected Integer storageCode;
    @XmlElement(name = "Cky2", required = true, type = Integer.class, nillable = true)
    protected Integer cky2;
    @XmlElement(name = "StorageName")
    protected String storageName;
    @XmlElement(name = "ReStockRequestNo", required = true, type = Integer.class, nillable = true)
    protected Integer reStockRequestNo;
    @XmlElement(name = "UserId")
    protected String userId;
    @XmlElement(name = "CheckTime", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar checkTime;
    @XmlElement(name = "Remark")
    protected String remark;

    /**
     * Gets the value of the receiptSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptSource() {
        return receiptSource;
    }

    /**
     * Sets the value of the receiptSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptSource(String value) {
        this.receiptSource = value;
    }

    /**
     * Gets the value of the orderId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * Sets the value of the orderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOrderId(Long value) {
        this.orderId = value;
    }

    /**
     * Gets the value of the canRefund property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCanRefund() {
        return canRefund;
    }

    /**
     * Sets the value of the canRefund property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCanRefund(Integer value) {
        this.canRefund = value;
    }

    /**
     * Gets the value of the storageCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStorageCode() {
        return storageCode;
    }

    /**
     * Sets the value of the storageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStorageCode(Integer value) {
        this.storageCode = value;
    }

    /**
     * Gets the value of the cky2 property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCky2() {
        return cky2;
    }

    /**
     * Sets the value of the cky2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCky2(Integer value) {
        this.cky2 = value;
    }

    /**
     * Gets the value of the storageName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStorageName() {
        return storageName;
    }

    /**
     * Sets the value of the storageName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStorageName(String value) {
        this.storageName = value;
    }

    /**
     * Gets the value of the reStockRequestNo property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getReStockRequestNo() {
        return reStockRequestNo;
    }

    /**
     * Sets the value of the reStockRequestNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setReStockRequestNo(Integer value) {
        this.reStockRequestNo = value;
    }

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserId(String value) {
        this.userId = value;
    }

    /**
     * Gets the value of the checkTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCheckTime() {
        return checkTime;
    }

    /**
     * Sets the value of the checkTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCheckTime(XMLGregorianCalendar value) {
        this.checkTime = value;
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

}
