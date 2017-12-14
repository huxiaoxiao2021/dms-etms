package com.jd.bluedragon.distribution.dbs.service;

/**
 * Created by dudong on 2016/9/19.
 */
public interface ObjectIdService {
    public Long getFirstId(String objectName, Integer count);
    /**
     * 根据key获取下个Id值
     * @param objectName
     * @return
     */
    public Long getNextId(String objectName);
}
