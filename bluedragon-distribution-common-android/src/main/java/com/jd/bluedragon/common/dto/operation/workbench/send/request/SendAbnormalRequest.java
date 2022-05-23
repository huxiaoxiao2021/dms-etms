package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName SendAbnormalPackRequest
 * @Description
 * @Author wyh
 * @Date 2022/5/19 17:55
 **/
public class SendAbnormalRequest implements Serializable {

    private static final long serialVersionUID = -7121343145796741992L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * send_detail业务主键
     */
    private String sendDetailBizId;

    /**
     * 车牌号
     */
    private String vehicleNumber;
}
