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
}
