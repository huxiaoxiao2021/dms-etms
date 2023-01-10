package com.jd.bluedragon.distribution.jy.dto.comboard;

import com.jd.bluedragon.common.dto.board.BizSourceEnum;

import java.util.List;

/**
 * @author liwenji
 * @date 2022-12-13 12:09
 */
public class CancelComboardSendTaskDto {

    /**
     * 操作人编码
     */
    private int userCode;

    /**
     * 操作人姓名
     */
    private String userName;

    /**
     * 操作人ERP
     */
    private String userErp;

    /**
     *操作单位编号
     */
    private Integer siteCode;

    /**
     *操作单位名称
     */
    private String siteName;

    /**
     * 目的地站点名称
     */
    private String endSiteName;

    /**
     * 板号
     */
    private String boardCode;
    /**
     * 运单号
     */
    private List<String> barCodeList;

    private BizSourceEnum bizSource;

    public BizSourceEnum getBizSource() {
        return bizSource;
    }

    public void setBizSource(BizSourceEnum bizSource) {
        this.bizSource = bizSource;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
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
}
