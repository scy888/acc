package com.weshare.adapter.migration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.migration
 * @date: 2021-07-16 20:03:51
 * @describe:
 */
@Component
public class ServiceLocator implements ApplicationContextAware {

    private Map<String, DataMigration> map;

    @Override
    //@PostConstruct
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        /**
         * 用于保存接口实现类名及对应的类
         */
        map = applicationContext.getBeansOfType(DataMigration.class);
    }

    public Map<String, DataMigration> getMap() {
        return map;
    }
}
