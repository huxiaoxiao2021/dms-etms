package com.jd.bluedragon.common.dto.sysConfig.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 安卓功能是否可使用条件配置 请求参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-04-11 17:38:02 周一
 */
public class MenuUsageConfigRequestDto implements Serializable {
    private static final long serialVersionUID = 7929667345809176046L;

    private String menuCode;

    private User user;

    private CurrentOperate currentOperate;

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }
}
