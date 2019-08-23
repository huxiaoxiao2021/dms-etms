package com.jd.bluedragon.common.dto.sendcode.response;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/7/27
 */
public class SendCodeCheckDto  implements Serializable {
    private static final long serialVersionUID = 1L;
    private int key;
    private String value;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
