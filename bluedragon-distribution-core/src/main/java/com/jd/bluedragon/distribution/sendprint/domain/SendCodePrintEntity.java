package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName SendCodePrintEntity
 * @date 2019/5/17
 */
public class SendCodePrintEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 目的道口号
     **/
    private String destinationCrossCode;

    /**
     * 目的笼车号
     **/
    private String destinationTabletrolleyCode;

    /**
     * 特殊说明，例如生鲜的“鲜”字
     */
    private String flagText;

    public String getDestinationCrossCode() {
        return destinationCrossCode;
    }

    public void setDestinationCrossCode(String destinationCrossCode) {
        this.destinationCrossCode = destinationCrossCode;
    }

    public String getDestinationTabletrolleyCode() {
        return destinationTabletrolleyCode;
    }

    public void setDestinationTabletrolleyCode(String destinationTabletrolleyCode) {
        this.destinationTabletrolleyCode = destinationTabletrolleyCode;
    }

    public String getFlagText() {
        return flagText;
    }

    public void setFlagText(String flagText) {
        this.flagText = flagText;
    }
}
