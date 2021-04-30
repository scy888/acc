package common;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 日期操作工具类
 */
public class DateUtil {
        public static final String FORMATONE="yyyy-MM-dd HH:mm:ss";
        public static final String FORMATTWO="yyyy年MM月dd日 HH时mm分ss秒";
        public static final String FORMATTHREE="yyyy-MM-dd";
        public static final String FORMATFOUR="yyyy年MM月dd日";
        public static final String FORMATFIVE="yyyyMMddHHmmss";

        /**
     * 日期转换-  String -> Date
     *
     * @param dateString 字符串时间
     * @return Date类型信息
     * @throws Exception 抛出异常
     */
    public static Date parseString2Date(String dateString) throws Exception {
        if (dateString == null) {
            return null;
        }
        return parseString2Date(dateString, "yyyy-MM-dd");
    }

    /**
     * 日期转换-  String -> Date
     *
     * @param dateString 字符串时间
     * @param pattern    格式模板
     * @return Date类型信息
     * @throws Exception 抛出异常
     */
    public static Date parseString2Date(String dateString, String pattern) throws Exception {
        if (dateString == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = sdf.parse(dateString);
        return date;
    }

    /**
     * 日期转换 Date -> String
     *
     * @param date Date类型信息
     * @return 字符串时间
     * @throws Exception 抛出异常
     */
    public static String parseDate2String(Date date) throws Exception {
        if (date == null) {
            return null;
        }
        return parseDate2String(date, "yyyy-MM-dd");
    }

    /**
     * 日期转换 Date -> String
     *
     * @param date    Date类型信息
     * @param pattern 格式模板
     * @return 字符串时间
     * @throws Exception 抛出异常
     */
    public static String parseDate2String(Date date, String pattern) throws Exception {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String strDate = sdf.format(date);
        return strDate;
    }
    /**
     * 获取当前日期的本周一是几号
     *
     * @return 本周一的日期
     */
    public static Date getThisWeekMonday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    /**
     * 获取当前日期周的最后一天
     *
     * @return 当前日期周的最后一天
     */
    public static Date getSundayOfThisWeek() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        c.add(Calendar.DATE, -dayOfWeek + 7);
        return c.getTime();
    }

    /**
     * 根据日期区间获取月份列表
     *
     * @param minDate 开始时间
     * @param maxDate 结束时间
     * @return 月份列表
     * @throws Exception
     */
    public static List<String> getMonthBetween(String minDate, String maxDate, String format) throws Exception {
        ArrayList<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        SimpleDateFormat sdf2 = new SimpleDateFormat(format);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf2.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }

    /**
     * 根据日期获取年度中的周索引
     *
     * @param date 日期
     * @return 周索引
     * @throws Exception
     */
    public static Integer getWeekOfYear(String date) throws Exception {
        Date useDate = parseString2Date(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(useDate);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 根据年份获取年中周列表
     *
     * @param year 年分
     * @return 周列表
     * @throws Exception
     */
    public static Map<Integer, String> getWeeksOfYear(String year) throws Exception {
        Date useDate = parseString2Date(year, "yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(useDate);
        //获取年中周数量
        int weeksCount = cal.getWeeksInWeekYear();
        Map<Integer, String> mapWeeks = new HashMap<>(55);
        for (int i = 0; i < weeksCount; i++) {
            cal.get(Calendar.DAY_OF_YEAR);
            mapWeeks.put(i + 1, parseDate2String(getFirstDayOfWeek(cal.get(Calendar.YEAR), i)));
        }
        return mapWeeks;
    }

    /**
     * 获取某年的第几周的开始日期
     *
     * @param year 年分
     * @param week 周索引
     * @return 开始日期
     * @throws Exception
     */
    public static Date getFirstDayOfWeek(int year, int week) throws Exception {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);

        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, week * 7);

        return getFirstDayOfWeek(cal.getTime());
    }

    /**
     * 获取某年的第几周的结束日期
     *
     * @param year 年份
     * @param week 周索引
     * @return 结束日期
     * @throws Exception
     */
    public static Date getLastDayOfWeek(int year, int week) throws Exception {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);

        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, week * 7);

