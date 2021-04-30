package common;

import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: scyang
 * @program: ssm_super
 * @date: 2019-09-18 21:48:24
 */
public class StringUtils {
    /**
     * @Author:scyang @Date:2019/9/18 21:49  60个字符串*
     */
    private static final String MASK_TIP = "********************************************";


    public static String startMask(String strSource, int prefixSize) {
        /**
         * @Description: 保留前prefixSize位数, 剩余掩码:1329705****
         * @methodName: startMask
         * @Param: [strSource, prefixSize]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/9/18 21:53
         */
        if (strSource.length() <= 0) {
            return "";
        } else {
            strSource = strSource.replaceAll(" ", "");
            if (strSource.length() < prefixSize) {
                return MASK_TIP.substring(0, strSource.length());
            } else {
                strSource = strSource.trim();
                return strSource.substring(0, prefixSize).concat(MASK_TIP.substring(0, strSource.length() - prefixSize));
            }
        }
    }

    public static String midlleMask(String strSource, int prefixSize, int suffixSize) {
        if (strSource.length() <= 0) {
            return "";
        } else {
            strSource = strSource.replaceAll(" ", "");
            if (strSource.length() < prefixSize + suffixSize) {
                return MASK_TIP.substring(0, strSource.length());
            } else {
                return strSource.substring(0, prefixSize)
                        .concat(MASK_TIP.substring(0, strSource.length() - prefixSize - suffixSize))
                        .concat(strSource.substring(strSource.length() - suffixSize, strSource.length()));
            }
        }
    }

    public static String startBuff(String strSource, int prefixSize) {
        if (strSource == null) {
            return "";
        } else {
            strSource = strSource.replaceAll(" ", "");
            StringBuffer sb = new StringBuffer();
            if (strSource.length() < prefixSize) {
                for (int i = 0; i < strSource.length(); i++) {
                    sb.append("*");
                }
                return sb.toString();
            } else {
                sb.append(strSource.substring(0, prefixSize));
                for (int i = 0; i < strSource.length() - prefixSize; i++) {
                    sb.append("*");
                }
                return sb.toString();
            }
        }
    }

    public static String middleSuff(String strSource, int prefixSize, int suffixSize) {
        if (strSource == null) {
            return "";
        } else {
            strSource = strSource.replaceAll(" ", "");
            StringBuffer sb = new StringBuffer();
            if (strSource.length() < prefixSize + suffixSize) {
                for (int i = 0; i < strSource.length(); i++) {
                    sb.append("*");
                }
                return sb.toString();
            } else {
                sb.append(strSource.substring(0, prefixSize));
                for (int i = 0; i < strSource.length() - prefixSize - suffixSize; i++) {
                    sb.append("*");
                }
                sb.append(strSource.substring(strSource.length() - suffixSize, strSource.length()));
                return sb.toString();
            }
        }
    }

    @Test
    public void test() {
        System.out.println(StringUtils.startMask("13297053048", 7));
        System.out.println(StringUtils.midlleMask("13297053048", 3, 4));
        System.out.println(StringUtils.midlleMask("6217 0028 7001 5622 705", 6, 4));
        System.out.println(StringUtils.startBuff("13297053048", 6));
        System.out.println(StringUtils.middleSuff("6217 0028 7001 5622 705", 6, 4));
    }

    /**********************************************************************************/

    public static boolean isCarCard(String strCarCard) {
        /**
         * @Description: 车牌号校验 粤B 5F6K8
         * @methodName: isCarCard
         * @Param: [strCarCard]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/9/26 22:14
         */
        strCarCard = strCarCard.replaceAll(" ", "");
        String regExp = "^[\\u4e00-\\u9fa5][A-Z]•\\w{5}$";
        return Pattern.compile(regExp).matcher(strCarCard).matches();
    }

