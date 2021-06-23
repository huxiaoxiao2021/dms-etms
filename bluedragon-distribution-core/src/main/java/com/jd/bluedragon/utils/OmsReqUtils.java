package com.jd.bluedragon.utils;

import cn.jdl.batrix.spec.RequestProfile;

import java.util.Locale;

/**
 * @ClassName OmsReqUtils
 * @Description
 * @Author wyh
 * @Date 2021/3/26 11:02
 **/
public class OmsReqUtils {

    /**
     * 默认国别 zh_CN
     */
    public static final String DEFAULT_LOCALE_CN = Locale.CHINA.toString();

    /**
     * 京东物流租户ID
     */
    public static final String JDL_TENANT_ID = "1000";

    /**
     * 默认时区
     */
    public static final String DEFAULT_TIMEZONE = "GMT+8";

    public static final String SYSTEM_CALLER = "SortingSystem";


    /**
     * 发起人标识
     */
    public static final int INITIATOR_TYPE_4 = 4; //寄件人

    /**
     * 对订单的集合字段标记操作类型
     */
    public static final String MODIFY_FILEDS_TYPE_ADD = "1"; // 集合增量更新
    public static final String MODIFY_FILEDS_TYPE_OVERRIDE = "2"; // 集合全量覆盖
    public static final String MODIFY_FILEDS_TYPE_DEL = "3"; // 集合全量删除

    /**
     * 操作类型，修改场景使用
     */
    public static final String OPERATE_TYPE_ADD = "1"; // 新增
    public static final String OPERATE_TYPE_MODIFY = "2"; // 修改
    public static final String OPERATE_TYPE_DEL = "3"; // 删除


    /**
     * 生成OMS接口认证
     * @return
     */
    public static RequestProfile genProfile() {
        RequestProfile profile = new RequestProfile();
        profile.setTimeZone(DEFAULT_TIMEZONE);
        profile.setLocale(DEFAULT_LOCALE_CN);
        profile.setTenantId(JDL_TENANT_ID);
        return profile;
    }
}

