package com.jd.bluedragon.common.dto.operation.workbench.unseal.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName SealTaskInfoRequest
 * @Description
 * @Author wyh
 * @Date 2022/3/5 10:38
 **/
public class SealTaskInfoRequest implements Serializable {

    private static final long serialVersionUID = 6900794010329512223L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 封车编码
     */
    private String sealCarCode;

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

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }
}
