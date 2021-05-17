package com.weshare.adapter.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weshare.service.api.entity.UserBaseReq;
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
    private UserBaseReq.IdCardType idCardType;
    @Field(name = "id_card_num")
    private String idCardNum;
    private String iphone;
    @Field(name = "car_num")
    private String carNum;
    private UserBaseReq.Sex sex;
    @Field(name = "project_no")
    private String projectNo;
    @Field(name = "due_bill_no")
    private String dueBillNo;
    @Field(name = "batch_date")
    private LocalDate batchDate;
    @Field(name = "come_list")
    // @Transient
    private List<UserBaseReq.Come> comeList;
    @Field(name = "link_man")
    private String LinkMan;
    @Field(name = "Back_card")
    private String BackCard;


}
