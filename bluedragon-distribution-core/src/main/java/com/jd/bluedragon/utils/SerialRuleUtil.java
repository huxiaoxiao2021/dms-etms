package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 序列号规则判断
 * Created by wangtingwei on 2014/11/3.
 * @see "http://jira.360buy.com/confluence/pages/viewpage.action?pageId=5486438"
 */
public class SerialRuleUtil {

    public static final String SPLIT_CHAR_STRING = "-";

    private static final String RULE_PACKAGECODE_REGEX_CHECK = "RULE_PACKAGECODE_REGEX_CHECK"; //包裹号正则
    private static final String RULE_WAYBILLCODE_REGEX_CHECK = "RULE_WAYBILLCODE_REGEX_CHECK"; //运单号正则

    /**
     * 结果
     */
    public class MatcherResult {
        private boolean isMatch;

        private String result;

        public boolean isMatch() {
            return isMatch;
        }

        public void setMatch(boolean isMatch) {
            this.isMatch = isMatch;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    /**
     * 京东自营运单号正则
     * 规则：  京东运单号：运单号为订单号 目前9位数字。
     */
    private static final Pattern RULE_WAYBILL_OWN_REGEX = Pattern.compile("^[1-9]{1}[0-9]{8,}$");

    /**
     * 京东包裹号规则
     * 规则：153636597N1S1,153636597N1S1H1,153636597N1S1H,。 H为分拣中心滑道编号  A具体编号 S表示总件数  N表示第几件
     * 153636597-1-1,153636597-1-1-,153636597-1-1-1
     * 【^([1-9]{1}\\d{8,})[-|N](\\d{1,3})[-|S](\\d{1,3})(|[-|H]([A-Za-z0-9]*))?$】
     * 【分组一：运单号】
     * 【分组二：第几件】
     * 【分组三：共几件】
     * 【分组四：道口号】
     */
    private static final Pattern RULE_PACKAGE_OWN_REGEX = Pattern.compile("^([1-9]{1}\\d{8,})(?:-(?=\\d{1,3}-)|N(?=\\d{1,3}S))([1-9]\\d{0,2})(?:-(?=\\d{1,3}-)|S(?=\\d{1,3}H))(\\d{1,3})[-|H]([A-Za-z0-9]*)?$");

    /**
     * 运单简易正则
     */
    private static final Pattern RULE_GENERATE_WAYBILL_ALL_REGEX = Pattern.compile("^([A-Z0-9]{8,32})$");

    /**
     * 生成包裹列表专用正则
     * 【分组一：运单号】
     * 【分组二：-或N】
     * 【分组三：第几件】
     * 【分组四：-或S】
     * 【分组五：共几件】
     * 【分组六：（-或H）与道口号组合】
     */
    private static final Pattern RULE_GENERATE_PACKAGE_ALL_REGEX = Pattern.compile("^([A-Z0-9]{8,})(-(?=\\d{1,3}-)|N(?=\\d{1,3}S))([1-9]\\d{0,2})(-(?=\\d{1,3}-)|S(?=\\d{1,3}H))([1-9]\\d{0,2})([-|H][A-Za-z0-9]*)$");

    /**
     * 京东外单运单号正则表达式
     * ("^((([A-Z][A-Z0])|90|00)[0-9]{1,}[0-6])$")
     */
    private static final Pattern RULE_WAYBILL_OUTER_REGEX = Pattern.compile("^((([A-Z][A-Z0])|90|00)[0-9]{1,}[0-6])$");


    /**
     * B商家包裹号正则表达式
     * 【分组一：运单号】
     * 【分组二：第几件】
     * 【分组三：共几件】
     * 【分组四：道口号】
     * ("^((?:(?:[A-Z][A-Z0])|90|00)[0-9]{1,}[0-6])(?:-(?=\d{1,3}-)|N(?=\d{1,3}S))([1-9]\d{0,2})(?:-(?=\d{1,3}-)|S(?=\d{1,3}H))(\d{1,3})[-|H]([A-Za-z0-9]*)?$")
     */
    private static final Pattern RULE_PACKAGE_OUTER_REGEX = Pattern.compile("^((?:(?:[A-Z][A-Z0])|90|00)[0-9]{1,}[0-6])(?:-(?=\\d{1,3}-)|N(?=\\d{1,3}S))([1-9]\\d{0,2})(?:-(?=\\d{1,3}-)|S(?=\\d{1,3}H))(\\d{1,3})[-|H]([A-Za-z0-9]*)?$");

    /**
     * 京东自营取件正则表达式^W?\d{10}$
     */
    private static final Pattern RULE_SLIPNO_OWN_REGEX = Pattern.compile("^W?\\d{10}$");

    /**
     * 箱号正则表达式
     */
    private static final Pattern RULE_BOXCODE_REGEX = Pattern.compile("^[A-Z]{2}[A-Z0-9]{14,16}[0-9]{8}$");

    private static final Pattern RULE_F_WAYBILL_CODE_REGEX = Pattern.compile("^F[0-9]{11}$");

    /**
     * 发货批次号正则表达式
     */
    private static final Pattern RULE_SEND_CODE_REGEX = Pattern.compile("^[Y|y]?(\\d+)-(\\d+)-([0-9]{14,17})$");

    /**
     * 提取发货批次号中站点正则
     */
    private static final Pattern RULE_SEND_CODE_SITE_CODE_REGEX = Pattern.compile("^[Y|y]?(\\d+)-(\\d+)-([0-9]{14,})$");

    private static final SimpleCache<Pattern> patternCache = new SimpleCache<Pattern>(); //25分钟有效期
    /**
     * 库房号正则
     */
    private static final Pattern RULE_STORE_CODE_REGEX = Pattern.compile("^([A-z]{1,})-(\\d+)-(\\d+)$");

    /**
     *对龙门架扫描到的运单号进行简单的正则过滤。
     * 目前过滤条件首字符严格限制（(V|W|T|F|[1-9])）。
     * 根据不同类型限制长度的最小值，最大长度按数据库最大长度30进行过滤。
     * Add by shipeilin on 2017/08/07
     * @param wayBillCode
     * @return boolean
     */
    public static final boolean isMatchCommonWaybillCode(String wayBillCode){
        if(StringUtils.isEmpty(wayBillCode)){
            return false;
        }
        Pattern pattern = getRegexPattern(RULE_WAYBILLCODE_REGEX_CHECK);
        if(pattern != null && !pattern.matcher(wayBillCode.trim()).matches()){  //启用运单号正则但校验不通过
            return false;
        }
        return true;
    }
    /**
     *对龙门架扫描到的包裹号进行简单的正则过滤。
     * 主要校验是否包含3个"-"或者"NSH"
     * Add by shipeilin on 2017年8月16日
     * @param packageCode
     * @return boolean
     */
    public static final boolean isMatchCommonPackageCode(String packageCode){
        if(StringUtils.isEmpty(packageCode)){
            return false;
        }
        Pattern pattern = getRegexPattern(RULE_PACKAGECODE_REGEX_CHECK);
        if(pattern != null && !pattern.matcher(packageCode.trim()).matches()){  //启用包裹号正则但校验不通过
            return false;
        }
        return true;
    }

    /**
     * 获取正则规则
     * @param key
     * @return
     */
    private static Pattern getRegexPattern(String key){
        Pattern pattern = null;
        BaseService baseService = (BaseService) SpringHelper.getBean("baseService");
        List<SysConfig> configs = baseService.queryConfigByKeyWithCache(key);
        if(configs == null || configs.size() != 1 || StringUtils.isBlank(configs.get(0).getConfigContent())
                || !StringHelper.matchSiteRule(configs.get(0).getConfigContent(), "ON")){  //未配置或未启用
            return pattern;
        }
        String content = configs.get(0).getConfigContent();
        String rule = content.substring(content.indexOf(Constants.SEPARATOR_COMMA) + 1);
        if(StringUtils.isBlank(rule)){
            return pattern;
        }
        pattern =  patternCache.get(rule);
        if(pattern == null){
            pattern =  Pattern.compile(rule);
            patternCache.put(rule, pattern);
        }
        return pattern;
    }

    /**
     * 获取收货站点
     *
     * @param sendCode 发货批次号
     * @return
     */
    public static final Integer getReceiveSiteCodeFromSendCode(String sendCode) {
        Matcher matcher = RULE_SEND_CODE_SITE_CODE_REGEX.matcher(sendCode.trim());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(2));
        }
        return null;
    }

