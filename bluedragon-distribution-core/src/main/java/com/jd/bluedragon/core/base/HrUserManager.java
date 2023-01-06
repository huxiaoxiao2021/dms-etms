package com.jd.bluedragon.core.base;

/**
 * 人资接口manager
 *
 * @author hujiping
 * @date 2022/12/19 2:22 PM
 */
public interface HrUserManager {

    /**
     * 获取erp获取直属上级ERP
     *
     * @param userErp
     * @return
     */
    String getSuperiorErp(String userErp);
}
