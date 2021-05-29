package com.weshare.service.api.vo;

import lombok.Data;
import lombok.Value;

import java.time.LocalDate;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.vo
 * @date: 2021-05-29 23:17:53
 * @describe:
 */
@Value
public class DueBillNoAndTermDueDate {

    private String dueBillNo;
    private Integer term;
    private LocalDate termDueDate;
}