        return getLastDayOfWeek(cal.getTime());
    }

    /**
     * 获取当前时间所在周的开始日期
     *
     * @param date 当前时间
     * @return 开始时间
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c.getTime();
    }

    /**
     * 获取当前时间所在周的结束日期
     *
     * @param date 当前时间
     * @return 结束日期
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
        return c.getTime();
    }
    //获得上周一的日期
    public static Date geLastWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    //获得本周一的日期
    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    //获得下周一的日期
    public static Date getNextWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, 7);
        return cal.getTime();
    }

    //获得今天日期
    public static Date getToday(){
        return new Date();
    }

    //获得本月一日的日期
    public static Date getFirstDay4ThisMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        return calendar.getTime();
    }

    public static void main(String[] args) {
        try {
            System.out.println("本周一" + parseDate2String(getThisWeekMonday()));
            System.out.println("本月一日" + parseDate2String(getFirstDay4ThisMonth()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***********************************************************************/

    public static String DateToStr(Date date,String pattern){
        SimpleDateFormat sdf=new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date StrToDate(String strDate,String pattern) throws Exception {
        SimpleDateFormat sdf=new SimpleDateFormat(pattern);
        return sdf.parse(strDate);
    }

    public static int getAge(String strDate,Date date,String pattern) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat(pattern);
        return (int)((date.getTime()-sdf.parse(strDate).getTime())/(1000L*60*60*24*30*12));
    }

    public static String getAppointDate(int year,int month,int day,String pattern){
        /**
         * @Description: 得到指定的字符串日期
         * @methodName: getAppointDate
         * @Param: [date, pattern]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/9/18 20:24
         */
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }
    public static int[] getDateNum(){
        /**
         * @Description: 得到日期数放入数组中
         * @methodName: getDateNum
         * @Param: []
         * @return: int[]
         * @Author: scyang
         * @Date: 2019/9/18 20:41
         */
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int[] arr= {year,month,day};
        return arr;
    }
    public static boolean isCurrentDate(Date date,String strDate,String pattern){
        /**
         * @Description: 判断某个日期是否是当前日期
         * @methodName: isCurrentDate
         * @Param: [date, strDate, pattern]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/9/18 20:45
         */
        String format = new SimpleDateFormat(pattern).format(date);
        if (format.equals(strDate)){
            return true;
        }
        return false;
    }
    public static String changeStrDate(String strDate,String pattern1,String pattern2) throws ParseException {
        /**
         * @Description: 日期间的转换2019-09-18 转换成 2019年9月18日
         * @methodName: changeStrDate
         * @Param: [strDate, pattern1, pattern2]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/9/18 20:53
         */
        Date date = new SimpleDateFormat(pattern1).parse(strDate);
        return new SimpleDateFormat(pattern2).format(date);
    }
    public static boolean isSameDate(String strDate1,String strDate2,String pattern1,String pattern2) throws ParseException {
        /**
         * @Description: 判定两个时间是否同一天
         * @methodName: isSameDate
         * @Param: [strDate1, strDate2, pattern1, pattern2]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/9/18 21:07
         */
        Long oneTime = new SimpleDateFormat(pattern1).parse(strDate1).getTime();
        Long twoTime = new SimpleDateFormat(pattern2).parse(strDate2).getTime();
        if (oneTime.longValue()==twoTime.longValue()){
            return true;
        }
        return false;
    }
    public static String getAddDate(Date date,int day,String pattern){
        /**
         * @Description: 在当前时间上加上天数
         * @methodName: getAddDate
         * @Param: [date, day, pattern]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/9/18 21:13
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //calendar.add(Calendar.DAY_OF_MONTH, 2);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+day);
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }
    public static Date getDate(int year,int month,int day){
        /**
         * @Description: 获取指定的Date日期
         * @methodName: getDate
         * @Param: [year, month, day]
         * @return: java.util.Date
         * @Author: scyang
         * @Date: 2019/9/18 21:22
         */
        return new Date(year-1900,month-1,day);
    }
    public static String getStrDate(int year,int month,int day,String pattern){
        /**
         * @Description: 获取指定的字符串日期
         * @methodName: getStrDate
         * @Param: [year, month, day, pattern]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/9/18 21:26
         */
        Date date=new Date(year-1900, month-1, day);
        return new SimpleDateFormat(pattern).format(date);
    }
    public static int getBettwenDay(String strDate1,String strDate2,String pattern) throws ParseException {
        /**
         * @Description: 获取两个日期之间相差天数
         * @methodName: getBettwenDay
         * @Param: [strDate1, strDate2, pattern]
         * @return: int
         * @Author: scyang
         * @Date: 2019/9/18 21:33
         */
        long time1 = new SimpleDateFormat(pattern).parse(strDate1).getTime();
        long time2 = new SimpleDateFormat(pattern).parse(strDate2).getTime();
        if (time1<time2){
            return (int) ((time2 - time1) / (1000L*60*60* 24));
        }
        return (int) ((time1 - time2) / (1000L*60*60*24));
    }
    public static int getBetweenDay(Date startDate,Date endDate){
        long time1 = startDate.getTime();
        long time2 = endDate.getTime();
        if (time1-time2>0){
            return (int) ((time1-time2)/(1000L*60*60*24));
        }
       return (int) ((time2-time1)/(1000L*60*60*24));
    }
    @Test
    public void test000(){
        System.out.println(getBetweenDay(new Date(1991-1900,11-1,9), new Date(1991-1900,10-1,10)));
    }
    public static String getDescriptionDate(Date date){
        /**
         * @Description: 日期描叙
         * @methodName: getDescriptionDate
         * @Param: [date]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/9/18 21:47
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year+"年"+month+"月"+day+"日";
    }
    public static Date getAddDate(Date date,int numDate){
        /**
         * @Description: 指定的Date日期加减天数
         * @methodName: getAddDate
         * @Param: [date, numDate]
         * @return: java.util.Date
         * @Author: scyang
         * @Date: 2019/11/1 2:05
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, numDate);
       // calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+numDate);
        return calendar.getTime();
    }
    public static Date addDate(Date date,int num){
        /**
         * @Description: 时间添加多少天
         * @methodName: addDate
         * @Param: [date, num]
         * @return: java.util.Date
         * @Author: scyang
         * @Date: 2020/4/25 16:13
         */
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.DAY_OF_MONTH, num);
        return instance.getTime();
    }
    public static boolean isWeekDay(Date date){
        /**
         * @Description: 判断当前日期是否是工作日
         * @methodName: isWeekDay
         * @Param: [date]
         * @return: boolean
         * @Author: scyang
         * @Date: 2020/5/8 19:51
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
        return !(week==0||week==6);
    }
    public static boolean isBetweenDate(Date date,String startDate,String endDate) throws Exception {
        /**
         * @Description: 判断是否在某段时间内
         * @methodName: isBetweenDate
         * @Param: [date, startDate, endDate]
         * @return: boolean
         * @Author: scyang
         * @Date: 2020/5/8 22:00
         */
        SimpleDateFormat sdf1=new SimpleDateFormat(FORMATTHREE);
        SimpleDateFormat sdf2=new SimpleDateFormat(FORMATONE);
        Date parse_start = sdf2.parse(sdf1.format(date) + " " + startDate);
        Date parse_end = sdf2.parse(sdf1.format(date) + " " + endDate);
        if (date.after(parse_start)&&date.before(parse_end)){
            return true;
        }
        return false;
    }
    public static Date appointDate(Date date,String dateStr) throws Exception {
        /**
         * @Description: 设置当天的指定时间
         * @methodName: appointDate
         * @Param: [date, dateStr]
         * @return: java.util.Date
         * @Author: scyang
         * @Date: 2020/5/8 22:21
         */
        SimpleDateFormat sdf1=new SimpleDateFormat(FORMATTHREE);
        SimpleDateFormat sdf2=new SimpleDateFormat(FORMATONE);
      return sdf2.parse(sdf1.format(date)+" "+dateStr);
    }
    @Test
    public void test() throws Exception {
        System.out.println(TimeUnit.DAYS.toHours(2));
        TimeUnit.MILLISECONDS.sleep(1000);
        System.out.println(TimeUnit.HOURS.toDays(24));
        System.out.println(TimeUnit.DAYS.convert(48, TimeUnit.HOURS));
        TimeUnit hours = TimeUnit.HOURS;
        System.out.println(hours);
        System.out.println("===============================================");
        System.out.println(addDate(new Date(), -20));
        System.out.println(isWeekDay(new Date()));
        System.out.println(isBetweenDate(new Date(), "22:00:00", "22:30:00"));
        System.out.println(appointDate(new Date(), "08:08:08"));
    }
}
