package com.weshare.service.api.enums;

import lombok.Getter;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.enums
 * @date: 2021-05-26 18:12:20
 * @describe:
 */
@Getter
public enum FeeTypeEnum {

    PRINCIPAL("本金"),
    INTEREST("利息"),
    PENALTY("罚息"),
    REDUCE_INTEREST("减免利息");

    private String desc;

    FeeTypeEnum(String desc) {
        this.desc = desc;
    }
}
