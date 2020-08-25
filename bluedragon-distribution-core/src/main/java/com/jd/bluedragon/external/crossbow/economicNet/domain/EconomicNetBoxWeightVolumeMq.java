package com.jd.bluedragon.external.crossbow.economicNet.domain;

import java.io.Serializable;

/**
 * 经济网按箱称重回传MQ
 *
 * @author: hujiping
 * @date: 2020/8/25 10:37
 */
public class EconomicNetBoxWeightVolumeMq extends EconomicNetBoxWeightVolumeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 扫描人编码
     * */
    private String scanManCode;

    /**
     * 扫描人ID
     * */
    private Integer scanManId;

    public String getScanManCode() {
        return scanManCode;
    }

    public void setScanManCode(String scanManCode) {
        this.scanManCode = scanManCode;
    }

    public Integer getScanManId() {
        return scanManId;
    }

    public void setScanManId(Integer scanManId) {
        this.scanManId = scanManId;
    }
}
