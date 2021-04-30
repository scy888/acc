package common;

import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @program: acc
 * @author: jiancai.zhou
 * @date: 2020-07-10 16:48
 **/
public class FrameworkUtils {
    /**
     * 从spring的BeanUtils拷贝，增加空值不拷贝的逻辑
     *
     * @param source the source bean
     * @param target the target bean
     * @param ignoreProperties array of property names to ignore
     * @throws BeansException if the copying failed
     */
    public static void copyNotNullProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
        copyNotNullProperties(source, target, null, ignoreProperties);
    }

    /**
     * Copy the property values of the given source bean into the given target bean.
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the
     * source bean exposes but the target bean does not will silently be ignored.
     * @param source the source bean
     * @param target the target bean
     * @param editable the class (or interface) to restrict property setting to
     * @param ignoreProperties array of property names to ignore
     * @throws BeansException if the copying failed
     */
    private static void copyNotNullProperties(Object source, Object target, @Nullable Class<?> editable,
                                              @Nullable String... ignoreProperties) throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                    "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                        ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            if (Objects.nonNull(value)) {
                                writeMethod.invoke(target, value);
                            }
                        }
                        catch (Throwable ex) {
                            throw new FatalBeanException(
                                "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }
    /**
     * obj转换
     *
     * @param object
     * @param targetType
     * @return T
     * @author jiancai.zhou
     * @date 2020-06-02 15:33
     **/
    public static <T> T convert(Object object, Class<T> targetType) {
        return ConvertUtils.lookup(targetType).convert(targetType, object);
    }

}
