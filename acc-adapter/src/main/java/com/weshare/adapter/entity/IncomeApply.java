package com.weshare.adapter.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.entity
 * @date: 2021-05-16 20:00:05
 * @describe:
 */
@Data
@Document(collection = "in_come_apply")
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class IncomeApply {
    @Id
    private String id;
    @Field(name = "user_id")
    private String userId;
    @Field(name = "user_name")
    private String userName;
    @Field(name = "id_card_type")
    private IdCardType idCardType;
    @Field(name = "id_card_num")
    private String idCardNum;
    private String iphone;
    @Field(name = "car_num")
    private String carNum;
    private Sex sex;
    @Field(name = "project_no")
    private String projectNo;
    @Field(name = "due_bill_no")
    private String dueBillNo;
    @Field(name = "batch_date")
    private LocalDate batchDate;
    @Field(name = "come_list")
    // @Transient
    private List<Come> comeList;
    @Field(name = "link_man")
    private String LinkMan;
    @Field(name = "Back_card")
    private String BackCard;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Come {
        @Field(name = "user_name")
        @JsonProperty(value = "user_name", index = 1)
        private String userName;
        @JsonProperty(value = "address", index = 2)
        private String address;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkMan {
        @JsonProperty(value = "user_id", index = 1)
        private String userId;
        @JsonProperty(value = "due_bill_no", index = 2)
        private String dueBillNo;
        @JsonProperty(value = "user_name", index = 3)
        private String userName;
        @JsonProperty(value = "sex", index = 4)
        private String sex;
        @JsonProperty(value = "iphone", index = 5)
        private String iphone;
        @JsonProperty(value = "id_card_type", index = 6)
        private IdCardType idCardType;
        @JsonProperty(value = "id_card_num", index = 7)
        private String idCardNum;
        @JsonProperty(value = "work_type", index = 8)
        private String workType;
        @JsonProperty(value = "address", index = 9)
        private String address;
        @JsonProperty(value = "relational_type", index = 10)
        private RelationalType relationalType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BackCard {

        @JsonProperty(value = "user_id", index = 1)
        private String userId;
        @JsonProperty(value = "due_bill_no", index = 2)
        private String dueBillNo;
        @JsonProperty(value = "user_name", index = 3)
        private String userName;
        @JsonProperty(value = "back_name", index = 4)
        private BackName backName;
        @JsonProperty(value = "back_code", index = 5)
        private String backCode;
        @JsonProperty(value = "back_num", index = 6)
        private String backNum;
    }

    @Getter
    public enum IdCardType {

        S("身份证"),
        H("护照"),
        GA("港澳通行证"),
        O("其他");

        private String code;

        IdCardType(String code) {
            this.code = code;
        }
    }

    public enum Sex {
        M,
        W;
    }

    @Getter
    public enum RelationalType {

        PO("配偶"),
        ZN("子女"),
        PY("朋友"),
        TS("同事");

        private String desc;

        RelationalType(String desc) {
            this.desc = desc;
        }
    }

    @Getter
    public enum BackName {

        中国建行设银行("ZGJSYH", "6217 0028 7001 5622 705"),
        中国建农业银行("ZGNYYH", "6228 4800 5864 3078 676"),
        中国建招商银行("ZGZSYH", "6214 8312 7106 8212 236"),
        中国建工商银行("ZGGSYH", "6217 8576 0000 7092 823");

        private String code;
        private String num;

        BackName(String code, String num) {
            this.code = code;
            this.num = num;
        }
    }
}
