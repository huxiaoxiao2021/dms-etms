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
     * 混扫任务非关注流向扫描单据扫描提示强发，服务端控制强发任务
     * 话术（当前单据流向未关注，请确认是否强发！）
     * 确认框
     */
    public static final int CODE_UNFOCUSED_FLOW_FORCE_SEND = 2102;
    public static final String MSG_UNFOCUSED_FLOW_FORCE_SEND = "当前单据流向[%s]未关注，请确认是否强发！";

    /**
     * 未匹配到混扫任务中的流向信息，PDA控制强发任务
     */
    public static final int CODE_NULL_FLOW_FORCE_SEND = 2103;
    public static final String MSG_NULL_FLOW_FORCE_SEND = "当前单据流向[%s]，未匹配到混扫任务信息，请确认是否强发！";

    /**
     * 异常提示框
     * 提示框
     */
    public static final int DEFAULT_FAIL = 4100;

    private Long nextSiteCode;

    /**
     * 强发任务信息
     */
    private String unfocusedBizId;
    private String unfocusedDetailBizId;
    private Long unfocusedNextSiteCode;
    private String unfocusedNextSiteName;



    public Long getNextSiteCode() {
        return nextSiteCode;
    }

    public void setNextSiteCode(Long nextSiteCode) {
        this.nextSiteCode = nextSiteCode;
    }

    public String getUnfocusedDetailBizId() {
        return unfocusedDetailBizId;
    }

    public void setUnfocusedDetailBizId(String unfocusedDetailBizId) {
        this.unfocusedDetailBizId = unfocusedDetailBizId;
    }

    public Long getUnfocusedNextSiteCode() {
        return unfocusedNextSiteCode;
    }

    public void setUnfocusedNextSiteCode(Long unfocusedNextSiteCode) {
        this.unfocusedNextSiteCode = unfocusedNextSiteCode;
    }

    public String getUnfocusedNextSiteName() {
        return unfocusedNextSiteName;
    }

    public void setUnfocusedNextSiteName(String unfocusedNextSiteName) {
        this.unfocusedNextSiteName = unfocusedNextSiteName;
    }

    public String getUnfocusedBizId() {
        return unfocusedBizId;
    }

    public void setUnfocusedBizId(String unfocusedBizId) {
        this.unfocusedBizId = unfocusedBizId;
    }
}
