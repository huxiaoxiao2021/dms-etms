
package com.jd.fa.refundService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfReqSheet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfReqSheet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReqSheet" type="{http://tempuri.org/}ReqSheet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfReqSheet", propOrder = {
    "reqSheet"
})
public class ArrayOfReqSheet {

    @XmlElement(name = "ReqSheet", nillable = true)
    protected List<ReqSheet> reqSheet;

    /**
     * Gets the value of the reqSheet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reqSheet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReqSheet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReqSheet }
     * 
     * 
     */
    public List<ReqSheet> getReqSheet() {
        if (reqSheet == null) {
            reqSheet = new ArrayList<ReqSheet>();
        }
        return this.reqSheet;
    }

}
