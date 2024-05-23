package com.jd.common.limiter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName NetworkUtil
 * @Description
 * @Author wyh
 * @Date 2023/5/17 21:41
 **/
@Slf4j
public class NetworkUtil {

    /**
     * 获取客户端IP
     * @return
     */
    public static String getRealIPAddr() {
        String ip = null;
        HttpServletRequest request = getRequest();
        if (null != request) {
            String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
            for (String header : headers) {
                ip = request.getHeader(header);
                if (!isUnknown(ip)) {
                    return getMultistageReverseProxyIp(ip);
                }
            }
            ip = request.getRemoteAddr();
            return getMultistageReverseProxyIp(ip);
        }
        return ip;
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    public static boolean isUnknown(String checkString) {
        return StringUtils.isEmpty(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }
}
