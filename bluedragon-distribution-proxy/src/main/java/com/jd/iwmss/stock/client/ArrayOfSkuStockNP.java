
package com.jd.iwmss.stock.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfSkuStockNP complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSkuStockNP">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SkuStockNP" type="{http://360buy.com/}SkuStockNP" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSkuStockNP", propOrder = {
    "skuStockNP"
})
public class ArrayOfSkuStockNP {

    @XmlElement(name = "SkuStockNP", nillable = true)
    protected List<SkuStockNP> skuStockNP;

    /**
     * Gets the value of the skuStockNP property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the skuStockNP property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSkuStockNP().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SkuStockNP }
     * 
     * 
     */
    public List<SkuStockNP> getSkuStockNP() {
        if (skuStockNP == null) {
            skuStockNP = new ArrayList<SkuStockNP>();
        }
        return this.skuStockNP;
    }

}
