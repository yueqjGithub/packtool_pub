package com.avalon.packer.http;

import java.io.Serializable;

/**
 * 描述： 全局错误编码
 * @author wangxb
ZzR **/
public enum AvalonError implements Serializable {
    UNKNOWN_ERROR(-1, "服务器开小差了，未知错误"),
    PARAM_ERROR(1001, "参数错误"),
    APP_ID_ERROR(1002, "The appId  is not legal"),
    APP_SIGN_ERROR(1003, "The sign  is not legal"),
    MOBILE_ERROR(5003, "手机号格式错误"),
    APP_NOT_GRANT(5004, "APP未授权"),
    CACHE_ERROR(5005, "缓存Redis出错"),
    FTP_CONNECT_ERROR(5006, "FTP服务器连接失败"),
    FTP_DOWN_ERROR(5007, "FTP文件下载失败"),
    SMS_CONFIG_TEMPLATE(5008, "短信模板配置错误，请联系技术人员"),
    IP_WHITE_LIST(5009, "IP白名单错误"),
    SIGN_ERROR(5010, "签名错误"),
    AUTH_ERROR(401,"IAM鉴权失败"),
    CODE_TIMEOUT(5012,"验证码超时"),
    CODE_CHECKOUT_ERROR(5013,"当日验证次数过多,请明天再试"),
    UPLOAD_ERROR(4000,"上传文件错误"),
    FILE_DIR_ERROR(4001,"文件夹创建失败"),
    APP_DISABLED(5015,"APP不可用"),
    CHANNEL_DISABLED(5016,"当前渠道不可用"),
    EMAIL_ERROR(5017,"邮件发送失败"),
    IAM_AUTH_ERROR(5018, "APP未授权"),
    REDIS_CACHE(6000,"缓存出错"),
    REDIS_OPS_HASH(6001,"Hash Cache Error"),
    REDIS_OPS_LIST(6002,"List Cache Error"),
    REDIS_OPS_STRING(6002,"Set Cache Error"),
    SVN_ERROR(7000,"svn Error"),
    PACK_FAILED(8000, ""),
    OK(0, "ok");
    private int status;
    private String desc;

    AvalonError(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
