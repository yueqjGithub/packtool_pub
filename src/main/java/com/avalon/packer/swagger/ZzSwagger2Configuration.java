package com.avalon.packer.swagger;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * 是否开启swagger，正式环境一般是需要关闭的（避免不必要的漏洞暴露！），可根据springboot的多环境配置进行设置
 *
 * @author wangxb
 */
@EnableKnife4j
@Configuration
@EnableSwagger2
@ConditionalOnProperty(name = "avalon.swagger.enabled", matchIfMissing = true)
public class ZzSwagger2Configuration {

    @Bean
    @ConditionalOnMissingBean
    public ZzSwaggerProperties swaggerProperties() {
        return new ZzSwaggerProperties();
    }

    @Bean
    public Docket createRestApi(ZzSwaggerProperties swaggerProperties) {
        String basePackage = swaggerProperties.getBasePackage();
        return new Docket(DocumentationType.OAS_30)
            //.globalRequestParameters(getParameterList())
            .apiInfo(apiInfo(swaggerProperties)).select()
            // 为当前包路径
            .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
            .apis(RequestHandlerSelectors.basePackage(basePackage)).paths(PathSelectors.any())
            .build();
    }

    private List<RequestParameter> getParameterList() {
        RequestParameterBuilder builder = new RequestParameterBuilder();
        builder
            .name("client")
            .description("客户端类型")
            .required(true)
            .in("header");
        List<RequestParameter> resList = new ArrayList<>(1);
        resList.add(builder.build());
        return resList;
    }

    /**
     * 构建 api文档的详细信息函数,注意这里的注解引用的是哪个
     *
     * @param swaggerProperties
     * @return
     */
    private ApiInfo apiInfo(ZzSwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
            // 页面标题
            .title(swaggerProperties.getTitle())
            // 创建人信息
            .contact(new Contact(
                swaggerProperties.getContact().getName().isEmpty() ? swaggerProperties.getContact().getName()
                    : swaggerProperties.getContact().getName(),
                swaggerProperties.getContact().getUrl().isEmpty() ? swaggerProperties.getContact().getUrl()
                    : swaggerProperties.getContact().getUrl(),
                swaggerProperties.getContact().getEmail().isEmpty() ? swaggerProperties.getContact().getEmail()
                    : swaggerProperties.getContact().getEmail()))
            // 版本号
            .version(swaggerProperties.getVersion())
            // 描述
            .description(swaggerProperties.getDescription())

            .build();
    }

}
