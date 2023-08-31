package com.jd.bluedragon.dms.utils;

import com.jd.etms.waybill.constant.WaybillCodePattern;
import com.jd.etms.waybill.util.UniformValidateUtil;
import com.jd.etms.waybill.util.WaybillCodeRuleValidateUtil;

import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jd.bluedragon.dms.utils.DmsConstants.*;

/**
 * @author tangchunqing
 * @Description: 业务相关判断
 * @date 2018年10月12日 18时:15分
 */
public class BusinessUtil {
    /**
     * 是不是发货批次号
     *
     * @param sendCode
     * @return
     */
    public static boolean isSendCode(String sendCode) {
        if (StringUtils.isBlank(sendCode)) {
            return false;
        }
        return sendCode.matches(SEND_CODE_ALL_REG) || isSingleBatchNo(sendCode);
    }

    /**
     * 是不是终端批次号
     * R开头
     *
     * @param sendCode
     * @return
     */
    public static boolean isTerminalSendCode(String sendCode) {
        if (StringUtils.isBlank(sendCode)) {
            return false;
        }
        return RULE_TERMINAL_SEND_CODE_ALL_REGEX.matcher(sendCode).matches();
    }

    /**
     * 根据批次号的正则匹配始发分拣中心id和目的分拣中心id
     *
     * @param sendCode 批次号
     * @return
     */
    public static Integer[] getSiteCodeBySendCode(String sendCode) {
        Integer[] sites = new Integer[]{-1, -1};
        if (StringUtils.isNotBlank(sendCode)) {
            Matcher matcher = DmsConstants.RULE_SEND_CODE_ALL_REGEX.matcher(sendCode.trim());
            if (matcher.matches()) {
                sites[0] = Integer.valueOf(matcher.group(1));
                sites[1] = Integer.valueOf(matcher.group(2));
            }
        }
        return sites;
    }

  /**
   * 是否为新批次号
   * 批次号判断批次号是否是：站点（数字）+站点（数字）+时间串（yyyyMMddHH 10位数字）+序号（6位数字）+模7余数
   * 模7余数：对 站点第一位+站点第一位+时间串+序列号 取模
   * 必须是17位（时间10位+序号6位+模7余数1位）
   * @param input
   * @return
   */
  public static boolean isSingleBatchNo(String input) {
      if (StringUtils.isBlank(input)) {
          return false;
      }
      if (input.matches(SEND_CODE_NEW_REG)) {
          String[] tempStr = input.split("-");
          String startSiteCode = tempStr[0];
          String desSiteCode = tempStr[1];
          String timeString = tempStr[2];
          long mod = Long.valueOf(startSiteCode.substring(0, 1) + desSiteCode.substring(0, 1) + timeString.substring(0, timeString.length() - 1)) % 7L;
          long tail = Long.valueOf(timeString.substring(timeString.length() - 1));
          return mod == tail;
      }

      return false;
  }

    /**
     * 判断输入字符串是否为箱号. 箱号规则： 箱号： B(T,G) C(S) 010F001 010F002 12345678 。
     * B，正向；T，逆向；G取件退货;C普通物品；S奢侈品；2-8位，出发地编号；9-15位，到达地编号；最后8位，流水号。一共23位。 前面有两个字母
     *
     * @param s 用来判断的字符串
     * @return 如果此字符串为箱号，则返回 true，否则返回 false
     */

    public static Boolean isBoxcode(String s) {
        if (StringUtils.isEmpty(s)) {
            return Boolean.FALSE;
        }
        return isMatchBoxCode(s);
    }
    public static Boolean isStoreCode(String s) {
        if (StringUtils.isEmpty(s)) {
            return Boolean.FALSE;
        }
        return isMatchStoreCode(s);
    }

    private static Boolean isMatchStoreCode(String s) {
        return  BIG_WARM_BOX_CODE_REGEX.matcher(s.toUpperCase().trim()).matches();
    }

    public static Boolean isIceBoardCode(String s) {
        if (StringUtils.isEmpty(s)) {
            return Boolean.FALSE;
        }
        return isMatchIceBoardCode(s);
    }

    private static Boolean isMatchIceBoardCode(String s) {
        return  DmsConstants.ICE_BOARD_BOX_CODE_REGEX.matcher(s.trim().toUpperCase()).matches();
    }

    /**
     * 判断是否为箱号
     *
     * @param boxCode 待验证字符
     * @return
     */
    private static boolean isMatchBoxCode(String boxCode) {
        return DmsConstants.RULE_BOXCODE_REGEX_OLD.matcher(boxCode.trim().toUpperCase()).matches()
                || DmsConstants.RULE_BOXCODE_REGEX.matcher(boxCode.trim().toUpperCase()).matches()
                || DmsConstants.RULE_BOXCODE_REGEX_OPEN_DP.matcher(boxCode.trim().toUpperCase()).matches();
    }

