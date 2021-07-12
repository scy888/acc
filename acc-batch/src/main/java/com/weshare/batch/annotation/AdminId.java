package com.weshare.batch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.annotation
 * @date: 2021-07-12 17:40:52
 * @describe: controller的方法参数中使用此注解, 该方法在映射时会注入当前登录的管理用户,
 * 如果没用湖区该用户登录，则会提示未登录
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminId {
}
