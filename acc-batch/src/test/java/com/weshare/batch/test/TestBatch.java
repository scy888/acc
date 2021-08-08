package com.weshare.batch.test;

import com.weshare.batch.entity.Person;
import com.weshare.service.api.entity.RepaymentPlanReq;
import com.weshare.service.api.enums.TermStatusEnum;
import com.weshare.service.api.vo.Tuple2;
import common.*;
import jodd.io.ZipUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import static java.nio.file.Files.*;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.test
 * @date: 2021-05-14 16:45:40
 * @describe:
 */
public class TestBatch {

    @Test
    public void test01() {
        String url = "http://10.10.19.72:9004/riskClient/repayMakeCsv/2021-04-30/WS001020000?path=/YXMS/";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        String body = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class).getBody();
        System.out.println(body);
    }

    @Test
    public void test005() {
        List<String> list = Collections.singletonList("a");
        //list.set(0,"b");
        System.out.println(list);
        List<String> list1 = Arrays.asList("a");
        list1.set(0, "b");
        System.out.println(list1);

    }

    @Test
    public void test02() throws Exception {

        List<Person> personList = List.of(new Person().setId(SnowFlake.getInstance().nextId() + "").setName("赵敏").setAddress("蒙古").setAge(20).setBirthday(LocalDate.parse("1992-06-18")).setSalary(new BigDecimal("1992.0618")).setStatus(Person.Status.F),
                new Person().setId(SnowFlake.getInstance().nextId() + "").setName("周芷若").setAddress("峨嵋").setAge(19).setBirthday(LocalDate.parse("1992-05-12")).setSalary(new BigDecimal("1992.0512")).setStatus(Person.Status.M),
                new Person().setId(SnowFlake.getInstance().nextId() + "").setName("小昭").setAddress("波斯").setAge(18).setBirthday(LocalDate.parse("1994-10-10")).setSalary(new BigDecimal("1994.1010")).setStatus(Person.Status.O),
                new Person().setId(SnowFlake.getInstance().nextId() + "").setName("阿离").setAddress("灵蛇岛").setAge(17).setBirthday(LocalDate.parse("1995-12-16")).setSalary(new BigDecimal("1995.1216")).setStatus(Person.Status.N));


        List<String> list = personList.stream().map(e -> ReflectUtil.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        list.add(0, ReflectUtil.getFieldNames(Person.class, "batchDate"));
        System.out.println(String.format("原始数据的打印: \n%s", String.join(System.lineSeparator(), list)) + "\n");

        Path createPath = Paths.get("/person", "create");
        if (notExists(createPath)) {
            createDirectories(createPath);
        }
        //写入
        write(Paths.get(String.valueOf(createPath), "person1.csv"), String.join(System.lineSeparator(), list).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        writeString(Paths.get(String.valueOf(createPath), "person2.csv"), String.join(System.lineSeparator(), list), /*Charset.forName("gbk"),*/ StandardOpenOption.CREATE);
        write(Paths.get(String.valueOf(createPath), "person3.csv"), list,/*Charset.forName("gbk"),*/StandardOpenOption.CREATE);

        //读取
        byte[] bytes = readAllBytes(Paths.get(String.valueOf(createPath), "person1.csv"));
        String str = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(String.format("读取字节的直接打印:\n%s", str) + "\n");

        List<String> asList = Arrays.asList(str.split(System.lineSeparator()));
        System.out.println(String.format("读取字节通过切割换行符打印:\n%s", String.join(System.lineSeparator(), asList)) + "\n");

        String readString = readString(Paths.get(String.valueOf(createPath), "person2.csv"));
        System.out.println(String.format("读取字符串的直接打印:\n%s", readString) + "\n");

        asList = Arrays.asList(readString.split(System.lineSeparator()));
        System.out.println(String.format("读取符串通过切割换行符打印:\n%s", String.join(System.lineSeparator(), asList)) + "\n");

        List<String> readAllLines = readAllLines(Paths.get(String.valueOf(createPath), "person3.csv"));
        System.out.println(String.format("读取字符集合的直接打印:\n%s", String.join("\n", readAllLines)) + "\n");

        //压缩
        Path zipPath = Paths.get("/person", "zip");
        if (notExists(zipPath)) {
            createDirectories(zipPath);
        }

        //压缩输出流的具体位置
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(Paths.get(String.valueOf(zipPath), "person.zip").toUri())))//new File(String.valueOf(zipPath), "person.zip")
        ) {
            for (File file : Objects.requireNonNull(new File(String.valueOf(createPath)).listFiles())) {
//                FileInputStream inputStream = new FileInputStream(new File(file.getAbsolutePath()));
//                byte[] bytes_ = new byte[inputStream.available()];
//                inputStream.read(bytes_);
//                ZipUtil.addToZip(zipOutputStream, bytes_, file.getName(), "zip");

                // ZipUtil.addToZip(zipOutputStream, Files.readAllBytes(Paths.get(file.getAbsolutePath())), file.getName(), "zip");

                ZipUtil.addToZip(zipOutputStream, file, file.getName(), "zip", false);
            }
        }
        //解压
        Path unzipPath = Paths.get("/person", "unzip");
        if (notExists(unzipPath)) {
            createDirectories(unzipPath);
        }
        for (File file : Objects.requireNonNull(new File(zipPath.toUri()).listFiles())) {
            // ZipUtil.unzip(file.getAbsoluteFile().toString(),unzipPath.toString());
            ZipUtil.unzip(file, new File(unzipPath.toUri()));
        }

        //将解压的三个文件合并成一个文件
        Path concatPath = Paths.get("/person", "concat");
        if (Files.notExists(concatPath)) {
            Files.createDirectories(concatPath);
        }
        list.clear();
        for (File file : Objects.requireNonNull(new File(unzipPath.toUri()).listFiles())) {
            //list.add(Files.readString(Paths.get(file.getAbsolutePath())));
            if (file.getName().endsWith("1.csv")) {
                list.addAll(Files.readAllLines(Paths.get(file.getAbsolutePath())));
            }
            list.addAll(Files.readAllLines(Paths.get(file.getAbsolutePath())).stream().skip(1L).collect(Collectors.toList()));
        }
        //Files.writeString(Paths.get(String.valueOf(concatPath),"concat.csv"),String.join(System.lineSeparator(),list),StandardCharsets.UTF_8,StandardOpenOption.CREATE);
        Files.write(Paths.get(String.valueOf(concatPath), "concat.csv"), list, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }

    @Test
    public void test03() {
        List<Person> personList = List.of(new Person().setId(SnowFlake.getInstance().nextId() + "").setName("赵敏").setAddress("蒙古").setAge(20).setBirthday(LocalDate.parse("1992-06-18")).setSalary(new BigDecimal("1992.0618")).setStatus(Person.Status.F),
                new Person().setId(SnowFlake.getInstance().nextId() + "").setName("周芷若").setAddress("峨嵋").setAge(19).setBirthday(LocalDate.parse("1992-05-12")).setSalary(new BigDecimal("1992.0512")).setStatus(Person.Status.M),
                new Person().setId(SnowFlake.getInstance().nextId() + "").setName("小昭").setAddress("波斯").setAge(18).setBirthday(LocalDate.parse("1994-10-10")).setSalary(new BigDecimal("1994.1010")).setStatus(Person.Status.O),
                new Person().setId(SnowFlake.getInstance().nextId() + "").setName("阿离").setAddress("灵蛇岛").setAge(17).setBirthday(LocalDate.parse("1995-12-16")).setSalary(new BigDecimal("1995.1216")).setStatus(Person.Status.N));
        Long integer = personList.stream().map(e -> new Long(e.getAge())).reduce(0L, Long::sum);
        System.out.println(integer);
        IntSummaryStatistics statistics = personList.stream().mapToInt(Person::getAge).summaryStatistics();
        IntSummaryStatistics statistics1 = personList.stream().collect(Collectors.summarizingInt(Person::getAge));
        System.out.println(statistics.getCount());
        System.out.println(statistics.getSum());
        System.out.println(statistics.getMax());
        System.out.println(statistics.getMin());
        System.out.println(statistics.getAverage());
        System.out.println("=================================================");

        List<BigDecimal> bigDecimals = List.of(
                new BigDecimal("1.00"),
                new BigDecimal("2.00"),
                new BigDecimal("3.00"),
                new BigDecimal("4.00"));
        BigDecimal sum = bigDecimals.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        System.out.println(sum);
        BigDecimal max = bigDecimals.stream().reduce(BigDecimal::max).orElse(BigDecimal.ZERO);
        System.out.println(max);
        BigDecimal min = bigDecimals.stream().reduce(BigDecimal::min).orElse(BigDecimal.ZERO);
        System.out.println(min);
        BigDecimal avg = bigDecimals.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO).divide(BigDecimal.valueOf(bigDecimals.size()));
        System.out.println(avg);
        System.out.println("===============================================");
        int size = personList.size();
        Integer reduce = personList.stream().map(Person::getAge).reduce(0, Integer::sum) / size;

    }

    @Test
    public void test04() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode("scy199109091016");
        System.out.println(password);
        boolean matches = encoder.matches("scy199109091016", password);
        System.out.println(matches);
    }

    @Test
    public void test05() {
        System.out.println(LocalDateTime.now().with(ChronoField.DAY_OF_MONTH, 12).withNano(0));
        System.out.println(LocalDateTime.of(2021, 5, 12, 15, 30, 30));

        System.out.println(LocalDate.parse("2020-03-12").until(LocalDate.parse("2020-06-13"), ChronoUnit.DAYS));
        System.out.println(LocalDate.parse("2020-06-13").toEpochDay() - LocalDate.parse("2020-03-12").toEpochDay());
        System.out.println(Duration.between(LocalDateTime.parse("2020-03-12 12:20:35", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2020-06-13 12:20:40", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).toDays());
        System.out.println(LocalDateTime.parse("2020-03-12 12:20:35", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).until(LocalDateTime.parse("2020-06-13 12:20:40", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ChronoUnit.DAYS));
        System.out.println(Duration.between(LocalDate.parse("2020-03-12").atStartOfDay(), LocalDate.parse("2020-06-13").atStartOfDay()).toDays());
        System.out.println(LocalDate.parse("2020-03-12").until(LocalDate.parse("2020-06-13")).get(ChronoUnit.DAYS));
        System.out.println("==============================================================");
        System.out.println(Duration.between(LocalDateTime.parse("2020-03-12 12:20:35", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2020-03-12 12:21:40", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).toMinutes());
        System.out.println(Duration.between(LocalDateTime.parse("2020-03-12 12:20:35", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2020-03-12 12:21:40", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).getSeconds());
        System.out.println(Duration.between(LocalDateTime.parse("2020-03-12 12:20:35", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2020-03-12 12:21:40", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).toMillis());
    }

    @Test
    public void test00() {
        int j = 0;
        for (int i = 0; i < 3; i++) {
            new Thread() {
                @Override
                public void run() {
                    Thread.currentThread().setName(Thread.currentThread().getName() + "--");
                    System.out.println("当前线程名字: " + Thread.currentThread().getName());
                }
            }.start();
        }
    }

    @Test
    public void test001() {
        System.out.println(ChangeEnumUtils.changeEnum("WS121212", "termStatus", "本期未还", TermStatusEnum.class));
        System.out.println(ChangeEnumUtils.changeEnum("WS121212", "termStatus", "本期已还", TermStatusEnum.class));
        System.out.println(ChangeEnumUtils.changeEnum("WS121212", "termStatus", "逾期", TermStatusEnum.class));
    }

    @Test
    public void test002() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        String format = decimalFormat.format(new BigDecimal("0.12564"));
        System.out.println(format);
        System.out.println(format.substring(0, format.lastIndexOf("%")));

        LocalDateTime localDateTime = LocalDateTime.of(2021, 5, 24, 12, 12, 12);
        System.out.println(localDateTime);
        LocalDateTime localDateTime1 = LocalDateTime.now();
        System.out.println(localDateTime.with(ChronoField.MILLI_OF_SECOND, 0).toString().replace("T", " "));
        System.out.println(localDateTime1.with(ChronoField.MILLI_OF_SECOND, 0).toString().replace("T", " "));

        List orElseGet = (List) Optional.ofNullable(null).orElseGet(() -> {
            ArrayList<String> list = new ArrayList<>();
            list.add("a");
            return list;
        });
        System.out.println(orElseGet);
    }

    @Test
    public void test003() {

        LocalDate batchDate = LocalDate.parse("2020-05-15");
        List<RepaymentPlanReq> repaymentPlanReqs = List.of(
                new RepaymentPlanReq("YX-101", 1, batchDate.plusMonths(1), new BigDecimal(300), new BigDecimal(170), new BigDecimal(130), batchDate),
                new RepaymentPlanReq("YX-101", 2, batchDate.plusMonths(2), new BigDecimal(300), new BigDecimal(180), new BigDecimal(120), batchDate),
                new RepaymentPlanReq("YX-101", 3, batchDate.plusMonths(3), new BigDecimal(300), new BigDecimal(190), new BigDecimal(110), batchDate),
                new RepaymentPlanReq("YX-101", 4, batchDate.plusMonths(4), new BigDecimal(300), new BigDecimal(200), new BigDecimal(100), batchDate),
                new RepaymentPlanReq("YX-101", 5, batchDate.plusMonths(5), new BigDecimal(300), new BigDecimal(210), new BigDecimal(90), batchDate),
                new RepaymentPlanReq("YX-101", 6, batchDate.plusMonths(6), new BigDecimal(300), new BigDecimal(250), new BigDecimal(50), batchDate),

                new RepaymentPlanReq("YX-102", 1, batchDate.plusMonths(1), new BigDecimal(300), new BigDecimal(170), new BigDecimal(130), batchDate),
                new RepaymentPlanReq("YX-102", 2, batchDate.plusMonths(2), new BigDecimal(300), new BigDecimal(180), new BigDecimal(120), batchDate),
                new RepaymentPlanReq("YX-102", 3, batchDate.plusMonths(3), new BigDecimal(300), new BigDecimal(190), new BigDecimal(110), batchDate),
                new RepaymentPlanReq("YX-102", 4, batchDate.plusMonths(4), new BigDecimal(300), new BigDecimal(200), new BigDecimal(100), batchDate),
                new RepaymentPlanReq("YX-102", 5, batchDate.plusMonths(5), new BigDecimal(300), new BigDecimal(210), new BigDecimal(90), batchDate),
                new RepaymentPlanReq("YX-102", 6, batchDate.plusMonths(6), new BigDecimal(300), new BigDecimal(250), new BigDecimal(50), batchDate)
        );
        Map<String, RepaymentPlanReq> collect = repaymentPlanReqs.stream().collect(Collectors.toMap(e -> e.getDueBillNo() + "_" + e.getTerm(), Function.identity(), (a, b) -> b));
        for (Map.Entry<String, RepaymentPlanReq> entry : collect.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        System.out.println("=========================================================================");
        Map<String, List<RepaymentPlanReq>> map = repaymentPlanReqs.stream().collect(Collectors.groupingBy(e -> e.getDueBillNo() + "_" + e.getTerm()));
        for (Map.Entry<String, List<RepaymentPlanReq>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }


    private static int ticket = 100;

    public static void main(String[] args) {
        ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

        ReentrantLock lock = new ReentrantLock(true);
        for (int i = 1; i < 5; i++) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                synchronized (TestBatch.class) {
                                    if (ticket > 0) {
                                        try {
                                            Thread.sleep(50);
                                            threadLocal.set(ticket--);
                                            System.out.println("当前窗口:" + Thread.currentThread().getName() + " 正在卖第:" + threadLocal.get() + "张票");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }, "窗口-" + i
            ).start();
        }
    }

    private String getName(String name) {
        return "当前线程名:" + Thread.currentThread().getName() + " 输出的姓名:" + name;
    }

    private void getNames(String names) {
        for (int i = 0; i < names.length(); i++) {
            char c = names.charAt(i);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(c);
        }
        System.out.println();
    }

    @Test
    public void testName() throws ExecutionException, InterruptedException {
        for (int i = 0; i < 5; i++) {
            FutureTask<String> FutureTask = new FutureTask<>(() -> getName("盛重阳"));
            new Thread(
                    FutureTask, "shengchongyang-" + i
            ).start();
            System.out.println(FutureTask.get());
        }
    }

    @Test
    public void testNames() {
        for (int i = 0; i < 2; i++) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            getNames("盛重阳");
                            //System.out.println(Thread.currentThread().getName());
                        }
                    }
            ).start();
        }
    }

    private Integer data;
    ThreadLocal<Integer> threadLocal = new ThreadLocal();

    @Test
    public void testThreadDate() throws Exception {
        for (int i = 1; i <= 4; i++) {
            FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    Integer date = getDate(6);
                    System.out.println("当前线程名:{}" + Thread.currentThread().getName() + " get的值: " + date);
                    return date;
                }
            });
            new Thread(
                    task, "thread " + i
            ).start();
            //System.out.println("当前线程名:{}"+Thread.currentThread().getName()+" get的值: "+task.get());
        }
    }

    private Integer getDate(Integer num) {
        data = new Random().nextInt(num);
        threadLocal.set(data);
        System.out.println("当前线程名:{}" + Thread.currentThread().getName() + " set的值：" + threadLocal.get());
        return threadLocal.get();
    }

    @Test
    public void testNum() {
        System.out.println((int) Math.ceil(17 / (3 * 1.0)));
        System.out.println(17 / 3.0);
    }

    @Test
    public void test() throws UnknownHostException {
        Student instance1 = Student.getInstance("赵敏", 20);
        instance1.setName("赵敏2");
        instance1.setAge(202);
        Student instance2 = Student.getInstance("周芷若", 19);
        instance2.setName("周芷若2");
        instance2.setAge(191);
        System.out.println(instance1 == instance2);
        System.out.println(instance1);
        System.out.println(instance2);
        System.out.println(LocalDateTime.now());
        System.out.println(LocalDateTime.now().withNano(0));
        System.out.println(LocalDateTime.now().with(ChronoField.MICRO_OF_SECOND, 0));
        System.out.println(LocalDateTime.now().with(ChronoField.MILLI_OF_SECOND, 0));
        System.out.println(LocalDate.parse("20200512", DateTimeFormatter.ofPattern("yyyyMMdd")));
        System.out.println(LocalDate.parse("2020-05-15").withDayOfMonth(30));
        System.out.println(LocalDate.parse("2020-05-30").with(ChronoField.MONTH_OF_YEAR, 6).with(ChronoField.DAY_OF_MONTH, 15));
        System.out.println(LocalDate.parse("2020-05-30").toEpochDay() - LocalDate.parse("2020-05-15").toEpochDay());
        System.out.println(LocalDate.parse("2020-05-15").until(LocalDate.parse("2020-05-30"), ChronoUnit.DAYS));
        System.out.println(LocalDate.parse("2020-05-15").until(LocalDate.parse("2020-05-30")).get(ChronoUnit.DAYS));
        System.out.println(ChronoUnit.DAYS.between(LocalDate.parse("2020-05-15"), LocalDate.parse("2020-05-30")));
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        System.out.println("===========================================");
        System.out.println(LocalDateTime.parse("2020-05-15T15:30:27").until(LocalDateTime.parse("2020-05-30T10:30:27"), ChronoUnit.DAYS));
        System.out.println(Duration.between(LocalDateTime.parse("2020-05-15T15:30:27"), LocalDateTime.parse("2020-05-30T10:30:27")).toDays());

        List<String> list = new ArrayList<>(List.of("b", "c", "d"));
        list.add(0, "a");
        System.out.println(list);
        Object[] array = list.toArray();
        for (Object s : array) {
            System.out.println(s);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Plan {
        private String dueBillNo;
        private Integer term;
        private BigDecimal amount;
    }

    @Test
    public void test_() {
        List<Plan> list1 = new ArrayList<>(
                List.of(new Plan("scy", 1, new BigDecimal("100.0")),
                        new Plan("scy", 2, new BigDecimal("200.0")))
        );
        List<Plan> list2 = list1.stream().filter(e -> e.getTerm() == 1).collect(Collectors.toList());
        list2.get(0).setAmount(new BigDecimal("90.0"));

        System.out.println(list1);
        System.out.println("============================");
        list1.removeAll(list2);
        System.out.println(list1);
        System.out.println("===============================");
        list1.addAll(list2);
        System.out.println(list1);
    }

    @Test
    public void test__() {
        List<Plan> list1 = List.of(new Plan("scy", 1, new BigDecimal("100.0")), new Plan("scy", 2, new BigDecimal("200.0")));
        List<Plan> list2 = List.of(new Plan("scy", 1, new BigDecimal("200.0")));
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);
        list2.get(0).setAmount(new BigDecimal("90.0"));
        list1.removeAll(list2);
        System.out.println(list1);
        System.out.println("====================");
        list1.addAll(list2);
        System.out.println(list1);
    }

    @Test
    public void testPageList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 198; i++) {
            list.add("value_" + i);
        }
        int pageSize = 20;
        int pageNum = (int) Math.ceil(list.size() * 1.0 / pageSize);
        for (int i = 1; i <= pageNum; i++) {
            List<String> subList = list.subList((i - 1) * pageSize, Math.min(i * pageSize, list.size()));
            for (String s : subList) {
                System.out.println(s);
            }
        }
    }

    @Test
    public void testDistinct() {
        List<Tuple2<String, Integer>> tuple2s = List.of(
                Tuple2.of("YX-101", 1), Tuple2.of("YX-101", 1),
                Tuple2.of("YX-101", 2), Tuple2.of("YX-101", 2),
                Tuple2.of("YX-102", 1), Tuple2.of("YX-102", 1),
                Tuple2.of("YX-102", 2), Tuple2.of("YX-102", 2), Tuple2.of("YX-103", 1)
        );

        List<Tuple2<String, Integer>> tuple2List = tuple2s.stream().map(e -> Tuple2.of(e.getFirst(), e.getSecond())).distinct().collect(Collectors.toList());
        for (Tuple2<String, Integer> tuple2 : tuple2List) {
            System.out.println(JsonUtil.toJson(tuple2,true));
        }
    }
}