    //    **
//            * 下划线转驼峰, eg: user_name -> userName
// *
//         * @param underline
// * @return
//         */
    public static String underlineToCamelLowerCase(String underline) {
        underline = underline.toLowerCase();
        Matcher matcher = Pattern.compile("_(\\w)").matcher(underline);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线， eg: userName -> user_name
     *
     * @param str
     * @return
     */
    public static String camelLowerCaseToUnderline(String str) {
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    public static String[] camelLowerCaseToUnderlines(String... strs) {
        List<String> stringList = Arrays.stream(strs).map(StringUtils::camelLowerCaseToUnderline).collect(Collectors.toList());
        return stringList.toArray(new String[stringList.size()]);
    }

    public static <T> String getInsertSql(String tableName, Class<T> clazz, String... strs) {
        /**
         * @Description:
         * @methodName: getInsertSql
         * @Param: [tableName, clazz, strs]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2021/4/14 21:49
         *  inert into table_name (a,b,c,d) values (?,?,?,?)
         */
        StringBuffer sb = new StringBuffer("insert into" + " " + tableName + "\n");
        Field[] fields = clazz.getDeclaredFields();
        String str = Arrays.stream(fields).map(e -> camelLowerCaseToUnderline(e.getName()))
                .collect(Collectors.joining(",", "(", ")"));
        for (String s : strs) {
            String[] split = str.split(",");
            if (split[split.length - 1].equals(s + ")")) {
                str = str.replace("," + s, "");
            } else {
                str = str.replace(s + ",", "");
            }
        }
        sb.append(" ").append(str).append("\n").append("values").append("\n");
        String str_ = Arrays.stream(fields).map(e -> "?").collect(Collectors.joining(",", "(", ")"));
        //(?,?,?,?)
        str_ = str_.substring(0, 1) + str_.substring(2 * strs.length + 1);
        sb.append(str_);
        return sb.toString();
    }

    public static <T> Object[] getFieldValue(T t, String... fieldNames) throws Exception {
        /**
         * @Description: 获取忽略字段后的属性这
         * @methodName: getFieldValue
         * @Param: [t, fieldNames]
         * @return: java.lang.Object[]
         * @Author: scyang
         * @Date: 2021/4/15 14:04
         */
        List<Object> list = new ArrayList<>();
        Class<T> clazz = (Class<T>) t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<Field> fieldList = new ArrayList<>();
        for (Field field : fields) {
            fieldList.add(field);
        }
//        for (int i = 0; i < fieldList.size(); i++) {
//            String name = fieldList.get(i).getName();
//            for (int j = 0; j < fieldNames.length; j++) {
//                String fieldName = fieldNames[j];
//                if (name.equals(fieldName)) {
//                    fieldList.remove(fieldList.get(i));
//                }
//            }
//        }

        for (String fieldName : fieldNames) {
            fieldList.removeIf(e -> e.getName().equals(fieldName));
        }
        for (Field field : fieldList) {
            field.setAccessible(true);
            Object o = field.get(t);
            if (o instanceof Enum) {
                list.add(((Enum) o).name());
            } else {
                list.add(o);
            }
        }
        //System.out.println(list);
        return list.toArray(new Object[list.size()]);
    }

    public static boolean isEqualStr(String strOne, String strTwo) {
        /**
         * @Description: 两个字符串是否相等
         * @methodName: isEqualStr
         * @Param: [strOne, strTwo]
         * @return: boolean
         * @Author: scyang
         * @Date: 2020/4/13 20:40
         */
        strOne = strOne == null ? "" : strOne.trim();
        strTwo = strTwo == null ? "" : strTwo.trim();
        return strOne.equals(strTwo);
    }

    public static String canceNull(String str) {
        /**
         * @Description: 为null返回""
         * @methodName: canceNull
         * @Param: [str]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2020/4/13 20:50
         */
        return str == null ? "" : str.trim();
    }

    @Test
    public void test00() {
        System.out.println(isEqualStr("a", " a"));
        System.out.println(isEqualStr(null, null));
        System.out.println(canceNull(null).length());
        System.out.println(canceNull(" aa"));

    }

    public static boolean isEqualTwoStr(String str1, String str2) {
        /**
         * @Description: 判断两个字符串是否相等 ""和null视为相等
         * @methodName: isEqualTwoStr
         * @Param: [str1, str2]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/13 9:01
         */
        if ((str1 == "" && str2 == null) || (str1 == null && str2 == "") || (str1 == null && str2 == null)) {
            return true;
        }
        return str1.equals(str2);
    }

    @Test
    public void test01() {
        System.out.println(StringUtils.isCarCard("粤B• S1991"));
        System.out.println(StringUtils.isEqualTwoStr("", null));
        System.out.println(StringUtils.isEqualTwoStr(null, ""));
        System.out.println(StringUtils.isEqualTwoStr("", ""));
        System.out.println(StringUtils.isEqualTwoStr(null, null));
        System.out.println(StringUtils.isEqualTwoStr("1", "1"));
        System.out.println(StringUtils.isEqualTwoStr("aa", "aaa"));
    }

    public static String nullToStr(Object oject) {
        /**
         * @Description: 将null值转换成""
         * @methodName: nullToStr
         * @Param: [oject]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/13 9:13
         */
       /* if (oject==null){
            return "";
        }
        return oject.toString();*/
        return oject == null ? "" : oject.toString();
    }

    public static String getStringValue(String str, String deflautValue) {
        if (null == str || "".equals(str.toString().trim())) {
            return deflautValue;
        }
        return str;
    }

    @Test
    public void test02() {
        System.out.println(StringUtils.nullToStr(null));
        System.out.println(StringUtils.nullToStr("阳阳"));
        System.out.println(getStringValue("yangyang", "a"));
    }

    public static boolean isEmpyStr(String str) {/**
     * @Description: 判断字符串是否为空null值视为空
     * @methodName: isEmpyStr
     * @Param: [str]
     * @return: boolean
     * @Author: scyang
     * @Date: 2019/10/13 9:43
     */
        return (str == null || "".equals(str.trim())) ? true : false;
    }

    @Test
    public void test03() {
        System.out.println(StringUtils.isEmpyStr(" "));
        System.out.println(StringUtils.isEmpyStr(null));
        System.out.println(StringUtils.isEmpyStr("a"));
    }

    public static String toUpperFistChar(String str) {
        /**
         * @Description: 将首字母转换大写
         * @methodName: toUpperFistChar
         * @Param: [str]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/13 9:47
         */
        if (str == null || "".equals(str)) {
            return "";
        }
        return str.substring(0, 1).toUpperCase().concat(str.substring(1, str.length()));
    }

    @Test
    public void test04() {
        System.out.println(StringUtils.toUpperFistChar(""));
        System.out.println(StringUtils.toUpperFistChar(null));
        System.out.println(StringUtils.toUpperFistChar("yang"));
    }

    public static boolean isBeforOrAfterSpace(String str) {
        /**
         * @Description: 判断字符串前后是否有空格
         * @methodName: isBeforOrAfterSpace
         * @Param: [str]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/13 9:58
         */
        if (str == null || "".equals(str)) {
            throw new RuntimeException("字符串为空...");
        }
        if (str.length() == str.trim().length()) {
            return false;
        }
        return true;
    }

    @Test
    public void test05() {
        System.out.println(StringUtils.isBeforOrAfterSpace(" aa"));
        System.out.println(StringUtils.isBeforOrAfterSpace("aa "));
        System.out.println(StringUtils.isBeforOrAfterSpace(" aa "));
        System.out.println(StringUtils.isBeforOrAfterSpace("a a"));
        System.out.println(StringUtils.isBeforOrAfterSpace(null));
    }

    public static String mainGetOrSet(String fieldName, String sign) {
        /**
         * @Description: 获取字段名的get和set方法
         * @methodName: mainGetOrSet
         * @Param: [fieldName, sign]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/13 10:29
         */
        if ((fieldName == null || "".equals(fieldName)) || (sign == null || "".equals(sign))) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if (sign == "get") {
            return sb.append("get").append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1, fieldName.length())).append("()").toString();
        }
        if (sign == "set") {
            return sb.append("set").append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1, fieldName.length())).append("()").toString();
        }
        return "";
    }

    @Test
    public void test06() {
        System.out.println(StringUtils.mainGetOrSet("name", "get"));
        System.out.println(StringUtils.mainGetOrSet("name", "set"));
        System.out.println(StringUtils.mainGetOrSet("", "set"));
    }

    public static int selectLength(String str) {
        /**
         * @Description: 获取字符串的长度中文字算两个长度
         * @methodName: selectLength
         * @Param: [str]
         * @return: int
         * @Author: scyang
         * @Date: 2019/11/9 10:01
         */
        if (isEmpyStr(str)) {
            throw new RuntimeException("参数不可为空....");
        }
        char[] chars = str.toCharArray();
        String regExp = "^[\\u4e00-\\u9fa5]|[\\uff08]|[\\uff09]$";
        int count = 0;
        for (char aChar : chars) {
            Pattern pattern = Pattern.compile(regExp);
            boolean flag = pattern.matcher(String.valueOf(aChar)).matches();
            //count= flag ? count+2 : count+1;
            count += flag ? 2 : 1;
        }
        return count;
    }

    @Test
    public void test15() {
        System.out.println(StringUtils.selectLength("盛重dd阳55aa"));
        System.out.println(StringUtils.selectLength("0"));

        String card = "盛重阳42220219910909349X";
        Character[] ch = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'X'};
        List<Character> list = Arrays.asList(ch);
        char[] chars = card.toCharArray();
        System.out.println(chars[0] + "" + chars[1] + chars[2]);
        int count = 0;
        for (int i = 3; i < chars.length; i++) {
            if ((StringUtils.selectLength(chars[0] + "" + chars[1] + chars[2]) != 6) || !list.contains(chars[i])) {
                count++;
            }
        }
        if (count != 0) {
            System.out.println("请核对正确的证件号....");
        } else {
            System.out.println("证件号符合规定...");
        }
    }

    public static String subring(String str, int num, String sgin) {
        /**
         * @Description: 截取指定的字符串长度, 中文按2个长度计算, 后面加上后缀sgin
         * @methodName: subring
         * @Param: [str, num, sgin]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/11/9 10:24
         */
        if (isEmpyStr(str)) {
            throw new RuntimeException("参数不可为空....");
        }
        String regExp = "^[\\u4e00-\\u9fa5]|[\\uff08]|[\\uff09]$";
        char[] chars = str.toCharArray();
        int i = 0;
        int j = 0;
        for (char aChar : chars) {
            boolean flag = String.valueOf(aChar).matches(regExp);
           /* i+= flag ? 2:1;
            j++;
            if (i==num){
                break;
            }
            if (i>num){
               j--;
               break;
            }*/
            if (flag) {
                i += 2;
                j++;
            }
            if (!flag) {
                i += 1;
                j++;
            }
            if (i == num) {
                break;
            }
            if (i > num) {
                j--;
                break;
            }
        }
        return str.substring(0, j) + sgin;
    }

    @Test
    public void test16() {
        System.out.println(StringUtils.subring("盛重阳aaa586", 5, "00"));
    }

    public static int getLength(String str) {
        /**
         * @Description: 获取字符串的长度中文字算两个长度
         * @methodName: getLength
         * @Param: [str]
         * @return: int
         * @Author: scyang
         * @Date: 2019/10/13 10:31
         */
        int num = 0;
        if (str == null || "".equals(str)) {
            return num;
        }
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) > 128) {
                num = num + 2;
            } else {
                num = num + 1;
            }
        }
        return num;
    }

    @Test
    public void test07() {
        System.out.println(StringUtils.getLength("盛重123阳"));
    }

    public static int zwToMath(String zwNum) {
        /**
         * @Description: 将中文数字转换成阿拉伯数字
         * @methodName: zwToMath
         * @Param: [zwNum]
         * @return: int
         * @Author: scyang
         * @Date: 2019/10/13 13:33
         */
        String[] zw_Num = {"壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾"};
        int[] sz_Num = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int i = 0; i < zw_Num.length; i++) {
            if (zw_Num[i].equals(zwNum)) {
                return sz_Num[i];
            }
        }
        return 0;
    }

    @Test
    public void test08() {
        System.out.println(StringUtils.zwToMath(""));
        System.out.println(StringUtils.zwToMath(null));
        System.out.println(StringUtils.zwToMath("捌"));
    }

    public static String mathToZw(int szNum) {
        /**
         * @Description: 将阿拉伯数字转换成中文数字
         * @methodName: mathToZw
         * @Param: [szNum]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/13 13:49
         */
        String[] zw_Num = {"壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾"};
        int[] sz_Num = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int i = 0; i < sz_Num.length; i++) {
            if (szNum == sz_Num[i]) {
                return zw_Num[i];
            }
        }
        return "";
    }

    @Test
    public void test09() {
        System.out.println(StringUtils.mathToZw(6));
        System.out.println(StringUtils.mathToZw(0));
        System.out.println(StringUtils.mathToZw(10));
    }

    public static boolean isIdCard(String idCard) {
        /**
         * @Description: 正则表达式校验身份账号 422202 19910909 3496
         * @methodName: isIdCard
         * @Param: [idCard]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/13 14:39
         */
        idCard = idCard.replaceAll(" ", "");
        String regExp = "^[1-9]\\d{5}(19\\d{2}||20\\d{2})(0[1-9]|1[0,1,2])(0[1-9]|[1,2]\\d||3[0,1])(\\d{4}|\\d{3}X)$";
        return idCard.matches(regExp);
    }

    public Date getDateFromStr(String dateStr, String pattern) throws Exception {
        /**
         * @Description: 将字符串日期转换date日期
         * @methodName: getDateFromStr
         * @Param: [dateStr, pattern]
         * @return: java.util.Date
         * @Author: scyang
         * @Date: 2019/10/13 14:58
         */
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(dateStr);
    }

    public static Date getBirthdayFromIdCard(String idCard) throws Exception {
        /**
         * @Description: 根据身份证号获取出生日期
         * @methodName: getBirthdayFromIdCard
         * @Param: [idCard]
         * @return: java.util.Date
         * @Author: scyang
         * @Date: 2019/10/13 15:14
         */
        if (!new StringUtils().isIdCard(idCard)) {
            throw new RuntimeException("身份证格式不对,请重新输入正确的格式....");
        }
        idCard = idCard.replaceAll(" ", "");
        String birthday = idCard.substring(6, 14);
        return new StringUtils().getDateFromStr(birthday, "yyyyMMdd");
    }

    @Test
    public void test10() throws Exception {
        System.out.println(new StringUtils().isIdCard("422202 19911016 349X"));
        System.out.println(StringUtils.getBirthdayFromIdCard("422202 19911016 349X"));
    }

    public static String getGenderFromIdCard(String idCard) {
        /**
         * @Description: 根据身份证号判断性别 身份证第17位奇数为男,偶数为女
         * @methodName: getGenderFromIdCard
         * @Param: [idCard]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/13 15:20
         */
        if (!new StringUtils().isIdCard(idCard)) {
            throw new RuntimeException("身份证格式不对,请重新输入正确的格式....");
        }
        idCard = idCard.replaceAll(" ", "");
        String genderNum = idCard.substring(16, idCard.length() - 1);

        if (Integer.valueOf(genderNum) % 2 == 0) {
            return "女";
        }
        return "男";
    }

    @Test
    public void test11() {
        System.out.println(StringUtils.getGenderFromIdCard("422202 19910909 3496"));
        System.out.println(StringUtils.getGenderFromIdCard("422202 19911016 348X"));
    }

    public static int getAgeFromIdCard(String idCard) throws Exception {
        /**
         * @Description: 根据身份证号得到年龄
         * @methodName: getAgeFromIdCard
         * @Param: [idCard]
         * @return: int
         * @Author: scyang
         * @Date: 2019/10/13 16:07
         */
        if (!new StringUtils().isIdCard(idCard)) {
            throw new RuntimeException("身份证格式不对,请重新输入正确的格式....");
        }
        /** 得到出生时的年份*/
        Calendar birthdayInstance = Calendar.getInstance();
        birthdayInstance.setTime(getBirthdayFromIdCard(idCard));
        int birthdayYear = birthdayInstance.get(Calendar.YEAR);
        /** 得到当前时间的年份*/
        Calendar nowInstance = Calendar.getInstance();
        nowInstance.setTime(new Date());
        int nowYear = nowInstance.get(Calendar.YEAR);
        int ageNum = nowYear - birthdayYear;
        // birthdayInstance.add(Calendar.YEAR, ageNum);
        birthdayInstance.set(Calendar.YEAR, nowInstance.get(Calendar.YEAR));
        /** 今年的生日期在现在日期之前表示今年已经过了生日*/
        if (birthdayInstance.before(nowInstance)) {
            // birthdayInstance小于nowInstance
            return ageNum;
        }
        return ageNum - 1;
    }

    @Test
    public void test12() throws Exception {
        System.out.println(StringUtils.getAgeFromIdCard("422202 19910909 3496"));
    }

    public static String leftPad(String str, int maxLength, String sign) {
        /**
         * @Description: 字符串str左边用符号sign补位, 使其达到最大长度maxLength
         * @methodName: leftPad
         * @Param: [str, maxLength, sign]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/20 10:28
         */
        if (str == null || "".equals(str) || sign == null || "".equals(sign)) {
            throw new RuntimeException("字符串str或者符号sign不能为null或者空串...");
        }
        if (maxLength < StringUtils.getLength(str)) {
            throw new RuntimeException("补位后的长度不能小于原字符串str的长度...");
        }
        StringBuffer sb = new StringBuffer();
        int length = StringUtils.getLength(str);
        for (int i = length; i < maxLength; i++) {
            sb.append(sign);
        }
        return sb.append(str).toString();
    }

    @Test
    public void test13() {
        System.out.println(StringUtils.leftPad("盛重阳", 9, "s"));
    }

    public static String rightPad(String str, int maxLength, String sign) {
        /**
         * @Description: 字符串str右边用符号sign补位, 使其达到最大长度maxLength
         * @methodName: rightPad
         * @Param: [str, maxLength, sign]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/20 10:46
         */
        StringBuffer sb = new StringBuffer();
        int length = StringUtils.getLength(str);
        if (str == null || "".equals(str) || sign == null || "".equals(sign)) {
            throw new RuntimeException("字符串str或者符号sign不能为null或者空串...");
        } else if (maxLength < length) {
            throw new RuntimeException("补位后的长度不能小于原字符串str的长度...");
        } else {
            while (sb.length() + length < maxLength) {
                sb.append(sign);
            }
        }
        return str + sb;
    }

    @Test
    public void test14() {
        System.out.println(StringUtils.rightPad("盛重阳", 9, "a"));
    }

    public static String canelNullObj(Object obj) {
        /**
         * @Description: 将对象为null值的转换成空字符串
         * @methodName: canelNullObj
         * @Param: [obj]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/21 20:22
         */
        return obj == null ? "" : obj.toString();
    }

    @Test
    public void getMonth() {
        String month = "";
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < 13; i++) {
            switch (i) {
                case 1:
                    month = "一月份,";
                    sb.append(month);
                    break;
                case 2:
                    month = "二月份,";
                    sb.append(month);
                    break;
                case 3:
                    month = "三月份,";
                    sb.append(month);
                    break;
                case 4:
                    month = "四月份,";
                    sb.append(month);
                    break;
                case 5:
                    month = "五月份,";
                    sb.append(month);
                    break;
                case 6:
                    month = "六月份,";
                    sb.append(month);
                    break;
                case 7:
                    month = "七月份,";
                    sb.append(month);
                    break;
                case 8:
                    month = "八月份,";
                    sb.append(month);
                    break;
                case 9:
                    month = "九月份,";
                    sb.append(month);
                    break;
                case 10:
                    month = "十月份,";
                    sb.append(month);
                    break;
                case 11:
                    month = "十一月份,";
                    sb.append(month);
                    break;
                case 12:
                    month = "十二月份,";
                    sb.append(month);
                    break;
            }
        }
        System.out.println(sb.deleteCharAt(sb.lastIndexOf(",")));
    }

    public static String transferNulltoStr(Object obj) {
        /**
         * @Description: 将null,"null",""转换成""
         * @methodName: transferNulltoStr
         * @Param: [object]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/11/19 21:27
         */
        if (null == obj || "".equals(obj) || "null".equals(obj)) {
            return "";
        }
        return obj.toString();
    }

    @Test
    public void test17() {
        System.out.println(transferNulltoStr(""));
        System.out.println(transferNulltoStr(null));
        System.out.println(transferNulltoStr("null"));
        System.out.println(transferNulltoStr("盛重阳"));
        // System.out.println(transferNulltoStr(new User()));
    }

    public static boolean objIsNullOrEmpty(Object obj) {
        /**
         * @Description: 对象是否为null或空
         * @methodName: objIsNullOrEmpty
         * @Param: [obj]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/11/20 20:53
         */
        boolean isTrue = false;
        if (null == obj) {
            isTrue = true;
        } else if (obj instanceof String) {
            isTrue = "".equals(obj) || ((String) ((String) obj).trim()).isEmpty();
        } else if (obj instanceof Collection) {
            isTrue = ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            isTrue = ((Map) obj).isEmpty();
        } else if (obj instanceof Object[]) {
            isTrue = ((Object[]) obj).length == 0;
        }
        return isTrue;
    }

    @Test
    public void test18() {
        System.out.println(objIsNullOrEmpty(""));
        System.out.println(objIsNullOrEmpty(null));
        System.out.println(objIsNullOrEmpty("null"));
        System.out.println(objIsNullOrEmpty(new ArrayList<>()));
        System.out.println(objIsNullOrEmpty(new HashMap<>()));
        System.out.println(objIsNullOrEmpty(new String[1]) + " " + "数组");
        List list = new ArrayList();
        list.add(2);
        Map map = new HashMap();
        map.put(1, 2);
        System.out.println(objIsNullOrEmpty(list));
        System.out.println(objIsNullOrEmpty(map));
    }

    public static void objIsNullOrEmpty(Object obj, String desc) {
        /**
         * @Description: 对象为空或为null抛异常
         * @methodName: objIsNullOrEmpty
         * @Param: [obj, desc]
         * @return: void
         * @Author: scyang
         * @Date: 2019/11/20 21:05
         */
        if (null == obj) {
            throw new RuntimeException(desc + "参数为空或为null....");
        } else if (obj instanceof String && ((String) obj).isEmpty()) {
            throw new RuntimeException(desc + "参数为空或为null....");
        } else if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            throw new RuntimeException(desc + "参数为空或为null....");
        } else if (obj instanceof Map && ((Map) obj).isEmpty()) {
            throw new RuntimeException(desc + "参数为空或为null....");
        } else if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
            throw new RuntimeException(desc + "参数为空或为null....");
        }
    }

    @Test
    public void test19() {
        List list = new ArrayList();
        list.add(2);
        objIsNullOrEmpty(null, "list");
    }

    @Test
    public void test100() {
        List<BigDecimal> list = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        Collections.addAll(list, new BigDecimal("2"), new BigDecimal("3"), new BigDecimal("4"));
        for (BigDecimal decimal : list) {
            sum = sum.add(decimal);
            // System.out.println(decimal);
        }
        System.out.println(sum);
    }

    @Test
    public void test200() {
        int sum = 0;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        for (Integer integer : list) {
            sum = sum + integer;
        }
        System.out.println(sum);
    }

    @Test
    public void test300() {
        int sum = 0;
        for (int i = 0; i < 100; i++) {
            if (i / 2 == 1) {
                sum = sum + i;
            }
        }
        System.out.println(sum);
    }

    public static void validateParams(Object... obj) {
        /**
         * @Description: 校验参数是否为空, 为空抛出异常
         * @methodName: validateParams
         * @Param: [obj]
         * @return: void
         * @Author: scyang
         * @Date: 2019/12/9 20:11
         */
        if (obj == null) {
            throw new RuntimeException(obj + "的值不可为null...");

        }
        for (Object o : obj) {
            if (null == o) {
                throw new RuntimeException(o + "的值不可为null...");
            } else if (o instanceof String) {
                if ("".equals(((String) o).trim())) {
                    throw new RuntimeException(o + "的值不可为空...");
                }
            } else if (o instanceof Collection) {
                if (((Collection) o).isEmpty()) {
                    throw new RuntimeException(o + "的值不可为空...");
                }
            } else if (o instanceof Map) {
                if (((Map) o).isEmpty()) {
                    throw new RuntimeException(o + "的值不可为空...");
                }
            } else if (o instanceof Object[]) {
                if (((Object[]) o).length == 0) {
                    throw new RuntimeException(o + "的值不可为空...");
                }
            }
        }
    }

    @Test
    public void test20() {
        Map map = new HashMap();
        validateParams(null);
    }

    public static boolean validateParamsIsEmpty(Object... obj) {
        /**
         * @Description: 校验参数是否为空
         * @methodName: validateParams
         * @Param: [obj]
         * @return: void
         * @Author: scyang
         * @Date: 2019/12/9 20:11
         */
        boolean isTrue = false;
        if (obj == null) {
            isTrue = true;

        }
        for (Object o : obj) {
            if (null == o) {
                isTrue = true;
            } else if (o instanceof String) {
                if ("".equals(((String) o).trim())) {
                    isTrue = true;
                }
            } else if (o instanceof Collection) {
                if (((Collection) o).isEmpty()) {
                    isTrue = true;
                }
            } else if (o instanceof Map) {
                if (((Map) o).isEmpty()) {
                    isTrue = true;
                }
            } else if (o instanceof Object[]) {
                if (((Object[]) o).length == 0) {
                    isTrue = true;
                }
            }
        }
        return isTrue;
    }

    @Test
    public void test21() {
        System.out.println(validateParamsIsEmpty(null, "aa"));
    }

    /*******************************************************************************************************************************/
    public static final List<String> nameList = Arrays.asList("司马", "欧阳", "东方", "南宫", "上官",
            "夏侯", "诸葛", "宇文", "北堂", "西门",
            "司空", "公孙", "左丘", "长孙", "东郭",
            "皇甫", "慕容", "令狐", "鲜余", "百里",
            "端木", "尉迟", "长孙", "太史", "独孤");

    private static String createmask(int length) {
        /**
         * @Description: 创建掩饰符
         * @methodName: createmask
         * @Param: [length]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2020/4/3 22:56
         */
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append("*");
        }
        return sb.toString();
    }

    private static boolean isChinese(String plainText) {
        /**a
         * @Description: 判断是否全是中文
         * @methodName: isChunese
         * @Param: [plainText]
         * @return: boolean
         * @Author: scyang
         * @Date: 2020/4/4 0:43
         */
        String regExp = "^[\\u4e00-\\u9fa5]+$";
        return plainText.matches(regExp);
    }

    private static boolean isEnglish(String plainText) {
        /**
         * @Description: 判断是否包含英文 字母,数字,下划线至少出现一次
         * @methodName: isEnglish
         * @Param: [plainText]
         * @return: boolean
         * @Author: scyang
         * @Date: 2020/4/4 1:11
         */
        String regExp = "^[a-zA-Z\\s]+$";
        return plainText.matches(regExp);
    }

    public static String firstMask(String plainText, int length) {

        /**
         * @Description: 前length位掩码
         * @methodName: firstMask
         * @Param: [plainTest, length]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2020/4/3 23:11
         */
        plainText = plainText.replaceAll(" ", "").trim();
        if ("".equals(plainText.trim()) || null == plainText) {
            throw new RuntimeException("掩码的字符串不能为空或为null...");
        } else if (plainText.length() < length) {
            return createmask(plainText.length());
        }
        StringBuffer sb = new StringBuffer();
        return sb.append(createmask(length)).
                append(plainText.substring(length, plainText.length())).toString();
    }

    public static String lastMask(String plainText, int length) {
        /**
         * @Description: 字符串后length位掩码
         * @methodName: lastMask
         * @Param: [plainText, length]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2020/4/3 23:31
         */
        plainText = plainText.replaceAll(" ", "").trim();
        if ("".equals(plainText.trim()) || null == plainText) {
            throw new RuntimeException("掩码的字符串不能为空或为null...");
        } else if (plainText.length() < length) {
            return createmask(plainText.length());
        }
        return plainText.substring(0, plainText.length() - length)
                .concat(createmask(length));
    }

    public static String middleMask(String plainText, int prefixSize, int suffixSize) {
        /**
         * @Description: 中间掩码
         * @methodName: middleMask
         * @Param: [plainText, prefixSize, suffixSize]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2020/4/3 23:38
         */
        plainText = plainText.replaceAll(" ", "").trim();
        if ("".equals(plainText.trim()) || null == plainText) {
            throw new RuntimeException("掩码的字符串不能为空或为null...");
        } else if (plainText.length() < prefixSize + suffixSize) {
            return createmask(plainText.length());
        }
        return plainText.substring(0, prefixSize)
                .concat(createmask(plainText.length() - prefixSize - suffixSize))
                .concat(plainText.substring(plainText.length() - suffixSize, plainText.length()));
    }

    public static String mailMask(String plainText) {
        /**
         * @Description: 邮箱掩码 @前面掩码 如:*******@qq.com
         * @methodName: mailMask
         * @Param: [plainText]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2020/4/3 23:59
         */
        plainText = plainText.replaceAll(" ", "").trim();
        if ("".equals(plainText) || null == plainText) {
            throw new RuntimeException("掩码的字符串不能为空或为null...");
        }
        int index = plainText.indexOf("@");
        return createmask(index).
                concat(plainText.substring(index, plainText.length()));
    }

    public static String idCardMask(String plainText) {
        /**
         * @Description: 省份证掩码 如: 422202********3496
         * @methodName: idCardMask
         * @Param: [plainText]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2020/4/4 0:12
         */
        plainText = plainText.replaceAll(" ", "").trim();
        if ("".equals(plainText) || null == plainText) {
            throw new RuntimeException("掩码的字符串不能为空或为null...");
        }
        return plainText.substring(0, 6)
                .concat(createmask(8))
                .concat(plainText.substring(14, 18));
    }

    public static String renameMask(String plainText) {
        /**
         * @Description: 复姓掩码 司马**
         * @methodName: renameMask
         * @Param: [plainText]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2020/4/4 1:18
         */
        plainText = plainText.replaceAll(" ", "").trim();
        if ("".equals(plainText) || null == plainText) {
            throw new RuntimeException("掩码的字符串不能为空或为null...");
        }
        if (plainText.length() > 2 && isChinese(plainText)) {
            for (String name : nameList) {
                if (plainText.contains(name)) {
                    return lastMask(plainText, plainText.length() - 2);
                }
            }
        }
        return null;
    }

    @Test
    public void test25() {
        System.out.println(createmask(6));
        System.out.println(firstMask("25", 4));
        System.out.println(firstMask("13297053048", 4));
        System.out.println(lastMask("132 9705 3048", 4));
        System.out.println(middleMask("132 9705 3048", 3, 4));
        System.out.println(mailMask("348691356@qq.com"));
        System.out.println(idCardMask("422202 19910909 3496"));
        String c = " a b ";
        System.out.println(c.length());
        // c=c.replaceAll(" ", "");
        System.out.println(c);
        System.out.println(c.trim().length());
        System.out.println(isChinese(""));
        System.out.println(renameMask("司马风"));
    }

    public static Integer getCurrentTerm(LocalDate firstDate, LocalDate endDate, LocalDate batchDate, Integer totalTerm) {
        /**
         * @Description: 获取当前的日期在那个期次里
         * @methodName: getCurrentTerm
         * @Param: [firstDate, endDate, batchDate, totalTerm]
         * @return: java.lang.Integer
         * @Author: scyang
         * @Date: 2021/4/3 23:00
         *
         * 2021-05-15
         * 2021-06-15
         * 2021-07-15
         * 2021-08-15
         * 2021-09-15
         * 2021-10-15
         */
        int num = 0;
        for (LocalDate localDate = firstDate; !localDate.isAfter(endDate); localDate = localDate.plusMonths(1)) {
            num++;
            if (!batchDate.isAfter(localDate)) {
                return num;
            }
        }
        return totalTerm;
    }

    @Test
    public void test26() {
        System.out.println(getCurrentTerm(LocalDate.parse("2021-05-15"), LocalDate.parse("2021-10-15"), LocalDate.parse("2020-10-15"), 6));
        System.out.println(getCurrentTerm(LocalDate.parse("2021-05-15"), LocalDate.parse("2021-10-15"), LocalDate.parse("2022-10-15"), 6));
        System.out.println(getCurrentTerm(LocalDate.parse("2021-05-15"), LocalDate.parse("2021-10-15"), LocalDate.parse("2021-05-15"), 6));
        System.out.println(getCurrentTerm(LocalDate.parse("2021-05-15"), LocalDate.parse("2021-10-15"), LocalDate.parse("2021-05-16"), 6));
        System.out.println(getCurrentTerm(LocalDate.parse("2021-05-15"), LocalDate.parse("2021-10-15"), LocalDate.parse("2021-09-14"), 6));
        System.out.println(getCurrentTerm(LocalDate.parse("2021-05-15"), LocalDate.parse("2021-10-15"), LocalDate.parse("2021-09-15"), 6));
        System.out.println(getCurrentTerm(LocalDate.parse("2021-05-15"), LocalDate.parse("2021-10-15"), LocalDate.parse("2021-09-16"), 6));
    }
}

