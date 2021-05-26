package com.weshare.service.api.enums;

import lombok.Getter;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.enums
 * @date: 2021-05-26 18:17:42
 * @describe:
 */

@Getter
public enum ReceiptTypeEnum {

    NORMAL("正常还款"),
    PRE("提前结清"),
    OVERDUE("逾期还款"),
    REDUCE("减免");

    private String desc;

    ReceiptTypeEnum(String desc) {
        this.desc = desc;
    }
}
