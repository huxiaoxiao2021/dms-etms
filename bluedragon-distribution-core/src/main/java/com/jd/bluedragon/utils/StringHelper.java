package com.jd.bluedragon.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.jd.bluedragon.Constants;
import com.jd.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class StringHelper {

    private static Logger log = LoggerFactory.getLogger(StringHelper.class);

    public static final String  SMILE = "^_^";           //微笑符号
    public static final int PHONE_FIRST_NUMBER = 3;//收件人联系方式前几位需要显示
    public static final int PHONE_HIGHLIGHT_NUMBER = 4;//收件人联系方式需要突出显示的位数(即手机尾数要保留的位数)

    public static String getRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(String.valueOf(random.nextInt(10)));
        }
        return sb.toString();
    }

    public static String getStringValue(Object object) {
        return ObjectHelper.isNotEmpty(object) ? object.toString() : "";
    }

    public static Boolean isEmpty(String s) {
        if (s == null || s.trim().length() == 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public static Boolean isAnyEmpty(String... ss) {
        for (String s : ss) {
            if (s == null || s.trim().length() == 0) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public static boolean isNotEmpty(String s) {
        return !StringHelper.isEmpty(s);
    }

    public static String join(Collection<?> objects) {
        return StringHelper.join(objects, Constants.SEPARATOR_COMMA);
    }

    public static String join(Collection<?> objects, String separator) {
        StringBuilder sb = new StringBuilder();

        for (Iterator<?> iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();

            if (o != null) {
                sb.append(o.toString());
            }

            if (iterator.hasNext()) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static String join(Collection<?> objects, String separator, String prefix,
            String postfix, String wrapSeparator) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (Iterator<?> iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();

            if (o != null) {
                sb.append(wrapSeparator);
                sb.append(o.toString());
                sb.append(wrapSeparator);
            }

            if (iterator.hasNext()) {
                sb.append(separator);
            }
        }
        sb.append(postfix);
        return sb.toString();
    }

    public static String join(Collection<?> objects, String methodName, String separator) {
        StringBuilder sb = new StringBuilder();

        for (Iterator<?> iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();

            try {
                Method method = o.getClass().getMethod(methodName);
                String value = String.valueOf(method.invoke(o));

                if (value != null) {
                    sb.append(value);
                }

                if (iterator.hasNext()) {
                    sb.append(separator);
                }
            } catch (Exception e) {
                StringHelper.log.error("error!", e);
                continue;
            }
        }

        return sb.toString();
    }

    public static String join(Collection<?> objects, String methodName, String separator,
            String wrapSeparator) {
        StringBuilder sb = new StringBuilder();

        for (Iterator<?> iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();

            try {
                Method method = o.getClass().getMethod(methodName);
                String value = String.valueOf(method.invoke(o));

                if (value != null) {
                    sb.append(wrapSeparator);
                    sb.append(value);
                    sb.append(wrapSeparator);
                }

                if (iterator.hasNext()) {
                    sb.append(separator);
                }
            } catch (Exception e) {
                StringHelper.log.error("error!", e);
                continue;
            }
        }

        return sb.toString();
    }

    public static String join(Collection<?> objects, String methodName, String separator,
            Integer numc) {
        StringBuilder sb = new StringBuilder();

        Integer i = 1;

        for (Iterator<?> iterator = objects.iterator(); iterator.hasNext() && numc > 0 && i <= numc;) {
            Object o = iterator.next();

            try {
                Method method = o.getClass().getMethod(methodName);
                String str = String.valueOf(method.invoke(o));

                if (str != null) {
                    sb.append(str);
                    i++;
                }

                if (iterator.hasNext() && numc > 0 && i <= numc) {
                    sb.append(separator);
                }
            } catch (Exception e) {
                StringHelper.log.error("error!", e);
                continue;
            }
        }

        return sb.toString();
    }

    public static String joinIds(Collection<?> objects, String separator) {
        return StringHelper.join(objects, "getId", separator);
    }

    public static String padZero(Long number) {
        return String.format("%08d", number);
    }

    public static String padZero(Long number, int length) {
        switch (length) {
            case 11:
                return String.format("%011d", number);
            case 2:
                return String.format("%02d", number);
            default:
                return String.format("%08d", number);
        }
    }

    /**
     *把时间格式字符串转为时间类型
     * @param date 字符串格式的时间
     * @param format 时间格式 如："yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static Date getFormatDate(String date, String format) {
         SimpleDateFormat sdf = new SimpleDateFormat(format);
         Date d = new Date();
         try {
              d = sdf.parse(date);
             }
         catch (ParseException e)
         {
             StringHelper.log.error("时间转换失败", e);
         }
         return d;
    }


	/**
	 * 获取文件名扩展后缀
	 * @param filename
	 * @return
	 */
	public static String getFileNameSuffix(String filename){
		if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
	}

	public static Boolean matchSiteRule(String ruleSite, String receiveSite) {
        if (ruleSite == null || receiveSite == null) {
            return Boolean.FALSE;
        }

        String[] ruleSiteArray = ruleSite.split(Constants.SEPARATOR_COMMA);
        Arrays.sort(ruleSiteArray);
        if (-1 < Arrays.binarySearch(ruleSiteArray, receiveSite)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

	/**
	 * HTML标签转义方法 —— java代码库
	 * @param content
	 * @return
	 */
	public static String html(String content) {
		if(content==null) return "";
		String html = content;
		html = StringUtils.replace(html, "'", "&apos;");
		html = StringUtils.replace(html, "\"", "&quot;");
		html = StringUtils.replace(html, "\t", "&nbsp;&nbsp;");// 替换跳格
		html = StringUtils.replace(html, " ", "&nbsp;");// 替换空格
		html = StringUtils.replace(html, "<", "&lt;");
		html = StringUtils.replace(html, ">", "&gt;");
		return html;
	}

	public static List<String> parseList(String str, String comma) {
		if (null == str || str.length() < 1 || comma == null || comma.length() < 1) {
			return new ArrayList<String>();
		}
		List<String> strList = new ArrayList<String>();
		Iterable<String> it = Splitter.on(comma).trimResults().omitEmptyStrings().split(str);
		Set<String> set = Sets.newTreeSet(it);
		for (String s : set) {
			strList.add(s);
		}
		return strList;
	}

    public static String prefixStr(String str, String comma) {
        if (null == str || str.length() < 1 || comma == null || comma.length() < 1) {
            return str;
        }
        int index = str.indexOf(comma);
        if (index > -1) {
            return str.substring(0, index);
        }
        return  str;
    }

    /**
     * 判断字符串是否为double数值 added by zhanglei 2016/12/21
     * @param str
     * @return
     */
    public static boolean isDouble(String str) {

        if (str == null || str.length() == 0) {
            return false;
        }

        Pattern pattern = Pattern.compile("^[-\\+]?\\d+(\\.\\d*)?|\\.\\d+$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断字符串是否为邮箱地址****@***.***
     * @param str
     * @return true false
     */
    public static boolean isMailAddress(String str) {

        if (str == null || str.length() == 0) {
            return false;
        }

        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        return pattern.matcher(str).matches();
    }
    /**
     * 将字符串分割，返回set类型
     * @param str
     * @param regex 分隔符
     * @return
     */
	public static Set<String> splitToSet(String str, String regex) {
		if (null == str || str.length() < 1 || regex == null || regex.length() < 1) {
			return new TreeSet<String>();
		}
		Iterable<String> it = Splitter.on(regex).trimResults().omitEmptyStrings().split(str);
		return Sets.newTreeSet(it);
	}

    /**
     * 将字符串中的指定字符串移除
     *
     * @param src
     * @param removeStr
     * @return
     */
    public static String remove(String src, String removeStr) {
        if (StringUtils.isEmpty(src) || StringUtils.isEmpty(removeStr)) {
            return src;
        }
        src = src.replace(removeStr, "");
        return src;
    }

    /**
     * 移除字符串中的\r、\n、\r\n
     *
     * @param src
     * @return
     */
    public static String removeRN(String src) {
        src = remove(src, "\r");
        src = remove(src, "\n");
        return src;
    }
    /**
     * 将str内容追加到字符串targetStr中，2个参数有一个为null，则返回不为null的字符串
     * @param targetStr 目标字符串
     * @param str 待追加的字符串
     * @return 
     */
    public static String append(String targetStr, String str) {
    	if(targetStr == null){
    		return str;
    	}else if(str == null){
    		return targetStr;
    	}else{
    		StringBuffer buffer = new StringBuffer(targetStr);
    		buffer.append(str);
    		return buffer.toString();
    	}
    }
    /**
     * 目标字符串不存在str内容时，将str内容追加到字符串targetStr中，2个参数有一个为null，则返回不为null的字符串
     * @param targetStr 目标字符串
     * @param str 待追加的字符串
     * @return 
     */
    public static String appendIfNotExist(String targetStr, String str) {
    	if(targetStr == null){
    		return str;
    	}else if(str == null || targetStr.contains(str)){
    		return targetStr;
    	}else{
    		StringBuffer buffer = new StringBuffer(targetStr);
    		buffer.append(str);
    		return buffer.toString();
    	}
    }
    /**
     * create by: yws
     * description: 把字符串中的任意一位转为integer类型
     * create time:
     *
     * @Param: s
     * @return
     */
    public static Integer stringToInteger(String s,int startIndex){
        Integer in=null;
        if(StringHelper.isEmpty(s) || startIndex<0 || startIndex>=s.length()){

        }
        else{
            in=Integer.valueOf(s.substring(startIndex,startIndex+1));
        }
        return in;
    }

    public static String longParseString(Long value){
        if(value == null){
            return null;
        }
        return String.valueOf(value);
    }

    /**
     * 对电话号码 加微笑符合
     * @param phone 手机号 电话号码
     * @return 加密后的字符串
     */
    public static String phoneEncrypt(String phone){
        if(org.apache.commons.lang.StringUtils.isBlank(phone)){
            return phone;
        }
        //进行隐藏要求tel/mobile至少有7位，<7位则不隐藏
        int phoneLeastLength = PHONE_FIRST_NUMBER + PHONE_HIGHLIGHT_NUMBER;
        //去除号码中间的空白字符
        String newPhone = phone.replaceAll("\\s*", "");
        if(newPhone.length() >= phoneLeastLength ){
            return newPhone.substring(0,PHONE_FIRST_NUMBER) + SMILE + newPhone.substring(newPhone.length() - PHONE_HIGHLIGHT_NUMBER);
        }
        return newPhone;
    }

    public static String substring(String value,int start,int end){
        if(StringUtils.isEmpty(value)){
            return value;
        }
        if(value.length() <= end){
            return value;
        }
        return value.substring(start,end);
    }

    /**
     * 判断字符串是否是数值类型转换成的
     */
    public static boolean isNumberic(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static final void main(String[] args) {
        System.out.println(phoneEncrypt("18600399842"));
        System.out.println(phoneEncrypt("1860039942  d"));
        System.out.println(phoneEncrypt("0105095762 8  "));
        System.out.println(phoneEncrypt(null));
    }
}
