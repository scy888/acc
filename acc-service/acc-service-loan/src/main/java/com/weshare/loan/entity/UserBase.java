package com.weshare.loan.entity;

import com.weshare.service.api.entity.UserBaseReq;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.entity
 * @date: 2021-05-18 14:08:31
 * @describe:
 */
@Data
@Accessors(chain = true)
@Entity
//@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {@Index(name = "user_base_due_bill_no_index", unique = true, columnList = "dueBillNo")})
@org.hibernate.annotations.Table(appliesTo = "user_base",comment = "用户基本信息表")
public class UserBase {
    @Id
    private String id;
    @Column(columnDefinition = "varchar(50) not null comment '用户id'")
    private String userId;
    @Column(columnDefinition = "varchar(50) not null comment '用户姓名'")
    private String userName;
    @Column(columnDefinition = "varchar(50) not null comment '证件类型'")
    @Enumerated(EnumType.STRING)
    private UserBaseReq.IdCardType idCardType;
    @Column(columnDefinition = "varchar(50) not null comment '证件号'")
    private String idCardNum;
    @Column(columnDefinition = "varchar(50) not null comment '手机号码'")
    private String iphone;
    @Column(columnDefinition = "varchar(50) not null comment '车牌号'")
    private String carNum;
    @Column(columnDefinition = "varchar(50) not null comment '性别'")
    @Enumerated(EnumType.STRING)
    private UserBaseReq.Sex sex;
    @Column(columnDefinition = "varchar(50) not null comment '项目编号'")
    private String projectNo;
    @Column(columnDefinition = "varchar(50) not null comment '借据号'")
    private String dueBillNo;
    @Column(columnDefinition = "date not null comment '批次日起'")
    private LocalDate batchDate;
    @Transient
    private List<UserBaseReq.LinkManReq> linkManList;
    @Transient
    private List<UserBaseReq.BackCardReq> backCardList;
}
