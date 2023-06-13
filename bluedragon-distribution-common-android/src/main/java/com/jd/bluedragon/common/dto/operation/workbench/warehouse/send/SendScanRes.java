package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;


import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanResponse;

import java.io.Serializable;

public class SendScanRes extends SendScanResponse implements Serializable {

    private static final long serialVersionUID = -1410653337122346470L;


    /**
     * 混扫任务关注流向中扫描上下单流向不一致
     * 话术（发货成功，与上一单不一致！）
     * 提示框
     */
    public static final int CODE_FOCUS_FLOW_DIFFER = 2101;
    public static final String String_FOCUS_FLOW_DIFFER = "发货成功，与上一单流向不一致！";

    /**
     * 混扫任务非关注流向扫描单据扫描提示强发
     * 话术（当前单据流向未关注，请确认是否强发！）
     * 确认框
     */
    public static final int CODE_UNFOCUSED_FLOW_FORCE_SEND = 2102;
    public static final String MSG_UNFOCUSED_FLOW_FORCE_SEND = "当前单据流向[%s]未关注，请确认是否强发！";

    private Long nextSiteCode;


    public Long getNextSiteCode() {
        return nextSiteCode;
    }

    public void setNextSiteCode(Long nextSiteCode) {
        this.nextSiteCode = nextSiteCode;
    }
}
