
package com.jd.pop.sortcenter.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for venderOperInfoResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="venderOperInfoResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="resultCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resultDescrib" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="venderOperateInfo" type="{http://offlineorder.service.order.pop.jd.com/}venderOperateInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "venderOperInfoResult", propOrder = {
    "resultCode",
    "resultDescrib",
    "success",
    "venderOperateInfo"
})
public class VenderOperInfoResult {

    protected String resultCode;
    protected String resultDescrib;
    protected boolean success;
    protected VenderOperateInfo venderOperateInfo;

    /**
     * Gets the value of the resultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultCode(String value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the resultDescrib property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultDescrib() {
        return resultDescrib;
    }

    /**
     * Sets the value of the resultDescrib property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultDescrib(String value) {
        this.resultDescrib = value;
    }

    /**
     * Gets the value of the success property.
     * 
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     */
    public void setSuccess(boolean value) {
        this.success = value;
    }

    /**
     * Gets the value of the venderOperateInfo property.
     * 
     * @return
     *     possible object is
     *     {@link VenderOperateInfo }
     *     
     */
    public VenderOperateInfo getVenderOperateInfo() {
        return venderOperateInfo;
    }

    /**
     * Sets the value of the venderOperateInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link VenderOperateInfo }
     *     
     */
    public void setVenderOperateInfo(VenderOperateInfo value) {
        this.venderOperateInfo = value;
    }

}
