package common;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.*;

/**
 * @author: scyang
 * @program: acc
 * @package: common
 * @date: 2021-07-04 00:38:12
 * @describe:
 */
@Slf4j
public class ProxyUtils {

    public static Object getProxyInstance(Object object) {
        /****
         * 使用动态代理实现比例计算
         *  JDK动态代理
         *  要求：被代理的对象至少实现一个接口
         *
         * newProxyInstance:
         * 第1个参数：被代理的对象的类加载器,主要作用是将字节码对象载入到内存中，并实施对数据类型的校验。
         * 第2个参数：被代理对象所实现的所有接口的字节码集合,主要作用是让生成的代理对象和被代理的对象拥有相同的行为动作
         *          其实就是给被代理对象的接口生成一个实现类。
         * 第3个参数：代理实现增强,实现一个接口InvoationHandler
         */

        Object o = Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), new InvocationHandler() {

            /****
             * 增强操作
             * @param proxy  被代理对象的引用
             * @param method 被代理对象的方法
             * @param args   调用被代理对象指定方法时，传入的参数
             * @return
             * @throws Throwable
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object obj = null;
                try {
                    long start = System.currentTimeMillis();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Parameter[] parameters = method.getParameters();
                    Class<?> returnType = method.getReturnType();
                    String[] objects = new String[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        objects[i] = parameterTypes[i].getSimpleName() + " " + parameters[i].getName();
                    }
                    log.info("方法:{},执行了耗时:{}毫秒",
                            Modifier.toString(method.getModifiers()) + " " + returnType.getSimpleName() + " " + method.getName() + "(" + String.join(",", objects) + ")",
                            System.currentTimeMillis() - start
                    );
                    obj = method.invoke(object, args);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                return obj;
            }
        });
        return o;
    }

    public static Object getProxyInstance_(Object object) {
        /**
         * 动态代理：
         * 	 特点：字节码随用随创建，随用随加载
         * 	 分类：基于接口的动态代理，基于子类的动态代理
         * 	 作用：不修改源码的基础上对方法增强
         * 	 基于子类的动态代理
         * 	提供者是：第三方cglib包，在使用时需要先导包(maven工程导入坐标即可)
         * 	使用要求：被代理类不能是最终类，不能被final修饰
         * 	涉及的类：Enhancer
         * 	创建代理对象的方法：create
         * 	方法的参数：
         *  Class：字节码。被代理对象的字节码。可以创建被代理对象的子类，还可以获取被代理对象的类加载器。
         * 	Callback：增强的代码。通常都是写一个接口的实现类或者匿名内部类。
         * 我们在使用时一般都是使用Callback接口的子接口：MethodInterceptor
         */
        Object o = Enhancer.create(object.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object invoke = null;
                try {
                    long start = System.currentTimeMillis();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Parameter[] parameters = method.getParameters();
                    Class<?> returnType = method.getReturnType();
                    String[] array = new String[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        array[i] = parameterTypes[i].getSimpleName() + " " + parameters[i].getName();
                    }
                    log.info("方法:{},执行了耗时:{}毫秒",
                            Modifier.toString(method.getModifiers()) + " " + returnType.getSimpleName() + " " + method.getName() + "(" + String.join(",", array) + ")",
                            System.currentTimeMillis() - start
                    );
                    invoke = method.invoke(object, objects);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return invoke;
            }
        });
        return o;
    }
}
