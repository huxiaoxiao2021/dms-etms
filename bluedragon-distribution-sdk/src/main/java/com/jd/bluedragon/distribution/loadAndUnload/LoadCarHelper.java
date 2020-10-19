package com.jd.bluedragon.distribution.loadAndUnload;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;


/**
 * @program: bluedragon-distribution
 * @description:装车任务协助人
 * @author: wuming
 * @create: 2020-10-15 20:35
 */
public class LoadCarHelper extends DbEntity {


    /**
     * 任务Id关联load_car表id
     */
    private Long taskId;

    /**
     * 协助人姓名
     */
    private String helperName;

    /**
     * 协助人erp
     */
    private String helperErp;

    /**
     * 创建人erp
     */
    private String createUserErp;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;


    public LoadCarHelper() {
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    public String getHelperErp() {
        return helperErp;
    }

    public void setHelperErp(String helperErp) {
        this.helperErp = helperErp;
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

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