    /**
     * 获取发送站点
     *
     * @param sendCode 发货批次号
     * @return
     */
    public static final Integer getCreateSiteCodeFromSendCode(String sendCode) {
        if (null == sendCode) {
            return null;
        }
        Matcher matcher = RULE_SEND_CODE_SITE_CODE_REGEX.matcher(sendCode.trim());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }


    /**
     * 是否匹配F返单号
     *
     * @param input
     * @return
     */
    public static final boolean isMatchFWaybillCode(String input) {
        return RULE_F_WAYBILL_CODE_REGEX.matcher(input).matches();
    }


    public static final boolean isMatchAllWaybillCode(String input) {
        return RULE_GENERATE_WAYBILL_ALL_REGEX.matcher(input).matches();
    }

    /**
     * 是否匹配京东自营运单号规则
     * 运单号为订单号 目前9位数字。
     *
     * @param input 待验证字符串
     * @return
     */
    public static boolean isMatchWaybillNo(String input) {
        return RULE_WAYBILL_OWN_REGEX.matcher(input.trim()).matches();
    }


    /**
     * 是否匹配京东包裹号规则
     * 规则：153636597N1S1,153636597N1S1H1,153636597N1S1H,。 H为分拣中心滑道编号  A具体编号 S表示总件数  N表示第几件 153636597-1-1,153636597-1-1-,153636597-1-1-1
     *
     * @param input 待验证字符串
     * @return
     */
    public static final MatcherResult isMatchPackageNo(String input) {
        MatcherResult result = new SerialRuleUtil().new MatcherResult();
        result.setResult(null);
        result.setMatch(false);
        Matcher match = RULE_PACKAGE_OWN_REGEX.matcher(input.trim());
        if (match.matches()) {   //校验老规则

            result.setMatch(Integer.valueOf(match.group(2)) <= Integer.valueOf(match.group(3)));
            result.setResult(match.group(1));
        }
        return result;
    }

