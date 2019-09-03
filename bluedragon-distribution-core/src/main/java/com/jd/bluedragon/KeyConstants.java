package com.jd.bluedragon;

import java.text.MessageFormat;

/**
 * <P>
 *     定义各种key key前缀 key后缀
 * </p>
 * @Description: 字符串定义常量类
 * @author wuzuxiang
 * @since 2019/4/5
 */

public class KeyConstants {

    public static final String REDIS_PREFIX_KEY_PACK_REPRINT = "RE_PRINT_CODE_{0}";

    public static final String REDIS_PREFIX_KEY_PACK_REPRINT_NEW = "RE_PRINT_CODE_NEW_{0}";

    public static String genConstantsKey(String pattern, String... vals) {
        return MessageFormat.format(pattern, vals);
    }
}
