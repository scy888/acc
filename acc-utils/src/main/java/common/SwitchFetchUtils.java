package common;

import com.netflix.config.DynamicPropertyFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;


/**
 * @author: scyang
 * @program: ssm_super
 * @package: com.itheima.commom
 * @date: 2019-10-23 20:22:24
 * @describe: 开关工具类
 */
public class SwitchFetchUtils {
    /** 初始化 dynamicProperty */
    private static final DynamicPropertyFactory dynamicProperty= DynamicPropertyFactory.getInstance();
    @Value("${levelONE}")
    private static String levelOne;
    @Value("${levelTWO}")
    private static String levelTwo;
    @Value("${levelTHREE}")
    private static String levelThree;
    public static boolean getBooleanSwitchValue(String switchKey,boolean defaultValue){
        /**
         * @Description: 获取布尔类型的开关值
         * @methodName: getBooleanSwitchValue
         * @Param: [switchKey, switchValue]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/23 20:27
         */
       // return switchValue;
        return dynamicProperty.getBooleanProperty(switchKey,defaultValue ).getValue();
    }
    @Test
    public void test01(){
        System.out.println(SwitchFetchUtils.getBooleanSwitchValue("", true));
        System.out.println(SwitchFetchUtils.getBooleanSwitchValue("a", false));
    }
    public static boolean getBooleanSwitchValue(String switchKey){
        /**
         * @Description: 获取布尔类型的开关值默认打开true开启,false关闭
         * @methodName: getBooleanProperty
         * @Param: [switchKey]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/23 20:36
         */
        return SwitchFetchUtils.getBooleanSwitchValue(switchKey,SwitchEnum.OPEN.isValue());
    }
    @Test
    public void test02(){
        System.out.println(SwitchFetchUtils.getBooleanSwitchValue(""));
    }
    public static String getStringSwitchValue(String switchKey,String defaultValue){
        /**
         * @Description: 获取String类型开关
         * @methodName: getStringSwitchValue
         * @Param: [switchKey, defaultValue]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/23 20:41
         */
        return dynamicProperty.getStringProperty(switchKey,defaultValue ).getValue();
    }
    @Test
    public void test03(){
        System.out.println(SwitchFetchUtils.getStringSwitchValue("", "1"));
        System.out.println(SwitchFetchUtils.getStringSwitchValue("a", "0"));
    }
    public static String getStringSwitchValue(String switchKey){
        /**
         * @Description: 获取String类型开关默认打开,1开 0关
         * @methodName: getStringSwitchValue
         * @Param: []
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/23 20:49
         */
        //return dynamicProperty.getStringProperty("",SwitchEnum.OPEN.getKey()).getValue();
        return SwitchFetchUtils.getStringSwitchValue(switchKey,SwitchEnum.OPEN.getKey() );
    }
    public static String getMiniMicroStringSwitchValue(String defaultValue){
        return dynamicProperty.getStringProperty(defaultValue, levelOne).getValue();
    }
    @Test
    public void test05(){
        System.out.println(getMiniMicroStringSwitchValue(""));
    }
    @Test
    public void test04(){
        System.out.println(SwitchFetchUtils.getStringSwitchValue(""));
    }
}
