
package com.jd.iwmss.stock.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SkuStockNP complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SkuStockNP">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Wid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OrgId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Sid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumOrderYd" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumTransferYdRk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumTransferYdCk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumRkCgYd" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumRk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumCk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumZt" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkuStockNP", propOrder = {
    "wid",
    "orgId",
    "sid",
    "numOrderYd",
    "numTransferYdRk",
    "numTransferYdCk",
    "numRkCgYd",
    "numRk",
    "numCk",
    "numZt"
})
public class SkuStockNP {

    @XmlElement(name = "Wid")
    protected int wid;
    @XmlElement(name = "OrgId")
    protected int orgId;
    @XmlElement(name = "Sid")
    protected int sid;
    @XmlElement(name = "NumOrderYd")
    protected int numOrderYd;
    @XmlElement(name = "NumTransferYdRk")
    protected int numTransferYdRk;
    @XmlElement(name = "NumTransferYdCk")
    protected int numTransferYdCk;
    @XmlElement(name = "NumRkCgYd")
    protected int numRkCgYd;
    @XmlElement(name = "NumRk")
    protected int numRk;
    @XmlElement(name = "NumCk")
    protected int numCk;
    @XmlElement(name = "NumZt")
    protected int numZt;

    /**
     * Gets the value of the wid property.
     * 
     */
    public int getWid() {
        return wid;
    }

    /**
     * Sets the value of the wid property.
     * 
     */
    public void setWid(int value) {
        this.wid = value;
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

    /**
     * Gets the value of the numOrderYd property.
     * 
     */
    public int getNumOrderYd() {
        return numOrderYd;
    }

    /**
     * Sets the value of the numOrderYd property.
     * 
     */
    public void setNumOrderYd(int value) {
        this.numOrderYd = value;
    }

    /**
     * Gets the value of the numTransferYdRk property.
     * 
     */
    public int getNumTransferYdRk() {
        return numTransferYdRk;
    }

    /**
     * Sets the value of the numTransferYdRk property.
     * 
     */
    public void setNumTransferYdRk(int value) {
        this.numTransferYdRk = value;
    }

    /**
     * Gets the value of the numTransferYdCk property.
     * 
     */
    public int getNumTransferYdCk() {
        return numTransferYdCk;
    }

    /**
     * Sets the value of the numTransferYdCk property.
     * 
     */
    public void setNumTransferYdCk(int value) {
        this.numTransferYdCk = value;
    }

    /**
     * Gets the value of the numRkCgYd property.
     * 
     */
    public int getNumRkCgYd() {
        return numRkCgYd;
    }

    /**
     * Sets the value of the numRkCgYd property.
     * 
     */
    public void setNumRkCgYd(int value) {
        this.numRkCgYd = value;
    }

    /**
     * Gets the value of the numRk property.
     * 
     */
    public int getNumRk() {
        return numRk;
    }

    /**
     * Sets the value of the numRk property.
     * 
     */
    public void setNumRk(int value) {
        this.numRk = value;
    }

    /**
     * Gets the value of the numCk property.
     * 
     */
    public int getNumCk() {
        return numCk;
    }

    /**
     * Sets the value of the numCk property.
     * 
     */
    public void setNumCk(int value) {
        this.numCk = value;
    }

    /**
     * Gets the value of the numZt property.
     * 
     */
    public int getNumZt() {
        return numZt;
    }

    /**
     * Sets the value of the numZt property.
     * 
     */
    public void setNumZt(int value) {
        this.numZt = value;
    }

}
