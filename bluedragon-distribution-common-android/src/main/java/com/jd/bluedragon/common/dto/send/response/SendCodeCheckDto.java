package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/7/27
 */
public class SendCodeCheckDto  implements Serializable {
    private static final long serialVersionUID = 1L;
    private int key;
    private String String;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public java.lang.String getString() {
        return String;
    }

    public void setString(java.lang.String string) {
        String = string;
    }
}
