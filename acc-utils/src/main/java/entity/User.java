package entity;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.StringJoiner;

@Data
@Accessors(chain = true)
@Document(collection = "user")
public class User implements Serializable {
    @Id
    private Long id;
    private String username;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date birthday;
    private int age;
    private String sex;
    private String address;
    private String password;
    private String mobile;
    private BigDecimal money;
    private Status status;

    public User() {
    }

    public User(Long id, String username, Date birthday, int age, String sex, String address, String password, String mobile, BigDecimal money) {
        this.id = id;
        this.username = username;
        this.birthday = birthday;
        this.age = age;
        this.sex = sex;
        this.address = address;
        this.password = password;
        this.mobile = mobile;
        this.money = money;

    }

    public User(String username, Date birthday, int age, String sex, String address, String password, String mobile, BigDecimal money, Status status) {
        this.username = username;
        this.birthday = birthday;
        this.age = age;
        this.sex = sex;
        this.address = address;
        this.password = password;
        this.mobile = mobile;
        this.money = money;
        this.status = status;
    }

    public User(Long id, String username, Date birthday, int age, String sex, String address, String password, String mobile, BigDecimal money, Status status) {
        this.id = id;
        this.username = username;
        this.birthday = birthday;
        this.age = age;
        this.sex = sex;
        this.address = address;
        this.password = password;
        this.mobile = mobile;
        this.money = money;
        this.status = status;
    }

    /**
     * @Author:scyang @Date:2019/10/2 22:44 后台转换,给前端显示的是字符串日期
     */
//    public String getBirthdayStr() {
//        if (birthday == null) {
//            return "";
//        }
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return sdf.format(birthday);
//    }



    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("username='" + username + "'")
                .add("birthday=" + birthday)
                .add("age=" + age)
                .add("sex='" + sex + "'")
                .add("address='" + address + "'")
                .add("password='" + password + "'")
                .add("mobile='" + mobile + "'")
                .add("money=" + money)
                .toString();
    }

    @Getter
    public enum Status {
        N,
        O,
        F;
    }
}
