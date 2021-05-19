package com.weshare.loan.entity;

import com.weshare.service.api.entity.UserBaseReq;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.entity
 * @date: 2021-05-18 14:27:04
 * @describe:
 */
@Data
@Accessors(chain = true)
@Entity
//@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {@Index(name = "link_man_id_card_num",unique = true,columnList = "idCardNum")})
@org.hibernate.annotations.Table(appliesTo = "link_man", comment = "联系人信息表")
public class LinkMan {

    @Id
    private String id;
    @Column(columnDefinition = "varchar(50) not null comment '用户id'")
    private String userId;
    @Column(columnDefinition = "varchar(50) not null comment '借据号'")
    private String dueBillNo;
    @Column(columnDefinition = "varchar(50) not null comment '用户名'")
    private String userName;
    @Column(columnDefinition = "varchar(50) not null comment '性别'")
    private String sex;
    @Column(columnDefinition = "varchar(50) not null comment '手机号码'")
    private String iphone;
    @Column(columnDefinition = "varchar(50) not null comment '身份证类型'")
    @Enumerated(EnumType.STRING)
    private UserBaseReq.IdCardType idCardType;
    @Column(columnDefinition = "varchar(50) not null comment '省份证号码'")
    private String idCardNum;
    @Column(columnDefinition = "varchar(50) not null comment '工作类型'")
    private String workType;
    @Column(columnDefinition = "varchar(50) not null comment '地址'")
    private String address;
    @Column(columnDefinition = "varchar(50) not null comment '关系类型'")
    @Enumerated(EnumType.STRING)
    private UserBaseReq.RelationalType relationalType;
}
