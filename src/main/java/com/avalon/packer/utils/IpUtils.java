package com.avalon.packer.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author wangxb
 * @date 2021/08/29 10:58
 **/
@Slf4j
public class IpUtils {
    /**
     * 获取IP地址
     * @param request
     * @return java.lang.String
     */
    public static String ipAddr(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        log.error(e.getMessage(), e);
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***".length()
            if (ipAddress != null && ipAddress.length() > 15) {
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }

        return ipAddress;
    }

    /**
     * @Author wxb
     * @Desc
     * @Params 检测ip
     * @Return
     * @Exception
     */
    public static boolean isLegalIp(String ip,String okIp){
        return ip.equals(okIp);
    }

    /**
     * 判断IP地址是否在IP白名单段内，仅支持IPv4
     * @param ip 待判定的IP地址
     * @param range 以192.168.0.0/22格式定义的IP网段
     * @return true|false
     */
    public static boolean isLegalIpInRange(String ip, String range) {
        if (StringUtils.isEmpty(range) || StringUtils.isEmpty(ip)) {
            return false;
        }
        int r = Integer.parseInt(range.replaceAll(".*/", ""));

        int mask = 0xFFFFFFFF << (32 - r);

        String cidrIp = range.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24) | (Integer.parseInt(cidrIps[1]) << 16) |
                (Integer.parseInt(cidrIps[2]) << 8) | Integer.parseInt(cidrIps[3]);

        String[] ips = ip.split("\\.");
        //简单判断IP格式，不必进行严格检查，后面的算法还会继续判断
        if (ips == null || ips.length != 4) {
            return false;
        }
        int ipAddr = (Integer.parseInt(ips[0]) << 24) | (Integer.parseInt(ips[1]) << 16) |
                (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
        if ((ipAddr & mask) == (cidrIpAddr & mask)) {
            return true;
        }
        return false;
    }
}