    /**
     * 获取滑到号
     * <p>
     * 规则：153636597N1S1,153636597N1S1H1,153636597N1S1H,。 H为分拣中心滑道编号  A具体编号 S表示总件数  N表示第几件
     *
     * @param input 输入包裹号
     * @return
     */
    public static String getPortCode(String input) {
        input = input.toUpperCase().trim();
        String portCode = "";
        Matcher match = RULE_PACKAGE_OWN_REGEX.matcher(input);
        if (match.matches()) {
            portCode = match.group(4).trim();
            return portCode;
        }
        match = RULE_PACKAGE_OUTER_REGEX.matcher(input.toUpperCase().trim());
        if (match.matches()) {
            portCode = match.group(4).trim();
        }
        return portCode;
    }

    /**
     * 是否匹配取件单号规则
     * 面单号（取件单生成的面单号）：W +10位流水号。一共11位
     *
     * @param input
     * @return
     */
    public static boolean isMatchSlipNo(String input) {
        return RULE_SLIPNO_OWN_REGEX.matcher(input.trim().toUpperCase()).matches();
    }

    /**
     * 判断是否为接货包裹号
     */
    public static MatcherResult getReceiveWaybillCode(String input) {
        MatcherResult result = new SerialRuleUtil().new MatcherResult();
        result.setMatch(false);
        Matcher match = RULE_PACKAGE_OUTER_REGEX.matcher(input.toUpperCase().trim());
        if (match.matches()) {
            String waybillNo = match.group(1).trim();
            result.setResult(waybillNo);
            result.setMatch(Integer.valueOf(match.group(2)) <= Integer.valueOf(match.group(3)));

        }
        return result;
    }

