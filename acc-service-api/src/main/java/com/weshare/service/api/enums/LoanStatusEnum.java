package com.weshare.service.api.enums;

import lombok.Getter;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.enums
 * @date: 2021-05-31 13:45:13
 * @describe:
 */
@Getter
public enum LoanStatusEnum {

    SUCCESS("放款成功"),
    REFUND("退票");

    private String desc;

    LoanStatusEnum(String desc) {
        this.desc = desc;
    }
}
