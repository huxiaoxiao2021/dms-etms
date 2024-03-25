package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;


import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanResponse;

import java.io.Serializable;
import java.util.List;

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
     * 强发逻辑
     * 两种场景： （1）有流向提示，（2）无流向提示
     */
    public static final int FORCE_SEND_CODE = 3001;
    public static final String FORCE_SEND_MSG_EXIST_FLOW = "此单流向[%s]不在混扫任务，请选择流向";
    public static final String FORCE_SEND_MSG_NULL_FLOW = "此单没有路由，请选择流向";

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
    /**
     * 需要强发校验的标识
     */
    private Boolean checkForceSendFlag;
    /**
     * 需要校验强发时的话术
     * （1） 无流向强发
     * （2）有流向不匹配强发
     */
    private String checkForceSendMsg;
    /**
     * 当前已添加的混扫任务流向
     */
    private List<MixScanTaskDetailDto> mixScanTaskDetailDtoList;
    /**
     * 流向不匹配
     */
    private Boolean nextSiteMismatchingFlag;


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

    public Boolean getCheckForceSendFlag() {
        return checkForceSendFlag;
    }

    public void setCheckForceSendFlag(Boolean checkForceSendFlag) {
        this.checkForceSendFlag = checkForceSendFlag;
    }

    public String getCheckForceSendMsg() {
        return checkForceSendMsg;
    }

    public void setCheckForceSendMsg(String checkForceSendMsg) {
        this.checkForceSendMsg = checkForceSendMsg;
    }

    public List<MixScanTaskDetailDto> getMixScanTaskDetailDtoList() {
        return mixScanTaskDetailDtoList;
    }

    public void setMixScanTaskDetailDtoList(List<MixScanTaskDetailDto> mixScanTaskDetailDtoList) {
        this.mixScanTaskDetailDtoList = mixScanTaskDetailDtoList;
    }

    public Boolean getNextSiteMismatchingFlag() {
        return nextSiteMismatchingFlag;
    }

    public void setNextSiteMismatchingFlag(Boolean nextSiteMismatchingFlag) {
        this.nextSiteMismatchingFlag = nextSiteMismatchingFlag;
    }
}