    public static boolean isMatchReceivePackageNo(String input) {
        MatcherResult result = getReceiveWaybillCode(input);
        return result.isMatch();
    }

    /**
     * 是否匹配接货运单号
     */
    public static boolean isMatchReceiveWaybillNo(String input) {
        if (null == input) {
            return false;
        }
        Matcher match = RULE_WAYBILL_OUTER_REGEX.matcher(input.trim());
        if (match.matches()) {
            String waybill = match.group(1).trim();
            if (match.group(3).length() > 0) {
                waybill = waybill.replaceFirst(match.group(3), "");
            }
            return Long.valueOf(waybill.substring(0, waybill.length() - 1)) % 7 == Long.valueOf(waybill.substring(waybill.length() - 1));
        }

        return false;
    }

    /**
     * 是否匹配包裹号包括自营及接货
     */
    public static MatcherResult getAllWaybillCode(String input) {
        MatcherResult result = isMatchPackageNo(input);
        if (!result.isMatch()) {
            result = getReceiveWaybillCode(input);
        }
        return result;
    }


    /**
     * 是否匹配包裹号包括自营及接货
     */
    public static boolean isMatchAllPackageNo(String input) {
        return getAllWaybillCode(input).isMatch();
    }


    /**
     * 是否匹配接货运单号和分拣运单号
     */
    public static boolean isMatchAllWaybillNo(String input) {
        return isMatchReceiveWaybillNo(input) || isMatchWaybillNo(input);
    }

    /**
     *
     */
    public static boolean isWaybillOrPackageNo(String input) {
        return isMatchAllPackageNo(input) || isMatchAllWaybillNo(input);
    }

    /**
     * 根据包裹号获取包裹数量
     */
    public static int getPackageCounter(String packageCode) {
        packageCode = packageCode.toUpperCase().trim();
        Matcher match = RULE_PACKAGE_OWN_REGEX.matcher(packageCode);
        if (match.matches()) {
            return Integer.valueOf(match.group(2)) <= Integer.valueOf(match.group(3)) ? Integer.valueOf(match.group(3)) : -1;
        }
        match = RULE_PACKAGE_OUTER_REGEX.matcher(packageCode);
        if (match.matches()) {
            return Integer.valueOf(match.group(2)) <= Integer.valueOf(match.group(3)) ? Integer.valueOf(match.group(3)) : -1;
        }
        return -1;
    }

    /**
     * 获取包裹序列号
     */
    public static String getPackageSerial(String packageCode) {
        if (isMatchAllPackageNo(packageCode)) {
            String[] splitPackageCode = packageCode.split("[-NSH]");
            if (splitPackageCode.length >= 3) {
                return MessageFormat.format("{0}/{1}", splitPackageCode[1], splitPackageCode[2]).toString();
            }
        }
        return "0/0";
    }

    /**
     * 生产包裹号码
     */
    public static List<String> generateAllPackageCodes(String input) {
        Matcher match = RULE_GENERATE_PACKAGE_ALL_REGEX.matcher(input.toUpperCase().trim());
        if (match.matches()) {
            String template = match.group(1) + match.group(2) + "{0}" + match.group(4) + match.group(5) + match.group(6);
            int count = Integer.valueOf(match.group(5));
            List<String> list = new ArrayList<String>(count);
            for (int i = 1; i <= count; i++) {
                list.add(MessageFormat.format(template, String.valueOf(i)));
            }
            return list;
        }
        List<String> list = new ArrayList<String>(1);
        list.add(input);
        return list;
    }

