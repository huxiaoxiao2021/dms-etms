package com.jd.bluedragon.distribution.coldChain.domain;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/5/5
 * @Description:
 */
public class ColdCheckCommonResult implements Serializable {

    static final long serialVersionUID = 1L;

    /**
     * 强制拦截
     */
    private boolean forced;

    /**
     * 弱拦截
     */
    private boolean weak;


    public boolean isForced() {
        return forced;
    }

    public boolean isWeak() {
        return weak;
    }

    public boolean getForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public boolean getWeak() {
        return weak;
    }

    public void setWeak(boolean weak) {
        this.weak = weak;
    }

}
