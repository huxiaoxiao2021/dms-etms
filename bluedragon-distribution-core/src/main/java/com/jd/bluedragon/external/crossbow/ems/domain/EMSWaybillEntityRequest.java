package com.jd.bluedragon.external.crossbow.ems.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * <p>
 *     全国邮政请求对象
 *
 * @author wuzuxiang
 * @since 2019/12/27
 **/
@XmlRootElement(name = "XMLInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class EMSWaybillEntityRequest {

    @XmlElement(name = "sysAccount")
    private String sysAccount;

    @XmlElement(name = "passWord")
    private String passWord;

    @XmlElement(name = "printKind")
    private String printKind;

    @XmlElement(name = "printDatas")
    private EMSWaybillEntityListDto printDatas;

    public String getSysAccount() {
        return sysAccount;
    }

    public void setSysAccount(String sysAccount) {
        this.sysAccount = sysAccount;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPrintKind() {
        return printKind;
    }

    public void setPrintKind(String printKind) {
        this.printKind = printKind;
    }

    public EMSWaybillEntityListDto getPrintDatas() {
        return printDatas;
    }

    public void setPrintDatas(EMSWaybillEntityListDto printDatas) {
        this.printDatas = printDatas;
    }
}
