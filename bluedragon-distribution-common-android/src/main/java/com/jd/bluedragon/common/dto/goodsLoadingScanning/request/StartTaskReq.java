package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;
import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 开始任务接口
 * @author: wuming
 * @create: 2020-10-14 16:05
 */
public class StartTaskReq implements Serializable {

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
    private List<AssistorInfoReq> assistorInfo;

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

    public List<AssistorInfoReq> getAssistorInfo() {
        return assistorInfo;
    }

    public void setAssistorInfo(List<AssistorInfoReq> assistorInfo) {
        this.assistorInfo = assistorInfo;
    }
}
