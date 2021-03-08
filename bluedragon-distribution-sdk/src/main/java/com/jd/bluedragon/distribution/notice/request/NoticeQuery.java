package com.jd.bluedragon.distribution.notice.request;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

    private List<Integer> typeList;

    private Integer level;

    private Integer isDisplay;

    private Integer isTopDisplay;

    /**
     * 接收对象范围类型 0-所有人、1-仅分拣工作台，2-仅安卓PDA
     */
    private Integer receiveScopeType;

    private List<Integer> receiveScopeTypeList;

    private String createTimeStartStr;

    private String createTimeEndStr;

    private Date createTimeStart;

    private Date createTimeEnd;

    private Integer pageSize;

    private String keyword;

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

    public List<Integer> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<Integer> typeList) {
        this.typeList = typeList;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getReceiveScopeType() {
        return receiveScopeType;
    }

    public void setReceiveScopeType(Integer receiveScopeType) {
        this.receiveScopeType = receiveScopeType;
    }

    public List<Integer> getReceiveScopeTypeList() {
        return receiveScopeTypeList;
    }

    public void setReceiveScopeTypeList(List<Integer> receiveScopeTypeList) {
        this.receiveScopeTypeList = receiveScopeTypeList;
    }

    public String getCreateTimeStartStr() {
        return createTimeStartStr;
    }

    public void setCreateTimeStartStr(String createTimeStartStr) {
        this.createTimeStartStr = createTimeStartStr;
    }

    public String getCreateTimeEndStr() {
        return createTimeEndStr;
    }

    public void setCreateTimeEndStr(String createTimeEndStr) {
        this.createTimeEndStr = createTimeEndStr;
    }

    public Date getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(Date createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        this.setLimit(pageSize);
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "NoticeQuery{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", typeList=" + typeList +
                ", level=" + level +
                ", isDisplay=" + isDisplay +
                ", isTopDisplay=" + isTopDisplay +
                ", receiveScopeType=" + receiveScopeType +
                ", receiveScopeTypeList=" + receiveScopeTypeList +
                ", createTimeStartStr='" + createTimeStartStr + '\'' +
                ", createTimeEndStr='" + createTimeEndStr + '\'' +
                ", createTimeStart=" + createTimeStart +
                ", createTimeEnd=" + createTimeEnd +
                ", pageSize=" + pageSize +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}
