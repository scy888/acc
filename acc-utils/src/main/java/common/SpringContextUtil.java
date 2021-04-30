package common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;

/**
 * 上下文工具类
 * @author: Chao
 * @date： 2020/7/24 15:45
 * @since: JDK1.8
 * @description:
 */
@Service
@Lazy(false)
public class SpringContextUtil implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);

    private static ApplicationContext applicationContext;
	
	private static MessageSourceAccessor messageSourceAccessor;

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clearHolder() {
		if (logger.isDebugEnabled()){
			logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		}
		applicationContext = null;
	}

	/**
	 * 实现ApplicationContextAware接口, 注入Context到静态变量中.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextUtil.applicationContext = applicationContext;
		SpringContextUtil.messageSourceAccessor = new MessageSourceAccessor(applicationContext);
	}

	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变量.
	 */
	@PreDestroy
	public void destroy() throws Exception {
		SpringContextUtil.clearHolder();
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	private static void assertContextInjected() {
		Assert.notNull(applicationContext, "applicaitonContext属性未注入, 请定义SpringContextHolder.");
	}
	
	/** 
	 * <p>
	 * 获取国际化文件读取器
	 * </p>
	 * @return
	 */
	public static MessageSourceAccessor getMessageAccessor() {
        return messageSourceAccessor;
    }
}