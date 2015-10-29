package com.jd.bluedragon.core.base;

/**
 * Created by wangtingwei on 2015/10/28.
 */
public interface PreseparateWaybillManager {

    /**
     * 获取预分拣站编号
     * @param waybillCode 运单号
     * @return 站点编号
     * @throws Exception
     */
    Integer getPreseparateSiteId(String waybillCode) throws Exception;
}
