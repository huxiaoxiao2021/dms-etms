
package com.jd.fa.orderrefund;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for blackUserQuery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="blackUserQuery">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="blackUsers" type="{http://www.360buy.com}blackUserModel" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="endTimeStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orders" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="refundcount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="startTimeStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "blackUserQuery", propOrder = {
    "blackUsers",
    "endTimeStr",
    "orders",
    "refundcount",
    "startTimeStr"
})
public class BlackUserQuery {

    @XmlElement(nillable = true)
    protected List<BlackUserModel> blackUsers;
    protected String endTimeStr;
    @XmlElement(nillable = true)
    protected List<Integer> orders;
    protected int refundcount;
    protected String startTimeStr;

    /**
     * Gets the value of the blackUsers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the blackUsers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBlackUsers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BlackUserModel }
     * 
     * 
     */
    public List<BlackUserModel> getBlackUsers() {
        if (blackUsers == null) {
            blackUsers = new ArrayList<BlackUserModel>();
        }
        return this.blackUsers;
    }

    /**
     * Gets the value of the endTimeStr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndTimeStr() {
        return endTimeStr;
    }

    /**
     * Sets the value of the endTimeStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndTimeStr(String value) {
        this.endTimeStr = value;
    }

    /**
     * Gets the value of the orders property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orders property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrders().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getOrders() {
        if (orders == null) {
            orders = new ArrayList<Integer>();
        }
        return this.orders;
    }

    /**
     * Gets the value of the refundcount property.
     * 
     */
    public int getRefundcount() {
        return refundcount;
    }

    /**
     * Sets the value of the refundcount property.
     * 
     */
    public void setRefundcount(int value) {
        this.refundcount = value;
    }

    /**
     * Gets the value of the startTimeStr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartTimeStr() {
        return startTimeStr;
    }

    /**
     * Sets the value of the startTimeStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartTimeStr(String value) {
        this.startTimeStr = value;
    }

}
