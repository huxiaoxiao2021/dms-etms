package com.jd.bluedragon.core.base;

/**
 * 外单
 * 修改运单信息接口
 * @author : xumigen
 * @date : 2019/8/12
 */
public interface LdopWaybillUpdateManager {

    /**
     * 调用外单接口 取消运单鸡毛信服务
     * @param waybillCode 运单号
     */
    boolean cancelFeatherLetterByWaybillCode(String waybillCode);

}
