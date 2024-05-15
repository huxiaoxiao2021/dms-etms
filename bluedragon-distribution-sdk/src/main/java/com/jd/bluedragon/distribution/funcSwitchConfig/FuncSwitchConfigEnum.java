package com.jd.bluedragon.distribution.funcSwitchConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能开关配置枚举
 *
 * @author: hujiping
 * @date: 2020/9/16 16:46
 */
public enum FuncSwitchConfigEnum {

    // 核心业务功能
    FUNCTION_INSPECTION(1001,"DMS-WEB-FUNCTION-SWITCH-INSPECTION","验货"),
    FUNCTION_SORTING(2001,"DMS-WEB-FUNCTION-SWITCH-SORTING","分拣"),
    FUNCTION_SEND(3001,"DMS-WEB-FUNCTION-SWITCH-SEND","发货"),
    FUNCTION_SEAL_CAR(4001,"DMS-WEB-FUNCTION-SWITCH-SEAL-CAR","封车"),

    // 普通业务功能
    FUNCTION_SPOT_CHECK(9001,"DMS-WEB-FUNCTION-SWITCH-SPOT-CHECK","抽检"),
    FUNCTION_COLLECTION_ADDRESS(9002,"DMS-WEB-FUNCTION-SWITCH-COLLECTION-ADDRESS","集包地"),
    FUNCTION_PRE_SELL(9003,"DMS-WEB-FUNCTION-SWITCH-PRE-SELL","预售"),
    FUNCTION_ALL_MAIL(9004,"DMS-WEB-FUNCTION_SWITCH-ALL-MAIL","众邮无重量拦截"),
    FUNCTION_COMPLETE_DELIVERY(9005,"DMS-WEB-FUNCTION-SWITCH-COMPLETE_DELIVERY","纯配无重量拦截"),
    FUNCTION_FILE_INTERCEPTION(9006, "DMS-WEB-FUNCTION-FILE-INTERCEPTION-WHITELIST", "文件集包拦截"),
    FUNCTION_BC_BOX_FILTER(9007,"DMS-WEB-FUNCTION-SWITCH_BC_BOX_FILTER","BC箱号集包袋拦截"),
    FUNCTION_USE_SIMULATOR(9008,"DMS-WEB-FUNCTION-SWITCH_USE_SIMULATOR","模拟器使用权限"),

    FUNCTION_SHOW_CALL_BUTTON(9009,"DMS-WEB-FUNCTION-SWITCH_CALL_NUMBER","发货任务是否显示叫号按钮"),
    FUNCTION_SHOW_REMIND_BUTTON(9010, "DMS-WEB-FUNCTION-SWITCH_REMIND_TRANS_JOB", "待派任务是否显示催派按钮"),

    FUNCTION_CLOUD_PRINT_100101(100101, "DMS-WEB-FUNCTION-SWITCH-CLOUD-PRINT-100101", "打印客户端切换云打印-平台打印"),
    FUNCTION_CLOUD_PRINT_100102(100102, "DMS-WEB-FUNCTION-SWITCH-CLOUD-PRINT-100102", "打印客户端切换云打印-站点平台打印"),
    FUNCTION_CLOUD_PRINT_100103(100103, "DMS-WEB-FUNCTION-SWITCH-CLOUD-PRINT-100103", "打印客户端切换云打印-包裹补打"),
    FUNCTION_CLOUD_PRINT_100104(100104, "DMS-WEB-FUNCTION-SWITCH-CLOUD-PRINT-100104", "打印客户端切换云打印-换单打印"),
    FUNCTION_CLOUD_PRINT_100105(100105, "DMS-WEB-FUNCTION-SWITCH-CLOUD-PRINT-100105", "打印客户端切换云打印-包裹称重打印"),
    FUNCTION_CLOUD_PRINT_100106(100106, "DMS-WEB-FUNCTION-SWITCH-CLOUD-PRINT-100106", "打印客户端切换云打印-驻厂打印"),
    FUNCTION_CLOUD_PRINT_100107(100107, "DMS-WEB-FUNCTION-SWITCH-CLOUD-PRINT-100107", "打印客户端切换云打印-批量分拣称重"),
    FUNCTION_CLOUD_PRINT_100108(100108, "DMS-WEB-FUNCTION-SWITCH-CLOUD-PRINT-100108", "打印客户端切换云打印-快运称重打印"),
    FUNCTION_CLOUD_PRINT_100109(100109, "DMS-WEB-FUNCTION-SWITCH-CLOUD-PRINT-100109", "切换云打印-批量打印包裹标签"),
    FUNCTION_CLOUD_PRINT_100110(100110, "DMS-WEB-FUNCTION-SWITCH-CLOUD-PRINT-100110", "切换云打印-收纳暂存打印");


    private int code;
    // 权限码
    private String authCode;
    private String name;

    public static Map<Integer, FuncSwitchConfigEnum> codeMap;
    //功能名称
    public static Map<Integer, String> interceptMenuEnumMap;
    //需要走拦截遍历的配置
    public static List<FuncSwitchConfigEnum> filterList;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, FuncSwitchConfigEnum>();
        interceptMenuEnumMap = new HashMap<Integer, String>();
        filterList = new ArrayList<FuncSwitchConfigEnum>();
        filterList.add(FUNCTION_ALL_MAIL);
        filterList.add(FUNCTION_COMPLETE_DELIVERY);

        for (FuncSwitchConfigEnum _enum : FuncSwitchConfigEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            interceptMenuEnumMap.put(_enum.getCode(),_enum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     */
    public static FuncSwitchConfigEnum getEnumByKey(int code) {
        return codeMap.get(code);
    }

    FuncSwitchConfigEnum(int code, String authCode, String name) {
        this.code = code;
        this.authCode = authCode;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 返回需要走拦截逻辑的功能编码集合
     * @return
     */
    public static List<Integer> getFilterList(){
        List<Integer> list = new ArrayList<Integer>();
        for (FuncSwitchConfigEnum funcSwitchConfigEnum:filterList){
            list.add(funcSwitchConfigEnum.getCode());
        }
        return list;
    }
}
