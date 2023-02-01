package com.avalon.packer.config;

import com.avalon.sdk.common.utils.ZzFtpClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: xb.wang
 * @create: 2022-04-06 16:08
 */
@Configuration
@Data
public class BeanConfig {
    @Value("${ftp.url}")
    private String ftpUrl;
    @Value("${ftp.username}")
    private String ftpUserName;
    @Value("${ftp.password}")
    private String ftpUserPwd;

    @Bean("gmMotherFtp")
    public ZzFtpClient getZzFtpClient(){
        return new ZzFtpClient(10000,ftpUrl,ftpUserName,ftpUserPwd);
    }
}
