package com.avalon.packer.ftp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class ZzFtpClient {
    private int connectTimeOut = 3000;
    private String url;
    private String username;
    private String password;
}
