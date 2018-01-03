package com.jd.bluedragon.distribution.transport.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 * @author wuyoude
 * @ClassName: ArSendCode
 * @Description: 发货批次表-实体类
 * @date 2017年12月28日 09:46:12
 */
public class ArSendCode extends DbEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 发货登记ID
     */
    private Long sendRegisterId;

    /**
     * 发货批次
     */
    private String sendCode;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 修改用户
     */
    private String updateUser;

    public Long getSendRegisterId() {
        return sendRegisterId;
    }

    public void setSendRegisterId(Long sendRegisterId) {
        this.sendRegisterId = sendRegisterId;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

}
