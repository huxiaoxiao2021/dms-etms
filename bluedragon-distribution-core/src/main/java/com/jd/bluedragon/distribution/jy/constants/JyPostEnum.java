package com.jd.bluedragon.distribution.jy.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 岗位类型
 * zhengchengfa
 */
public enum JyPostEnum {
    //
    SEND_SEAL_DMS("101","分拣发货封车岗"),
    SEND_SEAL_TYS("102","转运发货封车岗"),
    SEND_SEAL_BOARD("103","组板发货封车岗"),
    SEND_SEAL_WAREHOUSE("104","接货仓发货封车岗"),
    //
    RECEIVE_DMS("201","分拣卸车岗"),
    RECEIVE_TYS("202","转运卸车岗"),
    //
    ;
    private String code;
    private String desc;

    JyPostEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getDescByCode(String code) {
        for (JyPostEnum en : JyPostEnum.values()) {
            if (en.getCode().equals(code)) {
                return en.getDesc();
            }
        }
        return null;
    }

    public static JyPostEnum getJyPostEnumByCode(String code) {
        for (JyPostEnum en : JyPostEnum.values()) {
            if (en.getCode().equals(code)) {
                return en;
            }
        }
        return null;
    }

    /**
     * 发货岗集合
     * @return
     */
    public static List<JyPostEnum> getSendPost() {
        List<JyPostEnum> sendPost = new ArrayList<>();
        sendPost.add(JyPostEnum.SEND_SEAL_DMS);
        sendPost.add(JyPostEnum.SEND_SEAL_TYS);
        sendPost.add(JyPostEnum.SEND_SEAL_BOARD);
        sendPost.add(JyPostEnum.SEND_SEAL_WAREHOUSE);
        return sendPost;
    }

    /**
     * 卸车岗集合
     * @return
     */
    public static List<JyPostEnum> getUnloadPost() {
        List<JyPostEnum> sendPost = new ArrayList<>();
        sendPost.add(JyPostEnum.RECEIVE_DMS);
        sendPost.add(JyPostEnum.RECEIVE_TYS);
        return sendPost;
    }

    /**
     * 是否发货岗
     * @param code
     * @return
     */
    public static boolean isSendPost(String code) {
        List<JyPostEnum> sendList = JyPostEnum.getSendPost();
        for (JyPostEnum post : sendList) {
            if(post.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否卸车岗
     * @param code
     * @return
     */
    public static boolean isUnloadPost(String code) {
        List<JyPostEnum> unloadList = JyPostEnum.getUnloadPost();
        for (JyPostEnum post : unloadList) {
            if(post.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }



}
