package com.avalon.packer.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class WebUtil {



    /**
     * 获取IP,若获取失败返回null
     *
     * @param request
     * @return
     */
    public static final String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

            // XFF的工作机制是，每经过一层代理，由代理服务器，把tcp报文中的Source IP，添加到XFF的末尾，多个IP以逗号分隔
            // 比如[106.38.68.198, 106.38.68.198]
            if (!StringUtils.isEmpty(ip) && ip.contains(",")) {
                ip = ip.replaceAll(" ", "");
                String[] ips = ip.split(",");
                // 默认取"x-forwarded-for"的最后一个ip，也就是说，我们信任最后一个代理的记录来源ip，一般是我们自己的反向代理服务器
                return ips[ips.length -1];
            }
        } catch (Exception ex) {
            log.error("Get IP Error.", ex);
        }
        return ip;
    }
}
