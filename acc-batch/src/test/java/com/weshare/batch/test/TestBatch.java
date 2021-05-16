package com.weshare.batch.test;

import com.weshare.batch.entity.Person;
import common.ReflectUtils;
import common.SnowFlake;
import jodd.io.ZipUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Objects;
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
        for (int i = 0; i < 10; i++) {
            System.out.println(SnowFlake.getInstance().nextId());
        }
    }

    @Test
    public void test02() throws Exception {

        List<Person> personList = List.of(new Person().setId(SnowFlake.getInstance().nextId() + "").setName("赵敏").setAddress("蒙古").setAge(20).setBirthday(LocalDate.parse("1992-06-18")).setSalary(new BigDecimal("1992.0618")).setStatus(Person.Status.F),
                new Person().setId(SnowFlake.getInstance().nextId() + "").setName("周芷若").setAddress("峨嵋").setAge(19).setBirthday(LocalDate.parse("1992-05-12")).setSalary(new BigDecimal("1992.0512")).setStatus(Person.Status.M),
                new Person().setId(SnowFlake.getInstance().nextId() + "").setName("小昭").setAddress("波斯").setAge(18).setBirthday(LocalDate.parse("1994-10-10")).setSalary(new BigDecimal("1994.1010")).setStatus(Person.Status.O),
                new Person().setId(SnowFlake.getInstance().nextId() + "").setName("阿离").setAddress("灵蛇岛").setAge(17).setBirthday(LocalDate.parse("1995-12-16")).setSalary(new BigDecimal("1995.1216")).setStatus(Person.Status.N));


        List<String> list = personList.stream().map(e -> ReflectUtils.getFieldValues(e, "batchDate")).collect(Collectors.toList());
        list.add(0, ReflectUtils.getFieldNames(Person.class, "batchDate"));
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
        BigDecimal max = bigDecimals.stream().reduce( BigDecimal::max).orElse(BigDecimal.ZERO);
        System.out.println(max);
        BigDecimal min = bigDecimals.stream().reduce( BigDecimal::min).orElse(BigDecimal.ZERO);
        System.out.println(min);
        BigDecimal avg = bigDecimals.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO).divide(BigDecimal.valueOf(bigDecimals.size()));
        System.out.println(avg);
        System.out.println("===============================================");
        int size = personList.size();
        Integer reduce = personList.stream().map(Person::getAge).reduce(0, Integer::sum)/size;

    }
    @Test
    public void test04(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode("scy199109091016");
        System.out.println(password);
        boolean matches = encoder.matches("scy199109091016", password);
        System.out.println(matches);
    }
}
