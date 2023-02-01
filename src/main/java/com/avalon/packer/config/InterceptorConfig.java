package com.avalon.packer.config;

import com.avalon.packer.constant.IamLimitEnum;
import com.avalon.sdk.iam.IamInterceptor;
import com.avalon.sdk.iam.bo.DataAuthBo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Value("${iam.auth.authHost}")
    private String authHost;
    @Value("${iam.auth.projectToken}")
    private String projectToken;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Map<String, DataAuthBo> authMap = IamLimitEnum.getAll();
        registry.addInterceptor(new IamDependInterceptor(authHost, projectToken)).addPathPatterns("/admin/mediaFlag")
                .addPathPatterns("/admin/plugins")
                .addPathPatterns("/admin/plugins/types")
                .addPathPatterns("/admin/packerRecord/querySourceList")
                .addPathPatterns("/admin/packerRecord/doChannelVersions/*")
                .addPathPatterns("/admin/channel")
                .addPathPatterns("/admin/packerRecord")
                .addPathPatterns("/admin/history-record/detailList")
                .addPathPatterns("/admin/systemEnv");
        registry
                .addInterceptor(new IamInterceptor(authMap, authHost, projectToken)).addPathPatterns("/admin/**")
                .excludePathPatterns("/app/sensitive/v1/detection","/admin/packerRecord/doChannelVersions/**").excludePathPatterns("/admin/packerStatus")
                .excludePathPatterns("/file/upload")
                .excludePathPatterns("/file/uploadSource")
                .excludePathPatterns("/admin/mediaFlag")
                .excludePathPatterns("/admin/plugins")
                .excludePathPatterns("/admin/plugins/types")
                .excludePathPatterns("/admin/packerRecord/doChannelVersions/*")
                .excludePathPatterns("/admin/channel")
                .excludePathPatterns("/admin/packerRecord")
                .excludePathPatterns("/admin/history-record/detailList")
                .excludePathPatterns("/admin/systemEnv")
                .excludePathPatterns("/admin/packerRecord/queryVersion")
                .excludePathPatterns("/admin/packerRecord/getPlugins");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