    /**
     * 获取运单号
     * 当输入为包裹号时，返回对应的运单号，否则返回自身
     *
     * @param input 包裹号或运单号
     * @return
     */
    public static String getWaybillCode(String input) {
        if (null == input) {
            return input;
        }
        Matcher match = RULE_GENERATE_PACKAGE_ALL_REGEX.matcher(input.toUpperCase().trim());
        if (match.matches()) {
            return match.group(1);
        } else {
            return input.toUpperCase();
        }

    }
    /*
    public static void main(String[] args) {
        List<String> list= generateAllPackageCodes("VA05328419025-1-200-");
        for(String item:list ){
            System.out.println(item);
        }
        list= generateAllPackageCodes("VA05328419025-1-200-SDSfs");
        for(String item:list ){
            System.out.println(item);
        }
        list= generateAllPackageCodes("43205328419025-1-200-");
        for(String item:list ){
            System.out.println(item);
        }
        list= generateAllPackageCodes("43205328419025-1-1-");
        for(String item:list ){
            System.out.println(item);
        }
        list= generateAllPackageCodes("43205328419025-1-1-fdasafds");
        for(String item:list ){
            System.out.println(item);
        }
        list= generateAllPackageCodes("43205328419025-1S5-fdasafds");
        for(String item:list ){
            System.out.println(item);
        }
        list= generateAllPackageCodes("BBCSFD-1-5-fdasafds");
        for(String item:list ){
            System.out.println(item);
        }
        list= generateAllPackageCodes("BBCSFD34212314231-1-3245-fdasafds");
        for(String item:list ){
            System.out.println(item);
        }
        list= generateAllPackageCodes("BBCSFD34212314231-66-23-fdasafds");
        for(String item:list ){
            System.out.println(item);
        }
    }
*/

    /**
     * 生成包裹号
     *
     * @param packageCount 包裹总数
     * @param portCode     道口号
     * @param waybillCode  运单号
     */
    public static String[] generatePackageNo(String waybillCode, int packageCount, String portCode) {
        if (packageCount >= 99) {
            packageCount = 100;
        }
        String[] packageList = new String[packageCount];
        for (int index = 0; index < packageCount; index++) {
            packageList[index] = MessageFormat.format("{0}-{1}-{2}-{3}", waybillCode, String.valueOf(index + 1), String.valueOf(packageCount), portCode);
        }
        return packageList;
    }


    /**
     * 验证输入是否为正整数
     *
     * @param input 输入字符串
     */
    public static boolean isMatchNumeric(String input) {
        Pattern reg = Pattern.compile("^[1-9]{1}[0-9]*$");
        return reg.matcher(input.trim()).matches();
    }

    /**
     * 判断是否为箱号
     *
     * @param boxCode 待验证字符
     * @return
     */
    public static boolean isMatchBoxCode(String boxCode) {
        return RULE_BOXCODE_REGEX.matcher(boxCode.trim().toUpperCase()).matches();
    }

    public static boolean isMatchExpressorBoxCode(String boxCode) {
        return isMatchBoxCode(boxCode) && boxCode.toUpperCase().trim().startsWith("ZC");
    }

    /**
     * 生成批次号
     *
     * @param createSiteCode  始发站点
     * @param receiveSiteCode 接收站点
     * @return
     */
    public static String generateSendCode(long createSiteCode, long receiveSiteCode, Date time) {
        if(null==time)
        {
            time=new Date();
        }
        String timeString = DateHelper.formatDate(time,DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS);
        timeString= timeString.substring(0,timeString.length()-1);
        long magic=Long.valueOf(String.valueOf(createSiteCode).substring(0,1)+String.valueOf(receiveSiteCode).substring(0,1)+timeString)%7;
        StringBuilder sendCode=new StringBuilder();
        sendCode.append(String.valueOf(createSiteCode));
        sendCode.append(SPLIT_CHAR_STRING);
        sendCode.append(String.valueOf(receiveSiteCode));
        sendCode.append(SPLIT_CHAR_STRING);
        sendCode.append(timeString);
        sendCode.append(String.valueOf(magic));
        return sendCode.toString();
    }

    /**
     * 根据库房编码获取库房号
     * @param storeCode wms-6-1
     * @return
     */
    public static final Integer getStoreIdFromStoreCode(String storeCode) {
    	if(storeCode==null){
    		return null;
    	}
        Matcher matcher = RULE_STORE_CODE_REGEX.matcher(storeCode.trim());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(3));
        }
        return null;
    }
}
