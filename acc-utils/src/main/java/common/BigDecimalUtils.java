package common;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author: scyang
 * @program: ssm_super
 * @date: 2019-10-08 20:45:17
 * @describe:
 */
public class BigDecimalUtils {
    public static BigDecimal getBigdecimalFromStr(String value) {
        /**
         * @Description: 将字符串数字转化为BigDecimal数字
         * @methodName: getBigdecimalFromStr
         * @Param: [value]
         * @return: java.math.BigDecimal
         * @Author: scyang
         * @Date: 2019/10/8 20:53
         */
        if (value == null || value == "") {
            return new BigDecimal("0");
        }
        return new BigDecimal(value);
    }

    @Test
    public void test01() {
        System.out.println(BigDecimalUtils.getBigdecimalFromStr(null));
        System.out.println(BigDecimalUtils.getBigdecimalFromStr(""));
        System.out.println(BigDecimalUtils.getBigdecimalFromStr("12"));
    }

    public static String getStrFromBigDecimal(BigDecimal bigDecimal) {
        /**
         * @Description: 将BigDecimal数字转化成字符串数字
         * @methodName: getStrFromBigDecimal
         * @Param: [bigDecimal]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/8 21:00
         */
        if (bigDecimal == null) {
            return "";
        }
        return String.valueOf(bigDecimal);// bigDecimal.toString();
    }

    @Test
    public void test02() {
        System.out.println(BigDecimalUtils.getStrFromBigDecimal(null));
        System.out.println(BigDecimalUtils.getStrFromBigDecimal(new BigDecimal(0)));
        System.out.println(BigDecimalUtils.getStrFromBigDecimal(new BigDecimal("3.3")));
        System.out.println(BigDecimalUtils.getStrFromBigDecimal(BigDecimal.valueOf(3.3)));
    }

    public static BigDecimal getMin(BigDecimal one, BigDecimal two) {
        /**
         * @Description: 返回两个中较小的值
         * @methodName: getMin
         * @Param: [one, two]
         * @return: java.math.BigDecimal
         * @Author: scyang
         * @Date: 2019/10/8 21:07
         */
        if (one == null || two == null) {
            return new BigDecimal(0);
        }
        return one.compareTo(two) < 0 ? one : two;
    }

    @Test
    public void test03() {
        System.out.println(BigDecimalUtils.getMin(null, null));
        System.out.println(BigDecimalUtils.getMin(new BigDecimal(3), new BigDecimal(5)));
        System.out.println(BigDecimalUtils.getMin(new BigDecimal("7.5"), new BigDecimal("7.2")));
    }

    public static BigDecimal getMax(BigDecimal one, BigDecimal two) {
        /**
         * @Description: 返回两个中较大的数值
         * @methodName: getMax
         * @Param: [one, two]
         * @return: java.math.BigDecimal
         * @Author: scyang
         * @Date: 2019/10/8 21:13
         */
        if (one == null || two == null) {
            return BigDecimal.ZERO;
        }
        return one.compareTo(two) > 0 ? one : two;
    }

    @Test
    public void test04() {
        System.out.println(BigDecimalUtils.getMax(null, null));
        System.out.println(BigDecimalUtils.getMax(new BigDecimal("1.2"), new BigDecimal("1.5")));
        System.out.println(BigDecimalUtils.getMax(new BigDecimal("1.5"), new BigDecimal("1.2")));
    }

    public static BigDecimal toPercent(BigDecimal num) {
        /**
         * @Description: 返回一个百分数
         * @methodName: toPercent
         * @Param: [num]
         * @return: java.math.BigDecimal
         * @Author: scyang
         * @Date: 2019/10/8 21:19
         */
        if (num == null) {
            return BigDecimal.ZERO;
        }
        return num.divide(new BigDecimal("100"));
    }

    @Test
    public void test05() {
        System.out.println(BigDecimalUtils.toPercent(null));
        System.out.println(BigDecimalUtils.toPercent(new BigDecimal(50)));
    }

