package com.jd.bluedragon.distribution.dbs.domain;

/**
 * Created by dudong on 2016/9/19.
 */
public class ObjectId {
    private Long id;
    private String objectName;
    private Integer firstId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Integer getFirstId() {
        return firstId;
    }

    public void setFirstId(Integer firstId) {
        this.firstId = firstId;
    }
}
