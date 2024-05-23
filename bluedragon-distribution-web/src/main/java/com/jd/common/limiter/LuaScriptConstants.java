package com.jd.common.limiter;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName LuaScriptConstants
 * @Description
 * @Author wyh
 * @Date 2023/5/18 16:46
 **/
@Getter
@AllArgsConstructor
public enum LuaScriptConstants {

    RATE_LIMIT_LUA("JY:RATE_LIMIT_LUA", "限流服务LUA脚本");

    private String code;
    private String desc;


}
