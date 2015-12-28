package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

/**
 * Created by wangtingwei on 2015/12/23.
 */
public interface ComposeService {

    Integer PREPARE_SITE_CODE_NOTHING = 0;
    String PREPARE_SITE_NAME_NOTHING = "未定位门店";

    Integer PREPARE_SITE_CODE_OVER_LINE = -100;
    String PREPARE_SITE_NAME_OVER_LINE = "超区分界线";

    Integer PREPARE_SITE_CODE_OVER_AREA = -2;
    String PREPARE_SITE_NAME_OVER_AREA = "超区";

    Integer PREPARE_SITE_CODE_EMS_DIRECT = 999999999;
    String PREPARE_SITE_NAME_EMS_DIRECT = "EMS全国直发";

    String SPECIAL_MARK_AIRTRANSPORT ="空";
    String SPECIAL_MARK_ARAYACAK_CABINET = "柜";
    String SPECIAL_MARK_LOCAL_SCHEDULE = "调";
    String SPECIAL_MARK_ARAYACAK_SITE = "提";
    String SPECIAL_MARK_CROWD_SOURCING = "众";

    int AIR_TRANSPORT = 1;
    int ARAYACAK_CABINET = 1;
    int LOCAL_SCHEDULE = 1;

    static String ONLINE_PAYMENT="在线支付";
    static int ONLINE_PAYMENT_SIGN = 4;

    //自提点标示
    static int ARAYACAK_SIGN = 64;


    /**
     * 合成器
     * @param waybill           运单信息
     * @param dmsCode           始发分拣中心ID
     * @param targetSiteCode    目的站点ID
     */
    void handle(final PrintWaybill waybill,Integer dmsCode,Integer targetSiteCode);
}
