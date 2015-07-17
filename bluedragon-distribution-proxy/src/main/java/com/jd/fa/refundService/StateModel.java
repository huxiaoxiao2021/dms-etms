
package com.jd.fa.refundService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StateModel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StateModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IsChecked" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SourceType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="RequestType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="RealState" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StateModel", propOrder = {
    "requestId",
    "state",
    "isChecked",
    "sourceType",
    "requestType",
    "realState"
})
public class StateModel {

    @XmlElement(name = "RequestId")
    protected int requestId;
    @XmlElement(name = "State")
    protected int state;
    @XmlElement(name = "IsChecked", required = true, type = Integer.class, nillable = true)
    protected Integer isChecked;
    @XmlElement(name = "SourceType", required = true, type = Integer.class, nillable = true)
    protected Integer sourceType;
    @XmlElement(name = "RequestType", required = true, type = Integer.class, nillable = true)
    protected Integer requestType;
    @XmlElement(name = "RealState")
    protected int realState;

    /**
     * Gets the value of the requestId property.
     * 
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     */
    public void setRequestId(int value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the state property.
     * 
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     */
    public void setState(int value) {
        this.state = value;
    }

    /**
     * Gets the value of the isChecked property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsChecked() {
        return isChecked;
    }

    /**
     * Sets the value of the isChecked property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsChecked(Integer value) {
        this.isChecked = value;
    }

    /**
     * Gets the value of the sourceType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSourceType() {
        return sourceType;
    }

    /**
     * Sets the value of the sourceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSourceType(Integer value) {
        this.sourceType = value;
    }

    /**
     * Gets the value of the requestType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRequestType() {
        return requestType;
    }

    /**
     * Sets the value of the requestType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRequestType(Integer value) {
        this.requestType = value;
    }

    /**
     * Gets the value of the realState property.
     * 
     */
    public int getRealState() {
        return realState;
    }

    /**
     * Sets the value of the realState property.
     * 
     */
    public void setRealState(int value) {
        this.realState = value;
    }

}
