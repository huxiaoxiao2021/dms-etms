package com.jd.bluedragon.distribution.notice.request;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;

/**
 * 通知栏查询参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @date 2020-07-01 16:01:29 周三
 */
public class NoticeQuery extends BasePagerCondition implements Serializable {

    private static final long serialVersionUID = -5967555858860854610L;

    private Long id;

    private String title;

    private Integer type;

    private Integer level;

    private Integer isDisplay;

    private Integer isTopDisplay;

    private String timeStartStr;

    private String timeEndStr;

    private Integer pageSize;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getIsDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(Integer isDisplay) {
        this.isDisplay = isDisplay;
    }

    public Integer getIsTopDisplay() {
        return isTopDisplay;
    }

    public void setIsTopDisplay(Integer isTopDisplay) {
        this.isTopDisplay = isTopDisplay;
    }

    public String getTimeStartStr() {
        return timeStartStr;
    }

    public void setTimeStartStr(String timeStartStr) {
        this.timeStartStr = timeStartStr;
    }

    public String getTimeEndStr() {
        return timeEndStr;
    }

    public void setTimeEndStr(String timeEndStr) {
        this.timeEndStr = timeEndStr;
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
        return "NoticeQuery{" +
                "title='" + title + '\'' +
                ", type=" + type +
                ", isDisplay=" + isDisplay +
                ", isTopDisplay=" + isTopDisplay +
                ", timeStartStr='" + timeStartStr + '\'' +
                ", timeEndStr='" + timeEndStr + '\'' +
                '}';
    }
}
