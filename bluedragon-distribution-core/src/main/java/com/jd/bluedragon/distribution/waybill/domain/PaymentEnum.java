package com.jd.bluedragon.distribution.waybill.domain;

/**
 * <P>
 *     支付方式枚举
 *     https://cf.jd.com/pages/viewpage.action?pageId=2722918
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/8
 */

public enum PaymentEnum {

    CASH_ON_DELIVERY(1,"货到付款"),
    POST_OFFICE_REMITTANCE(2,"邮局汇款"),
    SELF_LIFTING(3,"自提"),
    ONLINE_PAYMENTS(4,"在线支付"),
    COMPANY_TRANSFER(5,"公司转帐"),
    BANK_CARD_TRANSFER(6,"银行卡转帐"),
    STAGING_CHINA_MERCHANTS_BANK(7,"分期-招行"),
    INSTALLMENT(8,"分期付款"),
    COLLEGE_AGENT_PAY_BY_YOURSELF(10,"高校代理-自己支付"),
    COLLEGE_AGENT_AGENT_ADVANCE(11,"高校代理-代理垫付"),
    MONTHLY(12,"月结"),
    UNIVERSITY_AGENT_CASH_ON_DELIVERY(13,"高校代理－货到付款"),
    PAYPAL(14,"PAYPAL"),
    SUBWAY_SELF_LIFTING(15,"地铁自提"),
    CAMPUS_SELF_RAISING(16,"校园自提"),
    GOOD_NEIGHBORS(17,"好邻居自提"),
    COMMUNITY_SELF_RAISING(18,"社区自提"),
    SELF_CONTAINED_CABINET(19,"自提柜"),
    MIXED_PAYMENT(99,"混合支付"),
    UNIONPAY_MOBILE_PAYMENT(165,"银联手机支付");

    /**
     * 编号值
     */
    private Integer code;

    /**
     * 名称
     */
    private String name;

    PaymentEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 通过code获取枚举汉字
     * @param code
     * @return
     */
    public static String getNameByCode(Integer code) {
        for (PaymentEnum payment : PaymentEnum.values()) {
            if (payment.getCode().equals(code)){
                return payment.getName();
            }
        }
        return "未知类型";
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
