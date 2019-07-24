package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:25分
 */
public class ArAbnormalResponse extends JdResponse {
    public static final Integer CODE_NODATA = 20001;
    public static final String MESSAGE_NODATA = "无输入";

    public static final Integer CODE_ERRORDATA = 20002;
    public static final String MESSAGE_ERRORDATA = "无效参数";

    public static final Integer CODE_TRANSPONDREASON = 20003;
    public static final String MESSAGE_TRANSPONDREASON = "无效的异常原因";

    public static final Integer CODE_TRANSPONDTYPE = 20004;
    public static final String MESSAGE_TRANSPONDTYPE = "无效的运输类型";

    public static final Integer CODE_SITECODE = 20005;
    public static final String MESSAGE_SITECODE= "站点编码不能为空";

    public static final Integer CODE_SITENAME = 20006;
    public static final String MESSAGE_SITENAME= "站点名称不能为空";

    public static final Integer CODE_USERCODE = 20007;
    public static final String MESSAGE_USERCODE= "用户编码不能为空";

    public static final Integer CODE_USERNAME = 20008;
    public static final String MESSAGE_USERNAME= "用户姓名不能为空";

    public static final Integer CODE_TIME = 20009;
    public static final String MESSAGE_TIME= "操作时间不能为空";

    public static final Integer CODE_TIMEERROR = 20010;
    public static final String MESSAGE_TIMEERROR= "操作时间格式错误";
}
