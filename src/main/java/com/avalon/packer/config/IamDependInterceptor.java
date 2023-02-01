package com.avalon.packer.config;
import com.ejlchina.okhttps.OkHttps;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class IamDependInterceptor implements HandlerInterceptor {
    private final String iamUrl;
    private final String projectToken;

    public IamDependInterceptor(String iamUrl, String projectToken) {
        this.iamUrl = iamUrl;
        this.projectToken = projectToken;
    }

    public boolean preHandle (final HttpServletRequest request, HttpServletResponse response, Object handler) {

        boolean selfResult = getResult(
                this.projectToken,
                this.iamUrl,
                request.getRequestURI(),
                request.getHeader("actionName"),
                request.getHeader("Authorization"));

        if (!selfResult) {
            String dependPath = request.getHeader("dependPath");
            String dependAction = request.getHeader("dependAction");
            if (null == dependPath || null == dependAction) {
                return false;
            } else {
                return getResult(
                        this.projectToken,
                        this.iamUrl,
                        dependPath,
                        dependAction,
                        request.getHeader("Authorization")
                );
            }
        } else {
            return true;
        }
    }

    public boolean getResult (String projectToken, String iamUrl, String path, String action, String authorization) {
        Map<String, String> authParams = new HashMap();
        authParams.put("projectToken", projectToken);
        authParams.put("action", URLDecoder.decode(action));
        authParams.put("route", path);
        Map<String, String> header = new HashMap();
        header.put("Authorization", authorization);
        Map map = OkHttps.sync(iamUrl).addHeader(header).addUrlPara(authParams).get().getBody().toBean(Map.class);
        return null != map && 0 == Integer.parseInt(String.valueOf(map.get("status")));
    }
}
