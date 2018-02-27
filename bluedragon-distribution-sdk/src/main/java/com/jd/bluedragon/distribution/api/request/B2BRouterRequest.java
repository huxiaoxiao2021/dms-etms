package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * Created by xumei3 on 2018/2/26.
 */
public class B2BRouterRequest implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer originalB2BSiteId;
    private String  originalB2BSiteName;

    private Integer destinalB2BSiteId;
    private String  destinalB2BSiteName;

    private Integer startIndex;
    private Integer endIndex;

    public Integer getDestinalB2BSiteId() {
        return destinalB2BSiteId;
    }

    public void setDestinalB2BSiteId(Integer destinalB2BSiteId) {
        this.destinalB2BSiteId = destinalB2BSiteId;
    }

    public String getDestinalB2BSiteName() {
        return destinalB2BSiteName;
    }

    public void setDestinalB2BSiteName(String destinalB2BSiteName) {
        this.destinalB2BSiteName = destinalB2BSiteName;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public Integer getOriginalB2BSiteId() {
        return originalB2BSiteId;
    }

    public void setOriginalB2BSiteId(Integer originalB2BSiteId) {
        this.originalB2BSiteId = originalB2BSiteId;
    }

    public String getOriginalB2BSiteName() {
        return originalB2BSiteName;
    }

    public void setOriginalB2BSiteName(String originalB2BSiteName) {
        this.originalB2BSiteName = originalB2BSiteName;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }
}
