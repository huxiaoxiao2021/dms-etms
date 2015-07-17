
package com.jd.iwmss.stock.client;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfStockDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfStockDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StockDetail" type="{http://360buy.com/}StockDetail" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfStockDetail", propOrder = {
		"stockDetail"
})
public class ArrayOfStockDetail {

	@XmlElement(name = "StockDetail", nillable = true)
	protected List<StockDetail> stockDetail;

	/**
	 * Gets the value of the stockDetail property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the stockDetail property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getStockDetail().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link StockDetail }
	 * 
	 * 
	 */
	public List<StockDetail> getStockDetail() {
		if (this.stockDetail == null) {
			this.stockDetail = new ArrayList<StockDetail>();
		}
		return this.stockDetail;
	}


	public void setStockDetail(List<StockDetail> stockDetail) {
		this.stockDetail = stockDetail;
	}
}
