package com.jd.bluedragon.core.security.dataam.manage;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

/**
 * 安全切片查询服务（安全校验）
 *
 * @author hujiping
 * @date 2022/8/12 5:09 PM
 */
public interface SecurityCheckManager {

    /**
     * 根据PIN和运单号，判断是否有访问权限
     *
     * @param userPin 用户pin
     * @param waybillNo 运单号
     * @return
     */
    InvokeResult<Boolean> verifyWaybillDetailPermissionByPin(String userPin, String waybillNo, String waybillAMToken);

    /**
     * 根据PERP和运单号，判断是否有访问权限
     *
     * @param userErp 用户ERP
     * @param waybillNo 运单号
     * @return
     */
    InvokeResult<Boolean> verifyWaybillDetailPermissionByErp(String userErp, String waybillNo, String waybillAMToken);

    /**
     * 根据PIN和订单号，判断是否有访问权限
     *
     * @param userPin 用户pin
     * @param waybillNo 运单号
     * @return
     */
    InvokeResult<Boolean> verifyOrderDetailPermissionByPin(String userPin, String waybillNo, String waybillAMToken);

    /**
     * 根据PERP和订单号，判断是否有访问权限
     *
     * @param userErp 用户ERP
     * @param waybillNo 运单号
     * @return
     */
    InvokeResult<Boolean> verifyOrderDetailPermissionByErp(String userErp, String waybillNo, String waybillAMToken);
}
