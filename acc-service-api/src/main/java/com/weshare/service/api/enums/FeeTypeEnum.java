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

    PRICINPAL("本金"),
    INTEREST("利息"),
    PENALTY("罚息"),
    REDUCE_PENALTY("减免罚息");

    private String desc;

    FeeTypeEnum(String desc) {
        this.desc = desc;
    }
}
