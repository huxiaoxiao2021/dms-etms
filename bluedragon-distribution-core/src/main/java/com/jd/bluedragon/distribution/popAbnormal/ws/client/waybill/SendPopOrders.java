
package com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sendPopOrders complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sendPopOrders">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="popOrderDtoList" type="{http://impl.rpc.waybill.etms.jd.com}popOrderDto" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendPopOrders", propOrder = {
    "popOrderDtoList"
})
public class SendPopOrders {

    protected List<PopOrderDto> popOrderDtoList;

    /**
     * Gets the value of the popOrderDtoList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the popOrderDtoList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPopOrderDtoList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PopOrderDto }
     * 
     * 
     */
    public List<PopOrderDto> getPopOrderDtoList() {
        if (popOrderDtoList == null) {
            popOrderDtoList = new ArrayList<PopOrderDto>();
        }
        return this.popOrderDtoList;
    }

}
