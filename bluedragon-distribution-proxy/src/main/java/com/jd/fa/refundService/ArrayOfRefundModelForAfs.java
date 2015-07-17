
package com.jd.fa.refundService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfRefundModelForAfs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfRefundModelForAfs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RefundModelForAfs" type="{http://tempuri.org/}RefundModelForAfs" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRefundModelForAfs", propOrder = {
    "refundModelForAfs"
})
public class ArrayOfRefundModelForAfs {

    @XmlElement(name = "RefundModelForAfs", nillable = true)
    protected List<RefundModelForAfs> refundModelForAfs;

    /**
     * Gets the value of the refundModelForAfs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the refundModelForAfs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRefundModelForAfs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RefundModelForAfs }
     * 
     * 
     */
    public List<RefundModelForAfs> getRefundModelForAfs() {
        if (refundModelForAfs == null) {
            refundModelForAfs = new ArrayList<RefundModelForAfs>();
        }
        return this.refundModelForAfs;
    }

}
