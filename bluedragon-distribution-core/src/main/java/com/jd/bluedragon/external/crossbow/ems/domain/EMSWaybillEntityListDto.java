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
 * @since 2019/12/30
 **/
@XmlRootElement(name = "printDatas")
@XmlAccessorType(XmlAccessType.FIELD)
public class EMSWaybillEntityListDto {

    @XmlElement(name = "printData")
    private List<EMSWaybillEntityDto> printDatas;

    public List<EMSWaybillEntityDto> getPrintDatas() {
        return printDatas;
    }

    public void setPrintDatas(List<EMSWaybillEntityDto> printDatas) {
        this.printDatas = printDatas;
    }
}
