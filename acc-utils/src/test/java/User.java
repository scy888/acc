import common.ReflectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.junit.Test;

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

        List<String> list = userList.stream().map(e -> ReflectUtils.getFieldValues(e).replace(",","|")).collect(Collectors.toList());
        System.out.println(String.join(System.lineSeparator(), list));
    }
}
