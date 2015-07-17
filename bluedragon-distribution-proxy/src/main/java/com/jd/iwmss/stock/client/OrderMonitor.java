
package com.jd.iwmss.stock.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrderMonitor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrderMonitor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SkuId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DeptId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ClassId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IsPurchase" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumTotal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumRk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumCk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumZt" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumYd" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderMonitor", propOrder = {
    "skuId",
    "deptId",
    "sId",
    "classId",
    "isPurchase",
    "numTotal",
    "numRk",
    "numCk",
    "numZt",
    "numYd"
})
public class OrderMonitor {

    @XmlElement(name = "SkuId")
    protected int skuId;
    @XmlElement(name = "DeptId")
    protected int deptId;
    @XmlElement(name = "SId")
    protected int sId;
    @XmlElement(name = "ClassId")
    protected int classId;
    @XmlElement(name = "IsPurchase")
    protected int isPurchase;
    @XmlElement(name = "NumTotal")
    protected int numTotal;
    @XmlElement(name = "NumRk")
    protected int numRk;
    @XmlElement(name = "NumCk")
    protected int numCk;
    @XmlElement(name = "NumZt")
    protected int numZt;
    @XmlElement(name = "NumYd")
    protected int numYd;

    /**
     * Gets the value of the skuId property.
     * 
     */
    public int getSkuId() {
        return skuId;
    }

    /**
     * Sets the value of the skuId property.
     * 
     */
    public void setSkuId(int value) {
        this.skuId = value;
    }

    /**
     * Gets the value of the deptId property.
     * 
     */
    public int getDeptId() {
        return deptId;
    }

    /**
     * Sets the value of the deptId property.
     * 
     */
    public void setDeptId(int value) {
        this.deptId = value;
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
     * Gets the value of the classId property.
     * 
     */
    public int getClassId() {
        return classId;
    }

    /**
     * Sets the value of the classId property.
     * 
     */
    public void setClassId(int value) {
        this.classId = value;
    }

    /**
     * Gets the value of the isPurchase property.
     * 
     */
    public int getIsPurchase() {
        return isPurchase;
    }

    /**
     * Sets the value of the isPurchase property.
     * 
     */
    public void setIsPurchase(int value) {
        this.isPurchase = value;
    }

    /**
     * Gets the value of the numTotal property.
     * 
     */
    public int getNumTotal() {
        return numTotal;
    }

    /**
     * Sets the value of the numTotal property.
     * 
     */
    public void setNumTotal(int value) {
        this.numTotal = value;
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

    /**
     * Gets the value of the numYd property.
     * 
     */
    public int getNumYd() {
        return numYd;
    }

    /**
     * Sets the value of the numYd property.
     * 
     */
    public void setNumYd(int value) {
        this.numYd = value;
    }

}
