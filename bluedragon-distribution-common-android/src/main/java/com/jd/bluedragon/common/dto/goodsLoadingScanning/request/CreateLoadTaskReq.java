package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import com.jd.bluedragon.common.dto.unloadCar.HelperDto;

import java.io.Serializable;
import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 开始任务请求参数
 * @author: wuming
 * @create: 2020-10-14 15:35
 */
public class CreateLoadTaskReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务Id
     */
    private Long id;

    /**
     * 创建人erp
     */
    private String createUserErp;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 协助人信息
     */
    private List<HelperDto> assistorInfo;

    public CreateLoadTaskReq() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public List<HelperDto> getAssistorInfo() {
        return assistorInfo;
    }

    public void setAssistorInfo(List<HelperDto> assistorInfo) {
        this.assistorInfo = assistorInfo;
    }
}
