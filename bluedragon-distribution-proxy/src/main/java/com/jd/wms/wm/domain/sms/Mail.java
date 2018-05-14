
package com.jd.wms.wm.domain.sms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Mail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Mail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MailSubject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MailBody" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SysNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Mail", propOrder = {
    "mailAddress",
    "mailSubject",
    "mailBody",
    "sysNo"
})
public class Mail {

    @XmlElement(name = "MailAddress")
    protected String mailAddress;
    @XmlElement(name = "MailSubject")
    protected String mailSubject;
    @XmlElement(name = "MailBody")
    protected String mailBody;
    @XmlElement(name = "SysNo")
    protected int sysNo;

    /**
     * Gets the value of the mailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * Sets the value of the mailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMailAddress(String value) {
        this.mailAddress = value;
    }

    /**
     * Gets the value of the mailSubject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMailSubject() {
        return mailSubject;
    }

    /**
     * Sets the value of the mailSubject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMailSubject(String value) {
        this.mailSubject = value;
    }

    /**
     * Gets the value of the mailBody property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMailBody() {
        return mailBody;
    }

    /**
     * Sets the value of the mailBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMailBody(String value) {
        this.mailBody = value;
    }

    /**
     * Gets the value of the sysNo property.
     * 
     */
    public int getSysNo() {
        return sysNo;
    }

    /**
     * Sets the value of the sysNo property.
     * 
     */
    public void setSysNo(int value) {
        this.sysNo = value;
    }

}
