package common;

import entity.User;
import jodd.io.ZipUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: common
 * @date: 2020-04-01 23:38:18
 * @describe: 反射调用工具类
 */
public class ReflectUtils {
    private static final Logger logger = LoggerFactory.getLogger(ReflectUtils.class);
    private static final ReflectUtils instance_ = new ReflectUtils();

    public static ReflectUtils getInstance() {
        return instance_;
    }

    public Object reflectMethodByParams(Object object, String methodName, Object... params) throws Exception {
        Object target = null;
        Class clazz = object.getClass();
        Object instance = clazz.newInstance();
        List<Class> calssList = new ArrayList<>();
        List<Object> paramList = new ArrayList<>();
        for (Object param : params) {
            calssList.add(param.getClass());
            paramList.add(param);
        }
        try {
            target = clazz.getDeclaredMethod(methodName, calssList.toArray(new Class[calssList.size()]))
                    .invoke(object, paramList.toArray(new Object[paramList.size()]));
            logger.info("{target}" + target);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            logger.info("{reflectUtils}:此处接收被调用方法内部未被捕获的异常");
        }
        return target;
    }

    public Object reflectMethodByParams(String baseName, String keyName, String methodName, Object... params) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName);
        Enumeration<String> keys = bundle.getKeys();
        Object target = null;
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (keyName.equals(key)) {
                String className = bundle.getString(key);
                Class<?> clazz = Class.forName(className);
                List<Class> classList = new ArrayList<>();
                List<Object> paramsList = new ArrayList<>();
                for (Object param : params) {
                    classList.add(param.getClass());
                    paramsList.add(param);
                }
                target = clazz.getDeclaredMethod(methodName, classList.toArray(new Class[classList.size()]))
                        .invoke(clazz.newInstance(), paramsList.toArray(new Object[paramsList.size()]));
                break;
            }
        }
        return target;
    }

    public static <T> String getFieldNames(Class<T> clazz, String... fieldNames) {
        /**
         * @Description: 通过反射获取对象的属性名
         * @methodName: getFieldNames
         * @Param: [t]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2021/1/10 14:10
         */
        StringBuffer sb = null;
        try {
            Field[] declaredFields = clazz.getDeclaredFields();
            if (fieldNames.length > 0) {
                List<Field> fielidList = new ArrayList<>();
                for (Field field : declaredFields) {
                    fielidList.add(field);
                }
                for (String fieldName : fieldNames) {
                    fielidList.removeIf(e -> e.getName().equals(fieldName));
                }
                declaredFields = fielidList.toArray(new Field[fielidList.size()]);
            }
            sb = new StringBuffer();
            for (Field field : declaredFields) {
                sb.append(field.getName()).append(",");
            }
            logger.info("通过反射获取的对象属性名调用正常...");
        } catch (SecurityException e) {
            e.printStackTrace();
            logger.error("通过反射获取的对象属性名调用失败...");
        }
        return sb.deleteCharAt(sb.lastIndexOf(",")).toString();
    }

    public static <T> List<String> getFieldNameList(Class<T> clazz) {
        /**
         * @Description: 通过反射获取对象的属性名
         * @methodName: getFieldNames
         * @Param: [t]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2021/1/10 14:10
         */
        List<String> collect = null;
        try {
            //Class<T> clazz = (Class<T>) t.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            collect = Arrays.stream(declaredFields).map(Field::getName).collect(Collectors.toList());
            logger.info("通过反射获取的对象属性名调用正常...");
        } catch (SecurityException e) {
            e.printStackTrace();
            logger.error("通过反射获取的对象属性名调用失败...");
        }
        return collect;
    }

    public static <T> String getFieldValues(T t, String... fieldNames) {
        /**
         * @Description: 通过反射获取对象名的属性值
         * @methodName: getFieldValues
         * @Param: [t]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2021/1/10 14:55
         */
        Class<?> clazz = t.getClass();
        StringBuffer sb = new StringBuffer();
        try {
            Field[] fields = clazz.getDeclaredFields();
            if (fieldNames.length > 0) {
                List<Field> fielidList = new ArrayList<>();
                for (Field field : fields) {
                    fielidList.add(field);
                }
                for (String fieldName : fieldNames) {
                    fielidList.removeIf(e -> e.getName().equals(fieldName));
                }
                fields = fielidList.toArray(new Field[fielidList.size()]);
            }

            for (Field field : fields) {
                String fieldName = field.getName();
                String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method getMethod = clazz.getDeclaredMethod(methodName);
                Object value = getMethod.invoke(t);
                sb.append(Objects.isNull(value) ? "" : value).append(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static <T> String getFieldValues_(T t) {
        /**
         * @Description: 通过反射获取对象名的属性值
         * @methodName: getFieldValues
         * @Param: [t]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2021/1/10 14:55
         */
        Class<?> clazz = t.getClass();
        StringBuffer sb = new StringBuffer();
        List<Object> list = new ArrayList<>();
        String str = null;
        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                list.add(Optional.ofNullable(field.get(t)).orElse(""));
            }
            str = list.stream().map(String::valueOf).collect(Collectors.joining(","));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    @Test
    public void test() throws IOException {
        String fieldNames = getFieldNames(User.class);
        List<String> fieldNameList = getFieldNameList(User.class);
        System.out.println(fieldNames);
        System.out.println(fieldNameList);
        Path path = Paths.get("/userFile", "user");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        Files.write(Paths.get(String.valueOf(path), "userStr.csv"), fieldNames.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        List<String> list = new ArrayList<>();
        list.add(fieldNameList.stream().collect(Collectors.joining(",")));
        Files.write(Paths.get(String.valueOf(path), "userList.csv"), list, StandardOpenOption.CREATE);
    }

    @Test
    public void test_() throws Exception {
        InetAddress inetAddress = InetAddress.getLocalHost();
        System.out.println(inetAddress.getHostName());
        System.out.println(inetAddress.getHostAddress());
        User user1 = new User(1L, "赵敏", null, 18, "女", "蒙古", "123", "132", BigDecimal.TEN, User.Status.F);
        User user2 = new User(2L, "周芷若", null, 19, "女", "峨嵋", "123", "132", BigDecimal.TEN, User.Status.F);
        User user3 = new User(3L, "小昭", null, 20, "女", "波斯", "123", "132", BigDecimal.TEN, User.Status.F);
        User user4 = new User(4L, "殷离", null, 21, "女", "灵蛇岛", "123", "132", BigDecimal.TEN, User.Status.F);
        List<User> userList = new ArrayList<>();
        Collections.addAll(userList, user1, user2, user3, user4);
        System.out.println(getFieldValues_(user1));
        System.out.println(getFieldValues(user1));

        String fieldNames = getFieldNames(User.class);
        Path path = Paths.get("/userFile", "user");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }

        List<String> list = new ArrayList<>();
        list.add(getFieldNames(User.class));

        for (User user : userList) {
            list.add(getFieldValues(user));
        }
//        InputStream inputStream = this.getClass().getResourceAsStream("");
//        byte[] bytes = new byte[inputStream.available()];
        Files.write(Paths.get(String.valueOf(path), "user_.csv"), list, StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(path), "user__.csv"), String.join(System.lineSeparator(), list).getBytes("GBK"), StandardOpenOption.CREATE);

        Thread.sleep(3 * 1000);
        list.clear();
        File file = new File(String.valueOf(path));
        for (File file_ : Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(e -> e.getName().endsWith("_.csv")).collect(Collectors.toList())) {
            list.add(new String(Files.readAllBytes(Paths.get(file_.getAbsolutePath())), StandardCharsets.UTF_8));
        }

        Files.write(Paths.get(String.valueOf(path), "userCat.csv"), String.join(System.lineSeparator(), list).getBytes("GBK"), StandardOpenOption.CREATE);

        Path zipPath = Paths.get("/userFile", "zip");
        if (Files.notExists(zipPath)) {
            Files.createDirectories(zipPath);
        }
        //压缩
        try (
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(Paths.get(String.valueOf(zipPath), "user.zip").toUri())))
        ) {
            for (File file_ : Objects.requireNonNull(new File(Paths.get(String.valueOf(path)).toUri()).listFiles())) {
                ZipUtil.addToZip(zipOutputStream, Files.readAllBytes(Paths.get(file_.getAbsolutePath())), file_.getName(), "zip");
                //ZipUtil.addToZip(zipOutputStream,new File(file_.getAbsolutePath()),file_.getName(),"zip",false);
            }
        }
        //解压
        Path unZipPath = Paths.get("/userFile", "unZip");
        if (Files.notExists(unZipPath)) {
            Files.createDirectories(unZipPath);
        }
        for (File file_ : new File(Paths.get(String.valueOf(zipPath)).toUri()).listFiles()) {
            ZipUtil.unzip(String.valueOf(file_.getAbsoluteFile()), String.valueOf(unZipPath));
        }
    }

    @Test
    public void test006() {
        // List<String> 省份 = Arrays.asList("湖北省", "湖南省", "河北省", "河南省");
        //List<String> 省会 = Arrays.asList("武汉市", "长沙市", "石家庄市", "郑州市");

        List<String> 省份 = new ArrayList<>();
        Collections.addAll(省份, "湖北省", "湖南省", "河北省", "河南省");
        List<String> 省会 = new ArrayList<>();
        Collections.addAll(省会, "武汉市", "长沙市", "石家庄市", "郑州市");
//        for (int i = 省份.size()-1; i >= 0; i--) {
//            String sheng = 省份.get(i);
//            for (int j = i; j >=0; j--) {
//                String shi = 省会.get(j);
//                System.out.println(sheng+":"+shi);
//                //省份.remove(sheng);
//                //省会.remove(shi);
//                break;
//            }
//        }
        for (int i = 0; i < 省份.size(); i++) {
            String sheng = 省份.get(i);
            for (int j = i; j >= 0; j--) {
                String shi = 省会.get(j);
                System.out.println(sheng + ":" + shi);
                //省份.remove(sheng);
                //省会.remove(shi);
                break;
            }
        }
    }

    @Test
    public void test007() {
        String fieldNames = getFieldNames(User.class, "id", "birthday", "status", "password");//"id", "birthday", "status", "password"
        User user1 = new User(1L, "赵敏", null, 18, "女", "蒙古", "123456", "132", BigDecimal.TEN, User.Status.F);
        User user2 = new User(2L, "周芷若", null, 19, "女", "峨嵋", "123456", "132", BigDecimal.TEN, User.Status.F);
        User user3 = new User(3L, "小昭", null, 20, "女", "波斯", "123456", "132", BigDecimal.TEN, User.Status.F);
        User user4 = new User(4L, "殷离", null, 21, "女", "灵蛇岛", "123456", "132", BigDecimal.TEN, User.Status.F);
        List<User> userList = new ArrayList<>();
        Collections.addAll(userList, user1, user2, user3, user4);
        System.out.println(
                "打印信息如下:\n" + fieldNames + "\n"
                        + userList.stream().map(e -> getFieldValues(e, "id", "birthday", "status", "password")).collect(Collectors.joining(System.lineSeparator()))
        );
    }

    @Test
    public void test008() throws Exception {
        User user = new User(1L, "赵敏", null, 18, "女", "蒙古", "123", "132", BigDecimal.TEN, User.Status.F);
        PropertyDescriptor pd = new PropertyDescriptor("username", User.class);
        Method readMethod = pd.getReadMethod();
        System.out.println(readMethod.getName());
        System.out.println(readMethod.invoke(user));
        Method writeMethod = pd.getWriteMethod();
        writeMethod.invoke(user, "周芷若");
        System.out.println(user.getUsername());
        System.out.println("======================================");
        BeanInfo beanInfo = Introspector.getBeanInfo(User.class);
        MethodDescriptor[] methodDescriptors = beanInfo.getMethodDescriptors();
        for (MethodDescriptor methodDescriptor : methodDescriptors) {
            System.out.println(methodDescriptor.getMethod().getName());
        }
        System.out.println("=======================================");
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            System.out.println(propertyDescriptor.getName());
        }
    }
}
