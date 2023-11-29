package com.jd.bluedragon.distribution.print.domain;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/28 21:34
 * @Description:  标准岗位编码
 */
public class JdStdPositionCodeConfig {

    /**
     * 标准岗位编码
     */
    private List<String> positionCodes;

    public List<String> getPositionCodes() {
        return positionCodes;
    }

    public void setPositionCodes(List<String> positionCodes) {
        this.positionCodes = positionCodes;
    }
}
