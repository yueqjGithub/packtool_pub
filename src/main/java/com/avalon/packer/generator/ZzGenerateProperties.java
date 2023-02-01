package com.avalon.packer.generator;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 加载prop文件的类,返回一个prop对象
 */
@Data
@Accessors(chain = true)
@Slf4j
public class ZzGenerateProperties {
    private static ZzGenerateProperties zzGenerateProperties = new ZzGenerateProperties();

    //读取properties文件的静态代码块
    static {
        Properties prop = new Properties();
        InputStream in = ZzGenerateProperties.class.getResourceAsStream("/generator.properties");
        if (null == in) {
            log.error("未读取到generator.properties文件");
        } else {
            try {
                prop.load(in);
                zzGenerateProperties.setBasePackage(prop.getProperty("basePackage").trim())
                    .setBaseProjectPath(prop.getProperty("baseProjectPath").trim())
                    .setAuthorName(prop.getProperty("authorName").trim()).setUrl(prop.getProperty("url").trim())
                    .setUsername(prop.getProperty("username").trim()).setTables(prop.getProperty("tables").split(","))
                    .setPassword(prop.getProperty("password").trim());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String baseProjectPath = "";
    private String basePackage = "";
    private String authorName = "";
    private String url = "";
    private String username = "";
    private String password = "";
    private String[] tables = new String[10];

    static ZzGenerateProperties getInstance() {
        return zzGenerateProperties;
    }
}
