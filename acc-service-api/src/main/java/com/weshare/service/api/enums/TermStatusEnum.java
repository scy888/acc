package com.weshare.service.api.enums;

import lombok.Getter;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.enums
 * @date: 2021-05-25 16:17:19
 * @describe:
 */
@Getter
public enum TermStatusEnum {
    UNDUE("本期未还"),
    REPAID("本期已还"),
    OVERDUE("逾期");

    private String desc;

    TermStatusEnum(String desc) {
        this.desc = desc;
    }
}
