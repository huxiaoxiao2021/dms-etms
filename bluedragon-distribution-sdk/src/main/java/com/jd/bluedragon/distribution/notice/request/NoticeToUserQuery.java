package com.jd.bluedragon.distribution.notice.request;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.List;

/**
 * 通知与用户操作关系
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2020-07-01 16:01:29 周三
 */
public class NoticeToUserQuery extends BasePagerCondition implements Serializable {

    private static final long serialVersionUID = 3131894485364784105L;

    /**
     * 通知ID
     */
    private Long noticeId;

    /**
     * 用户EPP
     */
    private String userErp;

    /**
     * 是否已读
     */
    private Integer isRead;

    private Integer yn;

    /**
     * 通知ID列表
     */
    private List<Long> noticeIdList;

    /**
     * 通知范围
     */
    private List<Integer> receiveScopeTypeList;

    private Integer pageSize;

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public List<Long> getNoticeIdList() {
        return noticeIdList;
    }

    public void setNoticeIdList(List<Long> noticeIdList) {
        this.noticeIdList = noticeIdList;
    }

    public List<Integer> getReceiveScopeTypeList() {
        return receiveScopeTypeList;
    }

    public void setReceiveScopeTypeList(List<Integer> receiveScopeTypeList) {
        this.receiveScopeTypeList = receiveScopeTypeList;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        this.setLimit(pageSize);
    }

    @Override
    public String toString() {
        return "NoticeToUserQuery{" +
                "noticeId=" + noticeId +
                ", userErp='" + userErp + '\'' +
                ", isRead=" + isRead +
                ", yn=" + yn +
                ", noticeIdList=" + noticeIdList +
                ", receiveScopeTypeList=" + receiveScopeTypeList +
                ", pageSize=" + pageSize +
                '}';
    }
}
