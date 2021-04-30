package common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * DTO对象转换器
 *
 * @author qujiayuan
 */
public class Castor {

    /**
     * 通过一个对象的值来创建另一个对象，属性相同的值自动初始化
     *
     * @param source 数据来源的对象
     * @param clazz  要转换的对象类型
     * @return 转换后的对象
     */
    public static <T> T cast(Object source, Class<T> clazz) {
        T result = null;
        try {
            result = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            if (source == null) {
                return result;
            }
            for (Field field : fields) {
                Field existField = null;
                try {
                    existField = source.getClass().getDeclaredField(field.getName());
                } catch (NoSuchFieldException e) {
                    // do nothing
                }
                if (existField != null) {
                    field.setAccessible(true);
                    if (Modifier.isStatic(field.getModifiers())
                            || isTheSameType(field.getType(), existField.getType())) {
                        continue;
                    }
                    String name = field.getName();

                    String firstLetter = name.substring(0, 1).toUpperCase(); // 将属性的首字母转换为大写
                    String getMethodName = "get" + firstLetter + name.substring(1);
                    String setMethodName = "set" + firstLetter + name.substring(1);

                    // 获取方法对象
                    Method getMethod = source.getClass().getMethod(getMethodName);
                    Method setMethod = clazz.getMethod(setMethodName, field.getType());// 注意set方法需要传入参数类型

                    // 调用get方法获取旧的对象的值
                    Object value = getMethod.invoke(source);
                    // 调用set方法将这个值复制到新的对象中去
                    String elseTypeName = field.getType().getTypeName();
                    if (!elseTypeName.contains("List") && elseTypeName.equals(existField.getType().getTypeName())) { // 类型不同，则跳过值的拷贝
                        setMethod.invoke(result, value);
                    }
                }

            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException | InstantiationException e) {
            // do nothing
        }
        return result;
    }


    /**
     * 将一个对象的值拷贝至另一个对象
     *
     * @param source 数据来源的对象
     * @param clazz  要转换的对象类型
     * @return 转换后的对象
     */
    public static <T> T castToProbe(Object source, Class<T> clazz) {
        T result = null;
        try {
            result = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            if (source == null) {
                return result;
            }
            for (Field field : fields) {
                Field existField = null;
                try {
                    existField = source.getClass().getDeclaredField(field.getName());
                } catch (NoSuchFieldException e) {
                    // do nothing
                }
                if (existField != null) {
                    field.setAccessible(true);
                    if (Modifier.isStatic(field.getModifiers())
                            || isTheSameType(field.getType(), existField.getType())) {
                        continue;
                    }
                    String name = field.getName();

                    String firstLetter = name.substring(0, 1).toUpperCase(); // 将属性的首字母转换为大写
                    String getMethodName = "get" + firstLetter + name.substring(1);
                    String setMethodName = "set" + firstLetter + name.substring(1);

                    // 获取方法对象
                    Method getMethod = source.getClass().getMethod(getMethodName);
                    Method setMethod = clazz.getMethod(setMethodName, field.getType());// 注意set方法需要传入参数类型

                    // 调用get方法获取旧的对象的值
                    Object value = getMethod.invoke(source);
                    if (value instanceof String && "".equals(value)) {
                        value = null;
                    }
                    // 调用set方法将这个值复制到新的对象中去
                    String elseTypeName = field.getType().getTypeName();
                    if (!elseTypeName.contains("List") && elseTypeName.equals(existField.getType().getTypeName())) { // 类型不同，则跳过值的拷贝
                        setMethod.invoke(result, value);
                    }
                }

            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException | InstantiationException e) {
            // do nothing
        }
        return result;
    }

    /**
     * 将一个对象的值浅拷贝至另一个对象
     *
     * @param source 数据来源的对象
     * @param t      要转换的对象
     * @return 转换后的对象
     */
    public static <T> T to(Object source, T t) {
        T result = null;
        try {
            result = t;

            Field[] fields = t.getClass().getDeclaredFields();
            if (source == null) {
                return null;
            }
            for (Field field : fields) {
                Field existField = null;
                try {
                    existField = source.getClass().getDeclaredField(field.getName());
                } catch (NoSuchFieldException e) {
                    // do nothing
                }
                if (existField != null) {
                    field.setAccessible(true);
                    if (Modifier.isStatic(field.getModifiers())
                            || !isTheSameType(field.getType(), existField.getType())) {
                        continue;
                    }
                    String name = field.getName();

                    String firstLetter = name.substring(0, 1).toUpperCase(); // 将属性的首字母转换为大写
                    String getMethodName = "get" + firstLetter + name.substring(1);
                    String setMethodName = "set" + firstLetter + name.substring(1);

                    // 获取方法对象
                    Method getMethod = source.getClass().getMethod(getMethodName);
                    Method setMethod = t.getClass().getMethod(setMethodName, field.getType());// 注意set方法需要传入参数类型

                    // 调用get方法获取旧的对象的值
                    Object value = getMethod.invoke(source);
                    // 调用set方法将这个值复制到新的对象中去
                    String elseTypeName = field.getType().toString();
                    if (!elseTypeName.contains("List") && !elseTypeName.contains("Res")) {
                        setMethod.invoke(result, value);
                    }
                }

            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException e) {

        }
        return result;
    }

    /**
     * 将 来源对象 里不为null的属性的值给到 目标对象。
     *
     * @param source 数据来源的对象
     * @param target 目标值
     * @return 合并后的值对象
     */
    public static <T, R> R merge(T source, R target) {
        if (source == null) {
            return target;
        }
        Field[] fields = source.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                String fieldName = field.getName();
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String firstLetter = fieldName.substring(0, 1).toUpperCase(); // 将属性的首字母转换为大写
                String getMethodName = "get" + firstLetter + fieldName.substring(1);
                String setMethodName = "set" + firstLetter + fieldName.substring(1);
                // 获取方法对象
                Method getMethod = source.getClass().getMethod(getMethodName);
                Object value = getMethod.invoke(source);

                if (value == null) { // 来源对象的属性值为空时，不传给目标对象。
                    continue;
                }
                Method setMethod = target.getClass().getMethod(setMethodName, field.getType());// 注意set方法需要传入参数类型
                setMethod.invoke(target, value);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            return target;
        }
        return target;
    }

    private static boolean isTheSameType(Class<?> sourceType, Class<?> targetType) {
        List<String> intList = Arrays.asList("java.lang.Integer", "int");
        List<String> booleanList = Arrays.asList("java.lang.Boolean", "boolean");
        List<String> doubleList = Arrays.asList("java.lang.Double", "double");
        List<String> longList = Arrays.asList("java.lang.Long", "long");
        if (!sourceType.toString().equals(targetType.toString())) {
            if (intList.contains(sourceType.getTypeName()) && intList.contains(targetType.getTypeName())) {
                return true;
            } else if (booleanList.contains(sourceType.getTypeName()) && booleanList.contains(targetType.getTypeName())) {
                return true;
            } else if (doubleList.contains(sourceType.getTypeName()) && doubleList.contains(targetType.getTypeName())) {
                return true;
            } else if (longList.contains(sourceType.getTypeName()) && longList.contains(targetType.getTypeName())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

}
