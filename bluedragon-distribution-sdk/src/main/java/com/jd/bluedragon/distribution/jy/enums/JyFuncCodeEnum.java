package com.jd.bluedragon.distribution.jy.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum JyFuncCodeEnum {

    UNSEAL_CAR_POSITION("UNSEAL_CAR_POSITION", "拣运到车岗"),
    UNLOAD_CAR_POSITION("UNLOAD_CAR_POSITION", "分拣卸车岗"),
    SEND_CAR_POSITION("SEND_CAR_POSITION", "分拣发货封车岗"),
    EXCEPTION_POSITION("EXCEPTION_POSITION", "拣运异常岗"),
    WAREHOUSE_INSPECTION("WAREHOUSE_INSPECTION", "接货仓验货岗"),
    TYS_UNLOAD_CAR_POSITION("TYS_UNLOAD_CAR_POSITION", "转运卸车岗"),
    TYS_SEND_CAR_POSITION("TYS_SEND_CAR_POSITION", "转运发货封车岗"),
    WEIGHT_VOLUME_CALIBRATE_POSITION("WEIGHT_VOLUME_CALIBRATE_POSITION", "称重量方校准岗"),
    COMBOARD_SEND_POSITION("COMBOARD_SEND_POSITION","分拣组板发货岗"),
    COMBOARD_SEAL_POSITION("COMBOARD_SEAL_POSITION","分拣组板封车岗"),
    PATROL_MANAGER_POSITION("PATROL_MANAGER_POSITION","任务线上化管理岗"),
    WAREHOUSE_SEND_POSITION("WAREHOUSE_SEND_POSITION", "接货仓发货封车岗");

    private static final Map<String, String> FUNC_CODE_ENUM_MAP;

    static {
        FUNC_CODE_ENUM_MAP = new HashMap<String, String >();
        for (JyFuncCodeEnum jyFuncCodeEnum : JyFuncCodeEnum.values()) {
            FUNC_CODE_ENUM_MAP.put(jyFuncCodeEnum.code,jyFuncCodeEnum.getName());
        }
    }

    private String code;
    private String name;
    JyFuncCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getFuncNameByCode(String code) {
        return FUNC_CODE_ENUM_MAP.get(code);
    }
    
    public String getName() {
        return name;
    }
    
    public String getCode() {
        return code;
    }



    public static JyFuncCodeEnum getJyPostEnumByCode(String code) {
        for (JyFuncCodeEnum en : JyFuncCodeEnum.values()) {
            if (en.getCode().equals(code)) {
                return en;
            }
        }
        return null;
    }

    public static String getDescByCode(String code) {
        for (JyFuncCodeEnum en : JyFuncCodeEnum.values()) {
            if (en.getCode().equals(code)) {
                return en.getName();
            }
        }
        return "未知";
    }


    /**
     * 查询该岗位是否关注新集齐模型数据
     * @param code
     * @return
     */
    public static boolean isFocusCollect(String code) {
        List<JyFuncCodeEnum> focusPosts = JyFuncCodeEnum.getFocusCollectPosts();
        if(focusPosts == null || focusPosts.size() <= 0) {
            return false;
        }
        for (JyFuncCodeEnum jyFuncCodeEnum : focusPosts) {
            if(jyFuncCodeEnum.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 所有关注新集齐模型的岗位
     * @return
     */
    public static List<JyFuncCodeEnum> getFocusCollectPosts() {
        List<JyFuncCodeEnum> focusCollectPosts = new ArrayList<JyFuncCodeEnum>();
        focusCollectPosts.add(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION);
        return focusCollectPosts;
    }


    /**
     * 是否发货岗
     * @param code
     * @return
     */
    public static boolean isSendPost(String code) {
        List<JyFuncCodeEnum> sendList = JyFuncCodeEnum.getSendPost();
        for (JyFuncCodeEnum post : sendList) {
            if(post.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 发货岗集合
     * @return
     */
    public static List<JyFuncCodeEnum> getSendPost() {
        List<JyFuncCodeEnum> sendPost = new ArrayList<JyFuncCodeEnum>();
        sendPost.add(JyFuncCodeEnum.SEND_CAR_POSITION);
        sendPost.add(JyFuncCodeEnum.TYS_SEND_CAR_POSITION);
        sendPost.add(JyFuncCodeEnum.COMBOARD_SEND_POSITION);
        sendPost.add(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION);
        return sendPost;
    }


}
