package com.weshare.service.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserBaseReq {
    private String id;
    private String userId;
    private String userName;
    private IdCardType idCardType;
    private String idCardNum;
    private String iphone;
    private String carNum;
    private Sex sex;
    private String projectNo;
    private String dueBillNo;
    private LocalDate batchDate;
    private List<Come> comeList;
    private List<LinkManReq> linkManList;
    private List<BackCardReq> backCardList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Come {
        //@Field(name = "user_name")
        @JsonProperty(value = "name", index = 1)
        private String name;
        @JsonProperty(value = "address", index = 2)
        private String address;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkManReq implements Serializable{
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
    public static class BackCardReq implements Serializable{

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ManReq{
        private String userId;
        private String dueBillNo;
    }
}
