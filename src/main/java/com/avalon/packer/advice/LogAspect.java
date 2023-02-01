package com.avalon.packer.advice;

import com.alibaba.fastjson.JSON;
import com.avalon.packer.constant.CommonConstants;
import com.avalon.packer.context.ZzContextHandler;
import com.avalon.packer.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;


/**
 *spring自动切换JDK动态代理和CGLIB
 */
@Component
@Aspect
@ComponentScan
@EnableAspectJAutoProxy
@Slf4j
public class LogAspect {
    @Resource
    HttpServletRequest request;
    @Resource
    HttpServletResponse response;

    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 在方法执行前进行切面
     */
    @Pointcut("execution(* com.avalon.packer..*.*(..)) && (@annotation(org.springframework.web.bind.annotation.GetMapping)||@annotation(org.springframework.web.bind.annotation.PutMapping)||@annotation(org.springframework.web.bind.annotation.DeleteMapping)||@annotation(org.springframework.web.bind.annotation.PostMapping)||@annotation(org.springframework.web.bind.annotation.RequestMapping))")
    public void log() {
    }

    @Around("log()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        String uri = request.getRequestURI();
        StringBuilder sb = new StringBuilder();
        long start = System.currentTimeMillis();
        Object result = point.proceed();
        long end = System.currentTimeMillis();
        Object query = request.getAttribute("params");
        if (StringUtils.isEmpty(query)) {
            String queryString = request.getQueryString();
            Object[] args = point.getArgs();
            if (StringUtils.isEmpty(queryString)) {
                if (args.length > 0) {
                    try {
                        query = JSON.toJSONString(args[0].toString());
                    } catch (Exception e) {
                        query = "args is null";
                    }
                }
            } else {
                query = queryString;
            }
            request.setAttribute("param", query);
        }

        sb.append("\t" + request.getRequestURI())
                .append("\t@RequestTime[" + (end - start) + "ms]")
                .append("\t@IP[" + IpUtils.ipAddr(request) + "]")
                .append("\t@Method: " + request.getMethod())
                .append("\t@Operator: " + ZzContextHandler.get(CommonConstants.IAMConstants.USER_NAME))
                .append("\t@Params:" + JSON.toJSONString(query))
                .append("\t@Response: " + response.getStatus())
                .append(JSON.toJSONString(result));
        if (Objects.equals(uri, "/packer/admin/packerStatus")) {
            return point.proceed();
        } else {
            log.info(sb.toString());
        }
        return result;
    }

//    @Pointcut("execution(* com.avalon.content..*.*(..)) && (@annotation(org.springframework.web.bind.annotation.GetMapping)||@annotation(org.springframework.web.bind.annotation.PutMapping)||@annotation(org.springframework.web.bind.annotation.DeleteMapping)||@annotation(org.springframework.web.bind.annotation.PostMapping)||@annotation(org.springframework.web.bind.annotation.RequestMapping))")
//    public void iam() {}
//
//    @Around("iam()")
//    public Object doIam(ProceedingJoinPoint point) throws Throwable {
//        String authorization = request.getHeader("Authorization");
//        Object path = request.getRequestURI();
//        String method = request.getMethod();
//        String action = "操作";
//        if (Objects.equals(method, "DELETE")) {
//            action = "删除";
//        }
//        String iamToken = envProperties.getProjectToken();
//        String iamUrl = envProperties.getAuthHost();
//        HttpHeaders requestHeader = new HttpHeaders();
//        RestTemplate restTemplate = new RestTemplate();
//        requestHeader.add("Authorization", authorization);
//        iamUrl = iamUrl + "?projectToken=" + iamToken + "&action=" + action + "&route=" + path;
//        try {
//            HttpEntity<IamResponse> requestEntity = new HttpEntity<IamResponse>(null, requestHeader);
//            ResponseEntity<IamResponse> res = restTemplate.exchange(iamUrl, HttpMethod.GET, requestEntity, IamResponse.class);
//            if (res.getBody().getStatus() == 0) {
//                return point.proceed();
//            } else {
//                return res.getBody();
//            }
//        } catch (Error err) {
//            return AvalonHttpResp.failed(err.toString());
//        }
//    }
}

//@Data
//class IamResponse {
//    int status;
//    Object data;
//    String message;
//    Object _link;
//}

