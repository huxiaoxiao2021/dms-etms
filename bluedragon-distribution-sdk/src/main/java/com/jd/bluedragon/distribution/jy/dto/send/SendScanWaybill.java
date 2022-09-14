package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendScanBarCode
 * @Description
 * @Author wyh
 * @Date 2022/5/19 17:57
 **/
public class SendScanWaybill implements Serializable {

    private static final long serialVersionUID = -9041548101278236982L;

    /**
     * 单号
     */
    private String barCode;

    /**
     * 运单下全量包裹数量
     */
    private Long allPackCount;

    /**
     * 异常类型对应扫描包裹数量
     */
    private Long scanPackCount;

    /**
     * 包裹集合
     */
    private List<SendScanPack> packList;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<SendScanPack> getPackList() {
        return packList;
    }

    public void setPackList(List<SendScanPack> packList) {
        this.packList = packList;
    }

    public Long getAllPackCount() {
        return allPackCount;
    }

    public void setAllPackCount(Long allPackCount) {
        this.allPackCount = allPackCount;
    }

    public Long getScanPackCount() {
        return scanPackCount;
    }

    public void setScanPackCount(Long scanPackCount) {
        this.scanPackCount = scanPackCount;
    }
}
