package com.jd.bluedragon.distribution.reverse.domain;

/**
 * 仓储逆向报文扩展字段
 */
public class ExtInfos {

    /**
     * 旧包裹号
     */
    private String originPackageCodes;

    public String getOriginPackageCodes() {
        return originPackageCodes;
    }

    public void setOriginPackageCodes(String originPackageCodes) {
        this.originPackageCodes = originPackageCodes;
    }
}
