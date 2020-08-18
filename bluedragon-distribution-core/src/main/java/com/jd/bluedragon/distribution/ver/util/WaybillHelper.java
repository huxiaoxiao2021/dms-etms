package com.jd.bluedragon.distribution.ver.util;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.constant.WaybillCodePattern;
import com.jd.etms.waybill.util.UniformValidateUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public class WaybillHelper {

    private static final char identifier = '1';
    private static final char identifier_2 = '2';

    //运单地址修改标识 自营
    private static final char WAYBILL_SIGN_7_1 = '1';
    private static final char WAYBILL_SIGN_7_2 = '2';
    //运单地址修改标识 POP
    private static final char WAYBILL_SIGN_7_3 = '3';
    //纯配外单标识
    private static final char WAYBILL_SIGN_53_2 = '2';
    //受信任商家
    private static final char WAYBILL_SIGN_56_1 = '1';

    private static final char identifier_6 = '6';
    private static final char identifier_7 = '7';
    private static final char identifier_146 = '1';

    private static final Integer POP_ORGID = 543;
    private static final Integer[] BOOK_ORGID = new Integer[]{321, 613, 620, 621, 622, 624, 626};

    public static Boolean isLuxury(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }

        if (WaybillHelper.identifier == waybill.getSendPay().charAt(19)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isSubway(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }

        if (WaybillHelper.identifier == waybill.getSendPay().charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean hasTransferStation(Waybill waybill) {
        if (waybill == null || waybill.getTransferStationId() == null) {
            return Boolean.FALSE;
        }

        if (waybill.getTransferStationId().intValue() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean is211(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }

        if (WaybillHelper.identifier == waybill.getSendPay().charAt(0)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isReplacement(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }

        if (WaybillHelper.identifier == waybill.getSendPay().charAt(18)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isDoorReplacement(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }

        if (WaybillHelper.identifier_2 == waybill.getSendPay().charAt(17)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isCOD(Waybill waybill) {
        if (waybill == null || waybill.getPaymentType() == null) {
            return Boolean.FALSE;
        }

        if (1 == waybill.getPaymentType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isPickup(Waybill waybill) {
        if (waybill == null) {
            return Boolean.FALSE;
        }

        if ((3 == waybill.getPaymentType().intValue()
                || (waybill.getDistributeType() != null
                && '2' != waybill.getSendPay().charAt(21)))
                && 64 == waybill.getDistributeType().intValue()
                && !isHi24(waybill) && !isSheQu(waybill)
                && !isZiTiGui(waybill) && !isHeZuoDaiShou(waybill) && !isBianMinZiTi(waybill)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    //是不是生鲜订单
    public static Boolean isShengXian(Waybill waybill) {
        if (waybill == null) {
            return Boolean.FALSE;
        }
        //根据温层判断生鲜订单，自营订单根据sendpay第二位=3、4、5、6、7、8、9，外单根据waybill_sign第10位=2、5、6、7、8、9来判断。
        if ('3' == waybill.getSendPay().charAt(1) ||    //产地直采
                '4' == waybill.getSendPay().charAt(1) || //常温
                '5' == waybill.getSendPay().charAt(1) || //鲜活
                '6' == waybill.getSendPay().charAt(1) || //控温(10~16度)
                '7' == waybill.getSendPay().charAt(1) || //冷藏(0~-8度)
                '8' == waybill.getSendPay().charAt(1) || //冷冻(-12度以下)
                '9' == waybill.getSendPay().charAt(1)) {//深冷(-30度以下)
            return Boolean.TRUE;
        }

        if (StringHelper.isNotEmpty(waybill.getWaybillSign())) {
            if ('2' == waybill.getWaybillSign().charAt(9) ||//常温
                    '5' == waybill.getWaybillSign().charAt(9) ||//鲜活
                    '6' == waybill.getWaybillSign().charAt(9) ||//控温(10~16度)
                    '7' == waybill.getWaybillSign().charAt(9) ||//冷藏(0~-8度)
                    '8' == waybill.getWaybillSign().charAt(9) ||//冷冻(-12度以下)
                    '9' == waybill.getWaybillSign().charAt(9)) {//深冷(-30度以下)
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    public static Boolean isContract(Waybill waybill, String ruleOrderType) {
        if (waybill == null || waybill.getType() == null || ruleOrderType == null) {
            return Boolean.FALSE;
        }

        String[] ruleOrderTypeArray = ruleOrderType.split(Constants.SEPARATOR_COMMA);
        Arrays.sort(ruleOrderTypeArray);
        if (-1 < Arrays.binarySearch(ruleOrderTypeArray, String.valueOf(waybill.getType()))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isHi24(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }

        if ('3' == waybill.getSendPay().charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断社区类型
     *
     * @param waybill
     * @return
     */
    public static Boolean isSheQu(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }

        if ('4' == waybill.getSendPay().charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断自提柜类型
     *
     * @param waybill
     * @return
     */
    public static Boolean isZiTiGui(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }

        if ('5' == waybill.getSendPay().charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否POP订单
     *
     * @param waybill
     * @return
     */
    public static Boolean isPopByOrgID(Waybill waybill) {
        if (waybill.getOrgId() != null && waybill.getOrgId().equals(POP_ORGID)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否图书订单
     *
     * @param waybill
     * @return
     */
    public static Boolean isBookByOrgID(Waybill waybill) {
        if (waybill.getOrgId() != null && Arrays.binarySearch(BOOK_ORGID, waybill.getOrgId()) >= 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否修改订单地址
     *
     * @param waybill
     * @return
     */
    public static Boolean isChageWaybillSign(Waybill waybill) {

        if (waybill == null || waybill.getWaybillSign() == null) {
            return Boolean.FALSE;
        }

        if (WaybillHelper.WAYBILL_SIGN_7_1 == waybill.getWaybillSign().charAt(7)) {
            return Boolean.TRUE;
        }

        if (WaybillHelper.WAYBILL_SIGN_7_2 == waybill.getWaybillSign().charAt(7)) {
            return Boolean.TRUE;
        }

        if (WaybillHelper.WAYBILL_SIGN_7_3 == waybill.getWaybillSign().charAt(7)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 合作自提柜 【sendpay 第22位等于6(合作自提柜 )】
     *
     * @param waybill
     * @return
     */
    public static Boolean isBianMinZiTi(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }
        if (WaybillHelper.identifier_6 == waybill.getSendPay().charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 合作代收点 【sendpay 7的订单(合作代收点)】
     *
     * @param waybill
     * @return
     */
    public static Boolean isHeZuoDaiShou(Waybill waybill) {
        if (waybill == null || waybill.getSendPay() == null) {
            return Boolean.FALSE;
        }
        if (WaybillHelper.identifier_7 == waybill.getSendPay().charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private static final int AIR_SIGN_INDEX = 136;
    private static final char AIR_SIGN_VALUE = '1';

    /**
     * 判断运单是否为航空运单
     *
     * @param waybill 运单对象
     * @return 航空运单：true;非航空运单：false;
     */
    public static boolean isAirWaybill(Waybill waybill) {
        return (StringUtils.isNotBlank(waybill.getSendPay())
                && waybill.getSendPay().length() > AIR_SIGN_INDEX
                && waybill.getSendPay().charAt(AIR_SIGN_INDEX) == AIR_SIGN_VALUE);
    }


    /**
     * 判断是否为平台订单
     *
     * @param waybill 运单对象
     * @return 平台订单：true;非平台订单：false;
     */
    public static boolean isPlateWaybill(Waybill waybill) {
        return Constants.POP_FBP.equals(waybill.getType())
                || Constants.POP_SOPL.equals(waybill.getType())
                || Constants.POP_SOP.equals(waybill.getType())
                || Constants.POP_B.equals(waybill.getType());
    }

    /**
     * 根据waybillSign和sendSign判断是否城配运单 waybillSign 36为1 或sendPay 146为1
     * @param waybill
     * @return
     */
    public static boolean isTmsTransBill(Waybill waybill) {
        return waybill!=null
        		&& BusinessUtil.isUrban(waybill.getWaybillSign(), waybill.getSendPay());
    }

    /**
     * 判断是否填航空仓订单
     * sendPay 137 = 1
     * @return
     */
    public static boolean isFillAviationWarehouse(Waybill waybill) {
        return null != waybill && BusinessUtil.isSignChar(waybill.getSendPay(), 137, '1');
    }

    /**
     * 根据单号判断是否是经济网单号
     * @param waybillCode
     * @return
     */
    public static boolean isEconomicNet(String waybillCode) {
        return WaybillCodePattern.ENOCOMIC_WAYBILL_CODE.equals(
                UniformValidateUtil.getSpecificWaybillCodePattern(waybillCode));
    }

}
