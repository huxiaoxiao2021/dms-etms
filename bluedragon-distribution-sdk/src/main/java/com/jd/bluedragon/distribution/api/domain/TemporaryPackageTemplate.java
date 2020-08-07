package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;
import java.util.List;

public class TemporaryPackageTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 模板名称
     */
    private String temporaryTemplateName;
    /**
     * 模板版本号
     */
    private Integer temporaryTemplateVersion;
    /**
     * 所有环境开启
     */
    private Boolean allRunningModeEnable = Boolean.TRUE;

    private List<Integer> siteCodeList;
    /**
     * 环境列表
     */
    private List<String> runningModeList;
    
    public String getTemporaryTemplateName() {
        return temporaryTemplateName;
    }

    public void setTemporaryTemplateName(String temporaryTemplateName) {
        this.temporaryTemplateName = temporaryTemplateName;
    }

    public List<Integer> getSiteCodeList() {
        return siteCodeList;
    }

    public void setSiteCodeList(List<Integer> siteCodeList) {
        this.siteCodeList = siteCodeList;
    }

	/**
	 * @return the temporaryTemplateVersion
	 */
	public Integer getTemporaryTemplateVersion() {
		return temporaryTemplateVersion;
	}

	/**
	 * @param temporaryTemplateVersion the temporaryTemplateVersion to set
	 */
	public void setTemporaryTemplateVersion(Integer temporaryTemplateVersion) {
		this.temporaryTemplateVersion = temporaryTemplateVersion;
	}

	public List<String> getRunningModeList() {
		return runningModeList;
	}

	public void setRunningModeList(List<String> runningModeList) {
		this.runningModeList = runningModeList;
	}

	public Boolean getAllRunningModeEnable() {
		return allRunningModeEnable;
	}

	public void setAllRunningModeEnable(Boolean allRunningModeEnable) {
		this.allRunningModeEnable = allRunningModeEnable;
	}
}
