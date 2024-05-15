package com.jd.bluedragon.common.dto.jyexpection.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperatorData;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * 质控H5页面回调接口请求参数
 */
public class AbnormalCallbackRequest implements Serializable {

    private static final long serialVersionUID = 506260885375303045L;

    /**
     * 操作信息
     */
    private CurrentOperate currentOperate;

    /**
     * 用户信息
     */
    private User user;

    /**
     * 操作数据对象
     */
    private OperatorData operatorData;

    /**
     * 包裹号或运单号列表
     */
    private List<String> barCodeList;

    /**
     * 业务主键
     * 与simple_wp_abnormal_record主题消息体中的id字段一一对应，每次请求都是唯一的
     */
    private String businessId;

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<String> barCodeList) {
        this.barCodeList = barCodeList;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public OperatorData getOperatorData() {
        return operatorData;
    }

    public void setOperatorData(OperatorData operatorData) {
        this.operatorData = operatorData;
    }
}
