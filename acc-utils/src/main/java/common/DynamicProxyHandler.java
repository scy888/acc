package common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: common
 * @date: 2020-02-29 16:58:03
 * @describe: 动态代理
 */
public class DynamicProxyHandler implements InvocationHandler {
    private static Object target;
    private static final Logger logger= LoggerFactory.getLogger(DynamicProxyHandler.class);
    public DynamicProxyHandler(Object target) {
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        logger.info(method.getName()+"()  "+new Date());

        return method.invoke(target,args);
    }
    public Object getDynamicProxyObj(){

       return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces() ,this );
    }
}
