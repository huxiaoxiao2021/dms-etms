package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class CancelSendTaskReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 1670179734143714982L;

    /**
     * 取消时扫描的号
     */
    private String code;
    /**
     * 按照什么类型维度去取消 1包裹 2运单 3箱号 4板号
     */
    private Integer type;
}
