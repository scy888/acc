package com.weshare.batch.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.annotation
 * @date: 2021-07-12 17:53:10
 * @describe:
 */
@Configuration
@Slf4j
@EnableJdbcHttpSession
public class AdminIdConfig {

    private static final String ADMIN_ID = "adminId";

    //@Bean
    public HandlerMethodArgumentResolver getResolver() {

        return new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                log.info("方法参数名:{}", parameter.getParameter().getName());
                return parameter.getParameter().getType().isAssignableFrom(String.class) &&
                        parameter.hasParameterAnnotation(AdminId.class);

            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

                HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
                //String adminId = request.getHeader(ADMIN_ID);

                String adminId = (String) request.getSession().getAttribute(ADMIN_ID);
                if (adminId != null) {
                    log.info("adminId:{}", adminId);
                    return adminId;
                } else {
                    throw new RuntimeException("请先登录...");
                }
            }
        };
    }

    @Bean
    public WebMvcConfigurer getConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
                resolvers.add(getResolver());
            }
        };
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new HeaderHttpSessionIdResolver("accessToken");
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
