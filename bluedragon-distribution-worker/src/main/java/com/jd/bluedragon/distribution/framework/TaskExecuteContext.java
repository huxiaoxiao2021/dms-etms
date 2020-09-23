package com.jd.bluedragon.distribution.framework;

import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * 任务执行上下文
 * Created by wangtingwei on 2017/1/13.
 */
public abstract class TaskExecuteContext {

    /**
     * 是否通过验证
     */
    private boolean passCheck;

    /**
     * 创建站点
     */
    private BaseStaffSiteOrgDto createSite;

    /**
     * 接收站点
     */
    private BaseStaffSiteOrgDto receiveSite;

    public boolean isPassCheck() {
        return passCheck;
    }

    /**
     * 运单验货是否按拆分任务执行
     */
    private Boolean executeBySplitTask = Boolean.FALSE;

    public void setPassCheck(boolean passCheck) {
        this.passCheck = passCheck;
    }

    public BaseStaffSiteOrgDto getCreateSite() {
        return createSite;
    }

    public void setCreateSite(BaseStaffSiteOrgDto createSite) {
        this.createSite = createSite;
    }

    public BaseStaffSiteOrgDto getReceiveSite() {
        return receiveSite;
    }

    public void setReceiveSite(BaseStaffSiteOrgDto receiveSite) {
        this.receiveSite = receiveSite;
    }

    public Boolean getExecuteBySplitTask() {
        return executeBySplitTask;
    }

    public void setExecuteBySplitTask(Boolean executeBySplitTask) {
        this.executeBySplitTask = executeBySplitTask;
    }

    /**
     * 运单大包裹验货拆分执行
     *
     * @return
     */
    public Boolean executeBySplit() {
        return getExecuteBySplitTask();
    }

}
