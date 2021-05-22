package com.weshare.batch.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime createDate;

    public enum Status {
        N,
        O,
        F,
        M;
    }

    public Person(String id, String name, String address, Integer age, Status status, LocalDate batchDate) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.age = age;
        this.status = status;
        this.batchDate = batchDate;
    }
}
