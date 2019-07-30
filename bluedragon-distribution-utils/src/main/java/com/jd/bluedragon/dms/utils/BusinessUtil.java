package com.jd.bluedragon.dms.utils;

import com.jd.etms.waybill.util.WaybillCodeRuleValidateUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

import static com.jd.bluedragon.dms.utils.DmsConstants.*;

/**
 * @author tangchunqing
 * @Description: 业务相关判断
 * @date 2018年10月12日 18时:15分
 */
public class BusinessUtil {

    /**
     * 提取发货批次号中站点正则
     */
    private static final Pattern RULE_SEND_CODE_SITE_CODE_REGEX = Pattern.compile(AO_SEND_CODE_REG);

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
        return sendCode.matches(SEND_CODE_REG) || sendCode.matches(AO_SEND_CODE_REG) || isSingleBatchNo(sendCode);
    }

  /**
   * 是否为新批次号
   * 批次号判断批次号是否是：站点（数字）+站点（数字）+时间串（14位数字）+序号（2位数字）+模7余数
   * 模7余数：对 站点第一位+站点第一位+时间串+序列号 取模
   * 必须是17位（时间14位+序号2位+模7余数1位）
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

    /**
     * 判断是否为箱号
     *
     * @param boxCode 待验证字符
     * @return
     */
    private static boolean isMatchBoxCode(String boxCode) {
        return DmsConstants.RULE_BOXCODE_REGEX_OLD.matcher(boxCode.trim().toUpperCase()).matches()
                || DmsConstants.RULE_BOXCODE_REGEX.matcher(boxCode.trim().toUpperCase()).matches();
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
        if (isSignChar(waybillSign, 31, '1')) {
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
    public static boolean isBizSite(String sReceiveSiteType) {

        Integer receiveSiteType;
        try {
            receiveSiteType = Integer.parseInt(sReceiveSiteType);
        } catch (Exception e) {
            return Boolean.FALSE;
        }

        if (DmsConstants.SITE_TYPE_BIZ.equals(receiveSiteType)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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
     * 纯配外单判断 【waybillSign第1为为2、3、6、9、K、Y且第53位为2】
     * */
    public static Boolean isPurematch(String waybillSign){
        if(waybillSign == null){
            return Boolean.FALSE;
        }
        if(isSignChar(waybillSign,53,'2')
                && isSignInChars(waybillSign,1,'2','3','6','9','K','Y')){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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
     * 判断是否是B网冷链运单
     * @param waybillSign
     * @return
     */
    public static Boolean isColdChainWaybill(String waybillSign){
        return isSignChar(waybillSign,WaybillSignConstants.POSITION_54,WaybillSignConstants.CHAR_54_2);
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
     *
     * @param waybillSign
     * @return
     */
    public static boolean isTc(String waybillSign) {
    	return isSignInChars(waybillSign,WaybillSignConstants.POSITION_89,
    			WaybillSignConstants.CHAR_89_1,WaybillSignConstants.CHAR_89_2);
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
        if(!isSendCode(sendCode)){
            return null;
        }
        Matcher matcher = RULE_SEND_CODE_SITE_CODE_REGEX.matcher(sendCode.trim());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(2));
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
        if(!isSendCode(sendCode)){
            return null;
        }
        Matcher matcher = RULE_SEND_CODE_SITE_CODE_REGEX.matcher(sendCode.trim());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
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
        return isSignChar(waybillSign,WaybillSignConstants.C_COLLECT_FEES_POSITION_25,WaybillSignConstants.C_COLLECT_FEES_CHAR_25_3);
    }

    /**
     * 是否正向 （外单）
     * @param waybillSign waybillSign
     * @return true 是，false 不是
     */
    public static boolean isForeignForward(String waybillSign) {
        return isSignChar(waybillSign,WaybillSignConstants.BACKWARD_TYPE_POSITION_61,WaybillSignConstants.BACKWARD_TYPE_NO_CHAR_61_0);
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
     * 判断是否是加盟商运单 106=2
     * @param waybillSign
     * @return
     */
    public static boolean isAllianceBusi(String waybillSign){
        return isSignChar(waybillSign, WaybillSignConstants.POSITION_106, WaybillSignConstants.CHAR_106_2);
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

}
