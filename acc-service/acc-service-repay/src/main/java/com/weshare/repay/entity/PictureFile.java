package com.weshare.repay.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author v_tianwenkai
 * @since 2020/12/29 17:56
 */
@Data
@Accessors(chain = true)
@Entity
@javax.persistence.Table(indexes = {
        @Index(name = "idx_picture_file_due_bill_no", columnList = "dueBillNo")
})
@Table(appliesTo = "picture_file", comment = "体外还款还款凭证")
public class PictureFile {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(50) not null comment '借据单号' ")
    private String dueBillNo;

    @Column(columnDefinition = "longblob null comment '体外还款凭证' ")//LongBlob
    private byte[] byteArray;

    @Column(columnDefinition = "varchar(120) null comment '文件名称' ")
    private String fileName;

    @Column(columnDefinition = "datetime null comment '批次日期' ")
    private LocalDateTime createTime;
}
