package com.weshare.batch.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.entity
 * @date: 2021-06-16 14:15:12
 * @describe:
 */
@Data
@Accessors(chain = true)
public class StartEvent {

    private String jobName;
    private String batchDate;
    private String endDate;
    private String projectNo;
    private String remark;
    private String status;
}