    /**
     * 判断是否板号
     *
     * @param boardCode
     * @return
     */
    public static final boolean isBoardCode(String boardCode) {
        if (StringUtils.isNotBlank(boardCode) && DmsConstants.RULE_BOARD_CODE_REGEX.matcher(boardCode.trim().toUpperCase()).matches()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为集货区编码
     * @param code
     * @return
     */
    public static final boolean isCollectAreaCode(String code){
        if (StringUtils.isBlank(code)) {
            return false;
        }
        return code.matches(RULE_COLLECT_AREA_CODE_REGEX);
    }

    /**
     * 判断是否为集货位编码
     * @param code
     * @return
     */
    public static final boolean isCollectPlaceCode(String code){
        if (StringUtils.isBlank(code)) {
            return false;
        }
        return code.matches(RULE_COLLECT_PLACE_CODE_REGEX);
    }
    /**
     * 判断是否为站点编号编码
     * @param code
     * @return
     */
    public static final boolean isSiteCode(String code){
        if (StringUtils.isBlank(code)) {
            return false;
        }
        return code.matches(RULE_SITE_CODE);
    }
    /**
     * 判断是否为三无编码
     * @param code
     * @return
     */
    public static final boolean isSanWuCode(String code){
        if (StringUtils.isBlank(code)) {
            return false;
        }
        return code.matches(RULE_SAN_WU_CODE);
    }


    /**
     * 判断是否逆向箱号（TC\TS\TW)
     * TC:退货普通
     * TS:退货奢侈品
     * TW:逆向内配
     *
     * @param boxCode
     * @return
     */
    public static final boolean isReverseBoxCode(String boxCode) {
        if (StringUtils.isNotBlank(boxCode) &&
                (DmsConstants.RULE_REVERSE_BOXCODE_REGEX_OLD.matcher(boxCode.trim().toUpperCase()).matches()
                ||DmsConstants.RULE_REVERSE_BOXCODE_REGEX.matcher(boxCode.trim().toUpperCase()).matches())) {
            return true;
        }
        return false;
    }


    /**
     * 获取包裹总数
     * 默认最大包裹数2W
     *
     * @param packCode
     * @return
     */
    public static int getPackNumByPackCode(String packCode) {
        return getPackNumByPackCode(packCode, DmsConstants.MAX_NUMBER);
    }

    /**
     * 获取包裹总数
     *
     * @param packCode
     * @param maxNum   最大包裹数
     * @return
     */
    public static int getPackNumByPackCode(String packCode, int maxNum) {
        int num = WaybillCodeRuleValidateUtil.getPackNumByPackCode(packCode);
        if (num > maxNum) {
            return maxNum;
        }
        return num;
    }

    /**
     * 判断字符串位置是否标记为1
     *
     * @param signStr
     * @param position 标识位
     * @return
     */
    public static boolean isSignY(String signStr, int position) {
        return isSignChar(signStr, position, DmsConstants.FLG_CHAR_YN_Y);
    }

    /**
     * 判断字符串位置是否标记为指定的字符
     *
     * @param signStr
     * @param position
     * @param signChar
     * @return
     */
    public static boolean isSignChar(String signStr, int position, char signChar) {
        if (StringUtils.isNotEmpty(signStr) && signStr.length() >= position) {
            return signStr.charAt(position - 1) == signChar;
        }
        return false;
    }

    /**
     * 获取标位指定位置的标位值
     * @param signStr 标位
     * @param position 位置
     * @return 标位值
     * @author fanggang7
     * @time 2023-03-13 16:16:17 周一
     */
    public static Character getSignCharAtPosition(String signStr, int position) {
        if (StringUtils.isNotEmpty(signStr) && signStr.length() >= position) {
            return signStr.charAt(position - 1);
        }
        return null;
    }

    /**
     * 是否奢侈品
     * @param sendPay
     * @return
     */
    public static boolean isLuxury(String sendPay) {
        return isSignChar(sendPay, 19,'1');
    }

    /**
     * 根据waybillSign和sendSign判断是否城配运单
     *
     * @param waybillSign 36为1
     * @param sendPay     146为1
     * @return
     */
    public static boolean isUrban(String waybillSign, String sendPay) {
        return isSignY(sendPay, 146) || isSignY(waybillSign, 36);
    }

    /**
     * 1号店订单判断逻辑：sendpay  60-62位 ，034、035、036、037、038、039为一号店订单
     *
     * @param sendPay 60=0 61=3 62=4 5 6 7 8 9
     * @return
     */
    public static boolean isYHD(String sendPay) {
        if (isSignChar(sendPay, 60, '0') && isSignChar(sendPay, 61, '3')) {
            if (isSignChar(sendPay, 62, '4') || isSignChar(sendPay, 62, '5') || isSignChar(sendPay, 62, '6') ||
                    isSignChar(sendPay, 62, '7') || isSignChar(sendPay, 62, '8') || isSignChar(sendPay, 62, '9')) {
                return true;
            }
        }

        return false;
    }

    /**
     * 根据waybillSign第一位判断是否SOP(标识为 2)或纯外单（标识为 3、6、9、K、Y）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isSopOrExternal(String waybillSign) {
        return (isSignChar(waybillSign, 1, '2') || isExternal(waybillSign));
    }

    /**
     * 根据waybillSign第一位判断是否纯外单（标识为 3、6、9、K、Y）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isExternal(String waybillSign) {
        return (isSignChar(waybillSign, 1, '3')
                || isSignChar(waybillSign, 1, '6')
                || isSignChar(waybillSign, 1, '9')
                || isSignChar(waybillSign, 1, 'K')
                || isSignChar(waybillSign, 1, 'Y'));
    }

    /**
     * 根据waybillSign第40位判断是否快运业务（标识为 1、2、3、4、5）
     *
     * @param waybillSign 运单标识位
     * @return
     */
     public static boolean isFastTrans(String waybillSign){
         return isSignInChars(waybillSign, WaybillSignConstants.POSITION_40, '1', '2', '3', '4', '5');
     }

    /**
     * 判断是否B网，转网到B+未转网到C并且waybillSign第40位1、2、3、4、5
     *
     * @param waybillSign
     * @return
     */
    public static boolean isB2b(String waybillSign) {
        return isSignInChars(waybillSign, WaybillSignConstants.POSITION_97,WaybillSignConstants.CHAR_97_1, WaybillSignConstants.CHAR_97_4)
        		|| (isSignInChars(waybillSign, WaybillSignConstants.POSITION_40, '1', '2', '3', '4', '5')
        				&& !isSignInChars(waybillSign, WaybillSignConstants.POSITION_97,
        						WaybillSignConstants.CHAR_97_2,WaybillSignConstants.CHAR_97_3));
    }

    /**
     * 称重量方B网卡控逻辑
     * B网卡控标准（waybillsign40=1/2/3，且80位不等于6/7/8（剔除冷链），且89位不等于1/2（剔除tc），且99位不等于1（剔除京小仓））：
     *
     * @param waybillSign
     * @return
     */
    public static boolean isWeightVolumeB(String waybillSign) {
        return isSignInChars(waybillSign, WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_1,WaybillSignConstants.CHAR_40_2, WaybillSignConstants.CHAR_40_3)
                && !isSignInChars(waybillSign, WaybillSignConstants.POSITION_80,WaybillSignConstants.CHAR_80_6,WaybillSignConstants.CHAR_80_7,WaybillSignConstants.CHAR_80_8)
                && !isSignInChars(waybillSign, WaybillSignConstants.POSITION_89,WaybillSignConstants.CHAR_89_1,WaybillSignConstants.CHAR_89_2)
                && !isSignInChars(waybillSign, WaybillSignConstants.POSITION_99,WaybillSignConstants.CHAR_99_1);
    }

    /**
     * 判断字符串指定的位置是否在指定的字符范围之内
     *
     * @param signStr  目标字符串
     * @param position 标识位置
     * @param chars    字符范围
     * @return
     */
    public static boolean isSignInChars(String signStr, int position, char... chars) {
        if (StringUtils.isNotEmpty(signStr)
                && signStr.length() >= position
                && chars != null
                && chars.length > 0) {
            char positionChar = signStr.charAt(position - 1);
            if (chars.length == 1) {
                return chars[0] == positionChar;
            } else {
                for (char tmp : chars) {
                    if (positionChar == tmp) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 根据waybillSign判断是否病单（34位标识为 2）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isSick(String waybillSign) {
        return isSignInChars(waybillSign, 34, '2');
    }

    /**
     * 根据waybillSign判断是否加履中心订单 （29 位 9 ）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isPerformanceOrder(String waybillSign) {
        return isSignInChars(waybillSign, 29, '9');
    }

    /**
     * 根据waybillSign判断是否一盘货订单 （29 位 6 ）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isYiPanHuoOrder(String waybillSign) {
        return isSignInChars(waybillSign, 29, '6');
    }

    /**
     * 包裹半收 标识 waybillSign 27位 （0-不半收 1-全收半退 2-包裹半收 3-运单明细半收 4-包裹明细半收）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isPackageHalf(String waybillSign) {
        return isSignChar(waybillSign, 27, '2');
    }

    /**
     * 支持协商再投
     *
     * @param waybillSign
     * @return
     */
    public static boolean isConsultationTo(String waybillSign) {
        return isSignChar(waybillSign, 5, '3');
    }

    /**
     * 通过运单标识 判断是否需求称重
     * <p>
     * 66 位 是1  标识不称重
     *
     * @param waybillSign
     * @return
     */
    public static boolean isNoNeedWeight(String waybillSign) {
        return isSignChar(waybillSign, 66, '1');
    }

    /**
     * 通过运单标识 判断B网耗材
     * <p>
     * 72位：是否需要包装服务： 0---不需要 默认，1---需要包装服务
     * 25 位 是3  标识 B网耗材不允许修改，只能操作确认
     *
     * @param waybillSign
     * @return
     */
    public static boolean isWaybillConsumableOnlyConfirm(String waybillSign) {
        return isSignChar(waybillSign, 25, '3');
    }

    /**
     * 获取始发道口号类型
     * <p>自营：sendpay137位为1，则为航运订单标识，航填,其他为普通
     * <p>外单：waybillsign第31位等于1，则为航空，waybillsign第31位等于0，且waybillsign第67位等于1则为航填
     *
     * @param waybillSign
     * @param sendPay
     * @return
     */
    public static Integer getOriginalCrossType(String waybillSign, String sendPay) {
        //外单-waybillsign第31位等于1，则为航空，waybillsign第31位等于0，且waybillsign第67位等于1则为航填
        //12日26日修改 特快送项目  变更标位， 变更为 （31位1 并且 116位0 ）或者 84位3 都代表航
        if ((isSignChar(waybillSign, 31, '1') &&
                BusinessUtil.isSignChar(waybillSign,WaybillSignConstants.POSITION_116,DmsConstants.FLG_CHAR_DEFAULT))
                || BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_84, WaybillSignConstants.CHAR_84_3)||
                BusinessUtil.isSignChar(waybillSign,WaybillSignConstants.POSITION_80,WaybillSignConstants.CHAR_80_C)) {
            return DmsConstants.ORIGINAL_CROSS_TYPE_AIR;
        } else if (isSignChar(waybillSign, 31, '0') && isSignChar(waybillSign, 67, '1')) {
            return DmsConstants.ORIGINAL_CROSS_TYPE_FILL;
        }
        //自营-sendpay137位为1，则为航运订单标识，航填
        if (isSignChar(sendPay, 137, '1')) {
            return DmsConstants.ORIGINAL_CROSS_TYPE_FILL;
        }
        return DmsConstants.ORIGINAL_CROSS_TYPE_GENERAL;
    }

    /**
     * 判断是否招商银行业务运单，waybill_sign第54位等于3时
     *
     * @param waybillSign
     * @return
     */
    public static boolean isCMBC(String waybillSign) {
        return isSignChar(waybillSign, 54, '3');
    }

    /**
     * 是否是RMA标识的运单
     *
     * @param waybillSign
     * @return
     */
    public static boolean isRMA(String waybillSign) {
        if (isSignChar(waybillSign, 32, '1')) {
            return true;
        }
        return false;
    }

    /**
     * 是否是小米运单
     * @param busiCode
     * @return
     */
    public static boolean isMillet(String busiCode){
        if(StringUtils.isEmpty(busiCode)){
            return false;
        }
        return DmsConstants.busiCodeOfMillet.equals(busiCode);
    }

    /**
     * 判断正向
     *
     * @param businessType
     * @return
     */
    public static Boolean isForward(Integer businessType) {
        if (businessType == null) {
            return Boolean.FALSE;
        }

        if (DmsConstants.BUSSINESS_TYPE_POSITIVE == businessType.intValue()
                || DmsConstants.BUSSINESS_TYPE_THIRD_PARTY == businessType.intValue()) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 判断逆向
     *
     * @param businessType
     * @return
     */
    public static Boolean isReverse(Integer businessType) {
        if (businessType == null) {
            return Boolean.FALSE;
        }

        if (DmsConstants.BUSSINESS_TYPE_REVERSE == businessType.intValue()) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 是否多宝岛订单 waybill.wabillType = 2
     * 拍卖订单
     *
     * @param waybillType
     * @return
     */
    public static boolean isAuction(Integer waybillType) {
        if (waybillType == null) {
            return Boolean.FALSE;
        }
        if (DmsConstants.AUCTION.equals(waybillType)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否为商家类型
     */
    public static boolean isBizSite(Integer siteType) {
        return DmsConstants.SITE_TYPE_BIZ.equals(siteType);
    }

    /**
     * 是否为仓类型
     */
    public static boolean isWmsSite(Integer siteType) {
        return DmsConstants.SITE_TYPE_WMS.equals(siteType);
    }

    /**
     * 是否为分拣中心类型
     */
    public static boolean isDistrubutionCenter(Integer siteType) {
        return DmsConstants.SITE_TYPE_DMS.equals(siteType);
    }

    /**
     * 是否为站点类型
     */
    public static boolean isSite(Integer siteType) {
        return DmsConstants.SITE_TYPE_SITE.equals(siteType);
    }

    /**
     * 是否为车队类型
     */
    public static boolean isFleet(Integer siteType) {
        return DmsConstants.SITE_TYPE_FLEET.equals(siteType);
    }

    /**
     * 判断订单是否是买卖宝的
     *
     * @param waybillCode
     * @param waybillSign
     * @param sendPay
     * @return
     */
    public static Boolean isMMBWaybill(String waybillCode, String waybillSign, String sendPay) {

        try {
            if (WaybillCodeRuleValidateUtil.isJDWaybillCode(waybillCode)) {
                if (!StringUtils.isEmpty(waybillSign) && DmsConstants.MMB_SELF_MARK == waybillSign.charAt(10)) {
                    return Boolean.TRUE;
                }
            }

            if (WaybillCodeRuleValidateUtil.isBusiWaybillCode(waybillCode)) {
                if (!StringUtils.isEmpty(sendPay) && DmsConstants.MMB_V_MARK.equals(sendPay.substring(59, 62))) {
                    return Boolean.TRUE;
                }
            }
        } catch (Exception ex) {
            return Boolean.FALSE;
        }

        return Boolean.FALSE;
    }

    /**
     * 是否是理赔换新单
     *
     * @param waybillSign
     * @return
     */
    public static boolean isLPNewWaybill(String waybillSign) {
        if (isSignChar(waybillSign, 18, '4')) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是CLPS订单
     * CLPS : 云仓
     *
     * @param busiOrderCode 运单中的BusiOrderCode字段,判断它是不是CSL开头单号
     * @return
     */
    public static Boolean isCLPSByBusiOrderCode(String busiOrderCode) {
        if (StringUtils.isEmpty(busiOrderCode)) {
            return Boolean.FALSE;
        }
        if (busiOrderCode.startsWith(DmsConstants.BUSI_ORDER_CODE_PRE_CLPS)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }


    /**
     * 通过运单标识 判断是否需求包装耗材
     * <p>
     * 72 位 是1  标识需要
     *
     * @param waybillSign
     * @return
     */
    public static boolean isNeedConsumable(String waybillSign) {
        return isSignChar(waybillSign, 72, '1');
    }

    /**
     * 判断是否是外单二次换单后退备件库的运单
     *
     * @param waybillSign
     * @return
     */
    public static Boolean isTwiceExchageWaybillSpare(String waybillSign) {
        return isSignChar(waybillSign, 18, '5');
    }
    /**
     * 判断是否是外单京准达,waybill_sign  第31位等于6
     * @param waybillSign
     * @return
     */
    public static Boolean isSopJZD(String waybillSign) {
        return isSignChar(waybillSign,31,'6');
    }

    /**
     * 判断是否C2C京准达，waybill_sign 第113位等于2
     */
    public static Boolean isC2CJZD(String waybillSign){
        return isSignChar(waybillSign,113,'2');
    }
    /**
     * 判断是否C2C:waybill_sign 第29位等于8
     */
    public static Boolean isC2C(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_29,WaybillSignConstants.CHAR_29_8);
    }
    /**
     * 是否为三方-合作站点
     * @param type
     * @return
     */
    public static Boolean isThreePartner(Integer type ,Integer subType) {
        if (type == null || subType == null) {
            return Boolean.FALSE;
        }

        if (16 == type.intValue() && 22 == subType.intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    /**
     * 是否为三方-校园派
     * @param type
     * @return
     */
    public static Boolean isSchoolyard(Integer type ,Integer subType) {
        if (type == null || subType == null) {
            return Boolean.FALSE;
        }

        if (16 == type.intValue() && 128 == subType.intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断自提柜类型
     *
     * @param sendPay
     * @return
     */
    public static Boolean isZiTiGui(String sendPay) {
        if (sendPay == null) {
            return Boolean.FALSE;
        }
        if ('5' == sendPay.charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 便民自提判断 【sendpay 第22位等于6(合作自提柜 )】
     */
    public static Boolean isBianMinZiTi(String sendPay) {
        if (sendPay == null ) {
            return Boolean.FALSE;
        }
        if ('6' == sendPay.charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 便民自提判断 【sendpay 7的订单(合作代收点)】
     */
    public static Boolean isHeZuoDaiShou(String sendPay) {
        if (sendPay == null ) {
            return Boolean.FALSE;
        }
        if ('7' == sendPay.charAt(21)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 支持半退标
     * @param waybillSign
     * @return
     */
    public static Boolean isPartReverse(String waybillSign){
        return isSignChar(waybillSign, 27, '5');
    }


    /**
     * 纯配外单判断 【waybillSign第1为为2、3、6、9、K、Y且第53位为2、0】
     * */
    public static Boolean isPurematch(String waybillSign){
        if(waybillSign == null){
            return Boolean.FALSE;
        }
        if(isSignInChars(waybillSign,53,'2','0')
                && isSignInChars(waybillSign,1,'2','3','6','9','K','Y')){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 快运外单判断
     * <p>
     *      1、快运零担【waybillSign第40位为2且第80位为0】
     *      2、特快重货【waybillSign第40位为2或3且第80位为9】
     *      3、特运零担【waybillSign第40位为2或3且第80位为2】
     *      4、特慧零担【waybillSign第40位为2且第80位为1】
     *      5、其他【waybillSign：89=0 & 99=0 & 54=0 & 62=0,1,4 & 29=2 & 10=1】
     * </p>
     * @param waybillSign
     * @return*/
    public static boolean isKyLdop(String waybillSign){
        if(isTKLD(waybillSign) || isTKZH(waybillSign) || isTYLD(waybillSign) || isTHLD(waybillSign)
                || (isSignChar(waybillSign,89,'0') && isSignChar(waybillSign,99,'0')
                && isSignChar(waybillSign,54,'0') && isSignInChars(waybillSign,62,'0','1','4')
                && isSignChar(waybillSign,29,'2') && isSignChar(waybillSign,10,'1'))){
            return true;
        }
        return false;
    }

    /**
     * 判断是否特快零担
     * @param waybillSign
     * @return
     */
    public static boolean isTKLD(String waybillSign){
        return isSignChar(waybillSign,40,'2') && isSignChar(waybillSign,80,'0');
    }

    /**
     * 判断是否特快重货
     * @param waybillSign
     * @return
     */
    public static boolean isTKZH(String waybillSign){
        return isSignInChars(waybillSign,40,'2', '3') && isSignChar(waybillSign,80,'9');
    }

    /**
     * 判断是否特运零担
     * @param waybillSign
     * @return
     */
    public static boolean isTYLD(String waybillSign){
        return isSignInChars(waybillSign,40,'2', '3') && isSignChar(waybillSign,80,'2');
    }

    /**
     * 判断是否特惠零担
     * @param waybillSign
     * @return
     */
    public static boolean isTHLD(String waybillSign){
        return isSignChar(waybillSign,40,'2') && isSignChar(waybillSign,80,'1');
    }

    /**
     * 判断是否是移动仓内配单
     * @param waybillSign
     * @return
     */
    public static Boolean isMovingWareHouseInnerWaybill(String waybillSign){
        return isSignChar(waybillSign,14,'5');
    }

    /**
     * 判断原单作废，逆向单不计费
     * @param waybillSign
     * @return
     */
    public static Boolean isYDZF(String waybillSign){
        return isSignChar(waybillSign,14,'D');
    }

    /**
     * 判断原单拒收因京东原因产生的逆向单，不计费
     * @param waybillSign
     * @return
     */
    public static Boolean isJDJS(String waybillSign){
        return isSignChar(waybillSign,14,'E');
    }

    /**
     * 判断是否是运费临时欠款 【WaybillSign 62位 =1（营业厅运单），且WaybillSign 25位 = 4 时】
     * @param waybillSign
     * @return
     */
    public static Boolean isTemporaryArrearsWaybill(String waybillSign){
        return isSignChar(waybillSign,62,'1') && isSignChar(waybillSign,25,'4');
    }

    /**
     * 分拣中心和转运中心判断
     * 1:分拣中心 0:转运中心 -1:都不是
     * @param subType 站点子类型
     * @return
     */
    public static Integer isSortOrTransport(Integer subType){
        Integer flage = -1;
        Integer[] transportSite = new Integer[]{6420,6460,44079};
        Integer[] notSortSites = new Integer[]{6420,6440,6450,6460,6470,44079};
        if(Arrays.asList(transportSite).contains(subType)){
            flage = 0;
        }else if(!Arrays.asList(notSortSites).contains(subType)){
            flage = 1;
        }else{
            flage = -1;
        }
        return flage;
    }

    /**
     * 拣运中心
     * @param type 站点类型
     * @return
     */
    public static boolean isSorting(Integer type){
        return Integer.valueOf(64).equals(type);
    }

    /**
     * 转运中心
     * @param subType
     * @return
     */
    public static boolean isTransferSite(Integer subType){
        return Integer.valueOf(6420).equals(subType);
    }

    /**
     *判断是否是冷链卡班
     */
    public static Boolean isColdChainKB(String waybillSign,String productType){
        return PRODUCT_TYPE_COLD_CHAIN_KB.equals(productType)
                || (isSignChar(waybillSign,WaybillSignConstants.POSITION_80,WaybillSignConstants.CHAR_80_7)
                     && isSignChar(waybillSign,WaybillSignConstants.POSITION_54,WaybillSignConstants.CHAR_54_2)
                     && isSignInChars(waybillSign,WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_2,WaybillSignConstants.CHAR_40_3)
                    );
    }

    /**
     * 判断是否是B网冷链运单
     * @param waybillSign
     * @return
     */
    public static Boolean isColdChainWaybill(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_54,WaybillSignConstants.CHAR_54_2);
    }

    /**
     * 判断是否是纯配冷链卡班
     * waybill_sign54位=2（生鲜）、waybill_sign80位=7（卡班）、40位=2（纯配快运零担）
     * @param waybillSign
     * @return
     */
    public static Boolean isColdChainCPKB(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_80,WaybillSignConstants.CHAR_80_7)
                && isSignChar(waybillSign,WaybillSignConstants.POSITION_54,WaybillSignConstants.CHAR_54_2)
                && isSignChar(waybillSign,WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_2);
    }

    /**
     * 判断是否是纯配医药零担
     * waybill_sign54位=4（医药）、waybill_sign80位=7（卡班）、40位=2（纯配快运零担）
     * @param waybillSign
     * @return
     */
    public static Boolean isMedicineCP(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_80,WaybillSignConstants.CHAR_80_7)
                && isSignChar(waybillSign,WaybillSignConstants.POSITION_54,WaybillSignConstants.CHAR_54_4)
                && isSignChar(waybillSign,WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_2);
    }

    /**
     * 判断是否医药零担
     *
     * @param waybillSign
     * @return
     */
    public static Boolean isMedicine(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_80, WaybillSignConstants.CHAR_80_7)
                && isSignChar(waybillSign,WaybillSignConstants.POSITION_54, WaybillSignConstants.CHAR_54_4)
                && isSignInChars(waybillSign,WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_2, WaybillSignConstants.CHAR_40_3);
    }

    /**
     * 判断是否是冷链专送
     *
     * @param waybillSign
     * @return
     */
    public static Boolean isColdDelivery(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_G);
    }

    /**
     * 判断是否是冷链城配
     *
     * @param waybillSign
     * @return
     */
    public static Boolean isColdCityDistribute(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_2);
    }

    /**
     * 判断是否是冷链卡班
     *
     * @param waybillSign
     * @return
     */
    public static Boolean isColdKB(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_54, WaybillSignConstants.CHAR_54_2);
    }

    /**
     * 判断是否是冷链小票
     *
     * @param waybillSign
     * @return
     */
    public static Boolean isColdReceipt(String waybillSign){
        return isSignInChars(waybillSign,WaybillSignConstants.POSITION_80, WaybillSignConstants.CHAR_80_6, WaybillSignConstants.CHAR_80_7);
    }

    /**
     * 判断是否是生鲜纯配城配共配
     * waybill_sign54位=2（生鲜）、waybill_sign80位=6（城配）、40位=2（纯配快运零担）、118位=1（共配）
     * @param waybillSign
     * @return
     */
    public static Boolean isFreshCPGP(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_80,WaybillSignConstants.CHAR_80_6)
                && isSignChar(waybillSign,WaybillSignConstants.POSITION_54,WaybillSignConstants.CHAR_54_2)
                && isSignChar(waybillSign,WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_2)
                && isSignChar(waybillSign,WaybillSignConstants.POSITION_118,WaybillSignConstants.CHAR_118_1);
    }

    /**
     * 40位=2（纯配快运零担）
     * @param waybillSign
     * @return
     */
    public static Boolean isCPKYLD(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_2);
    }

    /**
     * 40位=3（仓配零担）
     * @param waybillSign
     * @return
     */
    public static Boolean isCPLD(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_3);
    }

    /**
     * 不需要隐藏收件人信息
     * @param waybillSign
     * @return
     */
    public static Boolean isNoNeedHideCustomer(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_37,WaybillSignConstants.CHAR_37_0);
    }
    /**
     * 不需要隐藏寄件人信息
     * @param waybillSign
     * @return
     */
    public static Boolean isNoNeedHideConsigner(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_47,WaybillSignConstants.CHAR_47_0);
    }

    /**
     * 判断是否是冷链卡班
     */
    public static Boolean isColdChainKBWaybill(String waybillSign) {
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_80, WaybillSignConstants.CHAR_80_7)
                && isSignChar(waybillSign, WaybillSignConstants.POSITION_54, WaybillSignConstants.CHAR_54_2);
    }

    /**
     * 判断是否为冷链城配
     *
     * @return
     */
    public static Boolean isColdChainCityDeliveryWaybill(String waybillSign) {
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_80, WaybillSignConstants.CHAR_80_6)
                && isSignChar(waybillSign, WaybillSignConstants.POSITION_54, WaybillSignConstants.CHAR_54_2)
                && isSignInChars(waybillSign, WaybillSignConstants.POSITION_118, WaybillSignConstants.CHAR_118_0, WaybillSignConstants.CHAR_118_1);
    }

    /**
     * 判断是否是京仓运单
     * @param waybillSign
     * @return
     */
    public static Boolean isWareHouseJDWaybill(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_89,WaybillSignConstants.CHAR_89_3);
    }

    /**
     * 判断是否是非京仓运单
     * @param waybillSign
     * @return
     */
    public static Boolean isWareHouseNotJDWaybill(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_89,WaybillSignConstants.CHAR_89_4);
    }
    /**
     * 判断是否是纯外单 waybill_sign第1位等于 3或6或9或K或Y
     * @param waybillSign
     * @return
     */
    public static Boolean isForeignWaybill(String waybillSign){
        return isSignInChars(waybillSign,1,'3','6','9','K','Y');
    }

    /**
     * 判断是否是纯配运单 waybill_sign第53位等于0或2
     * @param waybillSign
     * @return
     */
    public static Boolean isPureDeliveryWaybill(String waybillSign){
        return isSignInChars(waybillSign,53,'0', '2');
    }
    /**
     * 判断是否TC，waybillSign第89位为1和2
     * 或者（89位为0且40位等于6或7）--新增 2020-3-15 23:29:42
     * @param waybillSign
     * @return
     */
    public static boolean isTc(String waybillSign) {
        if (isSignInChars(waybillSign,WaybillSignConstants.POSITION_89,
                WaybillSignConstants.CHAR_89_1,WaybillSignConstants.CHAR_89_2)) {
            return Boolean.TRUE;
        }
        if (isSignChar(waybillSign,WaybillSignConstants.POSITION_89,WaybillSignConstants.CHAR_89_0)
                && isSignInChars(waybillSign,WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_6,WaybillSignConstants.CHAR_40_7)) {
            return Boolean.TRUE;
        }

    	return Boolean.FALSE;
    }

    /**
     * 判断是否是终端
     * @param siteType
     * @return
     */
    public static boolean isTerminalSite(Integer siteType){
        List<Integer> terminalSiteTypeList = new ArrayList<Integer>();
        terminalSiteTypeList.add(4);//营业部
        terminalSiteTypeList.add(8);//自提点
        terminalSiteTypeList.add(16);//第三方
        terminalSiteTypeList.add(101);//B网营业厅
        terminalSiteTypeList.add(108);//全能营业厅

        return terminalSiteTypeList.contains(siteType);
    }

    /**
     * 判断是否是终端 （siteType =4或8）或（siteType =16且subType =128或16或1605或99或1604）
     * @param siteType
     * @return
     */
    public static boolean isTerminalSite(Integer siteType, Integer subType){
        List<Integer> terminalSiteTypeList = new ArrayList<Integer>();
        terminalSiteTypeList.add(4);//营业部
        terminalSiteTypeList.add(8);//自提点

        List<Integer> terminalSiteSubTypeList = new ArrayList<Integer>();
        terminalSiteSubTypeList.add(128);
        terminalSiteSubTypeList.add(16);
        terminalSiteSubTypeList.add(1605);
        terminalSiteSubTypeList.add(99);
        terminalSiteSubTypeList.add(1604);

        return terminalSiteTypeList.contains(siteType) || (siteType != null && siteType == 16 && terminalSiteSubTypeList.contains(subType));
    }

    /**
     * 判断是否是车队
     * @param siteType
     * @return
     */
    public static boolean isConvey(Integer siteType){
        return siteType.equals(96);
    }


    /**
     * 通过批次号获取目的站点
     *
     * @param sendCode 发货批次号
     * @return
     */
    public static Integer getReceiveSiteCodeFromSendCode(String sendCode) {
    	Integer[] sites = getSiteCodeBySendCode(sendCode);
        if (sites[1]>0) {
            return sites[1];
        }
        return null;
    }

    /**
     * 通过批次号获取始发站点
     *
     * @param sendCode 发货批次号
     * @return
     */
    public static Integer getCreateSiteCodeFromSendCode(String sendCode) {
    	Integer[] sites = getSiteCodeBySendCode(sendCode);
        if (sites[0]>0) {
            return sites[0];
        }
        return null;
    }

    /**
     * 是否是营业厅
     * @param waybillSign waybillSign
     * @return true 是，false 不是
     */
    public static boolean isBusinessHall(String waybillSign) {
        return isSignChar(waybillSign,WaybillSignConstants.REPLACE_ORDER_POSITION_62,WaybillSignConstants.REPLACE_ORDER_CHAR_62_1);
    }

    /**
     * 是否寄付
     */
    public static boolean isFreightSend(String waybillSign) {
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_25,WaybillSignConstants.CHAR_25_3);
    }

    /**
     * 是否到付现结
     */
    public static boolean isDF(String waybillSign) {
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_25,WaybillSignConstants.CHAR_25_2);
    }

    /**
     * 是否寄付临欠
     */
    public static boolean isJFLQ(String waybillSign) {
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_25,WaybillSignConstants.CHAR_25_4);
    }

    /**
     * 是否正向 （外单）
     * waybillSign 61位是0
     * @param waybillSign waybillSign
     * @return true 是，false 不是
     */
    public static boolean isForeignForward(String waybillSign) {
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_61,WaybillSignConstants.CHAR_61_0);
    }

    /**
     * 运单打标是否是正向
     * waybillSign 15位是0
     * @param waybillSign waybillSign
     * @return true 是，false 不是
     */
    public static boolean isWaybillMarkForward(String waybillSign) {
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_15,WaybillSignConstants.CHAR_15_0);
    }

    /**
     * 正向外单 并且运单打标也是正向
     * @param waybillSign
     * @return true 正向，false 非正向
     */
    public static boolean isForeignForwardAndWaybillMarkForward(String waybillSign){
        return isForeignForward(waybillSign) && isWaybillMarkForward(waybillSign);
    }

    /**
     * 外单
     * 是否是 营业厅运单 并且 寄付 并且 是正向单
     * @param waybillSign waybillSign
     * @return true 是，false 不是
     */
    public static boolean isBusinessHallFreightSendAndForward(String waybillSign) {
        return isBusinessHall(waybillSign) && isFreightSend(waybillSign) && isForeignForward(waybillSign);
    }
    /**
     * 根据waybillSign判断是否自营单号,waybill_sign第1位等于 1、4、5、7、8 、A，判断为【自营】运单
     * @param waybillSign
     * @return
     */
	public static boolean isSelf(String waybillSign) {
		if(waybillSign!=null){
			return isSignInChars(waybillSign,WaybillSignConstants.POSITION_1,
					WaybillSignConstants.CHAR_1_1,WaybillSignConstants.CHAR_1_4,WaybillSignConstants.CHAR_1_5,WaybillSignConstants.CHAR_1_7,WaybillSignConstants.CHAR_1_8,WaybillSignConstants.CHAR_1_A);
		}
		return false;
	}


    /**
     * 判断是否是冷链医药运单   waybillSign第54位为4
     * @param waybillSign
     * @return
     */
    public static boolean isBMedicine(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_54,WaybillSignConstants.CHAR_54_4);
    }

