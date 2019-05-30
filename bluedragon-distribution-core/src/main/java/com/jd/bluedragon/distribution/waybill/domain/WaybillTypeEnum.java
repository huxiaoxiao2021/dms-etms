package com.jd.bluedragon.distribution.waybill.domain;

/**
 * <P>
 *     订单类型枚举
 *     https://cf.jd.com/pages/viewpage.action?pageId=6489474
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/8
 */

public enum WaybillTypeEnum {

//    BULK_ORDER(-1,"大宗订单"),
    GENERAL_ORDER(0,"一般订单"),
    POINT_ORDER(1,"积分订单"),
    AUCTION_ORDER(2,"拍卖订单"),
    PURCHASE_ORDER(3,"代购订单"),
    VIRTUAL_MERCHANDISE(4,"虚拟商品"),
    BUY_ORDERS(5,"抢购订单"),
    INSTALL_ORDER(6,"分期订单"),
    SALES_RETURN(7,"内部订单(采销退货)"),
    SERVICE_PRODUCT(8,"服务产品"),
    SPARE_PARTS_LIBRARY_ADMINISTRATION(9,"备件库-行政"),
    SPARE_PARTS_LIBRARY_SALES(10,"备件库-销售"),
    AFTER_SALES_DELIVERY(11,"售后调货"),
    BIG_HOUSEHOLD_APPLIANCES_TO_COUNTRYSIDE(12,"大家电下乡"),
    ORDER_OF_THE_SALES_DEPARTMENT(13,"企销部订单"),
    PAIPAI_ORDER(14,"拍拍订单"),
    RETURN_SHIPMENT(15,"返修发货"),
    DIRECT_COMPENSATION(16,"直接赔偿"),
    CHINA_MERCHANTS_BANK_ONLINE_STAGING(17,"招行在线分期"),
    DIRECT_DELIVERY_FROM_THE_MANUFACTURER(18,"厂商直送"),
    CUSTOMER_SERVICE_PATCH(19,"客服补件"),
    TRADE_IN(20,"以旧换新"),
    POP_FBP(21,"POPFBP"),
    POP_SOP(22,"POPSOP"),
    POP_LBP(23,"POPLBP"),
    POP_LBV(24,"POP_LBV"),
    POP_SOPL(25,"POP_SOPL"),
    GROUP_PURCHASE_VIRTUAL(28,"团购(虚拟)"),
    MOBILE_PHONE_RECHARGE(30,"手机充值"),
    CAMPUS_ORDER(31,"校园订单"),
    IPHONE_CONTRACT(32,"IPHONE合约"),
    ELECTRONIC_GIFT_CARD(33,"电子礼品卡"),
    GAME_CARD(34,"游戏点卡"),
    AIR_TICKETS(35,"机票"),
    LOTTERY_TICKET(36,"彩票"),
    NEW_MOBILE_PHONE_RECHARGE(37,"手机充值(新)"),
    E_BOOK_ORDER(38,"电子书订单"),
    OVERSEAS_ORDERS(41,"海外订单"),
    GENERAL_CONTRACT_PLAN(42,"通用合约计划"),
    MOVIE_TICKET(43,"电影票"),
    ATTRACTION_TICKETS(44,"景点门票"),
    CAR_RENTAL(45,"租车"),
    TRAIN_TICKET(46,"火车票"),
    TOURISM(47,"旅游"),
    INSURANCE(48,"保险"),
    PHYSICAL_GIFT_CARD(49,"实物礼品卡"),
    INCORRECT_PURCHASE_ORDER(51,"误购取件费订单"),
    DONATION_ORDER(52,"捐赠订单"),
    TICKET_ORDER(53,"票务订单"),
    HOTEL_GROUP_PURCHASE(201,"酒店团购"),
    PICK_UP(1001,"上门取件"),
    RENEWAL(1002,"上门换新取件"),
    AFTER_SALES_INVOICE(1003,"售后发货单"),
    DELIVERY_TO_MAKE_UP_THE_PICK_UP_LIST(1004,"配送补下上门取件单"),
    LARGE_HOME_APPLIANCES_PICK_UP(1005,"大家电上门取件"),
    LARGE_HOUSEHOLD_APPLIANCES_FOR_NEW_PICKUPS(1006,"大家电上门换新取件"),
    B_MERCHANT_WAYBILL(10000,"B商家运单"),
    LUXURY(127,"奢侈品");


    private Integer code;

    private String name;

    WaybillTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        for (WaybillTypeEnum waybillTypeEnum : WaybillTypeEnum.values()) {
            if (waybillTypeEnum.getCode().equals(code)) {
                return waybillTypeEnum.getName();
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
