package com.jd.bluedragon.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by yangbo7 on 2016/6/14.
 */
public class NetHelper {

    private static Logger log = LoggerFactory.getLogger(NetHelper.class);

    public static String EXCLUDE_IP = "10.";
    public static String NET_INTERFACE;

    static {
        NET_INTERFACE = System.getProperty("nic");
        EXCLUDE_IP = System.getProperty("excludeIP", EXCLUDE_IP);
    }

    /**
     * 得到本机所有的IPV4地址
     *
     * @return
     * @throws Exception
     */
    public static List<String> getAllLocalIP() {
        List<String> result = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    InetAddress ip = ias.nextElement();
                    if (!ip.isLoopbackAddress() && (ip instanceof Inet4Address)) {
                        result.add(ip.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取当前主机的局域网IP失败", e);
        }
        return result;
    }

    /**
     * 得到指定网卡上的地址
     *
     * @param nic     网卡
     * @param exclude 排除的地址
     * @return 地址列表
     * @throws Exception
     */
    public static List<String> getLocalIP(String nic, String exclude) throws Exception {
        List<String> result = new ArrayList<String>();
        NetworkInterface ni;
        Enumeration<InetAddress> ias;
        InetAddress address;
        String ip;
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            ni = netInterfaces.nextElement();
            if (nic != null && !nic.isEmpty() && !ni.getName().equals(nic)) {
                continue;
            }
            ias = ni.getInetAddresses();
            while (ias.hasMoreElements()) {
                address = ias.nextElement();
                if (!address.isLoopbackAddress() && (address instanceof Inet4Address)) {
                    ip = address.getHostAddress();
                    result.add(ip);
                }
            }
        }

        int count = result.size();
        if (count <= 1) {
            return result;
        }
        if (exclude != null && !exclude.isEmpty()) {
            for (int i = count - 1; i >= 0; i--) {
                ip = result.get(i);
                if (ip.startsWith(exclude)) {
                    result.remove(i);
                    count--;
                    if (count == 1) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 得到本机IPV4地址
     *
     * @param exclude 排除的IP段
     * @return 本机IPV4地址
     * @throws Exception
     */
    public static String getLocalIP(final String exclude) throws Exception {
        List<String> ips = getLocalIP(NET_INTERFACE, exclude);
        if (ips != null && !ips.isEmpty()) {
            if (ips.size() == 1) {
                return ips.get(0);
            }
            for (String ip : ips) {
                if (ip.startsWith("172.")) {
                    return ip;
                } else if (ip.startsWith("192.")) {
                    return ip;
                }
            }
            return ips.get(0);
        }
        return null;
    }

    /**
     * 得到本机IPV4地址
     *
     * @return 本机IPV4地址
     * @throws Exception
     */
    public static String getLocalIP() throws Exception {
        return getLocalIP(EXCLUDE_IP);
    }


    public static void main(String[] args) throws Exception {
        System.out.println(NetHelper.getAllLocalIP());
    }

}