    public static BigDecimal minusToZero(BigDecimal num) {
        /**
         * @Description: 负数转为零
         * @methodName: minusToZero
         * @Param: [num]
         * @return: java.math.BigDecimal
         * @Author: scyang
         * @Date: 2019/10/8 21:25
         */
        if (num == null) {
            return BigDecimal.ZERO;
        }
        return num.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : num;
    }

    @Test
    public void test6() {
        System.out.println(BigDecimalUtils.minusToZero(null));
        System.out.println(BigDecimalUtils.minusToZero(new BigDecimal("-3.6")));
        System.out.println(BigDecimalUtils.minusToZero(new BigDecimal(3.6)));
    }

    public static boolean isEquals(BigDecimal one, BigDecimal two) {
        /**
         * @Description: 两个数是否相等
         * @methodName: isEquals
         * @Param: [one, two]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/8 21:36
         */
        if (one == null || two == null) {
            return false;
        }
        return one.compareTo(two) == 0;
    }

    @Test
    public void test07() {
        System.out.println(BigDecimalUtils.isEquals(null, null));
        System.out.println(BigDecimalUtils.isEquals(new BigDecimal("1.2"), new BigDecimal("1.2")));
        System.out.println(BigDecimalUtils.isEquals(new BigDecimal("1.2"), new BigDecimal("1.21")));
    }

    public static boolean isGreatZero(BigDecimal num) {
        /**
         * @Description: 判断是否大于零
         * @methodName: isGreatZero
         * @Param: [num]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/8 21:45
         */
        if (num == null) {
            return false;
        }
        return num.compareTo(BigDecimal.ZERO) >= 0;
    }

    @Test
    public void test08() {
        System.out.println(BigDecimalUtils.isGreatZero(null));
        System.out.println(BigDecimalUtils.isGreatZero(BigDecimal.ZERO));
        System.out.println(BigDecimalUtils.isGreatZero(new BigDecimal("1.2")));
        System.out.println(BigDecimalUtils.isGreatZero(new BigDecimal("-1.2")));
    }

    public static boolean isLessZero(BigDecimal num) {
        /**
         * @Description: 判断是否小于零
         * @methodName: isLessZero
         * @Param: [num]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/8 21:49
         */
        if (num == null) {
            return false;
        }
        return !BigDecimalUtils.isGreatZero(num);
    }

    @Test
    public void test09() {
        System.out.println(BigDecimalUtils.isLessZero(null));
        System.out.println(BigDecimalUtils.isLessZero(BigDecimal.ZERO));
        System.out.println(BigDecimalUtils.isLessZero(new BigDecimal("1.2")));
        System.out.println(BigDecimalUtils.isLessZero(new BigDecimal("-1.2")));
    }

    public static boolean isNull(BigDecimal num) {
        /**
         * @Description: 判断是否为null
         * @methodName: isNull
         * @Param: [num]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/8 21:54
         */if (num == null) {
            return true;
        }
        return false;
    }

    @Test
    public void test10() {
        System.out.println(BigDecimalUtils.isNull(null));
        System.out.println(BigDecimalUtils.isNull(BigDecimal.ZERO));
    }

    public static BigDecimal add(BigDecimal number, Integer num) {
        /**
         * @Description: 两个数相加
         * @methodName: add
         * @Param: [number, num]
         * @return: java.math.BigDecimal
         * @Author: scyang
         * @Date: 2019/10/8 21:57
         */
        if (number == null || num == null) {
            return BigDecimal.ZERO;
        }
        return number.add(new BigDecimal(num));
    }

    @Test
    public void test11() {
        System.out.println(BigDecimalUtils.add(null, 2));
        System.out.println(BigDecimalUtils.add(BigDecimal.ZERO, null));
        System.out.println(BigDecimalUtils.add(new BigDecimal("3.2"), 2));
    }

