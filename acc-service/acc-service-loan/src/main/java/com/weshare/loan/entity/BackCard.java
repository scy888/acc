package com.weshare.loan.entity;

import com.weshare.service.api.entity.UserBaseReq;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.entity
 * @date: 2021-05-18 14:39:09
 * @describe:
 */
@Data
@Accessors(chain = true)
@Entity
//@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {@Index(name = "back_card_back_num",unique = true,columnList = "backNum")})
@org.hibernate.annotations.Table(appliesTo = "back_card",comment = "银行卡号信息")
public class BackCard {

    @Id
    private String id;
    @Column(columnDefinition = "varchar(50) not null comment '用户id'")
    private String userId;
    @Column(columnDefinition = "varchar(50) not null comment '借据号'")
    private String dueBillNo;
    @Column(columnDefinition = "varchar(50) not null comment '用户名'")
    private String userName;
    @Column(columnDefinition = "varchar(50) not null comment '银行名'")
    @Enumerated(EnumType.STRING)
    private UserBaseReq.BackName backName;
    @Column(columnDefinition = "varchar(50) not null comment '银行代码'")
    private String backCode;
    @Column(columnDefinition = "varchar(50) not null comment '银行卡号'")
    private String backNum;
}
