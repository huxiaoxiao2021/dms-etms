package com.jd.bluedragon.external.crossbow.ems.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *     全国邮政返回对象
 *
 * @author wuzuxiang
 * @since 2019/12/27
 **/
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class EMSResponse {

    @XmlElement(name = "result")
    private String result;

    @XmlElement(name = "errorDesc")
    private String errorDesc;

    @XmlElement(name = "errorCode")
    private String errorCode;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