    public static BigDecimal threeMatchs(boolean flag, BigDecimal num) {
        /**
         * @Description: 三目运算
         * @methodName: threeMatchs
         * @Param: [flag, num]
         * @return: java.math.BigDecimal
         * @Author: scyang
         * @Date: 2019/10/8 22:06
         */
        if (num == null) {
            return BigDecimal.ZERO;
        }
        return flag ? num : BigDecimal.ZERO;
    }

    @Test
    public void test12() {
        System.out.println(BigDecimalUtils.threeMatchs(true, null));
        System.out.println(BigDecimalUtils.threeMatchs(false, null));
        System.out.println(BigDecimalUtils.threeMatchs(true, new BigDecimal("3.5")));
        System.out.println(BigDecimalUtils.threeMatchs(false, new BigDecimal("3.5")));
    }

    public static BigDecimal forMat(BigDecimal num) {
        /**
         * @Description: 保留二位小数
         * @methodName: forMat
         * @Param: [num]
         * @return: java.math.BigDecimal
         * @Author: scyang
         * @Date: 2019/10/8 22:10
         */
        if (num == null) {
            return BigDecimal.ZERO;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return new BigDecimal(df.format(num));
    }

    @Test
    public void test13() {
        System.out.println(BigDecimalUtils.forMat(null));
        System.out.println(BigDecimalUtils.forMat(new BigDecimal("5.3237")));
        System.out.println(BigDecimalUtils.forMat(new BigDecimal("5.3283")));
    }

    public static BigDecimal getNDecimal(BigDecimal number, Integer num) {
        /**
         * @Description: 保留n位小数
         * @methodName: getNDecimal
         * @Param: [number, num]
         * @return: java.math.BigDecimal
         * @Author: scyang
         * @Date: 2019/10/8 22:19
         */
        if (number == null || num == null) {
            return BigDecimal.ZERO;
        }
        return number.setScale(num, BigDecimal.ROUND_HALF_UP);
    }

    @Test
    public void test14() {
        System.out.println(BigDecimalUtils.getNDecimal(null, 2));
        System.out.println(BigDecimalUtils.getNDecimal(null, null));
        System.out.println(BigDecimalUtils.getNDecimal(new BigDecimal("3.569"), null));
        System.out.println(BigDecimalUtils.getNDecimal(new BigDecimal("3.5362189"), 2));
        System.out.println(BigDecimalUtils.getNDecimal(new BigDecimal("3.5362189"), 3));
        System.out.println(BigDecimalUtils.getNDecimal(new BigDecimal("3.5362189"), 4));
    }

    public static boolean getMaxBigDecimal(BigDecimal one, BigDecimal two) {
        return one.compareTo(two) > 0;
    }

    @Test
    public void test15() {
        System.out.println(getMaxBigDecimal(new BigDecimal("8"), new BigDecimal("9")));
    }

    public static BigDecimal zeroTransferOne(BigDecimal num) {
        /**
         * @Description: num为零转化为一
         * @methodName: zeroTransferOne
         * @Param: [num]
         * @return: java.math.BigDecimal
         * @Author: scyang
         * @Date: 2019/11/30 22:07
         */
        if (num != null) {
            return num.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : num;
        }
        return null;
    }

    @Test
    public void test16() {
        System.out.println(zeroTransferOne(BigDecimal.ZERO));
        System.out.println(zeroTransferOne(BigDecimal.TEN));
        System.out.println(zeroTransferOne(null));
    }

    @Test
    public void test17() {
        BigDecimal one = new BigDecimal("0.78966");
        BigDecimal two = new BigDecimal("3");
        System.out.println(one.divide(two, 2, BigDecimal.ROUND_HALF_UP));
        System.out.println(one.divide(two, 2, BigDecimal.ROUND_UP).setScale(2, BigDecimal.ROUND_HALF_UP));
        DecimalFormat df = new DecimalFormat("0.00%");
        System.out.println(df.format(one));

        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
        System.out.println(currencyInstance.format(88888));

        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        System.out.println(percentInstance.format(0.7869));

    }
}
