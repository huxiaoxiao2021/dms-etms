package com.jd.bluedragon.core.base;

/**
 * 众邮相关接口
 */
public interface BoxOperateApiManager {

    /**
     * 判断箱是否空箱
     * @param boxCode
     * @return
     */
    public boolean findBoxIsEmpty(String boxCode);
}
