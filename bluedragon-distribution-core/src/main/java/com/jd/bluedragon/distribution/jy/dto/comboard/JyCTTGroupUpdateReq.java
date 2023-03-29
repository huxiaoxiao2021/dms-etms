package com.jd.bluedragon.distribution.jy.dto.comboard;

import java.util.Date;
import java.util.List;

/**
 * @author liwenji
 * @date 2022-11-18 14:31
 */
public class JyCTTGroupUpdateReq {

    private String updateUserErp;

    private String updateUserName;

    private Date updateTime;
    
    private List<Long> ids;

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

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
