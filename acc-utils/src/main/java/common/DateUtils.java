package common;

import lombok.Data;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: com.tensquare.test.controller
 * @date: 2020-07-05 20:07:15
 * @describe:
 */
@Data
public class DateUtils {
    private final String PATTERN_ONE="yyyy-MM-dd";
    private final String PATTERN_TWO="yyyy年MM月dd日";
    private final String PATTERN_THREE="yyyyMMdd";
    private final String PATTERN_FOUR="yyyyMMddHHmmss";
    private final String PATTERN_FIVE="yyyy-MM-dd HH:mm:ss";
    private final String PATTERN_SIX="yyyy年MM月dd日 HH时mm分ss秒";

    public String getDateStr(int year,int month,int day,String pattern){
        String dateStr = LocalDate.of(year, month, day).format(DateTimeFormatter.ofPattern(pattern));
        return dateStr;
    }

    public String getDateStr(int year,int month,int day,int hour,int minute,int second, String pattern){
        String dateStr = LocalDateTime.of(year, month,day ,hour , minute,second ).format(DateTimeFormatter.ofPattern(pattern));

        return dateStr;
    }

    public LocalDate getLocalDate(String dateStr,String pattern){
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        return localDate;
    }

    public LocalDateTime getLocalDateTime(String dateStr,String pattern){
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        return localDateTime;
    }

    @Test
    public void test01(){
        DateUtils dateUtils = new DateUtils();
        System.out.println(dateUtils.getDateStr(2020, 7, 5, dateUtils.PATTERN_ONE));
        System.out.println(dateUtils.getDateStr(2020, 7, 5, dateUtils.PATTERN_TWO));
        System.out.println(dateUtils.getDateStr(2020, 7, 5, dateUtils.PATTERN_THREE));
        System.out.println(dateUtils.getDateStr(2020, 7, 5, 21, 3, 12, dateUtils.PATTERN_FOUR));
        System.out.println(dateUtils.getDateStr(2020, 7, 5, 21, 3, 12, dateUtils.PATTERN_FIVE));
        System.out.println(dateUtils.getDateStr(2020, 7, 5, 21, 3, 12, dateUtils.PATTERN_SIX));
        System.out.println("============================================================");

        System.out.println(dateUtils.getLocalDate("2020-07-05", dateUtils.PATTERN_ONE));
        System.out.println(dateUtils.getLocalDate("2020年07月05日", dateUtils.PATTERN_TWO));
        System.out.println(dateUtils.getLocalDate("20200705", dateUtils.PATTERN_THREE));
        System.out.println(dateUtils.getLocalDateTime("2020-07-05 15:35:36", dateUtils.PATTERN_FIVE));
        System.out.println(dateUtils.getLocalDateTime("2020年07月05日 15时35分36秒", dateUtils.PATTERN_SIX));
        System.out.println(dateUtils.getLocalDateTime("20200705153536", dateUtils.PATTERN_FOUR));

    }
}
