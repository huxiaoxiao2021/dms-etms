package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author liwenji
 * @description 功能可用配置白名单
 * @date 2023-08-30 14:28
 */

public class FuncUsageWhiteListDto implements Serializable {

    private static final long serialVersionUID = 5558432835220167498L;
    
    private List<String> userList;

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
