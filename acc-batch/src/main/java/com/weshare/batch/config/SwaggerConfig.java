//package com.weshare.batch.config;
//
//import io.swagger.annotations.Api;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
///**
// * @author: scyang
// * @program: acc
// * @package: com.weshare.batch.config
// * @date: 2021-05-19 22:15:29
// * @describe:
// */
//@Configuration
//@EnableSwagger2
////@EnableSwaggerBootstrapUI
//public class SwaggerConfig {
//
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//                .paths(PathSelectors.any())
//                .build();
//
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("新核心系统-还款服务")
//                .termsOfServiceUrl("http://localhost:9002/batch")
//                .contact(new Contact("新核心开发组", "http://localhost:9002/batch", "348691356@qq.com"))
//                .version("1.0")
//                .build();
//    }
//}
