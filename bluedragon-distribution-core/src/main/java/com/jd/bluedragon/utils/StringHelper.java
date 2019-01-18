package com.jd.bluedragon.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.jd.bluedragon.Constants;
import com.jd.common.util.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public class StringHelper {

    private static Logger logger = Logger.getLogger(StringHelper.class);

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
                StringHelper.logger.error("error!", e);
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
                StringHelper.logger.error("error!", e);
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
                StringHelper.logger.error("error!", e);
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
}
