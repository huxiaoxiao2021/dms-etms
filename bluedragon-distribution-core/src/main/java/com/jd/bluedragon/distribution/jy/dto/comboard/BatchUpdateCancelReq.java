package com.jd.bluedragon.distribution.jy.dto.comboard;

import java.util.List;

/**
 * @author liwenji
 * @date 2022-11-26 12:32
 */
public class BatchUpdateCancelReq {

    /**
     * 更新人姓名
     */
    private String updateUserErp;

    /**
     * 更新人姓名
     */
    private String updateUserName;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 运单号|包裹号|箱号集合
     */
    private List<String> barCodeList;

    /**
     * 取消标识
     */
    private Boolean cancelFlag;

    /**
     * 场地ID
     */
    private Long startSiteId;
    
    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public List<String> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<String> barCodeList) {
        this.barCodeList = barCodeList;
    }

    public Boolean getCancelFlag() {
        return cancelFlag;
    }

    public void setCancelFlag(Boolean cancelFlag) {
        this.cancelFlag = cancelFlag;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }
}
