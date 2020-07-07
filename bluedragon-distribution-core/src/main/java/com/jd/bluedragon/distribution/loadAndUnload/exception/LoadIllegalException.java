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

    public LoadIllegalException(String message) {
        super(message);
    }
}
