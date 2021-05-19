package com.weshare.service.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.entity
 * @date: 2021-05-19 19:58:44
 * @describe:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private Integer age;
}
