package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;

/**
 * Created by yanghongqiang on 2015/11/30.
 */
public interface LabelPrintingService {

    Integer PREPARE_SITE_CODE_NOTHING = 0;
    String PREPARE_SITE_NAME_NOTHING = "未定位门店";

    Integer PREPARE_SITE_CODE_OVER_LINE = -100;
    String PREPARE_SITE_NAME_OVER_LINE = "超区分界线";

    Integer PREPARE_SITE_CODE_OVER_AREA = -2;
    String PREPARE_SITE_NAME_OVER_AREA = "超区";

    Integer PREPARE_SITE_CODE_EMS_DIRECT = 999999999;
    String PREPARE_SITE_NAME_EMS_DIRECT = "EMS全国直发";

    String SPECIAL_MARK_AIRTRANSPORT ="航";
    String SPECIAL_MARK_SENIOR ="尊";
    String SPECIAL_MARK_ARAYACAK_CABINET = "柜";
    String SPECIAL_MARK_LOCAL_SCHEDULE = "调";
    String SPECIAL_MARK_ARAYACAK_SITE = "提";
    String SPECIAL_MARK_CROWD_SOURCING = "众";

    /**
     * 全球售合包订单标识
     * 当sendpay第108位为1或2或3且senpay第124位为4时，
     */
    String SPECIAL_MARK_SOLD_INTO_PACKAGE = "合";

    Integer AIR_TRANSPORT = 1;
    Integer ARAYACAK_CABINET = 1;
    Integer LOCAL_SCHEDULE = 1;

    static String ONLINE_PAYMENT="在线支付";
    static int ONLINE_PAYMENT_SIGN = 4;

    //自提点标示
    static int ARAYACAK_SIGN = 64;

    String SPECIAL_MARK1_WAREHOUSE_JD = "京仓";
    String SPECIAL_MARK1_WAREHOUSE_NOT_JD = "非京仓";

    /**
     * 包裹标签打印核心方法
     * @return
     */
    BaseResponseIncidental<LabelPrintingResponse> packageLabelPrint(LabelPrintingRequest request);

    /**
     * 打印主要方法，有二次预分拣逻辑（旧的保留不变）
     * @param request
     * @return
     */
    public BaseResponseIncidental<LabelPrintingResponse> packageLabelPrint(LabelPrintingRequest request, WaybillPrintContext context);

}
