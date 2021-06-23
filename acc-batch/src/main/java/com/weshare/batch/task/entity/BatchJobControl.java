package com.weshare.batch.task.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.entity
 * @date: 2021-06-17 09:46:14
 * @describe:
 */
@Entity
@Data
@Accessors(chain = true)
public class BatchJobControl {

    @Id
    private String jobName;
    private Boolean isRunning;
    private LocalDateTime createDate;
    private LocalDateTime lastModifyDate;
}