    /**
     * 判断是否是毕业寄   waybillSign第98位为1或2
     * @param waybillSign
     * @return
     */
    public static boolean isGraduationExpress(String waybillSign){
        return isSignInChars(waybillSign, WaybillSignConstants.POSITION_98, WaybillSignConstants.CHAR_98_1, WaybillSignConstants.CHAR_98_2);
    }

    /**
     * 判断是否是爱回收
     * 16-1604
     * @param type
     * @parm subType
     * @return
     */
    public static Boolean isRecovery(Integer type ,Integer subType) {
        if (type == null || subType == null) {
            return Boolean.FALSE;
        }

        if (16 == type.intValue() && 1604 == subType.intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断是否是乡镇共配站
     * 16-1605
     */
    public static Boolean isRuralSite(Integer type, Integer subType) {
        if (type == null || subType == null) {
            return Boolean.FALSE;
        }

        if (type == 16 && subType == 1605) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断是否是自提点
     * 8
     */
    public static Boolean isSelfSite(Integer type) {
        return type != null && type == 8;
    }

    /**
     * 判断是否是营业部
     * 4-4
     */
    public static Boolean isSalesDeptSite(Integer type, Integer subType) {

        if (type == null || subType == null) {
            return Boolean.FALSE;
        }

        if (type == 4 && subType == 4) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 可能存在所属站的情况，调用基础资料basicSiteQueryWS.getSiteExtensionBySiteId接口获取所属站信息
     */
    public static Boolean isMayBelongSiteExist(Integer type ,Integer subType) {
        return BusinessUtil.isSelfSite(type)
                || BusinessUtil.isThreePartner(type, subType)
                || BusinessUtil.isSchoolyard(type, subType)
                || BusinessUtil.isRecovery(type, subType)
                || BusinessUtil.isAllianceBusiSite(type, subType)
                || BusinessUtil.isRuralSite(type, subType)
                || BusinessUtil.isSalesDeptSite(type, subType);
    }

    /**
     * 判断是否是加盟商运单 106=2
     * @param waybillSign
     * @return
     */
    public static boolean isAllianceBusi(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_106, WaybillSignConstants.CHAR_106_2);
    }

    /**
     * 判断是否是国际配送运单
     * Sendpay 第124位等于7时，表示为自营国际配送运单
     * @param sendPay
     */
    public static boolean isInternationalWaybill(String sendPay){
        return isSignChar(sendPay,SendPayConstants.POSITION_124,SendPayConstants.CHAR_124_7);
    }

    /**
     * 判断是否是加盟商站点
     * @param siteType
     * @param subSiteType
     * @return
     */
    public static boolean isAllianceBusiSite(Integer siteType, Integer subSiteType) {
        if(siteType == null || subSiteType == null){
            return Boolean.FALSE;
        }
        return siteType == 16 && subSiteType == 88;
    }

    /**
     * 判断是否为正确的封箱号
     */
    public static boolean isSealBoxNo(String input){
        if (StringUtils.isEmpty(input)) {
            return Boolean.FALSE;
        }

       return input.matches(SEAL_BOX_NO);
    }

    /**
     * 判断是否是封车编码
     * @param input
     * @return
     */
    public static boolean isSealCarCode(String input) {
        if (StringUtils.isEmpty(input)) {
            return false;
        }
        return input.startsWith("SC") && input.length() == 16;
    }


    /**
     * 是否是鸡毛信运单
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isFeatherLetter(String waybillSign){
        return isSignInChars(waybillSign, WaybillSignConstants.POSITION_92, WaybillSignConstants.CHAR_92_2,WaybillSignConstants.CHAR_92_3);
    }

    /**
     * 是否是信任商家
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isTrustBusi(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_56,WaybillSignConstants.CHAR_56_1);
    }

    /**
     * 是否重货网运单
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isHeavyCargo(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_36, WaybillSignConstants.CHAR_36_4);
    }

    /**
     *  是否是【特快送 同城】
     *  waybill_sign第31位=1 且 116位=2，打印【特快送 同城】
     *  waybill_sign第31位=2，打印【特快送 同城】
     * @param waybillSign
     * @return EXPRESS_DELIVERY
     */
    public static boolean isExpressDeliverySameCity(String waybillSign){
        return (isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_1)
                    && isSignChar(waybillSign,WaybillSignConstants.POSITION_116,WaybillSignConstants.CHAR_116_2)
                )||isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_2);
    }

    /**
     *  是否是【特快送 次晨】
     *  waybill_sign第31位=1 且 116位=3 且 16位=4 ，打印【特快送 次晨】
     *  waybill_sign第31位=4 且 16位=4 ，打印【特快送 次晨】
     * @param waybillSign
     * @return
     */
    public static boolean isExpressDeliveryNextMorning(String waybillSign){
        return (isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_1)
                    && isSignChar(waybillSign,WaybillSignConstants.POSITION_116,WaybillSignConstants.CHAR_116_3)
                    && isSignChar(waybillSign,WaybillSignConstants.POSITION_16,WaybillSignConstants.CHAR_16_4)
                )||(isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_4)
                    && isSignChar(waybillSign,WaybillSignConstants.POSITION_16,WaybillSignConstants.CHAR_16_4));
    }

    /**
     *  是否是【特快送】
     *  waybill_sign第31位=1 且 116位=3 且 16位不等于4 ，打印【特快送】
     *  waybill_sign第31位=4 且 16位不等于4 ，打印【特快送】
     * @param waybillSign
     * @return
     */
    public static boolean isExpressDelivery(String waybillSign){
        return (isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_1)
                && isSignChar(waybillSign,WaybillSignConstants.POSITION_116,WaybillSignConstants.CHAR_116_3)
                && !isSignChar(waybillSign,WaybillSignConstants.POSITION_16,WaybillSignConstants.CHAR_16_4)
                )||(isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_4)
                && !isSignChar(waybillSign,WaybillSignConstants.POSITION_16,WaybillSignConstants.CHAR_16_4));
    }

    /**
     * 是否是同城
     * @param waybillSign
     * @return
     */
    public static boolean isSameCity(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_2);
    }

    /**
     * 是否是次晨
     * @param waybillSign
     * @return
     */
    public static boolean isNextMorning(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_3);
    }

    /**
     * 商家是否开通超长服务
     * @param traderSign
     * @return true 是，false 不是
     */
    public static boolean isOverLength(String traderSign){
        return isSignChar(traderSign, TraderSignConstants.POSITION_91, TraderSignConstants.CHAR_91_1);
    }

    /**
     * 是否是B2C纯配订单
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isB2CPureMatch(String waybillSign){
        return isSignInChars(waybillSign, WaybillSignConstants.POSITION_1,
                WaybillSignConstants.CHAR_1_3,WaybillSignConstants.CHAR_1_6,WaybillSignConstants.CHAR_1_9,WaybillSignConstants.CHAR_1_K,WaybillSignConstants.CHAR_1_Y)
                && isSignChar(waybillSign, WaybillSignConstants.POSITION_28, WaybillSignConstants.CHAR_28_0);
    }

    /**
     * 是否是B2C 28位等于0，且29位不等于8
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isB2C(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_28, WaybillSignConstants.CHAR_28_0)
                && !isSignChar(waybillSign, WaybillSignConstants.POSITION_29, WaybillSignConstants.CHAR_29_8);
    }

    /**
     * 是否是月结运单
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isMonthFinish(String waybillSign){
        return isSignInChars(waybillSign, WaybillSignConstants.POSITION_25,
                WaybillSignConstants.CHAR_25_0,WaybillSignConstants.CHAR_25_5);
    }
    /**
     * 是否是寄付现结
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isPrepaid(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_25,
                WaybillSignConstants.CHAR_25_1)||isSignChar(waybillSign, WaybillSignConstants.POSITION_25,
                WaybillSignConstants.CHAR_25_3) ;
    }
    /**
     * 是否是特惠送
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isPreferentialSend(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_0);
    }

    /**
     * 是否是次晨达
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isNextMorningArrived(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_4)
                || (isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                    && isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_3));
    }

    /**
     * 是否是同城当日达
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isSameCityArrived(String waybillSign){
        return (isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_2)
                && isSignChar(waybillSign, WaybillSignConstants.POSITION_16, WaybillSignConstants.CHAR_16_1))
                || (isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                && isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_2));
    }
    /**
     * 根据sendPay或者waybillSign判断是否无接触服务
     * @param sendPay
     * @param waybillSign
     * @return
     */
    public static boolean isNoTouchService(String sendPay,String waybillSign){
		return BusinessUtil.isSignInChars(sendPay,
					SendPayConstants.POSITION_295,
					SendPayConstants.CHAR_295_1,
					SendPayConstants.CHAR_295_2,
					SendPayConstants.CHAR_295_3,
					SendPayConstants.CHAR_295_4)
				|| BusinessUtil.isSignInChars(waybillSign,
						WaybillSignConstants.POSITION_33,
						WaybillSignConstants.CHAR_33_9,
						WaybillSignConstants.CHAR_33_A,
						WaybillSignConstants.CHAR_33_B,
						WaybillSignConstants.CHAR_33_C);
    }
    /**
     * 是否冷链卡班纯配
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isColdKBPureMatch(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_54, WaybillSignConstants.CHAR_54_2)
                && isSignChar(waybillSign, WaybillSignConstants.POSITION_80, WaybillSignConstants.CHAR_80_7)
                && isSignChar(waybillSign, WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_2);
    }
    /**
     * 是否冷链卡班仓配
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isColdKBWmsSend(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_54, WaybillSignConstants.CHAR_54_2)
                && isSignChar(waybillSign, WaybillSignConstants.POSITION_80, WaybillSignConstants.CHAR_80_7)
                && isSignChar(waybillSign, WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_3);
    }
    /**
     * 是否月结自提
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isMonthSelf(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_25, WaybillSignConstants.CHAR_25_0)
                && isSignChar(waybillSign, WaybillSignConstants.POSITION_79, WaybillSignConstants.CHAR_79_2);
    }
    /**
     * 是否经济网运单
     * @param waybillSign
     * @return true 是，false 不是
     */
    public static boolean isBusinessNet(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.BUSINESS_ENET_POSITION_62, WaybillSignConstants.BUSINESS_ENET_CHAR_62_8);
    }

    /**
     * 生鲜运单得标位：
     * WaybillSign 55位=1：“生鲜专送”；
     * WaybillSign 55位<>1且WaybillSign 31位=A：“生鲜特惠”；
     * WaybillSign 55位<>1且WaybillSign 31位=9，且waybillSign54位=2：“生鲜特快”
     * @param waybillSign
     * @return
     */
    public static boolean isFreshWaybill(String waybillSign) {
        /* 生鲜专送：55位为1 */
        boolean freshSpecialDelivery = isSignChar(waybillSign, WaybillSignConstants.POSITION_55, WaybillSignConstants.CHAR_55_1);
        if (freshSpecialDelivery) {
            return Boolean.TRUE;
        }

        /* 生鲜特惠：WaybillSign 55位<>1且WaybillSign 31位=A */
        boolean freshSpecialBenefit = isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_A);
        if (freshSpecialBenefit) {
            return Boolean.TRUE;
        }

        /* 生鲜特快：WaybillSign 55位<>1且WaybillSign 31位=9，且waybillSign54位=2 */
        boolean freshFastExpress = isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_9) &&
                isSignChar(waybillSign, WaybillSignConstants.POSITION_54, WaybillSignConstants.CHAR_54_2);

        return freshFastExpress? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * @Description
     * @param boxCode
     * @Author wyh
     * @Date 2020/2/21 14:07
     * @return java.lang.Boolean
     **/
    public static Boolean isWarmBoxCode(String boxCode) {
        if (StringUtils.isEmpty(boxCode)) {
            return Boolean.FALSE;
        }
        return DmsConstants.WARM_BOX_CODE_REGEX.matcher(boxCode.toUpperCase().trim()).matches() ||
                BIG_WARM_BOX_CODE_REGEX.matcher(boxCode.toUpperCase().trim()).matches();
    }

    /**
     * 获取条码类型逻辑
     * 判断范围包裹号，运单号，箱号，批次号
     *
     * @param barCode
     * @return null为未知条码
     */
    public static BarCodeType getBarCodeType(String barCode) {
        if (StringUtils.isBlank(barCode)) {
            return null;
        }
        if (BusinessUtil.isBoxcode(barCode)) {
            return BarCodeType.BOX_CODE;
        } else if (WaybillUtil.isPackageCode(barCode)) {
            return BarCodeType.PACKAGE_CODE;
        } else if (WaybillUtil.isWaybillCode(barCode)) {
            return BarCodeType.WAYBILL_CODE;
        } else if (BusinessUtil.isSendCode(barCode)) {
            return BarCodeType.SEND_CODE;
        } else if (BusinessUtil.isBoardCode(barCode)) {
            return BarCodeType.BOARD_CODE;
        } else {
            return null;
        }
    }


    /**
     * 根据sendPay判断是否预售,第297位等于1或2
     * @param sendPay
     * @return
     */
    public static boolean isPreSell(String sendPay) {
    	return isSignInChars(sendPay,SendPayConstants.POSITION_297,SendPayConstants.CHAR_297_1,SendPayConstants.CHAR_297_2);
    }
    /**
     * 根据sendPay判断是否预售未付款,第297位等于1
     * @param sendPay
     * @return
     */
    public static boolean isPreSellWithNoPay(String sendPay) {
        return isSignChar(sendPay,SendPayConstants.POSITION_297,SendPayConstants.CHAR_297_1);
    }
    /**
     * 预售未付款退仓,297位为1 且 228位为1或2
     * @param sendPay
     * @return
     */
    public static boolean isPreSellWithNoPayToWms(String sendPay) {
        return isSignChar(sendPay,SendPayConstants.POSITION_297,SendPayConstants.CHAR_297_1)
                && isSignInChars(sendPay,SendPayConstants.POSITION_228,SendPayConstants.CHAR_228_1,SendPayConstants.CHAR_228_2);
    }
    /**
     * 预售未付款暂存分拣,297位为1 且 228位为4
     * @param sendPay
     * @return
     */
    public static boolean isPreSellWithNoPayStorage(String sendPay) {
        return isSignChar(sendPay,SendPayConstants.POSITION_297,SendPayConstants.CHAR_297_1)
                && isSignChar(sendPay,SendPayConstants.POSITION_228,SendPayConstants.CHAR_228_4);
    }
    /**
     * 根据sendPay判断是否预售已付款,第297位等于2
     * @param sendPay
     * @return
     */
    public static boolean isPreSellWithPay(String sendPay) {
    	return isSignChar(sendPay,SendPayConstants.POSITION_297,SendPayConstants.CHAR_297_2);
    }
    /**
     * 航空转陆运
     * waybillsign第31位等于1或者84位等于3，目前用于航空转陆运通知质控和路由，如果需要确定使用请与产品确定标位
     *
     * @param waybillSign
     * @return
     */
    public static Boolean isArTransportMode(String waybillSign) {
        return BusinessUtil.isSignY(waybillSign, 31) || BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_84, WaybillSignConstants.CHAR_84_3);
    }

    /**
     * 航空件
     * @param waybillSign 运单标位
     * @return 判断结果标识
     */
    public static Boolean isAirLineMode(String waybillSign) {
        return BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_84, WaybillSignConstants.CHAR_84_3);
    }

    /**
     * 京航达运单
     *  sendPay第137位为1
     * @param sendPay
     * @return
     */
    public static boolean isJHD(String sendPay) {
        return BusinessUtil.isSignY(sendPay, 137);
    }

    /**
     * 根据标位判断是否企配仓数据sendpay标识314=1 或者 waybillSign128=1
     * @param sendPay
     * @param waybillSign
     * @return
     */
	public static boolean isEdn(String sendPay, String waybillSign) {
		return BusinessUtil.isSignInChars(sendPay, SendPayConstants.POSITION_314, SendPayConstants.CHAR_314_1)
				|| BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_128, WaybillSignConstants.CHAR_128_1);
	}
	/**
	 * 根据子类型判断是否企配仓类型
	 * @param subType
	 * @return
	 */
	public static boolean isEdnDmsSite(Integer subType) {
		return SITE_SUB_TYPE_EDN.equals(subType);
	}

    /**
     * 是否集配站点
     *
     * @param subType
     * @return
     */
    public static boolean isJPSite(Integer subType) {
        return Objects.equals(subType, 9605);
    }

    /**
     * 是否城配站点
     *
     * @param subType
     * @return
     */
    public static boolean isCPSite(Integer subType) {
        return Objects.equals(subType, 9607);
    }

    /**
     * 是否外单自提点
     *  C网 waybillsign第40位=0
     *  订单类型 SOP Waybillsign第1位=2
     * 且79位=2，且23位≠5、6、7，面单打印“提”字
     * @return
     */
    public static boolean isZiTiByWaybillSign(String waybillSign) {
        /* C网 waybillsign第40位=0
         * 订单类型 SOP Waybillsign第1位=2
         */
        boolean bool = BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_1, WaybillSignConstants.CHAR_1_2) ||
                BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_0);
        return bool &&
                BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_79, WaybillSignConstants.CHAR_79_2) &&
                !BusinessUtil.isSignInChars(waybillSign, WaybillSignConstants.POSITION_23,WaybillSignConstants.CHAR_23_5, WaybillSignConstants.CHAR_23_6, WaybillSignConstants.CHAR_23_7);
    }

    /**
     * 是否自提柜
     *  C网 waybillsign第40位=0
     *  订单类型 SOP Waybillsign第1位=2
     * 且79位=2，且23=5、6，面单打印【柜】字
     * @param waybillSign
     * @return
     */
    public static boolean isZiTiGuiByWaybillSign(String waybillSign) {
        /* C网 waybillsign第40位=0
         * 订单类型 SOP Waybillsign第1位=2
         */
        boolean bool = BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_1, WaybillSignConstants.CHAR_1_2) ||
                BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_0);
        return bool &&
                BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_79, WaybillSignConstants.CHAR_79_2) &&
                BusinessUtil.isSignInChars(waybillSign, WaybillSignConstants.POSITION_23,WaybillSignConstants.CHAR_23_5, WaybillSignConstants.CHAR_23_6);
    }

    /**
     * 是否自提店
     *  C网 waybillsign第40位=0
     *  订单类型 SOP Waybillsign第1位=2
     * 且79位=2，且23=7，面单打印【店】字
     * @param waybillSign
     * @return
     */
    public static boolean isZiTiDianByWaybillSign(String waybillSign) {
        /* C网 waybillsign第40位=0
         * 订单类型 SOP Waybillsign第1位=2
         */
        boolean bool = BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_1, WaybillSignConstants.CHAR_1_2) ||
                BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_0);
        return bool &&
                BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_79, WaybillSignConstants.CHAR_79_2) &&
                BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_23, WaybillSignConstants.CHAR_23_7);
    }

    /**
     * 判断是否函速达运单，waybill_sign第31位等于B时
     *
     * @param waybillSign
     * @return
     */
    public static boolean isLetterExpress(String waybillSign) {
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_B);
    }

    /**
     * 判断是否是集包袋编号
     * @param materialCode
     * @return
     */
    public static boolean isCollectionBag(String materialCode) {
        if (StringUtils.isBlank(materialCode)) {
            return false;
        }
        return (DmsConstants.RULE_CYCLE_BOX_REGEX.matcher(materialCode.trim().toUpperCase()).matches()) ||
                (materialCode.toUpperCase().startsWith(COLLECTION_AY_PREFIX) && materialCode.length() == 15);
    }
    /**
     * 判断是否无人车配送，sendpay第307位=1
     *
     * @param sendPay
     * @return
     */
    public static boolean isWrcps(String sendPay) {
        return isSignChar(sendPay, SendPayConstants.POSITION_307, SendPayConstants.CHAR_307_1);
    }

    /**
     * 判断站点是否为分拣中心类型
     * @param siteType 站点类型
     * @return boolean
     */
    public static boolean isSortingSiteType(Integer siteType) {
        if(null == siteType){
            return true;
        }
        if(SORTING_SITE_TYPE_LIST.contains(siteType)) {
            return true;
        }
        return false;
    }


    /**
     * 经济网需要拦截的运单范围
     * 防止少拦截运单，采用反向抛出法判断，优先筛选不拦截类型运单
     * @param waybillSign
     * @return true 需要拦截判断
     *          false 不需要拦截判断
     */
    public static boolean isEconomicNetValidateWeightVolume(String waybillCode,String waybillSign) {
        //非经济网运单不拦截
        if(!WaybillCodePattern.ENOCOMIC_WAYBILL_CODE.equals(
                UniformValidateUtil.getSpecificWaybillCodePattern(waybillCode))){
            return false;
        }
        //逆向不拦截
        if (!BusinessUtil.isSignChar(waybillSign, 61, '0')) {
            return false;
        }
        //不拦截 售后取件、合约返单等业务层面逆向单
        if (!BusinessUtil.isSignChar(waybillSign, 15, '0')) {
            return false;
        }
        return true;
    }
    /**
     * 隐藏手机号：7位以上手机号返回前3位+^_^+后四位，否则返回原值
     * @param phone 原手机号
     * @return
     */
    public static String getHidePhone(String phone) {
        return getHidePhone(phone,HIDE_SMILE);
    }
    /**
     * 隐藏手机号：7位以上手机号返回前3位+hideStr+后四位，否则返回原值
     * @param phone 原手机号
     * @param hideStr 隐藏后替换字符串，传值为空时默认^_^
     * @return
     */
    public static String getHidePhone(String phone,String hideStr) {
        if(StringUtils.isNotBlank(phone)){
        	String hidePlaceStr = hideStr;
        	if(StringUtils.isBlank(hidePlaceStr)){
        		hidePlaceStr = HIDE_SMILE;
        	}
            //去除号码中间的空白字符
        	String hidePhone = phone.replaceAll("\\s*", "");
            if(hidePhone.length() >= PHONE_LEAST_NUMBER ){
                return hidePhone.substring(0,PHONE_FIRST_NUMBER)
                		+ hidePlaceStr
                		+ hidePhone.substring(hidePhone.length() - PHONE_HIGHLIGHT_NUMBER);
            }
        }
        return phone;
    }
    /**
     * 隐藏姓名：1位以上地址返回前1位+^_^，否则返回原值
     * @param name 姓名
     * @return
     */
    public static String getHideName(String name) {
        if(StringUtils.isNotBlank(name)
        		&& name.length() >= NAME_SHOW_LENGTH){
            //保留前1位
        	return name.substring(0,NAME_SHOW_LENGTH) + HIDE_SMILE;
        }
        return getHideStr(name,NAME_SHOW_LENGTH,HIDE_SMILE);
    }
    /**
     * 隐藏地址：9位以上地址返回前9位+^_^，否则返回原值
     * @param address
     * @return
     */
    public static String getHideAddress(String address) {
        if(StringUtils.isNotBlank(address)
        		&& address.length() >= ADDRESS_SHOW_LENGTH){
            //保留前9位
        	return address.substring(0,ADDRESS_SHOW_LENGTH) + HIDE_SMILE;
        }
        return getHideStr(address,ADDRESS_SHOW_LENGTH,HIDE_SMILE);
    }
    /**
     * 隐藏处理：显示前showLength位，后几位用hideStr替换，否则返回原值
     * @param str 原字符串
     * @param showLength 显示长度
     * @param hideStr 隐藏后替换字符串，传值为空时默认^_^
     * @return
     */
    public static String getHideStr(String str,int showLength,String hideStr) {
        if(StringUtils.isNotBlank(str)
        		&& showLength > 0
        		&& str.length() >= showLength){
        	String hidePlaceStr = hideStr;
        	if(StringUtils.isBlank(hidePlaceStr)){
        		hidePlaceStr = HIDE_SMILE;
        	}
            //保留前几位
        	return str.substring(0,showLength) + hidePlaceStr;
        }
        return str;
    }
    /**
     * 合约机判断：Sendpay292位为1
     * @param sendPay
     * @return
     */
	public static boolean isContractPhone(String sendPay) {
		return isSignChar(sendPay, SendPayConstants.POSITION_292, SendPayConstants.CHAR_292_1);
	}
	/**
	 * 判断是否签单返回，waybillSign第4位：1,2,3,4,9
	 * @param waybillSign
	 * @return
	 */
	public static boolean isSignBack(String waybillSign){
		return BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_4,
				WaybillSignConstants.CHAR_4_1,WaybillSignConstants.CHAR_4_2,WaybillSignConstants.CHAR_4_3,WaybillSignConstants.CHAR_4_4,WaybillSignConstants.CHAR_4_9);
	}

    /**
     * 验证车牌号合法性
     */
    public static boolean isMatchCarLicenseNo(String input){
        if (StringUtils.isEmpty(input)){
            return false;
        }
        return Pattern.compile(DmsConstants.NUMBERPLATE_CODE).matcher(input).matches();
    }

    /**
     * 特定开头的箱号
     * @param boxCode
     * @param codePrefix
     * @return
     */
    public static boolean boxCodeMatchPrefix(String boxCode, String codePrefix) {
        if (StringUtils.isBlank(boxCode) || StringUtils.isBlank(codePrefix)) {
            return false;
        }
        return isBoxcode(boxCode) && boxCode.trim().toUpperCase().startsWith(codePrefix);
    }

    /**
     *  判断是否为C 网
     *  40 位为0:C网,   非0:B网
     */
    public static  boolean isCInternet(String waybillSign){
        if(BusinessUtil.isSignChar(waybillSign, 40, '0')){
            return true;
        }
        return  false;
    }

    /**
     * 判断是否B网（抽检专用）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isBInternet(String waybillSign) {
        return isSignInChars(waybillSign, WaybillSignConstants.POSITION_40, '1', '2', '3')
                && !isSignInChars(waybillSign, WaybillSignConstants.POSITION_80, '6', '7', '8')
                && !isSignInChars(waybillSign, WaybillSignConstants.POSITION_89, '1', '2')
                && !isSignChar(waybillSign, WaybillSignConstants.POSITION_99, '1');
    }

    /**
     * 判断是否是快运
     * 31位 为1 是特快送
     * @param waybillSign
     * @return
     */
    public static boolean isExpress(String waybillSign){
        if(StringUtils.isEmpty(waybillSign)){
            return  false;
        }
        return BusinessUtil.isSignChar(waybillSign,31,'1');
    }

    /**
     * 判断是否防疫物资绿色通道(82位6)
     *
     * @param waybillSign
     * @return
     */
    public static boolean isFYWZ(String waybillSign) {
        return isSignChar(waybillSign, 82, '6');
    }
    /**
     * 判断包裹维度是否有增值服务信息，waybillSign86位=2或者3 去获取包裹的
     * @param waybillSign
     * @return
     */
    public static boolean isPackageHavePickUpOrNo(String waybillSign){
        return BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_86,
                WaybillSignConstants.CHAR_86_2,WaybillSignConstants.CHAR_86_3);
    }
    /**
     * 判断运单维度是否有增值服务信息，waybillSign86位=1或者3
     * @param waybillSign
     * @return
     */
    public static boolean hasWaybillVas(String waybillSign){
        return BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_86,
                WaybillSignConstants.CHAR_86_1,WaybillSignConstants.CHAR_86_3);
    }
    /**
     * 判断是否是返单
     */
    public static boolean isRefund(String waybillSign) {
        return isSignChar(waybillSign, 1, '7');
    }

    /**
     * 判断是否需要打印包裹维度商品名称信息，waybillSign66位=3 去获取包裹的
     * @param waybillSign
     * @return
     */
    public static boolean isKaPackageOrNo(String waybillSign){
        boolean isSignChars = BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_66, WaybillSignConstants.CHAR_66_3) ;
        return isSignChars;
    }

    /**
     * 根据waybillSign判断是否需要打印包裹维度商品名称信息，waybillSign66位=3 或者2 包裹标签需要打印商品名称信息。
     * @param waybillSign
     * @return
     */
    public static boolean needPrintPackageName(String waybillSign){
        boolean isSignChars = BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_66, WaybillSignConstants.CHAR_66_3) ||
                BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_66, WaybillSignConstants.CHAR_66_2);
        return isSignChars;
    }

    /**
     * 根据waybillSign判断是否必须称重量方,waybillSign66位=3 必须称重量方
     * @param waybillSign
     * @return
     */
    public static boolean needWeighingSquare(String waybillSign){
        boolean isSignChars = BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_66, WaybillSignConstants.CHAR_66_3);
        return isSignChars;
    }

    /**
     * 当 WaybillSign40=2且 WaybillSign1≠7时，则查运单接口
     * @param waybillSign
     * @return
     */
    public static boolean isNeedCheckWeightOrNo(String waybillSign){
        return isSignInChars(waybillSign,WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_2)
                    && !isSignInChars(waybillSign,WaybillSignConstants.POSITION_1,WaybillSignConstants.CHAR_1_7);

    }

    /**
     * 校验是否需要校验重量(装卸车)
     * @param waybillSign
     * @return
     */
    public static boolean isNeedCheckWeightBusiness2OrNo(String waybillSign){
        return isSignInChars(waybillSign,WaybillSignConstants.REPLACE_ORDER_POSITION_62,WaybillSignConstants.REPLACE_ORDER_CHAR_62_1)
                && isSignInChars(waybillSign,WaybillSignConstants.POSITION_25,WaybillSignConstants.CHAR_25_4)
                && !isSignInChars(waybillSign,WaybillSignConstants.POSITION_1,WaybillSignConstants.CHAR_1_7);

    }


    /**
     * 判断是否支持按包裹维度批量导入-当WaybillSign66=0或1时，不支持
     * @param waybillSign
     * @return
     */
    public static boolean isNotSupportUpWeightByPackage(String waybillSign){
        return BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_66,
                WaybillSignConstants.CHAR_66_1, WaybillSignConstants.CHAR_66_0);
    }


    /**
     * 非B2B运单
     * @param sendPay
     * @return
     */
    public static boolean isNotB2B(String sendPay) {
        return BusinessUtil.isSignInChars(sendPay, SendPayConstants.POSITION_315, SendPayConstants.CHAR_315_0);
    }



    /**
     * 根据sendPay表位判断预售暂存类型
     * 如果sendPay 228位等于1或2，表示预售暂存到仓
     * 如果sendPay 228位等于4或5，表示预售暂存到配
     */
    public static Integer getStoreTypeBySendPay(String sendPay){
        Integer result = null;
        if (BusinessUtil.isSignChar(sendPay,SendPayConstants.POSITION_228,SendPayConstants.CHAR_228_1) ||
                BusinessUtil.isSignChar(sendPay,SendPayConstants.POSITION_228,SendPayConstants.CHAR_228_2)){
            result = PreSellTypeEnum.TOWAREHOUSE.getValue();
        }
        if (BusinessUtil.isSignChar(sendPay,SendPayConstants.POSITION_228,SendPayConstants.CHAR_228_4) ||
                BusinessUtil.isSignChar(sendPay,SendPayConstants.POSITION_228,SendPayConstants.CHAR_228_5)){
            result = PreSellTypeEnum.TODELIVERY.getValue();
        }
        return result;
    }

    //预售到仓且未付尾款
    public static boolean preSellAndUnpaidBalance(String sendPay){
        return (isSignChar(sendPay, SendPayConstants.POSITION_228, SendPayConstants.CHAR_228_1) ||
                isSignChar(sendPay, SendPayConstants.POSITION_228, SendPayConstants.CHAR_228_2)
                ) &&
                isSignChar(sendPay, SendPayConstants.POSITION_297, SendPayConstants.CHAR_297_1)
                ?Boolean.TRUE:Boolean.FALSE;
    }

    /**
     * 是否专网标识
     * @param waybillSign
     * @return
     */
    public static boolean isPrivateNetwork(String waybillSign){
        if(StringUtils.isEmpty(waybillSign)){
            return  false;
        }
        return BusinessUtil.isSignChar(waybillSign,135,'2');
    }

    /**
     * 是否生鲜
     *  生鲜特快：31 = 9
     *  生鲜特惠：31 = A
     * @param waybillSign
     * @return
     */
    public static boolean isFresh(String waybillSign) {
        return BusinessUtil.isSignInChars(waybillSign, WaybillSignConstants.POSITION_31,
                WaybillSignConstants.CHAR_31_9,WaybillSignConstants.CHAR_31_A);
    }

    /**
     * 根据waybillSign判断是否自提(waybillSign 79位等于2)
     *
     * @param waybillSign
     * @return
     */
    public static boolean isPickUpOrNo(String waybillSign) {
        return isSignInChars(waybillSign, WaybillSignConstants.POSITION_79, WaybillSignConstants.CHAR_79_2);
    }
    /**
     * 是否修改订单地址,waybillSign第8位1、2
     *
     * @param waybillSign
     * @return
     */
    public static boolean isChangeWaybillSign(String waybillSign) {
        return BusinessUtil.isSignInChars(waybillSign, WaybillSignConstants.POSITION_8,
                WaybillSignConstants.CHAR_8_1,WaybillSignConstants.CHAR_8_2);
    }
    /**
     * 是否修改订单地址,waybillSign第8位1、2
     *
     * @param waybillSign
     * @return
     */
    public static boolean isForceChangeWaybillSign(String waybillSign) {
        return BusinessUtil.isSignInChars(waybillSign, WaybillSignConstants.POSITION_8,
                WaybillSignConstants.CHAR_8_1);
    }

    /**
     *
     *  寄件人信息 非逆向运单 （waybillSign61位=0）
     * @param waybillSign
     * @return
     */
    public static boolean isJDConsigner(String waybillSign) {
        return BusinessUtil.isSignInChars(waybillSign, WaybillSignConstants.POSITION_61,
                WaybillSignConstants.CHAR_61_0);
    }


    /**
     * 是否修改订单地址,waybillSign第8位1、2
     *
     * @param waybillSign
     * @return
     */
    public static boolean isWeakChangeWaybillSign(String waybillSign) {
        return BusinessUtil.isSignInChars(waybillSign, WaybillSignConstants.POSITION_8,
                WaybillSignConstants.CHAR_8_2);
    }
    /**
     * 运单揽收类型为网点自送 71位是2 网点自提
     * @param waybillSign
     * @return
     */
    public static boolean isWdzsOrNo(String waybillSign) {
        return isSignInChars(waybillSign, WaybillSignConstants.POSITION_71, WaybillSignConstants.CHAR_71_2);
    }
    /**
     * 判断是否退分拣waybill_sign第18位=C
     * @param waybillSign
     * @return
     */
	public static boolean isScrapSortingSite(String waybillSign) {
		return BusinessUtil.isSignChar(waybillSign,WaybillSignConstants.POSITION_18,WaybillSignConstants.CHAR_18_C);
	}
    /**
     * 判断生鲜-SendPay第2位等于4或5或6或7或8或9
     * @param waybillSign
     * @return
     */
	public static boolean isSx(String sendPay) {
		return BusinessUtil.isSignInChars(sendPay,SendPayConstants.POSITION_2,
				SendPayConstants.CHAR_2_4,
				SendPayConstants.CHAR_2_5,
				SendPayConstants.CHAR_2_6,
				SendPayConstants.CHAR_2_7,
				SendPayConstants.CHAR_2_8,
				SendPayConstants.CHAR_2_9);
	}
	/**
	 * 生成bdBlockerCompleteMQ消息信息
	 * @param waybillCode
	 * @param orderType
	 * @param messageType
	 * @param operateTime
	 * @return
	 */
    public static String bdBlockerCompleteMQ(String waybillCode, String orderType, String messageType, String operateTime) {
        StringBuilder message = new StringBuilder();
        message.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
        message.append("<OrderTaskInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        message.append("<OrderId>" + waybillCode + "</OrderId>");
        message.append("<OrderType>"+orderType+"</OrderType>");
        message.append("<MessageType>"+messageType+"</MessageType>");
        message.append("<OperatTime>" + operateTime + "</OperatTime>");
        message.append("</OrderTaskInfo>");

        return message.toString();
    }
    /**
     * 校验该运单是否为航空单（WaybillSign31位=1【特快送】或WaybillSign84位=3【干线运输模式为航空】或Sendpay137位=1【京航达】）
     * @param waybillSign
     * @param sendPay
     * @return
     */
	public static boolean checkCanAirToRoad(String waybillSign, String sendPay) {
		return isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_1)
		     ||isSignChar(waybillSign,WaybillSignConstants.POSITION_84,WaybillSignConstants.CHAR_84_3)
		     ||isSignChar(sendPay,SendPayConstants.POSITION_137,SendPayConstants.CHAR_137_1);
	}
    /**
     * waybill_sign 29位=2  且  53位=1
     *
     * ECLP 仓配
     * @param waybillSign
     * @return
     */
    public static boolean isEclpAndWmsForDistribution(String waybillSign) {
        return isSignInChars(waybillSign, WaybillSignConstants.POSITION_29, WaybillSignConstants.CHAR_29_2)
                && isSignInChars(waybillSign, WaybillSignConstants.POSITION_53, WaybillSignConstants.CHAR_53_1);

    }
    /**
     * 验证工种
     * @param jobCodeStr
     * @return
     */
    public static boolean isJobCode(String jobCodeStr){
    	if(jobCodeStr == null
    			|| jobCodeStr.length() == 0) {
    		return false;
    	}
    	return jobCodeStr.matches(JOB_TYPE_REGEX);
    }
    /**
     * 判断是否人员三定条码,一位数字
     * @param scanUserCode
     * @return
     */
    public static boolean isScanUserCode(String scanUserCode) {
    	if(scanUserCode == null
    			|| scanUserCode.length() < 2) {
    		return false;
    	}
    	return isJobCode(scanUserCode.substring(0,1));
    }
    /**
     * 判断是否人员三定条码,返回用户编码，否则返回null
     * @param scanUserCode
     * @return
     */
    public static String getUserCodeFromScanUserCode(String scanUserCode) {
    	if(!isScanUserCode(scanUserCode)) {
    		return null;
    	}
    	return scanUserCode.substring(1);
    }
    /**
     * 判断是否人员三定条码,返回工种类型，否则返回null
     * @param scanUserCode
     * @return
     */
    public static Integer getJobCodeFromScanUserCode(String scanUserCode) {
    	if(!isScanUserCode(scanUserCode)) {
    		return null;
    	}
    	return Integer.parseInt(scanUserCode.substring(0,1));
    }
    /**
     * 判断是否是身份证号
     * @param userCode
     * @return
     */
    public static boolean isIdCardNo(String userCode) {
    	if(userCode == null) {
    		return false;
    	}
    	return userCode.matches(ID_CARD_NO_REGEX);
    }
    /**
     * 隐藏身份证号：是身份证，返回加密身份证号，第8位至15位显示为星号，否则返回原值
     * @param idCard
     * @return
     */
    public static String encryptIdCard(String idCard) {
    	if(!isIdCardNo(idCard)) {
    		return idCard;
    	}
    	return idCard.replaceAll("(\\w{4})\\w*(\\w{4})", "$1***$2");
    }
    /**
     * APP版本大小比较
     * @param appVersion 当前版本
     * @param newestVersion 配置的最新版本
     * @return true：需要升级
     */
    public static boolean appVersionCompare(String appVersion, String newestVersion) {
        if (StringUtils.isBlank(appVersion) || StringUtils.isBlank(newestVersion)) {
            return false;
        }

        Matcher curVersionMatcher = APP_VERSION_REGEX.matcher(appVersion);
        Matcher newestVersionMatcher = APP_VERSION_REGEX.matcher(newestVersion);
        if (!curVersionMatcher.matches() || !newestVersionMatcher.matches()) {
            return false;
        }

        String[] versionArr = appVersion.split("\\.");
        String[] newestVerArr = newestVersion.split("\\.");

        int minDigit = Math.min(versionArr.length, newestVerArr.length);

        for (int i = 0; i < minDigit; i++) {
            if (Integer.parseInt(versionArr[i]) < Integer.parseInt(newestVerArr[i])) {
                return true;
            }
            else if (Integer.parseInt(versionArr[i]) > Integer.parseInt(newestVerArr[i])){
                return false;
            }
        }

        return versionArr.length < newestVerArr.length;
    }

    /**
     * 是否医药冷链产品（精温送）
     * @param waybillSign
     * @return
     */
    public static boolean isMedicalFreshProductType(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_D);
    }

    /**
     * 是否自营逆向单
     *
     * @param waybillSign
     * @return
     */
    public static boolean isSelfReverse(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_1, WaybillSignConstants.CHAR_1_T);
    }

    /**
     * 是否是外单仓配业务
     * @param waybillSign
     * @return
     */
    public static boolean isWarehouseAndDistributionBusiness(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_53, WaybillSignConstants.CHAR_1);
    }
    /**
     * 判断是否需要抽检
     * @param tagSign
     * @return
     */
    public static boolean needSpotCheck(String tagSign){
    	return BusinessUtil.isSignInChars(tagSign, JyUnloadTaskSignConstants.POSITION_1,JyUnloadTaskSignConstants.CHAR_1_1,JyUnloadTaskSignConstants.CHAR_1_2);
    }

    /**
     * 自营逆向单（waybill_sign第一位=T），且为全球购订单（sendpay第8位 = 6）
     */
    public static boolean isReverseGlobalWaybill(String waybillSign, String sendPay){
        return isSelfReverse(waybillSign) && isGlobalPurchaseWaybill(sendPay);
    }

    /**
     * 是否全球购订单（sendpay第8位 = 6）
     */
    public static boolean isGlobalPurchaseWaybill(String sendPay){
        return isSignChar(sendPay, SendPayConstants.POSITION_8, SendPayConstants.CHAR_6);
    }

    public static void main(String[] args) {
        System.out.println(BusinessUtil.isCollectionBag("AD12345678901234"));
        System.out.println(BusinessUtil.isCollectionBag("ADAD123456789012"));
        System.out.println(BusinessUtil.isCollectionBag("ADAC123456789012"));
        System.out.println(BusinessUtil.isCollectionBag("ADAD1234567890123"));
        System.out.println(BusinessUtil.isCollectionBag("ADAD12345678901C"));
        System.out.println(BusinessUtil.isCollectionBag("AD1234567890123C"));
    }

    public static boolean isTaskSimpleCode(String simpleCode) {
        if (StringUtils.isBlank(simpleCode)) {
            return false;
        }
        return WORKITEM_SIMPLECODE_REGEX.matcher(simpleCode).matches() ;
    }

  public static boolean isCarCode(String carCode) {
    if (StringUtils.isBlank(carCode)) {
      return false;
    }
    return CARCODE_REGEX.matcher(carCode).matches() ;
  }

    /**
     * 判断是否是快运运单
     *
     * @param waybillSign
     * @return
     */
    public static boolean isKyWaybill(String waybillSign){
        if (waybillSign == null){
            return false;
        }
        return BusinessUtil.isSignChar(waybillSign,40,'2')
                && BusinessUtil.isSignChar(waybillSign,54,'0')
                && BusinessUtil.isSignInChars(waybillSign,80,'0', '1', '2', '9')
                && BusinessUtil.isSignChar(waybillSign,89,'0');
    }

    /**
     * 判断是否是快运改址拦截的运单
     *
     * @param waybillSign
     * @return
     */
    public static boolean isKyAddressModifyWaybill(String waybillSign){
        if (waybillSign == null){
            return false;
        }
        return isKyWaybill(waybillSign)
                && BusinessUtil.isSignInChars(waybillSign,103,'2', '3');
    }
    /**
     * 通过运单标识 判断是否需求称重
     * <p>
     * 66 位 是0  标识可以称重
     *
     * @param waybillSign
     * @return
     */
    public static boolean isAllowWeight(String waybillSign) {
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_66, WaybillSignConstants.CHAR_66_0);
    }
    /**
     *
     * @param waybillCode
     * @param sourceCode
     * @param sendPay
     * @return
     */
    public static boolean isDouyin(String waybillCode,String sourceCode,String sendPay){
    	return DmsConstants.SOURCE_CODE_DOUYIN.equals(sourceCode)
    			|| (waybillCode != null && waybillCode.startsWith(DmsConstants.WAYBILL_CODE_PRE_DOUYIN))
    			|| BusinessUtil.isSignChar(sendPay, SendPayConstants.POSITION_327,SendPayConstants.CHAR_327_2)
    	;

    }

    /**
     * 根据waybillSign 判断此运单是否包含增值服务 是： true  不是：false
     * @param waybillSign
     * @return
     */
    public static boolean isVasWaybill(String waybillSign){
        return !isSignChar(waybillSign,WaybillSignConstants.POSITION_86,WaybillSignConstants.CHAR_86_0);
    }
    /**
     * 是否特快送
     * @param waybillSign
     * @return
     */
    public static boolean isTKS(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_1);
    }

    /**
     * 航空填仓  WaybillSign67位=1
     * @param waybillSign
     * @return
     */
    public static boolean isAirFill(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_67,WaybillSignConstants.CHAR_67_1);
    }

    /**
     * 纯配(53=2)&&冷链生鲜单子
     * 冷链卡班、冷链卡班小票、冷链城配、冷链专送
     * 冷链卡班和冷链小票（WBS54位=2&&80位=7）、冷链城配（wbs54位=2&&80位=6）、冷链专送（wbs54位=2&&31位=G）
     * @param waybillSign
     * @return
     */
    public static boolean isExternalPureDeliveryAndColdFresh(String waybillSign){
        if(!isSignInChars(waybillSign,53,'0', '2')){
            return false;
        }
        if(!isColdChainWaybill(waybillSign)){
            return false;
        }
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_80,WaybillSignConstants.CHAR_80_7)
                || isSignChar(waybillSign,WaybillSignConstants.POSITION_80,WaybillSignConstants.CHAR_80_6)
                || isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_G);
    }

    /**
     *  自营生鲜 新逻辑
     * sendpay第338位为1（且sendpay第2位为4或5或6或7或8或9）
     */
    public static boolean isSelfSX(String sendPay){
        if(StringUtils.isBlank(sendPay)){
            return false;
        }
       return isSignChar(sendPay,SendPayConstants.POSITION_338,SendPayConstants.POSITION_338_1) &&  isSx(sendPay);
    }

    /**
     *  外单生鲜 新逻辑
     *  waybillsign31位为9或A
     * @param waybillSign
     * @return
     */
    public static boolean isNotSelfSX(String waybillSign){
        if(StringUtils.isBlank(waybillSign)){
            return false;
        }
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_9)
                || isSignChar(waybillSign,WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_A);
    }
    /**
     * 判断是否-场地网格key
     * @param businessKey
     * @return
     */
    public static boolean isWorkGridKey(String businessKey){
        if(StringUtils.isBlank(businessKey)){
            return false;
        }
        return businessKey.startsWith(DmsConstants.CODE_PREFIX_WORK_GRID);
    }
    /**
     * 判断是否-场地网格工序key
     * @param businessKey
     * @return
     */
    public static boolean isWorkStationGridKey(String businessKey){
        if(StringUtils.isBlank(businessKey)){
            return false;
        }
        return businessKey.startsWith(DmsConstants.CODE_PREFIX_WORK_STATION_GRID);
    }

    /**
     * 判断周转筐型号
     * 通过第13位判断周转筐型号
     * 1,2,3,对应小型 4对应大型
     * @param code
     * @return
     */
    public static RecycleBasketTypeEnum getRecycleBasketType(String code) {
        if (!StringUtils.isEmpty(code) && isMatchBoxCode(code)) {
            Integer type = Integer.valueOf(code.substring(12,13));
            if (SMALL_RECYCLE_BASKET_TYPE.contains(type)) {
                return RecycleBasketTypeEnum.SMALL;
            }
            if (RecycleBasketTypeEnum.BIG.getCode().equals(type)){
                return RecycleBasketTypeEnum.BIG;
            }
        }
        return null;
    }
    
}
