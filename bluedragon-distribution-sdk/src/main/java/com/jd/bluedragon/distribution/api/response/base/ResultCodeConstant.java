package com.jd.bluedragon.distribution.api.response.base;

/**
 * 返回码常量
 *
 * @author fanggang7
 * @time 2019-11-20 19:23:13 周三
 */
public class ResultCodeConstant {

    public static final int SUCCESS = 0;
    public static final int FAIL = -1;

    public static final int NULL_ARGUMENT = 1001;
    public static final int ILLEGAL_ARGUMENT = 1002;
    public static final int STATUS_INVALID = 1003;
    public static final int MISMATCH_CONDITION = 1004;

    public static final int BUSINESS_EXCEPTION = 1201;
    public static final int CONCURRENT_CONFLICT = 1301;

    public static final int CALL_ERROR = 1301;
    public static final int CALL_TIMEOUT = 1302;
    public static final int CALL_REMOTE_ERROR = 1303;
    public static final int CALL_REMOTE_TIMEOUT = 1304;
    public static final int RESOURCE_NOT_EXIST = 1305;
    public static final int RESOURCE_EXIST = 1306;

    public static final int NOT_LOGIN = 1401;
    public static final int NO_PERMISSION = 1402;
    public static final int FORBIDDEN = 1403;
    public static final int NOT_FOUND = 1404;
    public static final int NOT_FOUND_ALL = 1405;

    public static final int SYSTEM_EXCEPTION = 1501;
    public static final int CAST_EXCEPTION = 1502;
    public static final int PARSE_EXCEPTION = 1503;

    public static final int IO_EXCEPTION = 1901;
}
