package com.jd.bluedragon.distribution.api.response.sendcode;

import java.io.Serializable;

/**
 * @ClassName SendCodeResponse
 * @Description
 * @Author wyh
 * @Date 2021/6/21 15:26
 **/
public class SendCodeResponse implements Serializable {

    private static final long serialVersionUID = -7406618087316016823L;

    public static final Integer CODE_PARAMETER_ERROR = 1001;
    public static final String MESSAGE_PARAMETER_ERROR = "批次号[{0}]不符合规则！";

    public static final Integer CODE_NOT_EXIST_ERROR = 2001;
    public static final String MESSAGE_NOT_EXIST_ERROR = "批次号[{0}]不存在或已失效，请检查或更换！";
}
