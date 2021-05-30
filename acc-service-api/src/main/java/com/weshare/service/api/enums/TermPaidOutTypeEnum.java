package com.weshare.service.api.enums;

import lombok.Getter;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.enums
 * @date: 2021-05-26 17:02:36
 * @describe:
 */
@Getter
public enum TermPaidOutTypeEnum {

    PRE_PAIDOUT("提前还清本期"),
    NORMAL_PAIDOUT("正常还清本期"),
    OVERDUE_PAIDOUT("逾期还清本期"),
    REFUND_PAIDOUT("退票还清本期");

    private String desc;

    TermPaidOutTypeEnum(String desc) {
        this.desc = desc;
    }
}
