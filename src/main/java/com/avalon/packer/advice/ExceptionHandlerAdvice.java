package com.avalon.packer.advice;

import com.avalon.packer.exception.AvalonException;
import com.avalon.packer.http.AvalonError;
import com.avalon.packer.http.AvalonHttpResp;
import com.avalon.sdk.iam.exception.IAMException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 统一异常处理类
 * @author wangxb
 **/
@RestControllerAdvice
@Slf4j
@ResponseBody
public class ExceptionHandlerAdvice {
    /**
     * 处理未捕获的Exception
     * @param e 异常
     * @return 统一响应体
     */
    @ExceptionHandler(Exception.class)
    public AvalonHttpResp handleException(Exception e){
        log.error("{}",e);
        AvalonHttpResp avalonResp = new AvalonHttpResp<>();
        avalonResp.setStatus(AvalonError.UNKNOWN_ERROR.getStatus());
        avalonResp.setMessage(e.getMessage());
        return avalonResp;
    }


    /**
     * 处理未捕获的Exception
     * @param e 异常
     * @return 统一响应体
     */
    @ExceptionHandler(AvalonException.class)
    public AvalonHttpResp handleException(AvalonException e){
        log.error("{}",e);
        AvalonHttpResp avalonResp = new AvalonHttpResp<>();
        avalonResp.setStatus(e.getCode());
        avalonResp.setMessage(e.getMessage());
        return avalonResp;
    }

    /**
     * 处理未捕获的Exception
     * @param e 异常
     * @return 统一响应体
     */
    @ExceptionHandler(IAMException.class)
    public AvalonHttpResp iamHandleException(IAMException e){
        log.error("{}",e);
        AvalonHttpResp avalonResp = new AvalonHttpResp<>();
        avalonResp.setStatus(AvalonError.AUTH_ERROR.getStatus());
        avalonResp.setMessage(AvalonError.AUTH_ERROR.getDesc());
        return avalonResp;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AvalonHttpResp<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return AvalonHttpResp.failed(new AvalonException(AvalonError.PARAM_ERROR, e.getBindingResult().getFieldError().getDefaultMessage()));
    }
}
