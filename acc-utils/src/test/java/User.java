import common.JsonUtil;
import common.ReflectUtil;
import lombok.Value;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: PACKAGE_NAME
 * @date: 2021-05-26 14:16:37
 * @describe:
 */

public class User {
    @Value
    public static class user {
        private String name;
        private Integer age;
        private AddressEnum address;
        private LocalDate batchDate;
        private LocalDateTime createDate;

        public enum AddressEnum {
            蒙古,
            峨嵋,
            波斯,
            明教;
        }
    }

    @Test
    public void test() {
        List<user> userList = List.of(
                new user("赵敏", 20, user.AddressEnum.蒙古, LocalDate.parse("2021-05-12"), LocalDateTime.now()),
                new user("周芷若", 19, user.AddressEnum.峨嵋, LocalDate.parse("2021-05-12"), LocalDateTime.now()),
                new user("小昭", 18, user.AddressEnum.波斯, LocalDate.parse("2021-05-12"), LocalDateTime.now()),
                new user("阿离", 17, user.AddressEnum.明教, LocalDate.parse("2021-05-12"), LocalDateTime.now()));

        List<String> list = userList.stream().map(e -> ReflectUtil.getFieldValues(e).replace(",", "|")).collect(Collectors.toList());
        System.out.println(String.join(System.lineSeparator(), list));
    }

    @Test
    public void revertToStandardJsonString5() throws Exception {
        String nonStandardJsonString = Files.readString(Paths.get(this.getClass().getClassLoader().getResource("batch_redemption.json").toURI()), Charset.forName("GBK"));
        System.out.println("转换前的JSON字符串：\n" + nonStandardJsonString);
        String standardJsonString = JsonUtil.revertToStandardJsonString(nonStandardJsonString, true);
        System.out.println("\n转换后的JSON字符串：\n" + standardJsonString);
    }
}