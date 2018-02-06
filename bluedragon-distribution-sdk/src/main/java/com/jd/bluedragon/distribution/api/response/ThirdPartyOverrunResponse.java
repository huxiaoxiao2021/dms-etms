package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * 验证包裹三方承运商是否超限
 * <p>
 * <p>
 * Created by lixin39 on 2018/1/30.
 */
public class ThirdPartyOverrunResponse extends JdResponse {

    public final static Integer CODE_WEIGHT_OVERRUN = 3001;
    public final static String MESSAGE_WEIGHT_OVERRUN = "重量超限";

    public final static Integer CODE_VOLUME_OVERRUN = 3002;
    public final static String MESSAGE_VOLUME_OVERRUN = "体积超限";

    public final static Integer CODE_LENGTH_OVERRUN = 3003;
    public final static String MESSAGE_LENGTH_OVERRUN = "长度超限";

    public final static Integer CODE_WIDTH_OVERRUN = 3004;
    public final static String MESSAGE_WIDTH_OVERRUN = "宽度超限";

    public final static Integer CODE_HEIGHT_OVERRUN = 3005;
    public final static String MESSAGE_HEIGHT_OVERRUN = "高度超限";

    public void set(Integer code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

}
