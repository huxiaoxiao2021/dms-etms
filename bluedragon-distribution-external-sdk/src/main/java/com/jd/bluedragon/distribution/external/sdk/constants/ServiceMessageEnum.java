package com.jd.bluedragon.distribution.external.sdk.constants;

/**
 * @ClassName ServiceMessageEnum
 * @Description 对外错误码枚举类
 * @Author wyh
 * @Date 2021/1/4 16:38
 **/
public enum ServiceMessageEnum {
    CODE_SUCCESS(200,"成功"),
    CODE_FAIL(500, "请求失败"),
    CODE_PARAM_ERROR(400, "参数错误"),
    CODE_DEAL_ERROR(300, "处理失败，请联系分拣小秘"),
    CODE_SEND_CODE_WARN(30001, "生成批次号为空"),
    ;

    private Integer code;

    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ServiceMessageEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
