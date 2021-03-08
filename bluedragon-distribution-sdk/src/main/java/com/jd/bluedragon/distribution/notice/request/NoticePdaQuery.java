package com.jd.bluedragon.distribution.notice.request;

import java.io.Serializable;

/**
 * pda端通知查询参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2020-07-01 16:01:29 周三
 */
public class NoticePdaQuery implements Serializable {

    private static final long serialVersionUID = 1236612962511867597L;

    private String userErp;

    private Long noticeId;

    private String keyword;

    private Integer pageNumber;

    private Integer pageSize;

    /**
     * 分页起始值，用于分页limit
     */
    private int limitStart;

    /**
     * 最后的一条id记录作为游标用
     */
    private Long lastId;

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    @Override
    public String toString() {
        return "NoticePdaQuery{" +
                "userErp='" + userErp + '\'' +
                ", noticeId=" + noticeId +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}
