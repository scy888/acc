package com.weshare.loan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.entity
 * @date: 2021-05-19 10:03:12
 * @describe:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(indexes = {@Index(name = "critical_data_hash_due_bill_no", unique = true, columnList = "dueBillNo")})
@org.hibernate.annotations.Table(appliesTo = "critical_data_hash", comment = "哈希加密表")
public class CriticalDataHash {

    @Id
    private String userId;
    @Column(columnDefinition = "varchar(50) not null comment '用户姓名'")
    private String userName;
    @Column(columnDefinition = "varchar(50) not null comment '用户姓名哈希值'")
    private String userNameHash;
    @Column(columnDefinition = "varchar(50) not null comment '证件号'")
    private String idCardNum;
    @Column(columnDefinition = "varchar(50) not null comment '证件号哈希值'")
    private String idCardNumHash;
    @Column(columnDefinition = "varchar(50) not null comment '手机号'")
    private String iphone;
    @Column(columnDefinition = "varchar(50) not null comment '手机号哈希值'")
    private String iphoneHash;
    @Column(columnDefinition = "varchar(50) not null comment '车牌号'")
    private String carNum;
    @Column(columnDefinition = "varchar(50) not null comment '车牌号哈希值'")
    private String carNumHash;
    @Column(columnDefinition = "varchar(50) not null comment '项目编号'")
    private String projectNo;
    @Column(columnDefinition = "varchar(50) not null comment '借据号'")
    private String dueBillNo;
    @Column(columnDefinition = "date not null comment '创建日期'")
    private LocalDate createDate;
    @Column(columnDefinition = "datetime not null comment '修改时间'")
    private LocalDateTime lastModifyDate;
}
