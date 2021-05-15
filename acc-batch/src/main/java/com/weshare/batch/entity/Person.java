package com.weshare.batch.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch
 * @date: 2021-05-11 21:06:57
 * @describe:
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private String id;
    private String name;
    private String address;
    private Integer age;
    private LocalDate birthday;
    private BigDecimal salary;
    private Status status;
    private LocalDate batchDate;

    public enum Status {
        N,
        O,
        F,
        M;
    }
}
