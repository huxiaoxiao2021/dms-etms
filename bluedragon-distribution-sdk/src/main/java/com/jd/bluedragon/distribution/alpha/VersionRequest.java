package com.jd.bluedragon.distribution.alpha;

import java.io.Serializable;

/**
 * Created by wuzuxiang on 2016/8/26.
 */
public class VersionRequest implements Serializable{

    private static final long serialVersionUID = 5799267676878153722L;

    /**
     * 版本编号
     */
    private String versionId;

    /**
     * 版本状态
     */
    private String state;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
