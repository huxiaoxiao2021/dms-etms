package com.jd.bluedragon.distribution.dbs.service;

/**
 * Created by dudong on 2016/9/19.
 */
public interface ObjectIdService {
    public Long getFirstId(String objectName, Integer count);
    /**
     * 根据key获取下个FirstId的值
     * @param objectName
     * @return ++FirstId
     */
    public Long getNextFirstId(String objectName);
}
