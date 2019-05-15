package com.jd.bluedragon.distribution.api.request;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName NoticeRequest
 * @date 2019/4/17
 */
public class NoticeRequest {

    /**
     * 主题
     */
    private String theme;

    /**
     * 级别 普通、重要、严重
     */
    private Integer level;

    /**
     * 类型：通知、说明、警告、处罚等
     */
    private Integer type;

    /**
     * 是否置顶显示
     */
    private Integer isTopDisplay;

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsTopDisplay() {
        return isTopDisplay;
    }

    public void setIsTopDisplay(Integer isTopDisplay) {
        this.isTopDisplay = isTopDisplay;
    }
}
