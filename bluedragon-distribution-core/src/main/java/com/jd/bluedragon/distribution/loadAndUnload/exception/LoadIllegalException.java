package com.jd.bluedragon.distribution.loadAndUnload.exception;

/**
 * 装卸车异常信息
 *
 * @author: hujiping
 * @date: 2020/7/2 21:26
 */
public class LoadIllegalException extends RuntimeException {

    public static final String ALLIANCE_INTERCEPT_MESSAGE = "加盟商预付款余额不足，请联系加盟商处理!";

    public static final String SEAL_NOT_SCANPACK_INTERCEPT_MESSAGE = "封车编码【%s】未扫描包裹!";

    public static final String PACK_NOTIN_SEAL_INTERCEPT_MESSAGE = "此包裹不在卸车任务内,多货扫描!";

    public static final String FORBID_BOARD_INTERCEPT_MESSAGE = "禁止非同一方向组板!";

    public static final String BOARD_RECIEVE_EMPEY_INTERCEPT_MESSAGE = "板目的地为空!";

    public static final String BOARD_CREATE_FAIL_INTERCEPT_MESSAGE = "生成板号异常!";

    public static final String BOARD_PACK_SEND_INTERCEPT_MESSAGE = "包裹【%s】已发货";

    public static final String BOARD_PACKNUM_EXCEED_INTERCEPT_MESSAGE = "板号【%s】绑定的包裹数【%s】已达上限";

    public static final String BOARD_MOVED_INTERCEPT_MESSAGE = "组板转移服务异常!";

    public static final String BOARD_TOTC_FAIL_INTERCEPT_MESSAGE = "组板失败!";

    public static final String BOARD_NOTE_EXIST_INTERCEPT_MESSAGE = "板号不存在!";

    public static final String PACKAGE_IS_SCAN_INTERCEPT_MESSAGE = "包裹【%s】已组板【%s】,请勿重复扫描!";

    public static final String UNSCAN_PACK_ISNULL_INTERCEPT_MESSAGE = "封车编码【%s】没有未扫包裹,请操作完成卸车任务!";

    public static final String NO_WEIGHT_FORBID_SEND_MESSAGE = "无重量禁止发货!";

    public static final String FREIGTH_SEND_PAY_NO_MONEY_FORBID_SEND_MESSAGE = "运费寄付无运费金额禁止发货";

    public static final String FREIGTH_ARRIVE_PAY_NO_MONEY_FORBID_SEND_MESSAGE = "运费到付无运费金额禁止发货";

    public static final String FREIGTH_TEMPORARY_PAY_NO_WEIGHT_VOLUME_FORBID_SEND_MESSAGE = "运费临时欠款无重量体积禁止发货";

    public static final String PACK_SERVICE_NO_CONFIRM_FORBID_SEND_MESSAGE = "包装服务运单未确认包装完成禁止发货";

    public static final String JIN_PENG_NO_TOGETHER_FORBID_SEND_MESSAGE = "金鹏订单未上架集齐禁止发货";

    public static final String BNET_SEND_PAY_NO_RECEIVE_FINISH_MESSAGE = "B网营业厅寄付未揽收完成禁止发货";

    public static final String BORCODE_SEALCAR_INTERCEPT_EXIST_MESSAGE = "包裹已经扫描,请勿重复扫描!";

    public static final String PACKAGE_ALREADY_BIND="此包裹已在板号【%s】内，是否确认重新组板？";
    public static final String INIT_PACKAGE_CANCEL = "商家自送运单【%s】已取消,验收失败";

    public static final String PACKAGE_NO_WEIGHT="此包裹无重量体积，请到转运工作台按包裹录入重量体积";

    public LoadIllegalException(String message) {
        super(message);
    }
}
